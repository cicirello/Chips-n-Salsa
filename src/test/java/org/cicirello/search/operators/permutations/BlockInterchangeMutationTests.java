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

/** JUnit test cases for BlockInterchangeMutation. */
public class BlockInterchangeMutationTests extends PermutationMutationValidator {

  @Test
  public void testBlockInterchange() {
    BlockInterchangeMutation m = new BlockInterchangeMutation();
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    // Verify mutations are block interchanges
    for (int n = 2; n <= 6; n++) {
      Permutation p = new Permutation(n);
      for (int t = 0; t < NUM_RAND_TESTS; t++) {
        Permutation mutant = new Permutation(p);
        m.mutate(mutant);
        int a, b, c, d;
        for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++)
          ;
        for (d = p.length() - 1; d >= 0 && p.get(d) == mutant.get(d); d--)
          ;
        for (b = a; b < p.length() && p.get(b) != mutant.get(d); b++)
          ;
        for (c = d; c > b && p.get(c) != mutant.get(a); c--)
          ;
        assertTrue(a <= b && b < c && c <= d);
        int i, j;
        for (i = a, j = c; j <= d; i++, j++) {
          assertEquals(p.get(j), mutant.get(i));
        }
        for (j = b + 1; j < c; i++, j++) {
          assertEquals(p.get(j), mutant.get(i));
        }
        for (j = a; j <= b; j++, i++) {
          assertEquals(p.get(j), mutant.get(i));
        }
      }
    }
    // Check distribution of random indexes
    for (int n = 2; n <= 7; n++) {
      boolean[][] firstPair = new boolean[n - 1][n - 1];
      boolean[][] secondPair = new boolean[n - 1][n - 1];
      int numSamples = (n - 1) * (n - 1) * 80;
      int[] indexes = new int[4];
      for (int i = 0; i < numSamples; i++) {
        m.generateIndexes(n, indexes);
        firstPair[indexes[0]][indexes[1]] = true;
        secondPair[indexes[2] - 1][indexes[3] - 1] = true;
      }
      checkIndexQuartets(firstPair, secondPair);
    }
  }
}
