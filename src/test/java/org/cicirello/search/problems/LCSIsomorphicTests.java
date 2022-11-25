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

/** JUnit test cases for the Largest Common Subgraph Problem using isomorphic graphs. */
public class LCSIsomorphicTests {

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
}
