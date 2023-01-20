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
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.junit.jupiter.api.*;

/** JUnit tests for ParallelMultistarter using a single thread. */
public class ParallelMultistarterSingleThreadTests extends ParallelMultistarterOneThreadValidator {

  @Test
  public void testInterruptParallelOptimize() {
    class ParallelSearch implements Runnable {

      ParallelMultistarter<TestObject> restarter;
      ArrayList<TestInterrupted> metaheuristics;

      ParallelSearch() {
        ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
        TestProblem problem = new TestProblem();

        metaheuristics = new ArrayList<TestInterrupted>();
        metaheuristics.add(new TestInterrupted(1, problem, tracker));
        metaheuristics.add(new TestInterrupted(2, problem, tracker));
        metaheuristics.add(new TestInterrupted(3, problem, tracker));
        restarter = new ParallelMultistarter<TestObject>(metaheuristics, 1);
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
  public void testOptimizeExceptions() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
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
    ParallelMultistarter<TestObject> restarter =
        new ParallelMultistarter<TestObject>(metaheuristics, 1);
    SolutionCostPair<TestObject> solution = restarter.optimize(1);
    assertEquals(5, solution.getCost());
  }

  @Test
  public void testOptimizeStoppedFoundBest() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
    long expected = restarter.getTotalRunLength();
    restarter.getProgressTracker().stop();
    restarter.optimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
    heur = new TestRestartedMetaheuristic();
    restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
    expected = restarter.getTotalRunLength();
    // replaced call to deprecated setFoundBest()
    restarter.getProgressTracker().update(0, new TestObject(), true);
    restarter.optimize(1);
    assertEquals(expected, restarter.getTotalRunLength());
    restarter.close();
  }

  @Test
  public void testSetProgressTrackerNull() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    restarter.setProgressTracker(null);
    assertEquals(tracker, restarter.getProgressTracker());
    restarter.close();
  }

  @Test
  public void testConstantLength_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, r, 1);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new ParallelMultistarter<TestObject>(heur, 0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new ParallelMultistarter<TestObject>(heur, 1, 0));
  }

  @Test
  public void testConstantLength_Split() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelMultistarter<TestObject> restarter1 =
            new ParallelMultistarter<TestObject>(heur, r, 1);
        ParallelMultistarter<TestObject> restarter = restarter1.split();
        verifyConstantLengthSplit(restarter, heur, r, re);
        restarter1.close();
        restarter.close();
        assertTrue(restarter != restarter1);
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter1 = new ParallelMultistarter<TestObject>(heur, 1, 1);
    restarter1.close();
    ParallelMultistarter<TestObject> restarter = restarter1.split();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
    restarter.close();
  }

  @Test
  public void testParallelMetaheuristic_Split() {
    for (int r = 1; r <= 100; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        Multistarter<TestObject> multi = new Multistarter<TestObject>(heur, r);
        ParallelMetaheuristic<TestObject> restarter1 =
            new ParallelMetaheuristic<TestObject>(multi, 1);
        ParallelMetaheuristic<TestObject> restarter = restarter1.split();
        verifyConstantLengthSplitMetaheuristic(restarter, heur, r, re);
        restarter1.close();
        restarter.close();
        assertTrue(restarter != restarter1);
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    Multistarter<TestObject> multi = new Multistarter<TestObject>(heur, 1);
    ParallelMetaheuristic<TestObject> restarter1 = new ParallelMetaheuristic<TestObject>(multi, 1);
    restarter1.close();
    ParallelMetaheuristic<TestObject> restarter = restarter1.split();
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> restarter.optimize(1));
    restarter.close();
  }

  @Test
  public void testSetProgressTracker() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    restarter.setProgressTracker(tracker);
    restarter.close();
    assertTrue(tracker == restarter.getProgressTracker());
    assertTrue(tracker == heur.getProgressTracker());
  }

  @Test
  public void testGetProblem() {
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
    restarter.close();
    assertTrue(heur.getProblem() == restarter.getProblem());
  }

  @Test
  public void testConstantLength_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
    TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(1), 0));
  }

  @Test
  public void testConstantLength_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, schedules);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ParallelMultistarter<TestObject>(
                    new TestRestartedMetaheuristic(), new ArrayList<RestartSchedule>()));
  }

  @Test
  public void testConstantLength_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heurs, schedules);
        verifyConstantLength(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestProblem problem = new TestProblem();
              TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
              h.setProgressTracker(new ProgressTracker<TestObject>());
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic(problem));
              heurs.add(h);
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, schedules);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
              ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
              schedules.add(new ConstantRestartSchedule(100));
              schedules.add(new ConstantRestartSchedule(200));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, schedules);
            });
  }

  @Test
  public void testConstantLength_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
        heurs.add(new TestRestartedMetaheuristic());
        ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
        verifyConstantLength(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, 0);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestProblem problem = new TestProblem();
              TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
              h.setProgressTracker(new ProgressTracker<TestObject>());
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic(problem));
              heurs.add(h);
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, 1);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Metaheuristic<TestObject>> heurs =
                  new ArrayList<Metaheuristic<TestObject>>();
              heurs.add(new TestRestartedMetaheuristic());
              heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs, 1);
            });
  }

  @Test
  public void testConstantLength_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(multiStarter, 1);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
              Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, 1);
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(multiStarter, 0);
            });
  }

  @Test
  public void testConstantLength_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        heurs.add(new Multistarter<TestObject>(heur, r));
        ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
              heurs.add(new Multistarter<TestObject>(heur, 1));
              heurs.add(
                  new Multistarter<TestObject>(
                      new TestRestartedMetaheuristic(new TestProblem()), 1));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs);
            });
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
              TestProblem p = new TestProblem();
              TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(p);
              heur.setProgressTracker(new ProgressTracker<TestObject>());
              heurs.add(new Multistarter<TestObject>(heur, 1));
              heurs.add(new Multistarter<TestObject>(new TestRestartedMetaheuristic(p), 1));
              ParallelMultistarter<TestObject> restarter =
                  new ParallelMultistarter<TestObject>(heurs);
            });
  }

  @Test
  public void testStopped_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, r, 1);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
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
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
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
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
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
          ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
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
          ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, r);
          verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
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
          Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(multiStarter, 1);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
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
          ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          heurs.add(new Multistarter<TestObject>(heur, r));
          ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
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
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, r, 1);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
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
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
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
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
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
          ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
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
          ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, r);
          verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
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
          Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(multiStarter, 1);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
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
          ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          heurs.add(new Multistarter<TestObject>(heur, r));
          ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  private void verifyConstantLength(
      ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(re * r, restarter.getTotalRunLength());
    assertEquals(re, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }

  private void verifyConstantLengthSplit(
      ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re) {
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
      ParallelMetaheuristic<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re) {
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

  private void verifyConstantLengthStopped(
      ParallelMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(early, restarter.getTotalRunLength());
    assertEquals(i, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    assertFalse(tracker.didFindBest());
    assertTrue(tracker.isStopped());
  }

  private void verifyConstantLengthBest(
      ParallelMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertEquals(1, pair.getCost());
    assertEquals(early, restarter.getTotalRunLength());
    assertEquals(i, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    assertTrue(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }
}
