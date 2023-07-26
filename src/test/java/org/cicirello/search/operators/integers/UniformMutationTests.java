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

package org.cicirello.search.operators.integers;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.representations.IntegerValued;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.representations.SingleInteger;
import org.junit.jupiter.api.*;

/**
 * JUnit test cases for the classes that implement different variations of Uniform mutation for
 * mutating integer valued representations.
 */
public class UniformMutationTests {

  private final UniformMutationValidator validator;

  public UniformMutationTests() {
    validator = new UniformMutationValidator();
  }

  // During refactoring, removed unnecessary equals and hashCode methods
  // that nobody should ever want to use. If restored, uncomment next line
  // to restore tests.
  // @Test
  public void testEquals() {
    UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation(1);
    UniformMutation<IntegerValued> g2 = UniformMutation.createUniformMutation(1);
    UniformMutation<IntegerValued> g3 = UniformMutation.createUniformMutation(2);
    assertEquals(g1, g2);
    assertEquals(g1.hashCode(), g2.hashCode());
    assertNotEquals(g1, g3);
    assertFalse(g1.equals(null));
    assertFalse(g1.equals("hello"));
    UniformMutation<IntegerValued> p1 = UniformMutation.createUniformMutation(1, 1);
    UniformMutation<IntegerValued> p2 = UniformMutation.createUniformMutation(2, 1);
    UniformMutation<IntegerValued> p3 = UniformMutation.createUniformMutation(1, 2);
    UniformMutation<IntegerValued> p4 = UniformMutation.createUniformMutation(1, 0.5);
    UniformMutation<IntegerValued> p5 = UniformMutation.createUniformMutation(1, 0.25);
    assertNotEquals(p1, p2);
    assertNotEquals(p1, p3);
    assertNotEquals(p4, p5);
    assertFalse(p1.equals(null));
    assertFalse(p1.equals("hello"));
    assertFalse(p1.equals(g1));
  }

  // During refactoring, removed unnecessary equals and hashCode methods
  // that nobody should ever want to use. If restored, uncomment next line
  // to restore tests.
  // @Test
  public void testEqualsUndoable() {
    UndoableUniformMutation p1 = UndoableUniformMutation.createUniformMutation(1, 1);
    UndoableUniformMutation p2 = UndoableUniformMutation.createUniformMutation(2, 1);
    UndoableUniformMutation p3 = UndoableUniformMutation.createUniformMutation(1, 2);
    UndoableUniformMutation p4 = UndoableUniformMutation.createUniformMutation(1, 0.5);
    UndoableUniformMutation p5 = UndoableUniformMutation.createUniformMutation(1, 0.25);
    assertNotEquals(p1, p2);
    assertNotEquals(p1, p3);
    assertNotEquals(p4, p5);
    assertFalse(p1.equals(null));
    assertFalse(p1.equals("hello"));
    UndoableUniformMutation g1 = UndoableUniformMutation.createUniformMutation(1);
    assertFalse(p1.equals(g1));
  }

  @Test
  public void testToArray() {
    UniformMutation<IntegerValued> u = UniformMutation.createUniformMutation(2);
    int[] a = u.toArray(null);
    assertEquals(1, a.length);
    assertEquals(2, a[0]);
    a = u.toArray(new int[2]);
    assertEquals(1, a.length);
    assertEquals(2, a[0]);
    a[0] = 5;
    int[] b = a;
    a = u.toArray(a);
    assertEquals(1, a.length);
    assertEquals(2, a[0]);
    assertTrue(a == b);
  }

  @Test
  public void testUniformMutation1() {
    UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation();
    assertEquals(1, g1.get(0));
    validator.verifyMutate1(g1);

    UniformMutation<IntegerValued> g5 = UniformMutation.createUniformMutation(5);
    assertEquals(5, g5.get(0));
    validator.verifyMutate1(g5);

    UndoableUniformMutation<IntegerValued> g1u = UndoableUniformMutation.createUniformMutation();
    assertEquals(1, g1u.get(0));
    validator.verifyMutate1(g1u);
    validator.verifyUndo(g1u);
    validator.verifySplitUndo(g1u);

    UndoableUniformMutation<IntegerValued> g5u = UndoableUniformMutation.createUniformMutation(5);
    assertEquals(5, g5u.get(0));
    validator.verifyMutate1(g5u);
    validator.verifyUndo(g5u);
    validator.verifySplitUndo(g5u);

    UndoableUniformMutation<IntegerValued> g5copyU = g5u.split();
    assertEquals(5, g5copyU.get(0));
    // assertEquals(g5u, g5copyU);
    // assertEquals(g5u.hashCode(), g5copyU.hashCode());
    validator.verifyMutate1(g5copyU);
    validator.verifyUndo(g5copyU);
    validator.verifySplitUndo(g5copyU);

    g5copyU = g5u.copy();
    assertEquals(5, g5copyU.get(0));
    // assertEquals(g5u, g5copyU);
    // assertEquals(g5u.hashCode(), g5copyU.hashCode());
    validator.verifyMutate1(g5copyU);
    validator.verifyUndo(g5copyU);
    validator.verifySplitUndo(g5copyU);

    UniformMutation<IntegerValued> g5split = g5.split();
    assertEquals(5, g5split.get(0));
    // assertEquals(g5, g5split);
    assertTrue(g5 != g5split);
    // assertEquals(g5.hashCode(), g5split.hashCode());
    validator.verifyMutate1(g5split);

    UniformMutation<IntegerValued> g5copyM = g5.copy();
    assertEquals(5, g5copyM.get(0));
    // assertEquals(g5, g5copyM);
    assertTrue(g5 != g5copyM);
    // assertEquals(g5.hashCode(), g5copyM.hashCode());
    validator.verifyMutate1(g5copyM);

    UniformMutation<IntegerValued> g3 = g5;
    g3.set(0, 3);
    assertEquals(3, g3.get(0));
    validator.verifyMutate1(g3);

    g3.set(new int[] {7});
    assertEquals(7, g3.get(0));
  }

  @Test
  public void testPartialUniformMutation1() {
    for (int k = 1; k <= 8; k++) {
      UndoableUniformMutation<IntegerValued> g1 =
          UndoableUniformMutation.createUniformMutation(1, k);
      assertEquals(1, g1.get(0));
      validator.verifyMutate1(g1, k);
      validator.verifyUndo(g1);
      validator.verifySplitUndo(g1);

      UndoableUniformMutation<IntegerValued> g5 =
          UndoableUniformMutation.createUniformMutation(5, k);
      assertEquals(5, g5.get(0));
      validator.verifyMutate1(g5, k);
      validator.verifyUndo(g5);
      validator.verifySplitUndo(g5);

      UniformMutation<IntegerValued> g5dis = UniformMutation.createUniformMutation(5, k);
      assertEquals(5, g5dis.get(0));
      assertNotEquals(g5, g5dis);
      validator.verifyMutate1(g5dis, k);

      UndoableUniformMutation<IntegerValued> g5copy = g5.copy();
      assertEquals(5, g5copy.get(0));
      // assertEquals(g5, g5copy);
      // assertEquals(g5.hashCode(), g5copy.hashCode());
      validator.verifyMutate1(g5copy, k);
      validator.verifyUndo(g5copy);
      validator.verifySplitUndo(g5copy);

      UniformMutation<IntegerValued> g5copyDis = g5dis.copy();
      assertEquals(5, g5copyDis.get(0));
      // assertEquals(g5dis, g5copyDis);
      // assertEquals(g5dis.hashCode(), g5copyDis.hashCode());
      validator.verifyMutate1(g5copyDis, k);

      UndoableUniformMutation<IntegerValued> g5split = g5.split();
      assertEquals(5, g5split.get(0));
      // assertEquals(g5, g5split);
      assertTrue(g5 != g5split);
      // assertEquals(g5.hashCode(), g5split.hashCode());
      validator.verifyMutate1(g5split, k);
      validator.verifyUndo(g5split);
      validator.verifySplitUndo(g5split);

      UniformMutation<IntegerValued> g5splitDis = g5dis.split();
      assertEquals(5, g5splitDis.get(0));
      // assertEquals(g5dis, g5splitDis);
      assertTrue(g5dis != g5splitDis);
      // assertEquals(g5dis.hashCode(), g5splitDis.hashCode());
      validator.verifyMutate1(g5splitDis, k);

      UndoableUniformMutation<IntegerValued> g3 = g5;
      g3.set(0, 3);
      assertEquals(3, g3.get(0));
      validator.verifyMutate1(g3, k);
      validator.verifyUndo(g3);
      validator.verifySplitUndo(g3);
    }
    for (double k = 0.25; k <= 1.1; k += 0.25) {
      UndoableUniformMutation<IntegerValued> g1 =
          UndoableUniformMutation.createUniformMutation(1, k);
      assertEquals(1, g1.get(0));
      validator.verifyMutate1(g1, k);
      validator.verifyUndo(g1);
      validator.verifySplitUndo(g1);

      UndoableUniformMutation<IntegerValued> g5 =
          UndoableUniformMutation.createUniformMutation(5, k);
      assertEquals(5, g5.get(0));
      validator.verifyMutate1(g5, k);
      validator.verifyUndo(g5);
      validator.verifySplitUndo(g5);

      UniformMutation<IntegerValued> g5dis = UniformMutation.createUniformMutation(5, k);
      assertEquals(5, g5dis.get(0));
      assertNotEquals(g5, g5dis);
      validator.verifyMutate1(g5dis, k);

      UndoableUniformMutation<IntegerValued> g5copy = g5.copy();
      assertEquals(5, g5copy.get(0));
      // assertEquals(g5, g5copy);
      // assertEquals(g5.hashCode(), g5copy.hashCode());
      validator.verifyMutate1(g5copy, k);
      validator.verifyUndo(g5copy);
      validator.verifySplitUndo(g5copy);

      UniformMutation<IntegerValued> g5copyDis = g5dis.copy();
      assertEquals(5, g5copyDis.get(0));
      // assertEquals(g5dis, g5copyDis);
      // assertEquals(g5dis.hashCode(), g5copyDis.hashCode());
      validator.verifyMutate1(g5copyDis, k);

      UndoableUniformMutation<IntegerValued> g5split = g5.split();
      assertEquals(5, g5split.get(0));
      // assertEquals(g5, g5split);
      assertTrue(g5 != g5split);
      // assertEquals(g5.hashCode(), g5split.hashCode());
      validator.verifyMutate1(g5split, k);
      validator.verifyUndo(g5split);
      validator.verifySplitUndo(g5split);

      UniformMutation<IntegerValued> g5splitDis = g5dis.split();
      assertEquals(5, g5splitDis.get(0));
      // assertEquals(g5dis, g5splitDis);
      assertTrue(g5dis != g5splitDis);
      // assertEquals(g5dis.hashCode(), g5splitDis.hashCode());
      validator.verifyMutate1(g5splitDis, k);

      UndoableUniformMutation<IntegerValued> g3 = g5;
      g3.set(0, 3);
      assertEquals(3, g3.get(0));
      validator.verifyMutate1(g3, k);
      validator.verifyUndo(g3);
      validator.verifySplitUndo(g3);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> UniformMutation.createUniformMutation(1, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> UniformMutation.createUniformMutation(1, 0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1, 0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> UniformMutation.createUniformMutation(1, 2, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> UndoableUniformMutation.createUniformMutation(1, 2, 1));
  }

  @Test
  public void testConstrainedUniformMutation() {
    {
      UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation(1, -10, 10);
      assertEquals(1, g1.get(0));
      validator.verifyMutate1(g1);

      UniformMutation<IntegerValued> g5 = UniformMutation.createUniformMutation(5, -10, 10);
      assertEquals(5, g5.get(0));
      validator.verifyMutate1(g5);

      UniformMutation<IntegerValued> g = UniformMutation.createUniformMutation(1, 2, 5);
      IntegerVector r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2, r.get(0));
      assertEquals(2, r.get(1));
      assertEquals(2, r.get(2));
      assertTrue(r.get(3) >= 2 && r.get(3) <= 5);
      assertTrue(r.get(4) >= 2 && r.get(4) <= 5);
      assertEquals(5, r.get(5));
      assertEquals(5, r.get(6));
      assertEquals(5, r.get(7));
      g = UniformMutation.createUniformMutation(1, 4, 4);
      r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4, r.get(i));
      }
    }
    // copy
    {
      UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation(1, -10, 10).copy();
      assertEquals(1, g1.get(0));
      validator.verifyMutate1(g1);

      UniformMutation<IntegerValued> g5 = UniformMutation.createUniformMutation(5, -10, 10).copy();
      assertEquals(5, g5.get(0));
      validator.verifyMutate1(g5);

      UniformMutation<IntegerValued> g = UniformMutation.createUniformMutation(1, 2, 5).copy();
      IntegerVector r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2, r.get(0));
      assertEquals(2, r.get(1));
      assertEquals(2, r.get(2));
      assertTrue(r.get(3) >= 2 && r.get(3) <= 5);
      assertTrue(r.get(4) >= 2 && r.get(4) <= 5);
      assertEquals(5, r.get(5));
      assertEquals(5, r.get(6));
      assertEquals(5, r.get(7));
      g = UniformMutation.createUniformMutation(1, 4, 4).copy();
      r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4, r.get(i));
      }
    }
    // split
    {
      UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation(1, -10, 10).split();
      assertEquals(1, g1.get(0));
      validator.verifyMutate1(g1);

      UniformMutation<IntegerValued> g5 = UniformMutation.createUniformMutation(5, -10, 10).split();
      assertEquals(5, g5.get(0));
      validator.verifyMutate1(g5);

      UniformMutation<IntegerValued> g = UniformMutation.createUniformMutation(1, 2, 5).split();
      IntegerVector r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      assertEquals(2, r.get(0));
      assertEquals(2, r.get(1));
      assertEquals(2, r.get(2));
      assertTrue(r.get(3) >= 2 && r.get(3) <= 5);
      assertTrue(r.get(4) >= 2 && r.get(4) <= 5);
      assertEquals(5, r.get(5));
      assertEquals(5, r.get(6));
      assertEquals(5, r.get(7));
      g = UniformMutation.createUniformMutation(1, 4, 4).split();
      r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
      g.mutate(r);
      for (int i = 0; i < 7; i++) {
        assertEquals(4, r.get(i));
      }
    }
  }

  @Test
  public void testUndoableConstrainedUniformMutation() {
    UndoableUniformMutation<IntegerValued> g1 =
        UndoableUniformMutation.createUniformMutation(1, -10, 10);
    assertEquals(1, g1.get(0));
    validator.verifyMutate1(g1);
    validator.verifyUndo(g1);

    UndoableUniformMutation<IntegerValued> g5 =
        UndoableUniformMutation.createUniformMutation(5, -10, 10);
    assertEquals(5, g5.get(0));
    validator.verifyMutate1(g5);
    validator.verifyUndo(g5);

    UndoableUniformMutation<IntegerValued> g =
        UndoableUniformMutation.createUniformMutation(1, 2, 5);
    IntegerVector r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
    g.mutate(r);
    assertEquals(2, r.get(0));
    assertEquals(2, r.get(1));
    assertEquals(2, r.get(2));
    assertTrue(r.get(3) >= 2 && r.get(3) <= 5);
    assertTrue(r.get(4) >= 2 && r.get(4) <= 5);
    assertEquals(5, r.get(5));
    assertEquals(5, r.get(6));
    assertEquals(5, r.get(7));
    g = UndoableUniformMutation.createUniformMutation(1, 4, 4);
    r = new IntegerVector(new int[] {-1000, -100, -50, 0, 3, 50, 100, 1000});
    g.mutate(r);
    for (int i = 0; i < 7; i++) {
      assertEquals(4, r.get(i));
    }
  }

  private static class UniformMutationValidator {

    // We don't test the distribution of results.
    // Instead, we simply verify that mutation is capable of both increasing
    // and decreasing values.  This constant controls the max number of trials
    // executed in verifying this.  E.g., pass if at least 1 out of MAX_TRIALS
    // increases value, and if at least 1 out of MAX_TRIALS decreases.
    // A Uniform is symmetric about 0, so approximately half of mutations
    // should decrease and approximately half should increase.
    private static final int MAX_TRIALS = 100;

    private void verifySplitUndo(UndoableUniformMutation<IntegerValued> mutationOriginal) {
      for (int i = 0; i < 10; i++) {
        SingleInteger v1 = new SingleInteger(9);
        SingleInteger v2 = v1.copy();
        SingleInteger v3 = v1.copy();
        mutationOriginal.mutate(v2);
        UndoableUniformMutation<IntegerValued> mutation = mutationOriginal.split();
        mutation.mutate(v3);
        mutationOriginal.undo(v2);
        assertEquals(v1, v2);
        mutation.undo(v3);
        assertEquals(v1, v3);
      }
      for (int i = 0; i < 10; i++) {
        int[] vector = {2, 4, 8, 16, 32, 64, 128, 256};
        IntegerVector v1 = new IntegerVector(vector);
        IntegerVector v2 = v1.copy();
        IntegerVector v3 = v1.copy();
        mutationOriginal.mutate(v2);
        UndoableUniformMutation<IntegerValued> mutation = mutationOriginal.split();
        mutation.mutate(v3);
        mutationOriginal.undo(v2);
        assertEquals(v1, v2);
        mutation.undo(v3);
        assertEquals(v1, v3);
      }
    }

    private void verifyMutate1(AbstractIntegerMutation<IntegerValued> m) {
      int countLow = 0;
      int countHigh = 0;
      for (int i = 0; i < MAX_TRIALS && (countLow == 0 || countHigh == 0); i++) {
        SingleInteger f = new SingleInteger(9);
        m.mutate(f);
        assertTrue(Math.abs(9 - f.get()) <= m.get(0));
        if (f.get() < 9) countLow++;
        else if (f.get() > 9) countHigh++;
      }
      assertTrue(countLow > 0);
      assertTrue(countHigh > 0);
      for (int j = 0; j < 5; j++) {
        int[] v = new int[j];
        for (int k = 0; k < j; k++) {
          v[k] = 9 - k;
        }
        int[] low = new int[j];
        int[] high = new int[j];
        for (int i = 0; i < MAX_TRIALS; i++) {
          IntegerVector f = new IntegerVector(v.clone());
          m.mutate(f);
          boolean done = true;
          for (int k = 0; k < j; k++) {
            assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
            if (f.get(k) < v[k]) low[k]++;
            if (f.get(k) > v[k]) high[k]++;
            if (low[k] == 0 || high[k] == 0) done = false;
          }
          if (done) break;
        }
        for (int k = 0; k < j; k++) {
          assertTrue(low[k] > 0);
          assertTrue(high[k] > 0);
        }
      }
    }

    private void verifyMutate1(AbstractIntegerMutation<IntegerValued> m, double p) {
      int countLow = 0;
      int countHigh = 0;
      final int TRIALS = (int) (2 * MAX_TRIALS / p);
      for (int i = 0; i < TRIALS && (countLow == 0 || countHigh == 0); i++) {
        SingleInteger f = new SingleInteger(9);
        m.mutate(f);
        assertTrue(Math.abs(9 - f.get()) <= m.get(0));
        if (f.get() < 9) countLow++;
        else if (f.get() > 9) countHigh++;
      }
      assertTrue(countLow > 0);
      assertTrue(countHigh > 0);
      final int N = m.length() > 3 ? 3 + m.length() : 6;
      for (int j = 0; j < N; j++) {
        int[] v = new int[j];
        for (int k = 0; k < j; k++) {
          v[k] = 9 - k;
        }
        final int z = j;
        int[] low = new int[z];
        int[] high = new int[z];
        for (int i = 0; i < TRIALS; i++) {
          IntegerVector f = new IntegerVector(v.clone());
          m.mutate(f);
          boolean done = true;
          for (int k = 0; k < j; k++) {
            assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
            if (f.get(k) < v[k]) low[k]++;
            if (f.get(k) > v[k]) high[k]++;
            if (low[k] == 0 || high[k] == 0) done = false;
          }
          if (done) break;
        }
        for (int k = 0; k < low.length; k++) {
          assertTrue(low[k] > 0);
          assertTrue(high[k] > 0);
        }
      }
    }

    private void verifyMutate1(AbstractIntegerMutation<IntegerValued> m, int K) {
      int countLow = 0;
      int countHigh = 0;
      for (int i = 0; i < MAX_TRIALS && (countLow == 0 || countHigh == 0); i++) {
        SingleInteger f = new SingleInteger(9);
        m.mutate(f);
        assertTrue(Math.abs(9 - f.get()) <= m.get(0));
        if (f.get() < 9) countLow++;
        else if (f.get() > 9) countHigh++;
      }
      assertTrue(countLow > 0);
      assertTrue(countHigh > 0);
      final int N = m.length() > 3 ? 3 + m.length() : 6;
      for (int j = 0; j < N; j++) {
        int[] v = new int[j];
        for (int k = 0; k < j; k++) {
          v[k] = 9 - k;
        }
        final int z = j;
        int[] low = new int[z];
        int[] high = new int[z];
        final int TRIALS = (int) (2 * MAX_TRIALS / (K < j ? 1.0 * K / j : 1.0));
        for (int i = 0; i < TRIALS; i++) {
          IntegerVector f = new IntegerVector(v.clone());
          m.mutate(f);
          boolean done = true;
          int kCount = 0;
          for (int k = 0; k < j; k++) {
            assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
            if (f.get(k) < v[k]) {
              low[k]++;
              kCount++;
            }
            if (f.get(k) > v[k]) {
              high[k]++;
              kCount++;
            }
            if (low[k] == 0 || high[k] == 0) done = false;
          }
          assertTrue(kCount <= K);
          if (done) break;
        }
        for (int k = 0; k < low.length; k++) {
          assertTrue(low[k] > 0);
          assertTrue(high[k] > 0);
        }
      }
    }

    private void verifyUndo(UndoableUniformMutation<IntegerValued> m) {
      boolean changed = false;
      for (int i = 0; i < MAX_TRIALS; i++) {
        SingleInteger f = new SingleInteger(9);
        SingleInteger f2 = f.copy();
        m.mutate(f);
        if (!f.equals(f2)) {
          changed = true;
        }
        m.undo(f);
        assertEquals(f2, f);
      }
      assertTrue(changed);
      for (int j = 0; j < 5; j++) {
        int[] v = new int[j];
        for (int k = 0; k < j; k++) {
          v[k] = 9 - k;
        }
        changed = false;
        for (int i = 0; i < MAX_TRIALS; i++) {
          IntegerVector f = new IntegerVector(v.clone());
          IntegerVector f2 = f.copy();
          m.mutate(f);
          if (!f.equals(f2)) {
            changed = true;
          }
          m.undo(f);
          assertEquals(f2, f);
        }
      }
    }
  }
}
