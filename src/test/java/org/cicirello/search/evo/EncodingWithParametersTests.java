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

package org.cicirello.search.evo;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for EncodingWithParameters. */
public class EncodingWithParametersTests {

  @Test
  public void testConstructorForceRatesConstant() {
    TestObject obj = new TestObject(5);
    EncodingWithParameters<TestObject> ewp =
        new EncodingWithParameters<TestObject>(obj, 3, 0.4, 0.4 + Math.ulp(0.4));
    assertSame(obj, ewp.getCandidate());
    assertEquals(3, ewp.length());
    for (int i = 0; i < 3; i++) {
      assertEquals(0.4, ewp.getParameter(i).get());
    }
    for (int j = 0; j < 3; j++) {
      ewp.mutate();
      for (int i = 0; i < 3; i++) {
        assertTrue(
            0.4 <= ewp.getParameter(i).get() && ewp.getParameter(i).get() <= 0.4 + Math.ulp(0.4));
      }
    }
  }

  @Test
  public void testConstructorDefaults() {
    TestObject obj = new TestObject(5);
    EncodingWithParameters<TestObject> ewp = new EncodingWithParameters<TestObject>(obj, 3);
    assertSame(obj, ewp.getCandidate());
    assertEquals(3, ewp.length());
    double[] beforeMutate = new double[3];
    for (int i = 0; i < 3; i++) {
      beforeMutate[i] = ewp.getParameter(i).get();
      assertTrue(ewp.getParameter(i).get() >= 0.1 && ewp.getParameter(i).get() <= 1.0);
    }
    boolean[] changed = new boolean[3];
    ewp.mutate();
    for (int i = 0; i < 3; i++) {
      assertTrue(ewp.getParameter(i).get() >= 0.1 && ewp.getParameter(i).get() <= 1.0);
      changed[i] = ewp.getParameter(i).get() != beforeMutate[i];
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 5 && !changed[i]; j++) {
        ewp.mutate();
        for (int k = i; k < 3; k++) {
          assertTrue(ewp.getParameter(k).get() >= 0.1 && ewp.getParameter(k).get() <= 1.0);
          changed[k] = ewp.getParameter(k).get() != beforeMutate[k];
        }
      }
      assertTrue(changed[i]);
    }
  }

  @Test
  public void testEqualsAndHashCode() {
    TestObject obj1 = new TestObject(5);
    TestObject obj2 = new TestObject(5);
    EncodingWithParameters<TestObject> ewp1 = new EncodingWithParameters<TestObject>(obj1, 3);
    EncodingWithParameters<TestObject> ewp2 = new EncodingWithParameters<TestObject>(obj2, 3);
    for (int i = 0; i < 3 && parametersAreEqual(ewp1, ewp2); i++) {
      ewp2 = new EncodingWithParameters<TestObject>(obj2, 3);
    }
    EncodingWithParameters<TestObject> ewp3 = new EncodingWithParameters<TestObject>(obj1, 3);
    for (int i = 0; i < 3 && parametersAreEqual(ewp1, ewp3); i++) {
      ewp3 = new EncodingWithParameters<TestObject>(obj1, 3);
    }
    EncodingWithParameters<TestObject> ewp4 = new EncodingWithParameters<TestObject>(obj2, 2);
    EncodingWithParameters<TestObject> ewp5 = new EncodingWithParameters<TestObject>(obj1, 2);
    assertEquals(ewp1, ewp2);
    assertEquals(ewp1, ewp3);
    assertEquals(ewp1, ewp4);
    assertEquals(ewp1, ewp5);
    assertEquals(ewp2, ewp3);
    assertEquals(ewp2, ewp4);
    assertEquals(ewp2, ewp5);
    assertEquals(ewp3, ewp4);
    assertEquals(ewp3, ewp5);
    assertEquals(ewp4, ewp5);
    assertEquals(ewp1.hashCode(), ewp2.hashCode());
    assertEquals(ewp1.hashCode(), ewp3.hashCode());
    assertEquals(ewp1.hashCode(), ewp4.hashCode());
    assertEquals(ewp1.hashCode(), ewp5.hashCode());
    assertEquals(ewp2.hashCode(), ewp3.hashCode());
    assertEquals(ewp2.hashCode(), ewp4.hashCode());
    assertEquals(ewp2.hashCode(), ewp5.hashCode());
    assertEquals(ewp3.hashCode(), ewp4.hashCode());
    assertEquals(ewp3.hashCode(), ewp5.hashCode());
    assertEquals(ewp4.hashCode(), ewp5.hashCode());
    TestObject obj3 = new TestObject(4);
    EncodingWithParameters<TestObject> different = new EncodingWithParameters<TestObject>(obj3, 3);
    assertNotEquals(ewp1, different);
    assertNotEquals(ewp2, different);
    assertNotEquals(ewp3, different);
    different = new EncodingWithParameters<TestObject>(obj3, 2);
    assertNotEquals(ewp4, different);
    assertNotEquals(ewp5, different);

    // Something other than an EnocdingWithParameters should be not equal...
    assertNotEquals(ewp1, obj1);
    assertNotEquals(ewp2, obj2);
    assertNotEquals(ewp3, obj1);
    assertNotEquals(ewp4, obj2);
    assertNotEquals(ewp5, obj1);
  }

  @Test
  public void testCopy() {
    TestObject obj = new TestObject(5);
    EncodingWithParameters<TestObject> ewp = new EncodingWithParameters<TestObject>(obj, 3);
    EncodingWithParameters<TestObject> copy = ewp.copy();
    assertEquals(ewp.getCandidate(), copy.getCandidate());
    assertNotSame(ewp.getCandidate(), copy.getCandidate());
    assertSame(obj, ewp.getCandidate());
    assertNotSame(obj, copy.getCandidate());
    assertEquals(obj, copy.getCandidate());
    assertEquals(ewp.hashCode(), copy.hashCode());
    assertTrue(parametersAreEqual(ewp, copy));
    assertFalse(anyParametersAreSame(ewp, copy));
  }

  private boolean parametersAreEqual(
      EncodingWithParameters<TestObject> ewp1, EncodingWithParameters<TestObject> ewp2) {
    if (ewp1.length() != ewp2.length()) return false;
    for (int i = 0; i < ewp1.length(); i++) {
      if (!ewp1.getParameter(i).equals(ewp2.getParameter(i))) return false;
    }
    return true;
  }

  private boolean anyParametersAreSame(
      EncodingWithParameters<TestObject> ewp1, EncodingWithParameters<TestObject> ewp2) {
    for (int i = 0; i < ewp1.length(); i++) {
      if (ewp1.getParameter(i) == ewp2.getParameter(i)) return true;
    }
    return false;
  }

  private static class TestObject implements Copyable<TestObject> {

    private static int IDENTIFIER = 0;
    private int id;

    public TestObject() {
      IDENTIFIER++;
      id = IDENTIFIER;
    }

    private TestObject(int id) {
      this.id = id;
    }

    @Override
    public TestObject copy() {
      return new TestObject(id);
    }

    @Override
    public boolean equals(Object other) {
      return id == ((TestObject) other).id;
    }

    @Override
    public int hashCode() {
      return id;
    }
  }
}
