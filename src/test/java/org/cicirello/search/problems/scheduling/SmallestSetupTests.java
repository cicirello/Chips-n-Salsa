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

/** JUnit tests for SmallestSetup. */
public class SmallestSetupTests extends SchedulingHeuristicValidation {

  @Test
  public void testSmallestSetup() {
    double e = SmallestSetup.MIN_H;
    int highS = (int) Math.ceil(1 / e) * 2;
    int[][] s = {
      {0, highS, highS, highS, highS},
      {1, 1, 7, 3, 1},
      {1, 1, 3, 1, 1},
      {1, 1, 1, 7, 1},
      {1, 1, 1, 1, 15}
    };
    int[] w = {7, 8, 2, 10, 4};
    int[] p = {2, 5, 9, 2, 10};
    double[] expected = {1, 0.5, 0.25, 0.125, 0.0625};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, s);
    SmallestSetup h = new SmallestSetup(problem);
    PartialPermutation partial = new PartialPermutation(expected.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }
    partial.extend(0);
    for (int j = 1; j < expected.length; j++) {
      assertEquals(e, h.h(partial, j, null), 1E-10);
    }
    partial.extend(1);
    double[] expected2 = {999, 999, 0.125, 0.25, 0.5};
    for (int j = 2; j < expected.length; j++) {
      assertEquals(expected2[j], h.h(partial, j, null), 1E-10);
    }

    FakeProblemWeightsPTime problemNoSetups = new FakeProblemWeightsPTime(w, p, 0);
    h = new SmallestSetup(problemNoSetups);
    partial = new PartialPermutation(expected.length);
    for (int j = 0; j < p.length; j++) {
      assertEquals(1.0, h.h(partial, j, null), 1E-10);
    }
  }
}
