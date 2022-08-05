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
 
package org.cicirello.search.operators.permutations;

import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;


public final class PartiallyMatchedCrossover implements CrossoverOperator<Permutation> {
	
	/**
	 * Constructs a partially matched crossover (PMX) operator.
	 */
	public PartiallyMatchedCrossover() { }
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		internalCross(c1, c2, RandomIndexer.nextInt(c1.length()), RandomIndexer.nextInt(c1.length()));
	}
	
	@Override
	public PartiallyMatchedCrossover split() {
		// doesn't maintain any state, so safe to return this
		return this;
	}
	
	/*
	 * package private to facilitate unit testing
	 */
	final void internalCross(Permutation c1, Permutation c2, int i, int j) {
		if (j < i) {
			int temp = i;
			i = j;
			j = temp;
		}
		int[] inv1 = c1.getInverse();
		int[] inv2 = c2.getInverse();
		int[] old1 = c1.get(i,j);
		int[] old2 = c2.get(i,j);
		for (int k = i, h = 0; k <= j; k++, h++) {
			int g = inv1[old2[h]];
			if (k != g) {
				c1.swap(k, g);
				inv1[c1.get(g)] = g;
				inv1[old2[h]] = k;
			}
			g = inv2[old1[h]];
			if (k != g) {
				c2.swap(k, g);
				inv2[c2.get(g)] = g;
				inv2[old1[h]] = k;
			}
		}
	}
}
