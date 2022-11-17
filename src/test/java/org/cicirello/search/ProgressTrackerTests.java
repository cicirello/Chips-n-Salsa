/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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

package org.cicirello.search;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for the ProgressTracker. */
public class ProgressTrackerTests {

  private static final double EPSILON = 1e-10;

  @Test
  public void testTracking() {
    ProgressTracker<TestCopyable> t = new ProgressTracker<TestCopyable>();
    assertNull(t.getSolution());
    SolutionCostPair<TestCopyable> pair = t.getSolutionCostPair();
    assertNull(pair.getSolution());
    TestCopyable[] s = {
      new TestCopyable(5), new TestCopyable(4), new TestCopyable(3),
      new TestCopyable(4), new TestCopyable(5), new TestCopyable(1)
    };
    long initial = t.elapsed();
    long previousElapsed = initial;
    assertEquals(0, initial);
    int[] expectedCosts = {5, 4, 3, 3, 3, 1};
    for (int i = 0; i < s.length; i++) {
      // testing with int costs
      t.update(expectedCosts[i], s[i], false);
      assertEquals(expectedCosts[i], t.getCost());
      assertEquals(expectedCosts[i], t.getCostDouble(), EPSILON);
      TestCopyable solution = t.getSolution();
      pair = t.getSolutionCostPair();
      assertEquals(expectedCosts[i], pair.getCost());
      assertEquals(expectedCosts[i], pair.getCostDouble(), EPSILON);
      assertEquals(solution, pair.getSolution());
      int j = i < 3 ? i : (i < 5 ? 2 : 5);
      assertTrue(solution != s[j]);
      assertEquals(s[j], solution);
      long nextElapsed = t.elapsed();
      assertTrue(nextElapsed >= previousElapsed);
      previousElapsed = nextElapsed;
    }
    t = new ProgressTracker<TestCopyable>();
    assertNull(t.getSolution());
    pair = t.getSolutionCostPair();
    assertNull(pair.getSolution());

    initial = t.elapsed();
    previousElapsed = initial;
    assertEquals(0, initial);
    double[] expectedCostsD = {5, 4, 3, 3, 3, 1};
    for (int i = 0; i < s.length; i++) {
      // testing with double costs
      t.update(expectedCostsD[i], s[i], false);
      assertEquals(expectedCostsD[i], t.getCostDouble(), EPSILON);
      TestCopyable solution = t.getSolution();
      pair = t.getSolutionCostPair();
      assertEquals(expectedCostsD[i], pair.getCostDouble(), EPSILON);
      assertEquals(solution, pair.getSolution());
      int j = i < 3 ? i : (i < 5 ? 2 : 5);
      assertTrue(solution != s[j]);
      assertEquals(s[j], solution);
      long nextElapsed = t.elapsed();
      assertTrue(nextElapsed >= previousElapsed);
      previousElapsed = nextElapsed;
    }
  }

  @Test
  public void testTrackingWithSolutionCostPairUpdates() {
    ProgressTracker<TestCopyable> t = new ProgressTracker<TestCopyable>();
    assertNull(t.getSolution());
    SolutionCostPair<TestCopyable> pair = t.getSolutionCostPair();
    assertNull(pair.getSolution());
    TestCopyable[] s = {
      new TestCopyable(5), new TestCopyable(4), new TestCopyable(3),
      new TestCopyable(4), new TestCopyable(5), new TestCopyable(1)
    };
    long initial = t.elapsed();
    long previousElapsed = initial;
    assertEquals(0, initial);
    int[] expectedCosts = {5, 4, 3, 3, 3, 1};
    for (int i = 0; i < s.length; i++) {
      // testing with int costs
      t.update(new SolutionCostPair<TestCopyable>(s[i], expectedCosts[i], false));
      assertEquals(expectedCosts[i], t.getCost());
      assertEquals(expectedCosts[i], t.getCostDouble(), EPSILON);
      TestCopyable solution = t.getSolution();
      pair = t.getSolutionCostPair();
      assertEquals(expectedCosts[i], pair.getCost());
      assertEquals(expectedCosts[i], pair.getCostDouble(), EPSILON);
      assertEquals(solution, pair.getSolution());
      int j = i < 3 ? i : (i < 5 ? 2 : 5);
      assertTrue(solution != s[j]);
      assertEquals(s[j], solution);
      long nextElapsed = t.elapsed();
      assertTrue(nextElapsed >= previousElapsed);
      previousElapsed = nextElapsed;
    }
    t = new ProgressTracker<TestCopyable>();
    assertNull(t.getSolution());
    pair = t.getSolutionCostPair();
    assertNull(pair.getSolution());

    initial = t.elapsed();
    previousElapsed = initial;
    assertEquals(0, initial);
    double[] expectedCostsD = {5, 4, 3, 3, 3, 1};
    for (int i = 0; i < s.length; i++) {
      // testing with double costs
      t.update(new SolutionCostPair<TestCopyable>(s[i], expectedCostsD[i], false));
      assertEquals(expectedCostsD[i], t.getCostDouble(), EPSILON);
      TestCopyable solution = t.getSolution();
      pair = t.getSolutionCostPair();
      assertEquals(expectedCostsD[i], pair.getCostDouble(), EPSILON);
      assertEquals(solution, pair.getSolution());
      int j = i < 3 ? i : (i < 5 ? 2 : 5);
      assertTrue(solution != s[j]);
      assertEquals(s[j], solution);
      long nextElapsed = t.elapsed();
      assertTrue(nextElapsed >= previousElapsed);
      previousElapsed = nextElapsed;
    }
  }

  @Test
  public void testFlags() {
    // test found best flag
    ProgressTracker<TestCopyable> t = new ProgressTracker<TestCopyable>();
    for (int i = 5; i >= 0; i--) {
      assertFalse(t.didFindBest());
      t.update(i, new TestCopyable(i), false);
    }
    for (int i = 0; i <= 5; i++) {
      assertFalse(t.didFindBest());
      t.update(i, new TestCopyable(i), false);
    }
    // deprecated: t.setFoundBest();
    t.update(-1, new TestCopyable(-1), true);
    assertTrue(t.didFindBest());

    // test stop flag
    t = new ProgressTracker<TestCopyable>();
    for (int i = 5; i >= 0; i--) {
      assertFalse(t.isStopped());
      t.update(i, new TestCopyable(i), false);
    }
    for (int i = 0; i <= 5; i++) {
      assertFalse(t.isStopped());
      t.update(i, new TestCopyable(i), false);
    }
    t.stop();
    assertTrue(t.isStopped());

    // test containsIntCost flag
    t = new ProgressTracker<TestCopyable>();
    assertFalse(t.containsIntCost());
    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        t.update(100 - i, new TestCopyable(i), false);
        assertTrue(t.containsIntCost());
      } else {
        t.update(100.0 - i, new TestCopyable(i), false);
        assertFalse(t.containsIntCost());
      }
    }
  }

  private static class TestCopyable implements Copyable<TestCopyable> {

    int a;

    public TestCopyable(int a) {
      this.a = a;
    }

    @Override
    public TestCopyable copy() {
      return new TestCopyable(a);
    }

    @Override
    public boolean equals(Object other) {
      return other != null && ((TestCopyable) other).a == a;
    }
  }
}
