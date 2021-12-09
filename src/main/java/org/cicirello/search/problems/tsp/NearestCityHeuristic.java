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
 
package org.cicirello.search.problems.tsp;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.ConstructiveHeuristic;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.PartialPermutation;

/**
 * This class implements a nearest city constructive heuristic for the TSP
 * for use by stochastic sampling algorithms. The nearest city heuristic
 * prefers cities that are closest to the city most recently added to the tour.
 * Since the stochastic sampling algorithms of the library require higher heuristic
 * values to imply preferred choice, this heuristic is 
 * implemented as: h(i) == 1.0 / (1.0 + distance(j, i)), where h(i) is the heuristic
 * value for city i, and j is the most recently added city. If no cities have been added yet,
 * the heuristic simply returns 1. 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class NearestCityHeuristic implements ConstructiveHeuristic<Permutation> {
	
	private final TSP problem;
	
	/**
	 * Constructs a nearest city heuristic for an instance of the TSP.
	 * @param problem The TSP instance to solve.
	 */
	public NearestCityHeuristic(TSP problem) {
		this.problem = problem;
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		return p.size() > 0  ? 1.0 / (1.0 + problem.edgeCostForHeuristics(p.getLast(), element)) : 1;
	}
	
	@Override
	public final Problem<Permutation> getProblem() {
		return problem;
	}
	
	@Override
	public final Partial<Permutation> createPartial(int n) {
		return new PartialPermutation(n);
	}
	
	@Override
	public final int completeLength() {
		return problem.length();
	}
}