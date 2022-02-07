/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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
 
package org.cicirello.search.problems.binpack;

import org.cicirello.permutations.Permutation;
import java.util.ArrayList;

/**
 * This class represents a solution to a {@link BinPacking} problem instance.
 * Although you cannot construct instances of it directly (it doesn't have a public
 * constructor), you can use the methods of this class
 * to inspect the solution such as number of bins, and contents of the bins.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BinPackingSolution {
	
	private final ArrayList<Bin> solution;
	
	/*
	 * package-private so only the BinPacking class can directly construct instances.
	 */
	BinPackingSolution(Permutation p, int capacity, int[] items, int lowerBound) {
		solution = new ArrayList<Bin>();
		final int initialLength = items.length / lowerBound;
		for (int i = 0; i < p.length(); i++) {
			int id = p.get(i);
			boolean added = false;
			for (Bin b : solution) {
				if (b.space() >= items[id]) {
					b.addItem(id, items[id]);
					added = true;
					break;
				}
			}
			if (!added) {
				Bin b = new Bin(capacity, initialLength);
				b.addItem(id, items[id]);
				solution.add(b);
			}
		}
	}
	
	/**
	 * Gets the cost of this solution (i.e., number of bins used).
	 * @return solution.size();
	 */
	public final int cost() {
		return solution.size();
	}
	
	/**
	 * Gets one of the bins in this solution.
	 * @param i The id of the bin.
	 * @return The i-th bin in the solution.
	 */
	public final Bin getBin(int i) {
		return solution.get(i);
	}
}
