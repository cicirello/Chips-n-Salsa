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

/** JUnit tests for WeightedCostOverTimeSetupAdjusted. */
public class WeightedCostOverTimeSetupAdjustedTests extends SchedulingHeuristicValidation {

  @Test
  public void testCOVERTSetupAdjustedS0() {
    double e = WeightedCostOverTimeSetupAdjusted.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
    double[] expected0 = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    double[] slack = new double[p.length];
    for (int i = 1; i < p.length; i++) {
      slack[i] = 20 - p[i] - p[0];
      if (slack[i] < 0) slack[i] = 0;
    }
    PartialPermutation partial = new PartialPermutation(expected0.length);
    // Doesn't really matter: partial.extend(0);
    // All late tests
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
    WeightedCostOverTimeSetupAdjusted h = new WeightedCostOverTimeSetupAdjusted(problem);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      assertEquals(expected0[j], h.h(partial, j, inc), 1E-10, "negativeSlack, j:" + j);
    }
    // d=20, k default of 2
    problem = new FakeProblemWeightsPTime(w, p, 20);
    h = new WeightedCostOverTimeSetupAdjusted(problem);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = 1.0 - 0.5 * slack[j] / p[j];
      if (correction <= 0) correction = 0;
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-10, "positiveSlack, j:" + j);
    }
    // d=20, k=4
    problem = new FakeProblemWeightsPTime(w, p, 20);
    h = new WeightedCostOverTimeSetupAdjusted(problem, 4);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = 1.0 - 0.25 * slack[j] / p[j];
      if (correction <= 0) correction = 0;
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
              new WeightedCostOverTimeSetupAdjusted(pr);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedCostOverTimeSetupAdjusted(new FakeProblemWeightsPTime(w, p, 0), 0));
  }

  @Test
  public void testCOVERTSetupAdjustedS1() {
    double e = WeightedCostOverTimeSetupAdjusted.MIN_H;
    int highP = (int) Math.ceil(1 / e) * 2;
    int[] w = {1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
    int[] p = {0, 0, 1, 3, 7, 0, 1, 3, 7, 0, 1, 3, 7, highP - 1};
    double[] expected0 = {999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
    double[] slack = new double[p.length];
    for (int i = 1; i < p.length; i++) {
      slack[i] = 20 - p[i] - p[0] - 2;
      if (slack[i] < 0) slack[i] = 0;
    }
    PartialPermutation partial = new PartialPermutation(expected0.length);
    // Doesn't really matter: partial.extend(0);
    // All late tests
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, 1);
    WeightedCostOverTimeSetupAdjusted h = new WeightedCostOverTimeSetupAdjusted(problem);
    IncrementalEvaluation<Permutation> inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      assertEquals(expected0[j], h.h(partial, j, inc), 1E-10, "negativeSlack, j:" + j);
    }
    // d=20, k default of 2
    problem = new FakeProblemWeightsPTime(w, p, 20, 1);
    h = new WeightedCostOverTimeSetupAdjusted(problem);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = 1.0 - 0.5 * slack[j] / (1 + p[j]);
      if (correction <= 0) correction = 0;
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-10, "positiveSlack, j:" + j);
    }
    // d=20, k=4
    problem = new FakeProblemWeightsPTime(w, p, 20, 1);
    h = new WeightedCostOverTimeSetupAdjusted(problem, 4);
    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 0);
    for (int j = 1; j < expected0.length; j++) {
      double correction = 1.0 - 0.25 * slack[j] / (1 + p[j]);
      if (correction <= 0) correction = 0;
      double expected = expected0[j] * correction;
      assertEquals(
          expected < e ? e : expected, h.h(partial, j, inc), 1E-10, "positiveSlack, j:" + j);
    }

    inc = h.createIncrementalEvaluation();
    inc.extend(partial, 2);
    partial.extend(2);
    assertEquals(0.07, h.h(partial, 1, inc), 1E-10);
  }
}
