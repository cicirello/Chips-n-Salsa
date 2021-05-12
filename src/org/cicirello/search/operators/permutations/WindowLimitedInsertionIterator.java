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
 * all window limited removal/reinsertions.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021 
 */
final class WindowLimitedInsertionIterator implements MutationIterator {
	
	private boolean rolled;
	private boolean hasMore;
	private final Permutation p;
	private final int w;
	private int i;
	private int j;
	private int x;
	private int y;
	
	WindowLimitedInsertionIterator(Permutation p, int w) {
		this.p = p;
		this.w = w;
		hasMore = p.length() >= 2;
		// Default inits:
		//    x = y = i = j = 0;
		//    rolled = false;
	}
	
	@Override
	public boolean hasNext() {
		return hasMore && !rolled;
	}
	
	@Override
	public void nextMutant() {
		if (!hasMore) throw new IllegalStateException("no neighbors left");
		if (rolled) throw new IllegalStateException("illegal to call nextMutant after calling rollback");
		if (j >= i) {
			j++;
			if (j >= p.length() || j-i>w) {
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
			if (j < 0 || i-j>w) {
				p.removeAndInsert(i, j+1);
				i++;
				j = i - 2;
				p.swap(i,i-1);
			}
		}
		p.swap(i,j);
		if (p.length() <= 2 || (i==p.length()-1 && (j==0 || i-j==w)) || (w==1 && i==p.length()-2)) hasMore = false;
	}
	
	@Override
	public void setSavepoint() {
		x = i;
		y = j;
	}
	
	@Override
	public void rollback() {
		if (!rolled) {
			rolled = true;
			if (x == y) {
				if (i!=j) p.removeAndInsert(i,j);
			} else if (i != x || j != y) {
				p.removeAndInsert(i,j);
				p.removeAndInsert(y,x);
			}
		}
	}
	
	
}