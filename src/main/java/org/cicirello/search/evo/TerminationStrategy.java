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
 * This interface is used to define a termination strategy.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface TerminationStrategy<T> {

  /**
   * The state of the evolutionary algorithm prior to the start of a generation.
   *
   * @param <T> The type of object under optimization.
   * @param numCompletedGenerations the number of completed generations
   * @param fitnessEvaluations the total number of fitness evaluations so far for this call to
   *     optimize
   * @param bestSolution the best solution found across all generations so far for this run (i.e.,
   *     for this call to optimize)
   * @param bestFitness the fitness of the best solution found across all generations so far for
   *     this run (i.e., for this call to optimize)
   * @param currentPopulation the current population, enabling termination strategies that rely on
   *     population-level statistics. This provides termination strategies with access to the actual
   *     encodings of the members of the population as well as their fitnesses. Do not attempt to
   *     mutate members of the population here, as the behavior of the EA may become unstable.
   */
  record ExecutionState<T>(
      int numCompletedGenerations,
      long fitnessEvaluations,
      T bestSolution,
      double bestFitness,
      PopulationCandidates<T> currentPopulation) {}

  /**
   * Predicate method to determine whether or not to terminate the current run of the evolutionary
   * algorithm.
   *
   * @param state the state of the evolutionary algorithm prior to the start of a generation
   * @return true if and only if the evolutionary algorithm should terminate
   */
  boolean terminate(ExecutionState<T> state);
}
