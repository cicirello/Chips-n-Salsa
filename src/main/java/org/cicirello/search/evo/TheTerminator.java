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

import java.util.ArrayList;
import java.util.Collection;

/**
 * TheTerminator enables composition of multiple terminator strategies, terminating an evolutionary
 * algorithm if any of the component {@link TerminationStrategy} instance's termination criteria are
 * met.
 *
 * <p>Note that it is unnecessary to include maximum number of generations as that is a required
 * parameter of the {@link
 * org.cicirello.search.evo.PopulationMetaheuristic#optimize(int,TerminationStrategy)} method, and
 * is always a termination criteria. It is also unnecessary to include a termination criteria for
 * the case when a solution matches a lower bound detectable by the definition of the optimization
 * problem you are solving via the {@link org.cicirello.search.problems.OptimizationProblem#minCost}
 * or {@link org.cicirello.search.problems.IntegerCostOptimizationProblem#minCost} methods. The
 * library automatically terminates any of the evolutionary algorithms or other metaheuristics in
 * this case.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TheTerminator<T> implements TerminationStrategy<T> {

  private final ArrayList<TerminationStrategy<T>> terminators;

  /**
   * Initializes TheTerminator.
   *
   * @param terminators a collection of TerminationStrategy interfaces. The evolutionary algorithm
   *     will terminate if any of the terminators signal to terminate. We recommend that you order
   *     the terminators with the least costly computationally first in the collection and the most
   *     costly last. The {@link #terminate} method short-circuits on the first signal to terminate.
   */
  public TheTerminator(Collection<TerminationStrategy<T>> terminators) {
    this.terminators = new ArrayList<TerminationStrategy<T>>(terminators);
  }

  @Override
  public boolean terminate(TerminationStrategy.ExecutionState<T> state) {
    return terminators.stream().anyMatch(t -> t.terminate(state));
  }
}
