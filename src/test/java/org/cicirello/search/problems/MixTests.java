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

/** JUnit test cases for the Mix problem. */
public class MixTests {

  private final double EPSILON = 1e-10;

  @Test
  public void testMixAllOnesAndAllZerosCases() {
    Mix problem = new Mix();
    assertEquals(0.0, problem.minCost(), 0.0);
    double zero = 0;
    assertTrue(problem.isMinCost(zero));
    assertFalse(problem.isMinCost(zero - 1));
    assertFalse(problem.isMinCost(zero + 1));
    // Problem is ill-defined if n < 5,
    // so only testing cases with n >= 5.
    for (int n = 5; n <= 32; n++) {
      // For all zeros case, need to account for Porcupine, TwoMax, and Trap.
      int m = n / 5;
      if (n % 5 > 3) m++;
      int porcupineValue = m % 2 == 1 ? -15 : 0;
      m = n / 5;
      if (n % 5 > 1) m++;
      int twomaxValue = 8 * m;
      m = n / 5;
      if (n % 5 > 2) m++;
      int trapValue = m > 1 ? 8 * m : 0;
      int expectedValueZeroCase = porcupineValue + twomaxValue + trapValue;

      BitVector v = new BitVector(n);
      // all zeros
      assertEquals(expectedValueZeroCase, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValueZeroCase, problem.cost(v), 0.0);
      // all ones
      v.not();
      assertEquals(0.0, problem.cost(v), 0.0);
      assertEquals(10.0 * n, problem.value(v), 0.0);
    }
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);

      // For all zeros case, need to account for Porcupine, TwoMax, and Trap.
      int expectedValueZeroCase = 32 * i * 8 * 2;

      // all zeros
      assertEquals(expectedValueZeroCase, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValueZeroCase, problem.cost(v), 0.0);
      // all ones
      v.not();
      assertEquals(0.0, problem.cost(v), 0.0);
      assertEquals(10.0 * n, problem.value(v), 0.0);
    }
  }

  @Test
  public void testMixAllOnesInOneMaxSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 0; j < i; j++) {
        v.set32(j, 0xFFFFFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from all ones segment
      expectedValue += 10 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixAllOnesInTwoMaxSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = i; j < i + i; j++) {
        v.set32(j, 0xFFFFFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, and Trap.
      int expectedValue = m * 8;

      // Add in contribution from all ones segment
      expectedValue += 10 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixAllOnesInTrapSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 2 * i; j < 2 * i + i; j++) {
        v.set32(j, 0xFFFFFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax.
      int expectedValue = m * 8;

      // Add in contribution from all ones segment
      expectedValue += 10 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixAllOnesInPorcupineSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 3 * i; j < 3 * i + i; j++) {
        v.set32(j, 0xFFFFFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from all ones segment
      expectedValue += 10 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixAllOnesInPlateauSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 4 * i; j < 4 * i + i; j++) {
        v.set32(j, 0xFFFFFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from all ones segment
      expectedValue += 10 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixHalfOnesInOneMaxSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 0; j < i; j++) {
        v.set32(j, 0xFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from half ones segment
      expectedValue += 5 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixHalfOnesInTwoMaxSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = i; j < i + i; j++) {
        v.set32(j, 0xFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, and Trap.
      int expectedValue = m * 8;

      // Add in contribution from half ones segment
      expectedValue += m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixHalfOnesInTrapSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 2 * i; j < 2 * i + i; j++) {
        v.set32(j, 0xFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax.
      double expectedValue = m * 8;

      // Add in contribution from half ones segment
      expectedValue += 8.0 * m / 3.0;

      assertEquals(expectedValue, problem.value(v), EPSILON);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), EPSILON);
    }
  }

  @Test
  public void testMixHalfOnesInPorcupineSegment() {
    Mix problem = new Mix();
    // Exactly half ones case
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 3 * i; j < 3 * i + i; j++) {
        v.set32(j, 0xFFFF);
      }

      int m = 32 * i;

      // Need to account for all Zeros in TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from all ones segment
      expectedValue += 5 * m;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
    // Almost half ones case (to get a Porcupine quill in there).
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      v.set32(3 * i, 0xFFFE);
      if (i == 2) v.set32(3 * i + 1, 0xFFFF);

      int m = 32 * i;

      // Need to account for all Zeros in TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // Add in contribution from half ones segment
      expectedValue += 10 * (m / 2 - 1) - 15;

      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }

  @Test
  public void testMixMissingOneOnesInPlateauSegment() {
    Mix problem = new Mix();
    for (int i = 1; i <= 2; i++) {
      int n = 160 * i;
      BitVector v = new BitVector(n);
      for (int j = 4 * i; j < 4 * i + i; j++) {
        v.set32(j, 0xFFFFFFFE);
      }

      int m = 32 * i;

      // Need to account for all Zeros in Porcupine, TwoMax, and Trap.
      int expectedValue = m * 8 * 2;

      // all zeros
      assertEquals(expectedValue, problem.value(v), 0.0);
      assertEquals(10.0 * n - expectedValue, problem.cost(v), 0.0);
    }
  }
}
