/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
 * <p>The OneMax class is an implementation of 
 * the well-known OneMax problem, often used in benchmarking genetic 
 * algorithms and other metaheuristics.</p>
 *
 * <p>In the OneMax problem, the metaheuristic is searching the space
 * of bit-strings of length n for the bit-string with the most bits equal
 * to a 1.  It originated as a test problem for genetic algorithms,
 * where the standard form of a genetic algorithm represents solutions
 * to the problem with a string of bits.  The OneMax problem offers
 * a test problem with a known optimal solution, a bit-string of all
 * 1s.  For example, if n=8, then the optimal solution is: 11111111.</p>
 *
 * <p>The {@link #value value} method simply counts the number
 * of bits in the BitVector equal to 1.  The problem is to maximize this count.  Thus, as 
 * a cost function, the {@link #cost cost} method counts the number
 * of bits not equal to 1, where the minimum cost is thus 0.</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.11.2020
 */
public final class OneMax implements IntegerCostOptimizationProblem<BitVector> {
	
	/**
	 * Constructs a OneMax object for use in evaluating candidate solutions to the
	 * OneMax problem.
	 */
	public OneMax() { }
	
	@Override
	public int cost(BitVector candidate) {
		return candidate.countZeros();
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	@Override
	public int value(BitVector candidate) {
		return candidate.countOnes();
	}
	
	@Override
	public boolean isMinCost(int cost) {
		return cost == 0;
	}
}