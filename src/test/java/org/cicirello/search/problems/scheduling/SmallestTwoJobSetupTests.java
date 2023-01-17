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

/** JUnit tests for SmallestTwoJobSetup. */
public class SmallestTwoJobSetupTests extends SchedulingHeuristicValidation {

  @Test
  public void testSmallestTwoJobSetup() {
    double e = SmallestTwoJobSetup.MIN_H;
    int highS = (int) Math.ceil(1 / e) * 2;
    int[][] s = {
      {0, highS, highS, 0, highS},
      {1, 0, 7, 3, 1},
      {9, 9, 3, 0, 9},
      {7, 1, 7, 6, 7},
      {5, 5, 2, 5, 13}
    };
    int[] w = {7, 8, 2, 10, 4};
    int[] p = {2, 5, 9, 2, 10};
    double[] expected = {1, 0.5, 0.25, 0.125, 0.0625};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, s);
    SmallestTwoJobSetup h = new SmallestTwoJobSetup(problem);
    PartialPermutation partial = new PartialPermutation(expected.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10, "scheduled first");
    }
    partial.extend(0);
    for (int j = 1; j < expected.length; j++) {
      if (s[0][j] == highS) assertEquals(e, h.h(partial, j, null), 1E-10);
      else assertEquals(0.5, h.h(partial, j, null), 1E-10);
    }

    partial.extend(0);
    partial.extend(0);
    partial.extend(0);
    int k = partial.getExtension(0);
    assertEquals(1.0 / (1.0 + s[partial.getLast()][k]), h.h(partial, k, null), 1E-10);
    FakeProblemWeightsPTime problemNoSetups = new FakeProblemWeightsPTime(w, p, 0);
    h = new SmallestTwoJobSetup(problemNoSetups);
    partial = new PartialPermutation(expected.length);
    for (int j = 0; j < p.length; j++) {
      assertEquals(1.0, h.h(partial, j, null), 1E-10);
    }
  }
}
