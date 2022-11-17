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

/** JUnit test cases for SigmaScaling. */
public class SigmaScalingTests {

  @Test
  public void testSigmaZeroIntegers() {
    int[] fitnesses = {5, 5, 5, 5, 5, 5};
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      expected[i] = SigmaScaling.MIN_SCALED_FITNESS;
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    sig.init(101);
    assertTrue(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
    SigmaScaling sig2 = sig.split();
    assertTrue(wrapped.splitCalled);
    assertNotSame(sig, sig2);
  }

  @Test
  public void testSigmaZeroDoubles() {
    double[] fitnesses = {5, 5, 5, 5, 5, 5};
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      expected[i] = SigmaScaling.MIN_SCALED_FITNESS;
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
    assertFalse(wrapped.initCalled);
    sig.init(101);
    assertTrue(wrapped.initCalled);
    assertFalse(wrapped.splitCalled);
    SigmaScaling sig2 = sig.split();
    assertTrue(wrapped.splitCalled);
    assertNotSame(sig, sig2);
  }

  @Test
  public void testSigmaC2Integers() {
    int[] fitnesses = {6, 7, 8, 9, 10, 11, 12};
    double stdev = 2.160246899469287;
    double mean = 9.0;
    double adjust = mean - 2 * stdev;
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      expected[i] = fitnesses[i] - adjust;
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
  }

  @Test
  public void testSigmaC2Doubles() {
    double[] fitnesses = {6, 7, 8, 9, 10, 11, 12};
    double stdev = 2.160246899469287;
    double mean = 9.0;
    double adjust = mean - 2 * stdev;
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      expected[i] = fitnesses[i] - adjust;
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
  }

  @Test
  public void testSigmaC1Integers() {
    int[] fitnesses = {6, 7, 8, 9, 10, 11, 12};
    double stdev = 2.160246899469287;
    double mean = 9.0;
    double adjust = mean - stdev;
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      if (fitnesses[i] > 6) {
        expected[i] = fitnesses[i] - adjust;
      } else {
        expected[i] = SigmaScaling.MIN_SCALED_FITNESS;
      }
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped, 1.0);
    PopulationFitnessVector.Integer pop = PopulationFitnessVector.Integer.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
  }

  @Test
  public void testSigmaC1Doubles() {
    double[] fitnesses = {6, 7, 8, 9, 10, 11, 12};
    double stdev = 2.160246899469287;
    double mean = 9.0;
    double adjust = mean - stdev;
    double[] expected = new double[fitnesses.length];
    for (int i = 0; i < expected.length; i++) {
      if (fitnesses[i] > 6) {
        expected[i] = fitnesses[i] - adjust;
      } else {
        expected[i] = SigmaScaling.MIN_SCALED_FITNESS;
      }
    }
    TestSelection wrapped = new TestSelection(expected);
    SigmaScaling sig = new SigmaScaling(wrapped, 1.0);
    PopulationFitnessVector.Double pop = PopulationFitnessVector.Double.of(fitnesses);
    int[] selected = new int[fitnesses.length];
    sig.select(pop, selected);
    assertTrue(wrapped.selectDoubleCalled);
  }

  private static class TestSelection implements SelectionOperator {

    private double[] expected;
    private boolean selectDoubleCalled;
    private boolean initCalled;
    private boolean splitCalled;

    public TestSelection(double[] expected) {
      this.expected = expected;
    }

    public TestSelection(int[] f) {
      expected = new double[f.length];
      for (int i = 0; i < expected.length; i++) {
        expected[i] = f[i];
      }
    }

    @Override
    public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
      fail();
    }

    @Override
    public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
      assertEquals(expected.length, fitnesses.size());
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], fitnesses.getFitness(i));
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
      return new TestSelection(expected);
    }
  }
}
