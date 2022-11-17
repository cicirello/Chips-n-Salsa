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
 * This interface defines the required methods for implementations of simple metaheuristics that
 * locally optimize from some initial solution (random or otherwise) whose run length is
 * self-determined, such as hill climbers that terminate upon reaching a local optima.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.9.2020
 */
public interface SimpleLocalMetaheuristic<T extends Copyable<T>> extends SimpleMetaheuristic<T> {

  /**
   * Executes a single run of a metaheuristic, whose run length cannot be specified (e.g., a hill
   * climber that terminates when it reaches a local optima), beginning at a specified solution. If
   * this method is called multiple times, each call reinitializes any control parameters that may
   * have changed during the previous call to optimize to the start of run state.
   *
   * @param start The desired starting solution.
   * @return The current solution at the end of this run and its cost, which may or may not be the
   *     same as the solution contained in this metaheuristic's {@link
   *     org.cicirello.search.ProgressTracker ProgressTracker}, which contains the best of all runs.
   *     Returns null if the run did not execute, such as if the ProgressTracker already contains
   *     the theoretical best solution.
   */
  SolutionCostPair<T> optimize(T start);

  @Override
  SimpleLocalMetaheuristic<T> split();
}
