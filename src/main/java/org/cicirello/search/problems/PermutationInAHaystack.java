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
 
package org.cicirello.search.problems;

import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.PermutationDistanceMeasurer;

/**
 * <p>The Permutation in a Haystack is a family of optimization problems
 * that can be parameterized to the various types of permutation problem 
 * (e.g., absolute versus relative positioning).  The Permutation in a 
 * Haystack problem uses permutation distance metrics to specify search landscape topology,
 * providing an easy means of studying the behavior of search operators on a wide 
 * variety of permutation landscapes.</p>
 *
 * <p>The Permutation in a Haystack Problem, Haystack(&delta;, n), is defined as follows.
 * Find the permutation p such that, p = argmin<sub>p'</sub> &delta;(p', p<sub>n</sub>), 
 * where p<sub>n</sub> = [0, 1, ..., (n-1)].  The p<sub>n</sub> is called the target permutation,
 * and is just the permutation of the first n non-negative integers in increasing order.
 * The p<sub>n</sub> is our figurative needle for which we are searching our haystack.
 * It is also the optimal solution to the problem, so in this way the search problem has a
 * known optimal.  The &delta; is a measure of the distance for permutations.  There are
 * many measures of permutation distance available in the literature.  Some focus on
 * exact positions of elements in the permutation, others focus on relative ordering of
 * permutation elements, others focus on element precedences, etc.  In this way, the choice
 * of &delta; enables you to control the search space topology.  The
 * {@link org.cicirello.permutations.distance} package includes implementations of many 
 * permutation distance measures.  The class includes a constructor that uses the target
 * permutation as defined above, as well as an additional constructor that enables you
 * to specify a different target.</p>
 *
 * <p>The Permutation in a Haystack Problem was introduced in the following paper:<br> 
 * V.A. Cicirello, <a href="https://www.cicirello.org/publications/cicirello2016evc.html" target=_top>"The 
 * Permutation in a Haystack Problem and the Calculus of Search Landscapes,"</a> 
 * IEEE Transactions on Evolutionary Computation, 20(3):434-446, June 2016.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class PermutationInAHaystack implements IntegerCostOptimizationProblem<Permutation> {
	
	private final PermutationDistanceMeasurer distance;
	private final Permutation target;
	
	/**
	 * Constructs an instance of the Permutation in a Haystack problem, for a given distance
	 * measure.  The target permutation, the figurative
	 * needle for which we are searching the haystack, is set to the following 
	 * permutation: [0, 1, ..., (n-1)].  That is, the known optimal solution to the
	 * problem is just the permutation of the first n integers in increasing order.
	 *
	 * @param distance A permutation distance measure,
	 * @param n The length of the target permutation.
	 */
	public PermutationInAHaystack(PermutationDistanceMeasurer distance, int n) {
		this.distance = distance;
		int[] p = new int[n];
		for (int i = 0; i < n; i++) p[i] = i;
		target = new Permutation(p);
	}
	
	/**
	 * Constructs an instance of the Permutation in a Haystack problem, for a given distance
	 * measure, and given target permutation.
	 *
	 * @param distance A permutation distance measure,
	 * @param target The target permutation, such that the problem is to find the
	 * permutation with minimum distance to the target.  That is, target is our figurative
	 * needle for which we are searching the haystack.  It is the known optimal solution to 
	 * the problem.
	 */
	public PermutationInAHaystack(PermutationDistanceMeasurer distance, Permutation target) {
		this.distance = distance;
		this.target = new Permutation(target);
	}
	
	@Override
	public int cost(Permutation candidate) {
		return distance.distance(candidate, target);
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	@Override
	public int value(Permutation candidate) {
		return distance.distance(candidate, target);
	}
	
	@Override
	public boolean isMinCost(int cost) {
		return cost == 0;
	}
}