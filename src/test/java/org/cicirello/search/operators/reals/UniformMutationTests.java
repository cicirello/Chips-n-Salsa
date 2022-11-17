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

package org.cicirello.search.operators.reals;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.representations.RealValued;
import org.cicirello.search.representations.RealVector;
import org.junit.jupiter.api.*;

/**
 * JUnit test cases for the classes that implement different variations of Uniform mutation for
 * mutating floating-point function parameters.
 */
public class UniformMutationTests extends SharedTestRealMutationOps {

  // precision used in floating-point comparisons
  private static final double EPSILON = 1e-10;

  @Test
  public void testToArray() {
    UniformMutation<RealValued> u = UniformMutation.createUniformMutation(2);
    double[] a = u.toArray(null);
    assertEquals(1, a.length);
    assertEquals(2.0, a[0], EPSILON);
    a = u.toArray(new double[2]);
    assertEquals(1, a.length);
    assertEquals(2.0, a[0], EPSILON);
    a[0] = 5;
    double[] b = a;
    a = u.toArray(a);
    assertEquals(1, a.length);
    assertEquals(2.0, a[0], EPSILON);
    assertTrue(a == b);
  }

  @Test
  public void testUniformMutation1() {
    UniformMutation<RealValued> g1 = UniformMutation.createUniformMutation();
    assertEquals(1.0, g1.get(0), EPSILON);
    verifyMutate1(g1, true);

    UniformMutation<RealValued> g5 = UniformMutation.createUniformMutation(5.0);
    assertEquals(5.0, g5.get(0), EPSILON);
    verifyMutate1(g5, true);

    UndoableUniformMutation<RealValued> g1u = UndoableUniformMutation.createUniformMutation();
    assertEquals(1.0, g1u.get(0), EPSILON);
    verifyMutate1(g1u, true);
    verifyUndo(g1u);

    UndoableUniformMutation<RealValued> g5u = UndoableUniformMutation.createUniformMutation(5.0);
    assertEquals(5.0, g5u.get(0), EPSILON);
    verifyMutate1(g5u, true);
    verifyUndo(g5u);

    UndoableUniformMutation<RealValued> g5copyU = g5u.split();
    assertEquals(5.0, g5copyU.get(0), EPSILON);
    assertEquals(g5u.get(0), g5copyU.get(0));
    verifyMutate1(g5copyU, true);
    verifyUndo(g5copyU);

    g5copyU = g5u.copy();
    assertEquals(5.0, g5copyU.get(0), EPSILON);
    assertEquals(g5u.get(0), g5copyU.get(0));
    verifyMutate1(g5copyU, true);
    verifyUndo(g5copyU);

    UniformMutation<RealValued> g5split = g5.split();
    assertEquals(5.0, g5split.get(0), EPSILON);
    assertEquals(g5.get(0), g5split.get(0));
    assertTrue(g5 != g5split);
    verifyMutate1(g5split, true);

    UniformMutation<RealValued> g5copyM = g5.copy();
    assertEquals(5.0, g5copyM.get(0), EPSILON);
    assertEquals(g5.get(0), g5copyM.get(0));
    assertTrue(g5 != g5copyM);
    verifyMutate1(g5copyM, true);

    UniformMutation<RealValued> g3 = g5;
    g3.set(0, 3.0);
    assertEquals(3.0, g3.get(0), EPSILON);
    verifyMutate1(g3, true);

    g3.set(new double[] {7});
    assertEquals(7.0, g3.get(0));
  }

  @Test
  public void testPartialUniformMutation1() {
    for (int k = 1; k <= 8; k++) {
      UndoableUniformMutation<RealValued> g1 =
          UndoableUniformMutation.createUniformMutation(1.0, k);
      assertEquals(1.0, g1.get(0), EPSILON);
      verifyMutate1(g1, k, true);
      verifyUndo(g1);

      UndoableUniformMutation<RealValued> g5 =
          UndoableUniformMutation.createUniformMutation(5.0, k);
      assertEquals(5.0, g5.get(0), EPSILON);
      verifyMutate1(g5, k, true);
      verifyUndo(g5);

      UniformMutation<RealValued> g5dis = UniformMutation.createUniformMutation(5.0, k);
      assertEquals(5.0, g5dis.get(0), EPSILON);
      assertNotEquals(g5, g5dis);
      verifyMutate1(g5dis, k, true);

      UndoableUniformMutation<RealValued> g5copy = g5.copy();
      assertEquals(5.0, g5copy.get(0), EPSILON);
      assertEquals(g5.get(0), g5copy.get(0));
      verifyMutate1(g5copy, k, true);
      verifyUndo(g5copy);

      UniformMutation<RealValued> g5copyDis = g5dis.copy();
      assertEquals(5.0, g5copyDis.get(0), EPSILON);
      assertEquals(g5dis.get(0), g5copyDis.get(0));
      verifyMutate1(g5copyDis, k, true);

      UndoableUniformMutation<RealValued> g5split = g5.split();
      assertEquals(5.0, g5split.get(0), EPSILON);
      assertEquals(g5.get(0), g5split.get(0));
      assertTrue(g5 != g5split);
      verifyMutate1(g5split, k, true);
      verifyUndo(g5split);

      UniformMutation<RealValued> g5splitDis = g5dis.split();
      assertEquals(5.0, g5splitDis.get(0), EPSILON);
      assertEquals(g5dis.get(0), g5splitDis.get(0));
      assertTrue(g5dis != g5splitDis);
      verifyMutate1(g5splitDis, k, true);

      UndoableUniformMutation<RealValued> g3 = g5;
      g3.set(0, 3.0);
      assertEquals(3.0, g3.get(0), EPSILON);
      verifyMutate1(g3, k, true);
      verifyUndo(g3);
    }
    for (double k = 0.25; k <= 1.1; k += 0.25) {
      UndoableUniformMutation<RealValued> g1 =
          UndoableUniformMutation.createUniformMutation(1.0, k);
      assertEquals(1.0, g1.get(0), EPSILON);
      verifyMutate1(g1, k, true);
      verifyUndo(g1);

      UndoableUniformMutation<RealValued> g5 =
          UndoableUniformMutation.createUniformMutation(5.0, k);
      assertEquals(5.0, g5.get(0), EPSILON);
      verifyMutate1(g5, k, true);
      verifyUndo(g5);

      UniformMutation<RealValued> g5dis = UniformMutation.createUniformMutation(5.0, k);
      assertEquals(5.0, g5dis.get(0), EPSILON);
      assertNotEquals(g5, g5dis);
      verifyMutate1(g5dis, k, true);

      UndoableUniformMutation<RealValued> g5copy = g5.copy();
      assertEquals(5.0, g5copy.get(0), EPSILON);
      assertEquals(g5.get(0), g5copy.get(0));
      verifyMutate1(g5copy, k, true);
      verifyUndo(g5copy);

      UniformMutation<RealValued> g5copyDis = g5dis.copy();
      assertEquals(5.0, g5copyDis.get(0), EPSILON);
      assertEquals(g5dis.get(0), g5copyDis.get(0));
      verifyMutate1(g5copyDis, k, true);

      UndoableUniformMutation<RealValued> g5split = g5.split();
      assertEquals(5.0, g5split.get(0), EPSILON);
      assertEquals(g5.get(0), g5split.get(0));
      assertTrue(g5 != g5split);
      verifyMutate1(g5split, k, true);
      verifyUndo(g5split);

      UniformMutation<RealValued> g5splitDis = g5dis.split();
      assertEquals(5.0, g5splitDis.get(0), EPSILON);
      assertEquals(g5dis.get(0), g5splitDis.get(0));
      assertTrue(g5dis != g5splitDis);
      verifyMutate1(g5splitDis, k, true);

      UndoableUniformMutation<RealValued> g3 = g5;
      g3.set(0, 3.0);
      assertEquals(3.0, g3.get(0), EPSILON);
      verifyMutate1(g3, k, true);
      verifyUndo(g3);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> UniformMutation.createUniformMutation(1.0, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> UniformMutation.createUniformMutation(1.0, 0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1.0, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1.0, 0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UniformMutation.createUniformMutation(1.0, 2.0, 2.0 - Math.ulp(2.0)));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1.0, 2.0, 2.0 - Math.ulp(2.0)));
  }

  @Test
  public void testConstrainedUniformMutation() {
    {
      UniformMutation<RealValued> g1 = UniformMutation.createUniformMutation(1.0, -10.0, 10.0);
      assertEquals(1.0, g1.get(0));
      verifyMutate1(g1);

      UniformMutation<RealValued> g5 = UniformMutation.createUniformMutation(5.0, -10.0, 10.0);
      assertEquals(5.0, g5.get(0));
      verifyMutate1(g5);

      UniformMutation<RealValued> g = UniformMutation.createUniformMutation(1.0, 2.0, 5.0);
      RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2.0, r.get(0));
      assertEquals(2.0, r.get(1));
      assertEquals(2.0, r.get(2));
      assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
      assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
      assertEquals(5.0, r.get(5));
      assertEquals(5.0, r.get(6));
      assertEquals(5.0, r.get(7));
      g = UniformMutation.createUniformMutation(1.0, 4.0, 4.0);
      r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4.0, r.get(i));
      }
    }
    // copy
    {
      UniformMutation<RealValued> g1 =
          UniformMutation.createUniformMutation(1.0, -10.0, 10.0).copy();
      assertEquals(1.0, g1.get(0));
      verifyMutate1(g1);

      UniformMutation<RealValued> g5 =
          UniformMutation.createUniformMutation(5.0, -10.0, 10.0).copy();
      assertEquals(5.0, g5.get(0));
      verifyMutate1(g5);

      UniformMutation<RealValued> g = UniformMutation.createUniformMutation(1.0, 2.0, 5.0).copy();
      RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2.0, r.get(0));
      assertEquals(2.0, r.get(1));
      assertEquals(2.0, r.get(2));
      assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
      assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
      assertEquals(5.0, r.get(5));
      assertEquals(5.0, r.get(6));
      assertEquals(5.0, r.get(7));
      g = UniformMutation.createUniformMutation(1.0, 4.0, 4.0).copy();
      r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4.0, r.get(i));
      }
    }
    // split
    {
      UniformMutation<RealValued> g1 =
          UniformMutation.createUniformMutation(1.0, -10.0, 10.0).split();
      assertEquals(1.0, g1.get(0));
      verifyMutate1(g1);

      UniformMutation<RealValued> g5 =
          UniformMutation.createUniformMutation(5.0, -10.0, 10.0).split();
      assertEquals(5.0, g5.get(0));
      verifyMutate1(g5);

      UniformMutation<RealValued> g = UniformMutation.createUniformMutation(1.0, 2.0, 5.0).split();
      RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2.0, r.get(0));
      assertEquals(2.0, r.get(1));
      assertEquals(2.0, r.get(2));
      assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
      assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
      assertEquals(5.0, r.get(5));
      assertEquals(5.0, r.get(6));
      assertEquals(5.0, r.get(7));
      g = UniformMutation.createUniformMutation(1.0, 4.0, 4.0).split();
      r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4.0, r.get(i));
      }
    }
  }

  @Test
  public void testUndoableConstrainedUniformMutation() {
    UndoableUniformMutation<RealValued> g1 =
        UndoableUniformMutation.createUniformMutation(1.0, -10.0, 10.0);
    assertEquals(1.0, g1.get(0));
    verifyMutate1(g1);
    verifyUndo(g1);

    UndoableUniformMutation<RealValued> g5 =
        UndoableUniformMutation.createUniformMutation(5.0, -10.0, 10.0);
    assertEquals(5.0, g5.get(0));
    verifyMutate1(g5);
    verifyUndo(g5);

    UndoableUniformMutation<RealValued> g =
        UndoableUniformMutation.createUniformMutation(1.0, 2.0, 5.0);
    RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
    g.mutate(r);
    assertEquals(2.0, r.get(0));
    assertEquals(2.0, r.get(1));
    assertEquals(2.0, r.get(2));
    assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
    assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
    assertEquals(5.0, r.get(5));
    assertEquals(5.0, r.get(6));
    assertEquals(5.0, r.get(7));
    g = UndoableUniformMutation.createUniformMutation(1.0, 4.0, 4.0);
    r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
    g.mutate(r);
    for (int i = 0; i < 7; i++) {
      assertEquals(4.0, r.get(i));
    }
  }
}
