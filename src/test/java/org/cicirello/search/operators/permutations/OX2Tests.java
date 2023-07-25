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

package org.cicirello.search.operators.permutations;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit test cases for OX2. */
public class OX2Tests extends SharedTestCodeOrderingCrossovers {

  // Insert @Test here to activate during testing to visually inspect cross results
  public void visuallyInspectCrossResult() {
    visualInspection(3, new OrderCrossoverTwo());
  }

  @Test
  public void testOX2IdenticalParents() {
    OrderCrossoverTwo ox2 = new OrderCrossoverTwo();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
    OrderCrossoverTwo s = ox2.split();
    assertNotSame(ox2, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
  }

  @Test
  public void testOX2Near0U() {
    OrderCrossoverTwo ox2 = new OrderCrossoverTwo(Math.ulp(0.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      // the near 0 u should essentially keep all of the parents
      // other than a low probability statistical anomaly
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
    OrderCrossoverTwo s = ox2.split();
    assertNotSame(ox2, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      // the near 0 u should essentially keep all of the parents
      // other than a low probability statistical anomaly
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
  }

  @Test
  public void testOX2Near1U() {
    OrderCrossoverTwo ox2 = new OrderCrossoverTwo(1.0 - Math.ulp(1.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      // the near 1.0 u should essentially swap the parents
      // other than a low probability statistical anomaly
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
    OrderCrossoverTwo s = ox2.split();
    assertNotSame(ox2, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      // the near 1.0 u should essentially swap the parents
      // other than a low probability statistical anomaly
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
  }

  @Test
  public void testOX2Validity() {
    // Validates children as valid permutations only.
    // Does not validate behavior of the OX2.

    OrderCrossoverTwo ox2 = new OrderCrossoverTwo();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }

    ox2 = new OrderCrossoverTwo(0.25);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }

    ox2 = new OrderCrossoverTwo(0.75);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
  }

  @Test
  public void testOX2ValiditySplit() {
    // Validates children as valid permutations only.
    // Does not validate behavior of the OX2.

    OrderCrossoverTwo original = new OrderCrossoverTwo();
    OrderCrossoverTwo ox2 = original.split();

    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }

    ox2 = new OrderCrossoverTwo(0.25);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }

    ox2 = new OrderCrossoverTwo(0.75);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox2.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
  }

  @Test
  public void testInternalCrossOX2() {
    OrderCrossoverTwo ox2 = new OrderCrossoverTwo();
    {
      Permutation c1 = new Permutation(new int[] {1, 0, 3, 2, 5, 4, 7, 6});
      Permutation c2 = new Permutation(new int[] {6, 7, 4, 5, 2, 3, 0, 1});
      boolean[] mask = {false, true, false, true, false, true, false, true};
      Permutation expected1 = new Permutation(new int[] {7, 0, 5, 2, 3, 4, 1, 6});
      Permutation expected2 = new Permutation(new int[] {0, 7, 2, 5, 4, 3, 6, 1});
      final int[][] raw = new int[2][];
      c1.apply(
          (r1, r2) -> {
            raw[0] = r1;
            raw[1] = r2;
          },
          c2);
      ox2.internalCross(raw[0], raw[1], c1, c2, mask);
      assertEquals(expected1, c1);
      assertEquals(expected2, c2);
    }
    {
      Permutation c1 = new Permutation(new int[] {1, 0, 3, 2, 5, 4, 7, 6});
      Permutation c2 = new Permutation(new int[] {6, 7, 4, 5, 2, 3, 0, 1});
      boolean[] mask = {true, false, true, false, true, false, true, false};
      Permutation expected1 = new Permutation(new int[] {1, 6, 3, 4, 5, 2, 7, 0});
      Permutation expected2 = new Permutation(new int[] {6, 1, 4, 3, 2, 5, 0, 7});
      final int[][] raw = new int[2][];
      c1.apply(
          (r1, r2) -> {
            raw[0] = r1;
            raw[1] = r2;
          },
          c2);
      ox2.internalCross(raw[0], raw[1], c1, c2, mask);
      assertEquals(expected1, c1);
      assertEquals(expected2, c2);
    }
    {
      Permutation c1 = new Permutation(new int[] {1, 0, 3, 2, 5, 4, 7, 6});
      Permutation c2 = new Permutation(new int[] {6, 7, 4, 5, 2, 3, 0, 1});
      boolean[] mask = {false, true, true, false, false, false, true, true};
      Permutation expected1 = new Permutation(new int[] {7, 4, 3, 2, 5, 0, 1, 6});
      Permutation expected2 = new Permutation(new int[] {0, 3, 4, 5, 2, 7, 6, 1});
      final int[][] raw = new int[2][];
      c1.apply(
          (r1, r2) -> {
            raw[0] = r1;
            raw[1] = r2;
          },
          c2);
      ox2.internalCross(raw[0], raw[1], c1, c2, mask);
      assertEquals(expected1, c1);
      assertEquals(expected2, c2);
    }
  }

  @Test
  public void testExceptionsOX2() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new OrderCrossoverTwo(0.0));
    thrown = assertThrows(IllegalArgumentException.class, () -> new OrderCrossoverTwo(1.0));
  }
}
