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

package org.cicirello.search.representations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test BoundedRealVector class. */
public class BoundedRealVectorTests {

  @Test
  public void testBoundedRealVector() {
    for (int n = 0; n <= 10; n++) {
      double[] initial = new double[n];
      for (int i = 0; i < n; i++) {
        initial[i] = n - i;
      }
      BoundedRealVector f2 = new BoundedRealVector(initial, 1, (n >= 1 ? n : 1) + 1);
      double[] array = f2.toArray(null);
      double[] array2 = new double[n];
      double[] array3 = f2.toArray(array2);
      assertTrue(array2 == array3);
      BoundedRealVector f3 = new BoundedRealVector(initial, 1, (n >= 1 ? n : 1) + 1);
      assertEquals(n, f2.length());
      assertEquals(n, array.length);
      assertEquals(f2, f3);
      assertEquals(f2.hashCode(), f3.hashCode());
      for (int i = 0; i < n; i++) {
        assertEquals(n - i, f2.get(i), 0.0);
        assertEquals((n - i), array[i], 0.0);
        assertEquals((n - i), array3[i], 0.0);
        f3.set(i, n + 1);
        assertEquals(n + 1, f3.get(i), 0.0);
        assertNotEquals(f2, f3);
      }
      BoundedRealVector copy = new BoundedRealVector(f2);
      BoundedRealVector copy2 = f2.copy();
      assertEquals(f2.getClass(), copy2.getClass());
      assertEquals(f2, copy);
      assertEquals(f2, copy2);
      assertTrue(f2 != copy2);
      assertEquals(f2.hashCode(), copy.hashCode());
      assertEquals(f2.hashCode(), copy2.hashCode());
    }
    for (int n = 1; n <= 10; n++) {
      double[] initial = new double[n];
      for (int i = 0; i < n; i++) {
        initial[i] = i;
      }
      BoundedRealVector f = new BoundedRealVector(initial, 2, 5);
      for (int i = 0; i < n; i++) {
        if (i < 2) assertEquals(2, f.get(i), 0.0);
        else if (i < 5) assertEquals(i, f.get(i), 0.0);
        else assertEquals(5, f.get(i), 0.0);
      }
      for (int i = 0; i < n; i++) {
        for (int j = 2; j <= 5; j++) {
          f.set(i, j);
          assertEquals(j, f.get(i), 0.0);
        }
        f.set(i, 1);
        assertEquals(2, f.get(i), 0.0);
        f.set(i, 6);
        assertEquals(5, f.get(i), 0.0);
      }
    }
    final double[] values = {3, 4, 3, 4};
    BoundedRealVector f1 = new BoundedRealVector(values, 1, 10);
    BoundedRealVector f2 = new BoundedRealVector(values, 2, 10);
    BoundedRealVector f3 = new BoundedRealVector(values, 1, 9);
    assertFalse(f1.sameBounds(f2));
    assertFalse(f1.sameBounds(f3));
    assertTrue(f1.sameBounds(new BoundedRealVector(values, 1, 10)));
    assertNotEquals(f1, f2);
    assertNotEquals(f1, f3);
    assertFalse(f1.equals(null));
    RealVector nonBounded = new RealVector(values);
    assertFalse(f1.equals(nonBounded));
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoundedRealVector(values, 1.0001, 1));

    double[] changed = {0, 4, 8, 12};
    f3.set(changed);
    double[] expected = {1, 4, 8, 9};
    assertArrayEquals(expected, f3.toArray(null));
  }

  @Test
  public void testExchangeBoundedRealVector() {
    for (int n = 1; n <= 8; n *= 2) {
      for (int first = 0; first < n; first++) {
        for (int last = first; last < n; last++) {
          double[] raw1 = new double[n];
          double[] raw2 = new double[n];
          for (int i = 0; i < n; i++) {
            raw1[i] = 100 + i;
            raw2[i] = 200 + i;
          }
          BoundedRealVector v1 = new BoundedRealVector(raw1, 0, 300);
          BoundedRealVector v2 = new BoundedRealVector(raw2, 0, 300);
          RealVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v1.get(i), 0.0);
            assertEquals(raw1[i], v2.get(i), 0.0);
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }

          // First not in bounds of second
          v1 = new BoundedRealVector(raw1, 100, 199);
          v2 = new BoundedRealVector(raw2, 0, 300);
          RealVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }
          for (int i = first; i <= last; i++) {
            assertEquals(199, v1.get(i), 0.0);
            assertEquals(raw1[i], v2.get(i), 0.0);
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }

          // Second not in bounds of first
          v1 = new BoundedRealVector(raw1, 0, 300);
          v2 = new BoundedRealVector(raw2, 200, 300);
          RealVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v1.get(i), 0.0);
            assertEquals(200, v2.get(i), 0.0);
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }

          // second is not bounded
          v1 = new BoundedRealVector(raw1, 100, 199);
          RealVector v3 = new RealVector(raw2);
          RealVector.exchange(v1, v3, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v3.get(i), 0.0);
          }
          for (int i = first; i <= last; i++) {
            assertEquals(199, v1.get(i), 0.0);
            assertEquals(raw1[i], v3.get(i), 0.0);
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i), 0.0);
            assertEquals(raw2[i], v3.get(i), 0.0);
          }

          // First is not bounded
          v3 = new RealVector(raw1);
          v2 = new BoundedRealVector(raw2, 200, 300);
          RealVector.exchange(v3, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v3.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v3.get(i), 0.0);
            assertEquals(200, v2.get(i), 0.0);
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v3.get(i), 0.0);
            assertEquals(raw2[i], v2.get(i), 0.0);
          }
        }
      }
    }
  }
}
