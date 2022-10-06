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
import org.cicirello.math.rand.RandomSampler;
import org.cicirello.util.IntegerList;

/**
 * <p>Implementation of uniform order-based crossover (UOBX). UOBX is controlled by a parameter U,
 * which is the probability that an index is a fixed-point for children relative to parents (i.e.,
 * that the children get the elements at those positions from the parents). Child c1
 * gets the absolute positions of U*N elements on average from parent p1, where N is the permutation
 * length, and c1 gets the relative positions of the remaining elements from parent p2. Likewise, child
 * c2 gets the absolute positions of elements at those same chosen indexes from p2, with the relative 
 * positions of the others coming from p1.</p>
 *
 * <p>Consider an example to illustrate. Let parent p1 = [3, 0, 6, 2, 5, 1, 4, 7] and parent
 * p2 = [7, 6, 5, 4, 3, 2, 1, 0]. Consider U=0.5, and imagine in this hypothetical scenario that
 * exactly half of the indexes were chosen, and that those 4 indexes are: 0, 3, 4, 6. Child c1 will
 * get the elements at those indexes from p1, thus c1 begins with [3, x, x, 2, 5, x, 4, x]. The missing
 * elements, 0, 1, 6, 7 will get their relative ordering from p2, and thus will be ordered as: 7, 6, 1, 0.
 * In that order, they will fill into the open spots to derive: c1 = [3, 7, 6, 2, 5, 1, 4, 0]. Likewise,
 * c2 will get the elements from indexes 0, 3, 4, and 6 from p2 to initialize as [7, x, x, 4, 3, x, 1, x].
 * Its missing elements, 0, 2, 5, and 6 will get relative order from p1, and thus will be ordered
 * 0, 6, 2, 5 to derive c2 = [7, 0, 6, 4, 3, 2, 1, 5].</p>
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.</p>
 *
 * <p>UOBX was introduced in the following paper:<br>
 * Syswerda, G. Schedule Optimization using Genetic Algorithms. <i>Handbook of Genetic Algorithms</i>, 1991.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UniformOrderBasedCrossover implements CrossoverOperator<Permutation> {
	
	private final double u;
	
	/**
	 * Constructs a uniform order-based crossover (UOBX) operator, with a default U=0.5.
	 */
	public UniformOrderBasedCrossover() {
		this(0.5);
	}
	
	/**
	 * Constructs a uniform order-based crossover (UOBX) operator.
	 *
	 * @param u The probability of an index being among the fixed-point positions.
	 *
	 * @throws IllegalArgumentException if u is less than or equal to 0.0, or if u is greater than
	 * or equal to 1.0.
	 */
	public UniformOrderBasedCrossover(double u) {
		if (u <= 0 || u >= 1.0) throw new IllegalArgumentException("u must be: 0.0 < u < 1.0");
		this.u = u;
	}
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		c1.apply( 
			(raw1, raw2) -> {
				int[] indexes = RandomSampler.sample(raw1.length, u);
				boolean[] mask = new boolean[raw1.length];
				boolean[] in1 = new boolean[raw1.length];
				boolean[] in2 = new boolean[raw1.length];
				for (int k : indexes) {
					mask[k] = true;
					in1[raw1[k]] = true;
					in2[raw2[k]] = true;
				}
				final int orderedCount = raw1.length - indexes.length;
				if (orderedCount > 0) {
					IntegerList list1 = new IntegerList(orderedCount);
					IntegerList list2 = new IntegerList(orderedCount);
					for (int k = 0; k < raw1.length; k++) {
						if (!in2[raw1[k]]) {
							list1.add(raw1[k]);
						}
						if (!in1[raw2[k]]) {
							list2.add(raw2[k]);
						}
					}
					int w = 0;
					for (int k = 0; k < mask.length; k++) {
						if (!mask[k]) {
							raw1[k] = list2.get(w);
							raw2[k] = list1.get(w);
							w++;
						}
					}
				}
			},
			c2
		);
	}
	
	@Override
	public UniformOrderBasedCrossover split() {
		// doesn't maintain any mutable state, so safe to return this
		return this;
	}
}
