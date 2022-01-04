/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.math.rand.RandomVariates;

/**
 * <p>This class implements an evolutionary algorithm (EA) with a generational
 * model, where a population of children are formed by applying genetic operators to
 * members of the parent population, and where the children replace the 
 * parents in the next generation. In this EA, utilizes both a crossover operator
 * and a mutation operator, whose application is controlled by crossover and mutation
 * rates, but such that the operators are mutually exclusive. That is, each child is the
 * result of crossover, or mutation, or a simply copy of a parent, but never the result
 * of both crossover and mutation.</p>
 *
 * <p>The crossover, mutation, and selection operators are completely configurable
 * by passing instances of classes that implement the {@link CrossoverOperator},
 * {@link MutationOperator}, and {@link SelectionOperator} classes to one of the
 * constructors. The EA implemented by this class can also be configured to use
 * elitism, if desired, such that a specified number of the best solutions in the
 * population survive the generation unaltered.</p>
 *
 * <p>The library also includes a class for the more common generational model, such
 * that population members may undergo both crossover and mutation in the same generation
 * (see the {@link GenerationalEvolutionaryAlgorithm} class) as well as a class for 
 * mutation-only generational EAs
 * (see {@link GenerationalMutationOnlyEvolutionaryAlgorithm}).</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class GenerationalNANDOperatorsEvolutionaryAlgorithm<T extends Copyable<T>> extends AbstractEvolutionaryAlgorithm<T> {
	
	private final MutationOperator<T> mutation;
	private final double M_PRIME;
	private final CrossoverOperator<T> crossover;
	private final double C;
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, int eliteCount, ProgressTracker<T> tracker) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, tracker, eliteCount), f.getProblem(), mutation, mutationRate, crossover, crossoverRate);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, int eliteCount, ProgressTracker<T> tracker) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, tracker, eliteCount), f.getProblem(), mutation, mutationRate, crossover, crossoverRate);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, 0, tracker);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, 0, tracker);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, int eliteCount) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, eliteCount, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, int eliteCount) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, eliteCount, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for an EA utilizing both a crossover operator
	 * and a mutation operator, such that the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation.
	 * This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws IllegalArgumentException if mutationRate + crossoverRate &gt; 1.0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalNANDOperatorsEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection) {
		this(n, mutation, mutationRate, crossover, crossoverRate, initializer, f, selection, new ProgressTracker<T>());
	}
	
	// Internal Constructors
	
	/*
	 * Internal helper constructor for standard EAs with full generation (both crossover and mutation).
	 */
	private GenerationalNANDOperatorsEvolutionaryAlgorithm(Population<T> pop, Problem<T> problem, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate) {
		super(pop, problem);
		if (mutation == null) {
			throw new NullPointerException("mutation must be non-null");
		}
		if (crossover == null) {
			throw new NullPointerException("crossover must be non-null");
		}
		if (mutationRate < 0.0) {
			throw new IllegalArgumentException("mutationRate must not be negative");
		}
		if (crossoverRate < 0.0) {
			throw new IllegalArgumentException("crossoverRate must not be negative");
		}
		if (mutationRate + crossoverRate > 1.0) {
			throw new IllegalArgumentException("mutually exclusive operators requires mutationRate + crossoverRate <= 1.0");
		}
		C = crossoverRate;
		M_PRIME = C < 1.0 ? mutationRate / (1.0 - C) : 0.0;
		this.mutation = mutation;
		this.crossover = crossover;
	}
	
	/*
	 * Internal constructor for use by split method.
	 * package private so subclasses in same package can use it for initialization for their own split methods.
	 */
	GenerationalNANDOperatorsEvolutionaryAlgorithm(GenerationalNANDOperatorsEvolutionaryAlgorithm<T> other) {
		super(other);
		
		// Must be split
		mutation = other.mutation.split();
		crossover = other.crossover.split(); 
		
		// Threadsafe so just copy reference or values
		M_PRIME = other.M_PRIME;
		C = other.C;
	}
	
	@Override
	public GenerationalNANDOperatorsEvolutionaryAlgorithm<T> split() {
		return new GenerationalNANDOperatorsEvolutionaryAlgorithm<T>(this);
	}
		
	@Override
	final int oneGeneration(Population<T> pop) {
		pop.select();
		// Since select() above randomizes ordering, just use a binomial
		// to get count of number of pairs of parents to cross and cross the first 
		// count pairs of parents. Pair up parents with indexes: first and (first + count).
		final int count = C < 1.0 ? RandomVariates.nextBinomial(pop.mutableSize()/2, C) : pop.mutableSize()/2;
		for (int first = 0; first < count; first++) {
			int second = first + count;
			crossover.cross(pop.get(first), pop.get(second));
			pop.updateFitness(first);
			pop.updateFitness(second);
		}
		final int crossed = count << 1;
		int mutateCount = 0;
		if (C < 1.0) {
			mutateCount = crossed < pop.mutableSize() ? 
				(M_PRIME < 1.0 ? RandomVariates.nextBinomial(pop.mutableSize()-crossed, M_PRIME) : pop.mutableSize()-crossed) 
				: 0;
			for (int j = crossed + mutateCount - 1; j >= crossed; j--) {
				mutation.mutate(pop.get(j));
				pop.updateFitness(j);
			}
		}
		pop.replace();
		return crossed + mutateCount;
	}
}
