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
 * <p>This class implements a variation of fitness proportional selection that uses
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
 * { 1, 2, ..., N }, where N is the population size. To select M members of the population,
 * M independent random decisions are executed in this way, thus requiring generating M
 * random numbers of type double.</p>
 *
 * <p>This selection operator is compatible with all fitness functions, even in the case of
 * negative fitness values.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N + M lg N).</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ShiftedFitnessProportionalSelection extends FitnessProportionalSelection {
	
	/**
	 * Construct a shifted fitness proportional selection operator.
	 */
	public ShiftedFitnessProportionalSelection() {
		super();
	}
	
	@Override
	public ShiftedFitnessProportionalSelection split() {
		// Since this selection operator maintains no mutable state, it is
		// safe for multiple threads to share a single instance, so just return this.
		return this;
	}
	
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