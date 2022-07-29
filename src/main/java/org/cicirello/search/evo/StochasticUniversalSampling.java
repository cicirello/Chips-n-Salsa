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

import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements Stochastic Universal Sampling (SUS), a selection operator 
 * for evolutionary algorithms. In SUS, similarly to fitness proportional selection,
 * a member of the population is chosen randomly with probability proportional to its
 * fitness relative to the total fitness of the population. For example, if the 
 * fitness of population member i is f<sub>i</sub>, then the probability of selecting
 * population member i is: f<sub>i</sub> / &sum;<sub>j</sub> f<sub>j</sub>, for j &isin;
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
 * <p><b>This selection operator requires positive fitness values. Behavior is undefined if any 
 * fitness values are less than or equal to 0.</b> If your fitness values may be negative,
 * you can use {@link FitnessShifter}, which transforms fitness values such that minimum fitness
 * equals 1.</p>
 *
 * <p>The runtime to select M population members from a population of size N is
 * O(N + M), which includes the need to generate only a single random double, and O(M) random ints.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class StochasticUniversalSampling extends AbstractWeightedSelection {
	
	/**
	 * Construct an SUS operator.
	 */
	public StochasticUniversalSampling() {}
	
	@Override
	public StochasticUniversalSampling split() {
		// Since this selection operator maintains no mutable state, it is
		// safe for multiple threads to share a single instance, so just return this.
		return this;
	}
	
	@Override
	final void selectAll(double[] normalizedWeights, int[] selected) {
		double increment = 1.0 / selected.length;
		double pointer = ThreadLocalRandom.current().nextDouble(increment);		
		int j = selected[0] = selectOne(normalizedWeights, 0, normalizedWeights.length-1, pointer);
		for (int i = 1; i < selected.length; i++) {
			pointer += increment;
			while (normalizedWeights[j] <= pointer) {
				j++;
			}
			selected[i] = j;
		}
		randomize(selected);
	}
	
	private void randomize(int[] selected) {
		for (int i = selected.length-1; i > 0; i--) {
			int j = RandomIndexer.nextInt(i+1);
			if (i != j) {
				int temp = selected[i];
				selected[i] = selected[j];
				selected[j] = temp;
			}
		}
	}
}
