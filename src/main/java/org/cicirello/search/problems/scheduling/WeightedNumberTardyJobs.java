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
 * Implements the scheduling cost function known as weighted number of tardy jobs, which we want to
 * minimize. The lateness L[j] of job j is: L[j] = C[j] - d[j], where C[j] is the time it is
 * completed by the machine, and d[j] is its due date. The tardiness T[j] = max(0, L[j]). So
 * although lateness can be negative, tardiness is never negative. The weighted number of tardy jobs
 * is equal to the sum of the weights, w[j], of jobs j such that T[j] &gt; 0.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.15.2020
 */
public final class WeightedNumberTardyJobs implements SingleMachineSchedulingProblem {

  private final SingleMachineSchedulingProblemData instanceData;

  /**
   * Constructs a single machine scheduling problem for minimizing weighted number of tardy jobs.
   *
   * @param instanceData An encapsulation of the job characteristics, such as processing times, etc.
   * @throws IllegalArgumentException if instanceData.hasDueDates() returns false.
   */
  public WeightedNumberTardyJobs(SingleMachineSchedulingProblemData instanceData) {
    this.instanceData = instanceData;
    if (!instanceData.hasDueDates()) {
      throw new IllegalArgumentException("This cost function requires due dates.");
    }
  }

  @Override
  public SingleMachineSchedulingProblemData getInstanceData() {
    return instanceData;
  }

  @Override
  public int cost(Permutation candidate) {
    int[] c = instanceData.getCompletionTimes(candidate);
    int weightedCount = 0;
    for (int i = 0; i < c.length; i++) {
      int lateness = c[i] - instanceData.getDueDate(i);
      if (lateness > 0) weightedCount += instanceData.getWeight(i);
    }
    return weightedCount;
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
