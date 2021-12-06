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
 * <p>This class implements a variation of fitness proportionate selection that applies
 * a bias function to transform the fitness values. In this biased fitness proportionate selection,
 * a member of the population is chosen randomly with probability proportional to a bias function of its
 * fitness relative to the total of such biased fitness of the population. For example, if the 
 * fitness of population member i is f<sub>i</sub>, then the probability of selecting
 * population member i is: bias(f<sub>i</sub>) / &sum;<sub>j</sub> bias(f<sub>j</sub>), for j &isin;
 * { 1, 2, ..., N }, where N is the population size, and bias is a bias function. To select M members of the population,
 * M independent random decisions are executed in this way, thus requiring generating M
 * random numbers of type double.</p>
 *
 * <p>As an example bias function, consider: bias(x) = x<sup>2</sup>, which would square the fitness
 * values x.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N + M lg N), assuming the bias function has a constant runtime.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BiasedFitnessProportionateSelection extends FitnessProportionateSelection {
	
	private final FitnessBiasFunction bias;
	
	/**
	 * Construct a biased fitness proportionate selection operator.
	 * @param bias A bias function
	 */
	public BiasedFitnessProportionateSelection(FitnessBiasFunction bias) {
		this.bias = bias;
	}
	
	@Override
	public BiasedFitnessProportionateSelection split() {
		// The FitnessBiasFunction interface's contract is that implementations
		// must be threadsafe and thread efficient, so assume that the only state,
		// the bias function meets that contract.
		return this;
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
		double[] p = new double[fitnesses.size()];
		p[0] = bias.bias(fitnesses.getFitness(0));
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + bias.bias(fitnesses.getFitness(i));
		}
		return p;
	}
	
	@Override
	final double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
		double[] p = new double[fitnesses.size()];
		p[0] = bias.bias(fitnesses.getFitness(0));
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + bias.bias(fitnesses.getFitness(i));
		}
		return p;
	}
}
