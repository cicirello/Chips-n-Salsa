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
 
package org.cicirello.search.operators.integers;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.representations.BoundedIntegerVector;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * JUnit tests for crossover operators for IntegerVectors.
 */
public class CrossoverTests {
	
	@Test
	public void testUniformCrossover() {
		UniformCrossover<IntegerVector> crossover0 = new UniformCrossover<IntegerVector>(0.0);
		UniformCrossover<IntegerVector> crossover1 = new UniformCrossover<IntegerVector>(1.0);
		UniformCrossover<IntegerVector> crossoverDefault = new UniformCrossover<IntegerVector>();
		UniformCrossover<IntegerVector> crossover05 = new UniformCrossover<IntegerVector>(0.5);
		
		for (int n = 32; n <= 128; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			IntegerVector v1 = new IntegerVector(a1);
			IntegerVector v2 = new IntegerVector(a2);
			IntegerVector copy1 = v1.copy();
			IntegerVector copy2 = v2.copy();
			
			crossover0.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new IntegerVector(a1);
			v2 = new IntegerVector(a2);
			crossoverDefault.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new IntegerVector(a1);
			v2 = new IntegerVector(a2);
			crossover05.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
		
		UniformCrossover<IntegerVector> crossover0_s = crossover0.split();
		UniformCrossover<IntegerVector> crossover1_s = crossover1.split();
		UniformCrossover<IntegerVector> crossoverDefault_s = crossoverDefault.split();
		UniformCrossover<IntegerVector> crossover05_s = crossover05.split();
		
		for (int n = 32; n <= 128; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			IntegerVector v1 = new IntegerVector(a1);
			IntegerVector v2 = new IntegerVector(a2);
			IntegerVector copy1 = v1.copy();
			IntegerVector copy2 = v2.copy();
			
			crossover0_s.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1_s.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new IntegerVector(a1);
			v2 = new IntegerVector(a2);
			crossoverDefault_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new IntegerVector(a1);
			v2 = new IntegerVector(a2);
			crossover05_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	@Test
	public void testUniformCrossoverBounded() {
		UniformCrossover<BoundedIntegerVector> crossover0 = new UniformCrossover<BoundedIntegerVector>(0.0);
		UniformCrossover<BoundedIntegerVector> crossover1 = new UniformCrossover<BoundedIntegerVector>(1.0);
		UniformCrossover<BoundedIntegerVector> crossoverDefault = new UniformCrossover<BoundedIntegerVector>();
		UniformCrossover<BoundedIntegerVector> crossover05 = new UniformCrossover<BoundedIntegerVector>(0.5);
		
		for (int n = 32; n <= 128; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedIntegerVector v1 = new BoundedIntegerVector(a1, 0, 5);
			BoundedIntegerVector v2 = new BoundedIntegerVector(a2, 0, 5);
			BoundedIntegerVector copy1 = v1.copy();
			BoundedIntegerVector copy2 = v2.copy();
			
			crossover0.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new BoundedIntegerVector(a1, 0, 5);
			v2 = new BoundedIntegerVector(a2, 0, 5);
			crossoverDefault.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new BoundedIntegerVector(a1, 0, 5);
			v2 = new BoundedIntegerVector(a2, 0, 5);
			crossover05.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
		
		UniformCrossover<BoundedIntegerVector> crossover0_s = crossover0.split();
		UniformCrossover<BoundedIntegerVector> crossover1_s = crossover1.split();
		UniformCrossover<BoundedIntegerVector> crossoverDefault_s = crossoverDefault.split();
		UniformCrossover<BoundedIntegerVector> crossover05_s = crossover05.split();
		
		for (int n = 32; n <= 128; n *= 2) {
			int[] a1 = new int[n];
			int[] a2 = new int[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedIntegerVector v1 = new BoundedIntegerVector(a1, 0, 5);
			BoundedIntegerVector v2 = new BoundedIntegerVector(a2, 0, 5);
			BoundedIntegerVector copy1 = v1.copy();
			BoundedIntegerVector copy2 = v2.copy();
			
			crossover0_s.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1_s.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new BoundedIntegerVector(a1, 0, 5);
			v2 = new BoundedIntegerVector(a2, 0, 5);
			crossoverDefault_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new BoundedIntegerVector(a1, 0, 5);
			v2 = new BoundedIntegerVector(a2, 0, 5);
			crossover05_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
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
	
	@Test
	public void testKPoint1() {
		KPointCrossover<IntegerVector> crossover = new KPointCrossover<IntegerVector>(1);
		validateMultiPointCrossover(crossover, 1);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded = new KPointCrossover<BoundedIntegerVector>(1);
		validateMultiPointCrossoverBounded(crossoverBounded, 1);
		
		KPointCrossover<IntegerVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 1);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 1);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new KPointCrossover<IntegerVector>(0)
		);
	}
	
	@Test
	public void testKPoint2() {
		KPointCrossover<IntegerVector> crossover = new KPointCrossover<IntegerVector>(2);
		validateMultiPointCrossover(crossover, 2);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded = new KPointCrossover<BoundedIntegerVector>(2);
		validateMultiPointCrossoverBounded(crossoverBounded, 2);
		
		KPointCrossover<IntegerVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 2);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 2);
	}
	
	@Test
	public void testKPoint3() {
		KPointCrossover<IntegerVector> crossover = new KPointCrossover<IntegerVector>(3);
		validateMultiPointCrossover(crossover, 3);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded = new KPointCrossover<BoundedIntegerVector>(3);
		validateMultiPointCrossoverBounded(crossoverBounded, 3);
		
		KPointCrossover<IntegerVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 3);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 3);
	}
	
	@Test
	public void testKPoint4() {
		KPointCrossover<IntegerVector> crossover = new KPointCrossover<IntegerVector>(4);
		validateMultiPointCrossover(crossover, 4);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded = new KPointCrossover<BoundedIntegerVector>(4);
		validateMultiPointCrossoverBounded(crossoverBounded, 4);
		
		KPointCrossover<IntegerVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 4);
		
		KPointCrossover<BoundedIntegerVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 4);
	}
	
	private void validateMultiPointCrossover(CrossoverOperator<IntegerVector> crossover, int expectedCrossPoints) {
		for (int n = Math.max(2, expectedCrossPoints); n <= 32; n *= 2) {
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
		for (int n = Math.max(2, expectedCrossPoints); n <= 32; n *= 2) {
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
