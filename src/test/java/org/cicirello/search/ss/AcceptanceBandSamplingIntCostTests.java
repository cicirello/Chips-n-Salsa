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
public class AcceptanceBandSamplingIntCostTests extends SharedTestStochasticSampler {

  @Test
  public void testConstructorExceptions() {
    IntProblem problem = new IntProblem();
    IntHeuristic h = new IntHeuristic(problem, 5, 20);
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new AcceptanceBandSampling<Permutation>(h, -0.000001));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new AcceptanceBandSampling<Permutation>(h, 1.000001));
    NullPointerException thrownNull =
        assertThrows(
            NullPointerException.class, () -> new AcceptanceBandSampling<Permutation>(null, 0.5));
    thrownNull =
        assertThrows(
            NullPointerException.class,
            () -> new AcceptanceBandSampling<Permutation>(h, 0.5, null));
    IntHeuristic hNullProblem = new IntHeuristic(null, 5, 20);
    thrownNull =
        assertThrows(
            NullPointerException.class,
            () -> new AcceptanceBandSampling<Permutation>(hNullProblem, 0.5, null));
  }

  @Test
  public void testHeuristicNullIncremental() {
    for (int n = 0; n < 4; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristicNullIncremental h = new IntHeuristicNullIncremental(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithIntCosts() {
    for (int n = 0; n < 10; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithIntCostsMultipleSamples() {
    for (int n = 0; n < 10; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize(5);
      assertEquals(5, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize(2);
      assertEquals(7, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithIntCostsWithProgressTracker() {
    for (int n = 0; n < 10; n++) {
      ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch =
          new AcceptanceBandSampling<Permutation>(h, originalTracker);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      assertTrue(originalTracker == tracker);
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithIntCostsSplit() {
    for (int n = 0; n < 10; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> chOriginal = new AcceptanceBandSampling<Permutation>(h);
      AcceptanceBandSampling<Permutation> ch = chOriginal.split();
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testChooseBetaMax() {
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 1.0);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          for (int i = 0; i < k; i++) values[i] = 0.1 * (i + 1);
          double max = 0.1 * k;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertTrue(chosen < k);
          assertTrue(chosen >= 0);
          for (int i = 0; i < k; i++) {
            assertEquals(i, eq[i]);
          }
          for (int i = k; i < n; i++) {
            assertEquals(-1, eq[i]);
          }
        }
      }
    }
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 1.0);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          double max = 0.1 * k;
          for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertTrue(chosen < k);
          assertTrue(chosen >= 0);
          for (int i = 0; i < k; i++) {
            assertEquals(i, eq[i]);
          }
          for (int i = k; i < n; i++) {
            assertEquals(-1, eq[i]);
          }
        }
      }
    }
  }

  @Test
  public void testChooseBetaMin() {
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.0);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          for (int i = 0; i < k; i++) values[i] = 0.1 * (i + 1);
          double max = 0.1 * k;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertEquals(k - 1, chosen);
          assertEquals(k - 1, eq[0]);
          for (int i = 1; i < n; i++) {
            assertEquals(-1, eq[i]);
          }
        }
      }
    }
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.0);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          double max = 0.1 * k;
          for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertEquals(0, chosen);
          assertEquals(0, eq[0]);
          for (int i = 1; i < n; i++) {
            assertEquals(-1, eq[i]);
          }
        }
      }
    }
  }

  @Test
  public void testChooseBeta05() {
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.5);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          for (int i = 0; i < k; i++) values[i] = 0.1 * (i + 1);
          double max = 0.1 * k;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertTrue(chosen < k);
          assertTrue(chosen >= 0);
          for (int i = 0; i < (k + 2) / 2; i++) {
            String message = "increasing, i=" + i + ", k=" + k;
            assertEquals((k - 1) / 2 + i, eq[i], message);
          }
          for (int i = (k + 2) / 2; i < n; i++) {
            String message = "increasing, i=" + i + ", k=" + k;
            assertEquals(-1, eq[i], message);
          }
        }
      }
    }
    for (int n = 1; n <= 10; n++) {
      for (int k = 1; k <= n; k++) {
        IntProblem problem = new IntProblem();
        IntHeuristic h = new IntHeuristic(problem, n, 20);
        AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.5);
        for (int trial = 0; trial < 10; trial++) {
          double[] values = new double[n];
          double max = 0.1 * k;
          for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
          for (int i = k; i < n; i++) values[i] = 99999;
          int[] eq = new int[n];
          for (int i = 0; i < n; i++) eq[i] = -1;
          int chosen = ch.choose(values, k, max, eq);
          assertTrue(chosen < k);
          assertTrue(chosen >= 0);
          for (int i = 0; i < (k + 2) / 2; i++) {
            String message = "decreasing, i=" + i + ", k=" + k;
            assertEquals(i, eq[i], message);
          }
          for (int i = (k + 2) / 2; i < n; i++) {
            String message = "decreasing, i=" + i + ", k=" + k;
            assertEquals(-1, eq[i], message);
          }
        }
      }
    }
  }

  @Test
  public void testWithIntCostsBetaMax() {
    for (int n = 0; n < 10; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 1.0);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }

  @Test
  public void testWithIntCostsBetaMin() {
    for (int n = 0; n < 10; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n, 20);
      AcceptanceBandSampling<Permutation> ch = new AcceptanceBandSampling<Permutation>(h, 0.0);
      assertEquals(0, ch.getTotalRunLength());
      assertTrue(problem == ch.getProblem());
      ProgressTracker<Permutation> tracker = ch.getProgressTracker();
      SolutionCostPair<Permutation> solution = ch.optimize();
      assertEquals(1, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      Permutation p = solution.getSolution();
      assertEquals(n, p.length());
      solution = ch.optimize();
      assertEquals(2, ch.getTotalRunLength());
      assertEquals((n + 1) * n / 2, solution.getCost());
      assertEquals((n + 1) * n / 2, tracker.getCost());
      tracker = new ProgressTracker<Permutation>();
      ch.setProgressTracker(tracker);
      assertTrue(tracker == ch.getProgressTracker());
    }
  }
}
