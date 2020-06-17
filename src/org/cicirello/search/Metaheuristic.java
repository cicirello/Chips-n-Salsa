/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
 
package org.cicirello.search;

import org.cicirello.util.Copyable;

/**
 * This interface defines the required methods for implementations
 * of metaheuristics, in particular metaheuristics for which the maximum run length
 * can be specified.
 *
 * @param <T> The type of object under optimization.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.9.2020
 */
public interface Metaheuristic<T extends Copyable<T>> extends TrackableSearch<T> {
	
	/**
	 * <p>Executes a run of the metaheuristic beginning at a randomly generated solution.
	 * If this method is called multiple times, each call begins at a new randomly generated
	 * starting solution, and reinitializes any control parameters of the metaheuristic 
	 * that may have changed during the previous call to optimize to start of run state.</p>
	 *
	 * <p>Implementing classes should provide more specific documentation of this method
	 * to define in particular what run length means in the context of the given metaheuristic.
	 * It is also recommended that implementing classes rename the parameter to be a more
	 * meaningful name within the context of that metaheuristic.</p>
	 * @param runLength The length of a run of this metaheuristic.
	 * @return The current solution at the end of this run and its cost, which may or may not be the best
	 * of run solution, and which may or may not be the same as the solution contained
	 * in this metaheuristic's {@link ProgressTracker}, which contains the best of all runs.
	 * Returns null if the run did not execute, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	SolutionCostPair<T> optimize(int runLength);
	
	@Override
	Metaheuristic<T> split();
}