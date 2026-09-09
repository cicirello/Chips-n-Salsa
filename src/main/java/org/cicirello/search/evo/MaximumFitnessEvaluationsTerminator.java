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

/**
 * TerminationStrategy that terminates an evolutionary algorithm once the total number of fitness
 * evaluations exceeds a threshold. Note that the TerminationStrategy is evaluated once per
 * generation, so this MaximumFitnessEvaluationsTerminator may result in more fitness evaluations
 * than the target threshold (e.g., it won't cause termination in the middle of a generation, such
 * as while evaluating the children that result from crossing or mutating parents).
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class MaximumFitnessEvaluationsTerminator<T> implements TerminationStrategy<T> {

  private final long maximumNumberOfFitnessEvaluations;

  /**
   * Initializes the TerminationStrategy
   *
   * @param maximumNumberOfFitnessEvaluations the target maximum number of fitness evaluations. The
   *     evolutionary algorithm will terminate once this maximum is reached and detected at the
   *     start of a generation
   */
  public MaximumFitnessEvaluationsTerminator(long maximumNumberOfFitnessEvaluations) {
    this.maximumNumberOfFitnessEvaluations = maximumNumberOfFitnessEvaluations;
  }

  @Override
  public boolean terminate(TerminationStrategy.ExecutionState<T> state) {
    return state.fitnessEvaluations() >= maximumNumberOfFitnessEvaluations;
  }
}
