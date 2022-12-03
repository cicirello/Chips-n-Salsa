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
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for AdaptiveGeneration. */
public class AdaptiveGenerationTests {

  @Test
  public void testAdaptiveGeneration() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            N, init, f, selection, tracker, 0, 2);
    pop.init();

    TestMutation mutation = new TestMutation();
    TestCrossover crossover = new TestCrossover();

    AdaptiveGeneration<TestObject> ag = new AdaptiveGeneration<TestObject>(mutation, crossover);
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count + crossover.count, fitnessEvals);
    assertNotEquals(2 * N, fitnessEvals);
    assertNotEquals(0, fitnessEvals);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class, () -> new AdaptiveGeneration<TestObject>(mutation, null));
    thrown =
        assertThrows(
            NullPointerException.class, () -> new AdaptiveGeneration<TestObject>(null, crossover));
  }

  @Test
  public void testAdaptiveGenerationSplit() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestSelectionOp selection = new TestSelectionOp();
    TestFitnessInteger f = new TestFitnessInteger();
    TestInitializer init = new TestInitializer();
    final int N = 20;
    EvolvableParametersPopulation.IntegerFitness<TestObject> pop =
        new EvolvableParametersPopulation.IntegerFitness<TestObject>(
            N, init, f, selection, tracker, 0, 2);
    pop.init();

    TestMutation mutation = new TestMutation();
    TestCrossover crossover = new TestCrossover();

    AdaptiveGeneration<TestObject> ag =
        new AdaptiveGeneration<TestObject>(mutation, crossover).split();
    int fitnessEvals = ag.apply(pop);
    assertEquals(mutation.count + crossover.count, fitnessEvals);
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

  private static class TestProblemInteger implements IntegerCostOptimizationProblem<TestObject> {
    public int cost(TestObject c) {
      return 100 - c.id;
    }

    public int value(TestObject c) {
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

  private static class TestCrossover implements CrossoverOperator<TestObject> {

    private int count;

    @Override
    public void cross(TestObject c1, TestObject c2) {
      assertEquals(-1, c1.crossedWith);
      assertEquals(-1, c2.crossedWith);
      c1.crossedWith = c2.id;
      c2.crossedWith = c1.id;
      count += 2;
    }

    @Override
    public TestCrossover split() {
      return this;
    }
  }

  private static class TestObject implements Copyable<TestObject> {

    private static int nextId = 1;
    private int id;
    private int crossedWith;

    private boolean mutated;

    private TestObject() {
      id = nextId;
      nextId++;
      crossedWith = -1;
      mutated = false;
    }

    private TestObject(int id) {
      this.id = id;
      crossedWith = -1;
    }

    private TestObject(TestObject other) {
      id = other.id;
      crossedWith = other.crossedWith;
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
