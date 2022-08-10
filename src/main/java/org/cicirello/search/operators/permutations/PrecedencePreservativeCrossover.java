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


public final class PrecedencePreservativeCrossover implements CrossoverOperator<Permutation> {
	
	/**
	 * Constructs a precedence preservative crossover (PPX) operator.
	 */
	public PrecedencePreservativeCrossover() { }
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		c1.apply( 
			(raw1, raw2) -> {
				int[] old1 = raw1.clone();
				int[] old2 = raw2.clone();
				boolean[] used1 = new boolean[raw1.length];
				boolean[] used2 = new boolean[raw1.length];
				
				int i = RandomIndexer.nextInt(raw1.length);
				int j = RandomIndexer.nextInt(raw1.length);
				int otherCount = Math.abs(i-j) + 1;
				i = Math.min(i, j);
				
				for (int k = 0; k < i; k++) {
					used1[raw1[k]] = true;
					used2[raw2[k]] = true;
				}
				int k = i;
				int x = i;
				i = j = 0;
				while (otherCount > 0) {
					while (used1[old2[i]]) {
						i++;
					}
					while (used2[old1[j]]) {
						j++;
					}
					used1[raw1[k] = old2[i]] = true;
					used2[raw2[k] = old1[j]] = true;
					k++;
					i++;
					j++;
					otherCount--;
				}
				i = j = x;
				for ( ; k < raw1.length; k++) {
					while (used1[old1[i]]) {
						i++;
					}
					while (used2[old2[j]]) {
						j++;
					}
					used1[raw1[k] = old1[i]] = true;
					used2[raw2[k] = old2[j]] = true;
					i++;
					j++;
				}
			},
			c2
		);
	}
	
	@Override
	public PrecedencePreservativeCrossover split() {
		// doesn't maintain any state, so safe to return this
		return this;
	}
}
