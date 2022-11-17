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

import org.junit.jupiter.api.*;

/** JUnit test cases for FitnessShifter. */
public class FitnessShifterTests {

  @Test
  public void testAllPositiveIntegers() {
    int[] fitnesses = {8, 6, 4, 5, 9, 10};
    int[] expected = {5, 3, 1, 2, 6, 7};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertTrue(wrapped.selectIntegerCalled);
    assertFalse(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);

    shifter.init(101);
    assertTrue(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
    FitnessShifter shifter2 = shifter.split();
    assertTrue(wrapped.splitCalled);
    assertNotSame(shifter, shifter2);
  }

  @Test
  public void testAllNegativeIntegers() {
    int[] fitnesses = {-8, -6, -4, -5, -9, -10};
    int[] expected = {3, 5, 7, 6, 2, 1};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertTrue(wrapped.selectIntegerCalled);
    assertFalse(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
  }

  @Test
  public void testMixedIntegers() {
    int[] fitnesses = {-8, 6, -4, -5, 9, 10};
    int[] expected = {1, 15, 5, 4, 18, 19};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertTrue(wrapped.selectIntegerCalled);
    assertFalse(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
  }

  @Test
  public void testAllPositiveDoubles() {
    double[] fitnesses = {8, 6, 4, 5, 9, 10};
    double[] expected = {5, 3, 1, 2, 6, 7};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertFalse(wrapped.selectIntegerCalled);
    assertTrue(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);

    shifter.init(101);
    assertTrue(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
    FitnessShifter shifter2 = shifter.split();
    assertTrue(wrapped.splitCalled);
    assertNotSame(shifter, shifter2);
  }

  @Test
  public void testAllNegativeDoubles() {
    double[] fitnesses = {-8, -6, -4, -5, -9, -10};
    double[] expected = {3, 5, 7, 6, 2, 1};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertFalse(wrapped.selectIntegerCalled);
    assertTrue(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
  }

  @Test
  public void testMixedDoubles() {
    double[] fitnesses = {-8, 6, -4, -5, 9, 10};
    double[] expected = {1, 15, 5, 4, 18, 19};
    TestSelection wrapped = new TestSelection(expected);
    FitnessShifter shifter = new FitnessShifter(wrapped);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    shifter.select(pop, selected);
    assertFalse(wrapped.selectIntegerCalled);
    assertTrue(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
  }

  private static class TestSelection implements SelectionOperator {

    private double[] expectedD;
    private int[] expectedInt;
    private boolean selectDoubleCalled;
    private boolean selectIntegerCalled;
    private boolean initCalled;
    private boolean splitCalled;

    public TestSelection(double[] expectedD) {
      this.expectedD = expectedD;
    }

    public TestSelection(int[] expectedInt) {
      this.expectedInt = expectedInt;
    }

    private TestSelection(TestSelection other) {
      this.expectedD = other.expectedD;
      this.expectedInt = other.expectedInt;
    }

    @Override
    public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
      assertEquals(expectedInt.length, fitnesses.size());
      for (int i = 0; i < expectedInt.length; i++) {
        assertEquals(expectedInt[i], fitnesses.getFitness(i));
      }
      selectIntegerCalled = true;
    }

    @Override
    public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
      assertEquals(expectedD.length, fitnesses.size());
      for (int i = 0; i < expectedD.length; i++) {
        assertEquals(expectedD[i], fitnesses.getFitness(i));
      }
      selectDoubleCalled = true;
    }

    @Override
    public void init(int generations) {
      initCalled = true;
      assertEquals(101, generations);
    }

    @Override
    public TestSelection split() {
      splitCalled = true;
      return new TestSelection(this);
    }
  }
}
