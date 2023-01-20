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
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.junit.jupiter.api.*;

/** JUnit tests for ParallelMultistarter using 2 threads. */
public class ParallelMultistarterTwoThreadsTests extends ParallelMultistarterValidator {

  private final OptimizeValidator optValidator;

  public ParallelMultistarterTwoThreadsTests() {
    optValidator = new OptimizeValidator();
  }

  @Test
  public void testConstantLength_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, r, 2);
        optValidator.verifyConstantLength(restarter, heur, r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
        optValidator.verifyConstantLength(restarter, heur, r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heur, schedules);
        optValidator.verifyConstantLength(restarter, heur, r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(heurs, schedules);
        optValidator.verifyConstantLength(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
        optValidator.verifyConstantLength(
            restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
        ParallelMultistarter<TestObject> restarter =
            new ParallelMultistarter<TestObject>(multiStarter, 2);
        optValidator.verifyConstantLength(restarter, heur, r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
        TestProblem problem = new TestProblem();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(problem);
        heurs.add(new Multistarter<TestObject>(heur, r));
        TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic(problem);
        heur2.setProgressTracker(heur.getProgressTracker());
        heurs.add(new Multistarter<TestObject>(heur2, r));
        ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
        optValidator.verifyConstantLength(restarter, heur, r, re, 2);
        restarter.close();
      }
    }
  }

  @Test
  public void testStopped_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, r, 2);
          optValidator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, false);
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
              new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
          optValidator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, false);
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
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, schedules);
          optValidator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, schedules);
          optValidator.verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, r);
          optValidator.verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, false);
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
              new ParallelMultistarter<TestObject>(multiStarter, 2);
          optValidator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heurs.add(new Multistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heur2.setProgressTracker(heur.getProgressTracker());
          heurs.add(new Multistarter<TestObject>(heur2, r));
          ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
          optValidator.verifyConstantLengthStopped(restarter, heur, r, re, early, i, false);
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
              new ParallelMultistarter<TestObject>(heur, r, 2);
          optValidator.verifyConstantLengthBest(restarter, heur, r, re, early, i, false);
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
              new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
          optValidator.verifyConstantLengthBest(restarter, heur, r, re, early, i, false);
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
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heur, schedules);
          optValidator.verifyConstantLengthBest(restarter, heur, r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, schedules);
          optValidator.verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelMultistarter<TestObject> restarter =
              new ParallelMultistarter<TestObject>(heurs, r);
          optValidator.verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i, false);
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
              new ParallelMultistarter<TestObject>(multiStarter, 2);
          optValidator.verifyConstantLengthBest(restarter, heur, r, re, early, i, false);
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
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new Multistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new Multistarter<TestObject>(heur2, r));
          heur2.setProgressTracker(heur.getProgressTracker());
          ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
          optValidator.verifyConstantLengthBest(restarter, heur, r, re, early, i, false);
          restarter.close();
        }
      }
    }
  }
}
