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

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.junit.jupiter.api.*;

/** JUnit tests for the SteepestDescentHillClimber. */
public class SteepestDescentHillClimberTests extends SharedTestHillClimberHelpers {

  @Test
  public void testConstructorsSteepestDescent() {
    TestOptInt problem = new TestOptInt();
    TestOpt problemDouble = new TestOpt();
    TestMutator mutation = new TestMutator();
    TestObject init = new TestObject(1000);
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();

    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(problem, mutation, init, tracker);
    assertEquals(tracker, hc.getProgressTracker());
    assertEquals(problem, hc.getProblem());

    hc = new SteepestDescentHillClimber<TestObject>(problemDouble, mutation, init, tracker);
    assertEquals(tracker, hc.getProgressTracker());
    assertEquals(problemDouble, hc.getProblem());

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
                    (OptimizationProblem<TestObject>) null, mutation, init));
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problemDouble, null, init));
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
    thrown =
        assertThrows(
            NullPointerException.class,
            () -> new SteepestDescentHillClimber<TestObject>(problem, null, init));
    thrown =
        assertThrows(
            NullPointerException.class,
            () ->
                new SteepestDescentHillClimber<TestObject>(
                    (IntegerCostOptimizationProblem<TestObject>) null, mutation, init));
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
        assertEquals(2 * TestObject.OPT + 200, s.getCostDouble());
        assertEquals(TestObject.OPT + 100, s.getSolution().getA());
        ProgressTracker<TestObject> tracker = hc.getProgressTracker();
        SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
        assertEquals(2 * TestObject.OPT + 200, ts.getCostDouble());
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
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble());
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
    assertEquals(2.0 * TestObject.OPT, s.getCostDouble());
    assertEquals(TestObject.OPT, s.getSolution().getA());
    ProgressTracker<TestObject> tracker = hc.getProgressTracker();
    SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
    assertEquals(2.0 * TestObject.OPT, ts.getCostDouble());
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
      assertEquals(2.0 * TestObject.OPT, s.getCostDouble());
      assertEquals(TestObject.OPT, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT, ts.getCostDouble());
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
      assertEquals(2.0 * TestObject.OPT + 200, s.getCostDouble());
      assertEquals(TestObject.OPT + 100, s.getSolution().getA());
      ProgressTracker<TestObject> tracker = hc.getProgressTracker();
      SolutionCostPair<TestObject> ts = tracker.getSolutionCostPair();
      assertEquals(2.0 * TestObject.OPT + 200, ts.getCostDouble());
      assertEquals(TestObject.OPT + 100, ts.getSolution().getA());
      assertFalse(tracker.didFindBest());
    }
  }
}
