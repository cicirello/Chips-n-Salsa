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
 * JUnit 4 test cases for the Trap problem.
 */
public class TrapTests {
	
	private final double EPSILON = 1e-10;
	
	@Test
	public void testTrapNoOnes() {
		Trap problem = new Trap();
		assertEquals(0.0, problem.minCost(), 0.0);
		double zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			double expectedCost = n <= 1 ? 10*n : 2*n;
			double expectedValue = n <= 1 ? 0 : 8*n;
			assertEquals(expectedCost, problem.cost(v), 0.0);
			assertEquals(expectedValue, problem.value(v), 0.0);
			assertEquals(expectedValue+1, problem.fitness(v), 0.0);
		}
		assertTrue(problem == problem.getProblem());
	}
		
	@Test
	public void testTrapAllOnes() {
		Trap problem = new Trap();
		assertEquals(0.0, problem.minCost(), 0.0);
		double zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// all ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0.0, problem.cost(v), 0.0);
			assertEquals(10.0*n, problem.value(v), 0.0);
			assertEquals(10.0*n+1, problem.fitness(v), 0.0);
		}
		assertTrue(problem == problem.getProblem());
	}
	
	@Test
	public void testTrapMeetingPoint() {
		Trap problem = new Trap();
		for (int c = 1; c <= 8; c++) {
			int d = 3*c;
			int n = c+d;
			BitVector v = new BitVector(n);
			v.not();
			for (int i = 0; i < c; i++) {
				v.setBit(i, 0);
			}
			assertEquals(10.0*n, problem.cost(v), 0.0);
			assertEquals(0, problem.value(v), 0.0);
			assertEquals(1.0, problem.fitness(v), 0.0);
		}
	}
	
	@Test
	public void testTrapGeneralCases() {
		Trap problem = new Trap();
		for (int c = 2; c <= 4; c++) {
			int d = 3*c;
			int n = c+d;
			BitVector v1 = new BitVector(n);
			v1.not();
			for (int i = 0; i < c; i++) {
				v1.setBit(i, 0);
			}
			BitVector v2 = v1.copy(); 
			double expectedCost = 10*n;
			double expectedValue = 0;
			for (int i = 1; i < c; i++) {
				v1.setBit(i, 1);
				expectedCost -= 40;
				expectedValue += 40;
				assertEquals(expectedValue, problem.value(v1), EPSILON);
				assertEquals(expectedCost, problem.cost(v1), EPSILON);
				assertEquals(expectedValue+1, problem.fitness(v1), EPSILON);
			}
			expectedCost = 10*n;
			expectedValue = 0;
			final double delta = 32.0/3.0;
			for (int i = c+1; i < n; i++) {
				v2.setBit(i, 0);
				expectedCost -= delta;
				expectedValue += delta;
				assertEquals(expectedValue, problem.value(v2), EPSILON);
				assertEquals(expectedCost, problem.cost(v2), EPSILON);
				assertEquals(expectedValue+1, problem.fitness(v2), EPSILON);
			}
		}
	}
}
