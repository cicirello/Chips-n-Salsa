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
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.ReoptimizableMultistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.junit.jupiter.api.*;

/** JUnit tests for ParallelReoptimizableMultistarter using a single thread. */
public class ParallelReoptimizableMultistarterSingleThreadTests
    extends ParallelMultistarterValidator {

  private final ReoptimizeValidator validator;

  public ParallelReoptimizableMultistarterSingleThreadTests() {
    validator = new ReoptimizeValidator();
  }

  @Test
  public void testInterruptParallelOptimize() {
    class ParallelSearch implements Runnable {

      ParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;

      ParallelSearch() {
        ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        metaheuristics.add(new TestInterrupted(2, problem, tracker));
        metaheuristics.add(new TestInterrupted(3, problem, tracker));
        restarter = new ParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.optimize(10000);
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
  }

  @Test
  public void testInterruptParallelReoptimize() {
    class ParallelSearch implements Runnable {

      ParallelReoptimizableMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;

      ParallelSearch() {
        ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        metaheuristics.add(new TestInterrupted(2, problem, tracker));
        metaheuristics.add(new TestInterrupted(3, problem, tracker));
        restarter = new ParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
      }

      @Override
      public void run() {
        restarter.reoptimize(10000);
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
  }

  @Test
  public void testOptimizeExceptions() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter.close();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
  }

  @Test
  public void testOptimizeMetaheuristicThrowsException() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestOptThrowsExceptions> metaheuristics = new ArrayList<TestOptThrowsExceptions>();
    metaheuristics.add(new TestOptThrowsExceptions(1, problem, tracker));
    metaheuristics.add(new TestOptThrowsExceptions(2, problem, tracker));
    metaheuristics.add(new TestOptThrowsExceptions(3, problem, tracker));
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    SolutionCostPair<TestObject> solution = restarter.optimize(1);
    assertEquals(5, solution.getCost());
  }

  @Test
  public void testReoptimizeMetaheuristicThrowsException() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestProblem problem = new TestProblem();

    ArrayList<TestOptThrowsExceptions> metaheuristics = new ArrayList<TestOptThrowsExceptions>();
    metaheuristics.add(new TestOptThrowsExceptions(1, problem, tracker));
    metaheuristics.add(new TestOptThrowsExceptions(2, problem, tracker));
    metaheuristics.add(new TestOptThrowsExceptions(3, problem, tracker));
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(metaheuristics, 1);
    SolutionCostPair<TestObject> solution = restarter.reoptimize(1);
    assertEquals(5, solution.getCost());
  }

  @Test
  public void testReoptimizeExceptions() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter.close();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.reoptimize(1));
  }

  @Test
  public void testOptimizeStoppedFoundBest() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    long expected = restarter.getTotalRunLength();
    restarter.getProgressTracker().stop();
    restarter.optimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
    heur = new TestRestartedMetaheuristic();
    restarter = new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    expected = restarter.getTotalRunLength();
    // replaced call to deprecated setFoundBest()
    restarter.getProgressTracker().update(0, new TestObject(), true);
    restarter.optimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
  }

  @Test
  public void testReoptimizeStoppedFoundBest() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    long expected = restarter.getTotalRunLength();
    restarter.getProgressTracker().stop();
    restarter.reoptimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
    heur = new TestRestartedMetaheuristic();
    restarter = new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    expected = restarter.getTotalRunLength();
    // replaced call to deprecated setFoundBest()
    restarter.getProgressTracker().update(0, new TestObject(), true);
    restarter.reoptimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
  }

  @Test
  public void testSetProgressTrackerNull() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    restarter.setProgressTracker(null);
    assertEquals(tracker, restarter.getProgressTracker());
  }

  // optimize tests

  @Test
  public void testConstantLength_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
        validator.verifyConstantLength(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ParallelReoptimizableMultistarter<TestObject>(heur, 0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 0));
  }

  @Test
  public void testConstantLength_Split() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter1 =
            new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
        ParallelReoptimizableMultistarter<TestObject> restarter = restarter1.split();
        verifyConstantLengthSplit(restarter, heur, r, re);
        restarter1.close();
        restarter.close();
        assertTrue(restarter != restarter1);
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter1 =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter1.close();
    ParallelReoptimizableMultistarter<TestObject> restarter = restarter1.split();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
    restarter.close();
  }

  @Test
  public void testParallelReoptimizableMetaheuristic_Split() {
    for (int r = 1; r <= 100; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ReoptimizableMultistarter<TestObject> multi =
            new ReoptimizableMultistarter<TestObject>(heur, r);
        ParallelReoptimizableMetaheuristic<TestObject> restarter1 =
            new ParallelReoptimizableMetaheuristic<TestObject>(multi, 1);
        ParallelReoptimizableMetaheuristic<TestObject> restarter = restarter1.split();
        verifyConstantLengthSplitMetaheuristic(restarter, heur, r, re);
        restarter1.close();
        restarter.close();
        assertTrue(restarter != restarter1);
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ReoptimizableMultistarter<TestObject> multi =
        new ReoptimizableMultistarter<TestObject>(heur, 1);
    ParallelReoptimizableMetaheuristic<TestObject> restarter1 =
        new ParallelReoptimizableMetaheuristic<TestObject>(multi, 1);
    restarter1.close();
    ParallelReoptimizableMetaheuristic<TestObject> restarter = restarter1.split();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
    restarter.close();
  }

  @Test
  public void testSetProgressTracker() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    restarter.setProgressTracker(tracker);
    restarter.close();
    assertTrue(tracker == restarter.getProgressTracker());
    assertTrue(tracker == heur.getProgressTracker());
  }

  @Test
  public void testGetProblem() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelReoptimizableMultistarter<TestObject> restarter =
        new ParallelReoptimizableMultistarter<TestObject>(heur, 1, 1);
    restarter.close();
    assertTrue(heur.getProblem() == restarter.getProblem());
  }

  @Test
  public void testConstantLength_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(
                heur, new ConstantRestartSchedule(r), 1);
        validator.verifyConstantLength(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ParallelReoptimizableMultistarter<TestObject>(
                    heur, new ConstantRestartSchedule(1), 0));
  }

  @Test
  public void testConstantLength_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
        validator.verifyConstantLength(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ParallelReoptimizableMultistarter<TestObject>(
                    new TestRestartedMetaheuristic(), new ArrayList<RestartSchedule>()));
  }

  @Test
  public void testConstantLength_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
        validator.verifyConstantLength(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 1);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestProblem problem = new TestProblem();
              TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
              h.setProgressTracker(new ProgressTracker<TestObject>());
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic(problem));
              heurs.add(h);
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
            });
  }

  @Test
  public void testConstantLength_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
        validator.verifyConstantLength(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 1);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, 0);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestProblem problem = new TestProblem();
              TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
              h.setProgressTracker(new ProgressTracker<TestObject>());
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic(problem));
              heurs.add(h);
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, 1);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
                  new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs, 1);
            });
  }

  @Test
  public void testConstantLength_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ReoptimizableMultistarter<TestObject> multiStarter =
            new ReoptimizableMultistarter<TestObject>(heur, r);
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
        validator.verifyConstantLength(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
              ReoptimizableMultistarter<TestObject> multiStarter =
                  new ReoptimizableMultistarter<TestObject>(heur, 1);
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 0);
            });
  }

  @Test
  public void testConstantLength_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
            new ArrayList<ReoptimizableMultistarter<TestObject>>();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs);
        validator.verifyConstantLength(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
                  new ArrayList<ReoptimizableMultistarter<TestObject>>();
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
              heurs.add(new ReoptimizableMultistarter<TestObject>(heur, 1));
              heurs.add(
                  new ReoptimizableMultistarter<TestObject>(
                      new TestRestartedMetaheuristic(new TestProblem()), 1));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
                  new ArrayList<ReoptimizableMultistarter<TestObject>>();
              TestProblem p = new TestProblem();
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(p);
              heur.setProgressTracker(new ProgressTracker<TestObject>());
              heurs.add(new ReoptimizableMultistarter<TestObject>(heur, 1));
              heurs.add(
                  new ReoptimizableMultistarter<TestObject>(new TestRestartedMetaheuristic(p), 1));
              ParallelReoptimizableMultistarter<TestObject> restarter =
                  new ParallelReoptimizableMultistarter<TestObject>(heurs);
            });
  }

  @Test
  public void testStopped_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
          validator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 1);
          validator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          validator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          validator.verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          validator.verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
          validator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          validator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
          validator.verifyConstantLengthBest(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 1);
          validator.verifyConstantLengthBest(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          validator.verifyConstantLengthBest(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          validator.verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          validator.verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
          validator.verifyConstantLengthBest(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          validator.verifyConstantLengthBest(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  // reoptimize tests

  @Test
  public void testConstantLengthRe_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
        validator.verifyConstantLengthRe(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(
                heur, new ConstantRestartSchedule(r), 1);
        validator.verifyConstantLengthRe(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
        validator.verifyConstantLengthRe(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
        validator.verifyConstantLengthRe(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
        validator.verifyConstantLengthRe(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ReoptimizableMultistarter<TestObject> multiStarter =
            new ReoptimizableMultistarter<TestObject>(heur, r);
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
        validator.verifyConstantLengthRe(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
            new ArrayList<ReoptimizableMultistarter<TestObject>>();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs);
        validator.verifyConstantLengthRe(restarter, heur, r, re, 1);
        restarter.close();
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
          validator.verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 1);
          validator.verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          validator.verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          validator.verifyConstantLengthStoppedRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          validator.verifyConstantLengthStoppedRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
          validator.verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          validator.verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 1);
          validator.verifyConstantLengthBestRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 1);
          validator.verifyConstantLengthBestRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          validator.verifyConstantLengthBestRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          validator.verifyConstantLengthBestRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          validator.verifyConstantLengthBestRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 1);
          validator.verifyConstantLengthBestRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          validator.verifyConstantLengthBestRe(restarter, heur, r, re, early, i, true);
          restarter.close();
        }
      }
    }
  }

  private void verifyConstantLengthSplit(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(re * r, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }

  private void verifyConstantLengthSplitMetaheuristic(
      ParallelReoptimizableMetaheuristic<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(re * r, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }
}
