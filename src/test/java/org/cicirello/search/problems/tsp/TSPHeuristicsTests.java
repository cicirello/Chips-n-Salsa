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

package org.cicirello.search.problems.tsp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.ss.Partial;
import org.junit.jupiter.api.*;

/** JUnit tests for TSP constructive heuristics. */
public class TSPHeuristicsTests {

  @Test
  public void testNearestCity() {
    TSP.Double tsp = new TSP.Double(5, 10.0, 42);
    NearestCityHeuristic h = new NearestCityHeuristic(tsp);
    assertTrue(tsp == h.getProblem());
    assertEquals(5, h.completeLength());
    Partial<Permutation> partial = h.createPartial(5);
    assertFalse(partial.isComplete());
    assertEquals(5, partial.numExtensions());
    assertEquals(0, partial.size());
  }

  @Test
  public void testNearestCity_heuristicValues() {
    double[][] weights = {
      {0, 1, 2, 3, 4},
      {1, 0, 5, 6, 7},
      {2, 5, 0, 8, 9},
      {3, 6, 8, 0, 10},
      {4, 7, 9, 10, 0}
    };
    TSPSubClassExplicitWeights tsp = new TSPSubClassExplicitWeights(weights);
    NearestCityHeuristic h = new NearestCityHeuristic(tsp);
    assertTrue(tsp == h.getProblem());
    assertEquals(5, h.completeLength());
    Partial<Permutation> partial = h.createPartial(5);
    assertFalse(partial.isComplete());
    assertEquals(5, partial.numExtensions());
    assertEquals(0, partial.size());
    for (int i = 0; i < 5; i++) {
      assertEquals(1.0, h.h(partial, i, null), 1E-10);
    }
    partial.extend(2);
    boolean[] stillAvailable = {true, true, false, true, true};
    double[] expected = {1.0 / 3.0, 1.0 / 6.0, 0, 1.0 / 9.0, 1.0 / 10.0};
    for (int i = 0; i < 4; i++) {
      int j = partial.getExtension(i);
      assertTrue(stillAvailable[j]);
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }
    int k = partial.getExtension(2);
    partial.extend(2);
    stillAvailable[k] = false;
    for (int i = 0; i < 5; i++) {
      if (stillAvailable[i]) {
        expected[i] = 1.0 / (1.0 + weights[k][i]);
      } else {
        expected[i] = 0;
      }
    }
    for (int i = 0; i < 3; i++) {
      int j = partial.getExtension(i);
      assertTrue(stillAvailable[j]);
      assertEquals(expected[j], h.h(partial, j, null), 1E-10);
    }
  }

  @Test
  public void testNearestCityPair_InitialConditions() {
    TSP.Double tsp = new TSP.Double(5, 10.0, 42);
    NearestCityPairHeuristic h = new NearestCityPairHeuristic(tsp);
    assertTrue(tsp == h.getProblem());
    assertEquals(5, h.completeLength());
    Partial<Permutation> partial = h.createPartial(5);
    assertFalse(partial.isComplete());
    assertEquals(5, partial.numExtensions());
    assertEquals(0, partial.size());
  }

  @Test
  public void testNearestCityPair_IncrementalEvaluation_and_HeuristicValues() {
    double[][] weights = {
      {0, 1, 2, 3, 4},
      {1, 0, 5, 6, 7},
      {2, 5, 0, 8, 9},
      {3, 6, 8, 0, 10},
      {4, 7, 9, 10, 0}
    };
    double[] expectedDistance = {1, 1, 2, 3, 4};
    int[] expectedNearest = {1, 0, 0, 0, 0};
    double[] expectedH = {1.0 / 2.0, 1.0 / 2.0, 1.0 / 3.0, 1.0 / 4.0, 1.0 / 5.0};
    TSPSubClassExplicitWeights tsp = new TSPSubClassExplicitWeights(weights);
    NearestCityPairHeuristic h = new NearestCityPairHeuristic(tsp);
    Partial<Permutation> partial = h.createPartial(5);
    NearestCityPairHeuristic.NearestCityPairHeuristicIncrementalEvaluation inc =
        (NearestCityPairHeuristic.NearestCityPairHeuristicIncrementalEvaluation)
            h.createIncrementalEvaluation();
    for (int i = 0; i < 5; i++) {
      assertEquals(expectedNearest[i], inc.nearestRemainingCity[i]);
      assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10);
      assertEquals(expectedH[i], h.h(partial, i, inc), 1E-10);
    }
    assertEquals(5, inc.numRemaining());
    boolean[] skip = new boolean[5];

    int element = 2;
    inc.extend(partial, element);
    skip[element] = true;
    assertEquals(4, inc.numRemaining());
    for (int i = 0; i < partial.numExtensions(); i++) {
      if (partial.getExtension(i) == element) {
        partial.extend(i);
        break;
      }
    }
    expectedH[0] = 1.0 / 4.0;
    expectedH[1] = 1.0 / 7.0;
    expectedH[3] = 1.0 / 12.0;
    expectedH[4] = 1.0 / 14.0;
    for (int i = 0; i < 5; i++) {
      if (!skip[i]) {
        assertEquals(expectedNearest[i], inc.nearestRemainingCity[i]);
        assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10);
        assertEquals(expectedH[i], h.h(partial, i, inc), 1E-10);
      }
    }

    element = 0;
    inc.extend(partial, element);
    skip[element] = true;
    assertEquals(3, inc.numRemaining());
    for (int i = 0; i < partial.numExtensions(); i++) {
      if (partial.getExtension(i) == element) {
        partial.extend(i);
        break;
      }
    }
    expectedDistance[1] = 6;
    expectedNearest[1] = 3;
    expectedDistance[3] = 6;
    expectedNearest[3] = 1;
    expectedDistance[4] = 7;
    expectedNearest[4] = 1;
    expectedH[1] = 1.0 / 8.0;
    expectedH[3] = 1.0 / 10.0;
    expectedH[4] = 1.0 / 12.0;
    for (int i = 0; i < 5; i++) {
      if (!skip[i]) {
        assertEquals(expectedNearest[i], inc.nearestRemainingCity[i]);
        assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10, "i:" + i);
        assertEquals(expectedH[i], h.h(partial, i, inc), 1E-10);
      }
    }

    element = 3;
    inc.extend(partial, element);
    skip[element] = true;
    assertEquals(2, inc.numRemaining());
    for (int i = 0; i < partial.numExtensions(); i++) {
      if (partial.getExtension(i) == element) {
        partial.extend(i);
        break;
      }
    }
    expectedDistance[1] = 7;
    expectedNearest[1] = 4;
    expectedDistance[4] = 7;
    expectedNearest[4] = 1;
    expectedH[1] = 1.0 / 14.0;
    expectedH[4] = 1.0 / 18.0;
    for (int i = 0; i < 5; i++) {
      if (!skip[i]) {
        assertEquals(expectedNearest[i], inc.nearestRemainingCity[i]);
        assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10, "i:" + i);
        assertEquals(expectedH[i], h.h(partial, i, inc), 1E-10);
      }
    }

    element = 1;
    inc.extend(partial, element);
    skip[element] = true;
    assertEquals(1, inc.numRemaining());
    for (int i = 0; i < partial.numExtensions(); i++) {
      if (partial.getExtension(i) == element) {
        partial.extend(i);
        break;
      }
    }
    expectedDistance[4] = 0;
    expectedH[4] = 1.0 / 8.0;
    for (int i = 0; i < 5; i++) {
      if (!skip[i]) {
        assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10, "i:" + i);
        assertEquals(expectedH[i], h.h(partial, i, inc), 1E-10);
      }
    }

    // try to extend by element not left
    element = 0;
    inc.extend(partial, element);
    assertEquals(1, inc.numRemaining());
    for (int i = 0; i < 5; i++) {
      if (!skip[i]) {
        assertEquals(expectedDistance[i], inc.distanceToNearestCity[i], 1E-10, "i:" + i);
      }
    }

    // extend by last element
    element = 4;
    inc.extend(partial, element);
    skip[element] = true;
    assertEquals(0, inc.numRemaining());
  }

  private static class TSPSubClassExplicitWeights extends TSP
      implements OptimizationProblem<Permutation> {

    private final double[][] edgeWeights;

    public TSPSubClassExplicitWeights(double[][] edgeWeights) {
      // note that this test ignores this and overrides behavior
      super(edgeWeights.length, 100, new SplittableRandom(42));
      this.edgeWeights = edgeWeights;
    }

    @Override
    public double edgeCostForHeuristics(int i, int j) {
      return edgeWeights[i][j];
    }

    @Override
    public SolutionCostPair<Permutation> getSolutionCostPair(Permutation p) {
      return new SolutionCostPair<Permutation>(p, cost(p), false);
    }

    @Override
    public double cost(Permutation c) {
      double cost = edgeWeights[c.get(c.length() - 1)][c.get(0)];
      for (int i = 1; i < c.length(); i++) {
        cost += edgeWeights[c.get(i - 1)][c.get(i)];
      }
      return cost;
    }

    @Override
    public double value(Permutation c) {
      return cost(c);
    }
  }
}
