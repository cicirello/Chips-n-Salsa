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
 * JUnit 4 test cases for the TwoMax problem.
 */
public class TwoMaxTests {
	
	// Tests for original version
	
	@Test
	public void testTwoMaxNoOnes() {
		TwoMax problem = new TwoMax();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			assertEquals(2*n, problem.cost(v));
			assertEquals(8*n, problem.value(v));
		}
	}
	
	@Test
	public void testTwoMaxAllOnes() {
		TwoMax problem = new TwoMax();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0, problem.cost(v));
			assertEquals(10*n, problem.value(v));
		}
	}
	
	@Test
	public void testTwoMaxFourNinths() {
		TwoMax problem = new TwoMax();
		BitVector v = new BitVector(9);
		v.setBit(0, 1);
		v.setBit(1, 1);
		v.setBit(4, 1);
		v.setBit(8, 1);
		assertEquals(90, problem.cost(v));
		assertEquals(0, problem.value(v));
	}
	
	@Test
	public void testTwoMaxGeneralCases() {
		TwoMax problem = new TwoMax();
		BitVector v1 = new BitVector(9);
		v1.setBit(0, 1);
		v1.setBit(1, 1);
		v1.setBit(2, 1);
		v1.setBit(3, 1);
		BitVector v2 = v1.copy();
		int expectedValue = 0;
		int expectedCost = 90;
		for (int i = 4; i < 8; i++) {
			v1.setBit(i, 1);
			expectedValue += 18;
			expectedCost -= 18;
			assertEquals(expectedValue, problem.value(v1));
			assertEquals(expectedCost, problem.cost(v1));
		}
		expectedValue = 0;
		expectedCost = 90;
		for (int i = 0; i < 3; i++) {
			v2.setBit(i, 0);
			expectedValue += 18;
			expectedCost -= 18;
			assertEquals(expectedValue, problem.value(v2));
			assertEquals(expectedCost, problem.cost(v2));
		}
	}
	
	// Tests for equal peaks version
	
	@Test
	public void testTwoMaxEqualPeaksNoOnes() {
		TwoMaxEqualPeaks problem = new TwoMaxEqualPeaks();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			assertEquals(0, problem.cost(v));
			assertEquals(10*n, problem.value(v));
		}
	}
	
	@Test
	public void testTwoMaxEqualPeaksAllOnes() {
		TwoMaxEqualPeaks problem = new TwoMaxEqualPeaks();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0, problem.cost(v));
			assertEquals(10*n, problem.value(v));
		}
	}
	
	@Test
	public void testTwoMaxEqualPeaksMidway() {
		TwoMaxEqualPeaks problem = new TwoMaxEqualPeaks();
		BitVector v = new BitVector(8);
		v.setBit(0, 1);
		v.setBit(1, 1);
		v.setBit(2, 1);
		v.setBit(3, 1);
		assertEquals(80, problem.cost(v));
		assertEquals(0, problem.value(v));
	}
	
	@Test
	public void testTwoMaxEqualGeneralCase() {
		TwoMaxEqualPeaks problem = new TwoMaxEqualPeaks();
		BitVector v1 = new BitVector(8);
		v1.setBit(0, 1);
		v1.setBit(1, 1);
		v1.setBit(2, 1);
		v1.setBit(3, 1);
		BitVector v2 = v1.copy();
		int expectedValue = 0;
		int expectedCost = 80;
		for (int i = 4; i < 7; i++) {
			v1.setBit(i, 1);
			expectedValue += 20;
			expectedCost -= 20;
			assertEquals(expectedValue, problem.value(v1));
			assertEquals(expectedCost, problem.cost(v1));
		}
		expectedValue = 0;
		expectedCost = 80;
		for (int i = 0; i < 3; i++) {
			v2.setBit(i, 0);
			expectedValue += 20;
			expectedCost -= 20;
			assertEquals(expectedValue, problem.value(v2));
			assertEquals(expectedCost, problem.cost(v2));
		}
	}
	
}
