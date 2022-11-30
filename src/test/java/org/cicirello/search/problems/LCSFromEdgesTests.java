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
import org.junit.jupiter.api.*;

/** JUnit test cases for the Largest Common Subgraph Problem from list of edges. */
public class LCSFromEdgesTests {

  private ArrayList<LargestCommonSubgraph.Edge> edges1;
  private ArrayList<LargestCommonSubgraph.Edge> edges2;
  private int[][] in1;
  private int[][] notIn1;
  private int[][] in2;
  private int[][] notIn2;

  @BeforeEach
  public void initEdgeLists() {
    edges1 = new ArrayList<LargestCommonSubgraph.Edge>();
    edges1.add(new LargestCommonSubgraph.Edge(0, 2));
    edges1.add(new LargestCommonSubgraph.Edge(0, 4));
    edges1.add(new LargestCommonSubgraph.Edge(1, 2));
    edges1.add(new LargestCommonSubgraph.Edge(3, 0));

    edges2 = new ArrayList<LargestCommonSubgraph.Edge>();
    edges2.add(new LargestCommonSubgraph.Edge(0, 3));
    edges2.add(new LargestCommonSubgraph.Edge(4, 3));
    edges2.add(new LargestCommonSubgraph.Edge(4, 1));
    edges2.add(new LargestCommonSubgraph.Edge(1, 3));
    edges2.add(new LargestCommonSubgraph.Edge(0, 1));

    int[][] in1 = {{0, 2}, {0, 4}, {1, 2}, {3, 0}, {2, 0}, {4, 0}, {2, 1}, {0, 3}};
    int[][] notIn1 = {
      {0, 1}, {1, 0}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 1}, {3, 2}, {3, 4}, {4, 1}, {4, 2}, {4, 3}
    };
    this.in1 = in1;
    this.notIn1 = notIn1;

    int[][] in2 = {{0, 3}, {3, 4}, {4, 1}, {1, 3}, {0, 1}, {3, 0}, {4, 3}, {1, 4}, {3, 1}, {1, 0}};
    int[][] notIn2 = {
      {0, 2}, {0, 4}, {1, 2}, {2, 3}, {2, 4}, {2, 0}, {2, 1}, {3, 2}, {4, 0}, {4, 2}
    };
    this.in2 = in2;
    this.notIn2 = notIn2;
  }

  @Test
  public void testEdge() {
    LargestCommonSubgraph.Edge edge = new LargestCommonSubgraph.Edge(5, 7);
    assertEquals(5, edge.getU());
    assertEquals(7, edge.getV());
  }

  @Test
  public void testSpecificGraphsCase() {
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
  }

  @Test
  public void testConstructingFromEdgesExceptions() {
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
}
