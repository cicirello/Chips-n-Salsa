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
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;

/**
 * This heuristic is smallest normalized setup. The normalized setup of job j is: s[i][j] /
 * ave<sub>k</sub>(s[k][j]), where s[i][j] is the setup time for job j if it follows job i, job i is
 * the most recently scheduled job, and ave<sub>k</sub>(s[k][j]) is the average setup for job j,
 * averaged over the possible remaining predecessors. We define the smallest normalized setup
 * heuristic as: h(j) = 1 / (1 + s[i][j]/ave<sub>k</sub>(s[k][j])).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public final class SmallestNormalizedSetup extends SchedulingHeuristic {

  /**
   * Constructs an SmallestNormalizedSetup heuristic.
   *
   * @param problem The instance of a scheduling problem that is the target of the heuristic.
   */
  public SmallestNormalizedSetup(SingleMachineSchedulingProblem problem) {
    super(problem);
  }

  @Override
  public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
    if (HAS_SETUPS) {
      int n = p.numExtensions();
      if (n == 1) return 0.5;
      double s =
          (p.size() == 0 ? data.getSetupTime(element) : data.getSetupTime(p.getLast(), element));
      if (s == 0) return 1.0;
      double aveS = s;
      for (int i = 0; i < n; i++) {
        int k = p.getExtension(i);
        if (k != element) {
          aveS += data.getSetupTime(k, element);
        }
      }
      aveS /= n;
      double denominator = 1.0 + s / aveS;
      double h = 1.0 / denominator;
      // Case when h <= MIN_H is especially degenerate: would require number of
      // jobs n to be at least 1/MIN_H, al but one of which to have setup of 0.
      // Check here probably unnecessary.
      return h <= MIN_H ? MIN_H : h;
    } else {
      return 1;
    }
  }
}
