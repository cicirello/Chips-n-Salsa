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
 *
 *
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