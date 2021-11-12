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
import org.cicirello.search.concurrent.Splittable;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import java.util.ArrayList;

/**
 * The Population interface represents a population of candidate solutions
 * to a problem for use by implementations of genetic algorithms and other
 * evolutionary algorithms. It assumes the common generational model, with a 
 * constant population size, and with offspring (from one or both of crossover 
 * and mutation) replacing the parents in the next generation. 
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
interface Population<T extends Copyable<T>> extends Splittable<Population<T>>, PopulationFitnessVector {
	
	/**
	 * Gets a candidate solution from the population subject to genetic operators
	 * during the current generation.
	 *
	 * @param i An index into the population (indexes begin at 0).
	 * @return The member of the population at index i.
	 * @throws ArrayIndexOutOfBoundsException if i is outside the interval [0, nonEliteSize()).
	 */
	T get(int i);
	
	/**
	 * Gets the size of the population that is subject to genetic operators.
	 *
	 * @return The size of the population.
	 */
	int sizeNonElite();
	
	/**
	 * Gets the most fit candidate solution encountered in any generation.
	 * @return the most fit encountered in any generation
	 */
	SolutionCostPair<T> getMostFit();
	
	/**
	 * Update the fitness of a population member.
	 * @param i The population member.
	 */
	void updateFitness(int i);
	
	/**
	 * Reinitialize the population randomly.
	 */
	void init();
	
	/**
	 * Performs selection to choose the population for the next generation.
	 */
	void select();
	
	/**
	 * The Population for an evolutionary algorithm where fitness values are type double. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	static final class Double<T extends Copyable<T>> implements Population<T>, PopulationFitnessVector.Double {
		
		private final Initializer<T> initializer;
		private final SelectionOperator selection;
	
		private ArrayList<PopulationMember.DoubleFitness<T>> pop;
		private ArrayList<PopulationMember.DoubleFitness<T>> nextPop;
		
		private final FitnessFunction.Double<T> f;		
		private final int n;
		
		private final int[] selected;
		
		private SolutionCostPair<T> mostFit;
		private double bestFitness;
		
		/**
		 * Constructs the Population.
		 *
		 * @param n The size of the population, which must be positive.
		 * @param initializer An initializer to supply the population with a means of generating
		 * random initial population members.
		 * @param f The fitness function.
		 * @param selection The selection operator.
		 */
		public Double(int n, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
			this.initializer = initializer;
			this.selection = selection;
			this.f = f;
			this.n = n;
			pop = new ArrayList<PopulationMember.DoubleFitness<T>>(n);
			nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(n);
			selected = new int[n];
			bestFitness = java.lang.Double.NEGATIVE_INFINITY;
		}
		
		/*
		 * private constructor for use by split.
		 */
		private Double(Population.Double<T> other) {
			// these are threadsafe, so just copy references
			f = other.f;
			selection = other.selection;
			n = other.n;
			
			// split these: not threadsafe
			initializer = other.initializer.split();
		
			// initialize these fresh: not threadsafe or otherwise needs its own
			pop = new ArrayList<PopulationMember.DoubleFitness<T>>(n);
			nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(n);
			selected = new int[n];
			mostFit = null;
			bestFitness = java.lang.Double.NEGATIVE_INFINITY;
		}
		
		@Override
		public Population.Double<T> split() {
			return new Population.Double<T>(this);
		}
		
		@Override
		public T get(int i) {
			return pop.get(i).getCandidate();
		}
		
		@Override
		public double getFitness(int i) {
			return pop.get(i).getFitness();
		}
		
		@Override
		public int size() {
			return n;
		}
		
		@Override
		public int sizeNonElite() {
			return n;
		}
		
		@Override
		public SolutionCostPair<T> getMostFit() {
			return mostFit;
		}
		
		/**
		 * Gets fitness of the most fit candidate solution encountered in any generation.
		 * @return the fitness of the most fit encountered in any generation
		 */
		public double getFitnessOfMostFit() {
			return bestFitness;
		}
		
		@Override
		public void updateFitness(int i) {
			double fit = f.fitness(pop.get(i).getCandidate());
			pop.get(i).setFitness(fit);
			if (fit > bestFitness) {
				bestFitness = fit;
				mostFit = f.getProblem().getSolutionCostPair(pop.get(i).getCandidate().copy()); 
			}
		}
		
		@Override
		public void select() {
			selection.select(this, selected);
			for (int i = 0; i < n; i++) {
				nextPop.add(pop.get(selected[i]).copy());
			}
			ArrayList<PopulationMember.DoubleFitness<T>> temp = pop;
			pop = nextPop;
			nextPop = temp;
			nextPop.clear();
		}
		
		@Override
		public void init() {
			pop.clear();
			T newBest = null;
			for (int i = 0; i < n; i++ ) {
				T c = initializer.createCandidateSolution();
				double fit = f.fitness(c);
				pop.add(new PopulationMember.DoubleFitness<T>(c, fit));
				if (mostFit==null || fit > bestFitness) {
					bestFitness = fit;
					newBest = c;
				}
			}
			if (newBest != null) {
				mostFit = f.getProblem().getSolutionCostPair(newBest.copy());
			}
		}
	}
	
}
