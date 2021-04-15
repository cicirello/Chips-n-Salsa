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
 
package org.cicirello.search.operators.permutations;

import org.cicirello.search.operators.UndoableMutationOperator;
//import org.cicirello.search.operators.IterableMutationOperator;
//import org.cicirello.search.operators.MutationIterator;
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements the classic two-change operator as a mutation operator 
 * for permutations. The two-change operator originated as a local search operator 
 * for the TSP that removes two edges from a tour of the cities of a TSP and replaces
 * them with two different edges such that the result is a valid tour of the cities.
 * This implementation is not strictly for the TSP, and will operate on a permutation
 * regardless of what that permutation represents. However, it assumes that the 
 * permutation represents a cyclic sequence of edges, and specifically that if two elements
 * are adjacent in the permutation that it corresponds to an undirected edge between the
 * elements. For example, consider the permutation, p = [2, 1, 4, 0, 3], of the first 5 
 * non-negative integers. Now imagine that we have a graph with 5 vertexes, labeled 0 to
 * 4. This example permutation would correspond to a set of undirected edges: 
 * { (2, 1), (1, 4), (4, 0), (0, 3), (3, 2) }. Notice that we included (3, 2) here in that
 * the set of edges represented by the permutation is cyclic and includes an edge between
 * the two endpoints.  The classic two-change removes two edges and replaces them with two 
 * different edges that reconnect a valid traversal of all elements. One way of implementing
 * the equivalent of this as an operator on permutations is to reverse a subsequence.
 * For example, consider reversing the first 3 elements, which gives you: p = [4, 1, 2, 0, 3],
 * which under the edge interpretation corresponds to the edges: 
 * { (4, 1), (1, 2), (2, 0), (0, 3), (3, 4) }. Remember that we are interpreting the edges
 * as undirected edges, and that there are exactly two that have changed: (4, 0) and (3, 2)
 * were removed, and replaced by (2, 0) and (3, 4).</p> 
 * 
 * <p>Technically, if you wanted to use the approximate equivalent of a two change operator,
 * you could use the {@link ReversalMutation} class. The reason is that every two change
 * is equivalent to a reversal. However, not every reversal is equivalent to a two change.
 * If you reverse the entire permutation, you don't actually change any edges, if the permutation
 * represents a cyclic set of undirected edges. Likewise, if you reverse an n-1 length sequence
 * of elements, you also don't change any edges. This TwoChangeMutation class implements a
 * two change operator that always produces a mutant that is the equivalent to changing
 * two edges (if that is what the permutation represents). For example, it won't reverse
 * the entire permutation, and also won't reverse a subpermutation of length n-1. Additionally,
 * simply viewing reversals as two changes leads to redundancy (e.g., reversing the
 * first k elements changes the same two edges as reversing the last n-k elements), so if
 * your aim is to perform two changes, and if you want all possible two changes to be
 * equally likely when generating a random mutant, then the {@link ReversalMutation} class
 * will not give you that since it will be biased in favor of the two changes that have
 * multiple equivalent reversals. This TwoChangeMutation class is implemented such that
 * all two changes of a permutation are approximately equally likely.</p>
 *
 * <p>Also note that this TwoChangeMutation doesn't guarantee implementation by reversals.
 * It only guarantees that every mutation is equivalent to a two change (under the interpretation
 * of a permutation representing a cyclic set of edges), and that all possible such
 * two changes are equally likely. Under this assumption some two changes can be generated
 * faster than by a reversal.</p>
 *
 * <p>The runtime (worst case and average case) of both
 * the {@link #mutate(Permutation) mutate} and {@link #undo(Permutation) undo} methods is O(n),
 * where n is the length of the permutation. During a single call to one of these methods,
 * at most n/2 elements will change locations. Thus, this is roughly twice as fast as 
 * the {@link ReversalMutation} class, which can move as many as n elements during a single
 * call to these methods. If the set of edges interpretation applies to your problem, then
 * the TwoChangeMutation may be a better choice than ReversalMutation. At the very least it
 * will compute mutants faster. If that interpretation doesn't apply to your problem, then
 * we can't really say (at least not in a problem independent way) which might be better.</p>
 *
 * <p>For any given permutation of length n, there are n*(n-3)/2 possible two-change
 * neighbors. For permutations of length n &lt 4, the TwoChangeMutation operator
 * makes no changes, as there are no two-change neighbors of permutations of that size.</p> 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 4.15.2021
 */
public final class TwoChangeMutation implements UndoableMutationOperator<Permutation> {
	
	// needed to implement undo
	private int a;
	private int b;
	
	/**
	 * Constructs an TwoChangeMutation mutation operator.
	 */
	public TwoChangeMutation() {
	}

	@Override
	public final void mutate(Permutation c) {
		if (c.length() >= 4) {
			internalMutate(c, RandomIndexer.nextInt(c.length()), 1 + RandomIndexer.nextInt(c.length()-3));
		}
	}
	
	@Override
	public final void undo(Permutation c) {
		if (c.length() >= 4) {
			internalMutate(c);
		}
	}
	
	@Override
	public TwoChangeMutation split() {
		return new TwoChangeMutation();
	}
	
	/*
	 * package-private to facilitate unit-testing
	 */
	final void internalMutate(Permutation c, int first, int delta) {
		b = first + delta;
		if (b >= c.length()) {
			a = b - c.length() + 1;
			b = first - 1;				
		} else {
			a = first;
		}
		internalMutate(c);
	}
	
	private void internalMutate(Permutation c) {
		if (b - a < (c.length() >> 1)) {
			c.reverse(a, b);
		} else {
			int rightCount = c.length() - b - 1;
			int i=a-1; 
			int j=b+1;
			if (a < rightCount) {
				for ( ; i >= 0; i--, j++) {
					c.swap(i, j);
				}
				c.reverse(j, c.length()-1);
			} else if (a > rightCount) {
				for ( ; j < c.length(); i--, j++) {
					c.swap(i, j);
				}
				c.reverse(0, i);
			} else {
				for ( ; i >= 0; i--, j++) {
					c.swap(i, j);
				}
			}
		}
	}
}