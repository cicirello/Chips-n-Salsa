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

import org.cicirello.search.operators.MutationIterator;
import org.cicirello.permutations.Permutation;

/**
 * Internal (package-private) class implementing an iterator over
 * all block interchanges.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021 
 */
final class BlockInterchangeIterator implements MutationIterator {
	
	private boolean rolled;
	private boolean hasMoreInsertions;
	private boolean hasMoreElementSwaps;
	private boolean hasMoreBlocksInserts;
	private boolean hasMoreBlocksSwaps;
	private boolean hasMore;
	private int phase;
	private final Permutation p;
	
	private int h;
	private int i;
	private int j;
	private int k;
	
	private int w;
	private int x;
	private int y;
	private int z;
	private int savePhase;
	
	// used by phase 3
	private int nextS;
	
	BlockInterchangeIterator(Permutation p) {
		this.p = p;
		// phase 1: adjacent blocks and at least 1 block is single element
		hasMoreInsertions = p.length() >= 2;
		// phase 2: both blocks (non-adjacent) single element, which starts
		//    at length 3 because phase 1 covers this for length < 3.
		hasMoreElementSwaps = p.length() >= 3;
		// phase 3: adjacent blocks at least 2 elements each
		hasMoreBlocksInserts = p.length() >= 4;
		// phase 4: non-adjacent at least one block with at least 2 elements
		hasMoreBlocksSwaps = p.length() >= 4;
		// has any more of any phase
		hasMore = hasMoreInsertions;
		phase = 1;
	}
	
	@Override
	public boolean hasNext() {
		return hasMore && !rolled;
	}
	
	@Override
	public void nextMutant() {
		if (rolled) throw new IllegalStateException("illegal to call nextMutant after calling rollback");
		if (hasMoreInsertions) {
			nextInsertion();
		} else if (hasMoreElementSwaps) {
			nextSwap();
		} else if (hasMoreBlocksInserts) {
			nextBlockInsertion();
		} else if(hasMoreBlocksSwaps) {
			nextBlockSwap();
		} else {
			throw new IllegalStateException("no neighbors left");
		}
	}
	
	@Override
	public void setSavepoint() {
		w = h;
		x = i;
		y = j;
		z = k;
		savePhase = phase;
	}
	
	@Override
	public void rollback() {
		if (!rolled) {
			rolled = true;
			switch (phase) {
				case 1: 
					if (i!=j) p.removeAndInsert(i, j); 
					break;
				case 2:
					p.swap(i, j);
					break;
				case 3:
					p.removeAndInsert(i, k-j+1, j);
					break;
				default: // case 4:
					p.swapBlocks(h, h+k-j, k-i+h, k);
					break;
			}
			switch (savePhase) {
				case 1: 
					p.removeAndInsert(y, x); 
					break;
				case 2:
					p.swap(x, y);
					break;
				case 3:
					p.removeAndInsert(y, z-y+1, x);
					break;
				case 4:
					p.swapBlocks(w, x, y, z);
					break;
			}
		}
	}
	
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
		if (p.length() <= 2 || (j==0 && i==p.length()-1)) {
			hasMoreInsertions = false;
			hasMore = hasMoreElementSwaps;
		}
	}
	
	private void nextSwap() {
		if (phase == 1) {
			phase = 2;
			p.removeAndInsert(i, j);
			i = 0;
			j = 2;
		} else {
			p.swap(i, j);
			j++;
			if (j >= p.length()) {
				i++;
				j = i + 2;
			}
		}
		p.swap(i, j);
		if (i == p.length()-3) {
			hasMoreElementSwaps = false;
			hasMore = hasMoreBlocksInserts;
		}
	}
	
	private void nextBlockInsertion() {
		int s = k-j+1;
		if (phase == 2) {
			phase = 3;
			p.swap(i,j);
			nextS = 2;
			j = p.length() - 2;
			k = j + 1;
			i = j - 2;
			p.removeAndInsert(j, 2, i);
			if (i==0) {
				hasMoreBlocksInserts = false;
				hasMore = hasMoreBlocksSwaps;
			}
		} else if (s != nextS) {
			p.removeAndInsert(i,s,j);
			j = p.length() - nextS;
			i = j - nextS;
			s = nextS;
			k = j + s - 1;
			p.removeAndInsert(j, s, i);
			if (i==0) {
				hasMoreBlocksInserts = false;
				hasMore = hasMoreBlocksSwaps;
			}
		} else {
			if (j > i) {
				if (i > 0) {
					i--;
					p.removeAndInsert(i, i+s);
				} else if (j > s) {
					p.removeAndInsert(i,s,j);
					j--;
					k--;
					i = j - s;
					p.removeAndInsert(j,s,i);
				} else {
					p.removeAndInsert(i,s,j);
					j = 0;
					k = s - 1;
					i = s + 1;
					p.removeAndInsert(j,s,i);
					if (p.length() == s + s + 1) {
						hasMoreBlocksInserts = false;
						hasMore = hasMoreBlocksSwaps;
					}
				}
			} else {
				if (i < p.length() - s) {
					p.removeAndInsert(i+s, i);
					i++;
				} else {
					p.removeAndInsert(i,s,j);
					j++;
					k++;
					i = j + s + 1;
					p.removeAndInsert(j, s, i);
					if (i == p.length()-s) {
						nextS++;
					}
				}
			}
		}
	}
	
	private void nextBlockSwap() {
		if (phase == 3) {
			phase = 4;
			p.removeAndInsert(i, k-j+1, j);
			h = 0;
			i = 0;
			j = 2;
			k = 3;
		} else {
			p.swapBlocks(h, h+k-j, k-i+h, k);
			k++;
			if (k >= p.length()) {
				j++;
				if (j > p.length() - 1 || (h==i && j == p.length() - 1)) {
					i++;
					if (i > p.length() - 3) {
						h++;
						i = h;
						j = i + 2;
						k = j + 1;
					} else {
						j = i + 2;
						k = j;
					}
				} else {
					k = h==i ? j+1 : j;
				}
			}
		}
		p.swapBlocks(h, i, j, k);
		if (h == p.length()-4 && h!=i) hasMoreBlocksSwaps = hasMore = false;
	}
}