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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for the nested classes of PopulationMember for evolvable parameters. */
public class PopulationMemberEvolvableTests {

  @Test
  public void testConstructorForceRatesConstant_Double() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableDoubleFitness<TestObject> ewp =
        new PopulationMember.EvolvableDoubleFitness<TestObject>(
            obj, fitness, 3, 0.4, 0.4 + Math.ulp(0.4), generator);
    assertSame(obj, ewp.candidate());
    assertEquals(fitness, ewp.fitness());
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
  public void testConstructorForceRatesConstant_Integer() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableIntegerFitness<TestObject> ewp =
        new PopulationMember.EvolvableIntegerFitness<TestObject>(
            obj, fitness, 3, 0.4, 0.4 + Math.ulp(0.4), generator);
    assertSame(obj, ewp.candidate());
    assertEquals(fitness, ewp.fitness());
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
  public void testConstructorDefaults_Double() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableDoubleFitness<TestObject> ewp =
        new PopulationMember.EvolvableDoubleFitness<TestObject>(obj, fitness, 3, generator);
    assertSame(obj, ewp.candidate());
    assertEquals(fitness, ewp.fitness());
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
  public void testConstructorDefaults_Integer() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableIntegerFitness<TestObject> ewp =
        new PopulationMember.EvolvableIntegerFitness<TestObject>(obj, fitness, 3, generator);
    assertSame(obj, ewp.candidate());
    assertEquals(fitness, ewp.fitness());
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
  public void testCopy_Double() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableDoubleFitness<TestObject> ewp =
        new PopulationMember.EvolvableDoubleFitness<TestObject>(obj, fitness, 3, generator);
    PopulationMember.EvolvableDoubleFitness<TestObject> copy = ewp.copy();
    assertEquals(ewp.candidate(), copy.candidate());
    assertNotSame(ewp.candidate(), copy.candidate());
    assertSame(obj, ewp.candidate());
    assertNotSame(obj, copy.candidate());
    assertEquals(obj, copy.candidate());
    assertTrue(parametersAreEqual(ewp, copy, 3));
    assertFalse(anyParametersAreSame(ewp, copy, 3));
  }

  @Test
  public void testCopy_Integer() {
    int fitness = 10;
    EnhancedSplittableGenerator generator = new EnhancedSplittableGenerator(42);
    TestObject obj = new TestObject(5);
    PopulationMember.EvolvableIntegerFitness<TestObject> ewp =
        new PopulationMember.EvolvableIntegerFitness<TestObject>(obj, fitness, 3, generator);
    PopulationMember.EvolvableIntegerFitness<TestObject> copy = ewp.copy();
    assertEquals(ewp.candidate(), copy.candidate());
    assertNotSame(ewp.candidate(), copy.candidate());
    assertSame(obj, ewp.candidate());
    assertNotSame(obj, copy.candidate());
    assertEquals(obj, copy.candidate());
    assertTrue(parametersAreEqual(ewp, copy, 3));
    assertFalse(anyParametersAreSame(ewp, copy, 3));
  }

  private boolean parametersAreEqual(
      PopulationMember.EvolvableDoubleFitness<TestObject> ewp1,
      PopulationMember.EvolvableDoubleFitness<TestObject> ewp2,
      int length) {
    for (int i = 0; i < length; i++) {
      if (!ewp1.getParameter(i).equals(ewp2.getParameter(i))) return false;
    }
    return true;
  }

  private boolean parametersAreEqual(
      PopulationMember.EvolvableIntegerFitness<TestObject> ewp1,
      PopulationMember.EvolvableIntegerFitness<TestObject> ewp2,
      int length) {
    for (int i = 0; i < length; i++) {
      if (!ewp1.getParameter(i).equals(ewp2.getParameter(i))) return false;
    }
    return true;
  }

  private boolean anyParametersAreSame(
      PopulationMember.EvolvableDoubleFitness<TestObject> ewp1,
      PopulationMember.EvolvableDoubleFitness<TestObject> ewp2,
      int length) {
    for (int i = 0; i < length; i++) {
      if (ewp1.getParameter(i) == ewp2.getParameter(i)) return true;
    }
    return false;
  }

  private boolean anyParametersAreSame(
      PopulationMember.EvolvableIntegerFitness<TestObject> ewp1,
      PopulationMember.EvolvableIntegerFitness<TestObject> ewp2,
      int length) {
    for (int i = 0; i < length; i++) {
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
