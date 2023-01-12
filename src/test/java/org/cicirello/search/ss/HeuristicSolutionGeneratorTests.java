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

/** JUnit tests for the HeuristicSolutionGenerator class. */
public class HeuristicSolutionGeneratorTests extends SharedTestStochasticSampler {

  @Test
  public void testConstructorExceptions() {
    IntProblem problem = new IntProblem();
    IntHeuristic h = new IntHeuristic(problem, 3);
    NullPointerException thrownNull =
        assertThrows(
            NullPointerException.class, () -> new HeuristicSolutionGenerator<Permutation>(h, null));
    thrownNull =
        assertThrows(
            NullPointerException.class,
            () ->
                new HeuristicSolutionGenerator<Permutation>(
                    null, new ProgressTracker<Permutation>()));
  }

  @Test
  public void testBaseClassSplit() {
    for (int n = 0; n < 3; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicSolutionGenerator<Permutation> chOriginal =
          new HeuristicSolutionGenerator<Permutation>(h);
      HeuristicSolutionGenerator<Permutation> ch = chOriginal.split();
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      int evenStart = (n % 2 == 0) ? n - 2 : n - 1;
      int oddStart = (n % 2 == 0) ? n - 1 : n - 2;
      int i = 0;
      for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
        assertEquals(expected, p.get(i));
      }
      for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
        assertEquals(expected, p.get(i));
      }
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testTrackerStoppedBeforeOptimize() {
    IntProblem problem = new IntProblem();
    IntHeuristic h = new IntHeuristic(problem, 3);
    HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
    ProgressTracker<Permutation> tracker = ch.getProgressTracker();
    tracker.stop();
    assertNull(ch.optimize());
    assertEquals(0, ch.getTotalRunLength());
  }

  @Test
  public void testTrackerFoundBestBeforeOptimize() {
    IntProblem problem = new IntProblem();
    IntHeuristic h = new IntHeuristic(problem, 3);
    HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
    ProgressTracker<Permutation> tracker = ch.getProgressTracker();
    // replaced deprecated call to setFoundBest()
    tracker.update(0, new Permutation(1), true);
    assertNull(ch.optimize());
    assertEquals(0, ch.getTotalRunLength());
  }

  @Test
  public void testOptimizeFindsOptimalInt() {
    IntProblemOptimal problem = new IntProblemOptimal();
    IntHeuristic h = new IntHeuristic(problem, 1);
    HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
    ProgressTracker<Permutation> tracker = ch.getProgressTracker();
    SolutionCostPair<Permutation> solution = ch.optimize();
    assertEquals(solution.getSolution(), tracker.getSolution());
    assertTrue(tracker.containsIntCost());
    assertTrue(tracker.didFindBest());
    assertEquals(solution.getCost(), tracker.getCost());
  }

  @Test
  public void testOptimizeFindsOptimalDouble() {
    DoubleProblemOptimal problem = new DoubleProblemOptimal();
    DoubleHeuristic h = new DoubleHeuristic(problem, 1);
    HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
    ProgressTracker<Permutation> tracker = ch.getProgressTracker();
    SolutionCostPair<Permutation> solution = ch.optimize();
    assertEquals(solution.getSolution(), tracker.getSolution());
    assertFalse(tracker.containsIntCost());
    assertTrue(tracker.didFindBest());
    assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 1E-10);
  }

  private static class IntProblemOptimal extends IntProblem {
    // minCost will occur with a Permutation of length 1 (for testing)
    @Override
    public int minCost() {
      return 1;
    }
  }

  private static class DoubleProblemOptimal extends DoubleProblem {
    // minCost will occur with a Permutation of length 1 (for testing)
    @Override
    public double minCost() {
      return 1;
    }
  }
}
