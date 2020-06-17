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
 
package org.cicirello.search.problems;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.ExactMatchDistance;


/**
 * JUnit 4 test cases for the the PermutationInAHaystack problem.
 */
public class PermutationInAHaystackTests {
	
	@Test
	public void testMinCostMethods() {
		ExactMatchDistance d = new ExactMatchDistance();
		for (int n = 0; n < 5; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			assertEquals(0, problem.minCost());
			int zero = 0;
			assertTrue(problem.isMinCost(zero));
			assertFalse(problem.isMinCost(zero-1));
			assertFalse(problem.isMinCost(zero+1));
			
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			problem = new PermutationInAHaystack(d, perm);
			assertEquals(0, problem.minCost());
			assertTrue(problem.isMinCost(zero));
			assertFalse(problem.isMinCost(zero-1));
			assertFalse(problem.isMinCost(zero+1));
		}
	}
	
	@Test
	public void testOptimalCase() {
		ExactMatchDistance d = new ExactMatchDistance();
		for (int n = 0; n < 5; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			assertEquals(0, problem.cost(perm));
			assertEquals(0, problem.value(perm));
			
			problem = new PermutationInAHaystack(d, perm.copy());
			assertEquals(0, problem.cost(perm));
			assertEquals(0, problem.value(perm));
		}
	}
	
	@Test
	public void testLeastOptimalCase() {
		ExactMatchDistance d = new ExactMatchDistance();
		for (int n = 0; n < 5; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 1; i < n; i++) p[i] = i-1;
			if (n>0) p[0]=n-1;
			Permutation perm = new Permutation(p);
			if (n > 1) {
				assertEquals(n, problem.cost(perm));
				assertEquals(n, problem.value(perm));
			} else {
				assertEquals(0, problem.cost(perm));
				assertEquals(0, problem.value(perm));
			}
			
			int[] p0 = new int[n];
			for (int i = 0; i < n; i++) p0[i] = i;
			problem = new PermutationInAHaystack(d, new Permutation(p0));
			if (n > 1) {
				assertEquals(n, problem.cost(perm));
				assertEquals(n, problem.value(perm));
			} else {
				assertEquals(0, problem.cost(perm));
				assertEquals(0, problem.value(perm));
			}
		}
	}
	
	@Test
	public void testOffBy2Case() {
		ExactMatchDistance d = new ExactMatchDistance();
		for (int n = 2; n < 6; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(0,n-1);
			assertEquals(2, problem.cost(perm));
			assertEquals(2, problem.value(perm));
		}
		for (int n = 3; n < 6; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(0,1);
			assertEquals(2, problem.cost(perm));
			assertEquals(2, problem.value(perm));
		}
		for (int n = 3; n < 6; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(n-2,n-1);
			assertEquals(2, problem.cost(perm));
			assertEquals(2, problem.value(perm));
		}
		for (int n = 3; n < 6; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(0,n/2);
			assertEquals(2, problem.cost(perm));
			assertEquals(2, problem.value(perm));
		}
		for (int n = 3; n < 6; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(n-1,n/2);
			assertEquals(2, problem.cost(perm));
			assertEquals(2, problem.value(perm));
		}
	}
	
	@Test
	public void testOffBy3Case() {
		ExactMatchDistance d = new ExactMatchDistance();
		for (int n = 3; n < 7; n++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(d, n);
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			perm.swap(0,n/2);
			perm.swap(n-1,n/2);
			assertEquals(3, problem.cost(perm));
			assertEquals(3, problem.value(perm));
		}
		for (int n = 3; n < 7; n++) {
			int[] p = new int[n];
			for (int i = 0; i < n; i++) p[i] = i;
			Permutation perm = new Permutation(p);
			PermutationInAHaystack problem = new PermutationInAHaystack(d, perm.copy());
			perm.swap(0,n/2);
			perm.swap(n-1,n/2);
			assertEquals(3, problem.cost(perm));
			assertEquals(3, problem.value(perm));
		}
	}
	
}