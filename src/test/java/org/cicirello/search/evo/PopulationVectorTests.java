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

/** JUnit test cases for the internal class PopulationVector */
public class PopulationVectorTests {

  @Test
  public void testDoubleFitness() {
    PopulationMember.DoubleFitness<TestObject> a =
        new PopulationMember.DoubleFitness<TestObject>(new TestObject("A"), 5);
    PopulationMember.DoubleFitness<TestObject> b =
        new PopulationMember.DoubleFitness<TestObject>(new TestObject("B"), 2);
    PopulationMember.DoubleFitness<TestObject> c =
        new PopulationMember.DoubleFitness<TestObject>(new TestObject("C"), 12);

    PopulationVector.DoubleFitness<TestObject> v =
        new PopulationVector.DoubleFitness<TestObject>(3);
    assertEquals(0, v.size());
    v.add(a);
    assertEquals(1, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(a, v.get(0));
    assertEquals(5, v.fitness(0));
    v.add(b);
    assertEquals(2, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(b.candidate(), v.candidate(1));
    assertEquals(a, v.get(0));
    assertEquals(b, v.get(1));
    assertEquals(5, v.fitness(0));
    assertEquals(2, v.fitness(1));
    v.add(c);
    assertEquals(3, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(b.candidate(), v.candidate(1));
    assertEquals(c.candidate(), v.candidate(2));
    assertEquals(a, v.get(0));
    assertEquals(b, v.get(1));
    assertEquals(c, v.get(2));
    assertEquals(5, v.fitness(0));
    assertEquals(2, v.fitness(1));
    assertEquals(12, v.fitness(2));

    int[] fitnesses = {5, 2, 12};
    int next = 0;
    for (PopulationMember.DoubleFitness<TestObject> e : v) {
      assertEquals(fitnesses[next], e.fitness());
      assertEquals(
          next == 0 ? a.candidate() : (next == 1 ? b.candidate() : c.candidate()), e.candidate());
      assertEquals(next == 0 ? a : (next == 1 ? b : c), e);
      next++;
    }

    v.clear();
    assertEquals(0, v.size());
    v.add(c);
    assertEquals(1, v.size());
    assertEquals(c.candidate(), v.candidate(0));
    assertEquals(c, v.get(0));
    assertEquals(12, v.fitness(0));
    v.add(a);
    assertEquals(2, v.size());
    assertEquals(c.candidate(), v.candidate(0));
    assertEquals(a.candidate(), v.candidate(1));
    assertEquals(c, v.get(0));
    assertEquals(a, v.get(1));
    assertEquals(12, v.fitness(0));
    assertEquals(5, v.fitness(1));

    fitnesses = new int[] {12, 5};
    next = 0;
    for (PopulationMember.DoubleFitness<TestObject> e : v) {
      if (next == 2) {
        fail();
      }
      assertEquals(fitnesses[next], e.fitness());
      assertEquals(next == 0 ? c.candidate() : a.candidate(), e.candidate());
      assertEquals(next == 0 ? c : a, e);
      next++;
    }
  }

  @Test
  public void testIntegerFitness() {
    PopulationMember.IntegerFitness<TestObject> a =
        new PopulationMember.IntegerFitness<TestObject>(new TestObject("A"), 5);
    PopulationMember.IntegerFitness<TestObject> b =
        new PopulationMember.IntegerFitness<TestObject>(new TestObject("B"), 2);
    PopulationMember.IntegerFitness<TestObject> c =
        new PopulationMember.IntegerFitness<TestObject>(new TestObject("C"), 12);

    PopulationVector.IntegerFitness<TestObject> v =
        new PopulationVector.IntegerFitness<TestObject>(3);
    assertEquals(0, v.size());
    v.add(a);
    assertEquals(1, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(a, v.get(0));
    assertEquals(5, v.fitness(0));
    v.add(b);
    assertEquals(2, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(b.candidate(), v.candidate(1));
    assertEquals(a, v.get(0));
    assertEquals(b, v.get(1));
    assertEquals(5, v.fitness(0));
    assertEquals(2, v.fitness(1));
    v.add(c);
    assertEquals(3, v.size());
    assertEquals(a.candidate(), v.candidate(0));
    assertEquals(b.candidate(), v.candidate(1));
    assertEquals(c.candidate(), v.candidate(2));
    assertEquals(a, v.get(0));
    assertEquals(b, v.get(1));
    assertEquals(c, v.get(2));
    assertEquals(5, v.fitness(0));
    assertEquals(2, v.fitness(1));
    assertEquals(12, v.fitness(2));

    int[] fitnesses = {5, 2, 12};
    int next = 0;
    for (PopulationMember.IntegerFitness<TestObject> e : v) {
      assertEquals(fitnesses[next], e.fitness());
      assertEquals(
          next == 0 ? a.candidate() : (next == 1 ? b.candidate() : c.candidate()), e.candidate());
      assertEquals(next == 0 ? a : (next == 1 ? b : c), e);
      next++;
    }

    v.clear();
    assertEquals(0, v.size());
    v.add(c);
    assertEquals(1, v.size());
    assertEquals(c.candidate(), v.candidate(0));
    assertEquals(c, v.get(0));
    assertEquals(12, v.fitness(0));
    v.add(a);
    assertEquals(2, v.size());
    assertEquals(c.candidate(), v.candidate(0));
    assertEquals(a.candidate(), v.candidate(1));
    assertEquals(c, v.get(0));
    assertEquals(a, v.get(1));
    assertEquals(12, v.fitness(0));
    assertEquals(5, v.fitness(1));

    fitnesses = new int[] {12, 5};
    next = 0;
    for (PopulationMember.IntegerFitness<TestObject> e : v) {
      if (next == 2) {
        fail();
      }
      assertEquals(fitnesses[next], e.fitness());
      assertEquals(next == 0 ? c.candidate() : a.candidate(), e.candidate());
      assertEquals(next == 0 ? c : a, e);
      next++;
    }
  }

  private static final class TestObject implements Copyable<TestObject> {

    private final String s;

    public TestObject(String s) {
      this.s = s;
    }

    @Override
    public TestObject copy() {
      return new TestObject(s);
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof TestObject && ((TestObject) other).s.equals(s);
    }

    @Override
    public int hashCode() {
      return s.hashCode();
    }
  }
}
