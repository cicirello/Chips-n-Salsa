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
 
package org.cicirello.search.operators.reals;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.representations.RealValued;

/**
 * JUnit 4 test cases for the classes that implement different variations of
 * Gaussian mutation for mutating floating-point function parameters.
 */
public class GaussianMutationTests {
	
	// precision used in floating-point comparisons
	private static final double EPSILON = 1e-10;
	
	// We don't test the distribution of results.
	// Instead, we simply verify that mutation is capable of both increasing
	// and decreasing values.  This constant controls the max number of trials
	// executed in verifying this.  E.g., pass if at least 1 out of MAX_TRIALS
	// increases value, and if at least 1 out of MAX_TRIALS decreases.
	// A Gaussian is symmetric about 0.0, so approximately half of mutations 
	// should decrease and approximately half should increase.
	private static final int MAX_TRIALS = 100;
	
	@Test
	public void testEquals() {
		GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation(1);
		GaussianMutation<RealValued> g2 = GaussianMutation.createGaussianMutation(1);
		GaussianMutation<RealValued> g3 = GaussianMutation.createGaussianMutation(2);
		assertEquals(g1, g2);
		assertEquals(g1.hashCode(), g2.hashCode());
		assertNotEquals(g1, g3);
		assertFalse(g1.equals(null));
		assertFalse(g1.equals("hello"));
		GaussianMutation<RealValued> p1 = GaussianMutation.createGaussianMutation(1, 1);
		GaussianMutation<RealValued> p2 = GaussianMutation.createGaussianMutation(2, 1);
		GaussianMutation<RealValued> p3 = GaussianMutation.createGaussianMutation(1, 2);
		GaussianMutation<RealValued> p4 = GaussianMutation.createGaussianMutation(1, 0.5);
		GaussianMutation<RealValued> p5 = GaussianMutation.createGaussianMutation(1, 0.25);
		assertNotEquals(p1, p2);
		assertNotEquals(p1, p3);
		assertNotEquals(p4, p5);
		assertNotEquals(p1, g1);
		assertNotEquals(p1, g3);
		assertFalse(p1.equals(null));
		p1 = UndoableGaussianMutation.createGaussianMutation(1, 1);
		p2 = UndoableGaussianMutation.createGaussianMutation(2, 1);
		p3 = UndoableGaussianMutation.createGaussianMutation(1, 2);
		p4 = UndoableGaussianMutation.createGaussianMutation(1, 0.5);
		p5 = UndoableGaussianMutation.createGaussianMutation(1, 0.25);
		assertNotEquals(p1, p2);
		assertNotEquals(p1, p3);
		assertNotEquals(p4, p5);
		assertNotEquals(p1, g1);
		assertNotEquals(p1, g3);
		assertFalse(p1.equals(null));
	}
	
	@Test
	public void testToArray() {
		GaussianMutation<RealValued> u = GaussianMutation.createGaussianMutation(2);
		double[] a = u.toArray(null);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		a = u.toArray(new double[2]);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		a[0] = 5;
		double[] b = a;
		a = u.toArray(a);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		assertTrue(a==b);
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> GaussianMutation.createGaussianMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> GaussianMutation.createGaussianMutation(1.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableGaussianMutation.createGaussianMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableGaussianMutation.createGaussianMutation(1.0, 0.0)
		);
	}
	
	@Test
	public void testGaussianMutation1() {
		GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation();
		assertEquals("default sigma = 1.0", 1.0, g1.get(0), EPSILON);
		verifyMutate1(g1);
		
		GaussianMutation<RealValued> g5 = GaussianMutation.createGaussianMutation(5.0);
		assertEquals("verifying sigma initialized correctly", 5.0, g5.get(0), EPSILON);
		verifyMutate1(g5);
		
		UndoableGaussianMutation<RealValued> g1u = UndoableGaussianMutation.createGaussianMutation();
		assertEquals("verifying sigma initialized correctly", 1.0, g1u.get(0), EPSILON);
		verifyMutate1(g1u);
		verifyUndo(g1u);
		
		UndoableGaussianMutation<RealValued> g5u = UndoableGaussianMutation.createGaussianMutation(5.0);
		assertEquals("verifying sigma initialized correctly", 5.0, g5u.get(0), EPSILON);
		verifyMutate1(g5u);
		verifyUndo(g5u);
		
		UndoableGaussianMutation<RealValued> g5copyU = g5u.split();
		assertEquals("verifying sigma initialized correctly", 5.0, g5copyU.get(0), EPSILON);
		assertEquals("Verify split", g5u, g5copyU);
		assertEquals("hashCode of equal objects should be equal", g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		
		g5copyU = g5u.copy();
		assertEquals("verifying sigma initialized correctly", 5.0, g5copyU.get(0), EPSILON);
		assertEquals("Verify copy method", g5u, g5copyU);
		assertEquals("hashCode of equal objects should be equal", g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		
		GaussianMutation<RealValued> g5split = g5.split();
		assertEquals("verifying sigma initialized correctly", 5.0, g5split.get(0), EPSILON);
		assertEquals("Verify split", g5, g5split);
		assertTrue("Split should return a new object", g5 != g5split);
		assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
		verifyMutate1(g5split);
		
		GaussianMutation<RealValued> g5copyM = g5.copy();
		assertEquals("verifying sigma initialized correctly", 5.0, g5copyM.get(0), EPSILON);
		assertEquals("Verify split", g5, g5copyM);
		assertTrue("Split should return a new object", g5 != g5copyM);
		assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copyM.hashCode());
		verifyMutate1(g5copyM);
		
		GaussianMutation<RealValued> g3 = g5;
		g3.set(0, 3.0);
		assertEquals("verifying sigma changed correctly", 3.0, g3.get(0), EPSILON);
		verifyMutate1(g3);
	}
	
	@Test
	public void testPartialGaussianMutation1() {
		for (int k = 1; k <= 8; k++) {
			UndoableGaussianMutation<RealValued> g1 = UndoableGaussianMutation.createGaussianMutation(1.0, k);
			assertEquals("default sigma = 1.0", 1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			
			UndoableGaussianMutation<RealValued> g5 = UndoableGaussianMutation.createGaussianMutation(5.0, k);
			assertEquals("verifying sigma initialized correctly", 5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			
			GaussianMutation<RealValued> g5dis = GaussianMutation.createGaussianMutation(5.0, k);
			assertEquals("verifying sigma initialized correctly", 5.0, g5dis.get(0), EPSILON);
			assertNotEquals("Verify undo disabled", g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableGaussianMutation<RealValued> g5copy = g5.copy();
			assertEquals("verifying sigma initialized correctly", 5.0, g5copy.get(0), EPSILON);
			assertEquals("Verify copy method", g5, g5copy);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			
			GaussianMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals("verifying sigma initialized correctly", 5.0, g5copyDis.get(0), EPSILON);
			assertEquals("Verify copy method", g5dis, g5copyDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableGaussianMutation<RealValued> g5split = g5.split();
			assertEquals("verifying sigma initialized correctly", 5.0, g5split.get(0), EPSILON);
			assertEquals("Verify split", g5, g5split);
			assertTrue("Split should return a new object", g5 != g5split);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			
			GaussianMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals("verifying sigma initialized correctly", 5.0, g5splitDis.get(0), EPSILON);
			assertEquals("Verify split", g5dis, g5splitDis);
			assertTrue("Split should return a new object", g5dis != g5splitDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableGaussianMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals("verifying sigma changed correctly", 3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
		}
		for (double k = 0.25; k <= 1.1; k+=0.25) {
			UndoableGaussianMutation<RealValued> g1 = UndoableGaussianMutation.createGaussianMutation(1.0, k);
			assertEquals("default sigma = 1.0", 1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			
			UndoableGaussianMutation<RealValued> g5 = UndoableGaussianMutation.createGaussianMutation(5.0, k);
			assertEquals("verifying sigma initialized correctly", 5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			
			GaussianMutation<RealValued> g5dis = GaussianMutation.createGaussianMutation(5.0, k);
			assertEquals("verifying sigma initialized correctly", 5.0, g5dis.get(0), EPSILON);
			assertNotEquals("Verify undo disabled", g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableGaussianMutation<RealValued> g5copy = g5.copy();
			assertEquals("verifying sigma initialized correctly", 5.0, g5copy.get(0), EPSILON);
			assertEquals("Verify copy constructor", g5, g5copy);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			
			GaussianMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals("verifying sigma initialized correctly", 5.0, g5copyDis.get(0), EPSILON);
			assertEquals("Verify copy method", g5dis, g5copyDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableGaussianMutation<RealValued> g5split = g5.split();
			assertEquals("verifying sigma initialized correctly", 5.0, g5split.get(0), EPSILON);
			assertEquals("Verify split", g5, g5split);
			assertTrue("Split should return a new object", g5 != g5split);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			
			GaussianMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals("verifying sigma initialized correctly", 5.0, g5splitDis.get(0), EPSILON);
			assertEquals("Verify split", g5dis, g5splitDis);
			assertTrue("Split should return a new object", g5dis != g5splitDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableGaussianMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals("verifying sigma changed correctly", 3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
		}
	}
	
	
	
	
	private void verifyMutate1(GaussianMutation<RealValued> m) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials", countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials", countHigh > 0);
		for (int j = 0; j < 5; j++) {
			double[] v = new double[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9.0 - k;
			}
			int[] low = new int[j];
			int[] high = new int[j];
			for (int i = 0; i < MAX_TRIALS; i++) {
				RealVector f = new RealVector(v.clone());
				m.mutate(f);
				boolean done = true;
				for (int k = 0; k < j; k++) {
					if (f.get(k) < v[k]) low[k]++;
					if (f.get(k) > v[k]) high[k]++;
					if (low[k] == 0 || high[k] == 0) done = false;
				}
				if (done) break;
			}
			for (int k = 0; k < j; k++) {
				assertTrue("Mutation failed to decrease value at index " + k, low[k] > 0);
				assertTrue("Mutation failed to increase value at index " + k, high[k] > 0);
			}
		}
	}
	
	private void verifyMutate1(GaussianMutation<RealValued> m, double p) {
		int countLow = 0;
		int countHigh = 0;
		final int TRIALS = (int)(2 * MAX_TRIALS / p);
		for (int i = 0; i < TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials for p="+p, countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials for p="+p, countHigh > 0);
		final int N = m.length() > 3 ? 3 + m.length() : 6;
		for (int j = 0; j < N; j++) {
			double[] v = new double[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9.0 - k;
			}
			final int z = j;
			int[] low = new int[z];
			int[] high = new int[z];
			for (int i = 0; i < TRIALS; i++) {
				RealVector f = new RealVector(v.clone());
				m.mutate(f);
				boolean done = true;
				for (int k = 0; k < j; k++) {
					if (f.get(k) < v[k]) low[k]++;
					if (f.get(k) > v[k]) high[k]++;
					if (low[k] == 0 || high[k] == 0) done = false; 
				}
				if (done) break;
			}
			for (int k = 0; k < low.length; k++) {
				assertTrue("Mutation failed to decrease value at index " + k + " for p="+p, low[k] > 0);
				assertTrue("Mutation failed to increase value at index " + k + " for p="+p, high[k] > 0);
			}
		}
	}
	
	private void verifyMutate1(GaussianMutation<RealValued> m, int K) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials for k="+K, countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials for k="+K, countHigh > 0);
		final int N = m.length() > 3 ? 3 + m.length() : 6;
		for (int j = 0; j < N; j++) {
			double[] v = new double[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9.0 - k;
			}
			final int z = j; 
			int[] low = new int[z];
			int[] high = new int[z];
			final int TRIALS = (int)(2 * MAX_TRIALS / (K < j ? 1.0*K/j : 1));
			for (int i = 0; i < TRIALS; i++) {
				RealVector f = new RealVector(v.clone());
				m.mutate(f);
				boolean done = true;
				int kCount = 0;
				for (int k = 0; k < j; k++) {
					if (f.get(k) < v[k]) { low[k]++; kCount++; }
					if (f.get(k) > v[k]) { high[k]++; kCount++; }
					if (low[k] == 0 || high[k] == 0) done = false;				 
				}
				assertTrue("Verify valid number of input vars mutated, k="+K+ " count="+kCount, kCount <= K);
				if (done) break;
			}
			for (int k = 0; k < low.length; k++) {
				assertTrue("Mutation failed to decrease value at index " + k + " for k="+K, low[k] > 0);
				assertTrue("Mutation failed to increase value at index " + k + " for k="+K, high[k] > 0);
			}
		}
	}
	
	
	private void verifyUndo(UndoableGaussianMutation<RealValued> m) {
		boolean changed = false;
		for (int i = 0; i < MAX_TRIALS; i++) {
			SingleReal f = new SingleReal(9.0);
			SingleReal f2 = f.copy();
			m.mutate(f);
			if (!f.equals(f2)) {
				changed = true;
			}
			m.undo(f);
			assertEquals("mutate followed by undo should revert to original", f2, f);
		}
		assertTrue("verifying that mutate changed f at least once out of many trials", changed);
		for (int j = 0; j < 5; j++) {
			double[] v = new double[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9.0 - k;
			}
			changed = false;
			for (int i = 0; i < MAX_TRIALS; i++) {
				RealVector f = new RealVector(v.clone());
				RealVector f2 = f.copy();
				m.mutate(f);
				if (!f.equals(f2)) {
					changed = true;
				}
				m.undo(f);
				assertEquals("mutate followed by undo should revert to original", f2, f);
			}
		}
		verifySplitUndo(m);
	}
	
	private void verifySplitUndo(UndoableGaussianMutation<RealValued> mutationOriginal) {
		for (int i = 0; i < 10; i++) {
			SingleReal v1 = new SingleReal(9);
			SingleReal v2 = v1.copy();
			SingleReal v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableGaussianMutation<RealValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
		for (int i = 0; i < 10; i++) {
			double[] vector = {2, 4, 8, 16, 32, 64, 128, 256};
			RealVector v1 = new RealVector(vector);
			RealVector v2 = v1.copy();
			RealVector v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableGaussianMutation<RealValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
	}
}
