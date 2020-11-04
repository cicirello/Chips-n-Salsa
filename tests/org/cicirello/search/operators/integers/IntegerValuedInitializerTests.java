/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
import org.cicirello.search.representations.SingleInteger;
import org.cicirello.search.representations.IntegerVector;

/**
 * JUnit 4 test cases for the classes that implement Initializer for the
 * IntegerValued classes.
 */
public class IntegerValuedInitializerTests {
	
	// For tests involving randomness, number of test samples.
	private final int NUM_SAMPLES = 100;
	
	@Test
	public void testBoundedIntegerEquals() {
		IntegerValueInitializer f = new IntegerValueInitializer(2, 3, 2, 2);
		SingleInteger g1 = f.createCandidateSolution();
		SingleInteger g2 = f.createCandidateSolution();
		SingleInteger h = new SingleInteger(2);
		assertNotEquals(g1, h);
		assertEquals(g1, g2);
		assertEquals(g1.hashCode(), g2.hashCode());
		IntegerValueInitializer f2 = new IntegerValueInitializer(2, 3, 2, 2);
		SingleInteger g3 = f2.createCandidateSolution();
		assertNotEquals(g1, g3);
	}
	
	@Test
	public void testIntegerUnivariate() {
		SingleInteger theClass = new SingleInteger();
		int a = 3;
		int b = 11;
		IntegerValueInitializer f = new IntegerValueInitializer(a, b);
		IntegerValueInitializer fs = f.split();
		assertEquals(f, fs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("positive interval", g.get() < b && g.get() >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
			g.set(a - 1);
			assertEquals("verify unbounded set", a-1, g.get());
			g.set(b + 1);
			assertEquals("verify unbounded set", b+1, g.get());
		}
		a = -13;
		b = -2;
		f = new IntegerValueInitializer(a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("negative interval", g.get() < b && g.get() >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		a = -5;
		b = 5;
		f = new IntegerValueInitializer(a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("interval surrounding 0", g.get() < b && g.get() >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new IntegerValueInitializer(5, 5)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new IntegerValueInitializer(5, 5, 2, 6)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new IntegerValueInitializer(5, 6, 7, 6)
		);
	}
	
	@Test
	public void testBoundedIntegerUnivariate() {
		int a = 3;
		int b = 11;
		int min = 0;
		int max = 20;
		IntegerValueInitializer f = new IntegerValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("bounds wider than interval", g.get() < b && g.get() >= a);
			g.set(min - 1);
			assertEquals("verify lower bound works on set", min, g.get());
			g.set(max + 1);
			assertEquals("verify upper bound works on set", max, g.get());
			g.set(10);
			assertEquals("verify within bounds set", 10, g.get());
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new IntegerValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("bounds equal to interval", g.get() < b && g.get() >= a);
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 2;
		f = new IntegerValueInitializer(a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			SingleInteger g = f.createCandidateSolution();
			assertTrue("bounds narrower than interval", g.get() <= max && g.get() >= min);
			SingleInteger copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
	}
	
	
	@Test
	public void testIntegerMultivariate() {
		IntegerVector theClass = new IntegerVector(10);
		int a = 3;
		int b = 11;
		int n = 1;
		IntegerVectorInitializer f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertTrue("positive interval, one var", g.get(0) < b && g.get(0) >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		a = -13;
		b = -2;
		f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertTrue("negative interval, one var", g.get(0) < b && g.get(0) >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		a = -5;
		b = 5;
		f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertTrue("interval surrounding 0, one var", g.get(0) < b && g.get(0) >= a);
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		n = 10;
		a = 3;
		b = 11;
		f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("positive interval, ten vars", g.get(j) < b && g.get(j) >= a);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		a = -13;
		b = -2;
		f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("negative interval, ten vars", g.get(j) < b && g.get(j) >= a);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		a = -5;
		b = 5;
		f = new IntegerVectorInitializer(n, a, b);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("interval surrounding 0, ten vars", g.get(j) < b && g.get(j) >= a);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		int[] left = {  3, -13, -5, 4};
		int[] right = {11,  -2,  5, 5};
		n = 4;
		f = new IntegerVectorInitializer(left, right);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify runtime class is correct", theClass.getClass(), g.getClass());
			assertEquals("verify number of input variables", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("four vars different intervals", g.get(j) < right[j] && g.get(j) >= left[j]);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
			for (int j = 0; j < n; j++) {
				g.set(j, left[j] - 1);
				assertEquals("verify unbounded set", left[j]-1, g.get(j));
				g.set(j, right[j] + 1);
				assertEquals("verify unbounded set", right[j]+1, g.get(j));
			}
		}
	}
	
	
	
	@Test
	public void testBoundedIntegerMultivariate() {
		int a = 4;
		int b = 10;
		int n = 1;
		int min = 2;
		int max = 20;
		IntegerVectorInitializer f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			assertTrue("bounds wider than interval", g.get(0) < b && g.get(0) >= a);
			g.set(0, min - 1);
			assertEquals("verify lower bound works on set", min, g.get(0));
			g.set(0, max + 1);
			assertEquals("verify upper bound works on set", max, g.get(0));
			g.set(0, 10);
			assertEquals("verify within bounds set", 10, g.get(0));
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			assertTrue("bounds equal to interval", g.get(0) < b && g.get(0) >= a);
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 1;
		f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			assertTrue("bounds narrower than interval", g.get(0) <= max && g.get(0) >= min);
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		
		n = 10;
		min = 2;
		max = 20;
		f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("bounds wider than interval", g.get(j) < b && g.get(j) >= a);
				g.set(j, min - 1);
				assertEquals("verify lower bound works on set", min, g.get(j));
				g.set(j, max + 1);
				assertEquals("verify upper bound works on set", max, g.get(j));
				g.set(j, 10);
				assertEquals("verify within bounds set", 10, g.get(j));
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a;
		max = b;
		f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("bounds equal to interval", g.get(j) < b && g.get(j) >= a);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		min = a + 1;
		max = b - 1;
		f = new IntegerVectorInitializer(n, a, b, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("bounds narrower than interval", g.get(j) <= max && g.get(j) >= min);
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		
		min = 6;
		max = 15;
		int[] left = {  7,   0,  0,  7, 8};
		int[] right = {11,  25, 11, 25, 9};
		n = 5;
		f = new IntegerVectorInitializer(left, right, min, max);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("verify in interval, different intervals", g.get(j) < right[j] && g.get(j) >= left[j]);
				assertTrue("verify in bounds, different intervals", g.get(j) <= max && g.get(j) >= min);
				g.set(j, min - 1);
				assertEquals("verify lower bound works on set", min, g.get(j));
				g.set(j, max + 1);
				assertEquals("verify upper bound works on set", max, g.get(j));
				g.set(j, 10);
				assertEquals("verify within bounds set", 10, g.get(j));
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
		int[] mins = { 6, -15, -15, -15, 8};
		int[] maxs = {15,  -6,  15, 15,  8};
		left = new int[]  {8,  -18, 5, -30, 0};
		right = new int[] {12, -10, 18, 30, 20};
		f = new IntegerVectorInitializer(left, right, mins, maxs);
		for (int i = 0; i < NUM_SAMPLES; i++) {
			IntegerVector g = f.createCandidateSolution();
			assertEquals("verify length", n, g.length());
			for (int j = 0; j < n; j++) {
				assertTrue("verify in interval, different bounds", g.get(j) < right[j] && g.get(j) >= left[j]);
				assertTrue("verify in bounds, different bounds", g.get(j) <= maxs[j] && g.get(j) >= mins[j]);
				g.set(j, mins[j] - 1);
				assertEquals("verify lower bound works on set", mins[j], g.get(j));
				g.set(j, maxs[j] + 1);
				assertEquals("verify upper bound works on set", maxs[j], g.get(j));
				g.set(j, (mins[j]+maxs[j])/2);
				assertEquals("verify within bounds set", (mins[j]+maxs[j])/2, g.get(j));
			}
			IntegerVector copy = g.copy();
			assertTrue("copy should be new object", copy != g);
			assertEquals("copy should be identical to original", g, copy);
			assertEquals("verify runtime class of copy", g.getClass(), copy.getClass());
		}
	}
	
}