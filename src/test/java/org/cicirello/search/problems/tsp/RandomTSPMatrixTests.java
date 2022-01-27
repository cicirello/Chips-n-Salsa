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
 
package org.cicirello.search.problems.tsp;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit tests for the RandomTSPMatrix class and its nested subclasses.
 */
public class RandomTSPMatrixTests {
	
	@Test
	public void testIntegerConstructor1() {
		final int MAX = 1000000;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Integer tsp = new RandomTSPMatrix.Integer(n, 1); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX);
			validateSymmetric(tsp, n, MAX);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(1, 1)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(2, 0)
		);
	}
	
	@Test
	public void testIntegerConstructor2() {
		final int MAX_S = 1000000;
		final int MAX_A = 1000;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Integer tsp = new RandomTSPMatrix.Integer(n, 1, true); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, 1, false); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_S, true);
			validateSymmetric(tsp, n, MAX_S);
			tsp = new RandomTSPMatrix.Integer(n, MAX_A, false);
			validateAsymmetric(tsp, n, MAX_A);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(1, 1, true)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(2, 0, true)
		);
	}
	
	@Test
	public void testIntegerConstructor3() {
		final int MAX_S = 1000000;
		final int MAX_A = 1000;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Integer tsp = new RandomTSPMatrix.Integer(n, 1, true, false); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, 1, false, false); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_S, true, false);
			validateSymmetric(tsp, n, MAX_S);
			tsp = new RandomTSPMatrix.Integer(n, MAX_A, false, false);
			validateAsymmetric(tsp, n, MAX_A);
			
			tsp = new RandomTSPMatrix.Integer(n, 1, true, true); 
			validateAllOne(tsp, n);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, 1, false, true); 
			validateAllOne(tsp, n);
			validateTriangleAsymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_S, true, true);
			validateSymmetric(tsp, n, MAX_S);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_A, false, true);
			validateAsymmetric(tsp, n, MAX_A);
			validateTriangleAsymmetric(tsp, n);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(1, 1, true, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(2, 0, true, false)
		);
	}
	
	@Test
	public void testIntegerConstructor4() {
		final int MAX_S = 1000000;
		final int MAX_A = 1000;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Integer tsp = new RandomTSPMatrix.Integer(n, 1, true, false, 42); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, 1, false, false, 42); 
			validateAllOne(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_S, true, false, 42);
			validateSymmetric(tsp, n, MAX_S);
			tsp = new RandomTSPMatrix.Integer(n, MAX_A, false, false, 42);
			validateAsymmetric(tsp, n, MAX_A);
			
			tsp = new RandomTSPMatrix.Integer(n, 1, true, true, 42); 
			validateAllOne(tsp, n);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, 1, false, true, 42); 
			validateAllOne(tsp, n);
			validateTriangleAsymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_S, true, true, 42);
			validateSymmetric(tsp, n, MAX_S);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Integer(n, MAX_A, false, true, 42);
			validateAsymmetric(tsp, n, MAX_A);
			validateTriangleAsymmetric(tsp, n);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(1, 1, true, false, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(2, 0, true, false, 42)
		);
	}
	
	@Test
	public void testIntegerTSPMatrix() {
		int[][] matrix2 = { {7, 3}, {5, 9} };
		int[][] expected2 = { {7, 3}, {5, 9} };
		RandomTSPMatrix.Integer tsp = new RandomTSPMatrix.Integer(matrix2);
		assertEquals(0, tsp.minCost());
		assertEquals(2, tsp.length());
		for (int i = 0; i < expected2.length; i++) {
			for (int j = 0; j < expected2[i].length; j++) {
				assertEquals(expected2[i][j], tsp.getDistance(i, j));
				assertEquals(expected2[i][j], tsp.edgeCostForHeuristics(i, j), 0.0);
			}
		}
		
		int[][] matrix4 = {
			{0,  10, 12, 14},
			{8,  0,  5,  9},
			{20, 1,  0,  3},
			{17, 18, 4,  0}
		};
		int[][] expected4 = {
			{0,  10, 12, 14},
			{8,  0,  5,  9},
			{20, 1,  0,  3},
			{17, 18, 4,  0}
		};
		tsp = new RandomTSPMatrix.Integer(matrix4);
		assertEquals(4, tsp.length());
		for (int i = 0; i < expected4.length; i++) {
			for (int j = 0; j < expected4[i].length; j++) {
				assertEquals(expected4[i][j], tsp.getDistance(i, j));
				assertEquals(expected4[i][j], tsp.edgeCostForHeuristics(i, j), 0.0);
			}
		}
		
		int[][] permutationTestCases = {
			{0, 1, 2, 3},
			{3, 2, 1, 0},
			{2, 0, 3, 1}
		};
		int[] expectedCosts = {35, 27, 57};
		for (int i = 0; i < permutationTestCases.length; i++) {
			Permutation p = new Permutation(permutationTestCases[i]);
			assertEquals(expectedCosts[i], tsp.cost(p));
			assertEquals(expectedCosts[i], tsp.value(p));
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(new int[1][1])
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Integer(new int[4][5])
		);
	}
	
	@Test
	public void testDoubleConstructor1() {
		final double MAX = 1.0;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Double tsp = new RandomTSPMatrix.Double(n, 0.0); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX);
			validateSymmetric(tsp, n, MAX);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(1, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(2, -Math.ulp(0.0))
		);
	}
	
	@Test
	public void testDoubleConstructor2() {
		final double MAX = 1.0;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Double tsp = new RandomTSPMatrix.Double(n, 0.0, true); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, 0.0, false); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, true);
			validateSymmetric(tsp, n, MAX);
			tsp = new RandomTSPMatrix.Double(n, MAX, false);
			validateAsymmetric(tsp, n, MAX);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(1, 0.0, true)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(2, -Math.ulp(0.0), true)
		);
	}
	
	@Test
	public void testDoubleConstructor3() {
		final double MAX = 1.0;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Double tsp = new RandomTSPMatrix.Double(n, 0.0, true, false); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, 0.0, false, false); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, true, false);
			validateSymmetric(tsp, n, MAX);
			tsp = new RandomTSPMatrix.Double(n, MAX, false, false);
			validateAsymmetric(tsp, n, MAX);
			
			tsp = new RandomTSPMatrix.Double(n, 0.0, true, true); 
			validateAllZero(tsp, n);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, 0.0, false, true); 
			validateAllZero(tsp, n);
			validateTriangleAsymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, true, true);
			validateSymmetric(tsp, n, MAX);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, false, true);
			validateAsymmetric(tsp, n, MAX);
			validateTriangleAsymmetric(tsp, n);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(1, 0.0, true, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(2, -Math.ulp(0.0), true, false)
		);
	}
	
	@Test
	public void testDoubleConstructor4() {
		final double MAX = 1.0;
		for (int n = 2; n <= 8; n*= 2) {
			RandomTSPMatrix.Double tsp = new RandomTSPMatrix.Double(n, 0.0, true, false, 42); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, 0.0, false, false, 42); 
			validateAllZero(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, true, false, 42);
			validateSymmetric(tsp, n, MAX);
			tsp = new RandomTSPMatrix.Double(n, MAX, false, false, 42);
			validateAsymmetric(tsp, n, MAX);
			
			tsp = new RandomTSPMatrix.Double(n, 0.0, true, true, 42); 
			validateAllZero(tsp, n);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, 0.0, false, true, 42); 
			validateAllZero(tsp, n);
			validateTriangleAsymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, true, true, 42);
			validateSymmetric(tsp, n, MAX);
			validateTriangleSymmetric(tsp, n);
			tsp = new RandomTSPMatrix.Double(n, MAX, false, true, 42);
			validateAsymmetric(tsp, n, MAX);
			validateTriangleAsymmetric(tsp, n);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(1, 0.0, true, false, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(2, -Math.ulp(0.0), true, false, 42)
		);
	}
	
	@Test
	public void testDoubleTSPMatrix() {
		double[][] matrix2 = { {7, 3}, {5, 9} };
		double[][] expected2 = { {7, 3}, {5, 9} };
		RandomTSPMatrix.Double tsp = new RandomTSPMatrix.Double(matrix2);
		assertEquals(0, tsp.minCost(), 0.0);
		assertEquals(2, tsp.length());
		for (int i = 0; i < expected2.length; i++) {
			for (int j = 0; j < expected2[i].length; j++) {
				assertEquals(expected2[i][j], tsp.getDistance(i, j), 0.0);
				assertEquals(expected2[i][j], tsp.edgeCostForHeuristics(i, j), 0.0);
			}
		}
		
		double[][] matrix4 = {
			{0,  10, 12, 14},
			{8,  0,  5,  9},
			{20, 1,  0,  3},
			{17, 18, 4,  0}
		};
		double[][] expected4 = {
			{0,  10, 12, 14},
			{8,  0,  5,  9},
			{20, 1,  0,  3},
			{17, 18, 4,  0}
		};
		tsp = new RandomTSPMatrix.Double(matrix4);
		assertEquals(4, tsp.length());
		for (int i = 0; i < expected4.length; i++) {
			for (int j = 0; j < expected4[i].length; j++) {
				assertEquals(expected4[i][j], tsp.getDistance(i, j), 0.0);
				assertEquals(expected4[i][j], tsp.edgeCostForHeuristics(i, j), 0.0);
			}
		}
		
		int[][] permutationTestCases = {
			{0, 1, 2, 3},
			{3, 2, 1, 0},
			{2, 0, 3, 1}
		};
		double[] expectedCosts = {35, 27, 57};
		for (int i = 0; i < permutationTestCases.length; i++) {
			Permutation p = new Permutation(permutationTestCases[i]);
			assertEquals(expectedCosts[i], tsp.cost(p), 1E-10);
			assertEquals(expectedCosts[i], tsp.value(p), 1E-10);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(new double[1][1])
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomTSPMatrix.Double(new double[4][5])
		);
	}
	
	private void validateAllOne(RandomTSPMatrix.Integer tsp, int n) {
		assertEquals(n, tsp.length());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				assertEquals(i!=j?1:0, tsp.getDistance(i,j));
				assertEquals(i!=j?1.0:0.0, tsp.edgeCostForHeuristics(i,j), 0.0);
			}
		}
	}
	
	private void validateAllZero(RandomTSPMatrix.Double tsp, int n) {
		assertEquals(n, tsp.length());
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				assertEquals(0.0, tsp.getDistance(i,j), 0.0);
				assertEquals(0.0, tsp.edgeCostForHeuristics(i,j), 0.0);
			}
		}
	}
	
	private void validateSymmetric(RandomTSPMatrix.Integer tsp, int n, final int MAX) {
		assertEquals(n, tsp.length());
		boolean foundNotOne = false;
		boolean foundNotMax = false;
		for (int i = 0; i < n; i++) {
			assertEquals(0, tsp.getDistance(i,i));
			assertEquals(0.0, tsp.edgeCostForHeuristics(i,i), 0.0);
			for (int j = i+1; j < n; j++) {
				assertEquals(tsp.getDistance(j,i), tsp.getDistance(i,j));
				assertEquals(tsp.edgeCostForHeuristics(j,i), tsp.edgeCostForHeuristics(i,j), 0.0);
				assertEquals(tsp.getDistance(i,j), tsp.edgeCostForHeuristics(i,j), 0.0);
				assertTrue(tsp.getDistance(i,j) >= 1);
				assertTrue(tsp.getDistance(i,j) <= MAX);
				if (tsp.getDistance(i,j) > 1) {
					foundNotOne = true;
				}
				if (tsp.getDistance(i,j) < MAX) {
					foundNotMax = true;
				}
			}
		}
		assertTrue(foundNotOne);
		assertTrue(foundNotMax);
	}
	
	private void validateSymmetric(RandomTSPMatrix.Double tsp, int n, final double MAX) {
		assertEquals(n, tsp.length());
		boolean foundNotZero = false;
		boolean foundNotMax = false;
		for (int i = 0; i < n; i++) {
			assertEquals(0.0, tsp.getDistance(i,i), 0.0);
			assertEquals(0.0, tsp.edgeCostForHeuristics(i,i), 0.0);
			for (int j = i+1; j < n; j++) {
				assertEquals(tsp.getDistance(j,i), tsp.getDistance(i,j), 0.0);
				assertEquals(tsp.edgeCostForHeuristics(j,i), tsp.edgeCostForHeuristics(i,j), 0.0);
				assertEquals(tsp.getDistance(i,j), tsp.edgeCostForHeuristics(i,j), 0.0);
				assertTrue(tsp.getDistance(i,j) >= 0.0);
				assertTrue(tsp.getDistance(i,j) <= MAX);
				if (tsp.getDistance(i,j) > 0.0) {
					foundNotZero = true;
				}
				if (tsp.getDistance(i,j) < MAX) {
					foundNotMax = true;
				}
			}
		}
		assertTrue(foundNotZero);
		assertTrue(foundNotMax);
	}
	
	private void validateAsymmetric(RandomTSPMatrix.Integer tsp, int n, final int MAX) {
		assertEquals(n, tsp.length());
		boolean foundNotOne = false;
		boolean foundNotMax = false;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i==j) {
					assertEquals(0, tsp.getDistance(i,j));
					assertEquals(0.0, tsp.edgeCostForHeuristics(i,j), 0.0);
				} else {
					assertEquals(tsp.getDistance(i,j), tsp.edgeCostForHeuristics(i,j), 0.0);
					assertTrue(tsp.getDistance(i,j) >= 1);
					assertTrue(tsp.getDistance(i,j) <= MAX);
					if (tsp.getDistance(i,j) > 1) {
						foundNotOne = true;
					}
					if (tsp.getDistance(i,j) < MAX) {
						foundNotMax = true;
					}
				}
			}
		}
		assertTrue(foundNotOne);
		assertTrue(foundNotMax);
	}
	
	private void validateAsymmetric(RandomTSPMatrix.Double tsp, int n, final double MAX) {
		assertEquals(n, tsp.length());
		boolean foundNotZero = false;
		boolean foundNotMax = false;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i==j) {
					assertEquals(0.0, tsp.getDistance(i,j), 0.0);
					assertEquals(0.0, tsp.edgeCostForHeuristics(i,j), 0.0);
				} else {
					assertEquals(tsp.getDistance(i,j), tsp.edgeCostForHeuristics(i,j), 0.0);
					assertTrue(tsp.getDistance(i,j) >= 0.0);
					assertTrue(tsp.getDistance(i,j) <= MAX);
					if (tsp.getDistance(i,j) > 0.0) {
						foundNotZero = true;
					}
					if (tsp.getDistance(i,j) < MAX) {
						foundNotMax = true;
					}
				}
			}
		}
		assertTrue(foundNotZero);
		assertTrue(foundNotMax);
	}
	
	private void validateTriangleAsymmetric(RandomTSPMatrix.Integer tsp, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					for (int k = 0; k < n; k++) {
						if (k != i && k != j) {
							assertTrue(tsp.getDistance(i, j) <= tsp.getDistance(i, k) + tsp.getDistance(k, j));
						}
					}
				}
			}
		}
	}
	
	private void validateTriangleAsymmetric(RandomTSPMatrix.Double tsp, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					for (int k = 0; k < n; k++) {
						if (k != i && k != j) {
							assertTrue(tsp.getDistance(i, j) <= tsp.getDistance(i, k) + tsp.getDistance(k, j));
						}
					}
				}
			}
		}
	}
	
	private void validateTriangleSymmetric(RandomTSPMatrix.Integer tsp, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (k != i && k != j) {
						assertTrue(tsp.getDistance(i, j) <= tsp.getDistance(i, k) + tsp.getDistance(k, j));
					}
				}	
			}
		}
	}
	
	private void validateTriangleSymmetric(RandomTSPMatrix.Double tsp, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				for (int k = 0; k < n; k++) {
					if (k != i && k != j) {
						assertTrue(tsp.getDistance(i, j) <= tsp.getDistance(i, k) + tsp.getDistance(k, j));
					}
				}	
			}
		}
	}
	
}
