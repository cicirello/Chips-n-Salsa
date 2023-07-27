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

import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.CycleDistance;
import org.junit.jupiter.api.*;

/** JUnit test cases for cycle crossover. */
public class CycleCrossoverTests {

  // Insert @Test here to activate during testing to visually inspect cross results
  public void visuallyInspectCrossResult() {
    int reps = 3;
    CycleCrossover cx = new CycleCrossover();
    for (int i = 0; i < reps; i++) {
      Permutation p1 = new Permutation(10);
      Permutation p2 = new Permutation(10);

      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      cx.cross(child1, child2);
      System.out.println("CX Result");
      System.out.println("Parent 1: " + p1);
      System.out.println("Parent 2: " + p2);
      System.out.println("Child 1 : " + child1);
      System.out.println("Child 2 : " + child2);
      System.out.println();
    }
  }

  @Test
  public void testIdenticalPermutations() {
    CycleCrossover cross = new CycleCrossover();
    CycleCrossover s = cross.split();
    for (int n = 1; n <= 16; n *= 2) {
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
  public void testOneBigCycle() {
    CycleCrossover cross = new CycleCrossover();
    for (int n = 2; n <= 16; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      p2.rotate(1);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      cross.cross(parent1, parent2);
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
  }

  @Test
  public void testTypicalCase() {
    CycleCrossover cross = new CycleCrossover();
    validateTypicalCase(cross);
    CycleCrossover s = cross.split();
    assertNotSame(cross, s);
    validateTypicalCase(s);
  }

  private void validateTypicalCase(CycleCrossover cross) {
    int[] perm1 = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    int[] perm2 = {4, 3, 5, 2, 0, 1, 6, 7, 8};
    Permutation p1 = new Permutation(perm1);
    Permutation p2 = new Permutation(perm2);
    Permutation parent1 = new Permutation(p1);
    Permutation parent2 = new Permutation(p2);
    cross.cross(parent1, parent2);
    CycleDistance dist = new CycleDistance();
    assertEquals(1, dist.distance(p1, parent1));
    assertEquals(1, dist.distance(p2, parent2));
    assertEquals(2, dist.distance(p2, parent1));
    assertEquals(2, dist.distance(p1, parent2));
  }
}
