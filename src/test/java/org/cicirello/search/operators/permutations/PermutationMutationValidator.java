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
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.junit.jupiter.api.*;

/** Validation related helpers for test cases for permutation mutation operators. */
public class PermutationMutationValidator {

  // For tests involving randomness, number of trials to include in test case.
  static final int NUM_RAND_TESTS = 40;

  void undoTester(UndoableMutationOperator<Permutation> m) {
    undoTester(m, 0);
  }

  void undoTester(UndoableMutationOperator<Permutation> m, int minPermLength) {
    // iterate over different length permutations beginning with 0 length
    for (int i = minPermLength; i <= 6; i++) {
      Permutation p = new Permutation(i);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        m.undo(mutant);
        assertEquals(p, mutant);
      }
    }
  }

  void mutateTester(MutationOperator<Permutation> m) {
    mutateTester(m, 0);
  }

  void mutateTester(MutationOperator<Permutation> m, int minPermLength) {
    for (int i = minPermLength; i <= 6; i++) {
      Permutation p = new Permutation(i);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        // verify mutation produced a valid permutation
        validate(mutant);
        if (i < 2) {
          assertEquals(p, mutant);
        } else {
          assertNotEquals(p, mutant);
        }
      }
    }
  }

  void validate(Permutation p) {
    boolean[] inP = new boolean[p.length()];
    for (int i = 0; i < inP.length; i++) {
      int j = p.get(i);
      assertTrue(j >= 0 && j < inP.length);
      inP[j] = true;
    }
    for (int i = 0; i < inP.length; i++) {
      assertTrue(inP[i]);
    }
  }

  void splitTester(MutationOperator<Permutation> m) {
    MutationOperator<Permutation> s = m.split();
    assertEquals(m.getClass(), s.getClass());
    if (m instanceof UndoableMutationOperator) {
      for (int i = 0; i < 10; i++) {
        Permutation p1 = new Permutation(10);
        Permutation p2 = p1.copy();
        Permutation p3 = p1.copy();
        m.mutate(p2);
        assertNotEquals(p1, p2);
        UndoableMutationOperator<Permutation> m2 =
            (UndoableMutationOperator<Permutation>) m.split();
        m2.mutate(p3);
        assertNotEquals(p1, p3);
        ((UndoableMutationOperator<Permutation>) m).undo(p2);
        assertEquals(p1, p2);
        m2.undo(p3);
        assertEquals(p1, p3);
      }
    }
  }

  void checkIndexPairs(boolean[][] pairs) {
    // verify that each pair was generated over many trials
    for (int i = 0; i < pairs.length; i++) {
      for (int j = 0; j < pairs.length; j++) {
        if (i != j) {
          assertTrue(pairs[i][j], "failed to generate: (" + i + ", " + j + ")");
        }
      }
    }
  }

  void checkIndexPairs(boolean[][] pairs, int window) {
    // verify that each pair within window was generated over many trials
    // and verify no window violations
    for (int i = 0; i < pairs.length; i++) {
      for (int j = 0; j < pairs.length; j++) {
        if (i != j) {
          if (Math.abs(i - j) <= window) {
            assertTrue(
                pairs[i][j], "failed to generate: w=" + window + " pair=(" + i + ", " + j + ")");
          } else {
            assertFalse(
                pairs[i][j], "window violation: w=" + window + " pair=(" + i + ", " + j + ")");
          }
        }
      }
    }
  }

  void checkIndexTriples(boolean[][][] triples) {
    // verify that each triple was generated over many trials
    for (int i = 0; i < triples.length; i++) {
      for (int j = i + 1; j < triples.length; j++) {
        for (int k = j; k < triples.length; k++) {
          assertTrue(triples[i][j][k], "failed to generate: (" + i + ", " + j + ", " + k + ")");
        }
      }
    }
  }

  void checkIndexTriples(boolean[][][] triples, int window) {
    // verify that each triple was generated over many trials
    // and verify no window violations
    for (int i = 0; i < triples.length; i++) {
      for (int j = i + 1; j < triples.length; j++) {
        for (int k = j; k < triples.length; k++) {
          if (k - i <= window) {
            assertTrue(
                triples[i][j][k],
                "failed to generate: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")");
          } else {
            assertFalse(
                triples[i][j][k],
                "window violation: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")");
          }
        }
      }
    }
  }

  void checkIndexQuartets(boolean[][] firstPair, boolean[][] secondPair) {
    // verify that each of the possible left blocks was generated at least once
    // and each of the possible right blocks was generated at least once over
    // many trials.
    for (int i = 0; i < firstPair.length; i++) {
      for (int j = i; j < firstPair[i].length; j++) {
        assertTrue(firstPair[i][j], "failed to generate left pair: (" + i + ", " + j + ")");
        assertTrue(
            secondPair[i][j], "failed to generate right pair: (" + (i + 1) + ", " + (j + 1) + ")");
      }
    }
  }
}
