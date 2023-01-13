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

/** JUnit test cases for UniformScrambleMutation and UndoableUniformScrambleMutation. */
public class UniformScrambleMutationTests extends PermutationMutationValidator {

  @Test
  public void testUniformScramble() {
    UniformScrambleMutation m = new UniformScrambleMutation(0.0, true);
    mutateTester(m);
    splitTester(m);
    m = new UniformScrambleMutation(1.0);
    mutateTester(m);
    splitTester(m);
    m = new UniformScrambleMutation(0.5, true);
    mutateTester(m);
    splitTester(m);
    m = new UniformScrambleMutation(0.0, false);
    for (int n = 0; n <= 6; n++) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      m.mutate(p2);
      assertEquals(p1, p2);
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new UniformScrambleMutation(-0.000001));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new UniformScrambleMutation(1.000001));
  }

  @Test
  public void testUndoableUniformScramble() {
    UndoableUniformScrambleMutation m = new UndoableUniformScrambleMutation(0.0, true);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    m = new UndoableUniformScrambleMutation(1.0);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    m = new UndoableUniformScrambleMutation(0.5, true);
    undoTester(m);
    mutateTester(m);
    splitTester(m);
    m = new UndoableUniformScrambleMutation(0.0, false);
    for (int n = 0; n <= 6; n++) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      m.mutate(p2);
      assertEquals(p1, p2);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UndoableUniformScrambleMutation(-0.000001));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UndoableUniformScrambleMutation(1.000001));
  }
}
