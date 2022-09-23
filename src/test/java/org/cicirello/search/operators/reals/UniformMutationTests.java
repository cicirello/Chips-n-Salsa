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
 * JUnit test cases for the classes that implement different variations of
 * Uniform mutation for mutating floating-point function parameters.
 */
public class UniformMutationTests {
	
	// precision used in floating-point comparisons
	private static final double EPSILON = 1e-10;
	
	// We don't test the distribution of results.
	// Instead, we simply verify that mutation is capable of both increasing
	// and decreasing values.  This constant controls the max number of trials
	// executed in verifying this.  E.g., pass if at least 1 out of MAX_TRIALS
	// increases value, and if at least 1 out of MAX_TRIALS decreases.
	// A Uniform is symmetric about 0.0, so approximately half of mutations 
	// should decrease and approximately half should increase.
	private static final int MAX_TRIALS = 100;
	
	@Test
	public void testEquals() {
		UniformMutation<RealValued> g1 = UniformMutation.createUniformMutation(1);
		UniformMutation<RealValued> g2 = UniformMutation.createUniformMutation(1);
		UniformMutation<RealValued> g3 = UniformMutation.createUniformMutation(2);
		assertEquals(g1, g2);
		assertEquals(g1.hashCode(), g2.hashCode());
		assertNotEquals(g1, g3);
		assertFalse(g1.equals(null));
		assertFalse(g1.equals("hello"));
		UniformMutation<RealValued> p1 = UniformMutation.createUniformMutation(1, 1);
		UniformMutation<RealValued> p2 = UniformMutation.createUniformMutation(2, 1);
		UniformMutation<RealValued> p3 = UniformMutation.createUniformMutation(1, 2);
		UniformMutation<RealValued> p4 = UniformMutation.createUniformMutation(1, 0.5);
		UniformMutation<RealValued> p5 = UniformMutation.createUniformMutation(1, 0.25);
		assertNotEquals(p1, p2);
		assertNotEquals(p1, p3);
		assertNotEquals(p4, p5);
		assertNotEquals(p1, g1);
		assertNotEquals(p1, g3);
		assertFalse(p1.equals(null));
		p1 = UndoableUniformMutation.createUniformMutation(1, 1);
		p2 = UndoableUniformMutation.createUniformMutation(2, 1);
		p3 = UndoableUniformMutation.createUniformMutation(1, 2);
		p4 = UndoableUniformMutation.createUniformMutation(1, 0.5);
		p5 = UndoableUniformMutation.createUniformMutation(1, 0.25);
		assertNotEquals(p1, p2);
		assertNotEquals(p1, p3);
		assertNotEquals(p4, p5);
		assertNotEquals(p1, g1);
		assertNotEquals(p1, g3);
		assertFalse(p1.equals(null));
	}
	
	@Test
	public void testToArray() {
		UniformMutation<RealValued> u = UniformMutation.createUniformMutation(2);
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
	public void testUniformMutation1() {
		UniformMutation<RealValued> g1 = UniformMutation.createUniformMutation();
		assertEquals(1.0, g1.get(0), EPSILON);
		verifyMutate1(g1);
		
		UniformMutation<RealValued> g5 = UniformMutation.createUniformMutation(5.0);
		assertEquals(5.0, g5.get(0), EPSILON);
		verifyMutate1(g5);
		
		UndoableUniformMutation<RealValued> g1u = UndoableUniformMutation.createUniformMutation();
		assertEquals(1.0, g1u.get(0), EPSILON);
		verifyMutate1(g1u);
		verifyUndo(g1u);
		verifySplitUndo(g1u);
		
		UndoableUniformMutation<RealValued> g5u = UndoableUniformMutation.createUniformMutation(5.0);
		assertEquals(5.0, g5u.get(0), EPSILON);
		verifyMutate1(g5u);
		verifyUndo(g5u);
		verifySplitUndo(g5u);
		
		UndoableUniformMutation<RealValued> g5copyU = g5u.split();
		assertEquals(5.0, g5copyU.get(0), EPSILON);
		assertEquals(g5u, g5copyU);
		assertEquals(g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		verifySplitUndo(g5copyU);
		
		g5copyU = g5u.copy();
		assertEquals(5.0, g5copyU.get(0), EPSILON);
		assertEquals(g5u, g5copyU);
		assertEquals(g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		verifySplitUndo(g5copyU);
		
		UniformMutation<RealValued> g5split = g5.split();
		assertEquals(5.0, g5split.get(0), EPSILON);
		assertEquals(g5, g5split);
		assertTrue(g5 != g5split);
		assertEquals(g5.hashCode(), g5split.hashCode());
		verifyMutate1(g5split);
		
		UniformMutation<RealValued> g5copyM = g5.copy();
		assertEquals(5.0, g5copyM.get(0), EPSILON);
		assertEquals(g5, g5copyM);
		assertTrue(g5 != g5copyM);
		assertEquals(g5.hashCode(), g5copyM.hashCode());
		verifyMutate1(g5copyM);
		
		UniformMutation<RealValued> g3 = g5;
		g3.set(0, 3.0);
		assertEquals(3.0, g3.get(0), EPSILON);
		verifyMutate1(g3);
		
		g3.set(new double[] {7} );
		assertEquals(7.0, g3.get(0));
	}
	
	@Test
	public void testPartialUniformMutation1() {
		for (int k = 1; k <= 8; k++) {
			UndoableUniformMutation<RealValued> g1 = UndoableUniformMutation.createUniformMutation(1.0, k);
			assertEquals(1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			verifySplitUndo(g1);
			
			UndoableUniformMutation<RealValued> g5 = UndoableUniformMutation.createUniformMutation(5.0, k);
			assertEquals(5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			verifySplitUndo(g5);
			
			UniformMutation<RealValued> g5dis = UniformMutation.createUniformMutation(5.0, k);
			assertEquals(5.0, g5dis.get(0), EPSILON);
			assertNotEquals(g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableUniformMutation<RealValued> g5copy = g5.copy();
			assertEquals(5.0, g5copy.get(0), EPSILON);
			assertEquals(g5, g5copy);
			assertEquals(g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			verifySplitUndo(g5copy);
			
			UniformMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals(5.0, g5copyDis.get(0), EPSILON);
			assertEquals(g5dis, g5copyDis);
			assertEquals(g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableUniformMutation<RealValued> g5split = g5.split();
			assertEquals(5.0, g5split.get(0), EPSILON);
			assertEquals(g5, g5split);
			assertTrue(g5 != g5split);
			assertEquals(g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			verifySplitUndo(g5split);
			
			UniformMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals(5.0, g5splitDis.get(0), EPSILON);
			assertEquals(g5dis, g5splitDis);
			assertTrue(g5dis != g5splitDis);
			assertEquals(g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableUniformMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals(3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
			verifySplitUndo(g3);
		}
		for (double k = 0.25; k <= 1.1; k+=0.25) {
			UndoableUniformMutation<RealValued> g1 = UndoableUniformMutation.createUniformMutation(1.0, k);
			assertEquals(1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			verifySplitUndo(g1);
			
			UndoableUniformMutation<RealValued> g5 = UndoableUniformMutation.createUniformMutation(5.0, k);
			assertEquals(5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			verifySplitUndo(g5);
			
			UniformMutation<RealValued> g5dis = UniformMutation.createUniformMutation(5.0, k);
			assertEquals(5.0, g5dis.get(0), EPSILON);
			assertNotEquals(g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableUniformMutation<RealValued> g5copy = g5.copy();
			assertEquals(5.0, g5copy.get(0), EPSILON);
			assertEquals(g5, g5copy);
			assertEquals(g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			verifySplitUndo(g5copy);
			
			UniformMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals(5.0, g5copyDis.get(0), EPSILON);
			assertEquals(g5dis, g5copyDis);
			assertEquals(g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableUniformMutation<RealValued> g5split = g5.split();
			assertEquals(5.0, g5split.get(0), EPSILON);
			assertEquals(g5, g5split);
			assertTrue(g5 != g5split);
			assertEquals(g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			verifySplitUndo(g5split);
			
			UniformMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals(5.0, g5splitDis.get(0), EPSILON);
			assertEquals(g5dis, g5splitDis);
			assertTrue(g5dis != g5splitDis);
			assertEquals(g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableUniformMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals(3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
			verifySplitUndo(g3);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UniformMutation.createUniformMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UniformMutation.createUniformMutation(1.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableUniformMutation.createUniformMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableUniformMutation.createUniformMutation(1.0, 0.0)
		);
	}
	
	
	
	
	private void verifyMutate1(UniformMutation<RealValued> m) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			assertTrue(Math.abs(9.0 - f.get()) <= m.get(0));
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue(countLow > 0);
		assertTrue(countHigh > 0);
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
					assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
					if (f.get(k) < v[k]) low[k]++;
					if (f.get(k) > v[k]) high[k]++;
					if (low[k] == 0 || high[k] == 0) done = false;
				}
				if (done) break;
			}
			for (int k = 0; k < j; k++) {
				assertTrue(low[k] > 0);
				assertTrue(high[k] > 0);
			}
		}
	}
	
	private void verifyMutate1(UniformMutation<RealValued> m, double p) {
		int countLow = 0;
		int countHigh = 0;
		final int TRIALS = (int)(2 * MAX_TRIALS / p);
		for (int i = 0; i < TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			assertTrue(Math.abs(9.0 - f.get()) <= m.get(0));
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue(countLow > 0);
		assertTrue(countHigh > 0);
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
					assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
					if (f.get(k) < v[k]) low[k]++;
					if (f.get(k) > v[k]) high[k]++;
					if (low[k] == 0 || high[k] == 0) done = false; 
				}
				if (done) break;
			}
			for (int k = 0; k < low.length; k++) {
				assertTrue(low[k] > 0);
				assertTrue(high[k] > 0);
			}
		}
	}
	
	private void verifyMutate1(UniformMutation<RealValued> m, int K) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleReal f = new SingleReal(9.0);
			m.mutate(f);
			assertTrue(Math.abs(9.0 - f.get()) <= m.get(0));
			if (f.get() < 9.0) countLow++;
			else if (f.get() > 9.0) countHigh++;
		}
		assertTrue(countLow > 0);
		assertTrue(countHigh > 0);
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
					assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
					if (f.get(k) < v[k]) { low[k]++; kCount++; }
					if (f.get(k) > v[k]) { high[k]++; kCount++; }
					if (low[k] == 0 || high[k] == 0) done = false;				 
				}
				assertTrue(kCount <= K);
				if (done) break;
			}
			for (int k = 0; k < low.length; k++) {
				assertTrue(low[k] > 0);
				assertTrue(high[k] > 0);
			}
		}
	}
	
	
	private void verifyUndo(UndoableUniformMutation<RealValued> m) {
		boolean changed = false;
		for (int i = 0; i < MAX_TRIALS; i++) {
			SingleReal f = new SingleReal(9.0);
			SingleReal f2 = f.copy();
			m.mutate(f);
			if (!f.equals(f2)) {
				changed = true;
			}
			m.undo(f);
			assertEquals(f2, f);
		}
		assertTrue(changed);
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
				assertEquals(f2, f);
			}
		}
	}
	
	private void verifySplitUndo(UndoableUniformMutation<RealValued> mutationOriginal) {
		for (int i = 0; i < 10; i++) {
			SingleReal v1 = new SingleReal(9);
			SingleReal v2 = v1.copy();
			SingleReal v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableUniformMutation<RealValued> mutation = mutationOriginal.split();
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
			UndoableUniformMutation<RealValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
	}
}
