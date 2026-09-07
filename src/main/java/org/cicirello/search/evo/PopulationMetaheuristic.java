/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * This interface defines the required methods for implementations of metaheuristics that maintain a
 * population of candidate solutions such as evolutionary algorithms. The primary additional
 * functionality defined over that already included in the parent interface is the ability to define
 * termination criteria based on the state of the underlying population of solutions.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface PopulationMetaheuristic<T extends Copyable<T>>
    extends ReoptimizableMetaheuristic<T> {

  /**
   * Executes a run of the metaheuristic beginning at a randomly generated population of solutions.
   * If this method is called multiple times, each call begins at a new randomly generated starting
   * population, and reinitializes any control parameters of the metaheuristic that may have changed
   * during the previous call to optimize to start of run state.
   *
   * @param numGenerations The maximum number of generations, which will cause the run to terminate
   *     even if other criteria specified in the terminator are not currently met. Some selection
   *     operators and replacement strategies may need to know the maximum number of generations to
   *     operate properly, which is why this parameter is needed separately from other termination
   *     criteria.
   * @param terminator The termination strategy, such that the optimize method runs until that
   *     strategy is satisfied.
   * @return The current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this metaheuristic's {@link org.cicirello.search.ProgressTracker ProgressTracker}, which
   *     contains the best of all runs. Returns null if the run did not execute, such as if the
   *     ProgressTracker already contains the theoretical best solution.
   */
  SolutionCostPair<T> optimize(int numGenerations, TerminationStrategy<T> terminator);

  @Override
  PopulationMetaheuristic<T> split();
}
