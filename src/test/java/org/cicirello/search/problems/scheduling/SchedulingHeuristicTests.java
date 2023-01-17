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
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.PartialPermutation;
import org.junit.jupiter.api.*;

/** JUnit tests for the abstract base class SchedulingHeuristic. */
public class SchedulingHeuristicTests extends SchedulingHeuristicValidation {

  @Test
  public void testBaseClassMethods() {
    int[] duedates = {3, 0, 1, 7};
    FakeProblemDuedates problem = new FakeProblemDuedates(duedates);
    EarliestDueDate h = new EarliestDueDate(problem);
    assertEquals(problem, h.getProblem());
    assertEquals(4, h.completeLength());
    for (int i = 1; i < 4; i++) {
      Partial<Permutation> partial = h.createPartial(i);
      assertEquals(0, partial.size());
      assertFalse(partial.isComplete());
      assertEquals(i, partial.numExtensions());
    }
  }

  @Test
  public void testSchedulingHeuristicIncEvalExtend() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {3, 5, 6, 10, 15};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    assertEquals(0, inc.currentTime());
    for (int i = 0; i < p.length; i++) {
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
      assertEquals(e[i], inc.currentTime());
    }
  }

  @Test
  public void testSchedulingHeuristicIncEvalExtendWithSetups() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {10, 13, 18, 29, 44};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, 7);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    assertEquals(0, inc.currentTime());
    for (int i = 0; i < p.length; i++) {
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
      assertEquals(e[i], inc.currentTime());
    }
  }

  @Test
  public void testSchedulingHeuristicSlack() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {7, 5, 4, 0, -5};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    for (int i = 0; i < p.length; i++) {
      assertEquals(e[i], inc.slack(i, partial));
      assertEquals(e[i], inc.slack(i));
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
    }
  }

  @Test
  public void testSchedulingHeuristicSlackWithSetups() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {19, 16, 11, 0, -15};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 29, 7);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    for (int i = 0; i < p.length; i++) {
      assertEquals(e[i], inc.slack(i, partial));
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
    }
  }

  @Test
  public void testSchedulingHeuristicSlackPlus() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {7, 5, 4, 0, 0};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    for (int i = 0; i < p.length; i++) {
      assertEquals(e[i], inc.slackPlus(i, partial));
      assertEquals(e[i], inc.slackPlus(i));
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
    }
  }

  @Test
  public void testSchedulingHeuristicSlackPlusWithSetups() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] e = {19, 16, 11, 0, 0};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 29, 7);
    WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
    PartialPermutation partial = new PartialPermutation(e.length);
    SchedulingHeuristic.IncrementalTimeCalculator inc =
        (SchedulingHeuristic.IncrementalTimeCalculator) h.createIncrementalEvaluation();
    for (int i = 0; i < p.length; i++) {
      assertEquals(e[i], inc.slackPlus(i, partial));
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
    }
  }

  @Test
  public void testSchedulingHeuristicTotalAveragePTime() {
    int[] w = {1, 1, 1, 1, 1};
    int[] p = {3, 2, 1, 4, 5};
    int[] expectedTotal = {15, 12, 10, 9, 5};
    FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
    Montagne h = new Montagne(problem);
    PartialPermutation partial = new PartialPermutation(expectedTotal.length);
    SchedulingHeuristic.IncrementalAverageProcessingCalculator inc =
        (SchedulingHeuristic.IncrementalAverageProcessingCalculator)
            h.createIncrementalEvaluation();
    for (int i = 0; i < p.length; i++) {
      assertEquals(expectedTotal[i], inc.totalProcessingTime());
      assertEquals(expectedTotal[i] / (p.length - i - 0.0), inc.averageProcessingTime(), 1E-10);
      inc.extend(partial, i);
      if (i < partial.numExtensions()) partial.extend(i);
      else partial.extend(partial.numExtensions() - 1);
    }
    assertEquals(0, inc.totalProcessingTime());
  }
}
