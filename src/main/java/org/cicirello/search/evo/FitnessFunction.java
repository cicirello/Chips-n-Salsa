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

import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * Fitness function interfaces. Implement one of the two nested interfaces
 * to provide a fitness function for use by genetic algorithms and other forms of
 * evolutionary computation. Some of the {@link SelectionOperator} classes of this 
 * library assume that
 * fitness values are positive, so for maximal compatibility, you should design your
 * fitness function such that fitness values are always positive. The difference 
 * between the two nested interfaces is only the type of the fitness values (double vs int). 
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface FitnessFunction<T extends Copyable<T>> {
	
	/**
	 * Gets a reference to the problem that this fitness function is for.
	 * @return a reference to the problem.
	 */
	Problem<T> getProblem();
	
	/**
	 * Fitness function interface for double-valued fitnesses. Implement 
	 * this interface to provide a
	 * fitness function for use by genetic algorithms and other forms of
	 * evolutionary computation. Some of the {@link SelectionOperator} classes of this 
	 * library assume that
	 * fitness values are positive, so for maximal compatibility, you should design your
	 * fitness function such that fitness values are always positive. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	interface Double<T extends Copyable<T>> extends FitnessFunction<T> {
		
		/**
		 * Computes the fitness of a candidate solution to a problem,
		 * for use by genetic algorithms and other evolutionary algorithms.
		 *
		 * @param candidate The solution whose fitness is to be evaluated.
		 * @return the fitness of candidate
		 */
		double fitness(T candidate);
	}
	
	/**
	 * Fitness function interface for int-valued fitnesses. Implement 
	 * this interface to provide a
	 * fitness function for use by genetic algorithms and other forms of
	 * evolutionary computation. Some of the {@link SelectionOperator} classes of this 
	 * library assume that
	 * fitness values are positive, so for maximal compatibility, you should design your
	 * fitness function such that fitness values are always positive. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	interface Integer<T extends Copyable<T>> extends FitnessFunction<T> {
		
		/**
		 * Computes the fitness of a candidate solution to a problem,
		 * for use by genetic algorithms and other evolutionary algorithms.
		 *
		 * @param candidate The solution whose fitness is to be evaluated.
		 * @return the fitness of candidate
		 */
		int fitness(T candidate);
	}
}
