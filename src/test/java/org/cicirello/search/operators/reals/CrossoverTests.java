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
 
package org.cicirello.search.operators.reals;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.representations.BoundedRealVector;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * JUnit tests for crossover operators for RealVectors.
 */
public class CrossoverTests {
	
	@Test
	public void testUniformCrossover() {
		UniformCrossover<RealVector> crossover0 = new UniformCrossover<RealVector>(0.0);
		UniformCrossover<RealVector> crossover1 = new UniformCrossover<RealVector>(1.0);
		UniformCrossover<RealVector> crossoverDefault = new UniformCrossover<RealVector>();
		UniformCrossover<RealVector> crossover05 = new UniformCrossover<RealVector>(0.5);
		
		for (int n = 32; n <= 128; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			RealVector v1 = new RealVector(a1);
			RealVector v2 = new RealVector(a2);
			RealVector copy1 = v1.copy();
			RealVector copy2 = v2.copy();
			
			crossover0.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new RealVector(a1);
			v2 = new RealVector(a2);
			crossoverDefault.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new RealVector(a1);
			v2 = new RealVector(a2);
			crossover05.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
		
		UniformCrossover<RealVector> crossover0_s = crossover0.split();
		UniformCrossover<RealVector> crossover1_s = crossover1.split();
		UniformCrossover<RealVector> crossoverDefault_s = crossoverDefault.split();
		UniformCrossover<RealVector> crossover05_s = crossover05.split();
		
		for (int n = 32; n <= 128; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			RealVector v1 = new RealVector(a1);
			RealVector v2 = new RealVector(a2);
			RealVector copy1 = v1.copy();
			RealVector copy2 = v2.copy();
			
			crossover0_s.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1_s.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new RealVector(a1);
			v2 = new RealVector(a2);
			crossoverDefault_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new RealVector(a1);
			v2 = new RealVector(a2);
			crossover05_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	@Test
	public void testUniformCrossoverBounded() {
		UniformCrossover<BoundedRealVector> crossover0 = new UniformCrossover<BoundedRealVector>(0.0);
		UniformCrossover<BoundedRealVector> crossover1 = new UniformCrossover<BoundedRealVector>(1.0);
		UniformCrossover<BoundedRealVector> crossoverDefault = new UniformCrossover<BoundedRealVector>();
		UniformCrossover<BoundedRealVector> crossover05 = new UniformCrossover<BoundedRealVector>(0.5);
		
		for (int n = 32; n <= 128; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedRealVector v1 = new BoundedRealVector(a1, 0, 5);
			BoundedRealVector v2 = new BoundedRealVector(a2, 0, 5);
			BoundedRealVector copy1 = v1.copy();
			BoundedRealVector copy2 = v2.copy();
			
			crossover0.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new BoundedRealVector(a1, 0, 5);
			v2 = new BoundedRealVector(a2, 0, 5);
			crossoverDefault.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new BoundedRealVector(a1, 0, 5);
			v2 = new BoundedRealVector(a2, 0, 5);
			crossover05.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
		
		UniformCrossover<BoundedRealVector> crossover0_s = crossover0.split();
		UniformCrossover<BoundedRealVector> crossover1_s = crossover1.split();
		UniformCrossover<BoundedRealVector> crossoverDefault_s = crossoverDefault.split();
		UniformCrossover<BoundedRealVector> crossover05_s = crossover05.split();
		
		for (int n = 32; n <= 128; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedRealVector v1 = new BoundedRealVector(a1, 0, 5);
			BoundedRealVector v2 = new BoundedRealVector(a2, 0, 5);
			BoundedRealVector copy1 = v1.copy();
			BoundedRealVector copy2 = v2.copy();
			
			crossover0_s.cross(v1, v2);
			assertEquals(copy1, v1);
			assertEquals(copy2, v2);
			
			crossover1_s.cross(v1, v2);
			assertEquals(copy2, v1);
			assertEquals(copy1, v2);
			
			v1 = new BoundedRealVector(a1, 0, 5);
			v2 = new BoundedRealVector(a2, 0, 5);
			crossoverDefault_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
			
			v1 = new BoundedRealVector(a1, 0, 5);
			v2 = new BoundedRealVector(a2, 0, 5);
			crossover05_s.cross(v1, v2);
			assertNotEquals(copy1, v1);
			assertNotEquals(copy2, v2);
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	@Test
	public void testSinglePoint() {
		SinglePointCrossover<RealVector> crossover = new SinglePointCrossover<RealVector>();
		validateSinglePointCrossover(crossover);
		
		SinglePointCrossover<BoundedRealVector> crossoverBounded = new SinglePointCrossover<BoundedRealVector>();
		validateSinglePointCrossoverBounded(crossoverBounded);
		
		SinglePointCrossover<RealVector> crossover2 = crossover.split();
		validateSinglePointCrossover(crossover2);
		
		SinglePointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateSinglePointCrossoverBounded(crossoverBounded2);
		
	}
	
	@Test
	public void testTwoPoint() {
		TwoPointCrossover<RealVector> crossover = new TwoPointCrossover<RealVector>();
		validateMultiPointCrossover(crossover, 2);
		
		TwoPointCrossover<BoundedRealVector> crossoverBounded = new TwoPointCrossover<BoundedRealVector>();
		validateMultiPointCrossoverBounded(crossoverBounded, 2);
		
		TwoPointCrossover<RealVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 2);
		
		TwoPointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 2);
	}
	
	@Test
	public void testKPoint1() {
		KPointCrossover<RealVector> crossover = new KPointCrossover<RealVector>(1);
		validateMultiPointCrossover(crossover, 1);
		
		KPointCrossover<BoundedRealVector> crossoverBounded = new KPointCrossover<BoundedRealVector>(1);
		validateMultiPointCrossoverBounded(crossoverBounded, 1);
		
		KPointCrossover<RealVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 1);
		
		KPointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 1);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new KPointCrossover<RealVector>(0)
		);
	}
	
	@Test
	public void testKPoint2() {
		KPointCrossover<RealVector> crossover = new KPointCrossover<RealVector>(2);
		validateMultiPointCrossover(crossover, 2);
		
		KPointCrossover<BoundedRealVector> crossoverBounded = new KPointCrossover<BoundedRealVector>(2);
		validateMultiPointCrossoverBounded(crossoverBounded, 2);
		
		KPointCrossover<RealVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 2);
		
		KPointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 2);
	}
	
	@Test
	public void testKPoint3() {
		KPointCrossover<RealVector> crossover = new KPointCrossover<RealVector>(3);
		validateMultiPointCrossover(crossover, 3);
		
		KPointCrossover<BoundedRealVector> crossoverBounded = new KPointCrossover<BoundedRealVector>(3);
		validateMultiPointCrossoverBounded(crossoverBounded, 3);
		
		KPointCrossover<RealVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 3);
		
		KPointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 3);
	}
	
	@Test
	public void testKPoint4() {
		KPointCrossover<RealVector> crossover = new KPointCrossover<RealVector>(4);
		validateMultiPointCrossover(crossover, 4);
		
		KPointCrossover<BoundedRealVector> crossoverBounded = new KPointCrossover<BoundedRealVector>(4);
		validateMultiPointCrossoverBounded(crossoverBounded, 4);
		
		KPointCrossover<RealVector> crossover2 = crossover.split();
		validateMultiPointCrossover(crossover2, 4);
		
		KPointCrossover<BoundedRealVector> crossoverBounded2 = crossoverBounded.split();
		validateMultiPointCrossoverBounded(crossoverBounded2, 4);
	}
	
	private void validateMultiPointCrossover(CrossoverOperator<RealVector> crossover, int expectedCrossPoints) {
		for (int n = Math.max(2, expectedCrossPoints); n <= 32; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			RealVector v1 = new RealVector(a1);
			RealVector v2 = new RealVector(a2);
			crossover.cross(v1, v2);
			assertEquals(expectedCrossPoints, countCrossPoints(v1, 1));
			assertEquals(expectedCrossPoints, countCrossPoints(v2, 2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateMultiPointCrossoverBounded(CrossoverOperator<BoundedRealVector> crossover, int expectedCrossPoints) {
		for (int n = Math.max(2, expectedCrossPoints); n <= 32; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedRealVector v1 = new BoundedRealVector(a1, 0, 5);
			BoundedRealVector v2 = new BoundedRealVector(a2, 0, 5);
			crossover.cross(v1, v2);
			assertEquals(expectedCrossPoints, countCrossPoints(v1, 1));
			assertEquals(expectedCrossPoints, countCrossPoints(v2, 2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateSinglePointCrossover(CrossoverOperator<RealVector> crossover) {
		for (int n = 2; n <= 32; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			RealVector v1 = new RealVector(a1);
			RealVector v2 = new RealVector(a2);
			crossover.cross(v1, v2);
			assertEquals(1, countCrossPoints(v1));
			assertEquals(1, countCrossPoints(v2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}
	
	private void validateSinglePointCrossoverBounded(CrossoverOperator<BoundedRealVector> crossover) {
		for (int n = 2; n <= 32; n *= 2) {
			double[] a1 = new double[n];
			double[] a2 = new double[n];
			for (int i = 0; i < n; i++) {
				a1[i] = 1;
				a2[i] = 2;
			}
			BoundedRealVector v1 = new BoundedRealVector(a1, 0, 5);
			BoundedRealVector v2 = new BoundedRealVector(a2, 0, 5);
			crossover.cross(v1, v2);
			assertEquals(1, countCrossPoints(v1));
			assertEquals(1, countCrossPoints(v2));
			assertTrue(areSiblings(v1, v2, 1, 2));
		}
	}

	private int countCrossPoints(RealVector v1) {
		int count = 0;
		for (int i = 1; i < v1.length(); i++) {
			if (v1.get(i) != v1.get(i-1)) count++;
		}
		return count;
	}
	
	private int countCrossPoints(RealVector v1, double shouldStartWith) {
		int count = v1.get(0) != shouldStartWith ? 1 : 0;
		for (int i = 1; i < v1.length(); i++) {
			if (v1.get(i) != v1.get(i-1)) count++;
		}
		return count;
	}
	
	private boolean areSiblings(RealVector v1, RealVector v2, double value1, double value2) {
		if (v1.length() != v2.length()) return false;
		for (int i = 0; i < v1.length(); i++) {
			if (v1.get(i) == v2.get(i)) return false;
			if (v1.get(i) != value1 && v1.get(i) != value2) return false;
			if (v2.get(i) != value1 && v2.get(i) != value2) return false;
		}
		return true;
	}
}
