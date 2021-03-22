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
 * <p>This class implements a rotation mutation on permutations, where one mutation
 * consists in a random circular rotation of the permutation.  The number of positions
 * to rotate the permutation is uniformly at random from among the n-1 possible rotations,
 * where the
 * length of the permutation is n.  The runtime of both
 * the {@link #mutate(Permutation) mutate} and {@link #undo(Permutation) undo} methods is O(n).</p>
 *
 * <p>Unlike the other mutation operators of the Chips-n-Salsa library, it is
 * not possible to transform one permutation to <b>any</b> other simply via some
 * sequence of mutations. Thus, RotationMutation is unlikely to be effective as the
 * only mutation operator. The intention of the RotationMutation is for it to be used 
 * in combination with other mutation operators.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.22.2021
 */
public final class RotationMutation implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {
	
	private int r;
	
	/**
	 * Constructs an RotationMutation mutation operator.
	 */
	public RotationMutation() {}
	
	@Override
	public void mutate(Permutation c) {
        if (c.length() > 1) c.rotate(r = 1 + RandomIndexer.nextInt(c.length()-1));
	}
	
	@Override
	public void undo(Permutation c) {
		if (c.length() > 1) c.rotate(c.length() - r);
	}
	
	@Override
	public RotationMutation split() {
		return new RotationMutation();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The worst case runtime of the {@link MutationIterator#nextMutant}
	 * and {@link MutationIterator#rollback} methods of the {@link MutationIterator} 
	 * created by this method is O(n). The runtime of the {@link MutationIterator#hasNext} and
	 * {@link MutationIterator#setSavepoint} methods is O(1).</p>
	 */
	@Override
	public MutationIterator iterator(Permutation p) {
		return new RotationIterator(p);
	}
}
