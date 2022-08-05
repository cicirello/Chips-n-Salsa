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
 *<p>Implementation of partially matched crossover (PMX). Partially matched crossover begins
 * by selecting a random segment, much like a 2-point crossover for bit-strings. PMX then initializes
 * the children as copies of the parents, and proceeds to make a sequence of swaps within child c1
 * as to cause c1 at the end of those swaps to contain the cross region of p2 within its cross region,.
 * And similarly for the other child.</p>
 *
 * <p>Consider as an example parent permutation p1 = [0, 1, 2, 3, 4, 5, 6, 7] and parent
 * permutation p2 = [1, 2, 0, 5, 6, 7, 4, 3]. Now consider that the random cross region begins
 * at index 2 and ends at index 4, inclusive. Child c1 is initialized as a copy of p1, 
 * c1 = [0, 1, 2, 3, 4, 5, 6, 7], and we then swap the 2 with the 0 (the elements at index 2 in the parents)
 * to get c1 = [2, 1, 0, 3, 4, 5, 6, 7]. Next, we swap the 3 with the 5 (the elements at index 3 in the parents)
 * to get c1 = [2, 1, 0, 5, 4, 3, 6, 7]. Finally, we swap the 4 with the 6 (the elements at index 4 in the parents)
 * to end up with c1 = [2, 1, 0, 5, 6, 3, 4, 7]. In a similar way, we initialize c2 as a copy of p2 and proceed with
 * the designated swaps to end up with c2 = [1, 0, 2, 3, 4, 7, 6, 5].</p>
 *
 * <p>PMX was introduced in the following paper:<br>
 * Goldberg, D.E. and Lingle, R. Alleles, Loci, and the Traveling Salesman Problem. <i>Proceedings of the 1st 
 * International Conference on Genetic Algorithms</i>, 1985, pp. 154-159.</p>
 *
 * <p>Although, we actually relied on the seminal book on genetic algorithms by one of PMX's authors David E Goldberg:<br>
 * Goldberg, D.E. <i>Genetic Algorithms in Search, Optimization and Machine Learning</i>, Addison Wesley, 1989.</p>
 *
 * <p>Note that this implementation in Chips-n-Salsa is asymptotically faster than Goldberg and Lingle's algorithmic 
 * description of PMX. In the original PMX description, the indexes of the elements to swap were found with a linear search,
 * and since on average there is a linear number of these, PMX as originally described required O(n<sup>2</sup>) time.
 * However, the implementation here in Chips-n-Salsa computes the inverse of each permutation in linear time, which is then used
 * as a lookup table for the indexes of the elements to swap. Each swap is constant time, and also involves a constant time
 * update to the lookup table of indexes. Thus, this implementation has an average case and worse case runtime O(n), where
 * n is permutation length.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
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
