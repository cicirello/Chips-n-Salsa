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
 
package org.cicirello.search.evo;

/**
 * <p>This class implements a variation of Stochastic Universal Sampling (SUS) that uses
 * shifted fitness values. Specifically, it shifts all fitness values by the minimum
 * fitness minus one, such that the least fit population member's selection probability
 * is based on a transformed fitness equal to 1. 
 * A member of the population is chosen randomly with probability proportional to this
 * shifted fitness relative to the total shifted fitness of the population. For example, if the 
 * fitness of population member i is f<sub>i</sub>, and if the minimum fitness in the population
 * is f<sub>min</sub>, then the probability of selecting
 * population member i is: 
 * (f<sub>i</sub> - f<sub>min</sub> + 1) / &sum;<sub>j</sub> (f<sub>j</sub> - f<sub>min</sub> + 1), 
 * for j &isin;
 * { 1, 2, ..., N }, where N is the population size.</p>
 *
 * <p>However, whereas fitness proportional selection is like spinning a carnival wheel
 * with a single pointer M times to select M members of the population, SUS instead is
 * like spinning a carnival wheel that has M equidistant pointers a single time to select
 * all M simultaneously. One statistical consequence of this is that it reduces the variance
 * of the selected copies of population members as compared to fitness proportional selection.
 * Another consequence is that SUS is typically much faster since only a single random floating
 * point number is needed per generation, compared to M random floating-point numbers for fitness proportional
 * selection. However, SUS then must randomize the ordering of the population to avoid all of the
 * copies of a single population member from being in sequence so that parent assignment is random,
 * whereas fitness proportional selection has this property built in.</p>
 *
 * <p>This selection operator is compatible with all fitness functions, even in the case of
 * negative fitness values.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N + M), which includes the need to generate only a single random double, and O(M) ints.</p>
 *
 * @deprecated Instead of this class, you should use a combination of {@link FitnessShifter} and 
 * {@link StochasticUniversalSampling}. This class is scheduled for removal in release 6.0.0.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@Deprecated
public final class ShiftedStochasticUniversalSampling extends StochasticUniversalSampling {
	
	/**
	 * Construct a shifted stochastic universal sampling operator.
	 */
	public ShiftedStochasticUniversalSampling() {
		super();
	}
	
	@Override
	public ShiftedStochasticUniversalSampling split() {
		// Since this selection operator maintains no mutable state, it is
		// safe for multiple threads to share a single instance, so just return this.
		return this;
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
		double[] p = new double[fitnesses.size()];
		int adjustment = fitnesses.getFitness(0);
		for (int i = 1; i < p.length; i++) {
			if (fitnesses.getFitness(i) < adjustment) {
				adjustment = fitnesses.getFitness(i);
			}
		}
		adjustment--;
		p[0] = fitnesses.getFitness(0) - adjustment;
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + fitnesses.getFitness(i) - adjustment;
		}
		return p;
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
		double[] p = new double[fitnesses.size()];
		double adjustment = fitnesses.getFitness(0);
		for (int i = 1; i < p.length; i++) {
			if (fitnesses.getFitness(i) < adjustment) {
				adjustment = fitnesses.getFitness(i);
			}
		}
		adjustment -= 1.0;
		p[0] = fitnesses.getFitness(0) - adjustment;
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + fitnesses.getFitness(i) - adjustment;
		}
		return p;
	}
}