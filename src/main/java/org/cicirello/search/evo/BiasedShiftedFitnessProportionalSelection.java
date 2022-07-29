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
 * <p>This class implements a variation of fitness proportional selection that 
 * uses shifted fitness values and applies
 * a bias function to further transform the shifted fitness values. 
 * Specifically, first it shifts all fitness values by the minimum
 * fitness minus one, such that the least fit population member's selection probability
 * is based on a transformed fitness equal to 1.
 * Next, a member of the population is chosen randomly with probability proportional to a bias function of this
 * shifted fitness relative to the total of such biased shifted fitness of the population. For example, if the 
 * fitness of population member i is f<sub>i</sub>, and if the minimum fitness in the population
 * is f<sub>min</sub>, then the probability of selecting
 * population member i is: 
 * (bias(f<sub>i</sub>) - f<sub>min</sub> + 1) / &sum;<sub>j</sub> (bias(f<sub>j</sub>) - f<sub>min</sub> + 1), for j &isin;
 * { 1, 2, ..., N }, where N is the population size, and bias is a bias function. To select M members of the population,
 * M independent random decisions are executed in this way, thus requiring generating M
 * random numbers of type double.</p>
 *
 * <p>As an example bias function, consider: bias(x) = x<sup>2</sup>, which would square each fitness
 * value x.</p>
 *
 * <p>This selection operator is compatible with all fitness functions, even in the case of
 * negative fitness values.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N + M lg N), assuming the bias function has a constant runtime.</p>
 *
 * @deprecated Instead of this class, you should use a combination of {@link FitnessShifter} and 
 * {@link BiasedFitnessProportionalSelection}. This class is scheduled for removal in release 6.0.0.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@Deprecated
public final class BiasedShiftedFitnessProportionalSelection extends FitnessProportionalSelection {
	
	private final FitnessBiasFunction bias;
	
	/**
	 * Construct a biased shifted fitness proportional selection operator.
	 * @param bias A bias function
	 */
	public BiasedShiftedFitnessProportionalSelection(FitnessBiasFunction bias) {
		this.bias = bias;
	}
	
	@Override
	public BiasedShiftedFitnessProportionalSelection split() {
		// The FitnessBiasFunction interface's contract is that implementations
		// must be threadsafe and thread efficient, so assume that the only state,
		// the bias function meets that contract.
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
		p[0] = bias.bias(fitnesses.getFitness(0) - adjustment);
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + bias.bias(fitnesses.getFitness(i) - adjustment);
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
		p[0] = bias.bias(fitnesses.getFitness(0) - adjustment);
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + bias.bias(fitnesses.getFitness(i) - adjustment);
		}
		return p;
	}
}
