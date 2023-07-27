/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

package org.cicirello.search.restarts;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * This class is used for implementing multistart metaheuristics, that can be restarted at
 * previously found solutions. It can be used to restart any class that implements the {@link
 * ReoptimizableMetaheuristic} interface, and requires specification of either a {@link
 * RestartSchedule} for the purpose of specifying run lengths for the restarts, or a run length if
 * all runs are to be of the same length. A multistart metaheuristic returns the best result from
 * among all of the restarts.
 *
 * @param <T> The type of object being optimized.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ReoptimizableMultistarter<T extends Copyable<T>> extends Multistarter<T>
    implements ReoptimizableMetaheuristic<T> {

  private final ReoptimizableMetaheuristic<T> search;

  /**
   * Constructs a multistart metaheuristic that executes multiple runs of a specified metaheuristic,
   * whose run lengths follow a specified schedule.
   *
   * @param search The metaheuristic to restart multiple times.
   * @param r The schedule of run lengths for the multistart search
   */
  public ReoptimizableMultistarter(ReoptimizableMetaheuristic<T> search, RestartSchedule r) {
    super(search, r);
    this.search = search;
  }

  /**
   * Constructs a multistart metaheuristic that executes multiple runs of a specified metaheuristic,
   * whose runs are all the same in length.
   *
   * @param search The metaheuristic to restart multiple times.
   * @param runLength The length of every restarted run of the metaheuristic.
   * @throws IllegalArgumentException if runLength &lt; 1
   */
  public ReoptimizableMultistarter(ReoptimizableMetaheuristic<T> search, int runLength) {
    super(search, new ConstantRestartSchedule(runLength));
    this.search = search;
  }

  /**
   * Executes a multistart search, calling the underlying metaheuristic the specified number of
   * times, keeping track of the best solution across the multiple runs of the search. Each restart
   * begins at the best solution found so far, but reinitializes any search control parameters.
   *
   * <p>If this method is called multiple times, the restart schedule is not reinitialized, and the
   * run lengths for the additional restarts will continue where the schedule left off.
   *
   * @param numRestarts The number of times to restart the metaheuristic.
   * @return The best end of run solution (and its cost) of this set of restarts, which may or may
   *     not be the same as the solution contained in this metaheuristic's {@link ProgressTracker},
   *     which contains the best of all runs. Returns null if the run did not execute, such as if
   *     the ProgressTracker already contains the theoretical best solution.
   */
  @Override
  public SolutionCostPair<T> reoptimize(int numRestarts) {
    ProgressTracker<T> tracker = search.getProgressTracker();
    SolutionCostPair<T> bestRestart = null;
    for (int i = 0; i < numRestarts && !tracker.isStopped() && !tracker.didFindBest(); i++) {
      SolutionCostPair<T> thisRestart = search.reoptimize(r.nextRunLength());
      if (bestRestart == null || (thisRestart != null && thisRestart.compareTo(bestRestart) < 0))
        bestRestart = thisRestart;
    }
    return bestRestart;
  }

  @Override
  public ReoptimizableMultistarter<T> split() {
    return new ReoptimizableMultistarter<T>(search.split(), r.split());
  }
}
