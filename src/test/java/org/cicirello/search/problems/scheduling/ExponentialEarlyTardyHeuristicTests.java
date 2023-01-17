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

/** JUnit tests for ExponentialEarlyTardyHeuristic. */
public class ExponentialEarlyTardyHeuristicTests extends SchedulingHeuristicValidation {

  @Test
  public void testEXPETwlptRegion() {
    double e = ExponentialEarlyTardyHeuristic.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] p = {1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8};
    double aveP = 15.0 / 4;
    int[] we = {1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2};
    double[] expected = {
      1 + e, 1.5 + e, 1.75 + e, 1.875 + e, 2 + e, 2 + e, 2 + e, 2 + e, e, 1 + e, 1.5 + e, 1.75 + e
    };

    // This one doesn't really matter for this heuristic.
    // Meaningless different values to ensure don't affect results.
    int[] wt = {2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4};

    for (int k = 1; k <= 4; k++) {
      int a = (int) Math.ceil(aveP * k);
      for (int x = 0; x <= 2; x++) {
        int[] d = {
          x + a + 1, x + a + 2, x + a + 4, x + a + 8, x + a + 1, x + a + 2, x + a + 4, x + a + 8,
          x + a + 1, x + a + 2, x + a + 4, x + a + 8
        };
        FakeEarlyTardyProblem problem = new FakeEarlyTardyProblem(p, we, wt, d);
        ExponentialEarlyTardyHeuristic h =
            k == 1
                ? new ExponentialEarlyTardyHeuristic(problem)
                : new ExponentialEarlyTardyHeuristic(problem, k);
        IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
        PartialPermutation partial = new PartialPermutation(p.length);
        for (int j = 0; j < expected.length; j++) {
          assertEquals(expected[j], h.h(partial, j, inc), 1E-10, "j:" + j);
        }
      }
    }
  }

  @Test
  public void testEXPETwsptRegion() {
    double e = ExponentialEarlyTardyHeuristic.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] p = {1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8};
    double aveP = 15.0 / 4;
    int[] wt = {2, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0};
    int[] we = {1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2};
    double[] expected = {
      4 + e, 3 + e, 2.5 + e, 2.25 + e, 3 + e, 2.5 + e, 2.25 + e, 2.125 + e, 2 + e, 2 + e, 2 + e,
      2 + e
    };

    for (int k = 1; k <= 4; k++) {
      for (int x = 0; x <= 2; x++) {
        int[] d = {
          1 - x, 2 - x, 4 - x, 8 - x, 1 - x, 2 - x, 4 - x, 8 - x, 1 - x, 2 - x, 4 - x, 8 - x
        };
        FakeEarlyTardyProblem problem = new FakeEarlyTardyProblem(p, we, wt, d);
        ExponentialEarlyTardyHeuristic h =
            k == 1
                ? new ExponentialEarlyTardyHeuristic(problem)
                : new ExponentialEarlyTardyHeuristic(problem, k);
        IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
        PartialPermutation partial = new PartialPermutation(p.length);
        for (int j = 0; j < expected.length; j++) {
          assertEquals(expected[j], h.h(partial, j, inc), 1E-10, "j:" + j);
        }
      }
    }
  }

  @Test
  public void testEXPETtransitionRegion1() {
    double e = ExponentialEarlyTardyHeuristic.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] p = {1, 2, 4, 8, 1, 2, 4, 8};
    int[] wt = {2, 2, 2, 2, 1, 1, 1, 1};
    int[] we = {1, 1, 1, 1, 2, 2, 2, 2};
    double[] expected = new double[p.length];
    int k = 3 * 4; // k chosen to ensure we can hit bound exactly
    int[] d = new int[p.length];

    for (int i = 0; i < d.length; i++) {
      d[i] = wt[i] * k * 15 / (4 * (wt[i] + we[i])) + p[i];
      expected[i] = we[i] > 0 ? wt[i] * Math.exp(-1.0 * wt[i] / we[i]) / p[i] : 0;
      expected[i] += e + 2;
    }
    FakeEarlyTardyProblem problem = new FakeEarlyTardyProblem(p, we, wt, d);
    ExponentialEarlyTardyHeuristic h = new ExponentialEarlyTardyHeuristic(problem, k);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    PartialPermutation partial = new PartialPermutation(p.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, inc), 1E-10, "j:" + j);
    }
  }

  @Test
  public void testEXPETtransitionRegion2() {
    double e = ExponentialEarlyTardyHeuristic.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] p = {1, 2, 4, 8, 1, 2, 4, 8};
    int[] wt = {2, 2, 2, 2, 1, 1, 1, 1};
    int[] we = {1, 1, 1, 1, 2, 2, 2, 2};
    double[] expected = new double[p.length];
    int k = 1 * 2 * 3 * 4; // k chosen to ensure we can hit bound exactly
    int[] d = new int[p.length];

    for (int i = 0; i < d.length; i++) {
      d[i] = wt[i] * k * 15 / (4 * (wt[i] + we[i])) + p[i] + 1;
      double numTerm = we[i] + wt[i];
      double pBar = 15.0 / 4;
      expected[i] =
          -numTerm * numTerm * numTerm / (p[i] * k * k * k * pBar * pBar * pBar * we[i] * we[i]);
      expected[i] += e + 2;
    }
    FakeEarlyTardyProblem problem = new FakeEarlyTardyProblem(p, we, wt, d);
    ExponentialEarlyTardyHeuristic h = new ExponentialEarlyTardyHeuristic(problem, k);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    PartialPermutation partial = new PartialPermutation(p.length);
    for (int j = 0; j < expected.length; j++) {
      assertEquals(expected[j], h.h(partial, j, inc), 1E-10, "j:" + j);
    }
  }

  @Test
  public void testEXPETExceptions() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ExponentialEarlyTardyHeuristic(
                    new FakeEarlyTardyProblem(new int[1], new int[1], new int[1], new int[1]),
                    0.9999));
  }
}
