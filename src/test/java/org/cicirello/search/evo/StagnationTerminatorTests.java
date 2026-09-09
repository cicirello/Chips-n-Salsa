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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test cases for StagnationTerminator interface. */
public class StagnationTerminatorTests {

  @Test
  public void testNoImprovementSinceBeginning() {
    StagnationTerminator<String> terminator = new StagnationTerminator<String>(5);

    for (int numCompletedGenerations = 0; numCompletedGenerations < 5; numCompletedGenerations++) {
      long fitnessEvaluations = 10 + 10 * numCompletedGenerations;
      String bestSolution = "hello";
      double bestFitness = 5;

      TerminationStrategy.ExecutionState<String> ex =
          new TerminationStrategy.ExecutionState<String>(
              numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

      assertFalse(terminator.terminate(ex));
    }

    int numCompletedGenerations = 5;
    long fitnessEvaluations = 10 + 10 * numCompletedGenerations;
    String bestSolution = "hello";
    double bestFitness = 5;

    TerminationStrategy.ExecutionState<String> ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertTrue(terminator.terminate(ex));
  }

  @Test
  public void testNoImprovementAfterGenerationN() {
    StagnationTerminator<String> terminator = new StagnationTerminator<String>(5);

    final int N = 10;

    double bestFitness = 5;
    for (int numCompletedGenerations = 0; numCompletedGenerations < N; numCompletedGenerations++) {
      long fitnessEvaluations = 10 + 10 * numCompletedGenerations;
      String bestSolution = "hello";

      TerminationStrategy.ExecutionState<String> ex =
          new TerminationStrategy.ExecutionState<String>(
              numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

      assertFalse(terminator.terminate(ex));

      bestFitness += 1.0;
    }

    for (int numCompletedGenerations = N;
        numCompletedGenerations < N + 5;
        numCompletedGenerations++) {
      long fitnessEvaluations = 10 + 10 * numCompletedGenerations;
      String bestSolution = "hello";

      TerminationStrategy.ExecutionState<String> ex =
          new TerminationStrategy.ExecutionState<String>(
              numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

      assertFalse(
          terminator.terminate(ex),
          "Failed for numCompletedGenerations=" + numCompletedGenerations);
    }

    int numCompletedGenerations = N + 5;
    long fitnessEvaluations = 10 + 10 * numCompletedGenerations;
    String bestSolution = "hello";

    TerminationStrategy.ExecutionState<String> ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertTrue(terminator.terminate(ex));
  }
}
