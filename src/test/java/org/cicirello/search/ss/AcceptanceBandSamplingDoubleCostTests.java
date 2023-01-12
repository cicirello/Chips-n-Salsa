/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

package org.cicirello.search.ss;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.junit.jupiter.api.*;

/** JUnit tests for AcceptanceBandSampling for problems with int-valued costs. */
public class AcceptanceBandSamplingDoubleCostTests extends SharedTestStochasticSampler {

  @Test
  public void testWithDoubleCosts() {
    for (int n = 0; n < 10; n++) {
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithDoubleCostsMultipleSamples() {
    for (int n = 0; n < 10; n++) {
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize(5);
      assertEquals(5, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize(2);
      assertEquals(7, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithDoubleCostsWithProgressTracker() {
    for (int n = 0; n < 10; n++) {
      ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch =
          new AcceptanceBandSampling<Permutation>(h, originalTracker);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      assertTrue(originalTracker == tracker);
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithDoubleCostsSplit() {
    for (int n = 0; n < 10; n++) {
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> chOriginal = new AcceptanceBandSampling<Permutation>(h);
      AcceptanceBandSampling<Permutation> ch = chOriginal.split();
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithDoubleCostsBetaMax() {
    for (int n = 0; n < 10; n++) {
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 1.0);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithDoubleCostsBetaMin() {
    for (int n = 0; n < 10; n++) {
      DoubleProblem problem = new DoubleProblem();
      DoubleHeuristic h = new DoubleHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.0);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCostDouble(), 1E-10);
      assertEquals((n + 1) * n / 2, tracker.getCostDouble(), 1E-10);
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }
}
