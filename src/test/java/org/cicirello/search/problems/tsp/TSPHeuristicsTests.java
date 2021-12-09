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

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.Partial;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import java.util.SplittableRandom;

/**
 * JUnit tests for TSP constructive heuristics.
 */
public class TSPHeuristicsTests {
	
	@Test
	public void testNearestCity() {
		TSP.Double tsp = new TSP.Double(5, 10.0, 42); 
		NearestCityHeuristic h = new NearestCityHeuristic(tsp);
		assertTrue(tsp == h.getProblem());
		assertEquals(5, h.completeLength());
		Partial<Permutation> partial = h.createPartial(5);
		assertFalse(partial.isComplete());
		assertEquals(5, partial.numExtensions());
		assertEquals(0, partial.size());
	}
	
	@Test
	public void testNearestCity_heuristicValues() {
		double[][] weights = {
			{0, 1, 2, 3, 4}, 
			{1, 0, 5, 6, 7}, 
			{2, 5, 0, 8, 9}, 
			{3, 6, 8, 0, 10}, 
			{4, 7, 9, 10, 0}
		};
		TSPSubClassExplicitWeights tsp = new TSPSubClassExplicitWeights(weights);
		NearestCityHeuristic h = new NearestCityHeuristic(tsp);
		assertTrue(tsp == h.getProblem());
		assertEquals(5, h.completeLength());
		Partial<Permutation> partial = h.createPartial(5);
		assertFalse(partial.isComplete());
		assertEquals(5, partial.numExtensions());
		assertEquals(0, partial.size());
		for (int i = 0; i < 5; i++) {
			assertEquals(1.0, h.h(partial, i, null), 1E-10);
		}
		partial.extend(2);
		boolean[] stillAvailable = {true, true, false, true, true};
		double[] expected = {1.0/3.0, 1.0/6.0, 0, 1.0/9.0, 1.0/10.0};
		for (int i = 0; i < 4; i++) {
			int j = partial.getExtension(i);
			assertTrue(stillAvailable[j]);
			assertEquals(expected[j], h.h(partial, j, null), 1E-10);
		}
		int k = partial.getExtension(2);
		partial.extend(2);
		stillAvailable[k] = false;
		for (int i = 0; i < 5; i++) {
			if (stillAvailable[i]) {
				expected[i] = 1.0/(1.0+weights[k][i]);
			} else {
				expected[i] = 0;
			}
		}
		for (int i = 0; i < 3; i++) {
			int j = partial.getExtension(i);
			assertTrue(stillAvailable[j]);
			assertEquals(expected[j], h.h(partial, j, null), 1E-10);
		}
	}
	
	private static class TSPSubClassExplicitWeights extends TSP implements OptimizationProblem<Permutation> {
			
		private final double[][] edgeWeights;
		
		public TSPSubClassExplicitWeights(double[][] edgeWeights) {
			// note that this test ignores this and overrides behavior
			super(edgeWeights.length, 100, new SplittableRandom(42));
			this.edgeWeights = edgeWeights;
		}
		
		@Override
		public double edgeCostForHeuristics(int i, int j) {
			return edgeWeights[i][j];
		}
		
		@Override
		public SolutionCostPair<Permutation> getSolutionCostPair(Permutation p) {
			return new SolutionCostPair<Permutation>(p, cost(p), false);
		}
		
		@Override
		public double cost(Permutation c) {
			double cost = edgeWeights[c.get(c.length()-1)][c.get(0)];
			for (int i = 1; i < c.length(); i++) {
				cost += edgeWeights[c.get(i-1)][c.get(i)];
			}
			return cost;
		}
		
		@Override
		public double value(Permutation c) {
			return cost(c);
		}
	}
}