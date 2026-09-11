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

import java.util.List;
import org.junit.jupiter.api.*;

/** JUnit test cases for TheTerminator interface. */
public class TheTerminatorTests {

  @Test
  public void test() {
    TheTerminator<String> terminator =
        new TheTerminator<String>(
            List.of(s -> s.numCompletedGenerations() >= 5, s -> s.bestFitness() >= 10.0));

    int numCompletedGenerations = 4;
    long fitnessEvaluations = 10;
    String bestSolution = "hello";
    double bestFitness = 9.9;

    TerminationStrategy.ExecutionState<String> ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertFalse(terminator.terminate(ex));

    numCompletedGenerations = 5;
    ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertTrue(terminator.terminate(ex));

    bestFitness = 10.0;
    ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertTrue(terminator.terminate(ex));

    numCompletedGenerations = 4;
    ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertTrue(terminator.terminate(ex));

    bestFitness = 9.9;
    ex =
        new TerminationStrategy.ExecutionState<String>(
            numCompletedGenerations, fitnessEvaluations, bestSolution, bestFitness, null);

    assertFalse(terminator.terminate(ex));
  }
}
