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

/** JUnit test cases for WindowLimitedScrambleMutation and WindowLimitedUndoableScrambleMutation. */
public class WindowLimitedScrambleMutationTests extends PermutationMutationValidator {

  @Test
  public void testExceptions() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WindowLimitedUndoableScrambleMutation(0));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new WindowLimitedScrambleMutation(0));
  }

  @Test
  public void testWindowLimitedScramble() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation(window);
      mutateTester(m);
      splitTester(m);
      // Check distribution of random indexes
      for (int n = 2; n <= 6; n++) {
        boolean[][] indexPairs = new boolean[n][n];
        int numSamples = n * (n - 1) * 40;
        int[] indexes = new int[2];
        for (int i = 0; i < numSamples; i++) {
          m.generateIndexes(n, indexes);
          indexPairs[indexes[0]][indexes[1]] = true;
        }
        checkIndexPairs(indexPairs, window);
      }
      // verify window constraints
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
          assertTrue(b - a <= window);
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedScramble() {
    WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation();
    mutateTester(m, 3);
    // Check distribution of random indexes
    for (int n = 2; n <= 4; n++) {
      boolean[][] indexPairs = new boolean[n][n];
      int numSamples = n * (n - 1) * 40;
      int[] indexes = new int[2];
      for (int i = 0; i < numSamples; i++) {
        m.generateIndexes(n, indexes);
        indexPairs[indexes[0]][indexes[1]] = true;
      }
      checkIndexPairs(indexPairs);
    }
  }

  @Test
  public void testWindowLimitedUndoableScramble() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation(window);
      undoTester(m);
      mutateTester(m);
      splitTester(m);
      // Check distribution of random indexes
      for (int n = 2; n <= 6; n++) {
        boolean[][] indexPairs = new boolean[n][n];
        int numSamples = n * (n - 1) * 40;
        int[] indexes = new int[2];
        for (int i = 0; i < numSamples; i++) {
          m.generateIndexes(n, indexes);
          indexPairs[indexes[0]][indexes[1]] = true;
        }
        checkIndexPairs(indexPairs, window);
      }
      // verify window constraints
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
          assertTrue(b - a <= window);
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedUndoableScramble() {
    WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation();
    undoTester(m, 3);
    mutateTester(m, 3);
    // Check distribution of random indexes
    for (int n = 2; n <= 4; n++) {
      boolean[][] indexPairs = new boolean[n][n];
      int numSamples = n * (n - 1) * 40;
      int[] indexes = new int[2];
      for (int i = 0; i < numSamples; i++) {
        m.generateIndexes(n, indexes);
        indexPairs[indexes[0]][indexes[1]] = true;
      }
      checkIndexPairs(indexPairs);
    }
  }
}
