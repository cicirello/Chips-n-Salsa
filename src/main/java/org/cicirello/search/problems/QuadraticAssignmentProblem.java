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

package org.cicirello.search.problems;

import org.cicirello.math.rand.EnhancedRandomGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * This class is an implementation of the Quadratic Assignment Problem (QAP), an NP-Hard
 * optimization problem. In this implementation, both the cost and distance matrices are
 * integer-valued. This class uses factory methods, rather than constructors to better support a
 * variety of ways of generating random instances. The class supports generating uniform random
 * instances via the {@link #createUniformRandomInstance createUniformRandomInstance} method, as
 * well as creating instances by directly specifying the cost and distance matrices via the {@link
 * #createInstance createInstance}.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class QuadraticAssignmentProblem
    implements IntegerCostOptimizationProblem<Permutation> {

  private final int[][] cost;
  private final int[][] distance;

  /*
   * package private internal constructor. This constructor does no validation.
   * Additionally, be careful in matrices passed to this constructor as it
   * stores references directly to those matrices.
   */
  QuadraticAssignmentProblem(int[][] cost, int[][] distance) {
    this.cost = cost;
    this.distance = distance;
  }

  @Override
  public int cost(Permutation candidate) {
    int total = 0;
    for (int i = 0; i < cost.length; i++) {
      for (int j = i + 1; j < cost.length; j++) {
        total += cost[i][j] * distance[candidate.get(i)][candidate.get(j)];
        total += cost[j][i] * distance[candidate.get(j)][candidate.get(i)];
      }
    }
    return total;
  }

  @Override
  public int value(Permutation candidate) {
    return cost(candidate);
  }

  @Override
  public int minCost() {
    // simply return 0 as a trivial lower bound
    return 0;
  }

  /**
   * Gets the size of the instance.
   *
   * @return the size of the instance.
   */
  public int size() {
    return cost.length;
  }

  /**
   * Gets an element of the cost matrix.
   *
   * @param i The row index.
   * @param j The column index.
   * @return The cost of cell i, j.
   * @throws IndexOutOfBoundsException if either i or j is less than 0, or if either i or j is
   *     greater than or equal to size().
   */
  public int getCost(int i, int j) {
    return cost[i][j];
  }

  /**
   * Gets an element of the distance matrix.
   *
   * @param i The row index.
   * @param j The column index.
   * @return The distance of cell i, j.
   * @throws IndexOutOfBoundsException if either i or j is less than 0, or if either i or j is
   *     greater than or equal to size().
   */
  public int getDistance(int i, int j) {
    return distance[i][j];
  }

  /**
   * Creates an instance of the QAP problem from given cost and distance matrices. Note that the
   * {@link #cost cost} and {@link #value value} methods assume that the diagonal is always 0.
   *
   * @param cost The cost matrix which must be square.
   * @param distance The distance matrix which must be square and of the same dimensions as the cost
   *     matrix.
   * @return an instance of the QAP problem from the specified cost and distance matrices
   * @throws IllegalArgumentException if the cost and distance matrices are not square or not of the
   *     same dimensions
   */
  public static QuadraticAssignmentProblem createInstance(int[][] cost, int[][] distance) {
    if (cost.length != distance.length) {
      throw new IllegalArgumentException(
          "cost and distance matrices must have same number of rows");
    }
    int[][] costPrime = new int[cost.length][];
    int[][] distancePrime = new int[distance.length][];
    for (int i = 0; i < cost.length; i++) {
      if (cost[i].length != cost.length || distance[i].length != cost.length) {
        throw new IllegalArgumentException("cost and distance matrices must be square");
      }
      costPrime[i] = cost[i].clone();
      distancePrime[i] = distance[i].clone();
    }
    return new QuadraticAssignmentProblem(costPrime, distancePrime);
  }

  /**
   * Creates an instance of the QAP problem with cost and distance matrices formed from uniformly
   * random integers. Note that the diagonal is always 0.
   *
   * @param size Create a size by size instance.
   * @param minCost Costs are uniform at random from an interval with minCost as the minimum.
   * @param maxCost Costs are uniform at random from an interval with maxCost as the maximum.
   * @param minDistance Distances are uniform at random from an interval with minDistance as the
   *     minimum.
   * @param maxDistance Distances are uniform at random from an interval with maxDistance as the
   *     maximum.
   * @return an instance of the QAP problem with uniform random cost and distance matrices
   * @throws IllegalArgumentException if size is less than 1, or maxCost is less than minCost, or
   *     maxDistance is less than minDistance.
   */
  public static QuadraticAssignmentProblem createUniformRandomInstance(
      int size, int minCost, int maxCost, int minDistance, int maxDistance) {
    return createUniformRandomInstance(
        size,
        minCost,
        maxCost,
        minDistance,
        maxDistance,
        RandomnessFactory.threadLocalEnhancedSplittableGenerator());
  }

  /**
   * Creates an instance of the QAP problem with cost and distance matrices formed from uniformly
   * random integers. Note that the diagonal is always 0.
   *
   * @param size Create a size by size instance.
   * @param minCost Costs are uniform at random from an interval with minCost as the minimum.
   * @param maxCost Costs are uniform at random from an interval with maxCost as the maximum.
   * @param minDistance Distances are uniform at random from an interval with minDistance as the
   *     minimum.
   * @param maxDistance Distances are uniform at random from an interval with maxDistance as the
   *     maximum.
   * @param seed The seed for the random number generator.
   * @return an instance of the QAP problem with uniform random cost and distance matrices
   * @throws IllegalArgumentException if size is less than 1, or maxCost is less than minCost, or
   *     maxDistance is less than minDistance.
   */
  public static QuadraticAssignmentProblem createUniformRandomInstance(
      int size, int minCost, int maxCost, int minDistance, int maxDistance, long seed) {
    return createUniformRandomInstance(
        size,
        minCost,
        maxCost,
        minDistance,
        maxDistance,
        RandomnessFactory.createSeededEnhancedRandomGenerator(seed));
  }

  private static QuadraticAssignmentProblem createUniformRandomInstance(
      int size,
      int minCost,
      int maxCost,
      int minDistance,
      int maxDistance,
      EnhancedRandomGenerator gen) {
    if (size < 1) throw new IllegalArgumentException("size must be at least 1");
    if (maxCost < minCost) throw new IllegalArgumentException("maxCost must be at least minCost");
    if (maxDistance < minDistance)
      throw new IllegalArgumentException("maxDistance must be at least minDistance");
    return new QuadraticAssignmentProblem(
        createRandomMatrix(size, minCost, maxCost, gen),
        createRandomMatrix(size, minDistance, maxDistance, gen));
  }

  private static int[][] createRandomMatrix(
      int size, int min, int max, EnhancedRandomGenerator gen) {
    int[][] matrix = new int[size][size];
    int bound = max - min + 1;
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) {
        matrix[i][j] = min + gen.nextInt(bound);
        matrix[j][i] = min + gen.nextInt(bound);
      }
    }
    return matrix;
  }
}
