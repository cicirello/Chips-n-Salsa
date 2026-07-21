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

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
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
                new BaseElitistPopulation.DoubleFitness<TestObject>(
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
                new BaseElitistPopulation.IntegerFitness<TestObject>(
                    10,
                    new TestInitializer(),
                    new TestFitnessInteger(),
                    new TestSelectionOp(),
                    new ProgressTracker<TestObject>(),
                    10));

    final Population pop1 =
        new BaseElitistPopulation.DoubleFitness<TestObject>(
            3,
            new TestInitializer(),
            new TestFitnessDouble(),
            new TestSelectionOp(),
            new ProgressTracker<TestObject>(),
            1);
    UnsupportedOperationException thrown3 =
        assertThrows(UnsupportedOperationException.class, () -> pop1.getParameter(0, 0));
    final Population pop2 =
        new BaseElitistPopulation.IntegerFitness<TestObject>(
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
    TestFitnessDoubleElitist f = new TestFitnessDoubleElitist();
    BaseElitistPopulation.DoubleFitness<TestObject> pop =
        new BaseElitistPopulation.DoubleFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyDoubleElite(
        pop,
        f,
        tracker,
        selection,
        p -> ((BaseElitistPopulation.DoubleFitness<TestObject>) p).getFitnessOfMostFit(),
        3);
  }

  private void verifyDoubleElite(
      PopulationFitnessVector.DoubleFitness popVector,
      TestFitnessDoubleElitist f,
      ProgressTracker<TestObject> tracker,
      TestSelectionOp selection,
      ToDoubleFunction<Population<TestObject>> mostFitFitness,
      int elite) {
    @SuppressWarnings("unchecked")
    Population<TestObject> pop = (Population<TestObject>) popVector;

    assertTrue(tracker == pop.getProgressTracker());
    tracker = new ProgressTracker<TestObject>();
    pop.setProgressTracker(tracker);
    assertTrue(tracker == pop.getProgressTracker());

    pop.init();
    assertEquals(10, pop.size());
    assertEquals(10 - elite, pop.mutableSize());
    assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(1.0 / 7.0, pop.getMostFit().getCostDouble());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
    int[] expected = {1, 2, 3, 4, 5, 6, 5, 4, 3, 2};
    for (int i = 0; i < 10; i++) {
      // fitnesses of original before selection.
      assertEquals(expected[i] + 0.4, popVector.fitness(i));
    }
    assertFalse(selection.called);
    pop.select();
    assertTrue(selection.called);
    for (int i = 0; i < 10; i++) {
      // fitnesses of original before selection.
      assertEquals(expected[i] + 0.4, popVector.fitness(i));
      // subject to mutation to opposite order since we selected, which reversed.
      if (i < 10 - elite) {
        assertEquals(expected[9 - elite - i], pop.get(i).id);
      }
    }
    assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(1.0 / 7.0, pop.getMostFit().getCostDouble());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    pop.replace();
    // next line for elite case only
    int[] expectedNow = {4, 5, 6, 5, 6, 5, 4, 3, 2, 1};
    boolean[] gotit = new boolean[elite];
    for (int i = 0; i < 10; i++) {
      if (i < elite) {
        double fitness = popVector.fitness(i);
        int casted = (int) fitness;
        for (int j = 0; j < elite; j++) {
          if (expectedNow[j] == casted) {
            assertFalse(gotit[j]);
            gotit[j] = true;
            assertEquals(expectedNow[j] + 0.4, fitness, "index i=" + i);
          }
        }
      } else {
        assertEquals(expectedNow[i] + 0.4, popVector.fitness(i), "index i=" + i);
      }
    }
    for (boolean b : gotit) {
      assertTrue(b);
    }
    assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(1.0 / 7.0, pop.getMostFit().getCostDouble());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    pop.select();
    for (int i = 0; i < 10; i++) {
      assertEquals(expectedNow[i] + 0.4, popVector.fitness(i));
      if (i < 10 - elite) {
        assertEquals(expectedNow[9 - elite - i], pop.get(i).id);
      }
    }
    assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(1.0 / 7.0, pop.getMostFit().getCostDouble());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    int adjustThisOne = 0;
    f.changeFitness(5);
    pop.updateFitness(adjustThisOne);
    pop.replace();

    expectedNow = new int[] {4, 5, 6, 4, 5, 6, 5, 4, 5, 6};
    gotit = new boolean[elite];
    boolean[] gotitagain = new boolean[elite];
    for (int i = 0; i < 10; i++) {
      if (i < elite) {
        double fitness = popVector.fitness(i);
        int casted = (int) fitness;
        for (int j = 0; j < elite; j++) {
          if (expectedNow[j] == casted) {
            assertFalse(gotit[j]);
            gotit[j] = true;
            assertEquals(expectedNow[j] + 0.4, fitness, "index i=" + i);
          }
        }
      } else if (i >= 10 - elite) {
        double fitness = popVector.fitness(i);
        int casted = (int) fitness;
        for (int j = 0; j < elite; j++) {
          if (expectedNow[10 - elite + j] == casted) {
            assertFalse(gotitagain[j]);
            gotitagain[j] = true;
            assertEquals(expectedNow[10 - elite + j] + 0.4, fitness, "index i=" + i);
          }
        }
      } else {
        int temp = i == elite + adjustThisOne ? 5 : 0;
        assertEquals(expectedNow[i] + 0.4 + temp, popVector.fitness(i), "index i=" + i);
      }
    }
    for (boolean b : gotit) {
      assertTrue(b);
    }
    for (boolean b : gotitagain) {
      assertTrue(b);
    }
    assertEquals(9.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(4, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    f.changeFitness(12);
    Population<TestObject> pop2 = pop.split();
    @SuppressWarnings("unchecked")
    PopulationFitnessVector.DoubleFitness popVector2 = (PopulationFitnessVector.DoubleFitness) pop2;

    // orginal should be same
    assertEquals(
        expectedNow[elite + adjustThisOne] + 0.4 + 5, popVector.fitness(elite + adjustThisOne));
    assertEquals(9.4, mostFitFitness.applyAsDouble(pop));
    assertEquals(4, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    // trackers should be same
    assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());

    pop2.init();
    assertEquals(10, pop2.size());
    assertEquals(10 - elite, pop2.mutableSize());
    for (int i = 0; i < 10; i++) {
      assertEquals(1 - i + 12 + 0.4, popVector2.fitness(i));
    }

    assertFalse(pop.evolutionIsPaused());
    assertFalse(pop2.evolutionIsPaused());
    tracker.stop();
    assertTrue(pop.evolutionIsPaused());
    assertTrue(pop2.evolutionIsPaused());
    tracker.start();
    assertFalse(pop.evolutionIsPaused());
    assertFalse(pop2.evolutionIsPaused());
    tracker.update(0.0, new TestObject(), true);
    assertTrue(pop.evolutionIsPaused());
    assertTrue(pop2.evolutionIsPaused());
  }

  @Test
  public void testBasePopulationDouble_SelectCopies() {
    TestObject.reinit();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    BaseElitistPopulation.DoubleFitness<TestObject> pop =
        new BaseElitistPopulation.DoubleFitness<TestObject>(
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
    BaseElitistPopulation.IntegerFitness<TestObject> pop =
        new BaseElitistPopulation.IntegerFitness<TestObject>(
            10, new TestInitializer(), f, selection, tracker, 3);
    verifyIntegerElite(
        pop,
        f,
        tracker,
        selection,
        p -> ((BaseElitistPopulation.IntegerFitness<TestObject>) p).getFitnessOfMostFit(),
        3);
  }

  private void verifyIntegerElite(
      PopulationFitnessVector.IntegerFitness popVector,
      TestFitnessIntegerElitist f,
      ProgressTracker<TestObject> tracker,
      TestSelectionOp selection,
      ToIntFunction<Population<TestObject>> mostFitFitness,
      int elite) {
    @SuppressWarnings("unchecked")
    Population<TestObject> pop = (Population<TestObject>) popVector;

    assertTrue(tracker == pop.getProgressTracker());
    tracker = new ProgressTracker<TestObject>();
    pop.setProgressTracker(tracker);
    assertTrue(tracker == pop.getProgressTracker());

    pop.init();
    assertEquals(10, pop.size());
    assertEquals(10 - elite, pop.mutableSize());
    assertEquals(16, mostFitFitness.applyAsInt(pop));
    assertEquals(94, pop.getMostFit().getCost());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
    int[] expected = {1, 2, 3, 4, 5, 6, 5, 4, 3, 2};
    for (int i = 0; i < 10; i++) {
      // fitnesses of original before selection.
      assertEquals(expected[i] + 10, popVector.fitness(i));
    }
    assertFalse(selection.called);
    pop.select();
    assertTrue(selection.called);
    for (int i = 0; i < 10; i++) {
      // fitnesses of original before selection.
      assertEquals(expected[i] + 10, popVector.fitness(i));
      // subject to mutation to opposite order since we selected, which reversed.
      if (i < 10 - elite) {
        assertEquals(expected[9 - elite - i], pop.get(i).id);
      }
    }
    assertEquals(16, mostFitFitness.applyAsInt(pop));
    assertEquals(94, pop.getMostFit().getCost());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    pop.replace();
    // next line for elite case only
    int[] expectedNow = {4, 5, 6, 5, 6, 5, 4, 3, 2, 1};
    boolean[] gotit = new boolean[elite];
    for (int i = 0; i < 10; i++) {
      if (i < elite) {
        int fitness = popVector.fitness(i);
        for (int j = 0; j < elite; j++) {
          if (expectedNow[j] + 10 == fitness) {
            assertFalse(gotit[j]);
            gotit[j] = true;
            assertEquals(expectedNow[j] + 10, fitness, "index i=" + i);
          }
        }
      } else {
        assertEquals(expectedNow[i] + 10, popVector.fitness(i), "index i=" + i);
      }
    }
    for (int i = 0; i < elite; i++) { // (boolean b : gotit) {
      boolean b = gotit[i];
      assertTrue(b, "i=" + i + " ALL:" + gotit[0] + "," + gotit[1] + "," + gotit[2]);
    }
    assertEquals(16, mostFitFitness.applyAsInt(pop));
    assertEquals(94, pop.getMostFit().getCost());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    pop.select();
    for (int i = 0; i < 10; i++) {
      assertEquals(expectedNow[i] + 10, popVector.fitness(i));
      if (i < 10 - elite) {
        assertEquals(expectedNow[9 - elite - i], pop.get(i).id);
      }
    }
    assertEquals(16, mostFitFitness.applyAsInt(pop));
    assertEquals(94, pop.getMostFit().getCost());
    assertEquals(6, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    int adjustThisOne = 0;
    f.changeFitness(5);
    pop.updateFitness(adjustThisOne);
    pop.replace();

    expectedNow = new int[] {4, 5, 6, 4, 5, 6, 5, 4, 5, 6};
    gotit = new boolean[elite];
    boolean[] gotitagain = new boolean[elite];
    for (int i = 0; i < 10; i++) {
      if (i < elite) {
        int fitness = popVector.fitness(i);
        for (int j = 0; j < elite; j++) {
          if (expectedNow[j] + 10 == fitness) {
            assertFalse(gotit[j]);
            gotit[j] = true;
            assertEquals(expectedNow[j] + 10, fitness, "index i=" + i);
          }
        }
      } else if (i >= 10 - elite) {
        int fitness = popVector.fitness(i);
        for (int j = 0; j < elite; j++) {
          if (expectedNow[10 - elite + j] + 10 == fitness) {
            assertFalse(gotitagain[j]);
            gotitagain[j] = true;
            assertEquals(expectedNow[10 - elite + j] + 10, fitness, "index i=" + i);
          }
        }
      } else {
        int temp = i == elite + adjustThisOne ? 5 : 0;
        assertEquals(expectedNow[i] + 10 + temp, popVector.fitness(i), "index i=" + i);
      }
    }
    for (boolean b : gotit) {
      assertTrue(b);
    }
    for (boolean b : gotitagain) {
      assertTrue(b);
    }
    assertEquals(19, mostFitFitness.applyAsInt(pop));
    assertEquals(4, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    f.changeFitness(12);
    Population<TestObject> pop2 = pop.split();
    @SuppressWarnings("unchecked")
    PopulationFitnessVector.IntegerFitness popVector2 =
        (PopulationFitnessVector.IntegerFitness) pop2;

    // orginal should be same
    assertEquals(
        expectedNow[elite + adjustThisOne] + 10 + 5, popVector.fitness(elite + adjustThisOne));
    assertEquals(19, mostFitFitness.applyAsInt(pop));
    assertEquals(4, pop.getMostFit().getSolution().id);
    assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());

    // trackers should be same
    assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());

    pop2.init();
    assertEquals(10, pop2.size());
    assertEquals(10 - elite, pop2.mutableSize());
    for (int i = 0; i < 10; i++) {
      assertEquals(1 - i + 12 + 10, popVector2.fitness(i));
    }

    assertFalse(pop.evolutionIsPaused());
    assertFalse(pop2.evolutionIsPaused());
    tracker.stop();
    assertTrue(pop.evolutionIsPaused());
    assertTrue(pop2.evolutionIsPaused());
    tracker.start();
    assertFalse(pop.evolutionIsPaused());
    assertFalse(pop2.evolutionIsPaused());
    tracker.update(0, new TestObject(), true);
    assertTrue(pop.evolutionIsPaused());
    assertTrue(pop2.evolutionIsPaused());
  }
}
