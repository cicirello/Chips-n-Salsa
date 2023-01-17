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

/** JUnit tests for WeightedShortestProcessingTimeLateOnly. */
public class WeightedShortestProcessingTimeLateOnlyTests extends SchedulingHeuristicValidation {

  @Test
  public void testWSPT2() {
    double e = WeightedShortestProcessingTimeLateOnly.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    double[] expected = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    PartialPermutation partial = new PartialPermutation(expected.length);
    // Doesn't really matter: partial.extend(0);
    // All late tests
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, inc), 1E-10, "j:" + j);
    }
    // All on time tests
    problem = new FakeProblemWeightsPTime(w, p, 20);
    h = new WeightedShortestProcessingTimeLateOnly(problem);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected.length; j++) {
      assertEquals(e, h.h(partial, j, inc), 1E-10);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              int[] p2 = {1, 1};
              int[] w2 = {1, 1};
              FakeProblemWeightsPTime pr = new FakeProblemWeightsPTime(p2, w2);
              new WeightedShortestProcessingTimeLateOnly(pr);
            });
  }
}
