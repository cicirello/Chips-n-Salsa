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
 * 1s.  For example, if n=8, then the optimal solution is: 11111111.
 * The OneMax problem has no local optima, and thus should be trivially
 * easy for hill climbers.</p>
 *
 * <p>It was originally posed as a maximization problem because 
 * it was originally defined as a fitness function for a genetic algorithm.
 * The {@link #value value} method simply counts the number
 * of bits in the BitVector equal to 1, which is to be maximized. Thus, as 
 * a cost function, the {@link #cost cost} method counts the number
 * of bits not equal to 1, where the minimum cost is thus 0, corresponding
 * to the case of maximal number of 1-bits.</p>
 *
 * <p>The OneMax problem was introduced by Ackley (1985). His original
 * definition of the problem was to maximize: f(x) = 10 * CountOfOneBits(x).
 * Thus, Ackley's original OneMax multiplied the number of 1-bits by 10.
 * Our implementation does not multiply by 10. Doing so does not change the
 * optimal solution or the shape of the landscape. However, it may have
 * an effect on the behavior of some search algorithms. For example, 
 * simulated annealing decides whether or not to accept a worsening move
 * with a probability that depends on the difference in cost between the current
 * solution and the random neighbor, as well as on its current temperature.
 * Keeping all else the same and scaling the cost values can lead to 
 * different acceptance probabilities (for a specific temperature value).
 * If you want to use Ackley's original version, or any other scaling for that
 * matter, you can use 
 * the {@link IntegerCostFunctionScaler} class for this purpose. You can do so by 
 * defining your optimization problem with something like:
 * IntegerCostFunctionScaler&lt;BitVector&gt; problem = 
 * new IntegerCostFunctionScaler&lt;BitVector&gt;(new OneMax());
 * Additionally, the {@link OneMaxAckley} class specifically implements
 * Ackley's version with the costs scaled by a factor of 10.</p>
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