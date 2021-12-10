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
 * JUnit 4 test cases for the RoyalRoad problem.
 */
public class RoyalRoadTests {
	
	@Test
	public void testRoyalRoadAllOnesNoStones() {
		int[] permutationLengths = { 31, 32, 33, 63, 64, 65 };
		int[] blockSize = { 7, 8, 9, 31, 32, 33 };
		for (int m : blockSize) {
			RoyalRoad problem = new RoyalRoad(m, false);
			assertEquals(0, problem.minCost());
			assertTrue(problem.isMinCost(0));
			assertFalse(problem.isMinCost(1));
			assertFalse(problem.isMinCost(-1));
			for (int n : permutationLengths) {
				BitVector v = new BitVector(n);
				v.not();
				assertEquals(2*n, problem.value(v));
				assertEquals(0, problem.cost(v));
				assertEquals(2*n+1, problem.fitness(v));
			}
			assertTrue(problem == problem.getProblem());
		}		
	}
	
	@Test
	public void testRoyalRoadAllZerosNoStones() {
		int[] permutationLengths = { 31, 32, 33, 63, 64, 65 };
		int[] blockSize = { 7, 8, 9, 31, 32, 33 };
		for (int m : blockSize) {
			RoyalRoad problem = new RoyalRoad(m, false);
			assertEquals(0, problem.minCost());
			assertTrue(problem.isMinCost(0));
			assertFalse(problem.isMinCost(1));
			assertFalse(problem.isMinCost(-1));
			for (int n : permutationLengths) {
				BitVector v = new BitVector(n);
				assertEquals(2*n, problem.cost(v));
				assertEquals(0, problem.value(v));
				assertEquals(1, problem.fitness(v));
			}
		}		
	}
	
	@Test
	public void testRoyalRoadAllOnesSteppingStones() {
		int[] permutationLengths = { 31, 32, 33, 63, 64, 65 };
		int[] blockSize = { 7, 8, 9, 31, 32, 33 };
		for (int m : blockSize) {
			RoyalRoad problem = new RoyalRoad(m, true);
			assertEquals(0, problem.minCost());
			assertTrue(problem.isMinCost(0));
			assertFalse(problem.isMinCost(1));
			assertFalse(problem.isMinCost(-1));
			for (int n : permutationLengths) {
				BitVector v = new BitVector(n);
				v.not();
				assertEquals("n,m="+n+","+m, numLevels(n,m)*n, problem.value(v));
				assertEquals(0, problem.cost(v));
				assertEquals(numLevels(n,m)*n+1, problem.fitness(v));
			}
		}		
	}
	
	@Test
	public void testRoyalRoadAllZerosSteppingStones() {
		int[] permutationLengths = { 31, 32, 33, 63, 64, 65 };
		int[] blockSize = { 7, 8, 9, 31, 32, 33 };
		for (int m : blockSize) {
			RoyalRoad problem = new RoyalRoad(m, true);
			assertEquals(0, problem.minCost());
			assertTrue(problem.isMinCost(0));
			assertFalse(problem.isMinCost(1));
			assertFalse(problem.isMinCost(-1));
			for (int n : permutationLengths) {
				BitVector v = new BitVector(n);
				assertEquals("n,m="+n+","+m, numLevels(n,m)*n, problem.cost(v));
				assertEquals(0, problem.value(v));
				assertEquals(1, problem.fitness(v));
			}
		}		
	}
	
	@Test
	public void testRoyalRoadMitchellOriginalCaseNoStones() {
		int n = 64;
		int m = 8;
		RoyalRoad problem = new RoyalRoad(m, false);
		BitVector v = new BitVector(n);
		v.not();
		int expected = 2*n;
		assertEquals(expected, problem.value(v));
		assertEquals(0, problem.cost(v));
		expected -= n;
		for (int i = 0; i < n; i+= m) {
			v.flip(i);
			expected -= m;
			assertEquals(expected, problem.value(v));
			assertEquals(2*n-expected, problem.cost(v));
		}
		v = new BitVector(n);
		v.not();
		expected = n;
		for (int i = m-1; i < n; i+= m) {
			v.flip(i);
			expected -= m;
			assertEquals(expected, problem.value(v));
			assertEquals(2*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
		}
	}
	
	@Test
	public void testRoyalRoadMitchellOriginalCaseSteppingStones() {
		int n = 64;
		int m = 8;
		RoyalRoad problem = new RoyalRoad(m, true);
		BitVector v = new BitVector(n);
		v.not();
		int expected = 3*n;
		for (int i = 0; i < n; i += 32) {
			v.flip(i);
			expected = expected - 32 - 16 - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+8);
			expected = expected - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+16);
			expected = expected - 16 - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+24);
			expected = expected - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
		}
		v = new BitVector(n);
		v.not();
		expected = 3*n;
		for (int i = 7; i < n; i += 32) {
			v.flip(i);
			expected = expected - 32 - 16 - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+8);
			expected = expected - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+16);
			expected = expected - 16 - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
			v.flip(i+24);
			expected = expected - 8;
			assertEquals(expected, problem.value(v));
			assertEquals(4*n-expected, problem.cost(v));
			assertEquals(expected+1, problem.fitness(v));
		}
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RoyalRoad(0, true)
		);
	}
	
	private int numLevels(int n, int m) {
		int x = 2;
		m *= 2;
		while (m < n) {
			x++;
			m *= 2;
		}
		return x;
	}
}