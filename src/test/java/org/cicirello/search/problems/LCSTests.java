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

package org.cicirello.search.problems;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit test cases for the Largest Common Subgraph Problem. */
public class LCSTests {

  @Test
  public void testExceptions() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 0.5, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, -1E-10, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 1 + 1E-10, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 0.5, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, -1E-10, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 1 + 1E-10, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 0.5, true, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, -1E-10, true, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LargestCommonSubgraph(5, 1 + 1E-10, true, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 0.5, false, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, -1E-10, false, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LargestCommonSubgraph(5, 1 + 1E-10, false, 42));

    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 5, 0.5, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 0, 0.5, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 6, -1E-10, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 6, 0.5, -1E-10));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 6, 1 + 1E-10, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 6, 0.5, 1 + 1E-10));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(6, 5, -1E-10, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(6, 5, 0.5, -1E-10));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(6, 5, 1 + 1E-10, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(6, 5, 0.5, 1 + 1E-10));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 5, 0.5, 0.5, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 0, 0.5, 0.5, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 5, -1E-10, 0.5, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 5, 0.5, -1E-10, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LargestCommonSubgraph(5, 5, 1 + 1E-10, 0.5, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LargestCommonSubgraph(5, 5, 0.5, 1 + 1E-10, 42));
  }

  @Test
  public void testSameSizeRandom() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.5, false);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testSameSizeRandomSeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.5, false, 42);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testSameSizeAndDensity() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, n, 0.5, 0.5);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testSameSizeAndDensitySeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, n, 0.5, 0.5, 42);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testSameSizeDiffDensity() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, n, 0.3, 0.7);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());

    problem = new LargestCommonSubgraph(n, n, 0.7, 0.3);
    assertEquals(n, problem.size());
    p = new Permutation(n, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testSameSizeDiffDensitySeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, n, 0.3, 0.7, 42);
    assertEquals(n, problem.size());
    Permutation p = new Permutation(n, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());

    problem = new LargestCommonSubgraph(n, n, 0.7, 0.3, 42);
    assertEquals(n, problem.size());
    p = new Permutation(n, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testDiffSize() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 0.5, 0.5);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());

    problem = new LargestCommonSubgraph(n2, n1, 0.5, 0.5);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testDiffSizeSeed() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 0.5, 0.5, 42);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());

    problem = new LargestCommonSubgraph(n2, n1, 0.5, 0.5, 42);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertTrue(minCost >= problem.minCost());
    assertTrue(maxValue <= problem.maxValue());
  }

  @Test
  public void testDiffSizeFullyConnected() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 1.0, 1.0);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(n2 * (n2 - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n2 * (n2 - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);

    problem = new LargestCommonSubgraph(n2, n1, 1.0, 1.0);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(n2 * (n2 - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n2 * (n2 - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testDiffSizeFullyConnectedSeed() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 1.0, 1.0, 42);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(n2 * (n2 - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n2 * (n2 - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);

    problem = new LargestCommonSubgraph(n2, n1, 1.0, 1.0, 42);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(n2 * (n2 - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n2 * (n2 - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testDiffSizeNoEdges() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 0.0, 0.0);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);

    problem = new LargestCommonSubgraph(n2, n1, 0.0, 0.0);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testDiffSizeNoEdgesSeed() {
    int n1 = 7;
    int n2 = 4;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n1, n2, 0.0, 0.0, 42);
    assertEquals(n1, problem.size());
    Permutation p = new Permutation(n1, 0);
    int minCost = Integer.MAX_VALUE;
    int maxCost = Integer.MIN_VALUE;
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    Iterator<Permutation> iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);

    problem = new LargestCommonSubgraph(n2, n1, 0.0, 0.0, 42);
    assertEquals(n1, problem.size());
    p = new Permutation(n1, 0);
    minCost = Integer.MAX_VALUE;
    maxCost = Integer.MIN_VALUE;
    minValue = Integer.MAX_VALUE;
    maxValue = Integer.MIN_VALUE;
    iter = p.iterator();
    while (iter.hasNext()) {
      Permutation perm = iter.next();
      int cost = problem.cost(perm);
      int value = problem.value(perm);
      if (cost < minCost) minCost = cost;
      if (cost > maxCost) maxCost = cost;
      if (value < minValue) minValue = value;
      if (value > maxValue) maxValue = value;
      assertEquals(problem.maxValue() - value, cost);
    }
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }
}
