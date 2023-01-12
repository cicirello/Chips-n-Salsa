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

/** JUnit test cases for the default methods of the numeric representation interfaces. */
public class NumericInterfaceDefaultMethodTests {

  @Test
  public void testDefaultSetIntArray() {
    class Vector implements IntegerValued {

      private int[] a;

      Vector(int[] a) {
        this.a = a.clone();
      }

      @Override
      public int length() {
        return a.length;
      }

      @Override
      public int get(int i) {
        return a[i];
      }

      @Override
      public int[] toArray(int[] values) {
        // not correct in general (shouldn't expose internals, but this is for testing
        return a;
      }

      @Override
      public void set(int i, int value) {
        a[i] = value;
      }
    }
    Vector v = new Vector(new int[] {4, 5, 6});
    v.set(new int[] {7, 8, 9});
    assertArrayEquals(new int[] {7, 8, 9}, v.toArray(null));
  }

  @Test
  public void testDefaultSetDoubleArray() {
    class Vector implements RealValued {

      private double[] a;

      Vector(double[] a) {
        this.a = a.clone();
      }

      @Override
      public int length() {
        return a.length;
      }

      @Override
      public double get(int i) {
        return a[i];
      }

      @Override
      public double[] toArray(double[] values) {
        // not correct in general (shouldn't expose internals, but this is for testing
        return a;
      }

      @Override
      public void set(int i, double value) {
        a[i] = value;
      }
    }
    Vector v = new Vector(new double[] {4, 5, 6});
    v.set(new double[] {7, 8, 9});
    assertArrayEquals(new double[] {7, 8, 9}, v.toArray(null));
  }
}
