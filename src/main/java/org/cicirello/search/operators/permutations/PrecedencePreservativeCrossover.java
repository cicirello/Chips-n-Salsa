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

/**
 * <p>Implementation of Precedence Preservative Crossover (PPX), the two-point version. The paper 
 * by Bierwirth et al, which introduced PPX, described two versions of the operator, including the
 * two-point version that is implemented by this class, and a uniform version, implemented in the
 * {@link UniformPrecedencePreservativeCrossover} class. They referred to both
 * simply as PPX in that paper, but these are essentially two very similar, closely related crossover
 * operators.</p>
 *
 * <p>The paper that originally described PPX described it as producing one child from the cross of two
 * parents. However, our implementation generalizes this in the obvious way to producing two children from
 * two parents. In the two-point version of PPX, a pair of cross points are chosen randomly, similar to a two-point
 * bit-string crossover. The cross points are used in a rather different manner than other operators with
 * cross points. Let's say that the two cross points are i and j, and that i is the lower of the two cross 
 * indexes. Child c1 gets everything to the left of i from parent p1, and likewise child c2 gets everything to the
 * left of i from parent p2. Now let k = |i-j| + 1, be the size of the region defined by indexes i and j.
 * Child c1 gets its next k elements from parent p2, specifically the first k elements (left-to-right) from
 * parent p2 that are not yet in child c1. Likewise, child c2 gets its next k elements from parent p1, specifically 
 * the first k elements (left-to-right) from parent p1 that are not yet in child c2. The remaining elements of
 * child c1 come from parent p1 in the order they appear in p1; and the remaining elements of child c2 come from
 * parent p2 in the order they appear in p2.</p>
 *
 * <p>Consider this example with parent p1 = [7, 6, 5, 4, 3, 2, 1, 0] and parent p2 = [0, 1, 2, 3, 4, 5, 6, 7].
 * Now consider that the random i and j are 3 and 5, which means k = 3. Child c1 gets its first i=3 elements 
 * from p1, for example c1 = [7, 6, 5], and likewise c2 begins with the first i=3 elements of p2, such that 
 * c2 = [0, 1, 2]. Child c1 gets its next k=3 elements from p2, the first 3 such elements left to right from p2
 * that are not yet present in c1, which in this case happens to be p2's first 3 elements, leading to
 * c1 = [7, 6, 5, 0, 1, 2]. And in a similar way, c2 is now c2 = [0, 1, 2, 7, 6, 5]. We can now complete c1 taking
 * the remaining elements from p1 that are not yet in c1 in a left-to-right order. The final c1 = [7, 6, 5, 0, 1, 2, 4, 3].
 * Likewise, the final c2 = [0, 1, 2, 7, 6, 5, 3, 4].</p>
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.</p>
 *
 * <p>PPX was introduced in the following paper:<br>
 * Bierwirth, C., Mattfeld, D., and Kopfer, H. On permutation representations for scheduling problems. 
 * <i>Proceedings of the International Conference on Parallel Problem Solving from Nature</i>, 1996, pp. 310-318.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class PrecedencePreservativeCrossover implements CrossoverOperator<Permutation> {
	
	/**
	 * Constructs a precedence preservative crossover (PPX) operator.
	 */
	public PrecedencePreservativeCrossover() { }
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		c1.apply( 
			(raw1, raw2) -> internalCross(raw1, raw2, RandomIndexer.nextInt(raw1.length), RandomIndexer.nextInt(raw1.length)),
			c2
		);
	}
	
	@Override
	public PrecedencePreservativeCrossover split() {
		// doesn't maintain any state, so safe to return this
		return this;
	}
	
	/*
	 * package private to facilitate testing
	 */
	final void internalCross(int[] raw1, int[] raw2, int i, int j) {
		int[] old1 = raw1.clone();
		int[] old2 = raw2.clone();
		boolean[] used1 = new boolean[raw1.length];
		boolean[] used2 = new boolean[raw1.length];
		
		int otherCount = Math.abs(i-j) + 1;
		i = Math.min(i, j);
		
		int k = 0;
		while (k < i) {
			used1[raw1[k]] = true;
			used2[raw2[k]] = true;
			k++;
		}
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
		while (k < raw1.length) {
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
			k++;
		}
	}
}
