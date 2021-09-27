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
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;

/**
 * This class implements an adjacent swap mutation on permutations, where one mutation
 * consists in randomly swapping a pair of adjacent elements.  The random choice
 * of elements is performed uniformly at random from among all adjacent pairs.  If the
 * length of the permutation is n, there are n-1 such adjacent pairs.  The runtime of both
 * the {@link #mutate(Permutation) mutate} and {@link #undo(Permutation) undo} methods is O(1).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.22.2021
 */
public final class AdjacentSwapMutation implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

	private int index;
	
	/**
	 * Constructs an AdjacentSwapMutation mutation operator.
	 */
	public AdjacentSwapMutation() {}
	
	@Override
	public void mutate(Permutation c) {
        if (c.length() >= 2) c.swap(index = RandomIndexer.nextInt(c.length()-1), index+1);
	}
	
	@Override
	public void undo(Permutation c) {
		if (c.length() >= 2) c.swap(index, index+1);
	}
	
	@Override
	public AdjacentSwapMutation split() {
		return new AdjacentSwapMutation();
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
		return new AdjacentSwapIterator(p);
	}
}