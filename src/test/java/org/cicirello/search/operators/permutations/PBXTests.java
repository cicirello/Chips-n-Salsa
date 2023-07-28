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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit test cases for PBX. */
public class PBXTests {

  @Test
  public void testIdenticalPermutations() {
    PositionBasedCrossover cross = new PositionBasedCrossover();
    PositionBasedCrossover s = cross.split();
    assertNotSame(cross, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      cross.cross(parent1, parent2);
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
      s.cross(parent1, parent2);
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
  }

  @Test
  public void testReversedPermutations() {
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    PositionBasedCrossover cross = new PositionBasedCrossover();
    PositionBasedCrossover s = cross.split();
    assertNotSame(cross, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation parent1 = new Permutation(n, generator);
      Permutation parent2 = new Permutation(parent1);
      parent2.reverse();
      cross.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
    for (int n = 1; n <= 32; n *= 2) {
      Permutation parent1 = new Permutation(n, generator);
      Permutation parent2 = new Permutation(parent1);
      parent2.reverse();
      s.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
  }

  @Test
  public void testRandomPermutations() {
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    PositionBasedCrossover cross = new PositionBasedCrossover();
    PositionBasedCrossover s = cross.split();
    assertNotSame(cross, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation parent1 = new Permutation(n, generator);
      Permutation parent2 = new Permutation(n, generator);
      cross.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
    for (int n = 1; n <= 32; n *= 2) {
      Permutation parent1 = new Permutation(n, generator);
      Permutation parent2 = new Permutation(n, generator);
      s.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
  }

  private boolean validPermutation(Permutation p) {
    boolean[] foundIt = new boolean[p.length()];
    for (int i = 0; i < p.length(); i++) {
      if (foundIt[p.get(i)]) return false;
      foundIt[p.get(i)] = true;
    }
    return true;
  }
}
