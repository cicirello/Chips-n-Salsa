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
 
package org.cicirello.search.problems.tsp;

import org.cicirello.search.problems.Problem;
import org.cicirello.permutations.Permutation;

/**
 * This class serves as an abstract base class for the various
 * classes that implement variations of the Traveling Salesperson
 * Problem provided by the library. See the documentation for the
 * rest of the hierarchy for details of how they differ. The purpose
 * of this abstract base class is primarily to provide the minimum
 * common functionality required by the constructive heuristics for
 * the TSP.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public abstract class BaseTSP implements Problem<Permutation> {
	
	/**
	 * Gets the number of cities in this instance of the TSP.
	 * @return the number of cities in the problem.
	 */
	public abstract int length();
	
	/*
	 * package private to support implementing heuristics in same package.
	 */
	abstract double edgeCostForHeuristics(int i, int j);
}
