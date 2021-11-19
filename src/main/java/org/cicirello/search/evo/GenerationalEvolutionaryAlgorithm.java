/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
 *
 * This file is part of Chips-n-Salsa (https://chips-n-salsa.cicirello.org/).
 * 
 * Chips-n-Salsa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Chips-n-Salsa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.cicirello.search.evo;

import org.cicirello.util.Copyable;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.math.rand.RandomIndexer;

/**
 * This class implements an evolutionary algorithm with a generational
 * model, such as is commonly used in genetic algorithms, where a
 * population of children are formed by applying genetic operators to
 * members of the parent population, and where the children replace the 
 * parents in the next generation.
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class GenerationalEvolutionaryAlgorithm<T extends Copyable<T>> implements ReoptimizableMetaheuristic<T> {
	
	private final Population<T> pop;
	private final Problem<T> problem;
	private final MutationOperator<T> mutation;
	private final double M;
	
	private final SingleGen<T> sr;
	private final GenerationOption go;
	
	private int numFitnessEvals;
	
	/**
	 * Constructs and initializes the evolutionary algorithm with mutation only.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the mutation rate, and then pass 1.0 for this parameter.
	 * @param initializer An initializer for generat6ing random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		if (n < 1) {
			throw new IllegalArgumentException("n must be positive");
		}
		pop = new BasePopulation.Double<T>(n, initializer, f, selection, tracker);
		problem = f.getProblem();
		this.mutation = mutation;
		M = mutationRate;
		
		if (M < 1.0) {
			sr = mutationOnly(); 
			go = GenerationOption.MUTATION_ONLY;
		} else {
			sr = alwaysMutate();
			go = GenerationOption.ALWAYS_MUTATION;
		}
	}
	
	private GenerationalEvolutionaryAlgorithm(GenerationalEvolutionaryAlgorithm<T> other) {
		// Must be split
		pop = other.pop.split();
		mutation = other.mutation.split();
		
		// Threadsafe so just copy reference or values
		problem = other.problem;
		M = other.M;
		numFitnessEvals = 0;
		
		// Initialize the runner
		go = other.go;
		switch (go) {
			case MUTATION_ONLY: sr = mutationOnly(); break;
			case ALWAYS_MUTATION: sr = alwaysMutate(); break;
			default: sr = null; break;
		}
	}
	
	/**
	 * Runs the evolutionary algorithm beginning from a randomly generated population. If this 
	 * method is called multiple times, each call begins at a new randomly generated population.
	 *
	 * @param numGenerations The number of generations to run.
	 *
	 * @return The best solution found during this set of generations, which may or may not be the
	 * same as the solution contained in the {@link ProgressTracker}, which contains the best across all
	 * calls to optimize as well as {@link #reoptimize}. Returns null if the run did not execute, such 
	 * as if the ProgressTracker already contains the theoretical best solution.
	 */
	@Override
	public final SolutionCostPair<T> optimize(int numGenerations) {
		if (pop.evolutionIsPaused()) return null;
		pop.init();
		numFitnessEvals = numFitnessEvals + pop.size();
		internalOptimize(numGenerations);
		return pop.getMostFit();
	}
	
	/**
	 * Runs the evolutionary algorithm continuing from the final population from the most recent call
	 * to either {@link #optimize} or {@link #reoptimize}, or from a random population if this is the first
	 * call to either method. 
	 *
	 * @param numGenerations The number of generations to run.
	 *
	 * @return The best solution found during this set of generations, which may or may not be the
	 * same as the solution contained in the {@link ProgressTracker}, which contains the best across all
	 * calls to optimize as well as {@link #optimize}. Returns null if the run did not execute, such 
	 * as if the ProgressTracker already contains the theoretical best solution.
	 */
	@Override
	public final SolutionCostPair<T> reoptimize(int numGenerations) {
		if (pop.evolutionIsPaused()) return null;
		internalOptimize(numGenerations);
		return pop.getMostFit();
	}
	
	@Override
	public GenerationalEvolutionaryAlgorithm<T> split() {
		return new GenerationalEvolutionaryAlgorithm<T>(this);
	}
	
	@Override
	public final Problem<T> getProblem() {
		return problem;
	}
	
	@Override
	public final ProgressTracker<T> getProgressTracker() {
		return pop.getProgressTracker();
	}
	
	@Override
	public final void setProgressTracker(ProgressTracker<T> tracker) {
		pop.setProgressTracker(tracker);
	}
	
	/**
	 * Gets the total run length in number of fitness evaluations. This is the total run length across all 
	 * calls to {@link #optimize} and {@link #reoptimize}. This may differ from what may be expected 
	 * based on run lengths. For example, the search terminates if it finds the theoretical best 
	 * solution, and also immediately returns if a prior call found the theoretical best. In such 
	 * cases, the total run length may be less than the requested run length.
	 *
	 * @return The total number of generations completed across all calls to {@link #optimize} and {@link #reoptimize}.
	 */
	@Override
	public long getTotalRunLength() {
		return numFitnessEvals;
	}
		
	private interface SingleGen<T extends Copyable<T>> {
		void optimizeSingleGen();
	}
	
	private enum GenerationOption {
		MUTATION_ONLY, ALWAYS_MUTATION
	}
	
	private void internalOptimize(int numGenerations) {
		for (int i = 0; i < numGenerations && !pop.evolutionIsPaused(); i++) {
			sr.optimizeSingleGen();
		}
	}
	
	private SingleGen<T> mutationOnly() {
		return () -> {
			pop.select();
			int[] mutateThese = RandomIndexer.sample(pop.mutableSize(), M);
			for (int j = 0; j < mutateThese.length; j++) {
				mutation.mutate(pop.get(mutateThese[j]));
				pop.updateFitness(mutateThese[j]);
			}
			pop.replace();
			numFitnessEvals = numFitnessEvals + mutateThese.length;
		};
	}
	
	private SingleGen<T> alwaysMutate() {
		return () -> {
			final int LAMBDA = pop.mutableSize();
			pop.select();
			for (int j = 0; j < LAMBDA; j++) {
				mutation.mutate(pop.get(j));
				pop.updateFitness(j);
			}
			pop.replace();
			numFitnessEvals = numFitnessEvals + LAMBDA;
		};
	}
	
}