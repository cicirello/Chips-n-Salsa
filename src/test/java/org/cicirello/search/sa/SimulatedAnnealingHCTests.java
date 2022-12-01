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

package org.cicirello.search.sa;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SimpleLocalMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.hc.SteepestDescentHillClimber;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import org.junit.jupiter.api.*;

/** JUnit test cases for the SimulatedAnnealing with hill climbing as a post-processing step. */
public class SimulatedAnnealingHCTests {

  private static final double EPSILON = 1e-10;

  private SimulatedAnnealing<TestObject> d_unknown;
  private SimulatedAnnealing<TestObject> d_known;
  private SimulatedAnnealing<TestObject> i_unknown;
  private SimulatedAnnealing<TestObject> i_known;

  private SimulatedAnnealing<TestObject> d_unknown_hc;
  private SimulatedAnnealing<TestObject> d_known_hc;
  private SimulatedAnnealing<TestObject> i_unknown_hc;
  private SimulatedAnnealing<TestObject> i_known_hc;

  private OptimizationProblem<TestObject> pd_unknown;
  private OptimizationProblem<TestObject> pd_known;
  private IntegerCostOptimizationProblem<TestObject> pi_unknown;
  private IntegerCostOptimizationProblem<TestObject> pi_known;

  @BeforeEach
  public void setUp() {
    pd_unknown = new TestProblem();
    pd_known = new TestProblemKnownMin();
    pi_unknown = new TestProblemInt();
    pi_known = new TestProblemIntKnownMin();

    // These SimulatedAnnealing use a hill climber that does nothing to verify
    // that the SA part works.
    d_unknown =
        new SimulatedAnnealing<TestObject>(
            pd_unknown, new TestMutation(), new TestInitializer(), new NoHillClimber(pd_unknown));
    d_known =
        new SimulatedAnnealing<TestObject>(
            pd_known, new TestMutation(), new TestInitializer(), new NoHillClimber(pd_known));
    i_unknown =
        new SimulatedAnnealing<TestObject>(
            pi_unknown, new TestMutation(), new TestInitializer(), new NoHillClimber(pi_unknown));
    i_known =
        new SimulatedAnnealing<TestObject>(
            pi_known, new TestMutation(), new TestInitializer(), new NoHillClimber(pi_known));

    // These SimulatedAnnealing use a SA that does nothing to verify
    // that the HC part works.
    d_unknown_hc =
        new SimulatedAnnealing<TestObject>(
            pd_unknown,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new SteepestDescentHillClimber<TestObject>(
                pd_unknown, new TestDoNothingMutation(), new TestInitializer()));
    d_known_hc =
        new SimulatedAnnealing<TestObject>(
            pd_known,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new SteepestDescentHillClimber<TestObject>(
                pd_known, new TestDoNothingMutation(), new TestInitializer()));
    i_unknown_hc =
        new SimulatedAnnealing<TestObject>(
            pi_unknown,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new SteepestDescentHillClimber<TestObject>(
                pi_unknown, new TestDoNothingMutation(), new TestInitializer()));
    i_known_hc =
        new SimulatedAnnealing<TestObject>(
            pi_known,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new SteepestDescentHillClimber<TestObject>(
                pi_known, new TestDoNothingMutation(), new TestInitializer()));
  }

  @Test
  public void testConstructorExceptions() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new SimulatedAnnealing<TestObject>(
                    pd_unknown,
                    new TestDoNothingMutation(),
                    new TestInitializer(),
                    new SteepestDescentHillClimber<TestObject>(
                        pd_known, new TestDoNothingMutation(), new TestInitializer())));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new SimulatedAnnealing<TestObject>(
                    pi_unknown,
                    new TestDoNothingMutation(),
                    new TestInitializer(),
                    new SteepestDescentHillClimber<TestObject>(
                        pi_known, new TestDoNothingMutation(), new TestInitializer())));
  }

  @Test
  public void testConstructors() {
    TestMutation mutation = new TestMutation();
    ModifiedLam anneal = new ModifiedLam();
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    TestInitializer init = new TestInitializer();

    NoHillClimber hc = new NoHillClimber(pi_known);
    SimulatedAnnealing<TestObject> sa =
        new SimulatedAnnealing<TestObject>(pi_known, mutation, init, tracker, hc);
    sa.optimize(1);
    assertEquals(tracker, sa.getProgressTracker());
    assertEquals(1, sa.getTotalRunLength());
    assertEquals(pi_known, sa.getProblem());
    assertTrue(tracker.containsIntCost());

    tracker = new ProgressTracker<TestObject>();
    hc = new NoHillClimber(pd_known);
    sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, tracker, hc);
    sa.optimize(1);
    assertEquals(tracker, sa.getProgressTracker());
    assertEquals(1, sa.getTotalRunLength());
    assertEquals(pd_known, sa.getProblem());
    assertFalse(tracker.containsIntCost());

    hc = new NoHillClimber(pi_known);
    sa = new SimulatedAnnealing<TestObject>(pi_known, mutation, init, anneal, hc);
    tracker = sa.getProgressTracker();
    sa.optimize(1);
    assertEquals(tracker, sa.getProgressTracker());
    assertEquals(1, sa.getTotalRunLength());
    assertEquals(pi_known, sa.getProblem());
    assertTrue(tracker.containsIntCost());

    hc = new NoHillClimber(pd_known);
    sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, anneal, hc);
    tracker = sa.getProgressTracker();
    sa.optimize(1);
    assertEquals(tracker, sa.getProgressTracker());
    assertEquals(1, sa.getTotalRunLength());
    assertEquals(pd_known, sa.getProblem());
    assertFalse(tracker.containsIntCost());
  }

  @Test
  public void testSameTracker() {
    ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
    SteepestDescentHillClimber<TestObject> hc =
        new SteepestDescentHillClimber<TestObject>(
            pi_known, new TestDoNothingMutation(), new TestInitializer());
    hc.setProgressTracker(tracker);
    SimulatedAnnealing<TestObject> sa =
        new SimulatedAnnealing<TestObject>(
            pi_known,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new ModifiedLam(),
            tracker,
            hc);
    assertEquals(tracker, hc.getProgressTracker());
    assertEquals(tracker, sa.getProgressTracker());

    tracker = new ProgressTracker<TestObject>();
    hc =
        new SteepestDescentHillClimber<TestObject>(
            pd_known, new TestDoNothingMutation(), new TestInitializer());
    hc.setProgressTracker(tracker);
    sa =
        new SimulatedAnnealing<TestObject>(
            pd_known,
            new TestDoNothingMutation(),
            new TestInitializer(),
            new ModifiedLam(),
            tracker,
            hc);
    assertEquals(tracker, hc.getProgressTracker());
    assertEquals(tracker, sa.getProgressTracker());
  }

  @Test
  public void testReoptimizeNoHC() {
    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = d_unknown.reoptimize(100));
      double expected = i <= 6 ? 1000.0 - 100 * i : 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_unknown.cost(t.getSolution()), EPSILON);
      if (i <= 6) assertEquals(100 * i, result.getSolution().bar);
      elapsed += 100;
      assertEquals(elapsed, d_unknown.getTotalRunLength());
    }

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = i_unknown.reoptimize(100));
      int expected = i <= 6 ? 1000 - 100 * i : 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_unknown.cost(t.getSolution()));
      if (i <= 6) assertEquals(100 * i, result.getSolution().bar);
      elapsed += 100;
      assertEquals(elapsed, i_unknown.getTotalRunLength());
    }

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 6) {
        assertNotNull(result = d_known.reoptimize(100));
        assertEquals(100 * i, result.getSolution().bar);
      } else assertNull(result = d_known.reoptimize(100));
      double expected = i <= 6 ? 1000.0 - 100 * i : 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_known.cost(t.getSolution()), EPSILON);
      elapsed += i <= 6 ? 100 : 0;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 6) {
        assertNotNull(result = i_known.reoptimize(100));
        assertEquals(100 * i, result.getSolution().bar);
      } else assertNull(result = i_known.reoptimize(100));
      int expected = i <= 6 ? 1000 - 100 * i : 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed += i <= 6 ? 100 : 0;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
  }

  @Test
  public void testReoptimizeNoSA() {
    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = d_unknown_hc.reoptimize(100));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_unknown.cost(t.getSolution()), EPSILON);
      assertEquals(600, result.getSolution().bar);
      elapsed = 600 + 200 * i;
      assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    }

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = i_unknown_hc.reoptimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_unknown.cost(t.getSolution()));
      assertEquals(600, result.getSolution().bar);
      elapsed = 600 + 200 * i;
      assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    }

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = d_known_hc.reoptimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.reoptimize(100));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_known.cost(t.getSolution()), EPSILON);
      elapsed = 800;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = i_known_hc.reoptimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.reoptimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testOptimizeNoHC() {
    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = d_unknown.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900.0, t.getCostDouble(), EPSILON);
      assertEquals(900.0, pd_unknown.cost(t.getSolution()), EPSILON);
      elapsed += 100;
      assertEquals(elapsed, d_unknown.getTotalRunLength());
    }
    assertNotNull(result = d_unknown.optimize(1000));
    assertEquals(400.0, t.getCostDouble(), EPSILON);
    assertEquals(400.0, pd_unknown.cost(t.getSolution()), EPSILON);
    elapsed += 1000;
    assertEquals(elapsed, d_unknown.getTotalRunLength());

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = i_unknown.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900, t.getCost());
      assertEquals(900, pi_unknown.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_unknown.getTotalRunLength());
    }
    assertNotNull(result = i_unknown.optimize(1000));
    assertEquals(400, t.getCost());
    assertEquals(400, pi_unknown.cost(t.getSolution()));
    elapsed += 1000;
    assertEquals(elapsed, i_unknown.getTotalRunLength());

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = d_known.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900.0, t.getCostDouble(), EPSILON);
      assertEquals(900.0, pd_known.cost(t.getSolution()), EPSILON);
      elapsed += 100;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }
    assertNotNull(result = d_known.optimize(1000));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400.0, t.getCostDouble(), EPSILON);
    assertEquals(400.0, pd_known.cost(t.getSolution()), EPSILON);
    elapsed += 600;
    assertEquals(elapsed, d_known.getTotalRunLength());

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = i_known.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900, t.getCost());
      assertEquals(900, pi_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
    assertNotNull(result = i_known.optimize(1000));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCost());
    assertEquals(400, pi_known.cost(t.getSolution()));
    elapsed += 600;
    assertEquals(elapsed, i_known.getTotalRunLength());
  }

  @Test
  public void testOptimizeNoSA() {
    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = d_unknown_hc.optimize(100));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_unknown.cost(t.getSolution()), EPSILON);
      assertEquals(600, result.getSolution().bar);
      elapsed = 800 * i;
      assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    }

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = i_unknown_hc.optimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_unknown.cost(t.getSolution()));
      assertEquals(600, result.getSolution().bar);
      elapsed = 800 * i;
      assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    }

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = d_known_hc.optimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.optimize(100));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_known.cost(t.getSolution()), EPSILON);
      elapsed = 800;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = i_known_hc.optimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.optimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testOptimizeSpecifiedStartNoHC() {
    TestObject start = new TestObject(50);

    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = d_unknown.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850.0, t.getCostDouble(), EPSILON);
      assertEquals(850.0, pd_unknown.cost(t.getSolution()), EPSILON);
      elapsed += 100;
      assertEquals(elapsed, d_unknown.getTotalRunLength());
    }
    assertNotNull(result = d_unknown.optimize(1000, start));
    assertEquals(400.0, t.getCostDouble(), EPSILON);
    assertEquals(400.0, pd_unknown.cost(t.getSolution()), EPSILON);
    elapsed += 1000;
    assertEquals(elapsed, d_unknown.getTotalRunLength());

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = i_unknown.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850, t.getCost());
      assertEquals(850, pi_unknown.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_unknown.getTotalRunLength());
    }
    assertNotNull(result = i_unknown.optimize(1000, start));
    assertEquals(400, t.getCost());
    assertEquals(400, pi_unknown.cost(t.getSolution()));
    elapsed += 1000;
    assertEquals(elapsed, i_unknown.getTotalRunLength());

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = d_known.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850.0, t.getCostDouble(), EPSILON);
      assertEquals(850.0, pd_known.cost(t.getSolution()), EPSILON);
      elapsed += 100;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }
    assertNotNull(result = d_known.optimize(1000, start));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400.0, t.getCostDouble(), EPSILON);
    assertEquals(400.0, pd_known.cost(t.getSolution()), EPSILON);
    elapsed += 550;
    assertEquals(elapsed, d_known.getTotalRunLength());

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      assertNotNull(result = i_known.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850, t.getCost());
      assertEquals(850, pi_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
    assertNotNull(result = i_known.optimize(1000, start));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCost());
    assertEquals(400, pi_known.cost(t.getSolution()));
    elapsed += 550;
    assertEquals(elapsed, i_known.getTotalRunLength());
  }

  @Test
  public void testOptimizeSpecifiedStartNoSA() {
    TestObject start = new TestObject(100);

    // Test with unknown min solution: double costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = d_unknown_hc.optimize(100, start));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_unknown.cost(t.getSolution()), EPSILON);
      assertEquals(600, result.getSolution().bar);
      elapsed = 700 * i;
      assertEquals(elapsed, d_unknown_hc.getTotalRunLength());
    }

    // Test with unknown min solution: int costs
    elapsed = 0;
    t = i_unknown_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      assertNotNull(result = i_unknown_hc.optimize(100, start));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_unknown.cost(t.getSolution()));
      assertEquals(600, result.getSolution().bar);
      elapsed = 700 * i;
      assertEquals(elapsed, i_unknown_hc.getTotalRunLength());
    }

    // Test with known min solution: double costs
    elapsed = 0;
    t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = d_known_hc.optimize(100, start));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.optimize(100, start));
      double expected = 400.0;
      assertEquals(expected, t.getCostDouble(), EPSILON);
      assertEquals(expected, pd_known.cost(t.getSolution()), EPSILON);
      elapsed = 700;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }

    // Test with known min solution: int costs
    elapsed = 0;
    t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i <= 1) {
        assertNotNull(result = i_known_hc.optimize(100, start));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.optimize(100, start));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 700;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoSA_reopt() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = i_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.reoptimize(100));
        assertNull(result = split.reoptimize(100));
      }
      if (i <= 1) {
        assertNotNull(result = i_known_hc.reoptimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.reoptimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoSA_opt() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = i_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.optimize(100));
        assertNull(result = split.optimize(100));
      }
      if (i <= 1) {
        assertNotNull(result = i_known_hc.optimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.optimize(100));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoSA_specstart() {
    TestObject start = new TestObject(100);

    // Test with known min solution: int costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = i_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.optimize(100, start));
        assertNull(result = split.optimize(100, start));
      }
      if (i <= 1) {
        assertNotNull(result = i_known_hc.optimize(100, start));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = i_known_hc.optimize(100, start));
      int expected = 400;
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed = 700;
      assertEquals(elapsed, i_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoHC_reopt() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = i_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.reoptimize(100));
        assertEquals(200, result.getSolution().bar);
        assertNotNull(result = split.reoptimize(100));
        assertEquals(300, result.getSolution().bar);
      }
      if (i < 2) {
        assertNotNull(result = i_known.reoptimize(100));
        assertEquals(100 * i, result.getSolution().bar);
      } else if (i <= 4) {
        assertNotNull(result = i_known.reoptimize(100));
        assertEquals(100 * (i + 2), result.getSolution().bar);
      } else assertNull(result = i_known.reoptimize(100));
      int expected = 400;
      if (i < 2) {
        expected = 1000 - 100 * i;
      } else if (i <= 4) {
        expected = 1000 - 100 * (i + 2);
      }
      assertEquals(expected, t.getCost());
      assertEquals(expected, pi_known.cost(t.getSolution()));
      elapsed += i <= 4 ? 100 : 0;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoHC_opt() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      if (i == 3) {
        SimulatedAnnealing<TestObject> split = i_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.optimize(100));
        assertEquals(100, result.getSolution().bar);
        assertNotNull(result = split.optimize(100));
        assertEquals(100, result.getSolution().bar);
      }
      assertNotNull(result = i_known.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900, t.getCost());
      assertEquals(900, pi_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
    assertNotNull(result = i_known.optimize(1000));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCost());
    assertEquals(400, pi_known.cost(t.getSolution()));
    elapsed += 600;
    assertEquals(elapsed, i_known.getTotalRunLength());
  }

  @Test
  public void testSplitNoHC_specStart() {
    TestObject start = new TestObject(50);

    int elapsed = 0;
    ProgressTracker<TestObject> t = i_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      if (i == 3) {
        SimulatedAnnealing<TestObject> split = i_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.optimize(100, start));
        assertEquals(150, result.getSolution().bar);
        assertNotNull(result = split.optimize(100, start));
        assertEquals(150, result.getSolution().bar);
      }
      assertNotNull(result = i_known.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850, t.getCost());
      assertEquals(850, pi_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, i_known.getTotalRunLength());
    }
    assertNotNull(result = i_known.optimize(1000, start));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCost());
    assertEquals(400, pi_known.cost(t.getSolution()));
    elapsed += 550;
    assertEquals(elapsed, i_known.getTotalRunLength());
  }

  @Test
  public void testSplitNoSA_reopt_D() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = d_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.reoptimize(100));
        assertNull(result = split.reoptimize(100));
      }
      if (i <= 1) {
        assertNotNull(result = d_known_hc.reoptimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.reoptimize(100));
      int expected = 400;
      assertEquals(expected, t.getCostDouble());
      assertEquals(expected, pd_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoSA_opt_D() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = d_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.optimize(100));
        assertNull(result = split.optimize(100));
      }
      if (i <= 1) {
        assertNotNull(result = d_known_hc.optimize(100));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.optimize(100));
      int expected = 400;
      assertEquals(expected, t.getCostDouble());
      assertEquals(expected, pd_known.cost(t.getSolution()));
      elapsed = 800;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoSA_specstart_D() {
    TestObject start = new TestObject(100);

    // Test with known min solution: int costs
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known_hc.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known_hc.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = d_known_hc.split();
        assertEquals(t, split.getProgressTracker());
        assertNull(result = split.optimize(100, start));
        assertNull(result = split.optimize(100, start));
      }
      if (i <= 1) {
        assertNotNull(result = d_known_hc.optimize(100, start));
        assertEquals(600, result.getSolution().bar);
      } else assertNull(result = d_known_hc.optimize(100, start));
      int expected = 400;
      assertEquals(expected, t.getCostDouble());
      assertEquals(expected, pd_known.cost(t.getSolution()));
      elapsed = 700;
      assertEquals(elapsed, d_known_hc.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoHC_reopt_D() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known.getTotalRunLength());
    for (int i = 1; i <= 15; i++) {
      SolutionCostPair<TestObject> result;
      if (i == 2) {
        SimulatedAnnealing<TestObject> split = d_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.reoptimize(100));
        assertEquals(200, result.getSolution().bar);
        assertNotNull(result = split.reoptimize(100));
        assertEquals(300, result.getSolution().bar);
      }
      if (i < 2) {
        assertNotNull(result = d_known.reoptimize(100));
        assertEquals(100 * i, result.getSolution().bar);
      } else if (i <= 4) {
        assertNotNull(result = d_known.reoptimize(100));
        assertEquals(100 * (i + 2), result.getSolution().bar);
      } else assertNull(result = d_known.reoptimize(100));
      int expected = 400;
      if (i < 2) {
        expected = 1000 - 100 * i;
      } else if (i <= 4) {
        expected = 1000 - 100 * (i + 2);
      }
      assertEquals(expected, t.getCostDouble());
      assertEquals(expected, pd_known.cost(t.getSolution()));
      elapsed += i <= 4 ? 100 : 0;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }
  }

  @Test
  public void testSplitNoHC_opt_D() {
    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, d_known.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      if (i == 3) {
        SimulatedAnnealing<TestObject> split = d_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.optimize(100));
        assertEquals(100, result.getSolution().bar);
        assertNotNull(result = split.optimize(100));
        assertEquals(100, result.getSolution().bar);
      }
      assertNotNull(result = d_known.optimize(100));
      assertEquals(100, result.getSolution().bar);
      assertEquals(900, t.getCostDouble());
      assertEquals(900, pd_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }
    assertNotNull(result = d_known.optimize(1000));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCostDouble());
    assertEquals(400, pd_known.cost(t.getSolution()));
    elapsed += 600;
    assertEquals(elapsed, d_known.getTotalRunLength());
  }

  @Test
  public void testSplitNoHC_specStart_D() {
    TestObject start = new TestObject(50);

    int elapsed = 0;
    ProgressTracker<TestObject> t = d_known.getProgressTracker();
    assertNull(t.getSolution());
    assertEquals(elapsed, i_known.getTotalRunLength());
    SolutionCostPair<TestObject> result;
    for (int i = 1; i <= 15; i++) {
      if (i == 3) {
        SimulatedAnnealing<TestObject> split = d_known.split();
        assertEquals(t, split.getProgressTracker());
        assertNotNull(result = split.optimize(100, start));
        assertEquals(150, result.getSolution().bar);
        assertNotNull(result = split.optimize(100, start));
        assertEquals(150, result.getSolution().bar);
      }
      assertNotNull(result = d_known.optimize(100, start));
      assertEquals(150, result.getSolution().bar);
      assertEquals(850, t.getCostDouble());
      assertEquals(850, pd_known.cost(t.getSolution()));
      elapsed += 100;
      assertEquals(elapsed, d_known.getTotalRunLength());
    }
    assertNotNull(result = d_known.optimize(1000, start));
    assertEquals(600, result.getSolution().bar);
    assertEquals(400, t.getCostDouble());
    assertEquals(400, pd_known.cost(t.getSolution()));
    elapsed += 550;
    assertEquals(elapsed, d_known.getTotalRunLength());
  }

  /*
   * Verify that solution at end of simulated annealing portion of search is correct by
   * using a hill climber that does nothing for the post processing portion.
   */
  private static class NoHillClimber
      implements Metaheuristic<TestObject>, SimpleLocalMetaheuristic<TestObject> {
    IntegerCostOptimizationProblem<TestObject> optInt;
    OptimizationProblem<TestObject> opt;

    public NoHillClimber(OptimizationProblem<TestObject> opt) {
      this.opt = opt;
    }

    public NoHillClimber(IntegerCostOptimizationProblem<TestObject> opt) {
      optInt = opt;
    }

    @Override
    public NoHillClimber split() {
      if (optInt != null) return new NoHillClimber(optInt);
      else return new NoHillClimber(opt);
    }

    @Override
    public SolutionCostPair<TestObject> optimize() {
      return null;
    }

    @Override
    public SolutionCostPair<TestObject> optimize(TestObject start) {
      return new SolutionCostPair<TestObject>(
          start, optInt != null ? optInt.cost(start) : opt.cost(start), false);
    }

    @Override
    public SolutionCostPair<TestObject> optimize(int numRestarts) {
      return null; // unneeded for test cases
    }

    @Override
    public long getTotalRunLength() {
      return 0;
    }

    @Override
    public void setProgressTracker(ProgressTracker<TestObject> tracker) {
      /* not needed for tests */
    }

    @Override
    public ProgressTracker<TestObject> getProgressTracker() {
      /* not needed for tests */
      return null;
    }

    @Override
    public Problem<TestObject> getProblem() {
      return optInt != null ? optInt : opt;
    }
  }

  private static class TestProblem implements OptimizationProblem<TestObject> {
    @Override
    public double cost(TestObject c) {
      return 1000 - c.bar % 601;
    }

    @Override
    public double value(TestObject c) {
      return cost(c);
    }
  }

  private static class TestProblemKnownMin extends TestProblem {
    @Override
    public double minCost() {
      return 400;
    }

    @Override
    public boolean isMinCost(double c) {
      return c == minCost();
    }
  }

  private static class TestProblemInt implements IntegerCostOptimizationProblem<TestObject> {
    @Override
    public int cost(TestObject c) {
      return 1000 - c.bar % 601;
    }

    @Override
    public int value(TestObject c) {
      return cost(c);
    }
  }

  private static class TestProblemIntKnownMin extends TestProblemInt {
    @Override
    public int minCost() {
      return 400;
    }

    @Override
    public boolean isMinCost(int c) {
      return c == minCost();
    }
  }

  private static class TestMutation implements UndoableMutationOperator<TestObject> {
    @Override
    public void mutate(TestObject c) {
      c.bar++;
    }

    @Override
    public void undo(TestObject c) {
      c.bar--;
    }

    @Override
    public TestMutation split() {
      return new TestMutation();
    }
  }

  private static class TestDoNothingMutation
      implements UndoableMutationOperator<TestObject>, IterableMutationOperator<TestObject> {
    @Override
    public void mutate(TestObject c) {}

    @Override
    public void undo(TestObject c) {}

    @Override
    public TestDoNothingMutation split() {
      return new TestDoNothingMutation();
    }

    @Override
    public MutationIterator iterator(TestObject c) {
      return new TestMutationIterator(c);
    }
  }

  private static class TestMutationIterator implements MutationIterator {
    private TestObject c;
    private int callCount;
    private int save;

    TestMutationIterator(TestObject c) {
      this.c = c;
      callCount = 0;
      save = c.bar;
    }

    @Override
    public boolean hasNext() {
      return callCount < 100;
    }

    @Override
    public void nextMutant() {
      if (hasNext()) {
        c.bar++;
        callCount++;
      }
    }

    @Override
    public void setSavepoint() {
      save = c.bar;
    }

    @Override
    public void rollback() {
      c.bar = save;
    }
  }

  private static class TestInitializer implements Initializer<TestObject> {
    // for testing always start with same solution rather than random for predictable results
    @Override
    public TestObject createCandidateSolution() {
      return new TestObject(0);
    }

    @Override
    public TestInitializer split() {
      return this;
    }
  }

  private static class TestObject implements Copyable<TestObject> {
    int bar;

    public TestObject(int bar) {
      this.bar = bar;
    }

    @Override
    public TestObject copy() {
      return new TestObject(bar);
    }
  }
}
