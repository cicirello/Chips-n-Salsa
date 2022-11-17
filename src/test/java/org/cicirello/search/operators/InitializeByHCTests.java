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

package org.cicirello.search.operators;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.hc.FirstDescentHillClimber;
import org.cicirello.search.hc.SteepestDescentHillClimber;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for InitializeBySimpleMetaheuristic. */
public class InitializeByHCTests {

  private static final double EPSILON = 1e-10;

  @Test
  public void testIntSteepest() {
    TestObject.setB(0);
    TestOptInt problem = new TestOptInt();
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            problem, new TestMutator(), new TestObject(1000));

    InitializeBySimpleMetaheuristic<TestObject> init =
        new InitializeBySimpleMetaheuristic<TestObject>(hc);
    TestObject x = init.createCandidateSolution();

    assertEquals(41, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, problem.cost(x));
    assertEquals(TestObject.OPT, x.getA());

    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());

    InitializeBySimpleMetaheuristic<TestObject> s = init.split();
    assertNotEquals(init, s);
    assertNotNull(s);
  }

  @Test
  public void testDoubleSteepest() {
    TestObject.setB(0);
    TestOpt problem = new TestOpt();
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            problem, new TestMutator(), new TestObject(1000));

    InitializeBySimpleMetaheuristic<TestObject> init =
        new InitializeBySimpleMetaheuristic<TestObject>(hc);
    TestObject x = init.createCandidateSolution();

    assertEquals(41, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, problem.cost(x), EPSILON);
    assertEquals(TestObject.OPT, x.getA());

    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testIntFirst() {
    TestObject.setB(0);
    TestOptInt problem = new TestOptInt();
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, new TestMutator(), new TestObject(1000));

    InitializeBySimpleMetaheuristic<TestObject> init =
        new InitializeBySimpleMetaheuristic<TestObject>(hc);
    TestObject x = init.createCandidateSolution();

    assertEquals(26, hc.getTotalRunLength());
    assertEquals(2 * TestObject.OPT, problem.cost(x));
    assertEquals(TestObject.OPT, x.getA());

    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2 * TestObject.OPT, ts.getCost());
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
  }

  @Test
  public void testDoubleFirst() {
    TestObject.setB(0);
    TestOpt problem = new TestOpt();
    FirstDescentHillClimber<TestObject> hc =
        new FirstDescentHillClimber<TestObject>(problem, new TestMutator(), new TestObject(1000));

    InitializeBySimpleMetaheuristic<TestObject> init =
        new InitializeBySimpleMetaheuristic<TestObject>(hc);
    TestObject x = init.createCandidateSolution();

    assertEquals(26, hc.getTotalRunLength());
    assertEquals(2.0 * TestObject.OPT, problem.cost(x), EPSILON);
    assertEquals(TestObject.OPT, x.getA());

    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble(), EPSILON);
    assertEquals(TestObject.OPT, ts.getSolution().getA());
    assertTrue(tracker.didFindBest());
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
    @Override
    public int cost(TestObject c) {
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

    public int getA() {
      return a;
    }

    @Override
    public TestObject copy() {
      return new TestObject(a, b);
    }

    @Override
    public TestObject split() {
      return copy();
    }

    @Override
    public TestObject createCandidateSolution() {
      TestObject s = new TestObject(100 * (B + 1), B);
      if (B > 0) B--;
      return s;
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
