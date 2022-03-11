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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.representations.BitVector;

/**
 * JUnit test cases for the the OneMax problem.
 */
public class OneMaxTests {
	
	// OneMax class
	
	@Test
	public void testOneMaxNoOnes() {
		OneMax problem = new OneMax();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 35; n++) {
			BitVector v = new BitVector(n);
			assertEquals(n, problem.cost(v));
			assertEquals(0, problem.value(v));
		}
	}
	
	@Test
	public void testOneMaxAllOnes() {
		OneMax problem = new OneMax();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		// all ones
		for (int n = 0; n < 35; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0, problem.cost(v));
			assertEquals(n, problem.value(v));
		}
	}
	
	@Test
	public void testOneMaxSingleOne() {
		OneMax problem = new OneMax();
		// single one
		for (int n = 1; n <= 8; n++) {
			for (int shift = 0; shift < n; shift++) {
				BitVector v = new BitVector(n);
				v.setBit(shift, 1);
				assertEquals(n-1, problem.cost(v));
				assertEquals(1, problem.value(v));
			}
		}
	}
	
	@Test
	public void testOneMaxSingleZero() {
		OneMax problem = new OneMax();
		// single zero
		for (int n = 1; n <= 8; n++) {
			for (int shift = 0; shift < n; shift++) {
				BitVector v = new BitVector(n);
				v.setBit(shift, 1);
				v.not();
				assertEquals(1, problem.cost(v));
				assertEquals(n-1, problem.value(v));
			}
		}
	}
	
	@Test
	public void testOneMaxHalfOnes() {
		OneMax problem = new OneMax();
		for (int n = 0; n < 17; n++) {
			BitVector v = new BitVector(n);
			for (int shift = 0; shift < n; shift+=2) {
				v.setBit(shift, 1);
			}
			assertEquals(n/2, problem.cost(v));
			assertEquals((n+1)/2, problem.value(v));
			v.shiftLeft(1);
			assertEquals((n+1)/2, problem.cost(v));
			assertEquals(n/2, problem.value(v));
		}
	}
	
	// OneMaxAckley class
	
	@Test
	public void testOneMaxAckleyNoOnes() {
		OneMaxAckley problem = new OneMaxAckley();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 35; n++) {
			BitVector v = new BitVector(n);
			assertEquals(10*n, problem.cost(v));
			assertEquals(0, problem.value(v));
		}
	}
	
	@Test
	public void testOneMaxAckleyAllOnes() {
		OneMaxAckley problem = new OneMaxAckley();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		// all ones
		for (int n = 0; n < 35; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0, problem.cost(v));
			assertEquals(10*n, problem.value(v));
		}
	}
	
	@Test
	public void testOneMaxAckleySingleOne() {
		OneMaxAckley problem = new OneMaxAckley();
		// single one
		for (int n = 1; n <= 8; n++) {
			for (int shift = 0; shift < n; shift++) {
				BitVector v = new BitVector(n);
				v.setBit(shift, 1);
				assertEquals(10*n-10, problem.cost(v));
				assertEquals(10, problem.value(v));
			}
		}
	}
	
	@Test
	public void testOneMaxAckleySingleZero() {
		OneMaxAckley problem = new OneMaxAckley();
		// single zero
		for (int n = 1; n <= 8; n++) {
			for (int shift = 0; shift < n; shift++) {
				BitVector v = new BitVector(n);
				v.setBit(shift, 1);
				v.not();
				assertEquals(10, problem.cost(v));
				assertEquals(10*n-10, problem.value(v));
			}
		}
	}
	
	@Test
	public void testOneMaxAckleyHalfOnes() {
		OneMaxAckley problem = new OneMaxAckley();
		for (int n = 0; n < 17; n++) {
			BitVector v = new BitVector(n);
			for (int shift = 0; shift < n; shift+=2) {
				v.setBit(shift, 1);
			}
			assertEquals(10*(n/2), problem.cost(v));
			assertEquals(10*((n+1)/2), problem.value(v));
			v.shiftLeft(1);
			assertEquals(10*((n+1)/2), problem.cost(v));
			assertEquals(10*(n/2), problem.value(v));
		}
	}
	
}

