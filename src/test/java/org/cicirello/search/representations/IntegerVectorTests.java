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

/** JUnit test IntegerVector class. */
public class IntegerVectorTests {

  @Test
  public void testIntegerMultivariate() {
    for (int n = 0; n <= 10; n++) {
      IntegerVector f = new IntegerVector(n);
      assertEquals(n, f.length());
      int[] array = f.toArray(null);
      assertEquals(n, array.length);
      for (int i = 0; i < n; i++) {
        assertEquals(0, f.get(i));
        assertEquals(0, array[i]);
      }
      int[] initial = new int[n];
      for (int i = 0; i < n; i++) {
        initial[i] = n - i;
      }
      IntegerVector f2 = new IntegerVector(initial);
      array = f2.toArray(null);
      int[] array2 = new int[n];
      int[] array3 = f2.toArray(array2);
      assertTrue(array2 == array3);
      IntegerVector f3 = new IntegerVector(initial);
      assertEquals(n, f2.length());
      assertEquals(n, array.length);
      assertEquals(f2, f3);
      assertEquals(f2.hashCode(), f3.hashCode());
      if (n > 1) assertNotEquals(f2, f);
      for (int i = 0; i < n; i++) {
        f.set(i, n - i);
        assertEquals(n - i, f2.get(i));
        assertEquals((n - i), array[i]);
        assertEquals((n - i), array3[i]);
        assertEquals(n - i, f.get(i));
        f3.set(i, 100);
        assertNotEquals(f2, f3);
      }
      int[] changed = new int[initial.length];
      for (int i = 0; i < changed.length; i++) {
        changed[i] = (i + 1) * 5;
      }
      f3.set(changed.clone());
      assertArrayEquals(changed, f3.toArray(null));
      assertEquals(f2, f);
      assertEquals(f2.hashCode(), f.hashCode());
      IntegerVector copy = new IntegerVector(f2);
      IntegerVector copy2 = f2.copy();
      assertEquals(f2.getClass(), copy2.getClass());
      assertEquals(f2, copy);
      assertEquals(f2, copy2);
      assertTrue(f2 != copy2);
      assertEquals(f2.hashCode(), copy.hashCode());
      assertEquals(f2.hashCode(), copy2.hashCode());

      assertFalse(f2.equals(null));
      assertFalse(f2.equals("hello"));
    }
  }

  @Test
  public void testExchangeIntegerVector() {
    for (int n = 1; n <= 8; n *= 2) {
      for (int first = 0; first < n; first++) {
        for (int last = first; last < n; last++) {
          int[] raw1 = new int[n];
          int[] raw2 = new int[n];
          for (int i = 0; i < n; i++) {
            raw1[i] = 100 + i;
            raw2[i] = 200 + i;
          }
          IntegerVector v1 = new IntegerVector(raw1);
          IntegerVector v2 = new IntegerVector(raw2);
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

          // try first > last case
          if (first != last) {
            v1 = new IntegerVector(raw1);
            v2 = new IntegerVector(raw2);
            IntegerVector.exchange(v1, v2, last, first);
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
          }
        }
      }
    }
  }
}
