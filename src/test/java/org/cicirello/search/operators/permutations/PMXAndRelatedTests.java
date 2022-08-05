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
 
package org.cicirello.search.operators.permutations;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit test cases for PMX and UPMX.
 */
public class PMXAndRelatedTests {
	
	@Test
	public void testInternalPMX() {
		PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
		Permutation p1 = new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0});
		Permutation p2 = new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5});
		int[][] indexes = {
			{4, 4},
			{4, 5},
			{3, 5},
			{3, 6},
			{2, 6},
			{2, 7},
			{1, 7},
			{1, 8},
			{0, 8}
		};
		Permutation[][] expected = {
			{ new Permutation(new int[] {8, 4, 6, 5, 7, 3, 2, 1, 0}), new Permutation(new int[] {0, 1, 2, 6, 4, 8, 3, 7, 5}) },
			{ new Permutation(new int[] {3, 4, 6, 5, 7, 8, 2, 1, 0}), new Permutation(new int[] {0, 1, 2, 6, 4, 3, 8, 7, 5}) },
			{ new Permutation(new int[] {3, 4, 5, 6, 7, 8, 2, 1, 0}), new Permutation(new int[] {0, 1, 2, 5, 4, 3, 8, 7, 6}) },
			{ new Permutation(new int[] {2, 4, 5, 6, 7, 8, 3, 1, 0}), new Permutation(new int[] {0, 1, 8, 5, 4, 3, 2, 7, 6}) },
			{ new Permutation(new int[] {5, 4, 2, 6, 7, 8, 3, 1, 0}), new Permutation(new int[] {0, 1, 6, 5, 4, 3, 2, 7, 8}) },
			{ new Permutation(new int[] {5, 1, 2, 6, 7, 8, 3, 4, 0}), new Permutation(new int[] {0, 7, 6, 5, 4, 3, 2, 1, 8}) },
			{ new Permutation(new int[] {5, 1, 2, 6, 7, 8, 3, 4, 0}), new Permutation(new int[] {0, 7, 6, 5, 4, 3, 2, 1, 8}) },
			{ new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5}), new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0}) },
			{ new Permutation(new int[] {0, 1, 2, 6, 7, 8, 3, 4, 5}), new Permutation(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0}) }
		};
		for (int k = 0; k < indexes.length; k++) {
			int i = indexes[k][0];
			int j = indexes[k][1];
			Permutation child1 = new Permutation(p1);
			Permutation child2 = new Permutation(p2);
			pmx.internalCross(child1, child2, i, j);
			assertEquals(expected[k][0], child1);
			assertEquals(expected[k][1], child2);
			child1 = new Permutation(p1);
			child2 = new Permutation(p2);
			pmx.internalCross(child1, child2, j, i);
			assertEquals(expected[k][0], child1);
			assertEquals(expected[k][1], child2);
		}
	}
	
	@Test
	public void testPMXIdentical() {
		PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
		for (int n = 1; n <= 32; n *= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			Permutation child1 = new Permutation(p1);
			Permutation child2 = new Permutation(p2);
			pmx.cross(child1, child2);
			assertEquals(p1, child1);
			assertEquals(p2, child2);
		}
		assertSame(pmx, pmx.split());
	}
	
	@Test
	public void testPMX() {
		PartiallyMatchedCrossover pmx = new PartiallyMatchedCrossover();
		for (int n = 1; n <= 32; n *= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation child1 = new Permutation(p1);
			Permutation child2 = new Permutation(p2);
			pmx.cross(child1, child2);
			assertTrue(validPermutation(child1));
			assertTrue(validPermutation(child2));
		}
		assertSame(pmx, pmx.split());
	}
	
	private boolean validPermutation(Permutation p) {
		boolean[] foundIt = new boolean[p.length()];
		for (int i = 0; i < p.length(); i++) {
			if (foundIt[p.get(i)]) return false;
			foundIt[p.get(i)] = true;
		}
		return true;
	}
}
