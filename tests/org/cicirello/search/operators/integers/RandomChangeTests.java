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
import org.cicirello.search.representations.IntegerValued;

/**
 * JUnit 4 test cases for the classes that implement different variations of
 * random value change mutation for mutating integer valued representations.
 */
public class RandomChangeTests {
	
	@Test
	public void testEquals() {
		RandomValueChangeMutation<IntegerValued> r1 = new RandomValueChangeMutation<IntegerValued>(1, 4);
		RandomValueChangeMutation<IntegerValued> r2 = new RandomValueChangeMutation<IntegerValued>(0, 4);
		RandomValueChangeMutation<IntegerValued> r3 = new RandomValueChangeMutation<IntegerValued>(1, 5);
		RandomValueChangeMutation<IntegerValued> r4 = r1.split();
		RandomValueChangeMutation<IntegerValued> r5 = new RandomValueChangeMutation<IntegerValued>(1, 4, 0.0, 1);
		RandomValueChangeMutation<IntegerValued> r6 = new RandomValueChangeMutation<IntegerValued>(1, 4, 0.2, 1);
		RandomValueChangeMutation<IntegerValued> r7 = new RandomValueChangeMutation<IntegerValued>(1, 4, 0.0, 2);
		assertEquals(r1, r4);
		assertEquals(r1.hashCode(), r4.hashCode());
		assertEquals(r1, r5);
		assertEquals(r1.hashCode(), r5.hashCode());
		assertNotEquals(r1, r2);
		assertNotEquals(r1, r3);
		assertNotEquals(r1, r6);
		assertNotEquals(r1, r7);
		assertFalse(r1.equals(null));
		assertFalse(r1.equals("hello"));
		UndoableRandomValueChangeMutation<IntegerValued> u1 = new UndoableRandomValueChangeMutation<IntegerValued>(1, 4);
		UndoableRandomValueChangeMutation<IntegerValued> u2 = new UndoableRandomValueChangeMutation<IntegerValued>(0, 4);
		UndoableRandomValueChangeMutation<IntegerValued> u3 = new UndoableRandomValueChangeMutation<IntegerValued>(1, 5);
		UndoableRandomValueChangeMutation<IntegerValued> u4 = u1.split();
		UndoableRandomValueChangeMutation<IntegerValued> u5 = new UndoableRandomValueChangeMutation<IntegerValued>(1, 4, 0.0, 1);
		UndoableRandomValueChangeMutation<IntegerValued> u6 = new UndoableRandomValueChangeMutation<IntegerValued>(1, 4, 0.2, 1);
		UndoableRandomValueChangeMutation<IntegerValued> u7 = new UndoableRandomValueChangeMutation<IntegerValued>(1, 4, 0.0, 2);
		//assertFalse(r1.equals(u1));
		assertEquals(u1, u4);
		assertEquals(u1.hashCode(), u4.hashCode());
		assertEquals(u1, u5);
		assertEquals(u1.hashCode(), u5.hashCode());
		assertNotEquals(u1, u2);
		assertNotEquals(u1, u3);
		assertFalse(u1.equals(null));
		assertNotEquals(u1, u6);
		assertNotEquals(u1, u7);
		assertFalse(u1.equals(r1));
	}
	
	@Test
	public void testRandomValueChangeMutation() {
		for (int a = 0; a <= 2; a++) {
			for (int b = a+1; b <= a+3; b++) {
				for (double p = 0.0; p <= 1.0; p += 0.25) {
					for (int k = 0; k <= 3; k++) {
						// call helper that runs a set of tests with given a, b, p, k
						testRandomValueChangeMutation(a, b, p, k);
					}
				}
			}
		}
		RandomValueChangeMutation<IntegerValued> original = new RandomValueChangeMutation<IntegerValued>(1, 4);
		testRandomValueChangeMutation(1, 4, 0.0, 1, original);
		testRandomValueChangeMutation(1, 4, 0.5, 0, new RandomValueChangeMutation<IntegerValued>(1, 4, 0.5));
		RandomValueChangeMutation<IntegerValued> s = original.split();
		assertTrue(original != s);
		testRandomValueChangeMutation(1, 4, 0.0, 1, s);
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new RandomValueChangeMutation<IntegerValued>(5, 4)
		);
	}
	
	@Test
	public void testUndoableRandomValueChangeMutation() {
		for (int a = 0; a <= 2; a++) {
			for (int b = a+1; b <= a+3; b++) {
				for (double p = 0.0; p <= 1.0; p += 0.25) {
					for (int k = 0; k <= 3; k++) {
						// call helper that runs a set of tests with given a, b, p, k
						testUndoableRandomValueChangeMutation(a, b, p, k);
						// test the split method
						testUndoableRandomValueChangeMutationSplit(a, b, p, k);
					}
				}
			}
		}
		testUndoableRandomValueChangeMutation(1, 4, 0.0, 1, new UndoableRandomValueChangeMutation<IntegerValued>(1, 4));
		testUndoableRandomValueChangeMutation(1, 4, 0.5, 0, new UndoableRandomValueChangeMutation<IntegerValued>(1, 4, 0.5));
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UndoableRandomValueChangeMutation<IntegerValued>(5, 4)
		);
	}
	
	
	private void testRandomValueChangeMutation(int a, int b, double p, int k) {
		testRandomValueChangeMutation(a, b, p, k, new RandomValueChangeMutation<IntegerValued>(a, b, p, k));
	}
	
	private void testRandomValueChangeMutation(int a, int b, double p, int k, RandomValueChangeMutation<IntegerValued> m) {
		SingleInteger v1 = new SingleInteger((a+b)/2);
		SingleInteger v = v1.copy();
		m.mutate(v1);
		int count = 0;
		int min = k >= 1 || p == 1.0 ? 1 : 0;
		for (int i = 0; i < v1.length(); i++) {
			assertTrue(v1.get(i) >= a);
			assertTrue(v1.get(i) <= b);
			if (v1.get(i) != v.get(i)) count++; 
		}
		assertTrue(count >= min);
		for (int n = 0; n <= k + 3; n++) {
			int[] values = new int[n];
			int next = a;
			for (int i = 0; i < n; i++) {
				values[i] = next;
				next++;
				if (next > b) next = a;
			}
			IntegerVector vn = new IntegerVector(values);
			IntegerVector vm = vn.copy();
			m.mutate(vn);
			count = 0;
			min = k >= n || p == 1.0 ? n : k;
			for (int i = 0; i < n; i++) {
				assertTrue(vn.get(i) >= a);
				assertTrue(vn.get(i) <= b);
				if (vn.get(i) != vm.get(i)) count++; 
			}
			assertTrue(count >= min);
		}
	}
	
	private void testUndoableRandomValueChangeMutation(int a, int b, double p, int k) {
		testUndoableRandomValueChangeMutation(a, b, p, k, new UndoableRandomValueChangeMutation<IntegerValued>(a, b, p, k));
	}
	
	private void testUndoableRandomValueChangeMutation(int a, int b, double p, int k, UndoableRandomValueChangeMutation<IntegerValued> m) {
		SingleInteger v1 = new SingleInteger((a+b)/2);
		SingleInteger v = v1.copy();
		m.mutate(v1);
		int count = 0;
		int min = k >= 1 || p == 1.0 ? 1 : 0;
		for (int i = 0; i < v1.length(); i++) {
			assertTrue(v1.get(i) >= a);
			assertTrue(v1.get(i) <= b);
			if (v1.get(i) != v.get(i)) count++; 
		}
		assertTrue(count >= min);
		m.undo(v1);
		assertEquals(v1,v);
		for (int n = 0; n <= k + 3; n++) {
			int[] values = new int[n];
			int next = a;
			for (int i = 0; i < n; i++) {
				values[i] = next;
				next++;
				if (next > b) next = a;
			}
			IntegerVector vn = new IntegerVector(values);
			IntegerVector vm = vn.copy();
			m.mutate(vn);
			count = 0;
			min = k >= n || p == 1.0 ? n : k;
			for (int i = 0; i < n; i++) {
				assertTrue(vn.get(i) >= a);
				assertTrue(vn.get(i) <= b);
				if (vn.get(i) != vm.get(i)) count++; 
			}
			assertTrue(count >= min);
			m.undo(vn);
			assertEquals(vn,vm);
		}
	}
	
	private void testUndoableRandomValueChangeMutationSplit(int a, int b, double p, int k) {
		UndoableRandomValueChangeMutation<IntegerValued> mutationOriginal = new UndoableRandomValueChangeMutation<IntegerValued>(a, b, p, k);
		for (int i = 0; i < 10; i++) {
			SingleInteger v1 = new SingleInteger((a+b)/2);
			SingleInteger v2 = v1.copy();
			SingleInteger v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableRandomValueChangeMutation<IntegerValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
	}
	
	
}