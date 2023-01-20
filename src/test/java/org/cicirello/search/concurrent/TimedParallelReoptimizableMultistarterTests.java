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

package org.cicirello.search.concurrent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.ParallelVariableAnnealingLength;
import org.cicirello.search.restarts.ReoptimizableMultistarter;
import org.junit.jupiter.api.*;

/** JUnit tests for the TimedParallelReoptimizableMultistarter. */
public class TimedParallelReoptimizableMultistarterTests
    extends TimedParallelMultistarterValidator {

  @Test
  public void testParallelOptimizeSomeThreadFindsBest_Reopt() {
    class ParallelSearch implements Runnable {

      TimedParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;
      ProgressTracker<TestObject> tracker;

      ParallelSearch() {
        tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        restarter = new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.setTimeUnit(10);
        restarter.optimize(1000);
      }
    }

    ParallelSearch search = new ParallelSearch();
    Thread t = new Thread(search);
    t.start();
    while (search.metaheuristics.get(0).count < 1) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        break;
      }
    }
    // replaced deprecated call to setFoundBest()
    search.tracker.update(-10000.0, new TestObject(-10000), true);
    try {
      t.join(1000);
    } catch (InterruptedException ex) {
    }
    assertFalse(t.isAlive());
    search.restarter.close();
  }

  @Test
  public void testParallelReoptimizeSomeThreadFindsBest_Reopt() {
    class ParallelSearch implements Runnable {

      TimedParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;
      ProgressTracker<TestObject> tracker;

      ParallelSearch() {
        tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        restarter = new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.setTimeUnit(10);
        restarter.reoptimize(1000);
      }
    }

    ParallelSearch search = new ParallelSearch();
    Thread t = new Thread(search);
    t.start();
    while (search.metaheuristics.get(0).count < 1) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        break;
      }
    }
    // replaced deprecated call to setFoundBest()
    search.tracker.update(-10000.0, new TestObject(-10000), true);
    try {
      t.join(1000);
    } catch (InterruptedException ex) {
    }
    assertFalse(t.isAlive());
    search.restarter.close();
  }

  @Test
  public void testParallelOptimizeImprovementMade_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestImprovementMade> metaheuristics = new ArrayList<TestImprovementMade>();
    metaheuristics.add(new TestImprovementMade(1000, problem, tracker));
    metaheuristics.add(new TestImprovementMade(1001, problem, tracker));
    metaheuristics.add(new TestImprovementMade(1002, problem, tracker));
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    restarter.setTimeUnit(100);
    assertNotNull(restarter.optimize(1));
    restarter.close();
  }

  @Test
  public void testParallelReoptimizeImprovementMade_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestImprovementMade> metaheuristics = new ArrayList<TestImprovementMade>();
    metaheuristics.add(new TestImprovementMade(1000, problem, tracker));
    metaheuristics.add(new TestImprovementMade(1001, problem, tracker));
    metaheuristics.add(new TestImprovementMade(1002, problem, tracker));
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    restarter.setTimeUnit(50);
    assertNotNull(restarter.reoptimize(1));
    restarter.close();
  }

  @Test
  public void testInterruptParallelOptimize_Reopt() {
    class ParallelSearch implements Runnable {

      TimedParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;

      ParallelSearch() {
        ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        metaheuristics.add(new TestInterrupted(2, problem, tracker));
        metaheuristics.add(new TestInterrupted(3, problem, tracker));
        restarter = new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.setTimeUnit(10);
        restarter.optimize(1000);
      }
    }

    ParallelSearch search = new ParallelSearch();
    Thread t = new Thread(search);
    t.start();
    while (search.metaheuristics.get(0).count < 1) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        break;
      }
    }
    t.interrupt();
    try {
      t.join(1000);
    } catch (InterruptedException ex) {
    }
    assertFalse(t.isAlive());
    search.restarter.close();
  }

  @Test
  public void testInterruptParallelReoptimize_Reopt() {
    class ParallelSearch implements Runnable {

      TimedParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;

      ParallelSearch() {
        ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        metaheuristics.add(new TestInterrupted(2, problem, tracker));
        metaheuristics.add(new TestInterrupted(3, problem, tracker));
        restarter = new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.setTimeUnit(10);
        restarter.reoptimize(1000);
      }
    }

    ParallelSearch search = new ParallelSearch();
    Thread t = new Thread(search);
    t.start();
    while (search.metaheuristics.get(0).count < 1) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        break;
      }
    }
    t.interrupt();
    try {
      t.join(1000);
    } catch (InterruptedException ex) {
    }
    assertFalse(t.isAlive());
    search.restarter.close();
  }

  @Test
  public void testOptimizeMetaheuristicThrowsException_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestOptThrowsExceptions> metaheuristics = new ArrayList<TestOptThrowsExceptions>();
    metaheuristics.add(new TestOptThrowsExceptions(1, tracker, problem));
    metaheuristics.add(new TestOptThrowsExceptions(2, tracker, problem));
    metaheuristics.add(new TestOptThrowsExceptions(3, tracker, problem));
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    restarter.setTimeUnit(100);
    SolutionCostPair<TestObject> solution = restarter.optimize(1);
    assertTrue(solution == null || 0 == solution.getCost());
    restarter.close();
  }

  @Test
  public void testReoptimizeMetaheuristicThrowsException_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestOptThrowsExceptions> metaheuristics = new ArrayList<TestOptThrowsExceptions>();
    metaheuristics.add(new TestOptThrowsExceptions(1, tracker, problem));
    metaheuristics.add(new TestOptThrowsExceptions(2, tracker, problem));
    metaheuristics.add(new TestOptThrowsExceptions(3, tracker, problem));
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    restarter.setTimeUnit(100);
    SolutionCostPair<TestObject> solution = restarter.reoptimize(1);
    assertTrue(solution == null || 0 == solution.getCost());
    restarter.close();
  }

  @Test
  public void testOptimizeExceptions_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(1, tracker, problem);
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter.close();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
    thrown = assertThrows(IllegalStateException.class, () -> restarter.reoptimize(1));
  }

  @Test
  public void testSetProgressTrackerNull_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(1, tracker, problem);
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter.setProgressTracker(null);
    assertEquals(tracker, restarter.getProgressTracker());
    restarter.close();
  }

  @Test
  public void testOptimizeStoppedFoundBest_Reopt() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(1, tracker, problem);
    TimedParallelReoptimizableMultistarter<TestObject> restarter =
        new TimedParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    long expected = restarter.getTotalRunLength();
    // replaced call to deprecated setFoundBest()
    restarter.getProgressTracker().update(0, new TestObject(0), true);
    restarter.optimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.reoptimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
  }

  @Test
  public void testSetTimeUnitException_Reopt() {
    final int T = 1;
    ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(T);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= T; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    List<ParallelVariableAnnealingLength> schedules =
        ParallelVariableAnnealingLength.createRestartSchedules(T);
    final TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules);
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> tpm.setTimeUnit(0));
    tpm.close();
  }

  @Test
  public void testTimedParallelMultistarterVariousConstructorsExceptions_Reopt() {
    final int T = 3;
    ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(T);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= T; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    List<ParallelVariableAnnealingLength> schedules =
        ParallelVariableAnnealingLength.createRestartSchedules(T + 1);
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimedParallelReoptimizableMultistarter<TestObject>(searches, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              searches.add(
                  new TestRestartedMetaheuristic(
                      T + 1, new ProgressTracker<TestObject>(), problem));
              new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMultistarter<TestObject>> starters =
                  new ArrayList<ReoptimizableMultistarter<TestObject>>(T);
              for (int i = 0; i <= T; i++) {
                starters.add(new ReoptimizableMultistarter<TestObject>(searches.get(i), 1000));
              }
              new TimedParallelReoptimizableMultistarter<TestObject>(starters);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              searches.set(T, new TestRestartedMetaheuristic(T + 1, tracker, new TestProblem()));
              new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMultistarter<TestObject>> starters =
                  new ArrayList<ReoptimizableMultistarter<TestObject>>(T);
              for (int i = 0; i <= T; i++) {
                starters.add(new ReoptimizableMultistarter<TestObject>(searches.get(i), 1000));
              }
              new TimedParallelReoptimizableMultistarter<TestObject>(starters);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimedParallelReoptimizableMultistarter<TestObject>(searches.get(0), 0, T));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimedParallelReoptimizableMultistarter<TestObject>(searches.get(0), 1, 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new TimedParallelReoptimizableMultistarter<TestObject>(
                    searches.get(0), new ConstantRestartSchedule(1000), 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new TimedParallelReoptimizableMultistarter<TestObject>(
                    new ReoptimizableMultistarter<TestObject>(searches.get(0), 1000), 0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new TimedParallelReoptimizableMultistarter<TestObject>(
                    searches.get(0), new ArrayList<ParallelVariableAnnealingLength>()));
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterVariousConstructors() {
    final int T = 3;
    ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(T);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= T; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    List<ParallelVariableAnnealingLength> schedules =
        ParallelVariableAnnealingLength.createRestartSchedules(T);
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules);
    tpm.close();
    tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches.get(0), 1000, T);
    tpm.close();
    tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(
            searches.get(0), new ConstantRestartSchedule(1000), T);
    tpm.close();
    tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches.get(0), schedules);
    tpm.close();
    tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(
            new ReoptimizableMultistarter<TestObject>(searches.get(0), 1000), T);
    tpm.close();
    ArrayList<ReoptimizableMultistarter<TestObject>> starters =
        new ArrayList<ReoptimizableMultistarter<TestObject>>(T);
    for (int i = 0; i < T; i++) {
      starters.add(new ReoptimizableMultistarter<TestObject>(searches.get(0), 1000));
    }
    tpm = new TimedParallelReoptimizableMultistarter<TestObject>(starters);
    tpm.close();
    TimedParallelReoptimizableMultistarter<TestObject> split = tpm.split();
    split.close();
    assertTrue(split != tpm);
    tpm = new TimedParallelReoptimizableMultistarter<TestObject>(starters);
    split = tpm.split();
    tpm.close();
    split.close();
    assertTrue(split != tpm);
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterSetProgressTracker() {
    final int T = 3;
    ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(T);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= T; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    List<ParallelVariableAnnealingLength> schedules =
        ParallelVariableAnnealingLength.createRestartSchedules(T);
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, schedules);
    ProgressTracker<TestObject> tracker2 = new ProgressTracker<TestObject>();
    tpm.setProgressTracker(tracker2);
    tpm.close();
    assertTrue(tracker2 == tpm.getProgressTracker());
    for (TestRestartedMetaheuristic s : searches) {
      assertTrue(tracker2 == s.getProgressTracker());
    }
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterOne() {
    int numThreads = 1;
    ArrayList<TestRestartedMetaheuristic> searches =
        new ArrayList<TestRestartedMetaheuristic>(numThreads);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= numThreads; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
    assertEquals(1000, tpm.getTimeUnit());
    tpm.setTimeUnit(10);
    assertEquals(10, tpm.getTimeUnit());
    assertTrue(tracker == tpm.getProgressTracker());
    assertTrue(problem == tpm.getProblem());
    assertEquals(0, tpm.getTotalRunLength());
    assertNull(tpm.getSearchHistory());
    long time1 = System.nanoTime();
    SolutionCostPair<TestObject> solution = tpm.optimize(8);
    long time2 = System.nanoTime();
    int combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.reoptimizeCalled);
      assertTrue(search.optimizeCalled > 0);
      assertTrue(search.totalRunLength >= (search.optimizeCalled - 1) * 1001);
      assertTrue(search.totalRunLength <= search.optimizeCalled * 1001);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
    assertEquals(8, history.size());
    for (int i = 1; i < history.size(); i++) {
      assertTrue(history.get(i).getCostDouble() <= history.get(i - 1).getCostDouble());
      assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
      TestObject s = history.get(i).getSolution();
      if (s != null) {
        assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
      }
    }
    assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
    long actualRunTime = time2 - time1;
    assertTrue(
        actualRunTime >= 80000000,
        "verifying runtime, actual=" + actualRunTime + " ns, should be at least 80000000 ns");

    // verify can call optimize again
    solution = tpm.optimize(1);
    assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
    combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.reoptimizeCalled);
      assertTrue(search.optimizeCalled > 0);
      String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
      assertTrue(search.totalRunLength >= (search.optimizeCalled - 2) * 1001, msg);
      assertTrue(search.totalRunLength <= search.optimizeCalled * 1001, msg);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    history = tpm.getSearchHistory();
    assertEquals(1, history.size());
    assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
    TestObject s = history.get(0).getSolution();
    if (s != null) {
      assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
    }

    // Close the parallel multistarter
    tpm.close();
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterThree() {
    int numThreads = 3;
    final int NUM_TIME_CYCLES = 20;
    ArrayList<TestRestartedMetaheuristic> searches =
        new ArrayList<TestRestartedMetaheuristic>(numThreads);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= numThreads; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
    assertEquals(1000, tpm.getTimeUnit());
    tpm.setTimeUnit(10);
    assertEquals(10, tpm.getTimeUnit());
    assertTrue(tracker == tpm.getProgressTracker());
    assertTrue(problem == tpm.getProblem());
    assertEquals(0, tpm.getTotalRunLength());
    assertNull(tpm.getSearchHistory());
    long time1 = System.nanoTime();
    SolutionCostPair<TestObject> solution = tpm.optimize(NUM_TIME_CYCLES);
    long time2 = System.nanoTime();
    int combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.reoptimizeCalled);
      assertTrue(search.optimizeCalled > 0);
      assertTrue(search.totalRunLength >= (search.optimizeCalled - 1) * 1001);
      assertTrue(search.totalRunLength <= search.optimizeCalled * 1001);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
    assertEquals(NUM_TIME_CYCLES, history.size());
    for (int i = 1; i < history.size(); i++) {
      assertTrue(history.get(i).getCostDouble() <= history.get(i - 1).getCostDouble());
      assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
      TestObject s = history.get(i).getSolution();
      if (s != null) {
        assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
      }
    }
    assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
    long actualRunTime = time2 - time1;
    assertTrue(
        actualRunTime >= 80000000,
        "verifying runtime, actual=" + actualRunTime + " ns, should be at least 80000000 ns");

    // verify can call optimize again
    tpm.setTimeUnit(20);
    solution = tpm.optimize(1);
    assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
    combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.reoptimizeCalled);
      assertTrue(search.optimizeCalled > 0);
      String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
      assertTrue(search.totalRunLength >= (search.optimizeCalled - 2) * 1001, msg);
      assertTrue(search.totalRunLength <= search.optimizeCalled * 1001, msg);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    history = tpm.getSearchHistory();
    assertEquals(1, history.size());
    assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
    TestObject s = history.get(0).getSolution();
    if (s != null) {
      assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
    }

    // Close the parallel multistarter
    tpm.close();
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterOneReopt() {
    int numThreads = 1;
    ArrayList<TestRestartedMetaheuristic> searches =
        new ArrayList<TestRestartedMetaheuristic>(numThreads);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= numThreads; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
    assertEquals(1000, tpm.getTimeUnit());
    tpm.setTimeUnit(10);
    assertEquals(10, tpm.getTimeUnit());
    assertTrue(tracker == tpm.getProgressTracker());
    assertTrue(problem == tpm.getProblem());
    assertEquals(0, tpm.getTotalRunLength());
    assertNull(tpm.getSearchHistory());
    long time1 = System.nanoTime();
    SolutionCostPair<TestObject> solution = tpm.reoptimize(8);
    long time2 = System.nanoTime();
    int combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.optimizeCalled);
      assertTrue(search.reoptimizeCalled > 0);
      assertTrue(search.totalRunLength >= (search.reoptimizeCalled - 1) * 1001);
      assertTrue(search.totalRunLength <= search.reoptimizeCalled * 1001);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
    assertEquals(8, history.size());
    for (int i = 1; i < history.size(); i++) {
      assertTrue(history.get(i).getCostDouble() <= history.get(i - 1).getCostDouble());
      assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
      TestObject s = history.get(i).getSolution();
      if (s != null) {
        assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
      }
    }
    assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
    long actualRunTime = time2 - time1;
    assertTrue(
        actualRunTime >= 80000000,
        "verifying runtime, actual=" + actualRunTime + " ns, should be at least 80000000 ns");

    // verify can call reoptimize again
    solution = tpm.reoptimize(1);
    assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
    combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.optimizeCalled);
      assertTrue(search.reoptimizeCalled > 0);
      String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.reoptimizeCalled;
      assertTrue(search.totalRunLength >= (search.reoptimizeCalled - 2) * 1001, msg);
      assertTrue(search.totalRunLength <= search.reoptimizeCalled * 1001, msg);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    history = tpm.getSearchHistory();
    assertEquals(1, history.size());
    assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
    TestObject s = history.get(0).getSolution();
    if (s != null) {
      assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
    }

    // Close the parallel multistarter
    tpm.close();
  }

  @Test
  public void testTimedParallelReoptimizableMultistarterThreeReopt() {
    int numThreads = 3;
    ArrayList<TestRestartedMetaheuristic> searches =
        new ArrayList<TestRestartedMetaheuristic>(numThreads);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();
    for (int i = 1; i <= numThreads; i++) {
      searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
    }
    TimedParallelReoptimizableMultistarter<TestObject> tpm =
        new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
    assertEquals(1000, tpm.getTimeUnit());
    int timeUnit = 20;
    tpm.setTimeUnit(timeUnit);
    assertEquals(timeUnit, tpm.getTimeUnit());
    assertTrue(tracker == tpm.getProgressTracker());
    assertTrue(problem == tpm.getProblem());
    assertEquals(0, tpm.getTotalRunLength());
    assertNull(tpm.getSearchHistory());
    int reoptCount = 8;
    long time1 = System.nanoTime();
    SolutionCostPair<TestObject> solution = tpm.reoptimize(reoptCount);
    long time2 = System.nanoTime();
    int combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.optimizeCalled);
      assertTrue(search.reoptimizeCalled > 0);
      assertTrue(search.totalRunLength >= (search.reoptimizeCalled - 1) * 1001);
      assertTrue(search.totalRunLength <= search.reoptimizeCalled * 1001);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
    assertEquals(reoptCount, history.size());
    for (int i = 1; i < history.size(); i++) {
      assertTrue(history.get(i).getCostDouble() <= history.get(i - 1).getCostDouble());
      assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
      TestObject s = history.get(i).getSolution();
      if (s != null) {
        assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
      }
    }
    assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
    long actualRunTime = time2 - time1;
    int minRunTime = timeUnit * reoptCount * 1000000;
    assertTrue(
        actualRunTime >= minRunTime,
        "verifying runtime, actual="
            + actualRunTime
            + " ns, should be at least "
            + minRunTime
            + " ns");

    // verify can call reoptimize again
    solution = tpm.reoptimize(1);
    assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
    combinedRun = 0;
    for (TestRestartedMetaheuristic search : searches) {
      assertEquals(0, search.optimizeCalled);
      assertTrue(search.reoptimizeCalled > 0);
      String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.reoptimizeCalled;
      assertTrue(search.totalRunLength >= (search.reoptimizeCalled - 2) * 1001, msg);
      assertTrue(search.totalRunLength <= search.reoptimizeCalled * 1001, msg);
      combinedRun += search.totalRunLength;
    }
    assertEquals(combinedRun, tpm.getTotalRunLength());
    history = tpm.getSearchHistory();
    assertEquals(1, history.size());
    assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
    TestObject s = history.get(0).getSolution();
    if (s != null) {
      assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
    }

    // Close the parallel multistarter
    tpm.close();
  }
}
