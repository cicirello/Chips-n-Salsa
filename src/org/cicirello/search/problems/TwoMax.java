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
 * <p>This class implements the benchmarking problem known
 * as TwoMax. The TwoMax problem is to maximize the following
 * function: f(x) = |18*CountOfOneBits(x) - 8*n|, where x is
 * a vector of bits of length n. The global optimal solution is
 * when x is all ones, which has a maximal value of 10*n. This 
 * search landscape also has a local optima when x is all zeros,
 * which has a value of 8*n. Thus, this search landscape has
 * two basins of attraction. The attractions basin for the 
 * global optima is slightly larger. As long as x has more than
 * (4/9)n bits equal to a one, a strict hill climber will
 * be pulled into the global optima. However, a search that
 * ends up at the local optima would have a very steep climb
 * to escape.</p>
 *
 * <p>The {@link #value value} method implements the original maximization
 * version of the TwoMax problem, as described above. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost cost} method implements the equivalent
 * as the following minimization problem: minimize
 * cost(x) = 10*n - |18*CountOfOneBits(x) - 8*n|.  The global optima
 * is still all 1-bits, which has a cost equal to 0.  The local optima
 * is still all 0-bits, which has a cost equal to 2*n.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.18.2021
 */
public final class TwoMax implements IntegerCostOptimizationProblem<BitVector> {
	
	/**
	 * Constructs a TwoMax object for use in evaluating candidate solutions to the
	 * TwoMax problem.
	 */
	public TwoMax() { }
	
	@Override
	public int cost(BitVector candidate) {
		return 10*candidate.length() - Math.abs(18*candidate.countOnes()-8*candidate.length());
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	@Override
	public int value(BitVector candidate) {
		return Math.abs(18*candidate.countOnes()-8*candidate.length());
	}
	
	@Override
	public boolean isMinCost(int cost) {
		return cost == 0;
	}
}