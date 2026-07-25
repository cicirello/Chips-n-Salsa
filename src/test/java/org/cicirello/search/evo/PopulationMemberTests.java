/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

/** JUnit test cases for PopulationMember. */
public class PopulationMemberTests {

  private static final double EPSILON = 1e-10;

  @Test
  public void testExceptionsForParametersInNonAdaptiveCase() {
    TestObject original = new TestObject();

    final PopulationMember.DoubleFitness<TestObject> pm1 =
        new PopulationMember.DoubleFitness<TestObject>(original, 3.0);
    UnsupportedOperationException thrown =
        assertThrows(UnsupportedOperationException.class, () -> pm1.getParameter(0));

    final PopulationMember.IntegerFitness<TestObject> pm2 =
        new PopulationMember.IntegerFitness<TestObject>(original, 3);
    thrown = assertThrows(UnsupportedOperationException.class, () -> pm2.getParameter(0));
  }

  @Test
  public void testPopulationMemberDoubleFitness() {
    TestObject original = new TestObject();
    double fit = 12.5;
    PopulationMember.DoubleFitness<TestObject> pm =
        new PopulationMember.DoubleFitness<TestObject>(original, fit);
    assertTrue(original == pm.candidate());
    assertEquals(fit, pm.fitness(), EPSILON);
    fit = 32.7;
    pm.setFitness(fit);
    assertEquals(fit, pm.fitness(), EPSILON);
    assertTrue(original == pm.candidate());
    PopulationMember.DoubleFitness<TestObject> other = pm.copy();
    assertFalse(original == other.candidate());
    assertEquals(original, other.candidate());
    assertEquals(fit, pm.fitness(), EPSILON);
  }

  @Test
  public void testPopulationMemberIntegerFitness() {
    TestObject original = new TestObject();
    int fit = 12;
    PopulationMember.IntegerFitness<TestObject> pm =
        new PopulationMember.IntegerFitness<TestObject>(original, fit);
    assertTrue(original == pm.candidate());
    assertEquals(fit, pm.fitness());
    fit = 32;
    pm.setFitness(fit);
    assertEquals(fit, pm.fitness());
    assertTrue(original == pm.candidate());
    PopulationMember.IntegerFitness<TestObject> other = pm.copy();
    assertFalse(original == other.candidate());
    assertEquals(original, other.candidate());
    assertEquals(fit, pm.fitness());
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

    public TestObject copy() {
      return new TestObject(id);
    }

    public boolean equals(Object other) {
      return id == ((TestObject) other).id;
    }
  }
}
