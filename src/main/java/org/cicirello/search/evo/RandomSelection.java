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

import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements a simple random selection operator that selects
 * members of the population uniformly at random, independent of fitness
 * values.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(M), which includes generating O(M) random int values.</p> 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class RandomSelection implements SelectionOperator {
	
	/**
	 * Constructs the random selection operator.
	 */
	public RandomSelection() { }
	
	@Override
	public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
		internalSelect(fitnesses, selected);
	}
	
	@Override
	public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
		internalSelect(fitnesses, selected);
	}
	
	@Override
	public RandomSelection split() {
		// Since this selection operator maintains no state, it is
		// safe for multiple threads to share a single instance, so just return this.
		return this;
	}
	
	private void internalSelect(PopulationFitnessVector fitnesses, int[] selected) {
		final int N = fitnesses.size();
		for (int i = 0; i < selected.length; i++) {
			selected[i] = RandomIndexer.nextInt(N);
		}
	}
}
