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

/** JUnit test cases for PMX and UPMX. */
public class PMXAndRelatedTests {

  // Insert @Test here to activate during testing to visually inspect cross results
  public void visuallyInspectCrossResult() {
    int reps = 3;
    PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
    for (int i = 0; i < reps; i++) {
      Permutation p1 = new Permutation(10);
      Permutation p2 = new Permutation(10);

      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      pmx.cross(child1, child2);
      System.out.println("PMX Result");
      System.out.println("Parent 1: " + p1);
      System.out.println("Parent 2: " + p2);
      System.out.println("Child 1 : " + child1);
      System.out.println("Child 2 : " + child2);
      System.out.println();
    }

    UniformPartiallyMatchedCrossover upmx = new UniformPartiallyMatchedCrossover();
    for (int i = 0; i < reps; i++) {
      Permutation p1 = new Permutation(10);
      Permutation p2 = new Permutation(10);

      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      upmx.cross(child1, child2);
      System.out.println("UPMX Result");
      System.out.println("Parent 1: " + p1);
      System.out.println("Parent 2: " + p2);
      System.out.println("Child 1 : " + child1);
      System.out.println("Child 2 : " + child2);
      System.out.println();
    }
  }

  @Test
  public void testInternalPMX() {
    PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
    Permutation p1 = new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0});
    Permutation p2 = new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5});
    int[][] indexes = {
      {4, 4},
      {4, 5},
      {3, 5},
      {3, 6},
      {2, 6},
      {2, 7},
      {1, 7},
      {1, 8},
      {0, 8}
    };
    Permutation[][] expected = {
      {
        new Permutation(new int[] {8, 4, 6, 5, 7, 3, 2, 1, 0}),
        new Permutation(new int[] {0, 1, 2, 6, 4, 8, 3, 7, 5})
      },
      {
        new Permutation(new int[] {3, 4, 6, 5, 7, 8, 2, 1, 0}),
        new Permutation(new int[] {0, 1, 2, 6, 4, 3, 8, 7, 5})
      },
      {
        new Permutation(new int[] {3, 4, 5, 6, 7, 8, 2, 1, 0}),
        new Permutation(new int[] {0, 1, 2, 5, 4, 3, 8, 7, 6})
      },
      {
        new Permutation(new int[] {2, 4, 5, 6, 7, 8, 3, 1, 0}),
        new Permutation(new int[] {0, 1, 8, 5, 4, 3, 2, 7, 6})
      },
      {
        new Permutation(new int[] {5, 4, 2, 6, 7, 8, 3, 1, 0}),
        new Permutation(new int[] {0, 1, 6, 5, 4, 3, 2, 7, 8})
      },
      {
        new Permutation(new int[] {5, 1, 2, 6, 7, 8, 3, 4, 0}),
        new Permutation(new int[] {0, 7, 6, 5, 4, 3, 2, 1, 8})
      },
      {
        new Permutation(new int[] {5, 1, 2, 6, 7, 8, 3, 4, 0}),
        new Permutation(new int[] {0, 7, 6, 5, 4, 3, 2, 1, 8})
      },
      {
        new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5}),
        new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0})
      },
      {
        new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5}),
        new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0})
      }
    };
    final int[][] wrapper = new int[2][];
    for (int k = 0; k < indexes.length; k++) {
      int i = indexes[k][0];
      int j = indexes[k][1];
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      child1.apply(raw -> wrapper[0] = raw);
      child2.apply(raw -> wrapper[1] = raw);
      pmx.internalCross(wrapper[0], wrapper[1], child1, child2, i, j);
      assertEquals(expected[k][0], child1);
      assertEquals(expected[k][1], child2);
      child1 = new Permutation(p1);
      child2 = new Permutation(p2);
      child1.apply(raw -> wrapper[0] = raw);
      child2.apply(raw -> wrapper[1] = raw);
      pmx.internalCross(wrapper[0], wrapper[1], child1, child2, j, i);
      assertEquals(expected[k][0], child1);
      assertEquals(expected[k][1], child2);
    }
  }

  @Test
  public void testPMXIdentical() {
    PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      pmx.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
    assertSame(pmx, pmx.split());
  }

  @Test
  public void testPMX() {
    PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      pmx.cross(child1, child2);
      assertTrue(validPermutation(child1));
      assertTrue(validPermutation(child2));
    }
    assertSame(pmx, pmx.split());
  }

  @Test
  public void testUPMXIdentical() {
    UniformPartiallyMatchedCrossover upmx = new UniformPartiallyMatchedCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(p1);
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      upmx.cross(child1, child2);
      assertEquals(p1, child1);
      assertEquals(p2, child2);
    }
    assertSame(upmx, upmx.split());
  }

  @Test
  public void testUPMXNear0U() {
    UniformPartiallyMatchedCrossover upmx = new UniformPartiallyMatchedCrossover(Math.ulp(0.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      upmx.cross(parent1, parent2);
      // the near 0 u should essentially keep all of the parents
      // other than a low probability statistical anomaly
      assertEquals(p1, parent1);
      assertEquals(p2, parent2);
    }
  }

  @Test
  public void testUPMXNear1U() {
    UniformPartiallyMatchedCrossover upmx =
        new UniformPartiallyMatchedCrossover(1.0 - Math.ulp(1.0));
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      upmx.cross(parent1, parent2);
      // the near 1.0 u should essentially swap the parents
      // other than a low probability statistical anomaly
      assertEquals(p2, parent1);
      assertEquals(p1, parent2);
    }
  }

  @Test
  public void testUPMXInternalCross() {
    UniformPartiallyMatchedCrossover upmx = new UniformPartiallyMatchedCrossover();
    Permutation p1 = new Permutation(new int[] {7, 6, 5, 4, 3, 2, 1, 0});
    Permutation p2 = new Permutation(new int[] {1, 2, 0, 5, 6, 4, 7, 3});
    int[][] indexes = {
      {3}, // 4, 5
      {3, 1}, // 6, 2
      {3, 1, 6}, // 1, 7
      {3, 1, 6, 0}, // 7, 1
      {3, 1, 6, 0, 2}, // 5, 0
      {3, 1, 6, 0, 2, 5}, // 2, 4
      {3, 1, 6, 0, 2, 5, 4}, // 3, 6
      {3, 1, 6, 0, 2, 5, 4, 7} // 0, 3
    };
    Permutation[][] expected = {
      {
        new Permutation(new int[] {7, 6, 4, 5, 3, 2, 1, 0}),
        new Permutation(new int[] {1, 2, 0, 4, 6, 5, 7, 3})
      },
      {
        new Permutation(new int[] {7, 2, 4, 5, 3, 6, 1, 0}),
        new Permutation(new int[] {1, 6, 0, 4, 2, 5, 7, 3})
      },
      {
        new Permutation(new int[] {1, 2, 4, 5, 3, 6, 7, 0}),
        new Permutation(new int[] {7, 6, 0, 4, 2, 5, 1, 3})
      },
      {
        new Permutation(new int[] {1, 2, 4, 5, 3, 6, 7, 0}),
        new Permutation(new int[] {7, 6, 0, 4, 2, 5, 1, 3})
      },
      {
        new Permutation(new int[] {1, 2, 0, 5, 3, 6, 7, 4}),
        new Permutation(new int[] {7, 6, 5, 4, 2, 0, 1, 3})
      },
      {
        new Permutation(new int[] {1, 2, 0, 5, 3, 4, 7, 6}),
        new Permutation(new int[] {7, 6, 5, 4, 0, 2, 1, 3})
      },
      {
        new Permutation(new int[] {1, 2, 0, 5, 6, 4, 7, 3}),
        new Permutation(new int[] {7, 6, 5, 4, 3, 2, 1, 0})
      },
      {
        new Permutation(new int[] {1, 2, 0, 5, 6, 4, 7, 3}),
        new Permutation(new int[] {7, 6, 5, 4, 3, 2, 1, 0})
      }
    };
    final int[][] wrapper = new int[2][];
    for (int k = 0; k < indexes.length; k++) {
      Permutation child1 = new Permutation(p1);
      Permutation child2 = new Permutation(p2);
      child1.apply(raw -> wrapper[0] = raw);
      child2.apply(raw -> wrapper[1] = raw);
      upmx.internalCross(wrapper[0], wrapper[1], child1, child2, indexes[k]);
      assertEquals(expected[k][0], child1);
      assertEquals(expected[k][1], child2);
    }
  }

  @Test
  public void testUPMXValidPermutations() {
    UniformPartiallyMatchedCrossover upmx = new UniformPartiallyMatchedCrossover();
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      upmx.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }

    upmx = new UniformPartiallyMatchedCrossover(0.5);
    for (int n = 1; n <= 32; n *= 2) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      upmx.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
    }
  }

  @Test
  public void testExceptionsUPMX() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UniformPartiallyMatchedCrossover(0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new UniformPartiallyMatchedCrossover(1.0));
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
