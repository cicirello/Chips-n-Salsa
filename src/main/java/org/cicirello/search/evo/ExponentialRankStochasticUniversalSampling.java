/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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
 
package org.cicirello.search.evo;

/**
 * <p>This class implements exponential rank selection using Stochastic Universal Sampling (SUS). 
 * Exponential rank selection begins
 * be determining the rank of each population member, where the least fit
 * member of the population has rank 1, and the most fit member of the
 * population has rank N, where the population size is N. During selection,
 * the population member with rank r is chosen randomly with probability
 * proportional to: c<sup>N-r</sup>. The c is a real-valued parameter
 * that must be in the interval (0, 1). The most-fit member of the population
 * will be chosen with probability proportional to 1, while the least-fit will
 * be chosen with probability proportional to c<sup>N-1</sup>. In the limit
 * as c approaches 1, exponential selection converges to a uniform random selection
 * method. Whereas in the limit as c approaches 0, exponential selection converges
 * upon a degenerate selection method that chooses an entire population of copies
 * of the single most-fit population member. The lower the value of c, the faster the
 * degree of exponential decline in weight given to lower ranked population members.</p>
 *
 * <p>However, whereas the standard form of exponential rank selection is like spinning a carnival wheel
 * with a single pointer M times to select M members of the population, this SUS version instead is
 * like spinning a carnival wheel that has M equidistant pointers a single time to select
 * all M simultaneously. One statistical consequence of this is that it reduces the variance
 * of the selected copies of population members as compared to the other approach.
 * Another consequence is that SUS is typically much faster since only a single random floating
 * point number is needed per generation, compared to M random floating-point numbers.
 * </p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N lg N + M), which includes the need to generate only a single random double, and O(M) random ints.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ExponentialRankStochasticUniversalSampling extends StochasticUniversalSampling {
	
	private final double c;
	
	/**
	 * Construct an exponential rank selection operator that uses stochastic universal sampling.
	 * @param c The base of the exponential, such that c is in the interval (0.0, 1.0). 
	 * The closer c is to 0, the faster
	 * the selection weights decline from most-fit population member to least-fit, and the
	 * closer c is to 1, the closer the selection method is to a uniform random process.
	 *
	 * @throws IllegalArgumentException if c is less than or equal to 0 or greater than or equal to 1.
	 */
	public ExponentialRankStochasticUniversalSampling(double c) {
		super();
		if (c <= 0.0 || c >= 1.0) throw new IllegalArgumentException("c must be int he interval (0.0, 1.0).");
		this.c = c;
	}
	
	@Override
	public ExponentialRankStochasticUniversalSampling split() {
		// Since this selection operator maintains no mutable state, it is
		// safe for multiple threads to share a single instance, so just return this.
		return this;
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
		return computeWeightRunningSumRanks(
			sortedIndexes(fitnesses), 
			r -> Math.pow(c, fitnesses.size() - r - 1)
		);
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
		return computeWeightRunningSumRanks(
			sortedIndexes(fitnesses), 
			r -> Math.pow(c, fitnesses.size() - r - 1)
		);
	}
}
