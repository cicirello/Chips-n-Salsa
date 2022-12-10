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
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for AdaptiveMutationOnlyGeneration. */
public class AdaptiveMutationOnlyGenerationTests {

  @Test
  public void testAdaptiveMutationOnlyGeneration() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            N, init, f, selection, tracker, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation);
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
    assertNotEquals(0, fitnessEvals);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new AdaptiveMutationOnlyGeneration<TestObject>(
                    (MutationOperator<TestObject>) null));
  }

  @Test
  public void testAdaptiveMutationOnlyGenerationDouble() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersPopulation.DoubleFitness<TestObject>(
            N, init, f, selection, tracker, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation);
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
    assertNotEquals(0, fitnessEvals);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new AdaptiveMutationOnlyGeneration<TestObject>(
                    (MutationOperator<TestObject>) null));
  }

  @Test
  public void testAdaptiveMutationOnlyGenerationSplit() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            N, init, f, selection, tracker, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation).split();
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
  }

  @Test
  public void testAdaptiveMutationOnlyGenerationElitist() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersElitistPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
            N + 1, init, f, selection, tracker, 1, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation);
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
    assertNotEquals(0, fitnessEvals);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new AdaptiveMutationOnlyGeneration<TestObject>(
                    (MutationOperator<TestObject>) null));
  }

  @Test
  public void testAdaptiveMutationOnlyGenerationDoubleElitist() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessDouble f = new TestFitnessDouble();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersElitistPopulation.DoubleFitness<TestObject> pop =
        new EvolvableParametersElitistPopulation.DoubleFitness<TestObject>(
            N + 1, init, f, selection, tracker, 1, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation);
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
    assertNotEquals(0, fitnessEvals);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new AdaptiveMutationOnlyGeneration<TestObject>(
                    (MutationOperator<TestObject>) null));
  }

  @Test
  public void testAdaptiveMutationOnlyGenerationSplitElitist() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersElitistPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersElitistPopulation.IntegerFitness<TestObject>(
            N + 1, init, f, selection, tracker, 1, 1);
    pop.init();

    TestMutation mutation = new TestMutation();

    AdaptiveMutationOnlyGeneration<TestObject> ag =
        new AdaptiveMutationOnlyGeneration<TestObject>(mutation).split();
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
  }

  private static class TestSelectionOp implements SelectionOperator {

    boolean called;
    int initCalledWith;

    public TestSelectionOp() {
      called = false;
    }

    @Override
    public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
      int next = selected.length - 1;
      for (int i = 0; i < selected.length; i++) {
        selected[i] = next;
        next--;
      }
      called = true;
    }

    @Override
    public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
      int next = selected.length - 1;
      for (int i = 0; i < selected.length; i++) {
        selected[i] = next;
        next--;
      }
      called = true;
    }

    @Override
    public TestSelectionOp split() {
      return new TestSelectionOp();
    }

    @Override
    public void init(int generations) {
      initCalledWith = generations;
    }
  }

  private static class TestFitnessInteger implements FitnessFunction.Integer<TestObject> {

    private TestProblemInteger problem;
    private int adjustment;

    public TestFitnessInteger() {
      problem = new TestProblemInteger();
    }

    public int fitness(TestObject c) {
      return c.id + 10 + adjustment;
    }

    public Problem<TestObject> getProblem() {
      return problem;
    }

    public void changeFitness(int adjustment) {
      this.adjustment = adjustment;
    }
  }

  private static class TestFitnessDouble implements FitnessFunction.Double<TestObject> {

    private TestProblemDouble problem;
    private int adjustment;

    public TestFitnessDouble() {
      problem = new TestProblemDouble();
    }

    public double fitness(TestObject c) {
      return c.id + 10 + adjustment;
    }

    public Problem<TestObject> getProblem() {
      return problem;
    }

    public void changeFitness(int adjustment) {
      this.adjustment = adjustment;
    }
  }

  private static class TestProblemInteger implements IntegerCostOptimizationProblem<TestObject> {
    public int cost(TestObject c) {
      return 100 - c.id;
    }

    public int value(TestObject c) {
      return cost(c);
    }
  }

  private static class TestProblemDouble implements OptimizationProblem<TestObject> {
    public double cost(TestObject c) {
      return 100 - c.id;
    }

    public double value(TestObject c) {
      return cost(c);
    }
  }

  private static class TestInitializer implements Initializer<TestObject> {

    public TestObject createCandidateSolution() {
      TestObject obj = new TestObject();
      return obj;
    }

    public TestInitializer split() {
      return this;
    }
  }

  private static class TestMutation implements MutationOperator<TestObject> {

    private int count;

    @Override
    public void mutate(TestObject c) {
      assertFalse(c.mutated);
      c.mutated = true;
      count++;
    }

    @Override
    public TestMutation split() {
      return this;
    }
  }

  private static class TestObject implements Copyable<TestObject> {

    private static int nextId = 1;
    private int id;
    private boolean mutated;

    private TestObject() {
      id = nextId;
      nextId++;
      mutated = false;
    }

    private TestObject(int id) {
      this.id = id;
    }

    private TestObject(TestObject other) {
      id = other.id;
      mutated = other.mutated;
    }

    public TestObject copy() {
      return new TestObject(this);
    }

    @Override
    public int hashCode() {
      return id;
    }

    @Override
    public boolean equals(Object other) {
      return id == ((TestObject) other).id;
    }
  }
}
