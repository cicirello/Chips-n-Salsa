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
 * TerminationStrategy that terminates an evolutionary algorithm when a specified number of
 * generations elapses without further improvement to the fitness of the most-fit solution found.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class StagnationTerminator<T> implements TerminationStrategy<T> {

  private final int maximumGenerationsWithoutImprovement;
  private double bestFitnessThusFar;
  private int generation;

  /**
   * Initializes the TerminationStrategy
   *
   * @param maximumGenerationsWithoutImprovement the maximum numnber of generations to run the
   *     evolutionary algorithm (EA) without further improvement to the best found solution. This
   *     termination strategy tracks the generation number for each time a solution is found with
   *     fitness better than previous found solutions, and will terminate if no improvement occurs
   *     for this number of generations.
   */
  public StagnationTerminator(int maximumGenerationsWithoutImprovement) {
    this.maximumGenerationsWithoutImprovement = maximumGenerationsWithoutImprovement;
  }

  @Override
  public boolean terminate(TerminationStrategy.ExecutionState<T> state) {
    if (state.numCompletedGenerations() == 0 || state.bestFitness() > bestFitnessThusFar) {
      generation = state.numCompletedGenerations();
      bestFitnessThusFar = state.bestFitness();
      return false;
    }
    if (state.numCompletedGenerations() - generation >= maximumGenerationsWithoutImprovement) {
      return true;
    }
    return false;
  }
}
