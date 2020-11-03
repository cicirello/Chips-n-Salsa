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

import org.cicirello.search.operators.MutationIterator;
import org.cicirello.permutations.Permutation;

/**
 * Internal (package-private) class implementing an iterator over
 * all block moves.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.9.2019 
 */
final class BlockMoveIterator implements MutationIterator {
	
	private boolean rolled;
	private boolean hasMore;
	private final Permutation p;
	private int i;
	private int j;
	private int s;
	private int x;
	private int y;
	private int z;
	private final int MAX_S;
	private int nextS;
	
	BlockMoveIterator(Permutation p) {
		this.p = p;
		hasMore = p.length() >= 2;
		// Default inits:
		//    x = y = z = 0;
		//    rolled = false;
		nextS = s = 1;
		MAX_S = p.length() >> 1;
	}
	
	@Override
	public boolean hasNext() {
		return hasMore && !rolled;
	}
	
	@Override
	public void nextMutant() {
		if (!hasMore) throw new IllegalStateException("no neighbors left");
		if (rolled) throw new IllegalStateException("illegal to call nextMutant after calling rollback");
		if (nextS==1) {
			nextInsertion();
		} else {	
			nextBlockInsertion();
		}
	}
	
	@Override
	public void setSavepoint() {
		x = i;
		y = j;
		z = s;
	}
	
	@Override
	public void rollback() {
		if (!rolled) {
			rolled = true;
			if (z > 0) {
				if (i != x || j != y || s != z) {
					p.removeAndInsert(i, s, j);
					p.removeAndInsert(y, z, x);
				}
			} else {
				p.removeAndInsert(i, s, j);
			}
		}
	}
	
	/*
	 * Notes for the pair of private helper methods below: nextInsertion and nextBlockInsertion.
	 *
	 * (1) Rather complex logic, but for a purpose.
	 * (2) Although there are simpler ways to iterate over all block moves, this pair of
	 *     methods is designed to iterate over the block moves in a rather specific order
	 *     so that the nextMutant method's amortized runtime is O(1).
	 * (3) That order is as follows: First iterates over all block moves that remove
	 *     a block of size 1, and moves it at least 1 position (specifically, for each such removed
	 *     block, it considers inserting it 1 position away, then 2, etc).  Second, it then
	 *     iterates over all block moves where the removed block is size 2, and the insertion point
	 *     is at least 2 spots away.  It then continues in this manner with removed block size 3, etc.
	 * (4) E.g., consider p = [0, 1, 2, 3, 4].
	 *     Considers neighbors in this order:
	 *     (a) Removed block size 1: [1, 0, 2, 3, 4], [2, 0, 1, 3, 4], [3, 0, 1, 2, 4], [4, 0, 1, 2, 3], 
	 *         [0, 2, 1, 3, 4], [0, 3, 1, 2, 4], [0, 4, 1, 2, 3], [0, 1, 3, 2, 4], [0, 1, 4, 2, 3], 
	 *         [0, 1, 2, 4, 3], [1, 2, 0, 3, 4], [0, 2, 3, 1, 4], [1, 2, 3, 0, 4]
	 *         [0, 1, 3, 4, 2], [0, 2, 3, 4, 1], [1, 2, 3, 4, 0]
	 *     (b) Removed block size 2: [0, 3, 4, 1, 2], [3, 4, 0, 1, 2], [2, 3, 0, 1, 4], [2, 3, 4, 0, 1]
	 *     (c) This example is done... no need to go to block size 3... just repeat up to block size n/2	 
	 */ 
	 
	private void nextInsertion() {
		if (j >= i) {
			j++;
			if (j >= p.length()) {
				p.removeAndInsert(i, j-1);
				i++;
				if (i >= p.length() - 1) {
					i = 2;
					j = 0;
					p.swap(1, 2);
				} else j = i + 1;
			}
		} else {
			j--;
			if (j < 0) {
				p.removeAndInsert(i, j+1);
				i++;
				j = i - 2;
				p.swap(i,i-1);
			}
		}
		p.swap(i,j);
		if (p.length() <= 2 || j==0 && i==p.length()-1) {
			if (MAX_S==1) hasMore = false;
			else nextS = 2;
		}
	}
	
	private void nextBlockInsertion() {
		if (s != nextS) {
			p.removeAndInsert(i,s,j);
			j = p.length() - nextS;
			i = j - nextS;
			s = nextS;
			p.removeAndInsert(j, s, i);
			if (i == 0) hasMore = false;
		} else {
			if (j > i) {
				if (i > 0) {
					i--;
					p.removeAndInsert(i, i+s);
				} else if (j > s) {
					p.removeAndInsert(i,s,j);
					j--;
					i = j - s;
					p.removeAndInsert(j,s,i);
				} else {
					p.removeAndInsert(i,s,j);
					j = 0;
					i = s + 1;
					p.removeAndInsert(j,s,i);
					if (p.length() == s + s + 1) hasMore = false;
				}
			} else {
				if (i < p.length() - s) {
					p.removeAndInsert(i+s, i);
					i++;
				} else {
					p.removeAndInsert(i,s,j);
					j++;
					i = j + s + 1;
					p.removeAndInsert(j, s, i);
					if (i == p.length()-s) {
						nextS++;
					}
				}
			}
		}
	}
}