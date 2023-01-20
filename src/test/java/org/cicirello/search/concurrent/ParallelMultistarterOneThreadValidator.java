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
import org.junit.jupiter.api.*;

/** Test validation common to multiple test classes for testing parallel multistarters. */
public class ParallelMultistarterOneThreadValidator extends ParallelMultistarterValidator {

  static class TestInterrupted extends TestRestartedMetaheuristic {

    public volatile int count;

    public TestInterrupted(int id, TestProblem problem, ProgressTracker<TestObject> tracker) {
      super(problem);
      setProgressTracker(tracker);
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      count++;
      for (int i = 0; i < runLength; i++) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) {
          TestObject obj = new TestObject();
          return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
        }
      }
      return null;
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      count++;
      for (int i = 0; i < runLength; i++) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) {
          TestObject obj = new TestObject();
          return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
        }
      }
      return null;
    }
  }

  static class TestOptThrowsExceptions extends TestRestartedMetaheuristic {

    boolean throwException;
    boolean returnsNull;

    public TestOptThrowsExceptions(
        int id, TestProblem problem, ProgressTracker<TestObject> tracker) {
      super(problem);
      setProgressTracker(tracker);
      throwException = id == 2;
      returnsNull = id == 3;
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      if (throwException) {
        throw new RuntimeException("Testing exception handling");
      } else if (returnsNull) {
        return null;
      } else {
        TestObject obj = new TestObject();
        return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
      }
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      if (throwException) {
        throw new RuntimeException("Testing exception handling");
      } else if (returnsNull) {
        return null;
      } else {
        TestObject obj = new TestObject();
        return new SolutionCostPair<TestObject>(obj, problem.cost(obj), false);
      }
    }
  }

  static class TestRestartedMetaheuristic extends AbstractTestRestartedMetaheuristic
      implements ReoptimizableMetaheuristic<TestObject> {

    public final OptimizationProblem<TestObject> problem;

    public TestRestartedMetaheuristic() {
      super();
      problem = new TestProblem();
    }

    public TestRestartedMetaheuristic(TestProblem p) {
      super();
      problem = p;
    }

    public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval) {
      this(stopAtEval, findBestAtEval, new SplittableRandom(42));
    }

    public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval, SplittableRandom rand) {
      super(stopAtEval, findBestAtEval, rand);
      problem = new TestProblem();
    }

    public TestRestartedMetaheuristic(TestRestartedMetaheuristic other) {
      super(other);
      problem = new TestProblem();
    }

    @Override
    public TestRestartedMetaheuristic split() {
      return new TestRestartedMetaheuristic(this);
    }

    @Override
    public OptimizationProblem<TestObject> getProblem() {
      // not used by tests.
      return problem;
    }
  }
}
