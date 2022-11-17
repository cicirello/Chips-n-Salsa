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

package org.cicirello.search.hc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit tests for the hill climbers. */
public class HillClimberTests {

  private static final double EPSILON = 1e-10;

  // steepest descent

  @Test
  public void testConstructorsSteepestDescent() {
    TestOptInt problem = new TestOptInt();
    TestOpt problemDouble = new TestOpt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    assertEquals(problem, hc.pOptInt);
    assertEquals(null, hc.pOpt);
    assertEquals(mutation, hc.mutation);
    assertEquals(tracker, hc.tracker);

    hc = new SteepestDescentHillClimber<TestObject>(problemDouble, mutation, init, tracker);
    assertEquals(problemDouble, hc.pOpt);
    assertEquals(null, hc.pOptInt);
    assertEquals(mutation, hc.mutation);
    assertEquals(tracker, hc.tracker);

    NullPointerException thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new SteepestDescentHillClimber<TestObject>(
                    (OptimizationProblem<TestObject>) null, mutation, init, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problemDouble, null, init, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new SteepestDescentHillClimber<TestObject>(problemDouble, mutation, null, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problemDouble, mutation, init, null));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new SteepestDescentHillClimber<TestObject>(
                    (IntegerCostOptimizationProblem<TestObject>) null, mutation, init, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problem, null, init, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problem, mutation, null, tracker));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problem, mutation, init, null));
  }

  @Test
  public void testIntSteepest() {
    TestObject.setB(0);
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            new TestOptInt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    SolutionCostPair<TestObject> s = hc.optimize();
    assertEquals(41, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, s.getCost());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testIntSteepestStartSpecified() {
    TestObject.setB(0);
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            new TestOptInt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    TestObject start = new TestObject(95);
    SolutionCostPair<TestObject> s = hc.optimize(start);
    assertEquals(30, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, s.getCost());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testIntSteepestRestarts() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r - 1);
      SteepestDescentHillClimber<TestObject> hc =
          new SteepestDescentHillClimber<TestObject>(
              new TestOptInt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 41, hc.getTotalRunLength());
      assertEquals(2 * TestObject.OPT, s.getCost());
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2 * TestObject.OPT, ts.getCost());
      assertEquals(TestObject.OPT, ts.getSolution().getA());
      assertTrue(tracker.didFindBest());
    }
  }

  @Test
  public void testIntSteepestLocalOpt() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r);
      SteepestDescentHillClimber<TestObject> hc =
          new SteepestDescentHillClimber<TestObject>(
              new TestOptInt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 41, hc.getTotalRunLength());
      assertEquals(2 * TestObject.OPT + 200, s.getCost());
      assertEquals(TestObject.OPT + 100, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2 * TestObject.OPT + 200, ts.getCost());
      assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
      assertFalse(tracker.didFindBest());
    }
  }

  @Test
  public void testSteepestSplit() {
    for (int r = 1; r <= 5; r++) {
      for (int k = 0; k < r - 1; k++) {
        TestObject.setB(r);
        SteepestDescentHillClimber<TestObject> hc =
            new SteepestDescentHillClimber<TestObject>(
                new TestOptInt(), new TestMutator(), new TestObject(1000));
        assertEquals(0, hc.getTotalRunLength());
        SolutionCostPair<TestObject> s = null;
        for (int i = 0; i < r; i++) {
          if (k == i) {
            SteepestDescentHillClimber<TestObject> split = hc.split();
            split.optimize(1);
          } else {
            s = hc.optimize(1);
          }
        }
        assertEquals((r - 1) * 41, hc.getTotalRunLength());
        assertEquals(2 * TestObject.OPT + 200, s.getCost());
        assertEquals(TestObject.OPT + 100, s.getSolution().getA());
        ProgressTracker<TestObject> tracker = hc.getProgressTracker();
        SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
        assertEquals(2 * TestObject.OPT + 200, ts.getCost());
        assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
        assertFalse(tracker.didFindBest());
      }
    }
  }

  @Test
  public void testSteepestSplitDouble() {
    for (int r = 1; r <= 5; r++) {
      for (int k = 0; k < r - 1; k++) {
        TestObject.setB(r);
        SteepestDescentHillClimber<TestObject> hc =
            new SteepestDescentHillClimber<TestObject>(
                new TestOpt(), new TestMutator(), new TestObject(1000));
        assertEquals(0, hc.getTotalRunLength());
        SolutionCostPair<TestObject> s = null;
        for (int i = 0; i < r; i++) {
          if (k == i) {
            SteepestDescentHillClimber<TestObject> split = hc.split();
            split.optimize(1);
          } else {
            s = hc.optimize(1);
          }
        }
        assertEquals((r - 1) * 41, hc.getTotalRunLength());
        assertEquals(2 * TestObject.OPT + 200, s.getCostDouble(), EPSILON);
        assertEquals(TestObject.OPT + 100, s.getSolution().getA());
        ProgressTracker<TestObject> tracker = hc.getProgressTracker();
        SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
        assertEquals(2 * TestObject.OPT + 200, ts.getCostDouble(), EPSILON);
        assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
        assertFalse(tracker.didFindBest());
      }
    }
  }

  @Test
  public void testDoubleSteepest() {
    TestObject.setB(0);
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            new TestOpt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    SolutionCostPair<TestObject> s = hc.optimize();
    assertEquals(41, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testDoubleSteepestStartSpecified() {
    TestObject.setB(0);
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            new TestOpt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    TestObject start = new TestObject(95);
    SolutionCostPair<TestObject> s = hc.optimize(start);
    assertEquals(30, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testDoubleSteepestRestarts() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r - 1);
      SteepestDescentHillClimber<TestObject> hc =
          new SteepestDescentHillClimber<TestObject>(
              new TestOpt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 41, hc.getTotalRunLength());
      assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT, ts.getSolution().getA());
      assertTrue(tracker.didFindBest());
    }
  }

  @Test
  public void testDoubleSteepestLocalOpt() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r);
      SteepestDescentHillClimber<TestObject> hc =
          new SteepestDescentHillClimber<TestObject>(
              new TestOpt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 41, hc.getTotalRunLength());
      assertEquals(2.0 * TestObject.OPT + 200, s.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT + 100, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT + 200, ts.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
      assertFalse(tracker.didFindBest());
    }
  }

  // first descent

  @Test
  public void testStopFromAnotherThread() {
    class LongRunCallable implements Callable<SolutionCostPair<TestObject>> {

      FirstDescentHillClimber<TestObject> hc;
      volatile boolean started;

      LongRunCallable(FirstDescentHillClimber<TestObject> hc) {
        this.hc = hc;
        started = false;
      }

      @Override
      public SolutionCostPair<TestObject> call() {
        started = true;
        return hc.optimize(1000000);
      }
    }

    TestOptInt problem = new TestOptInt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    TestObject start = new TestObject(TestObject.OPT - 1);
    tracker.update(2 * TestObject.OPT - 2, start, false);

    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);

    ExecutorService threadPool = Executors.newFixedThreadPool(1);
    LongRunCallable thread = new LongRunCallable(hc);
    Future<SolutionCostPair<TestObject>> future = threadPool.submit(thread);
    SolutionCostPair<TestObject> solution = null;
    try {
      do {
        Thread.sleep(20);
      } while (!thread.started && problem.countEvals <= 1);
      tracker.stop();
      solution = future.get();
    } catch (InterruptedException ex) {
    } catch (ExecutionException ex) {
    }

    threadPool.shutdown();
    assertEquals(start, tracker.getSolution());
    assertTrue(tracker.getCost() < solution.getCost());
  }

  @Test
  public void testSetProgressTrackerFD() {
    TestOptInt problem = new TestOptInt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);

    ProgressTracker<TestObject> tracker2 = new ProgressTracker<TestObject>();
    hc.setProgressTracker(tracker2);
    assertEquals(tracker2, hc.getProgressTracker());
    hc.setProgressTracker(null);
    assertEquals(tracker2, hc.getProgressTracker());
  }

  @Test
  public void testStartsWithOptimalFD() {
    TestOptInt problem = new TestOptInt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    TestObject optimal = new TestObject(TestObject.OPT);
    SolutionCostPair<TestObject> solution = hc.optimize(optimal);
    assertTrue(tracker.didFindBest());
    assertEquals(optimal, tracker.getSolution());
    assertEquals(optimal, solution.getSolution());
    solution = hc.optimize(optimal);
    assertEquals(optimal, tracker.getSolution());
    assertNull(solution);
    solution = hc.optimize();
    assertEquals(optimal, tracker.getSolution());
    assertNull(solution);
    solution = hc.optimize(1);
    assertEquals(optimal, tracker.getSolution());
    assertNull(solution);
  }

  @Test
  public void testStoppedByAnotherThreadFD() {
    TestOptInt problem = new TestOptInt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    tracker.stop();
    TestObject start = new TestObject(2);
    SolutionCostPair<TestObject> solution = hc.optimize(start);
    assertNull(solution);
    solution = hc.optimize();
    assertNull(solution);
    solution = hc.optimize(1);
    assertNull(solution);
  }

  @Test
  public void testTrackerHasBetterSolutionFD() {
    TestOptInt problem = new TestOptInt();
    TestOpt problemDouble = new TestOpt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestObject start = new TestObject(TestObject.OPT - 1);
    tracker.update(2 * TestObject.OPT - 2, start, false);
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    SolutionCostPair<TestObject> solution = hc.optimize();
    assertEquals(start, tracker.getSolution());
    assertTrue(tracker.getCost() < solution.getCost());

    tracker = new ProgressTracker<TestObject>();
    start = new TestObject(TestObject.OPT - 1);
    tracker.update(2.0 * TestObject.OPT - 2, start, false);
    hc = new FirstDescentHillClimber<TestObject>(problemDouble, mutation, init, tracker);
    solution = hc.optimize();
    assertEquals(start, tracker.getSolution());
    assertTrue(tracker.getCostDouble() < solution.getCostDouble());
    solution = hc.optimize(2);
    assertEquals(start, tracker.getSolution());
    assertTrue(tracker.getCostDouble() < solution.getCostDouble());
  }

  @Test
  public void testConstructorsFirstDescent() {
    TestOptInt problem = new TestOptInt();
    TestOpt problemDouble = new TestOpt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    assertEquals(problem, hc.pOptInt);
    assertEquals(null, hc.pOpt);
    assertEquals(mutation, hc.mutation);
    assertEquals(tracker, hc.tracker);

    hc = new FirstDescentHillClimber<TestObject>(problemDouble, mutation, init, tracker);
    assertEquals(problemDouble, hc.pOpt);
    assertEquals(null, hc.pOptInt);
    assertEquals(mutation, hc.mutation);
    assertEquals(tracker, hc.tracker);
  }

  @Test
  public void testIntFirst() {
    TestObject.setB(0);
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(
            new TestOptInt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    SolutionCostPair<TestObject> s = hc.optimize();
    assertEquals(26, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, s.getCost());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testIntFirstStartSpecified() {
    TestObject.setB(0);
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(
            new TestOptInt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    TestObject start = new TestObject(95);
    SolutionCostPair<TestObject> s = hc.optimize(start);
    assertEquals(20, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, s.getCost());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testIntFirstRestarts() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r - 1);
      FirstDescentHillClimber<TestObject> hc =
          new FirstDescentHillClimber<TestObject>(
              new TestOptInt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 26, hc.getTotalRunLength());
      assertEquals(2 * TestObject.OPT, s.getCost());
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2 * TestObject.OPT, ts.getCost());
      assertEquals(TestObject.OPT, ts.getSolution().getA());
      assertTrue(tracker.didFindBest());
    }
  }

  @Test
  public void testIntFirstRestartsBestFoundBeforeLastRestart() {
    for (int r = 3; r <= 3; r++) {
      TestObject.setB(r - 1);
      FirstDescentHillClimber<TestObject> hc =
          new FirstDescentHillClimber<TestObject>(
              new TestOptInt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r + 1);
      assertEquals(r * 26, hc.getTotalRunLength());
      assertEquals(2 * TestObject.OPT, s.getCost());
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2 * TestObject.OPT, ts.getCost());
      assertEquals(TestObject.OPT, ts.getSolution().getA());
      assertTrue(tracker.didFindBest());
    }
  }

  @Test
  public void testIntFirstLocalOpt() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r);
      FirstDescentHillClimber<TestObject> hc =
          new FirstDescentHillClimber<TestObject>(
              new TestOptInt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 26, hc.getTotalRunLength());
      assertEquals(2 * TestObject.OPT + 200, s.getCost());
      assertEquals(TestObject.OPT + 100, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2 * TestObject.OPT + 200, ts.getCost());
      assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
      assertFalse(tracker.didFindBest());
    }
  }

  @Test
  public void testFirstSplit() {
    for (int r = 1; r <= 5; r++) {
      for (int k = 0; k < r - 1; k++) {
        TestObject.setB(r);
        FirstDescentHillClimber<TestObject> hc =
            new FirstDescentHillClimber<TestObject>(
                new TestOptInt(), new TestMutator(), new TestObject(1000));
        assertEquals(0, hc.getTotalRunLength());
        SolutionCostPair<TestObject> s = null;
        for (int i = 0; i < r; i++) {
          if (k == i) {
            FirstDescentHillClimber<TestObject> split = hc.split();
            split.optimize(1);
          } else {
            s = hc.optimize(1);
          }
        }
        assertEquals((r - 1) * 26, hc.getTotalRunLength());
        assertEquals(2 * TestObject.OPT + 200, s.getCost());
        assertEquals(TestObject.OPT + 100, s.getSolution().getA());
        ProgressTracker<TestObject> tracker = hc.getProgressTracker();
        SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
        assertEquals(2 * TestObject.OPT + 200, ts.getCost());
        assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
        assertFalse(tracker.didFindBest());
      }
    }
  }

  @Test
  public void testDoubleFirst() {
    TestObject.setB(0);
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(
            new TestOpt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    SolutionCostPair<TestObject> s = hc.optimize();
    assertEquals(26, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testDoubleFirstStartSpecified() {
    TestObject.setB(0);
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(
            new TestOpt(), new TestMutator(), new TestObject(1000));
    assertEquals(0, hc.getTotalRunLength());
    TestObject start = new TestObject(95);
    SolutionCostPair<TestObject> s = hc.optimize(start);
    assertEquals(20, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testDoubleFirstRestarts() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r - 1);
      FirstDescentHillClimber<TestObject> hc =
          new FirstDescentHillClimber<TestObject>(
              new TestOpt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 26, hc.getTotalRunLength());
      assertEquals(2.0 * TestObject.OPT, s.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT, ts.getSolution().getA());
      assertTrue(tracker.didFindBest());
    }
  }

  @Test
  public void testDoubleFirstLocalOpt() {
    for (int r = 1; r <= 5; r++) {
      TestObject.setB(r);
      FirstDescentHillClimber<TestObject> hc =
          new FirstDescentHillClimber<TestObject>(
              new TestOpt(), new TestMutator(), new TestObject(1000));
      assertEquals(0, hc.getTotalRunLength());
      SolutionCostPair<TestObject> s = hc.optimize(r);
      assertEquals(r * 26, hc.getTotalRunLength());
      assertEquals(2.0 * TestObject.OPT + 200, s.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT + 100, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT + 200, ts.getCostDouble(), EPSILON);
      assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
      assertFalse(tracker.didFindBest());
    }
  }

  private static class TestOpt implements OptimizationProblem<TestObject> {

    @Override
    public double cost(TestObject c) {
      return 2.0 * c.a;
    }

    @Override
    public double value(TestObject c) {
      return cost(c);
    }

    @Override
    public boolean isMinCost(double cost) {
      return cost == minCost();
    }

    @Override
    public double minCost() {
      return TestObject.OPT * 2.0;
    }
  }

  private static class TestOptInt implements IntegerCostOptimizationProblem<TestObject> {

    volatile int countEvals;

    @Override
    public int cost(TestObject c) {
      countEvals++;
      return 2 * c.a;
    }

    @Override
    public int value(TestObject c) {
      return cost(c);
    }

    @Override
    public boolean isMinCost(int cost) {
      return cost == minCost();
    }

    @Override
    public int minCost() {
      return TestObject.OPT * 2;
    }
  }

  private static class TestObject implements Copyable<TestObject>, Initializer<TestObject> {
    private int a;
    private int b;

    static final int OPT = 85;
    static int B;

    public static void setB(int b) {
      B = b;
    }

    public TestObject(int a) {
      this.a = a;
      b = 0;
    }

    public TestObject(int a, int b) {
      this.a = a;
      this.b = b;
    }

    public boolean equals(Object other) {
      TestObject to = (TestObject) other;
      return to.a == a && to.b == b;
    }

    public int getA() {
      return a;
    }

    @Override
    public TestObject copy() {
      return new TestObject(a, b);
    }

    @Override
    public TestObject createCandidateSolution() {
      TestObject s = new TestObject(100 * (B + 1), B);
      if (B > 0) B--;
      return s;
    }

    @Override
    public TestObject split() {
      return copy();
    }

    public MutationIterator getIter() {
      return new TestIter(this);
    }

    private class TestIter implements MutationIterator {
      private int original;
      private int[] neighbors;
      private int index;
      private int save;
      private TestObject t;
      private boolean rolled;

      public TestIter(TestObject t) {
        this.t = t;
        original = t.a;
        neighbors = new int[10];
        neighbors[0] = original > 100 * b + OPT ? original - 1 : original;
        for (int i = 1; i < 5; i++) {
          neighbors[i] = neighbors[i - 1] > 100 * b + OPT ? neighbors[i - 1] - 1 : neighbors[i - 1];
        }
        for (int i = 5; i < 10; i++) {
          neighbors[i] = neighbors[i - 1] + 1;
        }
        save = index = -1;
      }

      @Override
      public boolean hasNext() {
        return !rolled && index < neighbors.length - 1;
      }

      @Override
      public void nextMutant() {
        index++;
        t.a = neighbors[index];
      }

      @Override
      public void setSavepoint() {
        save = index;
      }

      @Override
      public void rollback() {
        if (!rolled) {
          if (save < 0) t.a = original;
          else t.a = neighbors[save];
          rolled = true;
        }
      }
    }
  }

  private static class TestMutator implements IterableMutationOperator<TestObject> {

    @Override
    public MutationIterator iterator(TestObject t) {
      return t.getIter();
    }

    @Override
    public TestMutator split() {
      return new TestMutator();
    }

    @Override
    public void mutate(TestObject t) {
      // deliberately empty....  not needed for this set of tests.
    }
  }
}
