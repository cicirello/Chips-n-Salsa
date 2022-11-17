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

import org.cicirello.search.concurrent.Splittable;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This interface defines the required functionality of search algorithm implementations that
 * support tracking search progress across multiple runs, whether multiple sequential runs, or
 * multiple concurrent runs in the case of a parallel metaheuristic.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public interface TrackableSearch<T extends Copyable<T>> extends Splittable<TrackableSearch<T>> {

  /**
   * Gets the {@link ProgressTracker} object that is in use for tracking search progress. The object
   * returned by this method contains the best solution found during the search (including across
   * multiple concurrent runs if the search is multithreaded, or across multiple restarts if the run
   * methods were called multiple times), as well as cost of that solution, among other information.
   * See the {@link ProgressTracker} documentation for more information about the search data
   * tracked by this object.
   *
   * @return the {@link ProgressTracker} in use by this metaheuristic.
   */
  ProgressTracker<T> getProgressTracker();

  /**
   * Sets the {@link ProgressTracker} object that is in use for tracking search progress. Any
   * previously set ProgressTracker is replaced by this one.
   *
   * @param tracker The new ProgressTracker to set. The tracker must not be null. This method does
   *     nothing if tracker is null.
   */
  void setProgressTracker(ProgressTracker<T> tracker);

  /**
   * Gets the total run length of the metaheuristic. This is the total run length across all calls
   * to the search. This may differ from what may be expected based on run lengths. For example, the
   * search terminates if it finds the theoretical best solution, and also immediately returns if a
   * prior call found the theoretical best. In such cases, the total run length may be less than the
   * requested run length.
   *
   * <p>The meaning of run length may vary from one metaheuristic to another. Therefore,
   * implementing classes should provide fresh documentation rather than relying entirely on the
   * interface documentation.
   *
   * @return the total run length of the metaheuristic
   */
  long getTotalRunLength();

  /**
   * Gets a reference to the problem that this search is solving.
   *
   * @return a reference to the problem.
   */
  Problem<T> getProblem();
}
