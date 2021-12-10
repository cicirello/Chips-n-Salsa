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
import org.cicirello.search.evo.FitnessFunction;

/**
 * <p>Implementation of the Royal Road problem of Mitchell, Forrest, and Holland,
 * both the variation with stepping stones and the one without. The problem was
 * introduced in the paper:<br>
 * M. Mitchell, S. Forrest, and J.H. Holland. The Royal Road for Genetic Algorithms:
 * Fitness Landscapes and GA Performance. In Proceedings of the First European
 * Conference on Artificial Life, 1992.</p>
 *
 * <p>Note that if you are looking for Holland's Royal Road problem,
 * see the {@link HollandRoyalRoad} class.</p>
 *
 * <p>Mitchell et al. described two versions of the problem. The first is an optimization
 * problem over bit strings of length 64. The fitness function to optimize in the
 * problem breaks the bit string into 8 equal length blocks of length 8. Each
 * block that contains all ones contributes 8 to the fitness of the bit string. And if the
 * bit string itself is all ones, then this contributes an additional 64 to the fitness.
 * The maximum fitness is thus 128 (when all of the bits are ones).</p>
 *
 * <p>In the second variation of the problem, Mitchell et al. added additional stepping
 * stones to the fitness function. The fitness function includes the above (scores for 8-bit blocks
 * of all ones, and a full string of all ones). Additionally, each of the four 16-bit
 * quarters contribute 16 to the fitness if it is all ones, and each of the two 32-bit halves
 * contribute 32 to the fitness if it is all ones. Thus, the maximum fitness in this case
 * is 256 (when all of the bits are ones).</p>
 *
 * <p>In our implementation, we generalize the original problem to bit vectors of any
 * length and for different block sizes. The following code block evaluates a random BitVector
 * using the equivalent of Mitchell et al's original RoyalRoad fitness function (without 
 * stepping stones):</p>
 * <pre><code>
 * RoyalRoad problem = new RoyalRoad(8, false);
 * BitVector b = new BitVector(64, true);
 * int fitness = problem.value(b);
 * </code></pre>
 * <p>The following code block evaluates a random BitVector
 * using the equivalent of Mitchell et al's original RoyalRoad fitness function (with 
 * stepping stones):</p>
 * <pre><code>
 * RoyalRoad problem = new RoyalRoad(8, true);
 * BitVector b = new BitVector(64, true);
 * int fitness = problem.value(b);
 * </code></pre>
 * <p>In our generalization, without stepping stones, the maximum fitness (i.e., the maximum
 * that can be returned by the {@link #value value} method) is 2*n where n is the 
 * length of the BitVector. With stepping stones, the maximum fitness varies based on
 * the initial block size. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost cost} method implements the equivalent
 * as a minimization problem with minimum cost of 0.</p>
 *
 * <p>The {@link #fitness} method returns 1 greater than the {@link #value value} method
 * because the library requires fitness to be positive, and the original function has a 
 * minimum of 0.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.27.2021
 */
public final class RoyalRoad implements IntegerCostOptimizationProblem<BitVector>, FitnessFunction.Integer<BitVector> {
	
	private final int blockSize;
	private final boolean steppingStones;
	
	/**
	 * Constructs a RoyalRoad function.
	 * @param blockSize The size of the blocks, which must be positive.
	 * @param steppingStones If true, the version of the problem with stepping stones
	 * will be constructed.
	 * @throws IllegalArgumentException if blockSize &lt; 1.
	 */
	public RoyalRoad(int blockSize, boolean steppingStones) {
		if (blockSize < 1) throw new IllegalArgumentException("blockSize must be positive");
		this.blockSize = blockSize;
		this.steppingStones = steppingStones;
	}
	
	@Override
	public int cost(BitVector candidate) {
		int maxValue = steppingStones ?
			(2 + intermediateLevelCount(candidate.length())) * candidate.length() :
			candidate.length() << 1;
		return maxValue - value(candidate);
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
	public int value(BitVector candidate) {
		int total = candidate.allOnes() ? candidate.length() : 0;
		total += blockSize > 32 ? 
			calculateLevelLargeBlock(candidate, blockSize) :
			calculateLevel(candidate, blockSize);
		if (steppingStones) {
			for (int m = blockSize << 1; m < candidate.length(); m <<= 1) {
				total += m > 32 ? 
					calculateLevelLargeBlock(candidate, m) :
					calculateLevel(candidate, m);
			}
		}
		return total;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Computes fitness as: 1 + value(candidate), which ensures that fitness is always positive.</p>
	 */
	@Override
	public int fitness(BitVector candidate) {
		return value(candidate) + 1;
	}
	
	private int calculateLevelLargeBlock(BitVector candidate, int m) {
		int total = 0;
		BitVector.BitIterator iter = candidate.bitIterator(32);
		while (iter.numRemainingBits() >= m) {
			if (new BitVector(m, iter.nextLargeBitBlock(m)).allOnes()) {
				total += m;
			}
		}
		m = iter.numRemainingBits();
		if (m > 0 && new BitVector(m, iter.nextLargeBitBlock(m)).allOnes()) {
			total += m;
		}
		return total;
	}
	
	private int calculateLevel(BitVector candidate, int m) {
		int total = 0;
		BitVector.BitIterator iter = candidate.bitIterator(m);
		int mask = m == 32 ? -1 : (1 << m) - 1;
		while (iter.numRemainingBits() >= m) {
			if (iter.nextBitBlock() == mask) {
				total += m;
			}
		}
		m = iter.numRemainingBits();
		if (m > 0) {
			mask = (1 << m) - 1;
			if (iter.nextBitBlock() == mask) {
				total += m;
			}
		}
		return total;
	}
	
	private int intermediateLevelCount(int n) {
		int count = 0;
		for (int m = blockSize << 1; m < n; m <<= 1) {
			count++;
		}
		return count;
	}
	
	@Override
	public RoyalRoad getProblem() {
		return this;
	}
}