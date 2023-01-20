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

import java.util.SplittableRandom;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** Test validation common to multiple test classes for testing timed parallel multistarters. */
public class TimedParallelMultistarterValidator {

  static class TestOptThrowsExceptions extends TestRestartedMetaheuristic {

    boolean throwException;
    boolean returnsNull;

    public TestOptThrowsExceptions(
        int id, ProgressTracker<TestObject> tracker, TestProblem problem) {
      super(id, tracker, problem);
      throwException = id == 2;
      returnsNull = id == 3;
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      optimizeCalled++;
      if (throwException) {
        throw new RuntimeException("Testing exception handling");
      } else if (returnsNull) {
        return null;
      } else {
        TestObject obj = new TestObject(0);
        return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
      }
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      reoptimizeCalled++;
      if (throwException) {
        throw new RuntimeException("Testing exception handling");
      } else if (returnsNull) {
        return null;
      } else {
        TestObject obj = new TestObject(0);
        return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
      }
    }
  }

  static class TestRestartedMetaheuristic implements ReoptimizableMetaheuristic<TestObject> {

    private ProgressTracker<TestObject> tracker;
    int id;
    public volatile int optimizeCalled;
    public volatile int reoptimizeCalled;
    private SplittableRandom r;
    public TestProblem problem;
    public volatile int totalRunLength;

    public TestRestartedMetaheuristic(
        int id, ProgressTracker<TestObject> tracker, TestProblem problem) {
      this.id = id;
      this.tracker = tracker;
      this.problem = problem;
      optimizeCalled = 0;
      reoptimizeCalled = 0;
      totalRunLength = 0;
      r = new SplittableRandom(id);
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      optimizeCalled++;
      TestObject threadBest = new TestObject(r.nextInt(10000));
      totalRunLength++;
      double bestCost = problem.cost(threadBest);
      tracker.update(bestCost, threadBest, false);
      while (!tracker.isStopped() && runLength > 0) {
        runLength--;
        TestObject candidate = new TestObject(r.nextInt(10000));
        totalRunLength++;
        double cost = problem.cost(candidate);
        if (cost < bestCost) {
          threadBest = candidate;
          bestCost = cost;
          tracker.update(bestCost, threadBest, false);
        }
      }
      return new SolutionCostPair<TestObject>(threadBest, problem.cost(threadBest), false);
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      reoptimizeCalled++;
      TestObject threadBest = new TestObject(r.nextInt(10000));
      totalRunLength++;
      double bestCost = problem.cost(threadBest);
      tracker.update(bestCost, threadBest, false);
      while (!tracker.isStopped() && runLength > 0) {
        runLength--;
        TestObject candidate = new TestObject(r.nextInt(10000));
        totalRunLength++;
        double cost = problem.cost(candidate);
        if (cost < bestCost) {
          threadBest = candidate;
          bestCost = cost;
          tracker.update(bestCost, threadBest, false);
        }
      }
      return new SolutionCostPair<TestObject>(threadBest, problem.cost(threadBest), false);
    }

    public TestRestartedMetaheuristic split() {
      return new TestRestartedMetaheuristic(10 * id, tracker, problem);
    }

    public ProgressTracker<TestObject> getProgressTracker() {
      return tracker;
    }

    public void setProgressTracker(ProgressTracker<TestObject> tracker) {
      if (tracker != null) this.tracker = tracker;
    }

    public OptimizationProblem<TestObject> getProblem() {
      return problem;
    }

    public long getTotalRunLength() {
      return totalRunLength;
    }
  }

  static class TestObject implements Copyable<TestObject> {
    private int value;

    public TestObject(int value) {
      this.value = value;
    }

    public TestObject copy() {
      return new TestObject(value);
    }

    public int getValue() {
      return value;
    }
  }

  static class TestProblem implements OptimizationProblem<TestObject> {
    public double cost(TestObject o) {
      return o.getValue();
    }

    public boolean isMinCost(double c) {
      return false;
    }

    public double minCost() {
      return -10000;
    }

    public double value(TestObject o) {
      return o.getValue();
    }
  }

  static class TestInterrupted extends TestRestartedMetaheuristic {

    public volatile int count;

    public TestInterrupted(int id, TestProblem problem, ProgressTracker<TestObject> tracker) {
      super(id, tracker, problem);
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      count++;
      for (int i = 0; i < runLength; i++) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) {
          TestObject obj = new TestObject(0);
          return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
        }
      }
      return null;
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      return optimize(runLength);
    }
  }

  static class TestImprovementMade extends TestRestartedMetaheuristic {

    public volatile int count;

    public TestImprovementMade(int id, TestProblem problem, ProgressTracker<TestObject> tracker) {
      super(id, tracker, problem);
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      count++;
      if (id == 1000) {
        TestObject sol = new TestObject(10);
        getProgressTracker().update(10, sol, false);
        return new SolutionCostPair<TestObject>(sol, 10, false);
      } else {
        while (!getProgressTracker().containsIntCost()) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException ex) {
          }
        }
        return new SolutionCostPair<TestObject>(new TestObject(id - 1000), id - 1000, false);
      }
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      return optimize(runLength);
    }
  }
}
