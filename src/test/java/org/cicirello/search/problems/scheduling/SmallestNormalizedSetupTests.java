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
import org.cicirello.search.ss.PartialPermutation;
import org.junit.jupiter.api.*;

/** JUnit tests for SmallestNormalizedSetup. */
public class SmallestNormalizedSetupTests extends SchedulingHeuristicValidation {

  @Test
  public void testSmallestNormalizedSetup() {
    double e = SmallestNormalizedSetup.MIN_H;
    int highS = (int) Math.ceil(1 / e) * 2;
    int[][] s = {
      {0, 3, 2, 4, highS},
      {1, 3, 2, 4, 1},
      {1, 3, 9, 4, 1},
      {1, 3, 1, 999, 1},
      {1, 3, 1, 4, 999} // don't test last 2 jobs
    };
    int[] w = {7, 8, 2, 10, 4};
    int[] p = {2, 5, 9, 2, 10};
    double[] expected = {1, 0.5, 0.25};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, s);
    SmallestNormalizedSetup h = new SmallestNormalizedSetup(problem);
    PartialPermutation partial = new PartialPermutation(p.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, null), 1E-10, "j:" + j);
    }
    partial.extend(4);
    double[] expected2 = {0.5, 0.5, 0.6};
    for (int j = 0; j < expected2.length; j++) {
      assertEquals(expected2[j], h.h(partial, j, null), 1E-10);
    }
    partial.extend(3);
    partial.extend(2);
    partial.extend(1);
    assertEquals(0.5, h.h(partial, 0, null), 1E-10);

    FakeProblemWeightsPTime problemNoSetups = new FakeProblemWeightsPTime(w, p, 0);
    h = new SmallestNormalizedSetup(problemNoSetups);
    partial = new PartialPermutation(p.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(1.0, h.h(partial, j, null), 1E-10);
    }

    final int N = highS / 2;

    FakeProblemSmallestNormSetup pr = new FakeProblemSmallestNormSetup(N);
    h = new SmallestNormalizedSetup(pr);
    partial = new PartialPermutation(N);
    assertEquals(e, h.h(partial, 2, null), 1E-10);
  }

  private static class FakeProblemSmallestNormSetupData
      implements SingleMachineSchedulingProblemData {

    private final int N;

    private FakeProblemSmallestNormSetupData(int N) {
      this.N = N;
    }

    public int getProcessingTime(int j) {
      return 1;
    }

    public int[] getCompletionTimes(Permutation schedule) {
      return null;
    }

    public int numberOfJobs() {
      return N;
    }

    public boolean hasSetupTimes() {
      return true;
    }

    public int getSetupTime(int j) {
      return 2;
    }

    public int getSetupTime(int i, int j) {
      if (i == 1 && j == 2) return 0;
      else return 0;
    }
  }

  private static class FakeProblemSmallestNormSetup implements SingleMachineSchedulingProblem {

    private final int N;

    private FakeProblemSmallestNormSetup(int N) {
      this.N = N;
    }

    public int cost(Permutation candidate) {
      return 0;
    }

    public int value(Permutation candidate) {
      return 0;
    }

    public SingleMachineSchedulingProblemData getInstanceData() {
      return new FakeProblemSmallestNormSetupData(N);
    }
  }
}
