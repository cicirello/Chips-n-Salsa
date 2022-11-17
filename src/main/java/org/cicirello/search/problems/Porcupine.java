/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

import org.cicirello.search.representations.BitVector;

/**
 * This class implements the Porcupine landscape (Ackley, 1985), which is a very rugged search
 * landscape, with an exponential number of local optima. The Porcupine problem is a maximization
 * problem to maximize the function: f(x) = 10 * CountOfOneBits(x) - 15 * (CountOfZeroBits(x) mod
 * 2), where x is a vector of bits of length n. The global optimal solution is when x is all ones,
 * which has a maximal value of 10*n.
 *
 * <p>The {@link #value value} method implements the original maximization version of the Porcupine
 * problem, as described above. The algorithms of the Chips-n-Salsa library are defined for
 * minimization, requiring a cost function. The {@link #cost cost} method implements the equivalent
 * as the following minimization problem: minimize cost(x) = 10*n - f(x). The global optima is still
 * all 1-bits, which has a cost equal to 0.
 *
 * <p>The Porcupine problem was introduced by David Ackley in the following paper:<br>
 * David H. Ackley. A connectionist algorithm for genetic search. Proceedings of the First
 * International Conference on Genetic Algorithms and Their Applications, pages 121-135, July 1985.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.18.2021
 */
public final class Porcupine implements IntegerCostOptimizationProblem<BitVector> {

  /**
   * Constructs a Porcupine object for use in evaluating candidate solutions to the Porcupine
   * problem.
   */
  public Porcupine() {}

  @Override
  public int cost(BitVector candidate) {
    int z = candidate.countZeros();
    int cost = 10 * z;
    if ((z & 1) == 1) {
      cost += 15;
    }
    return cost;
  }

  @Override
  public int minCost() {
    return 0;
  }

  @Override
  public int value(BitVector candidate) {
    int c = candidate.countOnes();
    int value = 10 * c;
    if (((candidate.length() - c) & 1) == 1) {
      value -= 15;
    }
    return value;
  }

  @Override
  public boolean isMinCost(int cost) {
    return cost == 0;
  }
}
