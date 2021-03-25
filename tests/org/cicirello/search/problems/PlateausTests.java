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
 
package org.cicirello.search.problems;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.BitVector;

/**
 * JUnit 4 test cases for the Plateaus problem.
 */
public class PlateausTests {
	
	private final double EPSILON = 1e-10;
	
	@Test
	public void testPlateausAllOnes() {
		Plateaus problem = new Plateaus();
		assertEquals(0.0, problem.minCost(), 0.0);
		double zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// all ones
		for (int n = 0; n <= 8; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0.0, problem.cost(v), 0.0);
			assertEquals(10.0*n, problem.value(v), 0.0);
		}
		for (int n = 124; n <= 132; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0.0, problem.cost(v), 0.0);
			assertEquals(10.0*n, problem.value(v), 0.0);
		}
	}
	
	@Test
	public void testPlateausAllZeros() {
		Plateaus problem = new Plateaus();
		assertEquals(0.0, problem.minCost(), 0.0);
		double zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// problem not well defined for n < 4, so expected behavior too
		// ill-defined to test for n < 4
		for (int n = 4; n <= 8; n++) {
			BitVector v = new BitVector(n);
			assertEquals("n:"+n, 0.0, problem.value(v), 0.0);
			assertEquals("n:"+n, 10.0*n, problem.cost(v), 0.0);
		}
		for (int n = 124; n <= 132; n++) {
			BitVector v = new BitVector(n);
			assertEquals("n:"+n, 0.0, problem.value(v), 0.0);
			assertEquals("n:"+n, 10.0*n, problem.cost(v), 0.0);
		}
	}
	
	@Test
	public void testPlateausValue0() {
		Plateaus problem = new Plateaus();
		int[] length = {
			4, 8, 8, 8,
			32, 32,
			128,
			128,
			132
		};
		int[][] cases = {
			{0}, {0}, {0xAA}, {0x55},
			{0xFEFEFEFE}, {0x7F7F7F7F},
			{0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE, 0xFFFFFFFE},
			{0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
			{0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFD, 0xFFFFFFFB, 0x7}
		};
		for (int i = 0; i < cases.length; i++) {
			int n = length[i];
			BitVector v = new BitVector(n, cases[i]);
			assertEquals("i:"+i+" n:"+n, 0.0, problem.value(v), 0.0);
			assertEquals("i:"+i+" n:"+n, 10.0*n, problem.cost(v), 0.0);
		}
	}
	
}
