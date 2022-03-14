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
import org.cicirello.permutations.Permutation;

/**
 * JUnit test cases for the Quadratic Assignment Problem.
 */
public class QAPTests {
	
	@Test
	public void testCreateFromMatrices() {
		int[][] cost = {
			{ 0, 3, 4, 5},
			{ 6, 0, 7, 8},
			{9, 10, 0, 11},
			{12, 13, 2, 0}
		};
		int[][] dist = {
			{ 0, 2, 2, 2},
			{ 3, 0, 3, 3},
			{4, 4, 0, 4},
			{5, 5, 5, 0}
		};
		int[][] costExpected = {
			{ 0, 3, 4, 5},
			{ 6, 0, 7, 8},
			{9, 10, 0, 11},
			{12, 13, 2, 0}
		};
		int[][] distExpected = {
			{ 0, 2, 2, 2},
			{ 3, 0, 3, 3},
			{4, 4, 0, 4},
			{5, 5, 5, 0}
		};
		QuadraticAssignmentProblem problem = QuadraticAssignmentProblem.createInstance(cost, dist);
		assertEquals(0, problem.minCost());
		assertEquals(4, problem.size());
		for (int i = 0; i < costExpected.length; i++) {
			for (int j = 0; j < costExpected.length; j++) {
				assertEquals(costExpected[i][j], problem.getCost(i,j));
				assertEquals(distExpected[i][j], problem.getDistance(i,j));
			}
		}
		
		int expected1 =  12 * 2 + 21 * 3 + 30 * 4 + 27 * 5;
		Permutation p1 = new Permutation(new int[] {0, 1, 2, 3});
		assertEquals(expected1, problem.cost(p1));
		assertEquals(expected1, problem.value(p1));
		
		int expected2 =  12 * 5 + 21 * 4 + 30 * 3 + 27 * 2;
		Permutation p2 = new Permutation(new int[] {3, 2, 1, 0});
		assertEquals(expected2, problem.cost(p2));
		assertEquals(expected2, problem.value(p2));
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createInstance(cost, new int[cost.length][cost.length+1])
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createInstance(new int[cost.length][cost.length+1], dist)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createInstance(new int[cost.length+1][cost.length+1], dist)
		);
	}
	
	@Test
	public void testQAPInternalConstructor() {
		int[][] cost = {
			{ 0, 3, 4, 5},
			{ 6, 0, 7, 8},
			{9, 10, 0, 11},
			{12, 13, 2, 0}
		};
		int[][] dist = {
			{ 0, 2, 2, 2},
			{ 3, 0, 3, 3},
			{4, 4, 0, 4},
			{5, 5, 5, 0}
		};
		int[][] costExpected = {
			{ 0, 3, 4, 5},
			{ 6, 0, 7, 8},
			{9, 10, 0, 11},
			{12, 13, 2, 0}
		};
		int[][] distExpected = {
			{ 0, 2, 2, 2},
			{ 3, 0, 3, 3},
			{4, 4, 0, 4},
			{5, 5, 5, 0}
		};
		QuadraticAssignmentProblem problem = new QuadraticAssignmentProblem(cost, dist);
		assertEquals(0, problem.minCost());
		assertEquals(4, problem.size());
		for (int i = 0; i < costExpected.length; i++) {
			for (int j = 0; j < costExpected.length; j++) {
				assertEquals(costExpected[i][j], problem.getCost(i,j));
				assertEquals(distExpected[i][j], problem.getDistance(i,j));
			}
		}
		
		int expected1 =  12 * 2 + 21 * 3 + 30 * 4 + 27 * 5;
		Permutation p1 = new Permutation(new int[] {0, 1, 2, 3});
		assertEquals(expected1, problem.cost(p1));
		assertEquals(expected1, problem.value(p1));
		
		int expected2 =  12 * 5 + 21 * 4 + 30 * 3 + 27 * 2;
		Permutation p2 = new Permutation(new int[] {3, 2, 1, 0});
		assertEquals(expected2, problem.cost(p2));
		assertEquals(expected2, problem.value(p2));
	}
	
	@Test
	public void testQAPUniformRandomNoSeed() {
		int n = 5;
		int minC = 10;
		int maxC = 20;
		int minD = 100;
		int maxD = 120;
		QuadraticAssignmentProblem problem = QuadraticAssignmentProblem.createUniformRandomInstance(5, minC, maxC, minD, maxD);
		assertEquals(0, problem.minCost());
		assertEquals(n, problem.size());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					assertTrue(problem.getCost(i,j) >= minC);
					assertTrue(problem.getDistance(i,j) >= minD);
					assertTrue(problem.getCost(i,j) <= maxC);
					assertTrue(problem.getDistance(i,j) <= maxD);
				} else {
					assertEquals(0, problem.getCost(i,i));
					assertEquals(0, problem.getDistance(i,i));
				}
			}
		}
		
		problem = QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 17, 14, 14);
		assertEquals(0, problem.minCost());
		assertEquals(n, problem.size());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					assertEquals(17, problem.getCost(i,j));
					assertEquals(14, problem.getDistance(i,j));
				} else {
					assertEquals(0, problem.getCost(i,i));
					assertEquals(0, problem.getDistance(i,i));
				}
			}
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(0, 17, 17, 14, 14)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 16, 14, 14)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 17, 14, 13)
		);
	}
	
	@Test
	public void testQAPUniformRandomWithSeed() {
		int n = 5;
		int minC = 10;
		int maxC = 20;
		int minD = 100;
		int maxD = 120;
		QuadraticAssignmentProblem problem = QuadraticAssignmentProblem.createUniformRandomInstance(5, minC, maxC, minD, maxD, 42);
		assertEquals(0, problem.minCost());
		assertEquals(n, problem.size());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					assertTrue(problem.getCost(i,j) >= minC);
					assertTrue(problem.getDistance(i,j) >= minD);
					assertTrue(problem.getCost(i,j) <= maxC);
					assertTrue(problem.getDistance(i,j) <= maxD);
				} else {
					assertEquals(0, problem.getCost(i,i));
					assertEquals(0, problem.getDistance(i,i));
				}
			}
		}
		
		problem = QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 17, 14, 14, 42);
		assertEquals(0, problem.minCost());
		assertEquals(n, problem.size());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					assertEquals(17, problem.getCost(i,j));
					assertEquals(14, problem.getDistance(i,j));
				} else {
					assertEquals(0, problem.getCost(i,i));
					assertEquals(0, problem.getDistance(i,i));
				}
			}
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(0, 17, 17, 14, 14, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 16, 14, 14, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> QuadraticAssignmentProblem.createUniformRandomInstance(5, 17, 17, 14, 13, 42)
		);
	}
}
