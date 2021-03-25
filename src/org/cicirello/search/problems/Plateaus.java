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
 * <p>This class implements Ackley's Plateaus problem, an
 * artificial search landscape over the space of bitstrings
 * that is characterized by large flat regions known as plateaus.
 * This is specifically an implementation of Ackley's 1987 version
 * of the problem (he described a similar problem in an earlier 
 * 1985 paper).</p>
 *
 * <p>The Plateaus problem involves maximizing the following function.
 * Divide the bits of the bit string into four equal sized parts.
 * For each of the four parts, check whether all bits in the segment 
 * are equal to a 1, and if so, then that segment contributes 2.5*n
 * to the fitness function, where n is the length of the entire bit 
 * string (if there are any 0s in the segment, then that segment doesn't
 * contribute anything to the fitness function).  Since there are four segments
 * the optimum occurs when the entire bit string is all 1s, which has a maximum
 * fitness of 10*n. The entire search space only has 5 possible fitness
 * values: 0, 2.5*n, 5*n, 7.5*n, and 10*n.</p>
 *
 * <p>The {@link #value value} method implements the original maximization
 * version of the Plateaus problem, as described above. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost cost} method implements the equivalent
 * as the following minimization problem: minimize
 * cost(x) = 10*n - f(x), where f(x) is the Plateaus function as defined above.  
 * The global optima
 * is still all 1-bits, which has a cost equal to 0.</p>
 *
 * <p>The Plateaus problem
 * was introduced by David Ackley in the following paper:<br>
 * David H. Ackley. An empirical study of bit vector function optimization. Genetic
 * Algorithms and Simulated Annealing,
 * pages 170-204, 1987.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.24.2021
 */
public final class Plateaus implements OptimizationProblem<BitVector> {
	
	/**
	 * Constructs an instance of Ackley's Plateaus problem.
	 */
	public Plateaus() {}
	
	@Override
	public double cost(BitVector candidate) {
		return 10*candidate.length() - value(candidate);
	}
	
	@Override
	public double minCost() {
		return 0;
	}
	
	@Override
	public double value(BitVector candidate) {
		// Segment size
		int m = candidate.length() >> 2;
		// Num segments with an extra bit if n not divisible by 4
		int r = candidate.length() & 3;
		int blockCount = 0;
		BitVector.BitIterator iter = candidate.bitIterator(32);
		for (int i = r; i < 4; i++) {
			if (isBlockAllOnes(iter, m)) {
				blockCount++;
			}
		}
		if (r > 0) {
			m++;
			for (int i = 0; i < r; i++) {
				if (isBlockAllOnes(iter, m)) {
					blockCount++;
				}
			}
		}
		return blockCount * candidate.length() * 2.5;
	}
	
	@Override
	public boolean isMinCost(double cost) {
		return cost == 0;
	}
	
	private boolean isBlockAllOnes(BitVector.BitIterator iter, int stillNeed) {
		while (stillNeed >= 32) {
			stillNeed -= 32;
			if (iter.nextBitBlock() != 0xffffffff) {
				while (stillNeed >= 32) {
					iter.nextBitBlock();
					stillNeed -= 32;
				}
				if (stillNeed > 0) {
					iter.nextBitBlock(stillNeed);
				}
				return false;
			}
		}
		if (stillNeed > 0) {
			int mask = (1 << stillNeed) - 1;
			if (iter.nextBitBlock(stillNeed) != mask) {
				return false;
			}
		}
		return true;
	}
 }