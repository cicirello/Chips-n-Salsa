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

import org.cicirello.search.representations.BitVector;
import org.junit.jupiter.api.*;

/** JUnit test cases for the Plateaus problem. */
public class PlateausTests {

  private final double EPSILON = 1e-10;

  @Test
  public void testPlateausAllOnes() {
    Plateaus problem = new Plateaus();
    assertEquals(0.0, problem.minCost(), 0.0);
    double zero = 0;
    assertTrue(problem.isMinCost(zero));
    assertFalse(problem.isMinCost(zero - 1));
    assertFalse(problem.isMinCost(zero + 1));
    // all ones
    for (int n = 0; n <= 8; n++) {
      BitVector v = new BitVector(n);
      v.not();
      assertEquals(0.0, problem.cost(v), 0.0);
      assertEquals(10.0 * n, problem.value(v), 0.0);
    }
    for (int n = 124; n <= 132; n++) {
      BitVector v = new BitVector(n);
      v.not();
      assertEquals(0.0, problem.cost(v), 0.0);
      assertEquals(10.0 * n, problem.value(v), 0.0);
    }
  }

  @Test
  public void testPlateausAllZeros() {
    Plateaus problem = new Plateaus();
    assertEquals(0.0, problem.minCost(), 0.0);
    double zero = 0;
    assertTrue(problem.isMinCost(zero));
    assertFalse(problem.isMinCost(zero - 1));
    assertFalse(problem.isMinCost(zero + 1));
    // problem not well defined for n < 4, so expected behavior too
    // ill-defined to test for n < 4
    for (int n = 4; n <= 8; n++) {
      BitVector v = new BitVector(n);
      assertEquals(0.0, problem.value(v), 0.0, "n:" + n);
      assertEquals(10.0 * n, problem.cost(v), 0.0, "n:" + n);
    }
    for (int n = 124; n <= 132; n++) {
      BitVector v = new BitVector(n);
      assertEquals(0.0, problem.value(v), 0.0, "n:" + n);
      assertEquals(10.0 * n, problem.cost(v), 0.0, "n:" + n);
    }
  }

  @Test
  public void testPlateausValue0() {
    Plateaus problem = new Plateaus();
    int[] length = {4, 8, 8, 8, 32, 32, 128, 128, 132, 132};
    int[][] cases = {
      {0},
      {0},
      {0xAA},
      {0x55},
      {0xFEFEFEFE},
      {0x7F7F7F7F},
      {0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE},
      {0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFB, 0x7},
      {0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFB, 0xFFFFFFF7, 0xF}
    };
    for (int i = 0; i < cases.length; i++) {
      int n = length[i];
      BitVector v = new BitVector(n, cases[i]);
      assertEquals(0.0, problem.value(v), 0.0, "i:" + i + " n:" + n);
      assertEquals(10.0 * n, problem.cost(v), 0.0, "i:" + i + " n:" + n);
    }
  }

  @Test
  public void testPlateausValue25() {
    Plateaus problem = new Plateaus();
    int[] length = {
      4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 32, 32, 32, 32, 32, 32, 32, 32, 128, 128, 128, 128, 128,
      128, 128, 128, 132, 132, 132, 132, 132, 132, 132, 132
    };
    int[][] cases = {
      {1},
      {2},
      {4},
      {8},
      {0xAB},
      {0xBA},
      {0xAE},
      {0xEA},
      {0xD5},
      {0x5D},
      {0x57},
      {0x75},
      {0xFFFEFEFE},
      {0xFEFFFEFE},
      {0xFEFEFFFE},
      {0xFEFEFEFF},
      {0xFF7F7F7F},
      {0x7FFF7F7F},
      {0x7F7FFF7F},
      {0x7F7F7FFF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE},
      {0xFFFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFE},
      {0x7FFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFE},
      {0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFF},
      {0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFB, 0x7},
      {0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFB, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFB, 0x7},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFB, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFF, 0x7},
      {0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFF, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFB, 0xF},
      {0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFB, 0xFFFFFFFF, 0xF}
    };
    for (int i = 0; i < cases.length; i++) {
      int n = length[i];
      BitVector v = new BitVector(n, cases[i]);
      assertEquals(2.5 * n, problem.value(v), 0.0, "i:" + i + " n:" + n);
      assertEquals(7.5 * n, problem.cost(v), 0.0, "i:" + i + " n:" + n);
    }
  }

  @Test
  public void testPlateausValue5() {
    Plateaus problem = new Plateaus();
    int[] length = {
      4, 4, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 32, 32, 32, 32, 32, 32, 32, 32, 128, 128, 128, 128,
      128, 128, 128, 128, 132, 132, 132, 132, 132, 132, 132, 132
    };
    int[][] cases = {
      {3},
      {6},
      {10},
      {12},
      {9},
      {5},
      {0xBB},
      {0xFA},
      {0xAF},
      {0xEB},
      {0xD7},
      {0x7D},
      {0x5F},
      {0xF5},
      {0xFFFFFEFE},
      {0xFEFFFEFF},
      {0xFEFEFFFF},
      {0xFFFEFEFF},
      {0xFF7F7FFF},
      {0xFFFF7F7F},
      {0x7FFFFF7F},
      {0x7F7FFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFE},
      {0xFFFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFE},
      {0x7FFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF},
      {0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFF},
      {0xFFFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFB, 0x7},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFB, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0x7},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFF, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFB, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFB, 0xFFFFFFFF, 0xF}
    };
    for (int i = 0; i < cases.length; i++) {
      int n = length[i];
      BitVector v = new BitVector(n, cases[i]);
      assertEquals(5.0 * n, problem.value(v), 0.0, "i:" + i + " n:" + n);
      assertEquals(5.0 * n, problem.cost(v), 0.0, "i:" + i + " n:" + n);
    }
  }

  @Test
  public void testPlateausValue75() {
    Plateaus problem = new Plateaus();
    int[] length = {
      4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 32, 32, 32, 32, 32, 32, 32, 32, 128, 128, 128, 128, 128,
      128, 128, 128, 132, 132, 132, 132, 132, 132, 132, 132
    };
    int[][] cases = {
      {7},
      {11},
      {13},
      {14},
      {0xFE},
      {0xFD},
      {0xFB},
      {0xF7},
      {0xEF},
      {0xDF},
      {0xBF},
      {0x7F},
      {0xFFFFFFFE},
      {0xFFFFFEFF},
      {0xFFFEFFFF},
      {0xFEFFFFFF},
      {0xFFFF7FFF},
      {0xFFFFFF7F},
      {0x7FFFFFFF},
      {0xFF7FFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFE},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF},
      {0x7FFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFB, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFF7, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0x7},
      {0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFF, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFF, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFD, 0xFFFFFFFF, 0xF},
      {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFB, 0xFFFFFFFF, 0xF}
    };
    for (int i = 0; i < cases.length; i++) {
      int n = length[i];
      BitVector v = new BitVector(n, cases[i]);
      assertEquals(7.5 * n, problem.value(v), 0.0, "i:" + i + " n:" + n);
      assertEquals(2.5 * n, problem.cost(v), 0.0, "i:" + i + " n:" + n);
    }
  }
}
