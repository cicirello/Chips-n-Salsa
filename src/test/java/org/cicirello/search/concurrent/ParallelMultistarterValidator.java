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

/** Test validation common to multiple test classes for testing parallel multistarters. */
public class ParallelMultistarterValidator {

  abstract static class AbstractTestRestartedMetaheuristic
      implements ReoptimizableMetaheuristic<TestObject> {

    private ProgressTracker<TestObject> tracker;
    private int elapsed;
    private final int stopAtEval;
    private final int findBestAtEval;
    private final int which; // 0 for both at same time, 1 for stop, 2 for best
    int optCounter;
    int reoptCounter;
    private final SplittableRandom rand;

    public AbstractTestRestartedMetaheuristic() {
      tracker = new ProgressTracker<TestObject>();
      elapsed = 0;
      stopAtEval = findBestAtEval = Integer.MAX_VALUE;
      which = 0;
      rand = new SplittableRandom(42);
    }

    public AbstractTestRestartedMetaheuristic(int stopAtEval, int findBestAtEval) {
      this(stopAtEval, findBestAtEval, new SplittableRandom(42));
    }

    public AbstractTestRestartedMetaheuristic(
        int stopAtEval, int findBestAtEval, SplittableRandom rand) {
      tracker = new ProgressTracker<TestObject>();
      elapsed = 0;
      this.stopAtEval = stopAtEval;
      this.findBestAtEval = findBestAtEval;
      if (stopAtEval < findBestAtEval) which = 1;
      else if (stopAtEval > findBestAtEval) which = 2;
      else which = 0;
      this.rand = rand;
    }

    public AbstractTestRestartedMetaheuristic(AbstractTestRestartedMetaheuristic other) {
      this(other.stopAtEval, other.findBestAtEval, other.rand.split());
    }

    @Override
    public ProgressTracker<TestObject> getProgressTracker() {
      return tracker;
    }

    @Override
    public void setProgressTracker(ProgressTracker<TestObject> tracker) {
      if (tracker != null) this.tracker = tracker;
    }

    @Override
    public long getTotalRunLength() {
      return elapsed;
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int runLength) {
      optCounter++;
      int c = update(runLength);
      return new SolutionCostPair<TestObject>(new TestObject(), c, false);
    }

    @Override
    public SolutionCostPair<TestObject> reoptimize(int runLength) {
      reoptCounter++;
      int c = update(runLength);
      return new SolutionCostPair<TestObject>(new TestObject(), c, false);
    }

    private int update(int runLength) {
      elapsed += runLength;
      int c = rand.nextInt(18) + 2;
      switch (which) {
        case 0:
          if (elapsed >= stopAtEval) {
            elapsed = stopAtEval;
            tracker.stop();
            // Replaces old call to deprecated setFoundBest()
            tracker.update(1, new TestObject(), true);
            c = 1;
          }
          break;
        case 1:
          if (elapsed >= stopAtEval) {
            elapsed = stopAtEval;
            tracker.stop();
          }
          break;
        case 2:
          if (elapsed >= findBestAtEval) {
            elapsed = findBestAtEval;
            // Replaces old call to deprecated setFoundBest()
            tracker.update(1, new TestObject(), true);
            c = 1;
          }
          break;
      }
      return c;
    }
  }

  static class TestObject implements Copyable<TestObject> {

    public TestObject() {}

    @Override
    public TestObject copy() {
      return new TestObject();
    }
  }

  static class TestProblem implements OptimizationProblem<TestObject> {
    public double cost(TestObject o) {
      return 5;
    }

    public boolean isMinCost(double c) {
      return false;
    }

    public double minCost() {
      return -10000;
    }

    public double value(TestObject o) {
      return 5;
    }
  }
}
