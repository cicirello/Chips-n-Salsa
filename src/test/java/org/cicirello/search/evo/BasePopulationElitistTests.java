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

import org.cicirello.search.ProgressTracker;
import org.junit.jupiter.api.*;

/** JUnit test cases for BasePopulation. */
public class BasePopulationElitistTests extends SharedTestPopulations {

  @Test
  public void testExceptions() {
    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    null,
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    null,
                    1));

    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    null,
                    new ProgressTracker<TestObject>(),
                    1));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    null,
                    1));

    IllegalArgumentException thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    -1));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    1));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    -1));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10));
  }

  @Test
  public void testBasePopulationElitismDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleElitist f = new TestFitnessDoubleElitist();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyDoubleElite(
        pop,
        f,
        tracker,
        selection,
        p -> ((BasePopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        3);
  }

  @Test
  public void testBasePopulationDouble_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            11, new TestInitializer(), f, selection, tracker, 1);
    verifySelectCopies(pop);
  }

  void verifySelectCopies(Population<TestObject> pop) {
    pop.init();
    pop.select();
    TestObject[] firstSelect = new TestObject[10];
    for (int i = 0; i < 10; i++) {
      firstSelect[i] = pop.get(i);
    }
    pop.replace();
    pop.select();
    TestObject[] secondSelect = new TestObject[10];
    for (int i = 0; i < 10; i++) {
      secondSelect[i] = pop.get(i);
    }
    for (int i = 0; i < 9; i++) {
      assertFalse(firstSelect[i] == secondSelect[8 - i]);
      assertFalse(firstSelect[i] == secondSelect[i]);
      assertEquals(firstSelect[i], secondSelect[8 - i]);
      assertEquals(firstSelect[i], secondSelect[i]);
    }
  }

  @Test
  public void testBasePopulationElitismInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessIntegerElitist f = new TestFitnessIntegerElitist();
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyIntegerElite(
        pop,
        f,
        tracker,
        selection,
        p -> ((BasePopulation.IntegerFitness<TestObject>) p).getFitnessOfMostFit(),
        3);
  }
}
