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

/** JUnit test cases for EvolvableParametersElitistPopulation. */
public class EvolvableParametersElitistPopulationTests extends SharedTestPopulations {

  @Test
  public void testExceptions() {
    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    null,
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    null,
                    1,
                    2));

    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    null,
                    new ProgressTracker<TestObject>(),
                    1,
                    2));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    null,
                    1,
                    2));

    IllegalArgumentException thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0,
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0,
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0,
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0,
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10,
                    2));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10,
                    2));
  }

  @Test
  public void testEvolvableParametersPopulationElitismDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    EvolvableParametersElitistPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3, 2);
    verifyDouble(
        pop,
        f,
        tracker,
        selection,
        p ->
            ((EvolvableParametersElitistPopulation.DoubleFitness<TestObject>) p)
                .getFitnessOfMostFit(),
        3);

    assertEquals(0, selection.initCalledWith);
    pop.initOperators(987);
    assertEquals(987, selection.initCalledWith);
  }

  @Test
  public void testEvolvableParametersPopulationElitismInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    EvolvableParametersElitistPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3, 2);
    verifyInteger(
        pop,
        f,
        tracker,
        selection,
        p ->
            ((EvolvableParametersElitistPopulation.IntegerFitness<TestObject>) p)
                .getFitnessOfMostFit(),
        3);

    assertEquals(0, selection.initCalledWith);
    pop.initOperators(987);
    assertEquals(987, selection.initCalledWith);
  }
}
