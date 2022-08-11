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
import org.cicirello.util.IntegerList;


public final class OrderCrossoverTwo implements CrossoverOperator<Permutation> {
	
	private final double u;
	
	/**
	 * Constructs Syswerda's order crossover operator, often referred to as OX2. Uses a default U=0.5.
	 */
	public OrderCrossoverTwo() {
		this(0.5);
	}
	
	/**
	 * Constructs Syswerda's order crossover operator, often referred to as OX2.
	 *
	 * @param u The probability of selecting an index.
	 *
	 * @throws IllegalArgumentException if u is less than or equal to 0.0, or if u is greater than
	 * or equal to 1.0.
	 */
	public OrderCrossoverTwo(double u) {
		if (u <= 0 || u >= 1.0) throw new IllegalArgumentException("u must be: 0.0 < u < 1.0");
		this.u = u;
	}
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		c1.apply( 
			(raw1, raw2, p1, p2) -> internalCross(raw1, raw2, p1, p2, RandomIndexer.arrayMask(raw1.length, u)),
			c2
		);
	}
	
	@Override
	public OrderCrossoverTwo split() {
		// doesn't maintain any mutable state, so safe to return this
		return this;
	}
	
	/*
	 * package private to facilitate testing
	 */
	final void internalCross(int[] raw1, int[] raw2, Permutation p1, Permutation p2, boolean[] mask) {
		int[] inv1 = p1.getInverse();
		int[] inv2 = p2.getInverse();
		IntegerList elementOrder1 = new IntegerList(raw1.length);
		IntegerList elementOrder2 = new IntegerList(raw1.length);
		boolean[] indexes1 = new boolean[raw1.length];
		boolean[] indexes2 = new boolean[raw1.length];
		for (int i = 0; i < mask.length; i++) {
			if (mask[i]) {
				elementOrder1.add(raw2[i]);
				elementOrder2.add(raw1[i]);
				indexes1[inv1[raw2[i]]] = true;
				indexes2[inv2[raw1[i]]] = true;
			}
		}
		int j = 0;
		int k = 0;
		for (int i = 0; i < indexes1.length; i++) {
			if (indexes1[i]) {
				raw1[i] = elementOrder1.get(j);
				j++;
			}
			if (indexes2[i]) {
				raw2[i] = elementOrder2.get(k);
				k++;
			}
		}
	}
}
