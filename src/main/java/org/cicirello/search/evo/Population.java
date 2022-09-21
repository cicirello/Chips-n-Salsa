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

import org.cicirello.search.concurrent.Splittable;
import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * The Population interface represents a population of candidate solutions
 * to a problem for use by implementations of genetic algorithms and other
 * evolutionary algorithms.  
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
interface Population<T extends Copyable<T>> extends Splittable<Population<T>>, PopulationFitnessVector {
	
	/**
	 * Gets a candidate solution subject to genetic operators
	 * during the current generation.
	 *
	 * @param i An index into the population (indexes begin at 0).
	 * @return The member of the population at index i.
	 * @throws ArrayIndexOutOfBoundsException if i is outside the interval [0, mutableSize()).
	 */
	T get(int i);
	
	/**
	 * Gets the number of candidate solutions subject to genetic operators.
	 *
	 * @return number of mutable candidate solutions.
	 */
	int mutableSize();
	
	/**
	 * Gets the most fit candidate solution encountered in any generation.
	 * @return the most fit encountered in any generation
	 */
	SolutionCostPair<T> getMostFit();
	
	/**
	 * Update the fitness of a candidate solution for the next generation.
	 * @param i The population member.
	 */
	void updateFitness(int i);
	
	/**
	 * Reinitialize the population randomly.
	 */
	void init();
	
	/**
	 * Initialize or reinitialize any operators such as
	 * selection that may require doing so.
	 * @param generations Number of generations
	 */
	void initOperators(int generations);
	
	/**
	 * Performs selection to choose the population members to undergo genetic operators.
	 */
	void select();
	
	/**
	 * Updates population based on children of genetic operators.
	 */
	void replace();
	
	/**
	 * Determines whether there is any reason the search should stop, such as
	 * if the ProgressTracker contains the best, or if another thread has stopped
	 * the ProgressTracker.
	 *
	 * @return true if the search should continue to run, and false otherwise.
	 */
	boolean evolutionIsPaused();
	
	/**
	 * Gets the ProgressTracker maintained by this population.
	 * @return the ProgressTracker
	 */
	ProgressTracker<T> getProgressTracker();
	
	/**
	 * Sets the ProgressTracker maintained by this population.
	 * @param tracker The new ProgressTracker
	 */
	void setProgressTracker(ProgressTracker<T> tracker);
	
	@Override
	abstract public Population<T> split();
}