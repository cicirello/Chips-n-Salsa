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
 
package org.cicirello.search.operators.reals;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.representations.RealValued;

/**
 * JUnit test cases for the classes that implement Initializer for the
 * RealValued classes.
 */
public class RealValuedInitializerTests {
	
	// For tests involving randomness, number of test samples.
	private final int NUM_SAMPLES = 100;
	
	// precision for floating point equals comparisons
	private final double EPSILON = 1e-10;
	
	@Test
	public void testRealValueInitializerEquals() {
		RealValueInitializer f = new RealValueInitializer(2, 3, 0, 5);
		RealValueInitializer g = new RealValueInitializer(2, 3, 0, 5);
		RealValueInitializer f1 = new RealValueInitializer(1, 3, 0, 5);
		RealValueInitializer f2 = new RealValueInitializer(2, 4, 0, 5);
		RealValueInitializer f3 = new RealValueInitializer(2, 3, -1, 5);
		RealValueInitializer f4 = new RealValueInitializer(2, 3, 0, 6);
		assertEquals(f, g);
		assertEquals(f.hashCode(), g.hashCode());
		assertNotEquals(f, f1);
		assertNotEquals(f, f2);
		assertNotEquals(f, f3);
		assertNotEquals(f, f4);
		assertNotEquals(f, null);
		assertNotEquals(f, "hello");
	}
	
	@Test
	public void testRealVectorInitializerEquals() {
		RealVectorInitializer f = new RealVectorInitializer(2, 2, 4, 0, 8);
		RealVectorInitializer g = new RealVectorInitializer(2, 2, 4, 0, 8);
		RealVectorInitializer f1 = new RealVectorInitializer(2, 1, 4, 0, 8);
		RealVectorInitializer f2 = new RealVectorInitializer(2, 2, 5, 0, 8);
		RealVectorInitializer f3 = new RealVectorInitializer(2, 2, 4, 1, 8);
		RealVectorInitializer f4 = new RealVectorInitializer(2, 2, 4, 0, 9);
		assertEquals(f, g);
		assertEquals(f.hashCode(), g.hashCode());
		assertNotEquals(f, f1);
		assertNotEquals(f, f2);
		assertNotEquals(f, f3);
		assertNotEquals(f, f4);
		assertNotEquals(f, null);
		assertNotEquals(f, "hello");
		f = new RealVectorInitializer(2, 2, 4);
		g = new RealVectorInitializer(2, 2, 4);
		f1 = new RealVectorInitializer(2, 1, 4);
		f2 = new RealVectorInitializer(2, 2, 5);
		f3 = new RealVectorInitializer(2, 2, 4, 0, 8);
		assertEquals(f, g);
		assertEquals(f.hashCode(), g.hashCode());
		assertNotEquals(f, f1);
		assertNotEquals(f, f2);
		assertNotEquals(f, f3);
	}
	
	@Test
	public void testBoundedRealEquals() {
		RealValueInitializer f = new RealValueInitializer(2, 2 + Math.ulp(2), 2, 2);
		SingleReal g1 = f.createCandidateSolution();
		SingleReal g2 = f.createCandidateSolution();
		SingleReal h = new SingleReal(2);
		assertFalse(g1.equals(h));
		assertFalse(g1.equals(null));
		assertFalse(g1.equals("hello"));
		assertEquals(g1, g2);
		assertEquals(g1.hashCode(), g2.hashCode());
		RealValueInitializer f2 = new RealValueInitializer(2, 2 + Math.ulp(2), 2, 2);
		SingleReal g3 = f2.createCandidateSolution();
		assertEquals(g1, g3);
		assertEquals(g1.hashCode(), g3.hashCode());
		f2 = new RealValueInitializer(2, 2 + Math.ulp(2), 2, 3);
		assertNotEquals(g1, f2.createCandidateSolution());
		f2 = new RealValueInitializer(2, 2 + Math.ulp(2), 0, 2);
		assertNotEquals(g1, f2.createCandidateSolution());
	}
	
	@Test
	public void testBoundedRealVectorEquals() {
		RealVectorInitializer f = new RealVectorInitializer(new double[] {2, 2}, new double[] {2 + Math.ulp(2), 2 + Math.ulp(2)}, new double[] {2, 2}, new double[] {2, 2});
		RealVector g1 = f.createCandidateSolution();
		RealVector g2 = f.createCandidateSolution();
		RealVector h = new RealVector(new double[] {2});
		assertNotEquals(g1, h);
		assertEquals(g1, g2);
		assertEquals(g1.hashCode(), g2.hashCode());
		RealVectorInitializer f2 = new RealVectorInitializer(new double[] {2, 2}, new double[] {2 + Math.ulp(2), 2 + Math.ulp(2)}, new double[] {2, 2}, new double[] {2, 2});
		RealVector g3 = f2.createCandidateSolution();
		assertEquals(g1, g3);
		assertEquals(g1.hashCode(), g3.hashCode());
		f2 = new RealVectorInitializer(new double[] {2, 2}, new double[] {2 + Math.ulp(2), 2 + Math.ulp(2)}, new double[] {2, 2}, new double[] {3, 3});
		assertNotEquals(g1, f2.createCandidateSolution());
		f2 = new RealVectorInitializer(new double[] {2, 2}, new double[] {2 + Math.ulp(2), 2 + Math.ulp(2)}, new double[] {0, 0}, new double[] {2, 2});
		assertNotEquals(g1, f2.createCandidateSolution());
	}
	
	@Test
	public void testUnivariate() {
		SingleReal theClass = new SingleReal();
		double a = 3.0;
		double b = 11.0;
		RealValueInitializer f = new RealValueInitializer(a, b);
		RealValueInitializer fs = f.split();
		assertEquals(f, fs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			SingleReal copy = g.copy(); 
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
			g.set(0, a - 1);
			assertEquals(a-1, g.get(0), EPSILON);
			g.set(0, b + 1);
			assertEquals(b+1, g.get(0), EPSILON);
		}
		a = -13.0;
		b = -2.0;
		f = new RealValueInitializer(a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			SingleReal copy = g.copy(); 
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		a = -5.0;
		b = 5.0;
		f = new RealValueInitializer(a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			SingleReal copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealValueInitializer(5, 5)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealValueInitializer(5, 5, 2, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealValueInitializer(5, 6, 7, 6)
		);
	}
	
	@Test
	public void testBoundedUnivariate() {
		double a = 3.0;
		double b = 11.0;
		double min = 0;
		double max = 20;
		RealValueInitializer f = new RealValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			g.set(0, min - 1);
			assertEquals(min, g.get(0), EPSILON);
			g.set(0, max + 1);
			assertEquals(max, g.get(0), EPSILON);
			g.set(0, 10);
			assertEquals(10, g.get(0), EPSILON);
			SingleReal copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new RealValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			SingleReal copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 1;
		f = new RealValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleReal g = f.createCandidateSolution();
			assertTrue(g.get(0) <= max && g.get(0) >= min);
			SingleReal copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
	}
	
	@Test
	public void testMultivariate() {
		RealVector theClass = new RealVector(10);
		double a = 3;
		double b = 11;
		int n = 1;
		RealVectorInitializer f = new RealVectorInitializer(n, a, b);
		RealVectorInitializer fs = f.split();
		assertEquals(f, fs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		a = -13;
		b = -2;
		f = new RealVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		a = -5;
		b = 5;
		f = new RealVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertTrue(g.get(0) < b && g.get(0) >= a);
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		n = 10;
		a = 3;
		b = 11;
		f = new RealVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < b && g.get(j) >= a);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		a = -13;
		b = -2;
		f = new RealVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < b && g.get(j) >= a);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		a = -5;
		b = 5;
		f = new RealVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < b && g.get(j) >= a);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		double[] left = {  3, -13, -5, 4};
		double[] right = {11,  -2,  5, 4.1};
		n = 4;
		f = new RealVectorInitializer(left, right);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(theClass.getClass(), g.getClass());
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
			for (int j = 0; j < n; j++) {
				g.set(j, left[j] - 1);
				assertEquals(left[j]-1, g.get(j), EPSILON);
				g.set(j, right[j] + 1);
				assertEquals(right[j]+1, g.get(j), EPSILON);
			}
		}
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(1, 5, 5)
		);
		final double[] a1 = { 1 };
		final double[] a2 = { 2, 1 };
		final double[] b2 = { 2, 3 };
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a1, b2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(1, 5, 5, 2, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(1, 4, 5, 7, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a1, b2, 2, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, 7, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, 2, 6)
		);
		final double[] min1 = { 1 };
		final double[] min2 = { 1, 1 };
		final double[] max2 = { 2, 2 };
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a1, b2, min2, max2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, min1, max2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, min2, max2)
		);
		a2[0] = 1;
		min2[0] = 3;
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, min2, max2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RealVectorInitializer(a2, b2, new double[] {1}, new double[] {5})
		);
	}
	
	@Test
	public void testBoundedMultivariate() {
		double a = 4;
		double b = 10;
		int n = 1;
		double min = 2;
		double max = 20;
		RealVectorInitializer f = new RealVectorInitializer(n, a, b, min, max);
		RealVectorInitializer fs = f.split();
		assertEquals(f, fs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			assertTrue(g.get(0) < b && g.get(0) >= a);
			g.set(0, min - 1);
			assertEquals(min, g.get(0), EPSILON);
			g.set(0, max + 1);
			assertEquals(max, g.get(0), EPSILON);
			g.set(0, 10);
			assertEquals(10, g.get(0), EPSILON);
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new RealVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			assertTrue(g.get(0) < b && g.get(0) >= a);
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 1;
		f = new RealVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			assertTrue(g.get(0) <= max && g.get(0) >= min);
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		
		n = 10;
		min = 2;
		max = 20;
		f = new RealVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < b && g.get(j) >= a);
				g.set(j, min - 1);
				assertEquals(min, g.get(j), EPSILON);
				g.set(j, max + 1);
				assertEquals(max, g.get(j), EPSILON);
				g.set(j, 10);
				assertEquals(10, g.get(j), EPSILON);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new RealVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < b && g.get(j) >= a);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 1;
		f = new RealVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) <= max && g.get(j) >= min);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		
		min = 6;
		max = 15;
		double[] left = {  7,   0,  0,  7, 8};
		double[] right = {11,  25, 11, 25, 8.1};
		n = 5;
		f = new RealVectorInitializer(left, right, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
				assertTrue(g.get(j) <= max && g.get(j) >= min);
				g.set(j, min - 1);
				assertEquals(min, g.get(j), EPSILON);
				g.set(j, max + 1);
				assertEquals(max, g.get(j), EPSILON);
				g.set(j, 10);
				assertEquals(10, g.get(j), EPSILON);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
		double[] mins = { 6, -15, -15, -15, 8};
		double[] maxs = {15,  -6,  15, 15,  8};
		left = new double[]  {8,  -18, 5, -30, 0};
		right = new double[] {12, -10, 18, 30, 20};
		f = new RealVectorInitializer(left, right, mins, maxs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			RealVector g = f.createCandidateSolution();
			assertEquals(n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
				assertTrue(g.get(j) <= maxs[j] && g.get(j) >= mins[j]);
				g.set(j, mins[j] - 1);
				assertEquals(mins[j], g.get(j), EPSILON);
				g.set(j, maxs[j] + 1);
				assertEquals(maxs[j], g.get(j), EPSILON);
				g.set(j, (mins[j]+maxs[j])/2);
				assertEquals((mins[j]+maxs[j])/2, g.get(j), EPSILON);
			}
			RealVector copy = g.copy();
			assertTrue(copy != g);
			assertEquals(g, copy);
			assertEquals(g.getClass(), copy.getClass());
		}
	}
}
