/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements a swap mutation on permutations, where one mutation
 * selects two elements uniformly at random and swaps their locations.
 * The pair of indexes for the chosen elements are chosen
 * uniformly at random from among all n(n-1)/2 possible pairs of indexes, where n
 * is the length of the permutation.</p>
 * <p>The runtime of both
 * the {@link #mutate(Permutation) mutate} and {@link #undo(Permutation) undo} methods is O(1)
 * since only a constant number of assignments are necessary to execute a swap.</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.7.2019
 */
public class SwapMutation implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

	// needed to implement undo
	private final int[] indexes;
	
	/**
	 * Constructs an SwapMutation mutation operator.
	 */
	public SwapMutation() { 
		indexes = new int[2]; 
	}
	
	@Override
	public final void mutate(Permutation c) {
		if (c.length() >= 2) {
			generateIndexes(c.length(), indexes);
			c.swap(indexes[0], indexes[1]);
		}
	}
	
	@Override
	public final void undo(Permutation c) {
		if (c.length() >= 2) c.swap(indexes[0], indexes[1]);
	}
	
	@Override
	public SwapMutation split() {
		return new SwapMutation();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The worst case runtime of the {@link MutationIterator#hasNext},
	 * {@link MutationIterator#nextMutant}, {@link MutationIterator#setSavepoint}, and
	 * {@link MutationIterator#rollback} methods of the {@link MutationIterator} created by this method
	 * is O(1).</p>
	 */
	@Override
	public MutationIterator iterator(Permutation p) {
		return new SwapIterator(p);
	}
	
	/*
	 * This package access method allows the window limited version
	 * implemented as a subclass to change how indexes are generated
	 * without modifying the mutate method.
	 */
	void generateIndexes(int n, int[] indexes) {
		RandomIndexer.nextIntPair(n, indexes);
	}
}