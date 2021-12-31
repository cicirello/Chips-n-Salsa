/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 
package org.cicirello.search.operators.integers;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.representations.BoundedIntegerVector;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * JUnit tests for crossover operators for IntegerVectors.
 */
public class CrossoverTests {
	
	@Test
	public void testSinglePoint() {
		SinglePointCrossover<IntegerVector> crossover = new SinglePointCrossover<IntegerVector>();
		validateSinglePointCrossover(crossover);
		
		SinglePointCrossover<BoundedIntegerVector> crossoverBounded = new SinglePointCrossover<BoundedIntegerVector>();
		validateSinglePointCrossoverBounded(crossoverBounded);
		
		SinglePointCrossover<IntegerVector> crossover2 = crossover.split();
		validateSinglePointCrossover(crossover2);
		
		SinglePointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateSinglePointCrossoverBounded(crossoverBounded2);
		
	}
	
	@Test
	public void testTwoPoint() {
		TwoPointCrossover<IntegerVector> crossover = new TwoPointCrossover<IntegerVector>();
		validateMultiPointCrossover(crossover, 2);
		
		TwoPointCrossover<BoundedIntegerVector> crossoverBounded = new TwoPointCrossover<BoundedIntegerVector>();
		validateMultiPointCrossoverBounded(crossoverBounded, 2);
		
		TwoPointCrossover<IntegerVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 2);
		
		TwoPointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 2);
	}
	
	private void validateMultiPointCrossover(CrossoverOperator<IntegerVector> crossover, int expectedCrossPoints) {
		for (int n = 2; n <= 32; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			IntegerVector v1 = new IntegerVector(a1);
			IntegerVector v2 = new IntegerVector(a2);
			crossover.cross(v1, v2);
			assertEquals(expectedCrossPoints, countCrossPoints(v1, 1));
			assertEquals(expectedCrossPoints, countCrossPoints(v2, 2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateMultiPointCrossoverBounded(CrossoverOperator<BoundedIntegerVector> crossover, int expectedCrossPoints) {
		for (int n = 2; n <= 32; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedIntegerVector v1 = new BoundedIntegerVector(a1, 0, 5);
			BoundedIntegerVector v2 = new BoundedIntegerVector(a2, 0, 5);
			crossover.cross(v1, v2);
			assertEquals(expectedCrossPoints, countCrossPoints(v1, 1));
			assertEquals(expectedCrossPoints, countCrossPoints(v2, 2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateSinglePointCrossover(CrossoverOperator<IntegerVector> crossover) {
		for (int n = 2; n <= 32; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			IntegerVector v1 = new IntegerVector(a1);
			IntegerVector v2 = new IntegerVector(a2);
			crossover.cross(v1, v2);
			assertEquals(1, countCrossPoints(v1));
			assertEquals(1, countCrossPoints(v2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateSinglePointCrossoverBounded(CrossoverOperator<BoundedIntegerVector> crossover) {
		for (int n = 2; n <= 32; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedIntegerVector v1 = new BoundedIntegerVector(a1, 0, 5);
			BoundedIntegerVector v2 = new BoundedIntegerVector(a2, 0, 5);
			crossover.cross(v1, v2);
			assertEquals(1, countCrossPoints(v1));
			assertEquals(1, countCrossPoints(v2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}

	private int countCrossPoints(IntegerVector v1) {
		int count = 0;
		for (int i = 1; i < v1.length(); i++) {
			if (v1.get(i) != v1.get(i-1)) count++;
		}
		return count;
	}
	
	private int countCrossPoints(IntegerVector v1, int shouldStartWith) {
		int count = v1.get(0) != shouldStartWith ? 1 : 0;
		for (int i = 1; i < v1.length(); i++) {
			if (v1.get(i) != v1.get(i-1)) count++;
		}
		return count;
	}
	
	private boolean areSiblings(IntegerVector v1, IntegerVector v2, int value1, int value2) {
		if (v1.length() != v2.length()) return false;
		for (int i = 0; i < v1.length(); i++) {
			if (v1.get(i) == v2.get(i)) return false;
			if (v1.get(i) != value1 && v1.get(i) != value2) return false;
			if (v2.get(i) != value1 && v2.get(i) != value2) return false;
		}
		return true;
	}
}
