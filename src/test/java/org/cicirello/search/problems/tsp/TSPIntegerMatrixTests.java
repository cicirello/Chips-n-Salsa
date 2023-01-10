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

package org.cicirello.search.problems.tsp;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit tests for the TSP.IntegerMatrix class. */
public class TSPIntegerMatrixTests {

  @Test
  public void testIntCostMatrixFromArrays() {
    double[] x = {2.0, 2.0, 8.0};
    double[] y = {5.0, 9.0, 9.0};
    int[][] expected = {
      {0, 4, 7},
      {4, 0, 6},
      {7, 6, 0}
    };
    TSP.IntegerMatrix tsp = new TSP.IntegerMatrix(x, y);
    assertEquals(0, tsp.minCost());
    assertEquals(x.length, tsp.x.length);
    assertEquals(y.length, tsp.y.length);
    assertTrue(x != tsp.x);
    assertTrue(y != tsp.y);
    for (int i = 0; i < x.length; i++) {
      assertEquals(x[i], tsp.x[i], 0.0);
      assertEquals(y[i], tsp.y[i], 0.0);
      for (int j = 0; j < y.length; j++) {
        assertEquals(expected[i][j], tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected[i][j], tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    int[] permArray = {1, 2, 0};
    int expectedCost = 17;
    Permutation perm = new Permutation(permArray);
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(new double[2], new double[3]));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(new double[1], new double[1]));
  }

  @Test
  public void testIntCostMatrixFromArraysWithDistance() {
    double[] x = {2.0, 2.0, 8.0};
    double[] y = {5.0, 9.0, 9.0};
    int[][] expected = {
      {0, 4, 10},
      {4, 0, 6},
      {10, 6, 0}
    };
    TSP.IntegerMatrix tsp =
        new TSP.IntegerMatrix(x, y, (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2));
    assertEquals(0, tsp.minCost());
    assertEquals(x.length, tsp.x.length);
    assertEquals(y.length, tsp.y.length);
    assertTrue(x != tsp.x);
    assertTrue(y != tsp.y);
    for (int i = 0; i < x.length; i++) {
      assertEquals(x[i], tsp.x[i], 0.0);
      assertEquals(y[i], tsp.y[i], 0.0);
      for (int j = 0; j < y.length; j++) {
        assertEquals(expected[i][j], tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected[i][j], tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    int[] permArray = {1, 2, 0};
    int expectedCost = 20;
    Permutation perm = new Permutation(permArray);
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(new double[2], new double[3]));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(new double[1], new double[1]));
  }

  @Test
  public void testIntCostMatrix() {
    int W = 5;
    int N = 10;
    TSP.IntegerMatrix tsp = new TSP.IntegerMatrix(N, W);
    assertEquals(0, tsp.minCost());
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
    }
    W = 10;
    N = 3;
    tsp = new TSP.IntegerMatrix(N, W);
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    int[] permArray = {1, 2, 0};
    Permutation perm = new Permutation(permArray);
    int expectedCost = 0;
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
      int k = (i + 1) % N;
      expectedCost +=
          (int)
              Math.round(
                  Math.sqrt(
                      (tsp.x[i] - tsp.x[k]) * (tsp.x[i] - tsp.x[k])
                          + (tsp.y[i] - tsp.y[k]) * (tsp.y[i] - tsp.y[k])));
      for (int j = 0; j < N; j++) {
        int expected =
            (int)
                Math.round(
                    Math.sqrt(
                        (tsp.x[i] - tsp.x[j]) * (tsp.x[i] - tsp.x[j])
                            + (tsp.y[i] - tsp.y[j]) * (tsp.y[i] - tsp.y[j])));
        assertEquals(expected, tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected, tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(4, 6).cost(new Permutation(3)));
  }

  @Test
  public void testIntCostMatrixSeed() {
    int W = 5;
    int N = 10;
    TSP.IntegerMatrix tsp = new TSP.IntegerMatrix(N, W, 42);
    assertEquals(0, tsp.minCost());
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
    }
    W = 10;
    N = 3;
    tsp = new TSP.IntegerMatrix(N, W, 42);
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    int[] permArray = {1, 2, 0};
    Permutation perm = new Permutation(permArray);
    int expectedCost = 0;
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
      int k = (i + 1) % N;
      expectedCost +=
          (int)
              Math.round(
                  Math.sqrt(
                      (tsp.x[i] - tsp.x[k]) * (tsp.x[i] - tsp.x[k])
                          + (tsp.y[i] - tsp.y[k]) * (tsp.y[i] - tsp.y[k])));
      for (int j = 0; j < N; j++) {
        int expected =
            (int)
                Math.round(
                    Math.sqrt(
                        (tsp.x[i] - tsp.x[j]) * (tsp.x[i] - tsp.x[j])
                            + (tsp.y[i] - tsp.y[j]) * (tsp.y[i] - tsp.y[j])));
        assertEquals(expected, tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected, tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TSP.IntegerMatrix(4, 6, 42).cost(new Permutation(5)));
  }

  @Test
  public void testIntCostMatrixWithDistance() {
    int W = 5;
    int N = 10;
    TSP.IntegerMatrix tsp =
        new TSP.IntegerMatrix(N, W, (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2));
    assertEquals(0, tsp.minCost());
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
    }
    W = 10;
    N = 3;
    tsp = new TSP.IntegerMatrix(N, W, (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2));
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    int[] permArray = {1, 2, 0};
    Permutation perm = new Permutation(permArray);
    int expectedCost = 0;
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
      int k = (i + 1) % N;
      expectedCost +=
          (int) Math.round(Math.abs(tsp.x[i] - tsp.x[k]) + Math.abs(tsp.y[i] - tsp.y[k]));
      for (int j = 0; j < N; j++) {
        int expected =
            (int) Math.round(Math.abs(tsp.x[i] - tsp.x[j]) + Math.abs(tsp.y[i] - tsp.y[j]));
        assertEquals(expected, tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected, tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));
  }

  @Test
  public void testIntCostMatrixSeedWithDistance() {
    int W = 5;
    int N = 10;
    TSP.IntegerMatrix tsp =
        new TSP.IntegerMatrix(N, W, (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2), 42);
    assertEquals(0, tsp.minCost());
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
    }
    W = 10;
    N = 3;
    tsp =
        new TSP.IntegerMatrix(N, W, (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2), 42);
    assertEquals(N, tsp.x.length);
    assertEquals(N, tsp.y.length);
    int[] permArray = {1, 2, 0};
    Permutation perm = new Permutation(permArray);
    int expectedCost = 0;
    for (int i = 0; i < N; i++) {
      assertTrue(tsp.x[i] < W && tsp.x[i] >= 0.0);
      assertTrue(tsp.y[i] < W && tsp.y[i] >= 0.0);
      int k = (i + 1) % N;
      expectedCost +=
          (int) Math.round(Math.abs(tsp.x[i] - tsp.x[k]) + Math.abs(tsp.y[i] - tsp.y[k]));
      for (int j = 0; j < N; j++) {
        int expected =
            (int) Math.round(Math.abs(tsp.x[i] - tsp.x[j]) + Math.abs(tsp.y[i] - tsp.y[j]));
        assertEquals(expected, tsp.d.distanceAsInt(tsp.x[i], tsp.y[i], tsp.x[j], tsp.y[j]));
        assertEquals(expected, tsp.edgeCostForHeuristics(i, j), 1E-10);
      }
    }
    assertEquals(expectedCost, tsp.cost(perm));
    assertEquals(expectedCost, tsp.value(perm));
  }
}
