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

/** JUnit test RealVector class. */
public class RealVectorTests {

  @Test
  public void testMultivariate() {
    for (int n = 0; n <= 10; n++) {
      RealVector f = new RealVector(n);
      assertEquals(n, f.length());
      double[] array = f.toArray(null);
      assertEquals(n, array.length);
      for (int i = 0; i < n; i++) {
        assertEquals(0.0, f.get(i));
        assertEquals(0.0, array[i]);
      }
      double[] initial = new double[n];
      for (int i = 0; i < n; i++) {
        initial[i] = n - i;
      }
      RealVector f2 = new RealVector(initial);
      array = f2.toArray(null);
      double[] array2 = new double[n];
      double[] array3 = f2.toArray(array2);
      assertTrue(array2 == array3);
      RealVector f3 = new RealVector(initial);
      assertEquals(n, f2.length());
      assertEquals(n, array.length);
      assertEquals(f2, f3);
      assertEquals(f2.hashCode(), f3.hashCode());
      if (n > 1) assertNotEquals(f2, f);
      for (int i = 0; i < n; i++) {
        f.set(i, (double) (n - i));
        assertEquals((double) (n - i), f2.get(i));
        assertEquals((double) (n - i), array[i]);
        assertEquals((double) (n - i), array3[i]);
        assertEquals((double) (n - i), f.get(i));
        f3.set(i, 100.0);
        assertNotEquals(f2, f3);
      }
      double[] changed = new double[initial.length];
      for (int i = 0; i < changed.length; i++) {
        changed[i] = (i + 1) * 5;
      }
      f3.set(changed.clone());
      assertArrayEquals(changed, f3.toArray(null));
      assertEquals(f2, f);
      assertEquals(f2.hashCode(), f.hashCode());
      RealVector copy = new RealVector(f2);
      RealVector copy2 = f2.copy();
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
  public void testExchangeRealVector() {
    for (int n = 1; n <= 8; n *= 2) {
      for (int first = 0; first < n; first++) {
        for (int last = first; last < n; last++) {
          double[] raw1 = new double[n];
          double[] raw2 = new double[n];
          for (int i = 0; i < n; i++) {
            raw1[i] = 100 + i;
            raw2[i] = 200 + i;
          }
          RealVector v1 = new RealVector(raw1);
          RealVector v2 = new RealVector(raw2);
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

          // try first > last case
          if (first != last) {
            v1 = new RealVector(raw1);
            v2 = new RealVector(raw2);
            RealVector.exchange(v1, v2, last, first);
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
          }
        }
      }
    }
  }
}
