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

import org.cicirello.search.operators.integers.IntegerVectorInitializer;
import org.cicirello.search.representations.IntegerVector;

/**
 * The BoundMax class is an implementation of a generalization of the well-known OneMax problem,
 * often used in benchmarking genetic algorithms and other metaheuristics.
 *
 * <p>In the OneMax problem, the metaheuristic is searching the space of bit-strings of length n for
 * the bit-string with the most bits equal to a 1. It originated as a test problem for genetic
 * algorithms, where the standard form of a genetic algorithm represents solutions to the problem
 * with a string of bits. The OneMax problem offers a test problem with a known optimal solution, a
 * bit-string of all 1s. For example, if n=8, then the optimal solution is: 11111111.
 *
 * <p>BoundMax generalizes OneMax to vectors of integers such that each integer is bound in the
 * interval [0,B] for some B &ge; 1. The problem is to find the vector of length n with maximum
 * number of integers equal to B. The optimal solution is thus n copies of B. For example, if n is
 * 8, the optimal solution is [B, B, B, B, B, B, B, B]. The OneMax problem is the special case when
 * B=1.
 *
 * <p>The {@link #value value} method simply counts the number of components equal to B. The problem
 * is to maximize this count. Thus, as a cost function, the {@link #cost cost} method counts the
 * number of components not equal to B, where the minimum cost is thus 0.
 *
 * <p>The BoundMax class extends {@link IntegerVectorInitializer} to ensure that metaheuristics
 * solving an instance have access to a correct means of generating valid vectors within the search
 * space (correct length and components in the interval [0,B].
 *
 * <p>Although technically you can use the BoundMax class, which evaluates IntegerVector objects,
 * using a bound B=1, to define the OneMax problem, you should instead use the {@link OneMax} class
 * for the original OneMax problem. The {@link OneMax} class evaluates {@link
 * org.cicirello.search.representations.BitVector} objects, which is a proper implementation of an
 * indexable vector of bits.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BoundMax extends IntegerVectorInitializer
    implements IntegerCostOptimizationProblem<IntegerVector> {

  private final int b;
  private final int n;

  /**
   * Constructs an instance of the BoundMax problem.
   *
   * @param n The length of the instance (length of the array under optimization).
   * @param bound The maximum value allowed for each integer.
   * @throws IllegalArgumentException if bound is negative
   * @throws NegativeArraySizeException if n is negative
   */
  public BoundMax(int n, int bound) {
    super(n, 0, bound + 1, 0, bound);
    b = bound;
    this.n = n;
  }

  @Override
  public int cost(IntegerVector candidate) {
    return n - value(candidate);
  }

  @Override
  public int value(IntegerVector candidate) {
    if (candidate == null) return 0;
    int sum = 0;
    int m = candidate.length() < n ? candidate.length() : n;
    for (int i = 0; i < m; i++) {
      if (candidate.get(i) == b) sum++;
    }
    return sum;
  }

  @Override
  public int minCost() {
    return 0;
  }

  @Override
  public boolean isMinCost(int cost) {
    return cost == 0;
  }

  @Override
  public boolean equals(Object other) {
    if (!super.equals(other)) {
      return false;
    }
    BoundMax o = (BoundMax) other;
    return b == o.b && n == o.n;
  }
}
