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

import org.cicirello.search.Configurator;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.representations.BitVector;
import org.junit.jupiter.api.*;

/** JUnit test cases for TerminationStrategy interface. */
public class TerminationStrategyTests {

  @BeforeEach
  public void seed() {
    Configurator.configureRandomGenerator(42L);
  }

  @Test
  public void testGenerationsCauseTermination() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new InverseCostFitnessFunction<BitVector>(problem),
            M,
            new FitnessProportionalSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    SolutionCostPair<BitVector> solution = ga.optimize(5, s -> s.numCompletedGenerations() >= 10);
    // Make sure correct number of fitness evals
    assertEquals(n * 6, ga.getTotalRunLength());
    assertEquals(tracker.getCostDouble(), solution.getCostDouble());
    BitVector b = solution.getSolution();
    assertEquals(L, b.length());
    assertEquals(L, tracker.getSolution().length());
    assertEquals(b.countZeros(), solution.getCostDouble());
  }

  @Test
  public void testTerminationStrategyCausesTermination() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new InverseCostFitnessFunction<BitVector>(problem),
            M,
            new FitnessProportionalSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    SolutionCostPair<BitVector> solution = ga.optimize(10, s -> s.numCompletedGenerations() >= 5);
    // Make sure correct number of fitness evals
    assertEquals(n * 6, ga.getTotalRunLength());
    assertEquals(tracker.getCostDouble(), solution.getCostDouble());
    BitVector b = solution.getSolution();
    assertEquals(L, b.length());
    assertEquals(L, tracker.getSolution().length());
    assertEquals(b.countZeros(), solution.getCostDouble());
  }

  @Test
  public void testAnotherThreadPausedEvolutionBeforeOptimize() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new InverseCostFitnessFunction<BitVector>(problem),
            M,
            new FitnessProportionalSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    tracker.stop();
    SolutionCostPair<BitVector> solution = ga.optimize(5, s -> s.numCompletedGenerations() >= 10);
    assertNull(solution);
  }

  @Test
  public void testAnotherThreadPausedEvolutionDuringOptimization() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    final ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new InverseCostFitnessFunction<BitVector>(problem),
            M,
            new FitnessProportionalSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    SolutionCostPair<BitVector> solution =
        ga.optimize(
            5,
            s -> {
              if (s.numCompletedGenerations() == 1) {
                tracker.stop();
              }
              return s.numCompletedGenerations() >= 10;
            });
    // Make sure correct number of fitness evals
    assertEquals(n * 2, ga.getTotalRunLength());
    assertEquals(tracker.getCostDouble(), solution.getCostDouble());
    BitVector b = solution.getSolution();
    assertEquals(L, b.length());
    assertEquals(L, tracker.getSolution().length());
    assertEquals(b.countZeros(), solution.getCostDouble());
  }

  @Test
  public void testGenerationsCauseTerminationIntegerFitness() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new NegativeIntegerCostFitnessFunction<BitVector>(problem),
            M,
            new RandomSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    SolutionCostPair<BitVector> solution = ga.optimize(5, s -> s.numCompletedGenerations() >= 10);
    // Make sure correct number of fitness evals
    assertEquals(n * 6, ga.getTotalRunLength());
    assertEquals(tracker.getCostDouble(), solution.getCostDouble());
    BitVector b = solution.getSolution();
    assertEquals(L, b.length());
    assertEquals(L, tracker.getSolution().length());
    assertEquals(b.countZeros(), solution.getCostDouble());
  }

  @Test
  public void testTerminationStrategyCausesTerminationIntegerFitness() {
    double M = 0.5;
    int n = 10;
    int L = 32;
    OneMax problem = new OneMax();
    ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
    MutationOnlyGeneticAlgorithm ga =
        new MutationOnlyGeneticAlgorithm(
            n,
            L,
            new NegativeIntegerCostFitnessFunction<BitVector>(problem),
            M,
            new RandomSelection(),
            tracker);
    assertTrue(tracker == ga.getProgressTracker());
    assertTrue(problem == ga.getProblem());
    SolutionCostPair<BitVector> solution = ga.optimize(10, s -> s.numCompletedGenerations() >= 5);
    // Make sure correct number of fitness evals
    assertEquals(n * 6, ga.getTotalRunLength());
    assertEquals(tracker.getCostDouble(), solution.getCostDouble());
    BitVector b = solution.getSolution();
    assertEquals(L, b.length());
    assertEquals(L, tracker.getSolution().length());
    assertEquals(b.countZeros(), solution.getCostDouble());
  }
}
