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

/** JUnit test cases for CycleAlphaMutation. */
public class CycleAlphaMutationTests extends PermutationMutationValidator {

  @Test
  public void testCycleAlphaComputeK0() {
    double[] U = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.99};
    CycleAlphaMutation m = new CycleAlphaMutation(Math.ulp(0.0));
    for (int n = 2; n <= 128; n *= 2) {
      for (double u : U) {
        assertEquals(2, m.computeK(n, u));
      }
    }
  }

  @Test
  public void testCycleAlphaComputeK999() {
    double[] U = {0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1.0 - Math.ulp(1.0)};
    int[] expected = {2, 2, 3, 4, 5, 6, 7, 8, 9};
    CycleAlphaMutation m = new CycleAlphaMutation(0.999);
    int n = 9;
    for (int i = 0; i < U.length; i++) {
      double u = U[i];
      assertEquals(expected[i], m.computeK(n, u), "u:" + u);
    }
  }

  @Test
  public void testCycleAlphaComputeK05() {
    double[] U = {
      0.0,
      0.5,
      0.50197,
      0.75,
      0.75295,
      0.875,
      0.8785,
      0.9375,
      0.942,
      0.96875,
      0.9726,
      0.984375,
      0.9883,
      0.9961,
      1.0 - Math.ulp(1.0)
    };
    int[] expected = {2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 9, 9};
    CycleAlphaMutation m = new CycleAlphaMutation(0.5);
    int n = 9;
    for (int i = 0; i < U.length; i++) {
      double u = U[i];
      assertEquals(expected[i], m.computeK(n, u), "u:" + u);
    }
  }

  @Test
  public void testCycleAlphaNear0() {
    CycleAlphaMutation m = new CycleAlphaMutation(Math.ulp(0.0));
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // When alpha is near 0, with extremely high probability all mutations
    // should be 2-cycles, i.e., swaps.
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int a, b;
        for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++)
          ;
        for (b = p.length() - 1; b >= 0 && p.get(b) == mutant.get(b); b--)
          ;
        assertTrue(a <= b);
        assertEquals(p.get(a), mutant.get(b));
        assertEquals(p.get(b), mutant.get(a));
        for (int i = a + 1; i < b; i++) {
          assertEquals(p.get(i), mutant.get(i));
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new CycleAlphaMutation(0.0));
    thrown = assertThrows(IllegalArgumentException.class, () -> new CycleAlphaMutation(1.0));
  }

  @Test
  public void testCycleAlphaNear1() {
    CycleAlphaMutation m = new CycleAlphaMutation(0.999);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // When alpha is very near 1.0, cycle length should be approximately uniform in [2, n]
    boolean[] foundCycleLength = new boolean[7];
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      int[] indexes = new int[n];

      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int size = 0;
        for (int i = 0; i < p.length(); i++) {
          if (p.get(i) != mutant.get(i)) {
            indexes[size] = i;
            size++;
          }
        }
        foundCycleLength[size] = true;
        int[] inv = p.getInverse();
        boolean[] cycleCheck = new boolean[n];
        int j = indexes[0];
        int next = p.get(j);
        for (int i = 0; i < size; i++) {
          assertFalse(cycleCheck[next]);
          cycleCheck[next] = true;
          next = mutant.get(j);
          j = inv[next];
          assertNotEquals(p.get(j), mutant.get(j));
        }
        assertTrue(cycleCheck[next]);
      }
    }
    for (int i = 2; i < foundCycleLength.length; i++) {
      assertTrue(foundCycleLength[i], "i=" + i);
    }
  }

  @Test
  public void testCycleAlpha05() {
    CycleAlphaMutation m = new CycleAlphaMutation(0.5);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      int[] indexes = new int[n];
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int size = 0;
        for (int i = 0; i < p.length(); i++) {
          if (p.get(i) != mutant.get(i)) {
            indexes[size] = i;
            size++;
          }
        }
        int[] inv = p.getInverse();
        boolean[] cycleCheck = new boolean[n];
        int j = indexes[0];
        int next = p.get(j);
        for (int i = 0; i < size; i++) {
          assertFalse(cycleCheck[next]);
          cycleCheck[next] = true;
          next = mutant.get(j);
          j = inv[next];
          assertNotEquals(p.get(j), mutant.get(j));
        }
        assertTrue(cycleCheck[next]);
      }
    }
  }
}
