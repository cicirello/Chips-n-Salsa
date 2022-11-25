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

import org.junit.jupiter.api.*;

/** JUnit test cases for the Largest Common Subgraph Problem using Generalized Petersen Graphs. */
public class LCSPetersenTests {

  @Test
  public void testCreateInstanceGeneralizedPetersenGraph() {
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
  }

  @Test
  public void testCreateInstanceGeneralizedPetersenGraphExceptions() {
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
}
