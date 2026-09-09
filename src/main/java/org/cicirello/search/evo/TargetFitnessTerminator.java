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
 * TerminationStrategy that terminates an evolutionary algorithm if a solution with a target fitness
 * value (or better) is found.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TargetFitnessTerminator<T> implements TerminationStrategy<T> {

  private final double targetFitness;

  /**
   * Initializes the TerminationStrategy
   *
   * @param targetFitness the target fitness. Note that this is the target fitness value and not a
   *     target for the objective function you are optimizing, which may or may not be the same
   *     thing depending upon how you have configured the evolutionary algorithm
   */
  public TargetFitnessTerminator(double targetFitness) {
    this.targetFitness = targetFitness;
  }

  @Override
  public boolean terminate(TerminationStrategy.ExecutionState<T> state) {
    return state.bestFitness() >= targetFitness;
  }
}
