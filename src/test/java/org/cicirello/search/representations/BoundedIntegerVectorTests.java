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

/** JUnit test BoundedIntegerVector class. */
public class BoundedIntegerVectorTests {

  @Test
  public void testBoundedIntegerVector() {
    for (int n = 0; n <= 10; n++) {
      int[] initial = new int[n];
      for (int i = 0; i < n; i++) {
        initial[i] = n - i;
      }
      BoundedIntegerVector f2 = new BoundedIntegerVector(initial, 1, (n >= 1 ? n : 1) + 1);
      int[] array = f2.toArray(null);
      int[] array2 = new int[n];
      int[] array3 = f2.toArray(array2);
      assertTrue(array2 == array3);
      BoundedIntegerVector f3 = new BoundedIntegerVector(initial, 1, (n >= 1 ? n : 1) + 1);
      assertEquals(n, f2.length());
      assertEquals(n, array.length);
      assertEquals(f2, f3);
      assertEquals(f2.hashCode(), f3.hashCode());
      for (int i = 0; i < n; i++) {
        assertEquals(n - i, f2.get(i));
        assertEquals((n - i), array[i]);
        assertEquals((n - i), array3[i]);
        f3.set(i, n + 1);
        assertEquals(n + 1, f3.get(i));
        assertNotEquals(f2, f3);
      }
      BoundedIntegerVector copy = new BoundedIntegerVector(f2);
      BoundedIntegerVector copy2 = f2.copy();
      assertEquals(f2.getClass(), copy2.getClass());
      assertEquals(f2, copy);
      assertEquals(f2, copy2);
      assertTrue(f2 != copy2);
      assertEquals(f2.hashCode(), copy.hashCode());
      assertEquals(f2.hashCode(), copy2.hashCode());
    }
    for (int n = 1; n <= 10; n++) {
      int[] initial = new int[n];
      for (int i = 0; i < n; i++) {
        initial[i] = i;
      }
      BoundedIntegerVector f = new BoundedIntegerVector(initial, 2, 5);
      for (int i = 0; i < n; i++) {
        if (i < 2) assertEquals(2, f.get(i));
        else if (i < 5) assertEquals(i, f.get(i));
        else assertEquals(5, f.get(i));
      }
      for (int i = 0; i < n; i++) {
        for (int j = 2; j <= 5; j++) {
          f.set(i, j);
          assertEquals(j, f.get(i));
        }
        f.set(i, 1);
        assertEquals(2, f.get(i));
        f.set(i, 6);
        assertEquals(5, f.get(i));
      }
    }
    int[] values = {3, 4, 3, 4};
    BoundedIntegerVector f1 = new BoundedIntegerVector(values, 1, 10);
    BoundedIntegerVector f2 = new BoundedIntegerVector(values, 2, 10);
    BoundedIntegerVector f3 = new BoundedIntegerVector(values, 1, 9);
    assertNotEquals(f1, f2);
    assertNotEquals(f1, f3);
    assertFalse(f1.sameBounds(f2));
    assertFalse(f1.sameBounds(f3));
    assertTrue(f1.sameBounds(new BoundedIntegerVector(values, 1, 10)));
    assertFalse(f1.equals(null));
    IntegerVector nonBounded = new IntegerVector(values);
    assertFalse(f1.equals(nonBounded));
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new BoundedIntegerVector(values, 2, 1));

    int[] changed = {0, 4, 8, 12};
    f3.set(changed);
    int[] expected = {1, 4, 8, 9};
    assertArrayEquals(expected, f3.toArray(null));
  }

  @Test
  public void testExchangeBoundedIntegerVector() {
    for (int n = 1; n <= 8; n *= 2) {
      for (int first = 0; first < n; first++) {
        for (int last = first; last < n; last++) {
          int[] raw1 = new int[n];
          int[] raw2 = new int[n];
          for (int i = 0; i < n; i++) {
            raw1[i] = 100 + i;
            raw2[i] = 200 + i;
          }
          BoundedIntegerVector v1 = new BoundedIntegerVector(raw1, 0, 300);
          BoundedIntegerVector v2 = new BoundedIntegerVector(raw2, 0, 300);
          IntegerVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v1.get(i));
            assertEquals(raw1[i], v2.get(i));
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }

          // First not in bounds of second
          v1 = new BoundedIntegerVector(raw1, 100, 199);
          v2 = new BoundedIntegerVector(raw2, 0, 300);
          IntegerVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }
          for (int i = first; i <= last; i++) {
            assertEquals(199, v1.get(i));
            assertEquals(raw1[i], v2.get(i));
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }

          // Second not in bounds of first
          v1 = new BoundedIntegerVector(raw1, 0, 300);
          v2 = new BoundedIntegerVector(raw2, 200, 300);
          IntegerVector.exchange(v1, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v1.get(i));
            assertEquals(200, v2.get(i));
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v2.get(i));
          }

          // second is not bounded
          v1 = new BoundedIntegerVector(raw1, 100, 199);
          IntegerVector v3 = new IntegerVector(raw2);
          IntegerVector.exchange(v1, v3, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v3.get(i));
          }
          for (int i = first; i <= last; i++) {
            assertEquals(199, v1.get(i));
            assertEquals(raw1[i], v3.get(i));
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v1.get(i));
            assertEquals(raw2[i], v3.get(i));
          }

          // First is not bounded
          v3 = new IntegerVector(raw1);
          v2 = new BoundedIntegerVector(raw2, 200, 300);
          IntegerVector.exchange(v3, v2, first, last);
          for (int i = 0; i < first; i++) {
            assertEquals(raw1[i], v3.get(i));
            assertEquals(raw2[i], v2.get(i));
          }
          for (int i = first; i <= last; i++) {
            assertEquals(raw2[i], v3.get(i));
            assertEquals(200, v2.get(i));
          }
          for (int i = last + 1; i < n; i++) {
            assertEquals(raw1[i], v3.get(i));
            assertEquals(raw2[i], v2.get(i));
          }
        }
      }
    }
  }
}
