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
 
package org.cicirello.search.problems;

import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;

/**
 * Base interface for all interfaces defining types of problems supported
 * by the library. 
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface Problem<T extends Copyable<T>> {
	
	/**
	 * Computes the cost of a candidate solution to the problem instance.
	 * The lower the cost, the more optimal the candidate solution. 
	 * 
	 * @param candidate The candidate solution to evaluate.
	 * @return A SolutionCostPair object containing the candidate solution
	 * and the cost of that candidate solution.  Lower cost means better solution.
	 */
	SolutionCostPair<T> getSolutionCostPair(T candidate);
	
	/**
	 * Computes the cost of a candidate solution to the problem instance.
	 * The lower the cost, the more optimal the candidate solution. Note that
	 * subinterfaces provide methods for computing the cost as more specific
	 * types (e.g., as an int).
	 * 
	 * @param candidate The candidate solution to evaluate.
	 * @return The cost of the candidate solution as a value of type double. 
	 * Lower cost means better solution.
	 */
	double costAsDouble(T candidate);
}
