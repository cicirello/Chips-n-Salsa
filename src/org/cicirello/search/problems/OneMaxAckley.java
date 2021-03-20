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
 * <p>The OneMaxAckley class is an implementation of 
 * the well-known OneMax problem, often used in benchmarking genetic 
 * algorithms and other metaheuristics.  Specifically, it implements
 * Ackley's (1985) original version of the problem.</p>
 *
 * <p>In the OneMax problem, the metaheuristic is searching the space
 * of bit-strings of length n for the bit-string with the most bits equal
 * to a 1.  It originated as a test problem for genetic algorithms,
 * where the standard form of a genetic algorithm represents solutions
 * to the problem with a string of bits.  The OneMax problem offers
 * a test problem with a known optimal solution, a bit-string of all
 * 1s.  For example, if n=8, then the optimal solution is: 11111111.
 * The OneMax problem has no local optima, and thus should be trivially
 * easy for hill climbers.</p>
 *
 * <p>It was originally posed as a maximization problem because 
 * it was originally defined as a fitness function for a genetic algorithm.
 * The problem was originally stated to maximize f(x) = 10 * CountOfOneBits(x),
 * where x is a vector of bits of length n.
 * The {@link #value value} method returns 10 times the number
 * of bits in the BitVector equal to 1, which is to be maximized. Thus, as 
 * a cost function, the {@link #cost cost} method returns 10 times the number
 * of bits not equal to 1, where the minimum cost is thus 0, corresponding
 * to the case of maximal number of 1-bits.</p>
 *
 * <p>The Chips-n-Salsa library also includes a version that is a simple
 * count of the bits without the multiplication by 10 in the {@link OneMax}
 * class.</p>
 *
 * <p>Although commonly used by others without reference, the OneMax problem
 * was introduced by David Ackley in the following paper:<br>
 * David H. Ackley. A connectionist algorithm for genetic search. Proceedings of
 * the First International Conference on Genetic Algorithms and Their Applications,
 * pages 121-135, July 1985.</p>
 * 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.20.2021
 */
public final class OneMaxAckley implements IntegerCostOptimizationProblem<BitVector> {
	
	/**
	 * Constructs a OneMaxAckley object for use in evaluating candidate solutions to the
	 * OneMax problem.
	 */
	public OneMaxAckley() { }
	
	@Override
	public int cost(BitVector candidate) {
		return 10*candidate.countZeros();
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	@Override
	public int value(BitVector candidate) {
		return 10*candidate.countOnes();
	}
	
	@Override
	public boolean isMinCost(int cost) {
		return cost == 0;
	}
}