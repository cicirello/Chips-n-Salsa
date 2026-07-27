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

/** JUnit test cases for BasePopulation with specified ReplacementStrategy. */
public class BasePopulationReplacementTests extends SharedTestPopulations {

  private SimpleGeneration<TestObject> generation;

  @BeforeEach
  public void initTest() {
    generation =
        new SimpleGeneration<TestObject>(new TestMutation(), 0.1, new TestCrossover(), 0.5);
  }

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
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    null,
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    null,
                    generation));
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
                    new ProgressTracker<TestObject>(),
                    generation));

    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    null,
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    null,
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    null,
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    null,
                    generation));
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
                    new ProgressTracker<TestObject>(),
                    generation));

    IllegalArgumentException thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.DoubleFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessDouble(),
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BasePopulation.IntegerFitness<TestObject>(
                    0,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new GenerationalReplacement<TestObject>(),
                    new ProgressTracker<TestObject>(),
                    generation));
  }

  @Test
  public void testBasePopulationDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifyDouble(
        pop,
        f,
        tracker,
        selection,
        p -> ((BasePopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        0);
  }

  @Test
  public void testBasePopulationDouble_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationDoubleIntCost() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifyDoubleWithIntCost(
        pop,
        f,
        tracker,
        selection,
        p -> ((BasePopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        0);
  }

  @Test
  public void testBasePopulationDoubleIntCost_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifyInteger(
        pop,
        f,
        tracker,
        selection,
        p -> ((BasePopulation.IntegerFitness<TestObject>) p).getFitnessOfMostFit(),
        0);
  }

  @Test
  public void testBasePopulationInteger_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    GenerationalReplacement<TestObject> r = new GenerationalReplacement<TestObject>();
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, r, tracker, generation);
    verifySelectCopies(pop);
  }
}
