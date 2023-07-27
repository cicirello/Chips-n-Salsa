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

/** JUnit test cases for PPX. */
public class PPXTests {

  @Test
  public void testPPXIdentical() {
    PrecedencePreservativeCrossover ppx = new PrecedencePreservativeCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
    PrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
  }

  @Test
  public void testPPXRandom() {
    PrecedencePreservativeCrossover ppx = new PrecedencePreservativeCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
    PrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
  }

  @Test
  public void testInternalCrossPPX() {
    PrecedencePreservativeCrossover ppx = new PrecedencePreservativeCrossover();
    int[] perm1 = {5, 4, 3, 2, 1, 0};
    int[] perm2 = {0, 1, 2, 3, 4, 5};
    for (int i = 0; i < perm1.length; i++) {
      for (int j = i; j < perm1.length; j++) {
        int[] p1 = perm1.clone();
        int[] p2 = perm2.clone();
        ppx.internalCross(p1, p2, i, j);
        for (int k = 0; k < i; k++) {
          assertEquals(perm1[k], p1[k]);
          assertEquals(perm2[k], p2[k]);
        }
        for (int k = i; k <= j; k++) {
          assertEquals(perm1[k - i], p2[k]);
          assertEquals(perm2[k - i], p1[k]);
        }
        for (int k = j + 1; k < p1.length; k++) {
          assertEquals(perm1[k - j - 1 + i], p1[k]);
          assertEquals(perm2[k - j - 1 + i], p2[k]);
        }
      }
    }
    int[] overlap1 = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    int[] overlap2 = {0, 8, 1, 2, 6, 4, 0, 3, 5};
    int[] p1 = overlap1.clone();
    int[] p2 = overlap2.clone();
    int i = 3;
    int j = 5;
    ppx.internalCross(p1, p2, i, j);
    int[] expected1 = {8, 7, 6, 0, 1, 2, 5, 4, 3};
    int[] expected2 = {0, 8, 1, 7, 6, 5, 2, 4, 3};
    assertArrayEquals(expected1, p1);
    assertArrayEquals(expected2, p2);
  }

  @Test
  public void testUPPXIdentical() {
    UniformPrecedencePreservativeCrossover ppx = new UniformPrecedencePreservativeCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
    UniformPrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
  }

  @Test
  public void testUPPXRandom() {
    UniformPrecedencePreservativeCrossover ppx = new UniformPrecedencePreservativeCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
    UniformPrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }

    ppx = new UniformPrecedencePreservativeCrossover(0.25);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
    s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }

    ppx = new UniformPrecedencePreservativeCrossover(0.75);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      ppx.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
    s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      s.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
  }

  @Test
  public void testExceptionsUPPX() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UniformPrecedencePreservativeCrossover(0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UniformPrecedencePreservativeCrossover(1.0));
  }

  @Test
  public void testInternalCrossUPPX() {
    UniformPrecedencePreservativeCrossover ppx = new UniformPrecedencePreservativeCrossover();
    int[] p1 = {7, 6, 5, 4, 3, 2, 1, 0};
    int[] p2 = {0, 1, 2, 3, 4, 5, 6, 7};
    int[] expected1 = {0, 7, 1, 2, 3, 6, 4, 5};
    int[] expected2 = {7, 0, 6, 5, 4, 1, 3, 2};
    boolean[] mask = {false, true, false, false, false, true, false, true};
    ppx.internalCross(p1, p2, mask);
    assertArrayEquals(expected1, p1);
    assertArrayEquals(expected2, p2);
  }

  @Test
  public void testUPPXNear0U() {
    UniformPrecedencePreservativeCrossover ppx =
        new UniformPrecedencePreservativeCrossover(Math.ulp(0.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ppx.cross(parent1, parent2);
      // the near 0 u should essentially swap the parents
      // other than a low probability statistical anomaly
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
    UniformPrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      // the near 0 u should essentially swap the parents
      // other than a low probability statistical anomaly
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
  }

  @Test
  public void testUPPXNear1U() {
    UniformPrecedencePreservativeCrossover ppx =
        new UniformPrecedencePreservativeCrossover(1.0 - Math.ulp(1.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ppx.cross(parent1, parent2);
      // the near 1.0 u should essentially keep all of the parents
      // other than a low probability statistical anomaly
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
    UniformPrecedencePreservativeCrossover s = ppx.split();
    assertNotSame(ppx, s);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      // the near 1.0 u should essentially keep all of the parents
      // other than a low probability statistical anomaly
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
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
