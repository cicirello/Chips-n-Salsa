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
 * JUnit 4 test cases for the Porcupine problem.
 */
public class PorcupineTests {
	
	@Test
	public void testPorcupineAllOnes() {
		Porcupine problem = new Porcupine();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// All ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			v.not();
			assertEquals(0, problem.cost(v));
			assertEquals(10*n, problem.value(v));
		}
	}
	
	@Test
	public void testPorcupineNoOnes() {
		Porcupine problem = new Porcupine();
		assertEquals(0, problem.minCost());
		int zero = 0;
		assertTrue(problem.isMinCost(zero));
		assertFalse(problem.isMinCost(zero-1));
		assertFalse(problem.isMinCost(zero+1));
		// 0 ones
		for (int n = 0; n < 8; n++) {
			BitVector v = new BitVector(n);
			int penalty = n%2 == 1 ? -15 : 0;
			assertEquals(10*n-penalty, problem.cost(v));
			assertEquals(penalty, problem.value(v));
		}
	}
	
	@Test
	public void testPorcupineGeneralCase() {
		Porcupine problem = new Porcupine();
		int[] bits = { 0 };
		int n = 8;
		for (int i = 0; i <= 8; i++) {
			BitVector v = new BitVector(n, bits);
			int penalty = (n-i)%2 == 1 ? -15 : 0;
			assertEquals("i:"+i, 10*(n-i)-penalty, problem.cost(v));
			assertEquals("i:"+i, 10*i+penalty, problem.value(v));
			bits[0] = bits[0] | (1 << i); 
		}
	}
	
}
