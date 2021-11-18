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

/**
 * The PopulationMember class represents a single member of a population
 * for use by implementations of genetic algorithms and other
 * evolutionary algorithms. It includes the candidate solution to the
 * problem that this member of the population represents as well as its
 * fitness. 
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class PopulationMember<T extends Copyable<T>> {
	
	final T candidate;
	
	/*
	 * for use only by the nested classes
	 */
	private PopulationMember(T candidate) {
		this.candidate = candidate;
	}
	
	/**
	 * Gets a reference to the candidate solution contained in this PopulationMember.
	 *
	 * @return the candidate solution
	 */
	public final T getCandidate() {
		return candidate;
	}
	
	/**
	 * The PopulationMember class represents a single member of a population
	 * for use by implementations of genetic algorithms and other
	 * evolutionary algorithms, specifically where fitness values are
	 * of type double. It includes the candidate solution to the
	 * problem that this member of the population represents as well as its
	 * fitness. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	static final class DoubleFitness<T extends Copyable<T>> extends PopulationMember<T> implements Copyable<DoubleFitness<T>> {
		
		private double fitness;
		
		/**
		 * Construct a member of the population.
		 *
		 * @param candidate The candidate solution for the member of the population.
		 * @param fitness The fitness of the candidate solution.
		 */
		public DoubleFitness(T candidate, double fitness) {
			super(candidate);
			this.fitness = fitness;
		}
		
		@Override
		public DoubleFitness<T> copy() {
			return new DoubleFitness<T>(candidate, fitness);
		}
		
		/**
		 * Gets the fitness of this population member as currently stored.
		 * @return the fitness of the population member
		 */
		public final double getFitness() {
			return fitness;
		}
		
		/**
		 * Changes the fitness of the population member, such as if necessary
		 * after a mutation or crossover.
		 * @param fitness The fitness of the candidate solution.
		 */
		public final void setFitness(double fitness) {
			this.fitness = fitness;
		}
	}
	
	/**
	 * The PopulationMember class represents a single member of a population
	 * for use by implementations of genetic algorithms and other
	 * evolutionary algorithms, specifically where fitness values are
	 * of type int. It includes the candidate solution to the
	 * problem that this member of the population represents as well as its
	 * fitness. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	static final class IntegerFitness<T extends Copyable<T>> extends PopulationMember<T> implements Copyable<IntegerFitness<T>> {
		
		private int fitness;
		
		/**
		 * Construct a member of the population.
		 *
		 * @param candidate The candidate solution for the member of the population.
		 * @param fitness The fitness of the candidate solution.
		 */
		public IntegerFitness(T candidate, int fitness) {
			super(candidate);
			this.fitness = fitness;
		}
		
		@Override
		public IntegerFitness<T> copy() {
			return new IntegerFitness<T>(candidate, fitness);
		}
		
		/**
		 * Gets the fitness of this population member as currently stored.
		 * @return the fitness of the population member
		 */
		public final int getFitness() {
			return fitness;
		}
		
		/**
		 * Changes the fitness of the population member, such as if necessary
		 * after a mutation or crossover.
		 * @param fitness The fitness of the candidate solution.
		 */
		public final void setFitness(int fitness) {
			this.fitness = fitness;
		}
	}
}
