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

import org.cicirello.search.ProgressTracker;
import org.junit.jupiter.api.*;

/** JUnit test cases for EvolvableParametersPopulation. */
public class EvolvableParametersPopulationTests extends SharedTestPopulations {

  @Test
  public void testExceptions() {
    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.DoubleFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    null,
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    null,
                    2));

    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.IntegerFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    null,
                    new ProgressTracker<TestObject>(),
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    null,
                    2));

    IllegalArgumentException thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersPopulation.DoubleFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersPopulation.IntegerFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    2));
  }

  @Test
  public void testEvolvableParametersPopulation_getParameter_Double() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    pop.init();
    pop.select();
    boolean allSame0 = true;
    boolean allSame1 = true;
    double last0 = -1;
    double last1 = -1;
    for (int i = 0; i < 10; i++) {
      double param0 = pop.getParameter(i, 0).get();
      double param1 = pop.getParameter(i, 1).get();
      assertTrue(param0 >= 0.0 && param0 <= 1.0);
      assertTrue(param1 >= 0.0 && param1 <= 1.0);
      if (i > 0) {
        if (allSame0) {
          allSame0 = param0 == last0;
        }
        if (allSame1) {
          allSame1 = param1 == last1;
        }
      }
      last0 = param0;
      last1 = param1;
    }
    assertFalse(allSame0);
    assertFalse(allSame1);

    pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 1);
    pop.init();
    pop.select();
    allSame0 = true;
    for (int i = 0; i < 10; i++) {
      double param0 = pop.getParameter(i, 0).get();
      assertTrue(param0 >= 0.0 && param0 <= 1.0);
      if (i > 0) {
        if (allSame0) {
          allSame0 = param0 == last0;
        }
      }
      last0 = param0;
    }
    assertFalse(allSame0);
  }

  @Test
  public void testEvolvableParametersPopulation_getParameter_Integer() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    pop.init();
    pop.select();
    boolean allSame0 = true;
    boolean allSame1 = true;
    double last0 = -1;
    double last1 = -1;
    for (int i = 0; i < 10; i++) {
      double param0 = pop.getParameter(i, 0).get();
      double param1 = pop.getParameter(i, 1).get();
      assertTrue(param0 >= 0.0 && param0 <= 1.0);
      assertTrue(param1 >= 0.0 && param1 <= 1.0);
      if (i > 0) {
        if (allSame0) {
          allSame0 = param0 == last0;
        }
        if (allSame1) {
          allSame1 = param1 == last1;
        }
      }
      last0 = param0;
      last1 = param1;
    }
    assertFalse(allSame0);
    assertFalse(allSame1);

    pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 1);
    pop.init();
    pop.select();
    allSame0 = true;
    for (int i = 0; i < 10; i++) {
      double param0 = pop.getParameter(i, 0).get();
      assertTrue(param0 >= 0.0 && param0 <= 1.0);
      if (i > 0) {
        if (allSame0) {
          allSame0 = param0 == last0;
        }
      }
      last0 = param0;
    }
    assertFalse(allSame0);
  }

  @Test
  public void testEvolvableParametersPopulationDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifyDouble(
        pop,
        f,
        tracker,
        selection,
        p -> ((EvolvableParametersPopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        0);

    assertEquals(0, selection.initCalledWith);
    pop.initOperators(987);
    assertEquals(987, selection.initCalledWith);
  }

  @Test
  public void testEvolvableParametersPopulationDouble_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifySelectCopies(pop);
  }

  @Test
  public void testEvolvableParametersPopulationDoubleIntCost() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifyDoubleWithIntCost(
        pop,
        f,
        tracker,
        selection,
        p -> ((EvolvableParametersPopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        0);
  }

  @Test
  public void testEvolvableParametersPopulationDoubleIntCost_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifySelectCopies(pop);
  }

  @Test
  public void testEvolvableParametersPopulationInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifyInteger(
        pop,
        f,
        tracker,
        selection,
        p -> ((EvolvableParametersPopulation.IntegerFitness<TestObject>) p).getFitnessOfMostFit(),
        0);

    assertEquals(0, selection.initCalledWith);
    pop.initOperators(987);
    assertEquals(987, selection.initCalledWith);
  }

  @Test
  public void testEvolvableParametersPopulationInteger_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 2);
    verifySelectCopies(pop);
  }
}
