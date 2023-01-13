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

import java.util.ArrayList;
import java.util.HashSet;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.MutationIterator;
import org.junit.jupiter.api.*;

/** JUnit test cases for RotationMutation. */
public class RotationMutationTests extends PermutationMutationValidator {

  @Test
  public void testRotation() {
    RotationMutation m = new RotationMutation();
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    Permutation[] testcases = {
      new Permutation(new int[] {0}),
      new Permutation(new int[] {0, 1}),
      new Permutation(new int[] {0, 2, 1}),
      new Permutation(new int[] {2, 0, 3, 1})
    };
    Permutation[][] expectedRaw = {
      {},
      {new Permutation(new int[] {1, 0})},
      {new Permutation(new int[] {2, 1, 0}), new Permutation(new int[] {1, 0, 2})},
      {
        new Permutation(new int[] {0, 3, 1, 2}),
        new Permutation(new int[] {3, 1, 2, 0}),
        new Permutation(new int[] {1, 2, 0, 3})
      }
    };
    ArrayList<HashSet<Permutation>> expected = new ArrayList<HashSet<Permutation>>();
    for (int i = 0; i < expectedRaw.length; i++) {
      HashSet<Permutation> set = new HashSet<Permutation>();
      for (int j = 0; j < expectedRaw[i].length; j++) {
        set.add(expectedRaw[i][j]);
      }
      expected.add(set);
    }
    for (int i = 1; i < testcases.length; i++) {
      for (int j = 0; j < 8; j++) {
        Permutation mutant = new Permutation(testcases[i]);
        m.mutate(mutant);
        assertTrue(expected.get(i).contains(mutant));
      }
    }
    for (int i = 0; i < testcases.length; i++) {
      Permutation p = new Permutation(testcases[i]);
      final MutationIterator iter = m.iterator(p);
      HashSet<Permutation> observed = new HashSet<Permutation>();
      int count = 0;
      while (iter.hasNext()) {
        iter.nextMutant();
        count++;
        observed.add(p);
      }
      assertEquals(count, observed.size());
      assertEquals(count, expected.get(i).size());
      assertEquals(expected.get(i), observed);
      iter.rollback();
      assertEquals(testcases[i], p);
    }
    for (int i = 1; i < testcases.length; i++) {
      for (int s = 1; s <= expectedRaw[i].length; s++) {
        Permutation p = new Permutation(testcases[i]);
        MutationIterator iter = m.iterator(p);
        HashSet<Permutation> observed = new HashSet<Permutation>();
        int count = 0;
        Permutation pExp = null;
        while (iter.hasNext()) {
          iter.nextMutant();
          count++;
          observed.add(p);
          if (s == count) {
            iter.setSavepoint();
            pExp = new Permutation(p);
          }
        }
        iter.rollback();
        assertEquals(pExp, p);
        assertEquals(count, observed.size());
        assertEquals(count, expected.get(i).size());
        assertEquals(expected.get(i), observed);
      }
      for (int s = 1; s <= expectedRaw[i].length; s++) {
        Permutation p = new Permutation(testcases[i]);
        MutationIterator iter = m.iterator(p);
        int count = 0;
        Permutation pExp = null;
        while (iter.hasNext()) {
          iter.nextMutant();
          count++;
          if (s == count) {
            iter.setSavepoint();
            pExp = new Permutation(p);
            break;
          }
        }
        iter.rollback();
        assertEquals(pExp, p);
        IllegalStateException thrown =
            assertThrows(IllegalStateException.class, () -> iter.nextMutant());
      }
      for (int s = 1; s <= expectedRaw[i].length; s++) {
        Permutation p = new Permutation(testcases[i]);
        MutationIterator iter = m.iterator(p);
        int count = 0;
        Permutation pExp = null;
        while (iter.hasNext()) {
          iter.nextMutant();
          count++;
          if (s == count) {
            iter.setSavepoint();
            pExp = new Permutation(p);
          } else if (s == count - 1) {
            break;
          }
        }
        iter.rollback();
        assertEquals(pExp, p);
        assertFalse(iter.hasNext());
        iter.rollback();
        assertEquals(pExp, p);
      }
    }
  }
}
