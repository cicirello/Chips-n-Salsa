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
 * This class implements a variation the weighted shortest process time heuristic with non-zero
 * heuristic values only for late jobs, but adjusted to incorporate setups times for problems with
 * sequence-dependent setups.
 *
 * <p>The heuristic is essentially that of {@link WeightedShortestProcessingTimeLateOnly}, but using
 * the setup time adjustment also used by {@link WeightedShortestProcessingPlusSetupTime}.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public final class WeightedShortestProcessingPlusSetupTimeLateOnly
    extends WeightedShortestProcessingPlusSetupTime {

  /**
   * Constructs an WeightedShortestProcessingPlusSetupTimeLateOnly heuristic.
   *
   * @param problem The instance of a scheduling problem that is the target of the heuristic.
   * @throws IllegalArgumentException if problem.hasDueDates() returns false.
   */
  public WeightedShortestProcessingPlusSetupTimeLateOnly(SingleMachineSchedulingProblem problem) {
    super(problem);
    if (!data.hasDueDates()) {
      throw new IllegalArgumentException("This heuristic requires due dates.");
    }
  }

  @Override
  public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
    if (((IncrementalTimeCalculator) incEval).slack(element, p) >= 0) {
      return MIN_H;
    }
    return super.h(p, element, incEval);
  }

  @Override
  public IncrementalEvaluation<Permutation> createIncrementalEvaluation() {
    return new IncrementalTimeCalculator();
  }
}
