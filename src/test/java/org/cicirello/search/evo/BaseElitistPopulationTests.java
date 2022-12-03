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

/** JUnit test cases for BaseElitistPopulation. */
public class BaseElitistPopulationTests extends SharedTestPopulations {

  @Test
  public void testExceptions() {
    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
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
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Double<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BaseElitistPopulation.Integer<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    0));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BaseElitistPopulation.Double<TestObject>(
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
                new BaseElitistPopulation.Integer<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10));

    final Population pop1 =
        new BaseElitistPopulation.Double<TestObject>(
            3,
            new TestInitializer(),
            new TestFitnessDouble(),
            new TestSelectionOp(),
            new ProgressTracker<TestObject>(),
            1);
    UnsupportedOperationException thrown3 =
        assertThrows(UnsupportedOperationException.class, () -> pop1.getParameter(0, 0));
    final Population pop2 =
        new BaseElitistPopulation.Integer<TestObject>(
            3,
            new TestInitializer(),
            new TestFitnessInteger(),
            new TestSelectionOp(),
            new ProgressTracker<TestObject>(),
            1);
    thrown3 = assertThrows(UnsupportedOperationException.class, () -> pop2.getParameter(0, 0));
  }

  @Test
  public void testBasePopulationElitismDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    BaseElitistPopulation.Double<TestObject> pop =
        new BaseElitistPopulation.Double<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyDouble(
        pop,
        f,
        tracker,
        selection,
        p -> ((BaseElitistPopulation.Double<TestObject>) p).getFitnessOfMostFit(),
        3);
  }

  @Test
  public void testBasePopulationDouble_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    BaseElitistPopulation.Double<TestObject> pop =
        new BaseElitistPopulation.Double<TestObject>(
            11, new TestInitializer(), f, selection, tracker, 1);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationElitismInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    BaseElitistPopulation.Integer<TestObject> pop =
        new BaseElitistPopulation.Integer<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyInteger(
        pop,
        f,
        tracker,
        selection,
        p -> ((BaseElitistPopulation.Integer<TestObject>) p).getFitnessOfMostFit(),
        3);
  }
}
