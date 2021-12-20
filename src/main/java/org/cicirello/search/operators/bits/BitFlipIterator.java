/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 
package org.cicirello.search.operators.bits;

import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.representations.BitVector;


/**
 * Internal (package-private) class implementing an iterator over
 * all bit flips up to a specified number of flipped bits.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a> 
 */
final class BitFlipIterator implements MutationIterator {
	
	private final BitVector v;
	private final int maxBits;
	private boolean rolled;
	private boolean hasMore;
	private final int[] indexes;
	private int numBits;
	private int[] save;
	private int numBitsSaved;
	
	BitFlipIterator(BitVector v, int maxBits) {
		this.v = v;
		this.maxBits = maxBits;
		// default init: rolled = false;
		hasMore = v.length() >= 1;
		if (hasMore) {
			indexes = new int[maxBits];
			indexes[0] = -1;
			numBits = 1;
		} else {
			indexes = null;
		}
	}
	
	@Override
	public boolean hasNext() {
		return hasMore && !rolled;
	}
	
	@Override
	public void nextMutant() {
		if (!hasMore) throw new IllegalStateException("no neighbors left");
		if (rolled) throw new IllegalStateException("illegal to call nextMutant after calling rollback");
		int j = numBits-1;
		if (indexes[0] >= 0) {
			// undo previous flip
			v.flip(indexes[j]);
		}
		// compute next mutant
		indexes[j]++;
		while (j > 0 && indexes[j] > v.length() - (numBits - j)) {
			j--;
			// undo previous flip
			v.flip(indexes[j]);
			indexes[j]++;
		}
		if (indexes[0] > v.length() - numBits) {
			numBits++;
			j = 0;
			indexes[0] = 0;
		}
		v.flip(indexes[j]);
		for (int i = j+1; i < numBits; i++) {
			indexes[i] = indexes[i-1] + 1;
			v.flip(indexes[i]);
		}
		// check if more mutants
		if (numBits==maxBits && indexes[0]==v.length()-numBits) hasMore = false;
	}
	
	@Override
	public void setSavepoint() {
		if (indexes[0] >= 0) {
			save = indexes.clone();
			numBitsSaved = numBits;
		}
	}
	
	@Override
	public void rollback() {
		if (!rolled) {
			rolled = true;
			for (int i = 0; i < numBits; i++) {
				v.flip(indexes[i]);
			}
			if (save != null) {
				for (int i = 0; i < numBitsSaved; i++) {
					v.flip(save[i]);
				}
			}
		}
	}

}