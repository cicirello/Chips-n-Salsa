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

/** JUnit test cases for CycleMutation. */
public class CycleMutationTests extends PermutationMutationValidator {

  @Test
  public void testCycle2() {
    CycleMutation m = new CycleMutation(2);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // Verify mutations are 2-cycles (i.e., swaps)
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
        assertThrows(IllegalArgumentException.class, () -> new CycleMutation(1));
  }

  @Test
  public void testCycle3() {
    CycleMutation m = new CycleMutation(3);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // Verify mutations are 2-cycles or 3-cycles
    int[] indexes = new int[3];
    boolean[] foundCycleLength = new boolean[4];
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int size = 0;
        for (int i = 0; i < p.length(); i++) {
          if (p.get(i) != mutant.get(i)) {
            if (size == indexes.length) {
              fail("cycle is too large");
            }
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
      assertTrue(foundCycleLength[i]);
    }
  }

  @Test
  public void testCycle4() {
    CycleMutation m = new CycleMutation(4);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // Verify mutations are 2-cycles, 3-cycles, or 4-cycles
    int[] indexes = new int[4];
    boolean[] foundCycleLength = new boolean[5];
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int size = 0;
        for (int i = 0; i < p.length(); i++) {
          if (p.get(i) != mutant.get(i)) {
            if (size == indexes.length) {
              fail("cycle is too large");
            }
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
      assertTrue(foundCycleLength[i]);
    }
  }
}
