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

import java.util.ArrayList;
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
  public void testPetersenGraphInstance() {
    for (int n = 1; n <= 7; n++) {
      for (int k = 0; k < 0.5 * n; k++) {
        LargestCommonSubgraph lcs =
            LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(n, k);
        assertEquals(2 * n, lcs.size());
        assertEquals(3 * n, lcs.maxValue());
        for (int i = 0; i < n; i++) {
          assertTrue(lcs.hasEdge1(i, (i + 1) % n));
          assertTrue(lcs.hasEdge1(n + i, n + ((i + k) % n)));
          assertTrue(lcs.hasEdge1(i, n + i));
        }
        lcs = LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(n, k, 42);
        assertEquals(2 * n, lcs.size());
        assertEquals(3 * n, lcs.maxValue());
        for (int i = 0; i < n; i++) {
          assertTrue(lcs.hasEdge1(i, (i + 1) % n));
          assertTrue(lcs.hasEdge1(n + i, n + ((i + k) % n)));
          assertTrue(lcs.hasEdge1(i, n + i));
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(1, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(2, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(3, 2));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(4, 2));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(5, 3));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(6, 3));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(1, 1, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(2, 1, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(3, 2, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(4, 2, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(5, 3, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(6, 3, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(5, -1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(5, -1, 42));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(0, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> LargestCommonSubgraph.createInstanceGeneralizedPetersenGraph(0, 0, 42));
  }

  @Test
  public void testSpecificGraphsCase() {
    LargestCommonSubgraph.Edge edge = new LargestCommonSubgraph.Edge(5, 7);
    assertEquals(5, edge.getU());
    assertEquals(7, edge.getV());

    ArrayList<LargestCommonSubgraph.Edge> edges1 = new ArrayList<LargestCommonSubgraph.Edge>();
    edges1.add(new LargestCommonSubgraph.Edge(0, 2));
    edges1.add(new LargestCommonSubgraph.Edge(0, 4));
    edges1.add(new LargestCommonSubgraph.Edge(1, 2));
    edges1.add(new LargestCommonSubgraph.Edge(3, 0));
    int[][] in1 = {{0, 2}, {0, 4}, {1, 2}, {3, 0}, {2, 0}, {4, 0}, {2, 1}, {0, 3}};
    int[][] notIn1 = {
      {0, 1}, {1, 0}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 1}, {3, 2}, {3, 4}, {4, 1}, {4, 2}, {4, 3}
    };
    ArrayList<LargestCommonSubgraph.Edge> edges2 = new ArrayList<LargestCommonSubgraph.Edge>();
    edges2.add(new LargestCommonSubgraph.Edge(0, 3));
    edges2.add(new LargestCommonSubgraph.Edge(4, 3));
    edges2.add(new LargestCommonSubgraph.Edge(4, 1));
    edges2.add(new LargestCommonSubgraph.Edge(1, 3));
    edges2.add(new LargestCommonSubgraph.Edge(0, 1));
    int[][] in2 = {{0, 3}, {3, 4}, {4, 1}, {1, 3}, {0, 1}, {3, 0}, {4, 3}, {1, 4}, {3, 1}, {1, 0}};
    int[][] notIn2 = {
      {0, 2}, {0, 4}, {1, 2}, {2, 3}, {2, 4}, {2, 0}, {2, 1}, {3, 2}, {4, 0}, {4, 2}
    };

    LargestCommonSubgraph lcs = new LargestCommonSubgraph(5, 5, edges1, edges2);
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(5, lcs.size());

    lcs = new LargestCommonSubgraph(5, 5, edges2, edges1);
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(5, lcs.size());

    lcs = new LargestCommonSubgraph(5, 6, edges1, edges2);
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(6, lcs.size());

    lcs = new LargestCommonSubgraph(5, 6, edges2, edges1);
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(6, lcs.size());

    lcs = new LargestCommonSubgraph(6, 5, edges1, edges2);
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(6, lcs.size());

    lcs = new LargestCommonSubgraph(6, 5, edges2, edges1);
    for (int[] e : in1) {
      assertTrue(lcs.hasEdge1(e[0], e[1]));
      assertTrue(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : in2) {
      assertTrue(lcs.hasEdge2(e[0], e[1]));
      assertTrue(lcs.hasEdge2(e[1], e[0]));
    }
    for (int[] e : notIn1) {
      assertFalse(lcs.hasEdge1(e[0], e[1]));
      assertFalse(lcs.hasEdge1(e[1], e[0]));
    }
    for (int[] e : notIn2) {
      assertFalse(lcs.hasEdge2(e[0], e[1]));
      assertFalse(lcs.hasEdge2(e[1], e[0]));
    }
    assertEquals(4, lcs.maxValue());
    assertEquals(6, lcs.size());

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(0, 5, edges2, edges1));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 0, edges2, edges1));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(4, 5, edges1, edges2));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(4, 5, edges2, edges1));
    edges2.add(new LargestCommonSubgraph.Edge(5, 2));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 5, edges1, edges2));
    edges2.set(edges2.size() - 1, new LargestCommonSubgraph.Edge(2, 5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LargestCommonSubgraph(5, 5, edges1, edges2));
  }

  @Test
  public void testIsomorphicCase() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.5, true);
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
    assertEquals(minCost, problem.minCost());
    assertEquals(maxValue, problem.maxValue());
  }

  @Test
  public void testIsomorphicCaseSeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.5, true, 42);
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
    assertEquals(minCost, problem.minCost());
    assertEquals(maxValue, problem.maxValue());
  }

  @Test
  public void testIsomorphicCaseFullyConnected() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 1.0, true);
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
    assertEquals(0, problem.minCost());
    assertEquals(n * (n - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n * (n - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testIsomorphicCaseFullyConnectedSeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 1.0, true, 42);
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
    assertEquals(0, problem.minCost());
    assertEquals(n * (n - 1) / 2, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(n * (n - 1) / 2, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testIsomorphicCaseNoEdges() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.0, true);
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
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
  }

  @Test
  public void testIsomorphicCaseNoEdgesSeed() {
    int n = 7;
    LargestCommonSubgraph problem = new LargestCommonSubgraph(n, 0.0, true, 42);
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
    assertEquals(0, problem.minCost());
    assertEquals(0, problem.maxValue());
    assertEquals(0, minCost);
    assertEquals(0, maxValue);
    assertEquals(minCost, maxCost);
    assertEquals(minValue, maxValue);
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
