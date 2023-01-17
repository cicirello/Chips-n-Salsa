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

/** JUnit tests for ATCS. */
public class ATCSTests extends SchedulingHeuristicValidation {

  @Test
  public void testATCS0() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    double[] expected0 = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    double[] slack = new double[p.length];
    double pAve = 0;
    for (int i = 1; i < p.length; i++) {
      slack[i] = 20 - p[i] - p[0];
      if (slack[i] < 0) slack[i] = 0;
      pAve += p[i];
    }
    pAve += p[0];
    pAve /= p.length;
    PartialPermutation partial = new PartialPermutation(expected0.length);
    // Doesn't really matter: partial.extend(0);
    // All late tests, k1=2, k2=7?shouldn't matter
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
    ATCS h = new ATCS(problem, 2, 7);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      assertEquals(expected0[j], h.h(partial, j, inc), 1E-10, "negativeSlack, j:" + j);
    }
    // d=20, k1=2, k2=7?shouldn't matter
    problem = new FakeProblemWeightsPTime(w, p, 20);
    h = new ATCS(problem, 2, 7);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = Math.exp(-0.5 * slack[j] / pAve);
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-10, "positiveSlack, j:" + j);
    }
    // d=20, k=4, k2=7?shouldn't matter
    problem = new FakeProblemWeightsPTime(w, p, 20);
    h = new ATCS(problem, 4, 7);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = Math.exp(-0.25 * slack[j] / pAve);
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-10, "positiveSlack, j:" + j);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              int[] p2 = {1, 1};
              int[] w2 = {1, 1};
              FakeProblemWeightsPTime pr = new FakeProblemWeightsPTime(p2, w2);
              new ATCS(pr, 1, 1);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ATCS(new FakeProblemWeightsPTime(w, p, 0), 0.0, 0.001));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ATCS(new FakeProblemWeightsPTime(w, p, 0), 0.001, 0.0));
  }

  @Test
  public void testATCS1() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    double[] expected0 = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    double[] slack = new double[p.length];
    double pAve = 0;
    for (int i = 1; i < p.length; i++) {
      slack[i] = 20 - p[i];
      if (slack[i] < 0) slack[i] = 0;
      pAve += p[i];
    }
    pAve /= p.length - 1;
    PartialPermutation partial = new PartialPermutation(expected0.length);
    // All late tests, k1=2, k2=1
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, 4);
    ATCS h = new ATCS(problem, 2, 1);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    double sAve = h.getSetupAverage();
    for (int j = 1; j < expected0.length; j++) {
      double correction = expected0[j] * Math.exp(-4.0 / sAve);
      assertEquals(
          correction < e ? e : correction, h.h(partial, j, inc), 1E-10, "negativeSlack, j:" + j);
    }
    // d=20, k1=2, k2=2
    problem = new FakeProblemWeightsPTime(w, p, 20, 4);
    h = new ATCS(problem, 2, 2);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      double correction = Math.exp(-0.5 * slack[j] / pAve) * Math.exp(-2.0 / sAve);
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-4, "positiveSlack, j:" + j);
    }
    // d=20, k=4, k2=3
    problem = new FakeProblemWeightsPTime(w, p, 20, 4);
    h = new ATCS(problem, 4, 3);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      double correction = Math.exp(-0.25 * slack[j] / pAve) * Math.exp(-4.0 / 3.0 / sAve);
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-4, "positiveSlack, j:" + j);
    }
  }

  @Test
  public void testATCSdefault() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    int[] dates = {30, 25, 20, 45, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22};
    double[] expected0 = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    double[] slack = new double[p.length];
    double pAve = 0;
    for (int i = 1; i < p.length; i++) {
      slack[i] = 20 - p[i];
      if (slack[i] < 0) slack[i] = 0;
      pAve += p[i];
    }
    pAve /= p.length - 1;
    PartialPermutation partial = new PartialPermutation(expected0.length);
    // All late tests,
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, 4);
    ATCS h = new ATCS(problem);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    double sAve = h.getSetupAverage();
    for (int j = 1; j < expected0.length; j++) {
      assertTrue(expected0[j] >= h.h(partial, j, inc), "negativeSlack, j:" + j);
    }
    // d=20,
    problem = new FakeProblemWeightsPTime(w, p, 20, 4);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      assertTrue(expected0[j] >= h.h(partial, j, inc), "positiveSlack, j:" + j);
    }
    // d=all different,
    problem = new FakeProblemWeightsPTime(w, p, dates, 4);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      assertTrue(expected0[j] >= h.h(partial, j, inc), "positiveSlack, j:" + j);
    }
    // wider duedate range
    int[] dw = {20, highP * 2, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    problem = new FakeProblemWeightsPTime(w, p, dw, 4);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      assertTrue(expected0[j] >= h.h(partial, j, inc), "positiveSlack, j:" + j);
    }
    // extremely wide duedate range
    int[] dew = {20, highP * 4, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    problem = new FakeProblemWeightsPTime(w, p, dew, 4);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      assertTrue(expected0[j] >= h.h(partial, j, inc), "positiveSlack, j:" + j);
    }
    // variable setup times, higher variance
    int[] p0 = {1, 2, 4, 8};
    int[] w0 = {3, 3, 3, 3};
    int[] d0 = {30, 30, 30, 30};
    double[] e0 = {3.0, 1.5, 0.75, 0.375};
    int[][] setups = {
      {1, 2, 3, 4},
      {2, 4, 6, 8},
      {5, 2, 4, 7},
      {10, 2, 1, 7}
    };
    problem = new FakeProblemWeightsPTime(w0, p0, d0, setups);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 0; j < e0.length; j++) {
      assertTrue(e0[j] >= h.h(partial, j, inc));
    }
    // variable setup times
    int[][] sLowVar = {
      {10, 12, 10, 11},
      {12, 10, 10, 11},
      {12, 10, 10, 11},
      {12, 10, 10, 11}
    };
    int[] dlow = {20, 20, 20, 20};
    problem = new FakeProblemWeightsPTime(w0, p0, dlow, sLowVar);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 0; j < e0.length; j++) {
      assertTrue(e0[j] >= h.h(partial, j, inc));
    }
    // zero setups
    int[][] setupZero = {
      {0, 0, 0, 0},
      {0, 0, 0, 0},
      {0, 0, 0, 0},
      {0, 0, 0, 0}
    };
    problem = new FakeProblemWeightsPTime(w0, p0, dlow, setupZero);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 0; j < e0.length; j++) {
      assertTrue(e0[j] >= h.h(partial, j, inc));
    }
    // variable setup times: very low variance
    int N = 10000;
    int[] w1 = new int[N];
    int[] d1 = new int[N];
    int[] p1 = new int[N];
    int[][] s1 = new int[N][N];
    for (int i = 0; i < N; i++) {
      w1[i] = 2;
      d1[i] = N * 5;
      p1[i] = 2 + i % 2;
      for (int j = 0; j < N; j++) {
        s1[i][j] = 10;
      }
    }
    s1[0][0] = 11;
    problem = new FakeProblemWeightsPTime(w1, p1, d1, s1);
    h = new ATCS(problem);
    inc = h.createIncrementalEvaluation();
    for (int j = 0; j < e0.length; j++) {
      assertTrue(e0[j] >= h.h(partial, j, inc));
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              int[] p2 = {1, 1};
              int[] w2 = {1, 1};
              FakeProblemWeightsPTime pr = new FakeProblemWeightsPTime(p2, w2);
              new ATCS(pr);
            });
  }

  @Test
  public void testATCSSetupsAll0() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {0, 1, 1, 1, 1};
    int[] p = {0, 1, 2, 4, 8};
    int[] d = {0, 7, 5, 20, 13};
    int[][] s = new int[d.length][d.length];
    double[] expected0 = {
      999, Math.exp(-1), 0.5 * Math.exp(-0.5), 0.25 * Math.exp(-8.0 / 3), 0.125 * Math.exp(-5.0 / 6)
    };
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, d, s);
    ATCS h = new ATCS(problem, 2.0, 3.0);
    PartialPermutation partial = new PartialPermutation(expected0.length);
    partial.extend(0);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      assertEquals(expected0[j], h.h(partial, j, inc), 1E-10);
    }

    // force small heuristic case with k1
    int wp = 1;
    double k = 0.5 / Math.log(1.0 / e);
    int due = 2;
    problem = new FakeProblemWeightsPTime(new int[] {wp}, new int[] {wp}, due);
    h = new ATCS(problem, k, 1);
    inc = h.createIncrementalEvaluation();
    partial = new PartialPermutation(1);
    assertEquals(e, h.h(partial, 0, inc), 1E-10);

    // force small heuristic case with k2
    int[][] sets = {{1}};
    problem = new FakeProblemWeightsPTime(new int[] {wp}, new int[] {wp}, new int[] {due}, sets);
    h = new ATCS(problem, 1, k);
    inc = h.createIncrementalEvaluation();
    partial = new PartialPermutation(1);
    assertEquals(e, h.h(partial, 0, inc), 1E-10);
  }

  @Test
  public void testATCSSetupsAll2() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {0, 1, 1, 1, 1};
    int[] p = {0, 1, 2, 4, 8};
    int[] d = {0, 7, 5, 20, 13};
    int[][] s = new int[d.length][d.length];
    for (int i = 0; i < s.length; i++) {
      for (int j = 0; j < s.length; j++) {
        s[i][j] = 2;
      }
    }
    s[0][0] = 0;
    s[4][4] = 0;
    double sbar = (s.length * s.length - 2) * 2.0 / (s.length * s.length);
    double[] expected0 = {
      999, Math.exp(-1), 0.5 * Math.exp(-0.5), 0.25 * Math.exp(-8.0 / 3), 0.125 * Math.exp(-5.0 / 6)
    };
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, d, s);
    ATCS h = new ATCS(problem, 2.0, 3.0);
    PartialPermutation partial = new PartialPermutation(expected0.length);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    for (int j = 1; j < expected0.length; j++) {
      double expect = j < 4 ? expected0[j] * Math.exp(-2.0 / (3 * sbar)) : expected0[j];
      assertEquals(expect, h.h(partial, j, inc), 1E-10, "j:" + j);
    }
  }

  @Test
  public void testATCSSetupsAll2NonEmptyPartial() {
    double e = ATCS.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {0, 1, 1, 1, 1};
    int[] p = {0, 1, 2, 4, 8};
    int[] d = {0, 7, 5, 20, 13};
    int[][] s = new int[d.length][d.length];
    for (int i = 0; i < s.length; i++) {
      for (int j = 0; j < s.length; j++) {
        s[i][j] = 2;
      }
    }
    s[0][0] = 0;
    s[4][4] = 0;
    double sbar = (s.length * s.length - 2) * 2.0 / (s.length * s.length);
    double[] expected0 = {
      999, Math.exp(-1), 0.5 * Math.exp(-0.5), 0.25 * Math.exp(-8.0 / 3), 0.125 * Math.exp(-5.0 / 6)
    };
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, d, s);
    ATCS h = new ATCS(problem, 2.0, 3.0);
    PartialPermutation partial = new PartialPermutation(expected0.length);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    partial.extend(0);
    for (int j = 1; j < expected0.length - 1; j++) {
      double expect = j < 4 ? expected0[j] * Math.exp(-2.0 / (3 * sbar)) : expected0[j];
      assertEquals(expect, h.h(partial, j, inc), 1E-10, "j:" + j);
    }
  }
}
