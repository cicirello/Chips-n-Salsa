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

import org.cicirello.permutations.Permutation;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.ss.ConstructiveHeuristic;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.PartialPermutation;

/**
 * This class implements a constructive heuristic for the TSP that prefers the first city of the
 * nearest pair of cities. Since the stochastic sampling algorithms of the library require higher
 * heuristic values to imply preferred choice, this heuristic is implemented as: h(j) == 1.0 / (1.0
 * + distance(i, j) + min<sub>k</sub>(distance(j, k))), where h(j) is the heuristic value for city
 * j, and i is the most recently added city.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class NearestCityPairHeuristic implements ConstructiveHeuristic<Permutation> {

  private final BaseTSP problem;

  /**
   * Constructs a nearest city pair heuristic for an instance of the TSP.
   *
   * @param problem The TSP instance to solve.
   */
  public NearestCityPairHeuristic(BaseTSP problem) {
    this.problem = problem;
  }

  @Override
  public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
    NearestCityPairHeuristicIncrementalEvaluation evaluator =
        (NearestCityPairHeuristicIncrementalEvaluation) incEval;
    double denom = 1.0 + evaluator.distanceToNearestCity[element];
    if (p.size() > 0) {
      denom += problem.edgeCostForHeuristics(p.getLast(), element);
    }
    return 1.0 / (denom);
  }

  @Override
  public IncrementalEvaluation<Permutation> createIncrementalEvaluation() {
    return new NearestCityPairHeuristicIncrementalEvaluation();
  }

  @Override
  public final Problem<Permutation> getProblem() {
    return problem;
  }

  @Override
  public final Partial<Permutation> createPartial(int n) {
    return new PartialPermutation(n);
  }

  @Override
  public final int completeLength() {
    return problem.length();
  }

  /*
   * package private IncrementalEvaluation class, package private to facilitate
   * unit testing.
   */
  final class NearestCityPairHeuristicIncrementalEvaluation
      implements IncrementalEvaluation<Permutation> {

    final double[] distanceToNearestCity;
    final int[] nearestRemainingCity;
    private final int[] remainingCities;
    private int numRemaining;

    NearestCityPairHeuristicIncrementalEvaluation() {
      numRemaining = problem.length();
      distanceToNearestCity = new double[numRemaining];
      nearestRemainingCity = new int[numRemaining];
      remainingCities = new int[numRemaining];
      for (int i = 0; i < numRemaining; i++) {
        distanceToNearestCity[i] = Double.POSITIVE_INFINITY;
        remainingCities[i] = i;
        for (int j = 0; j < numRemaining; j++) {
          if (i != j) {
            double d = problem.edgeCostForHeuristics(i, j);
            if (d < distanceToNearestCity[i]) {
              distanceToNearestCity[i] = d;
              nearestRemainingCity[i] = j;
            }
          }
        }
      }
    }

    @Override
    public void extend(Partial<Permutation> p, int element) {
      removeFromRemaining(element);
      if (numRemaining > 1) {
        for (int i = 0; i < numRemaining; i++) {
          int x = remainingCities[i];
          if (nearestRemainingCity[x] == element) {
            distanceToNearestCity[x] = Double.POSITIVE_INFINITY;
            for (int j = 0; j < numRemaining; j++) {
              if (i != j) {
                int y = remainingCities[j];
                double d = problem.edgeCostForHeuristics(x, y);
                if (d < distanceToNearestCity[x]) {
                  distanceToNearestCity[x] = d;
                  nearestRemainingCity[x] = y;
                }
              }
            }
          }
        }
      } else {
        distanceToNearestCity[remainingCities[0]] = 0;
      }
    }

    private void removeFromRemaining(int element) {
      int i = 0;
      for (; i < numRemaining && remainingCities[i] != element; i++)
        ;
      if (remainingCities[i] == element) {
        for (i++; i < numRemaining; i++) {
          remainingCities[i - 1] = remainingCities[i];
        }
        numRemaining--;
      }
    }

    /*
     * package private for testing
     */
    final int numRemaining() {
      return numRemaining;
    }
  }
}
