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
 * <p>Implementation of Holland's Royal Road problem, as described in the following
 * paper:<br>
 * Terry Jones. A Description of Holland's Royal Road Function. Evolutionary Computation
 * 2(4): 409-415, 1995.<br>
 * Originally described by Holland in:<br>
 * J.H. Holland. Royal Road Functions. Internet Genetic Algorithms Digest, 7(22), 1993.</p>
 *
 * <p>We suggest that the Jones (1995) paper be consulted for a detailed description and
 * detailed example of Holland's Royal Road function.</p>
 *
 * <p>Note that if you are looking for the original Royal Road problems of Mitchell, Forrest, and
 * Holland, see the {@link RoyalRoad} class.</p>
 *
 * <p>The problem is defined with several parameters. Part of the fitness function
 * is calculated over k+1 levels, where there are 2<sup>k</sup> regions in level 0, and
 * each level i has 2<sup>k-i</sup> regions. Each region at level 0 begins with a
 * block of b bits, which is followed by a gap of g bits. Thus each region at level 0
 * contains b + g bits. Each region of level 1 is the concatenation of 2 consecutive regions
 * from level 0. Likewise, each region of level 2 is the concatenation of 2 consecutive regions
 * from level 1, and so forth, until level k which is simply the entire bit vector.</p>
 *
 * <p>Fitness evaluation includes two components. The first is referred to as the Part fitness,
 * in which each level 0 region contributes to the fitness of the bit vector. The gap bits of
 * each region are ignored and don't factor into fitness. If all of the bits in the b-bit
 * block are all ones, then the region doesn't factor into the Part calculation (only the 
 * Bonus calculation). Otherwise, if there are mStar or less one bits in the block, then each contributes
 * v to the fitness, and otherwise if there are more than mStar bits in the block then the
 * excess bits are each penalized in the fitness by b.  For example, suppose the block size b=8, 
 * mStar=4, and v=0.02. If there is only a single one-bit, then the block contributes 0.02 to
 * the fitness. If there are 2 one-bits, then the block contributes 0.04 to the fitness.
 * If there are 3 one-bits, then the block contributes 0.06 to the fitness.
 * If there are 4 one-bits, then the block contributes 0.08 to the fitness.
 * However, if there are 5, 6, or 7 one-bits, then the block causes a reduction in fitness.
 * For example, if there are 6 one-bits, then the 2 extra (above the mStar of 4) each incur
 * a penalty of -0.02 (total penalty of 0.04). If all 8 bits are ones, no increase nor decrease
 * in fitness occurs during the Part calculation.</p>
 *
 * <p>The second phase of fitness is called the Bonus calculation. The Bonus calculation is 
 * computed over the k+1 levels. The first complete block (all one-bits) at level 0 contributes
 * uStar to the fitness, and each additional complete block at level 0 contributes u to the fitness.
 * For example, if there are 5 complete blocks at level 0, and if uStar is 1.0 and u is 0.3, then
 * the level 0 bonus is 1.0 + 4 * 0.3 = 2.2.  Level 1 then consists of half as many regions,
 * each region formed by the concatenation of two consecutive regions of level 0. Note that each
 * each at level 1 will consist of b block bits, followed by g gap bits, followed by b block bits,
 * followed by g gap bits. At level 1, a complete block is when the 2b bits in the 2 block segments
 * are all ones. Just like level 0, the first complete block at level 1 contributes uStar to fitness, and then
 * each additional complete block at level 1 contributes u to the fitness. This then proceeds through
 * levels 2, 3, ..., k.</p>
 *
 * <p>Holland's Royal Road function is a fitness function, and thus must be maximized. Due to the penalty 
 * terms in the Part calculation, it can evaluate to negative values. The {@link #value value} method
 * computes the fitness function of this problem.  Since the metaheuristics of the
 * Chips-n-Salsa library assume minimization problems, the {@link #cost cost} method transforms the
 * problem to minimization. It does this by computing cost = MaxFitness - value. MaxFitness can be
 * computed from the parameters of the problem, k, b, g, mStar, v, uStar, and u. So although
 * the {@link #value value} method may return negative values, the {@link #cost cost} method
 * is guaranteed to never return a negative, and the optimal solution to an instance has a cost of 0.
 * Although note that each instance will have many optimal solutions due to the gap bits which do not
 * affect fitness or cost.</p>
 *
 * <p>The {@link #value value} and {@link #cost cost} methods will throw exceptions if you attempt
 * to evaluate a BitVector whose length is inconsistent with the parameters passed to the constructor.
 * The {@link #supportedBitVectorLength supportedBitVectorLength} method returns the length of
 * BitVector supported by an instance of the problem, which is 2<sup>k</sup>(b + g).</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.28.2021
 */
public final class HollandRoyalRoad implements OptimizationProblem<BitVector> {
	
	private final int blockSize;
	private final int gapSize;
	private final int k;
	private final int numBlocks;
	private final int n;
	private final int mStar;
	private final double v;
	private final double uStar;
	private final double u;
	private final double maxFitness;
	
	/**
	 * Constructs an instance of Holland's Royal Road fitness function,
	 * with Holland's original set of default parameter values: k=4, b=8,
	 * g=7, mStar=4, v=0.02, uStar=1.0, and u=0.3.
	 */
	public HollandRoyalRoad() {
		this(4, 8, 7, 4, 0.02, 1.0, 0.3);
	}
	
	/**
	 * Constructs an instance of Holland's Royal Road fitness function.
	 *
	 * @param k The number of level 0 blocks. There will be 2<sup>k</sup> level 0 blocks
	 * (see the references listed in the class comment for relevant definitions). The number of
	 * levels in the function is: k + 1.
	 * @param blockSize The number of bits in each block. This parameter was originally simply b
	 * in Holland's description as well as Jones's more detailed presentation.
	 * @param gapSize The number of bits in the gap following each block. This parameter was 
	 * originally simply g in Holland's description as well as in Jones's more detailed presentation.
	 * The gap bits are ignored by the fitness function. 
	 * @param mStar This is a parameter used in calculating what Holland called the Part fitness (originally
	 * named m* by Holland). It is the number of one-bits that a block may have before being penalized
	 * for having too many ones. The penalty applies to any blocks that have more than mStar ones, but less than
	 * blockSize ones.
	 * @param v This parameter is also used in calculating Holland's Part fitness. If there are no more than
	 * mStar ones in the block, then v is a reward for each one-bit in the block. If there are more than
	 * mStar ones in the block but less than blockSize, then v is a penalty per one-bit in the block.
	 * If the block is complete (all one-bits), then the Part fitness is 0.
	 * @param uStar This parameter is used in calculating Holland's Bonus fitness. At each level, the first
	 * completed (all one-bits) block (or block set) received a fitness bonus of uStar.
	 * @param u This parameter is used in calculating Holland's Bonus fitness. At each level, each completed
	 * (all one-bits) block (or block set) after the first contributes an additional u to the fitness.
	 * @throws IllegalArgumentException if k &lt; 0 or blockSize &lt; 1 or gapSize &lt; 0 or mStar &lt; 0
	 * or mStar &gt; blockSize or v &lt; 0.0 or uStar &lt; 0.0 or u &lt; 0.0
	 */
	public HollandRoyalRoad(int k, int blockSize, int gapSize, int mStar, double v, double uStar, double u) {
		if (k < 0) {
			throw new IllegalArgumentException("k must be non-negative");
		} else if (blockSize < 1) {
			throw new IllegalArgumentException("blockSize must be positive");
		} else if (gapSize < 0) {
			throw new IllegalArgumentException("gapSize must be non-negative");
		} else if (mStar < 0 || mStar > blockSize) {
			throw new IllegalArgumentException("mStar must be non-negative and not greater than blockSize");
		} else if (v < 0.0) {
			throw new IllegalArgumentException("v must be non-negative");
		} else if (uStar < 0.0) {
			throw new IllegalArgumentException("uStar must be non-negative");
		} else if (u < 0.0) {
			throw new IllegalArgumentException("u must be non-negative");
		}		
		this.blockSize = blockSize;
		this.gapSize = gapSize;
		this.k = k;
		numBlocks = 1 << k;
		n = numBlocks * (blockSize + gapSize);
		this.mStar = mStar;
		this.v = v;
		this.uStar = uStar;
		this.u = u;
		maxFitness = Math.max(maxBonusFitness(), maxPartFitness());
	}
	
	/**
	 * The length of BitVectors supported by this instance of HollandRoyalRoad,
	 * which is 2<sup>k</sup>(blockSize + gapSize).
	 * @return the length BitVector supported by this instance of HollandRoyalRoad.
	 */
	public int supportedBitVectorLength() {
		return n;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if candidate.length() is not equal to 2<sup>k</sup>(blockSize + gapSize).
	 * See the {@link #supportedBitVectorLength} method.
	 */
	@Override
	public double cost(BitVector candidate) {
		return maxFitness - value(candidate);
	}
	
	@Override
	public double minCost() {
		return 0.0;
	}
	
	@Override
	public boolean isMinCost(double cost) {
		// minCost really should be equal to 0.0,
		// but checking with <= to handle potential floating-point rounding error
		// in calculation of cost.
		return cost <= 0.0;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if candidate.length() is not equal to 2<sup>k</sup>(blockSize + gapSize).
	 * See the {@link #supportedBitVectorLength} method.
	 */
	@Override
	public double value(BitVector candidate) {
		if (candidate.length() != n) {
			throw new IllegalArgumentException("The candidate BitVector's length is inconsistent with this HollandRoyalRoad's configuration.");
		}
		double fitness = 0;
		BitVector.BitIterator iter = candidate.bitIterator(blockSize);
		boolean[] completedBlocks = new boolean[numBlocks];
		for (int i = 0; i < numBlocks; i++) {
			int partBitCount = new BitVector(blockSize, iter.nextLargeBitBlock(blockSize)).countOnes();
			if (partBitCount < blockSize) {
				if (partBitCount <= mStar) {
					fitness += v * partBitCount;
				} else {
					fitness -= (partBitCount - mStar) * v;
				}
			} else {
				completedBlocks[i] = true;
			}
			iter.skip(gapSize);
		}
		fitness += bonus(completedBlocks);
		return fitness;
	}
	
	/*
	 * This computes Holland's "bonus" calculation.
	 */
	private double bonus(boolean[] completedBlocks) {
		double bonusFitness = 0;
		int size = completedBlocks.length;
		for (int level = 0; level <= k; level++) {
			int count = 0;
			for (int i = 0; i < size; i++) {
				if (completedBlocks[i]) count++;
			}
			if (count > 0) {
				bonusFitness += uStar + (count - 1) * u;
				size >>= 1;
				for (int j = 0; j < size; j++) {
					int x = j << 1;
					completedBlocks[j] = completedBlocks[x] && completedBlocks[x+1];  
				}
			} else {
				break;
			}
		}
		return bonusFitness;
	}
	
	private double maxBonusFitness() {
		return (k+1)*uStar + u*((1 << (k+1)) - k - 2);
	}
	
	private double maxPartFitness() {
		if (mStar >= blockSize) {
			return (mStar - 1) * v * numBlocks;
		} else {
			return mStar * v * numBlocks;
		}
	}
}