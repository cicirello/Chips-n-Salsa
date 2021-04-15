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
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements a scramble mutation on permutations, where one mutation
 * consists in randomizing the order of a randomly selected subpermutation.  This version of
 * scramble mutation also supports the undo method.
 * The pair of indexes that indicate the subpermutation to scramble is chosen
 * uniformly at random from among all n(n-1)/2 possible pairs of indexes, where n
 * is the length of the permutation.</p>
 * <p>The runtime (worst case and average case) of both
 * the {@link #mutate(Permutation) mutate} and {@link #undo(Permutation) undo} methods is O(n),
 * where n is the length of the permutation.  The worst case 
 * runtime occurs when the random indexes are
 * the two end points.  
 * On average, a scramble mutation moves approximately n/3 elements.</p>
 * <p>If you don't need the {@link #undo(Permutation) undo} method, then it is recommended
 * that you instead use the {@link ScrambleMutation} class instead to avoid the
 * O(n) extra memory required to store the prior permutation state, as well as the time
 * associated with copying that state prior to mutation.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 4.13.2021
 */
public class UndoableScrambleMutation extends Permutation.Mechanic implements UndoableMutationOperator<Permutation> {

	private int[] last;
	private Permutation previous;
	private final int[] indexes;
	
	/**
	 * Constructs an UndoableScrambleMutation mutation operator.
	 */
	public UndoableScrambleMutation() { 
		indexes = new int[2];
	}
	
	@Override
	public final void mutate(Permutation c) {
		if (c.length() >= 2) {
			previous = c;
			last = c.toArray();
			generateIndexes(c.length(), indexes);
			c.scramble(indexes[0], indexes[1]);
		}
	}
	
	@Override
	public final void undo(Permutation c) {
		// Verify that c was the most recently mutated permutation.
		// If so, undo the mutation.
		if (previous == c) {
			if (indexes[0] < indexes[1]) {
				set(c, last, indexes[0], indexes[0], indexes[1]-indexes[0]+1);
			} else {
				set(c, last, indexes[1], indexes[1], indexes[0]-indexes[1]+1);
			}			
		} 
	}
	
	@Override
	public UndoableScrambleMutation split() {
		return new UndoableScrambleMutation();
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