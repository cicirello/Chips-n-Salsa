/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.PartialPermutation;
import org.junit.jupiter.api.*;

/** JUnit tests for MinimumSlackTime. */
public class MinimumSlackTimeTests extends SchedulingHeuristicValidation {

  @Test
  public void testMST() {
    int[] p = {2, 4, 3, 5};
    int[] duedates = {3, 8, 5, 2};
    double[] expected = {7, 4, 6, 11};
    FakeProblemDuedates problem = new FakeProblemDuedates(duedates, p);
    MinimumSlackTime h = new MinimumSlackTime(problem);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    for (int j = 0; j < duedates.length; j++) {
      assertEquals(expected[j], h.h(null, j, inc), 1E-10);
    }

    PartialPermutation partial = new PartialPermutation(expected.length);
    problem = new FakeProblemDuedates(duedates, p, 3);
    h = new MinimumSlackTime(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 0; j < duedates.length; j++) {
      assertEquals(expected[j] + 3, h.h(partial, j, inc), 1E-10);
    }
    partial.extend(3);
    for (int j = 0; j < duedates.length - 1; j++) {
      assertEquals(expected[j] + 6 + j, h.h(partial, j, inc), 1E-10);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              int[] p2 = {1, 1};
              int[] w2 = {1, 1};
              FakeProblemWeightsPTime pr = new FakeProblemWeightsPTime(p2, w2);
              new MinimumSlackTime(pr);
            });
  }
}
