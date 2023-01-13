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

/** JUnit test cases for mutation operators on permutations. */
public class PermutationMutationTests extends PermutationMutationValidator {

  @Test
  public void testWindowLimitedInsertion() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation(window);
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
      // Verify mutations are insertions
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
          if (mutant.get(b) == p.get(a)) {
            for (int i = a; i < b; i++) {
              assertEquals(p.get(i + 1), mutant.get(i));
            }
          } else if (mutant.get(a) == p.get(b)) {
            for (int i = a + 1; i <= b; i++) {
              assertEquals(p.get(i - 1), mutant.get(i));
            }
          } else {
            fail("Not an insertion.");
          }
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedInsertion() {
    WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation();
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
    // Verify mutations are insertions
    for (int n = 2; n <= 4; n++) {
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
        if (mutant.get(b) == p.get(a)) {
          for (int i = a; i < b; i++) {
            assertEquals(p.get(i + 1), mutant.get(i));
          }
        } else if (mutant.get(a) == p.get(b)) {
          for (int i = a + 1; i <= b; i++) {
            assertEquals(p.get(i - 1), mutant.get(i));
          }
        } else {
          fail("Not an insertion.");
        }
      }
    }
  }

  @Test
  public void testWindowLimitedReversal() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedReversalMutation m = new WindowLimitedReversalMutation(window);
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
      // Verify mutations are reversals
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
          while (a <= b) {
            assertEquals(p.get(a), mutant.get(b));
            a++;
            b--;
          }
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedReversal() {
    WindowLimitedReversalMutation m = new WindowLimitedReversalMutation();
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
    // Verify mutations are reversals
    for (int n = 2; n <= 4; n++) {
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
        while (a <= b) {
          assertEquals(p.get(a), mutant.get(b));
          a++;
          b--;
        }
      }
    }
  }

  @Test
  public void testWindowLimitedSwap() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedSwapMutation m = new WindowLimitedSwapMutation(window);
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
      // Verify mutations are swaps
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
          assertEquals(p.get(a), mutant.get(b));
          assertEquals(p.get(b), mutant.get(a));
          for (int i = a + 1; i < b; i++) {
            assertEquals(p.get(i), mutant.get(i));
          }
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedSwap() {
    WindowLimitedSwapMutation m = new WindowLimitedSwapMutation();
    undoTester(m, 4);
    mutateTester(m, 4);
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
    // Verify mutations are swaps
    for (int n = 2; n <= 4; n++) {
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
  }

  @Test
  public void testWindowLimitedBlockMove() {
    for (int window = 1; window <= 6; window++) {
      WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation(window);
      undoTester(m);
      mutateTester(m);
      splitTester(m);
      // Check distribution of random indexes
      for (int n = 2; n <= 6; n++) {
        boolean[][][] indexTriples = new boolean[n][n][n];
        int numSamples = n * (n - 1) * (n + 1) * 40 / 6;
        int[] indexes = new int[3];
        for (int i = 0; i < numSamples; i++) {
          m.generateIndexes(n, indexes);
          indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
        }
        checkIndexTriples(indexTriples, window);
      }
      // Verify mutations are block moves
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
          int c;
          for (c = a + 1; c <= b && p.get(a) != mutant.get(c); c++)
            ;
          // block of p from index a to index (a+b-c) should be same as mutant index c to index b
          int e = a;
          for (int d = c; d <= b; d++, e++) {
            assertEquals(p.get(e), mutant.get(d));
          }
          // block of p from index (a+b-c+1) to index b should be same as mutant index a to index
          // c-1
          for (int d = a; e <= b; d++, e++) {
            assertEquals(p.get(e), mutant.get(d));
          }
        }
      }
    }
  }

  @Test
  public void testWindowUNlimitedBlockMove() {
    WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation();
    undoTester(m, 3);
    mutateTester(m, 3);
    // Check distribution of random indexes
    for (int n = 2; n <= 6; n++) {
      boolean[][][] indexTriples = new boolean[n][n][n];
      int numSamples = n * (n - 1) * (n + 1) * 40 / 6;
      int[] indexes = new int[3];
      for (int i = 0; i < numSamples; i++) {
        m.generateIndexes(n, indexes);
        indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
      }
      checkIndexTriples(indexTriples);
    }
    // Verify mutations are block moves
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
        int c;
        for (c = a + 1; c <= b && p.get(a) != mutant.get(c); c++)
          ;
        // block of p from index a to index (a+b-c) should be same as mutant index c to index b
        int e = a;
        for (int d = c; d <= b; d++, e++) {
          assertEquals(p.get(e), mutant.get(d));
        }
        // block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
        for (int d = a; e <= b; d++, e++) {
          assertEquals(p.get(e), mutant.get(d));
        }
      }
    }
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

  @Test
  public void testWindowLimitedMutationConstructorExceptions() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WindowLimitedUndoableScrambleMutation(0));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new WindowLimitedScrambleMutation(0));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new WindowLimitedBlockMoveMutation(0));
    thrown = assertThrows(IllegalArgumentException.class, () -> new WindowLimitedSwapMutation(0));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new WindowLimitedReversalMutation(0));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new WindowLimitedInsertionMutation(0));
  }
}
