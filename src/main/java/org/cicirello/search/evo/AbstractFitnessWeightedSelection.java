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
 * <p>This abstract class serves as a base class for selection operators that
 * select population members randomly but weighted by either their fitness directly
 * or a function of their fitness.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractFitnessWeightedSelection implements SelectionOperator {
	
	/**
	 * Construct a selection operator, which weights population members
	 * by their fitness or a function of their fitness.
	 */
	public AbstractFitnessWeightedSelection() {}
	
	@Override
	public final void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
		selectAll(normalizeWeights(computeWeightRunningSum(fitnesses)), selected);
	}
	
	@Override
	public final void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
		selectAll(normalizeWeights(computeWeightRunningSum(fitnesses)), selected);
	}
	
	/*
	 * package private to enable subclasses in same package to override
	 */
	double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
		double[] p = new double[fitnesses.size()];
		p[0] = fitnesses.getFitness(0);
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + fitnesses.getFitness(i);
		}
		return p;
	}
	
	/*
	 * package private to enable subclasses in same package to override
	 */
	double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
		double[] p = new double[fitnesses.size()];
		p[0] = fitnesses.getFitness(0);
		for (int i = 1; i < p.length; i++) {
			p[i] = p[i-1] + fitnesses.getFitness(i);
		}
		return p;
	}
	
	/*
	 * package private for use by subclasses in same package.
	 */
	final int selectOne(double[] normalizedWeights, int first, int last, double u) {
		if (last <= first) {
			return first;
		}
		int mid = (first + last) >> 1;
		if (u < normalizedWeights[mid]) {
			return selectOne(normalizedWeights, first, mid, u);
		} else {
			return selectOne(normalizedWeights, mid+1, last, u);
		}
	}
	
	/*
	 * package private to enable subclasses in same package to override
	 */
	abstract void selectAll(double[] normalizedWeights, int[] selected);
	
	private double[] normalizeWeights(double[] weights) {
		double total = weights[weights.length-1];
		weights[weights.length-1] = 1.0;
		for (int i = weights.length-2; i >= 0; i--) {
			weights[i] /= total;
		}
		return weights;
	}
}
