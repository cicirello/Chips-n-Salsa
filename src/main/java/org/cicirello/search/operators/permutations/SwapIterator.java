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
 * all swaps.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.7.2019 
 */
final class SwapIterator implements MutationIterator {
	
	private boolean rolled;
	private boolean hasMore;
	private final Permutation p;
	private int i;
	private int j;
	private int x;
	private int y;
	
	SwapIterator(Permutation p) {
		this.p = p;
		hasMore = p.length() >= 2;
		// Default inits:
		//    y = x = i = j = 0;
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
		if (i != j) {
			p.swap(i,j);
		}
		j++;
		if (j >= p.length()) {
			i++;
			j = i + 1;
		}
		p.swap(i,j);
		if (i == p.length()-2) hasMore = false;
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
			if (y == 0) {
				if (j > 0) p.swap(i,j);
			} else if (i != x || j != y) {
				p.swap(i,j);
				p.swap(x,y);
			}
		}
	}
}