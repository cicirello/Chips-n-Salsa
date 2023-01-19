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
import org.junit.jupiter.api.*;

/** JUnit tests for CommoDuedateScheduling. */
public class CommoDuedateSchedulingTests {

  @Test
  public void testConstructorExceptions() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new CommonDuedateScheduling(-1, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new CommonDuedateScheduling(1, -0.00001));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new CommonDuedateScheduling(1, 1.00001));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new CommonDuedateScheduling(-1, 0.5, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new CommonDuedateScheduling(1, -0.00001, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new CommonDuedateScheduling(1, 1.00001, 42));
  }

  @Test
  public void testCorrectNumJobs() {
    double[] h = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int n = 1; n <= 5; n++) {
      for (int i = 0; i < h.length; i++) {
        CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
        assertEquals(n, s.numberOfJobs());
        assertTrue(s.hasDueDates());
        assertTrue(s.hasWeights());
        assertTrue(s.hasEarlyWeights());
        assertFalse(s.hasSetupTimes());
        assertFalse(s.hasReleaseDates());
        CommonDuedateScheduling s2 = new CommonDuedateScheduling(n, h[i]);
        assertEquals(n, s2.numberOfJobs());
        assertTrue(s2.hasDueDates());
        assertTrue(s2.hasWeights());
        assertTrue(s2.hasEarlyWeights());
        assertFalse(s2.hasSetupTimes());
        assertFalse(s2.hasReleaseDates());
      }
    }
  }

  @Test
  public void testConsistencyWithParameters() {
    double[] h = {0.0, 0.25, 0.5, 0.75, 1.0};
    int[] m = {0, 1, 1, 3, 1};
    int[] d = {1, 4, 2, 4, 1};
    for (int n = 1; n <= 10; n++) {
      for (int i = 0; i < h.length; i++) {
        CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
        int totalP = 0;
        for (int x = 0; x < n; x++) {
          assertTrue(
              s.getProcessingTime(x) >= CommonDuedateScheduling.MIN_PROCESS_TIME
                  && s.getProcessingTime(x) <= CommonDuedateScheduling.MAX_PROCESS_TIME);
          assertTrue(
              s.getWeight(x) >= CommonDuedateScheduling.MIN_TARDINESS_WEIGHT
                  && s.getWeight(x) <= CommonDuedateScheduling.MAX_TARDINESS_WEIGHT);
          assertTrue(
              s.getEarlyWeight(x) >= CommonDuedateScheduling.MIN_EARLINESS_WEIGHT
                  && s.getEarlyWeight(x) <= CommonDuedateScheduling.MAX_EARLINESS_WEIGHT);
          totalP += s.getProcessingTime(x);
        }
        int expectedDuedate = totalP * m[i] / d[i];
        for (int x = 0; x < n; x++) {
          assertEquals(expectedDuedate, s.getDueDate(x));
        }
      }
    }
  }

  @Test
  public void testCompletionTimeCalculationWithH0() {
    double h = 0.0;
    for (int n = 1; n <= 10; n++) {
      final CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
      int[] perm1 = new int[n];
      int[] perm2 = new int[n];
      for (int i = 0; i < n; i++) {
        perm1[i] = i;
        perm2[n - 1 - i] = i;
      }
      Permutation p1 = new Permutation(perm1);
      Permutation p2 = new Permutation(perm2);
      int[] c1 = s.getCompletionTimes(p1);
      int expected = 0;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p1.get(x));
        assertEquals(expected, c1[p1.get(x)], "forward");
      }
      int[] c2 = s.getCompletionTimes(p2);
      expected = 0;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p2.get(x));
        assertEquals(expected, c2[p2.get(x)], "backward");
      }
      final int nPlus = n + 1;
      IllegalArgumentException thrown =
          assertThrows(
              IllegalArgumentException.class, () -> s.getCompletionTimes(new Permutation(nPlus)));
    }
  }

  @Test
  public void testCompletionTimeCalculationWithH1() {
    double h = 1.0;
    for (int n = 1; n <= 10; n++) {
      CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
      int[] perm1 = new int[n];
      int[] perm2 = new int[n];
      for (int i = 0; i < n; i++) {
        perm1[i] = i;
        perm2[n - 1 - i] = i;
      }
      Permutation p1 = new Permutation(perm1);
      Permutation p2 = new Permutation(perm2);
      int[] c1 = s.getCompletionTimes(p1);
      int duedate = s.getDueDate(0);
      int earlySum = 0;
      int tardySum = 0;
      int onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c1[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      String message = "Forward: earlySum,tardySum=" + earlySum + "," + tardySum;
      int notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      assertTrue(onTimeJob >= 0);
      if (onTimeJob >= 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertEquals(0, c1[p1.get(0)] - s.getProcessingTime(p1.get(0)));
        assertTrue(earlySum <= tardySum);
      }
      int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
      int expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p1.get(x));
        assertEquals(expected, c1[p1.get(x)], "forward");
      }
      int[] c2 = s.getCompletionTimes(p2);
      earlySum = 0;
      tardySum = 0;
      onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c2[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      message = "Backward: earlySum,tardySum,n=" + earlySum + "," + tardySum + "," + n;
      notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      assertTrue(onTimeJob >= 0);
      if (onTimeJob >= 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertEquals(0, c2[p2.get(0)] - s.getProcessingTime(p2.get(0)));
        assertTrue(earlySum <= tardySum);
      }
      delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
      expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p2.get(x));
        assertEquals(expected, c2[p2.get(x)], "backward");
      }
    }
  }

  @Test
  public void testCompletionTimeCalculationWithH05() {
    double h = 0.5;
    for (int n = 1; n <= 10; n++) {
      CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
      int[] perm1 = new int[n];
      int[] perm2 = new int[n];
      for (int i = 0; i < n; i++) {
        perm1[i] = i;
        perm2[n - 1 - i] = i;
      }
      Permutation p1 = new Permutation(perm1);
      Permutation p2 = new Permutation(perm2);
      int[] c1 = s.getCompletionTimes(p1);
      int duedate = s.getDueDate(0);
      int earlySum = 0;
      int tardySum = 0;
      int onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c1[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      String message = "Forward: earlySum,tardySum=" + earlySum + "," + tardySum;
      int notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      int expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p1.get(x));
        assertEquals(expected, c1[p1.get(x)], "forward");
      }
      int[] c2 = s.getCompletionTimes(p2);
      earlySum = 0;
      tardySum = 0;
      onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c2[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      message = "Backward: earlySum,tardySum,n=" + earlySum + "," + tardySum + "," + n;
      notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p2.get(x));
        assertEquals(expected, c2[p2.get(x)], "backward");
      }
    }
  }

  @Test
  public void testCompletionTimeCalculationWithH025() {
    double h = 0.25;
    for (int n = 1; n <= 10; n++) {
      CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
      int[] perm1 = new int[n];
      int[] perm2 = new int[n];
      for (int i = 0; i < n; i++) {
        perm1[i] = i;
        perm2[n - 1 - i] = i;
      }
      Permutation p1 = new Permutation(perm1);
      Permutation p2 = new Permutation(perm2);
      int[] c1 = s.getCompletionTimes(p1);
      int duedate = s.getDueDate(0);
      int earlySum = 0;
      int tardySum = 0;
      int onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c1[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      String message = "Forward: earlySum,tardySum=" + earlySum + "," + tardySum;
      int notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      int expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p1.get(x));
        assertEquals(expected, c1[p1.get(x)], "forward");
      }
      int[] c2 = s.getCompletionTimes(p2);
      earlySum = 0;
      tardySum = 0;
      onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c2[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      message = "Backward: earlySum,tardySum,n=" + earlySum + "," + tardySum + "," + n;
      notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p2.get(x));
        assertEquals(expected, c2[p2.get(x)], "backward");
      }
    }
  }

  @Test
  public void testCompletionTimeCalculationWithH075() {
    double h = 0.75;
    for (int n = 1; n <= 10; n++) {
      CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
      int[] perm1 = new int[n];
      int[] perm2 = new int[n];
      for (int i = 0; i < n; i++) {
        perm1[i] = i;
        perm2[n - 1 - i] = i;
      }
      Permutation p1 = new Permutation(perm1);
      Permutation p2 = new Permutation(perm2);
      int[] c1 = s.getCompletionTimes(p1);
      int duedate = s.getDueDate(0);
      int earlySum = 0;
      int tardySum = 0;
      int onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c1[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      String message = "Forward: earlySum,tardySum=" + earlySum + "," + tardySum;
      int notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      int expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p1.get(x));
        assertEquals(expected, c1[p1.get(x)], "forward");
      }
      int[] c2 = s.getCompletionTimes(p2);
      earlySum = 0;
      tardySum = 0;
      onTimeJob = -1;
      for (int x = 0; x < n; x++) {
        if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
        else if (c2[x] > duedate) tardySum += s.getWeight(x);
        else onTimeJob = x;
      }
      message = "Backward: earlySum,tardySum,n=" + earlySum + "," + tardySum + "," + n;
      notEarlySumOfTardy = tardySum;
      if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
      assertTrue(earlySum <= notEarlySumOfTardy, message);
      delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
      if (onTimeJob >= 0 && delay > 0) {
        assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
      } else {
        assertTrue(delay == 0 && earlySum <= tardySum, "case with no ontime jobs");
      }
      expected = delay;
      for (int x = 0; x < n; x++) {
        expected += s.getProcessingTime(p2.get(x));
        assertEquals(expected, c2[p2.get(x)], "backward");
      }
    }
  }
}
