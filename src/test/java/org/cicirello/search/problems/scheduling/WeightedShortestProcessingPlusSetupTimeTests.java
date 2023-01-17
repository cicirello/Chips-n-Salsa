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

import org.cicirello.search.ss.PartialPermutation;
import org.junit.jupiter.api.*;

/** JUnit tests for WeightedShortestProcessingPlusSetupTime. */
public class WeightedShortestProcessingPlusSetupTimeTests extends SchedulingHeuristicValidation {

  @Test
  public void testWSPTSetupAdjusted() {
    double e = WeightedShortestProcessingPlusSetupTime.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    double[] expected = {1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    PartialPermutation partial = new PartialPermutation(expected.length);
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p);
    WeightedShortestProcessingPlusSetupTime h =
        new WeightedShortestProcessingPlusSetupTime(problem);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }
    partial.extend(p.length - 1);
    for (int j = 0; j < expected.length - 1; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }

    int[] ps = {0, 1, 3, 7, 0, 1, 3, 7, 0, 1, 3, 7, highP - 1};
    problem = new FakeProblemWeightsPTime(w, ps, 0, 1);
    h = new WeightedShortestProcessingPlusSetupTime(problem);
    partial = new PartialPermutation(expected.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }
    partial.extend(4);
    double[] expected2 = {1.0 / 8, 1.0 / 10, 1.0 / 13, 1.0 / 18};
    for (int j = 0; j < expected2.length; j++) {
      assertEquals(expected2[j], h.h(partial, j, null), 1E-10);
    }
  }
}
