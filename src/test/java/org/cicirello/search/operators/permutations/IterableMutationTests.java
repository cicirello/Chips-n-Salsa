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

package org.cicirello.search.operators.permutations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.junit.jupiter.api.*;

/** JUnit tests for iterable mutation operators. */
public class IterableMutationTests {

  @Test
  public void testAdjSwapIterator() {
    AdjacentSwapMutation m = new AdjacentSwapMutation();
    for (int n = 0; n <= 6; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int i = 0; i < n - 1; i++) {
        Permutation p = original.copy();
        p.swap(i, i + 1);
        expectedNeighbors.add(p);
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testSwapIterator() {
    SwapMutation m = new SwapMutation();
    for (int n = 0; n <= 6; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          Permutation p = original.copy();
          p.swap(i, j);
          expectedNeighbors.add(p);
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testTwoChangeIterator() {
    TwoChangeMutation m = new TwoChangeMutation();
    // For n < 4, there are no two change neighbors to iterate over.
    for (int n = 0; n < 4; n++) {
      Permutation original = new Permutation(n);
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
    // Now test for n >= 4.
    for (int n = 4; n <= 7; n++) {
      Permutation original = new Permutation(n);
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n - 1 && j - i <= n - 3; j++) {
          Permutation p = original.copy();
          p.reverse(i, j);
          expectedNeighbors.add(p);
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      assertEquals(n * (n - 3) / 2, validate(m, original, expectedNeighbors));
    }
  }

  @Test
  public void testReversalIterator() {
    ReversalMutation m = new ReversalMutation();
    for (int n = 0; n <= 6; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          Permutation p = original.copy();
          p.reverse(i, j);
          expectedNeighbors.add(p);
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testInsertionIterator() {
    InsertionMutation m = new InsertionMutation();
    for (int n = 0; n <= 6; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          if (i == j) continue;
          Permutation p = original.copy();
          p.removeAndInsert(i, j);
          expectedNeighbors.add(p);
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testBlockMoveIterator() {
    BlockMoveMutation m = new BlockMoveMutation();
    for (int n = 0; n <= 8; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          for (int s = 1; j + s - 1 < n; s++) {
            Permutation p = original.copy();
            p.removeAndInsert(j, s, i);
            expectedNeighbors.add(p);
          }
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testBlockInterchangeIterator() {
    BlockInterchangeMutation m = new BlockInterchangeMutation();
    for (int n = 0; n <= 8; n++) {
      // generate the set of actual neigbors of a random permutation of length n
      HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
      Permutation original = new Permutation(n);
      for (int h = 0; h < n; h++) {
        for (int i = h; i < n; i++) {
          for (int j = i + 1; j < n; j++) {
            for (int k = j; k < n; k++) {
              Permutation p = original.copy();
              p.swapBlocks(h, i, j, k);
              expectedNeighbors.add(p);
            }
          }
        }
      }
      // validate the MutationIterator:
      // (1) Verify that it generates the correct set of neighbors
      // (2) Verify that rollback() will rollback to original
      // (3) Verify that setSavepoint and rollback work correctly in combination
      validate(m, original, expectedNeighbors);
    }
  }

  @Test
  public void testWindowedSwapIterator() {
    for (int n = 0; n <= 6; n++) {
      for (int w = 1; w <= n + 1; w++) {
        WindowLimitedSwapMutation m = new WindowLimitedSwapMutation(w);
        // generate the set of actual neigbors of a random permutation of length n
        HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
        Permutation original = new Permutation(n);
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n && j - i <= w; j++) {
            Permutation p = original.copy();
            p.swap(i, j);
            expectedNeighbors.add(p);
          }
        }
        // validate the MutationIterator:
        // (1) Verify that it generates the correct set of neighbors
        // (2) Verify that rollback() will rollback to original
        // (3) Verify that setSavepoint and rollback work correctly in combination
        validate(m, original, expectedNeighbors);
      }
    }
  }

  @Test
  public void testWindowedReversalIterator() {
    for (int n = 0; n <= 6; n++) {
      for (int w = 1; w <= n + 1; w++) {
        WindowLimitedReversalMutation m = new WindowLimitedReversalMutation(w);
        // generate the set of actual neigbors of a random permutation of length n
        HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
        Permutation original = new Permutation(n);
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n && j - i <= w; j++) {
            Permutation p = original.copy();
            p.reverse(i, j);
            expectedNeighbors.add(p);
          }
        }
        // validate the MutationIterator:
        // (1) Verify that it generates the correct set of neighbors
        // (2) Verify that rollback() will rollback to original
        // (3) Verify that setSavepoint and rollback work correctly in combination
        validate(m, original, expectedNeighbors);
      }
    }
  }

  @Test
  public void testWindowedInsertionIterator() {
    for (int n = 0; n <= 6; n++) {
      for (int w = 1; w <= n + 1; w++) {
        WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation(w);
        // generate the set of actual neigbors of a random permutation of length n
        HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
        Permutation original = new Permutation(n);
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            if (i == j || Math.abs(j - i) > w) continue;
            Permutation p = original.copy();
            p.removeAndInsert(i, j);
            expectedNeighbors.add(p);
          }
        }
        // validate the MutationIterator:
        // (1) Verify that it generates the correct set of neighbors
        // (2) Verify that rollback() will rollback to original
        // (3) Verify that setSavepoint and rollback work correctly in combination
        validate(m, original, expectedNeighbors);
      }
    }
  }

  @Test
  public void testWindowedBlockMoveIterator() {
    for (int n = 0; n <= 8; n++) {
      for (int w = 1; w <= n + 1; w++) {
        WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation(w);
        // generate the set of actual neigbors of a random permutation of length n
        HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
        Permutation original = new Permutation(n);
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n; j++) {
            for (int s = 1; j + s - 1 < n; s++) {
              if (Math.abs(j + s - 1 - i) > w) continue;
              Permutation p = original.copy();
              p.removeAndInsert(j, s, i);
              expectedNeighbors.add(p);
            }
          }
        }
        // validate the MutationIterator:
        // (1) Verify that it generates the correct set of neighbors
        // (2) Verify that rollback() will rollback to original
        // (3) Verify that setSavepoint and rollback work correctly in combination
        validate(m, original, expectedNeighbors);
      }
    }
  }

  private int validate(
      IterableMutationOperator<Permutation> mutation,
      Permutation original,
      HashSet<Permutation> expectedNeighbors) {
    Permutation p = original.copy();
    MutationIterator iter = mutation.iterator(p);
    HashSet<Permutation> neighbors = new HashSet<Permutation>();
    int count = 0;
    while (iter.hasNext()) {
      iter.nextMutant();
      neighbors.add(p.copy());
      count++;
    }
    final MutationIterator iterNoMoreMutantsTest = iter;
    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class,
            () -> iterNoMoreMutantsTest.nextMutant(),
            "verify nextMutant throws exception if no more mutants");
    iter.rollback();
    assertEquals(
        expectedNeighbors.size(), neighbors.size(), "verify number of neighbors, n=" + p.length());
    assertEquals(
        expectedNeighbors,
        neighbors,
        "verify set of neighbors are as expected, original=" + original);
    assertEquals(original, p, "verify rolled back to original");
    assertEquals(expectedNeighbors.size(), count);
    iter.rollback();
    assertEquals(original, p, "verify extra rollback does nothing");
    for (int i = 0; i < count; i++) {
      iter = mutation.iterator(p);
      Permutation saved = p.copy();
      int j = 0;
      while (iter.hasNext()) {
        iter.nextMutant();
        if (j == i) {
          iter.setSavepoint();
          saved = p.copy();
        }
        j++;
      }
      iter.rollback();
      assertEquals(
          saved, p, "verify rolled back to last savepoint, original=" + original + " i=" + i);
    }
    // test rollback immediately after setSavepoint
    for (int i = 0; i < count; i++) {
      iter = mutation.iterator(p);
      Permutation saved = p.copy();
      int j = 0;
      while (iter.hasNext()) {
        iter.nextMutant();
        if (j == i) {
          iter.setSavepoint();
          saved = p.copy();
          break;
        }
        j++;
      }
      iter.rollback();
      assertEquals(
          saved,
          p,
          "rollback immediately after setSavepoint, verify rolled back to last savepoint, original="
              + original
              + " i="
              + i);
      final MutationIterator iterRolledWithoutIteratingOverAllTest = iter;
      thrown =
          assertThrows(
              IllegalStateException.class,
              () -> iterRolledWithoutIteratingOverAllTest.nextMutant(),
              "verify nextMutant throws exception if rolled back");
      assertFalse(iter.hasNext());
    }
    // test rollback one step after setSavepoint
    for (int i = 0; i < count - 1; i++) {
      iter = mutation.iterator(p);
      Permutation saved = p.copy();
      int j = 0;
      while (iter.hasNext()) {
        iter.nextMutant();
        if (j == i) {
          iter.setSavepoint();
          saved = p.copy();
        } else if (j > i) {
          break;
        }
        j++;
      }
      iter.rollback();
      assertEquals(
          saved,
          p,
          "rollback one step after setSavepoint, verify rolled back to last savepoint, original="
              + original
              + " i="
              + i);
    }
    // test rollback two steps after setSavepoint
    for (int i = 0; i < count - 1; i++) {
      iter = mutation.iterator(p);
      Permutation saved = p.copy();
      int j = 0;
      while (iter.hasNext()) {
        iter.nextMutant();
        if (j == i) {
          iter.setSavepoint();
          saved = p.copy();
        } else if (j > i + 1) {
          break;
        }
        j++;
      }
      iter.rollback();
      assertEquals(
          saved,
          p,
          "rollback two steps after setSavepoint, verify rolled back to last savepoint, original="
              + original
              + " i="
              + i);
    }
    return count;
  }
}
