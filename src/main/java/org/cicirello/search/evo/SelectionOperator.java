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

import org.cicirello.search.concurrent.Splittable;

/**
 * Implement this interface to provide a selection operator
 * for use by genetic algorithms and other forms of
 * evolutionary computation.  
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface SelectionOperator extends Splittable<SelectionOperator> {
	
	/**
	 * Selects a set of members of the population based on fitness. Implementations should
	 * ensure that the array of indexes of population members is in a random order. For some
	 * selection operators, this required behavior is met by definition (e.g., the common
	 * fitness proportionate selection will have this behavior as is). But other selection
	 * operators may require randomizing the array of indexes after selection. For example,
	 * the obvious implementation of stochastic universal sampling will likely have all copies
	 * of an individual population member ordered together, and thus will require a 
	 * shuffling of the array before returning.
	 *
	 * @param fitnesses A vector of fitnesses of the members of the population.
	 * @param selected An array for the result. The selection operator should select 
	 * selected.length members of the population based on fitnesses, populating 
	 * selected with the indexes of the chosen members. Note that selected.length may
	 * be different than the fitnesses.size().
	 */
	void select(PopulationFitnessVector.Integer fitnesses, int[] selected);
	
	/**
	 * Selects a set of members of the population based on fitness. Implementations should
	 * ensure that the array of indexes of population members is in a random order. For some
	 * selection operators, this required behavior is met by definition (e.g., the common
	 * fitness proportionate selection will have this behavior as is). But other selection
	 * operators may require randomizing the array of indexes after selection. For example,
	 * the obvious implementation of stochastic universal sampling will likely have all copies
	 * of an individual population member ordered together, and thus will require a 
	 * shuffling of the array before returning.
	 *
	 * @param fitnesses A vector of fitnesses of the members of the population.
	 * @param selected An array for the result. The selection operator should select 
	 * selected.length members of the population based on fitnesses, populating 
	 * selected with the indexes of the chosen members. Note that selected.length may
	 * be different than the fitnesses.size().
	 */
	void select(PopulationFitnessVector.Double fitnesses, int[] selected);
	
	/**
	 * Perform any initialization necessary for the selection operator at the start
	 * of the run of the evolutionary algorithm. This method is called by the evolutionary
	 * algorithm at the start of a run (i.e., whenever an EA's optimize or reoptimize methods
	 * are called. The default implementation of this method does nothing, which is appropriate
	 * for most selection operators since the behavior of most standard selection operators is
	 * doesn't change during runs. However, some selection operators may adjust behavior during
	 * the run, such as Boltzmann selection which adjusts a temperature parameter. The init method
	 * enables reinitializing such parameters at the start of runs.
	 * @param generations The number of generations for the run of the evolutionary algorithm about to commence.
	 */
	default void init(int generations) {}
}
