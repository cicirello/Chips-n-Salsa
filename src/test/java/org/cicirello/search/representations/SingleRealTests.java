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

/** JUnit test SingleReal class. */
public class SingleRealTests {

  @Test
  public void testUnivariate() {
    SingleReal f0 = new SingleReal();
    assertEquals(0, f0.get());
    SingleReal f5 = new SingleReal(5.0);
    assertEquals(5.0, f5.get());
    assertEquals(0, f0.get(0));
    assertEquals(5.0, f5.get(0));
    double[] array1 = f0.toArray(null);
    assertEquals(0, array1[0]);
    double[] array2 = f5.toArray(null);
    assertEquals(5, array2[0]);
    double[] array3 = f5.toArray(array1);
    assertTrue(array1 == array3);
    assertEquals(5, array3[0]);

    double[] wrongLength = new double[2];
    double[] array4 = f5.toArray(wrongLength);
    assertTrue(array4 != wrongLength);
    assertEquals(1, array4.length);
    assertEquals(5.0, array4[0]);

    SingleReal copy = new SingleReal(f5);
    assertEquals(5.0, copy.get());
    SingleReal copy2 = f5.copy();
    assertEquals(5.0, copy2.get());
    assertTrue(copy2 != f5);
    assertEquals(f5.getClass(), copy2.getClass());
    f0.set(10);
    assertEquals(10.0, f0.get());
    f0.set(0, 8);
    assertEquals(8.0, f0.get(0));
    f0.set(new double[] {42});
    assertEquals(42.0, f0.get());

    assertEquals(f5, copy);
    assertEquals(f5, copy2);
    assertEquals(f5.hashCode(), copy.hashCode());
    assertEquals(f5.hashCode(), copy2.hashCode());

    assertEquals(1, f0.length());
    assertEquals(1, f5.length());

    assertFalse(f5.equals(null));
    assertFalse(f5.equals("hello"));
  }
}
