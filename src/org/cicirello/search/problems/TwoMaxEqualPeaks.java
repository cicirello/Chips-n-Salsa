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
 * <p>This class implements a variation of the benchmarking problem known
 * as TwoMax. The original TwoMax problem was defined as a problem with
 * one global optima (the vector of all 1-bits) and a sub-optimal local
 * optima (the vector of all 0-bits).  For an implementation of the
 * original TwoMax problem, see the {@link TwoMax} class. In the variation
 * that we define here, we instead have two equally desirable global optima
 * (one of these is the vector of all 1-bits, and the other is the vector
 * of all 0-bits).  We define it as follows. Maximize the
 * function: f(x) = |20*CountOfOneBits(x) - 10*n|, where x is
 * a vector of bits of length n. The two global optimal solutions
 * have a maximal value of 10*n. This search landscape has
 * two basins of attraction, which meet where the vector has an equal number of
 * ones as zeros.</p>
 *
 * <p>The {@link #value} method implements the maximization
 * version as described above. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost} method implements the equivalent
 * as the following minimization problem: minimize
 * cost(x) = 10*n - |20*CountOfOneBits(x) - 10*n|.  The global optima
 * are still all 1-bits or all 0-bits, each of which has a cost 
 * equal to 0.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.18.2021
 */
public final class TwoMaxEqualPeaks implements IntegerCostOptimizationProblem<BitVector> {
	
	/**
	 * Constructs a TwoMaxEqualPeaks object for use 
	 * in evaluating candidate solutions to the
	 * TwoMaxEqualPeaks problem, a variation of the TwoMax problem
	 * but with two globally optimal solutions, rather than one
	 * global optima and a local optima.
	 */
	public TwoMaxEqualPeaks() { }
	
	@Override
	public int cost(BitVector candidate) {
		return 10*candidate.length() - Math.abs(20*candidate.countOnes()-10*candidate.length());
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	@Override
	public int value(BitVector candidate) {
		return Math.abs(20*candidate.countOnes()-10*candidate.length());
	}
	
	@Override
	public boolean isMinCost(int cost) {
		return cost == 0;
	}
}