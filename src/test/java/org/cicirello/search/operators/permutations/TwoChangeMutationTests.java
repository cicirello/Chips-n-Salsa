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

/** JUnit test cases for TwoChangeMutation. */
public class TwoChangeMutationTests extends PermutationMutationValidator {

  @Test
  public void testTwoChange() {
    TwoChangeMutation m = new TwoChangeMutation();
    undoTester(m);
    mutateTester(m, 4);
    splitTester(m);
    // For n < 4, this mutation operator should do nothing:
    for (int n = 0; n < 4; n++) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      m.mutate(p2);
      assertEquals(p1, p2);
    }
    // test internal mutate for n = 4
    int[] perm = {0, 1, 2, 3};
    Permutation[] expected = {
      new Permutation(new int[] {1, 0, 2, 3}),
      new Permutation(new int[] {0, 2, 1, 3}),
      new Permutation(new int[] {0, 1, 3, 2}),
      new Permutation(new int[] {0, 2, 1, 3})
    };
    for (int i = 0; i < expected.length; i++) {
      Permutation p = new Permutation(perm);
      m.internalMutate(p, i, 1);
      assertEquals(expected[i], p);
    }
    // test internal mutate for n = 5
    perm = new int[] {0, 1, 2, 3, 4};
    Permutation[][] expected2 = {
      {
        new Permutation(new int[] {1, 0, 2, 3, 4}),
        new Permutation(new int[] {0, 2, 1, 3, 4}),
        new Permutation(new int[] {0, 1, 3, 2, 4}),
        new Permutation(new int[] {0, 1, 2, 4, 3}),
        new Permutation(new int[] {4, 1, 2, 3, 0})
      },
      {
        new Permutation(new int[] {0, 1, 2, 4, 3}),
        new Permutation(new int[] {4, 1, 2, 3, 0}),
        new Permutation(new int[] {1, 0, 2, 3, 4}),
        new Permutation(new int[] {0, 2, 1, 3, 4}),
        new Permutation(new int[] {0, 1, 3, 2, 4})
      }
    };
    for (int i = 0; i < expected2.length; i++) {
      for (int j = 0; j < expected2[i].length; j++) {
        Permutation p = new Permutation(perm);
        m.internalMutate(p, j, i + 1);
        assertEquals(expected2[i][j], p);
      }
    }
    // test internal mutate for n = 6
    perm = new int[] {0, 1, 2, 3, 4, 5};
    expected2 =
        new Permutation[][] {
          {
            new Permutation(new int[] {1, 0, 2, 3, 4, 5}),
            new Permutation(new int[] {0, 2, 1, 3, 4, 5}),
            new Permutation(new int[] {0, 1, 3, 2, 4, 5}),
            new Permutation(new int[] {0, 1, 2, 4, 3, 5}),
            new Permutation(new int[] {0, 1, 2, 3, 5, 4}),
            new Permutation(new int[] {5, 1, 2, 3, 4, 0})
          },
          {
            new Permutation(new int[] {2, 1, 0, 3, 4, 5}),
            new Permutation(new int[] {0, 3, 2, 1, 4, 5}),
            new Permutation(new int[] {0, 1, 4, 3, 2, 5}),
            new Permutation(new int[] {0, 1, 2, 5, 4, 3}),
            new Permutation(new int[] {0, 3, 2, 1, 4, 5}),
            new Permutation(new int[] {0, 1, 4, 3, 2, 5})
          },
          {
            new Permutation(new int[] {0, 1, 2, 3, 5, 4}),
            new Permutation(new int[] {5, 1, 2, 3, 4, 0}),
            new Permutation(new int[] {1, 0, 2, 3, 4, 5}),
            new Permutation(new int[] {0, 2, 1, 3, 4, 5}),
            new Permutation(new int[] {0, 1, 3, 2, 4, 5}),
            new Permutation(new int[] {0, 1, 2, 4, 3, 5})
          }
        };
    for (int i = 0; i < expected2.length; i++) {
      for (int j = 0; j < expected2[i].length; j++) {
        Permutation p = new Permutation(perm);
        m.internalMutate(p, j, i + 1);
        assertEquals(expected2[i][j], p);
      }
    }
    // test internal mutate for n = 7.
    perm = new int[] {0, 1, 2, 3, 4, 5, 6};
    expected2 =
        new Permutation[][] {
          {
            new Permutation(new int[] {1, 0, 2, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 2, 1, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 3, 2, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 4, 3, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 3, 5, 4, 6}),
            new Permutation(new int[] {0, 1, 2, 3, 4, 6, 5}),
            new Permutation(new int[] {6, 1, 2, 3, 4, 5, 0})
          },
          {
            new Permutation(new int[] {2, 1, 0, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 3, 2, 1, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 4, 3, 2, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 5, 4, 3, 6}),
            new Permutation(new int[] {0, 1, 2, 3, 6, 5, 4}),
            new Permutation(new int[] {5, 1, 2, 3, 4, 0, 6}),
            new Permutation(new int[] {0, 6, 2, 3, 4, 5, 1})
          },
          {
            new Permutation(new int[] {0, 1, 2, 3, 6, 5, 4}),
            new Permutation(new int[] {5, 1, 2, 3, 4, 0, 6}),
            new Permutation(new int[] {0, 6, 2, 3, 4, 5, 1}),
            new Permutation(new int[] {2, 1, 0, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 3, 2, 1, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 4, 3, 2, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 5, 4, 3, 6})
          },
          {
            new Permutation(new int[] {0, 1, 2, 3, 4, 6, 5}),
            new Permutation(new int[] {6, 1, 2, 3, 4, 5, 0}),
            new Permutation(new int[] {1, 0, 2, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 2, 1, 3, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 3, 2, 4, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 4, 3, 5, 6}),
            new Permutation(new int[] {0, 1, 2, 3, 5, 4, 6})
          }
        };
    for (int i = 0; i < expected2.length; i++) {
      for (int j = 0; j < expected2[i].length; j++) {
        Permutation p = new Permutation(perm);
        m.internalMutate(p, j, i + 1);
        assertEquals(expected2[i][j], p);
      }
    }
  }
}
