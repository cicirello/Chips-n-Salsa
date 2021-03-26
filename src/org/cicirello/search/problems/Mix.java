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
 * <p>This class implements Ackley's Mix problem, an artificial landscape that
 * is a mix of the OneMax, TwoMax, Trap, and Plateau problems, which provides
 * for a landscape that combines all of the properties of these benchmarking
 * problems. For details of the 5 component search landscapes, see the 
 * {@link OneMaxAckley}, {@link TwoMax}, {@link Trap}, {@link Porcupine},
 * and {@link Plateau} classes.</p>
 *
 * <p>The Mix problem is defined as the following maximization problem.
 * Maximize the fitness function, f(x), of bit string x, such that we
 * do the following. Break x into 5 equal-sized segments, and sum the fitnesses
 * of the 5 segments, where the first segment is scores as a OneMax instance,
 * the second segment is scored as a TwoMax instance, the third segment is 
 * scored as a Trap instance, the fourth segment is scores as a Porcupine
 * instance, and the fifth is scored as one segment of a Plateau instance.
 * Note that the fifth segment is not scored directly as a full Plateau instance,
 * but rather if all of the bits of that segment are 1s, then it scores as 10*p,
 * where p is the length of that segment, and otherwise it scores as 0.
 * The optimum occurs when the entire bit string is all 1s, which has a maximum
 * fitness of 10*n.</p>
 *
 * <p>The {@link #value value} method implements the original maximization
 * version of the Mix problem, as described above. The algorithms
 * of the Chips-n-Salsa library are defined for minimization, requiring
 * a cost function. The {@link #cost cost} method implements the equivalent
 * as the following minimization problem: minimize
 * cost(x) = 10*n - f(x), where f(x) is the Mix function as defined above.  
 * The global optima
 * is all 1-bits, which has a cost equal to 0.</p>
 *
 * <p>The Mix problem
 * was introduced by David Ackley in the following paper:<br>
 * David H. Ackley. An empirical study of bit vector function optimization. Genetic
 * Algorithms and Simulated Annealing,
 * pages 170-204, 1987.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.26.2021
 */
public final class Mix implements OptimizationProblem<BitVector> {
	
	private final OneMaxAckley onemax;
	private final TwoMax twomax;
	private final Trap trap;
	private final Porcupine porcupine;
	
	/**
	 * Constructs an instance of Ackley's Mix problem.
	 */
	public Mix() {
		onemax = new OneMaxAckley();
		twomax = new TwoMax();
		trap = new Trap();
		porcupine = new Porcupine();
	}
	
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
		int m = candidate.length() / 5;
		// Num segments with an extra bit if n not divisible by 4
		int r = candidate.length() % 5;
		BitVector[] subVectors = new BitVector[5];
		BitVector.BitIterator iter = candidate.bitIterator();
		for (int i = r; i < 5; i++) {
			subVectors[i] = new BitVector(m, iter.nextLargeBitBlock(m));
		}
		if (r > 0) {
			m++;
			for (int i = 0; i < r; i++) {
				subVectors[i] = new BitVector(m, iter.nextLargeBitBlock(m));
			}
		}
		int plateauValue = subVectors[4].allOnes() ? 10 * subVectors[4].length() : 0;
		return onemax.value(subVectors[0]) 
			+ twomax.value(subVectors[1])
			+ trap.value(subVectors[2])
			+ porcupine.value(subVectors[3])
			+ plateauValue;
	}
	
	@Override
	public boolean isMinCost(double cost) {
		return cost == 0;
	}
}