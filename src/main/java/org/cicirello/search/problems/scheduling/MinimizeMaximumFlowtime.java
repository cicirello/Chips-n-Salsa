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

package org.cicirello.search.problems.scheduling;

import org.cicirello.permutations.Permutation;

/**
 * Implements the scheduling cost function known as maximum flowtime (which we want to minimize).
 * The flowtime F[j] of job j is: F[j] = C[j] - r[j], where C[j] is the time it is completed by the
 * machine, and r[j] is its release time. The maximum flowtime cost function is equal to:
 * max<sub>j</sub> { F[j] }.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.15.2020
 */
public final class MinimizeMaximumFlowtime implements SingleMachineSchedulingProblem {

  private final SingleMachineSchedulingProblemData instanceData;

  /**
   * Constructs a single machine scheduling problem for minimizing maximum flowtime.
   *
   * @param instanceData An encapsulation of the job characteristics, such as processing times, etc.
   */
  public MinimizeMaximumFlowtime(SingleMachineSchedulingProblemData instanceData) {
    this.instanceData = instanceData;
  }

  @Override
  public SingleMachineSchedulingProblemData getInstanceData() {
    return instanceData;
  }

  @Override
  public int cost(Permutation candidate) {
    int[] c = instanceData.getCompletionTimes(candidate);
    int max = 0;
    for (int i = 0; i < c.length; i++) {
      int f = c[i] - instanceData.getReleaseDate(i);
      if (f > max) max = f;
    }
    return max;
  }

  @Override
  public int value(Permutation candidate) {
    return cost(candidate);
  }

  @Override
  public int minCost() {
    return 0;
  }
}
