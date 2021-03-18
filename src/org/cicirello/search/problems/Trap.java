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
 * <p>This class implements Ackley's Trap function, which defines
 * a fitness landscape with a single global optima, and a single
 * sub-optimal local optima, such that most of the search landscape
 * is within the attraction basin of the local optima.
 * The Trap function is related to the {@link TwoMax} problem,
 * but in the TwoMax problem, slightly more than half of the search space
 * is within the attraction basin of the global optima.</p>
 *
 * <p>The Trap problem is to maximize the following
 * fitness function, f(x), where x is a vector of n bits. 
 * Let z = 0.75n.  If CountOfOneBits(x) &le; z,
 * then f(x) = (8n/z)(z-c). Otherwise, f(x) = (10n/(n-z))(c-z).</p>
 *
 * <p>The global optimal solution is
 * when x is all ones, which has a maximal value of 10*n. This 
 * search landscape also has a local optima when x is all zeros,
 * which has a value of 8*n. Only bit vectors with at least 3/4 of
 * the bits equal to a one are within the attraction basin of the global
 * optima.</p>
 *
 * <p>The {@link #value value} method implements the original maximization
 * version of the Trap problem, as described above. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost cost} method implements the equivalent
 * as the following minimization problem: minimize
 * cost(x) = 10*n - f(x), where f(x) is the Trap function as defined above.  
 * The global optima
 * is still all 1-bits, which has a cost equal to 0.  The local optima
 * is still all 0-bits, which has a cost equal to 2*n.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.18.2021
 */
public final class Trap implements OptimizationProblem<BitVector> {
	
	/**
	 * Constructs an instance of Ackley's Trap function.
	 */
	public Trap() {}
	
	@Override
	public double cost(BitVector candidate) {
		int c = candidate.countOnes();
		if (c <= 0.75*candidate.length()) {
			return 2*candidate.length() + 10.666666666666666*c;
		} else {
			return 40 * (candidate.length() - c);
		}
	}
	
	@Override
	public double minCost() {
		return 0;
	}
	
	@Override
	public double value(BitVector candidate) {
		int c = candidate.countOnes();
		if (c <= 0.75*candidate.length()) {
			return 8*candidate.length() - 10.666666666666666*c;
		} else {
			return 40*c - 30*candidate.length();
		}
	}
	
	@Override
	public boolean isMinCost(double cost) {
		return cost == 0;
	}
}