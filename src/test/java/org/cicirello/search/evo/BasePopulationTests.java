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
public class BasePopulationTests extends SharedTestPopulations {

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
                    0));
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
                    0));
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
                    0));
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
                    0));

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
                    0));
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
                    0));
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
                    0));
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
                    0));

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
                    0));
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
                    0));
  }

  @Test
  public void testBasePopulationDouble() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
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
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationDoubleIntCost() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
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
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationInteger() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
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
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 0);
    verifySelectCopies(pop);
  }

  @Test
  public void testBasePopulationDoubleFitness_WithReplacementThatPicksMultipleCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    TestMultiReplacement replacement = new TestMultiReplacement();
    BasePopulation.DoubleFitness<TestObject> pop =
        new BasePopulation.DoubleFitness<TestObject>(
            10,
            new TestInitializer(),
            f,
            selection,
            tracker,
            (candidate, fitness) ->
                new PopulationMember.DoubleFitness<TestObject>(candidate, fitness),
            0,
            replacement);
    pop.init();
    pop.select(); // 2, 3, 4, 5, 6, 5, 4, 3, 2, 1
    pop.replace();
    int[] chosenCounts = new int[10];
    for (int i = 0; i < 10; i++) {
      chosenCounts[(int) pop.fitness(i)]++;
    }
    int[] expectedCounts = {0, 4, 1, 5, 0, 0, 0, 0, 0, 0};
    assertArrayEquals(expectedCounts, chosenCounts);
    pop.select();
    int[] exp = {1, 1, 1, 1, 3, 3, 3, 3, 3, 2};
    for (int i = 0; i < 10; i++) {
      assertEquals(exp[i], pop.get(i).hashCode());
      if (i > 0 && exp[i] == exp[i - 1]) {
        assertEquals(pop.get(i - 1), pop.get(i));
        assertNotSame(pop.get(i - 1), pop.get(i));
      }
    }
  }

  @Test
  public void testBasePopulationIntegerFitness_WithReplacementThatPicksMultipleCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestMultiReplacement replacement = new TestMultiReplacement();
    BasePopulation.IntegerFitness<TestObject> pop =
        new BasePopulation.IntegerFitness<TestObject>(
            10,
            new TestInitializer(),
            f,
            selection,
            tracker,
            (candidate, fitness) ->
                new PopulationMember.IntegerFitness<TestObject>(candidate, fitness),
            0,
            replacement);
    pop.init();
    pop.select(); // 2, 3, 4, 5, 6, 5, 4, 3, 2, 1
    pop.replace();
    int[] chosenCounts = new int[10];
    for (int i = 0; i < 10; i++) {
      chosenCounts[pop.fitness(i) - 10]++;
    }
    int[] expectedCounts = {0, 4, 1, 5, 0, 0, 0, 0, 0, 0};
    assertArrayEquals(expectedCounts, chosenCounts);
    pop.select();
    int[] exp = {1, 1, 1, 1, 3, 3, 3, 3, 3, 2};
    for (int i = 0; i < 10; i++) {
      assertEquals(exp[i], pop.get(i).hashCode());
      if (i > 0 && exp[i] == exp[i - 1]) {
        assertEquals(pop.get(i - 1), pop.get(i));
        assertNotSame(pop.get(i - 1), pop.get(i));
      }
    }
  }

  private static class TestMultiReplacement implements ReplacementStrategy<TestObject> {

    @Override
    public void replace(
        PopulationCandidates.IntegerFitness<TestObject> parentPopulation,
        PopulationCandidates.IntegerFitness<TestObject> childPopulation,
        Replacements replacements,
        int targetPopulationSize) {
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(0);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
    }

    @Override
    public void replace(
        PopulationCandidates.DoubleFitness<TestObject> parentPopulation,
        PopulationCandidates.DoubleFitness<TestObject> childPopulation,
        Replacements replacements,
        int targetPopulationSize) {
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(9);
      replacements.addFromChildPopulation(0);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
      replacements.addFromChildPopulation(1);
    }

    @Override
    public TestMultiReplacement split() {
      return this;
    }
  }
}
