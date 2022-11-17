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

package org.cicirello.search.operators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/**
 * JUnit tests for the various classes that implement hybrid crossover operators (i.e., that combine
 * multiple crossover operators).
 */
public class HybridCrossoverTests {

  // A few of the test cases test goodness of fit using Chi-square test.
  // These statistical tests are computed at the 95% level, which means
  // 5% of the time on average they should be expected to fail.
  // There are several such tests in this set of test cases, so there is
  // a reasonably high chance that at least one will fail.
  // This part of the test cases can be disabled with this constant.
  // If you make any code changes that can potentially affect this, then
  // reenable by setting true.  You can then set to false after the test cases pass.
  // Just note that if enabled and the chi-square tests fail, rerun the test cases.
  private static final boolean DISABLE_STATISTICAL_TESTS = true;

  @Test
  public void testExceptions() {
    final ArrayList<TestCrossover> c = new ArrayList<TestCrossover>();
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new HybridCrossover<TestObject>(c));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedHybridCrossover<TestObject>(c, new int[0]));
    c.add(new TestCrossover());
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedHybridCrossover<TestObject>(c, new int[] {1, 2}));
    c.add(new TestCrossover());
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedHybridCrossover<TestObject>(c, new int[] {0, 2}));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedHybridCrossover<TestObject>(c, new int[] {1, 0}));
  }

  @Test
  public void testHybridCrossover() {
    int n = 6000;
    // used for chi-square tests: tested at 95% level
    double[] threshold = {0, 3.841, 5.991};
    for (int k = 1; k <= 3; k++) {
      ArrayList<TestCrossover> crosses = new ArrayList<TestCrossover>();
      for (int i = 0; i < k; i++) {
        crosses.add(new TestCrossover());
      }
      HybridCrossover<TestObject> c = new HybridCrossover<TestObject>(crosses);
      TestObject t1 = new TestObject("a");
      TestObject t2 = new TestObject("b");
      for (int i = 0; i < n; i++) {
        c.cross(t1, t2);
      }
      if (k == 1) {
        assertEquals(n, crosses.get(0).crossCount);
      } else {
        // Chi-square goodness-of-fit tests on the distribution
        // of cross calls across the set of crossover ops.
        int x = 0;
        int total = 0;
        for (int i = 0; i < k; i++) {
          int o = crosses.get(i).crossCount;
          assertTrue(o > 0 && o <= n);
          x += o * o * k;
          total += o;
        }
        assertEquals(n, total);
        double v = 1.0 * x / n - n;
        if (!DISABLE_STATISTICAL_TESTS) {
          // Chi-square at 95% level
          assertTrue(
              v <= threshold[k - 1],
              "chi-square test failed, rerun tests since expected to fail 5% of the time");
        }
      }
      HybridCrossover<TestObject> s = c.split();
      for (int i = 0; i < 10; i++) {
        s.cross(t1, t2);
      }
      int total = 0;
      for (int i = 0; i < k; i++) {
        int o = crosses.get(i).crossCount;
        total += o;
      }
      // Verify split didn't keep references to pre-split
      // component crosses.
      assertEquals(n, total);
    }
  }

  @Test
  public void testWeightedHybridCrossoverEqualWeights() {
    int n = 6000;
    // used for chi-square tests: tested at 95% level
    double[] threshold = {0, 3.841, 5.991};
    for (int w = 1; w <= 2; w++) {
      for (int k = 1; k <= 3; k++) {
        int[] weights = new int[k];
        ArrayList<TestCrossover> crosses = new ArrayList<TestCrossover>();
        for (int i = 0; i < k; i++) {
          crosses.add(new TestCrossover());
          weights[i] = w;
        }
        WeightedHybridCrossover<TestObject> m =
            new WeightedHybridCrossover<TestObject>(crosses, weights);
        TestObject t1 = new TestObject("a");
        TestObject t2 = new TestObject("b");
        for (int i = 0; i < n; i++) {
          m.cross(t1, t2);
        }
        if (k == 1) {
          assertEquals(n, crosses.get(0).crossCount);
        } else {
          // Chi-square goodness-of-fit tests on the distribution
          // of cross calls across the set of crossover ops.
          int x = 0;
          int total = 0;
          for (int i = 0; i < k; i++) {
            int o = crosses.get(i).crossCount;
            assertTrue(o > 0 && o <= n);
            x += o * o * k;
            total += o;
          }
          assertEquals(n, total);
          double v = 1.0 * x / n - n;
          if (!DISABLE_STATISTICAL_TESTS) {
            // Chi-square at 95% level
            assertTrue(
                v <= threshold[k - 1],
                "chi-square test failed, rerun tests since expected to fail 5% of the time");
          }
        }
        WeightedHybridCrossover<TestObject> s = m.split();
        for (int i = 0; i < 10; i++) {
          s.cross(t1, t2);
        }
        int total = 0;
        for (int i = 0; i < k; i++) {
          int o = crosses.get(i).crossCount;
          total += o;
        }
        // Verify split didn't keep references to pre-split
        // component crosses.
        assertEquals(n, total);
      }
    }
  }

  @Test
  public void testWeightedHybridCrossoverUnequalWeights() {
    int n = 6000;
    // used for chi-square tests: tested at 95% level
    double[] threshold = {0, 3.841, 5.991};
    int k = 3;
    int[] weights = {1, 2, 1};
    ArrayList<TestCrossover> crosses = new ArrayList<TestCrossover>();
    for (int i = 0; i < k; i++) {
      crosses.add(new TestCrossover());
    }
    WeightedHybridCrossover<TestObject> m =
        new WeightedHybridCrossover<TestObject>(crosses, weights);
    TestObject t1 = new TestObject("a");
    TestObject t2 = new TestObject("b");
    for (int i = 0; i < n; i++) {
      m.cross(t1, t2);
    }
    // Chi-square goodness-of-fit tests on the distribution
    // of cross calls across the set of crossover ops.
    int x = 0;
    int total = 0;
    for (int i = 0; i < k; i++) {
      int o = crosses.get(i).crossCount;
      assertTrue(o > 0 && o <= n);
      int mult = (i == 1) ? 2 : 4;
      x += o * o * mult;
      total += o;
    }
    assertEquals(n, total);
    double v = 1.0 * x / n - n;
    if (!DISABLE_STATISTICAL_TESTS) {
      // Chi-square at 95% level
      assertTrue(
          v <= threshold[k - 1],
          "chi-square test failed, rerun tests since expected to fail 5% of the time");
    }
    WeightedHybridCrossover<TestObject> s = m.split();
    for (int i = 0; i < 10; i++) {
      s.cross(t1, t2);
    }
    total = 0;
    for (int i = 0; i < k; i++) {
      int o = crosses.get(i).crossCount;
      total += o;
    }
    // Verify split didn't keep references to pre-split
    // component crosses.
    assertEquals(n, total);
  }

  private static class TestCrossover implements CrossoverOperator<TestObject> {

    private int id;
    int crossCount;

    private static int nextID = 0;
    private static int lastCalled = -1;

    public TestCrossover() {
      id = nextID;
      nextID++;
      crossCount = 0;
    }

    public void cross(TestObject t1, TestObject t2) {
      assertEquals("a", t1.id);
      assertEquals("b", t2.id);
      crossCount++;
      lastCalled = id;
    }

    public TestCrossover split() {
      return new TestCrossover();
    }
  }

  private static class TestObject implements Copyable<TestObject> {
    private String id;

    public TestObject(String id) {
      this.id = id;
    }

    public TestObject copy() {
      return new TestObject(id);
    }
  }
}
