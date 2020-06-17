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
 * Uniform mutation for mutating integer valued representations.
 */
public class UniformMutationTests {
	
	
	// We don't test the distribution of results.
	// Instead, we simply verify that mutation is capable of both increasing
	// and decreasing values.  This constant controls the max number of trials
	// executed in verifying this.  E.g., pass if at least 1 out of MAX_TRIALS
	// increases value, and if at least 1 out of MAX_TRIALS decreases.
	// A Uniform is symmetric about 0.0, so approximately half of mutations 
	// should decrease and approximately half should increase.
	private static final int MAX_TRIALS = 40;
	
	@Test
	public void testUniformMutation1() {
		UniformMutation<IntegerValued> g1 = UniformMutation.createUniformMutation(1);
		assertEquals("default radius = 1", 1, g1.get(0));
		verifyMutate1(g1);
		
		UniformMutation<IntegerValued> g5 = UniformMutation.createUniformMutation(5);
		assertEquals("verifying radius initialized correctly", 5, g5.get(0));
		verifyMutate1(g5);
		
		UndoableUniformMutation<IntegerValued> g1u = UndoableUniformMutation.createUniformMutation(1);
		assertEquals("verifying radius initialized correctly", 1, g1u.get(0));
		verifyMutate1(g1u);
		verifyUndo(g1u);
		verifySplitUndo(g1u);
		
		UndoableUniformMutation<IntegerValued> g5u = UndoableUniformMutation.createUniformMutation(5);
		assertEquals("verifying radius initialized correctly", 5, g5u.get(0));
		verifyMutate1(g5u);
		verifyUndo(g5u);
		verifySplitUndo(g5u);
		
		UndoableUniformMutation<IntegerValued> g5copyU = g5u.split();
		assertEquals("verifying radius initialized correctly", 5, g5copyU.get(0));
		assertEquals("Verify split", g5u, g5copyU);
		assertEquals("hashCode of equal objects should be equal", g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		verifySplitUndo(g5copyU);
		
		g5copyU = g5u.copy();
		assertEquals("verifying radius initialized correctly", 5, g5copyU.get(0));
		assertEquals("Verify copy method", g5u, g5copyU);
		assertEquals("hashCode of equal objects should be equal", g5u.hashCode(), g5copyU.hashCode());
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		verifySplitUndo(g5copyU);
		
		UniformMutation<IntegerValued> g5split = g5.split();
		assertEquals("verifying radius initialized correctly", 5, g5split.get(0));
		assertEquals("Verify split", g5, g5split);
		assertTrue("Split should return a new object", g5 != g5split);
		assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
		verifyMutate1(g5split);
		
		UniformMutation<IntegerValued> g5copyM = g5.copy();
		assertEquals("verifying radius initialized correctly", 5, g5copyM.get(0));
		assertEquals("Verify split", g5, g5copyM);
		assertTrue("Split should return a new object", g5 != g5copyM);
		assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copyM.hashCode());
		verifyMutate1(g5copyM);
		
		UniformMutation<IntegerValued> g3 = g5;
		g3.set(0, 3);
		assertEquals("verifying radius changed correctly", 3, g3.get(0));
		verifyMutate1(g3);
	}
	
	@Test
	public void testPartialUniformMutation1() {
		for (int k = 1; k <= 8; k++) {
			UndoableUniformMutation<IntegerValued> g1 = UndoableUniformMutation.createUniformMutation(1, k);
			assertEquals("default radius = 1.0", 1, g1.get(0));
			verifyMutate1(g1, k);
			verifyUndo(g1);
			verifySplitUndo(g1);
			
			UndoableUniformMutation<IntegerValued> g5 = UndoableUniformMutation.createUniformMutation(5, k);
			assertEquals("verifying radius initialized correctly", 5, g5.get(0));
			verifyMutate1(g5, k);
			verifyUndo(g5);
			verifySplitUndo(g5);
			
			UniformMutation<IntegerValued> g5dis = UniformMutation.createUniformMutation(5, k);
			assertEquals("verifying radius initialized correctly", 5, g5dis.get(0));
			assertNotEquals("Verify undo disabled", g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableUniformMutation<IntegerValued> g5copy = g5.copy();
			assertEquals("verifying radius initialized correctly", 5, g5copy.get(0));
			assertEquals("Verify copy method", g5, g5copy);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			verifySplitUndo(g5copy);
			
			UniformMutation<IntegerValued> g5copyDis = g5dis.copy();
			assertEquals("verifying radius initialized correctly", 5, g5copyDis.get(0));
			assertEquals("Verify copy method", g5dis, g5copyDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableUniformMutation<IntegerValued> g5split = g5.split();
			assertEquals("verifying radius initialized correctly", 5, g5split.get(0));
			assertEquals("Verify split", g5, g5split);
			assertTrue("Split should return a new object", g5 != g5split);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			verifySplitUndo(g5split);
			
			UniformMutation<IntegerValued> g5splitDis = g5dis.split();
			assertEquals("verifying radius initialized correctly", 5, g5splitDis.get(0));
			assertEquals("Verify split", g5dis, g5splitDis);
			assertTrue("Split should return a new object", g5dis != g5splitDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableUniformMutation<IntegerValued> g3 = g5;
			g3.set(0, 3);
			assertEquals("verifying radius changed correctly", 3, g3.get(0));
			verifyMutate1(g3, k);
			verifyUndo(g3);
			verifySplitUndo(g3);
		}
		for (double k = 0.25; k <= 1.1; k+=0.25) {
			UndoableUniformMutation<IntegerValued> g1 = UndoableUniformMutation.createUniformMutation(1, k);
			assertEquals("default radius = 1.0", 1, g1.get(0));
			verifyMutate1(g1, k);
			verifyUndo(g1);
			verifySplitUndo(g1);
			
			UndoableUniformMutation<IntegerValued> g5 = UndoableUniformMutation.createUniformMutation(5, k);
			assertEquals("verifying radius initialized correctly", 5, g5.get(0));
			verifyMutate1(g5, k);
			verifyUndo(g5);
			verifySplitUndo(g5);
			
			UniformMutation<IntegerValued> g5dis = UniformMutation.createUniformMutation(5, k);
			assertEquals("verifying radius initialized correctly", 5, g5dis.get(0));
			assertNotEquals("Verify undo disabled", g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableUniformMutation<IntegerValued> g5copy = g5.copy();
			assertEquals("verifying radius initialized correctly", 5, g5copy.get(0));
			assertEquals("Verify copy constructor", g5, g5copy);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5copy.hashCode());
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			verifySplitUndo(g5copy);
			
			UniformMutation<IntegerValued> g5copyDis = g5dis.copy();
			assertEquals("verifying radius initialized correctly", 5, g5copyDis.get(0));
			assertEquals("Verify copy method", g5dis, g5copyDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5copyDis.hashCode());
			verifyMutate1(g5copyDis, k);
			
			UndoableUniformMutation<IntegerValued> g5split = g5.split();
			assertEquals("verifying radius initialized correctly", 5, g5split.get(0));
			assertEquals("Verify split", g5, g5split);
			assertTrue("Split should return a new object", g5 != g5split);
			assertEquals("hashCode of equal objects should be equal", g5.hashCode(), g5split.hashCode());
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			verifySplitUndo(g5split);
			
			UniformMutation<IntegerValued> g5splitDis = g5dis.split();
			assertEquals("verifying radius initialized correctly", 5, g5splitDis.get(0));
			assertEquals("Verify split", g5dis, g5splitDis);
			assertTrue("Split should return a new object", g5dis != g5splitDis);
			assertEquals("hashCode of equal objects should be equal", g5dis.hashCode(), g5splitDis.hashCode());
			verifyMutate1(g5splitDis, k);
			
			UndoableUniformMutation<IntegerValued> g3 = g5;
			g3.set(0, 3);
			assertEquals("verifying radius changed correctly", 3, g3.get(0));
			verifyMutate1(g3, k);
			verifyUndo(g3);
			verifySplitUndo(g3);
		}
	}
	
	
	
	private void verifySplitUndo(UndoableUniformMutation<IntegerValued> mutationOriginal) {
		for (int i = 0; i < 10; i++) {
			SingleInteger v1 = new SingleInteger(9);
			SingleInteger v2 = v1.copy();
			SingleInteger v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableUniformMutation<IntegerValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
		for (int i = 0; i < 10; i++) {
			int[] vector = {2, 4, 8, 16, 32, 64, 128, 256};
			IntegerVector v1 = new IntegerVector(vector);
			IntegerVector v2 = v1.copy();
			IntegerVector v3 = v1.copy();
			mutationOriginal.mutate(v2);
			UndoableUniformMutation<IntegerValued> mutation = mutationOriginal.split();
			mutation.mutate(v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
	}
	
	
	private void verifyMutate1(UniformMutation<IntegerValued> m) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleInteger f = new SingleInteger(9);
			m.mutate(f);
			assertTrue("Verify mutation within radius", Math.abs(9 - f.get()) <= m.get(0));
			if (f.get() < 9) countLow++;
			else if (f.get() > 9) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials", countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials", countHigh > 0);
		for (int j = 0; j < 5; j++) {
			int[] v = new int[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9 - k;
			}
			int[] low = new int[j];
			int[] high = new int[j];
			for (int i = 0; i < MAX_TRIALS; i++) {
				IntegerVector f = new IntegerVector(v.clone());
				m.mutate(f);
				boolean done = true;
				for (int k = 0; k < j; k++) {
					assertTrue("Verify mutation within radius for multivariate case", Math.abs(v[k] - f.get(k)) <= m.get(0));
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
	
	private void verifyMutate1(UniformMutation<IntegerValued> m, double p) {
		int countLow = 0;
		int countHigh = 0;
		final int TRIALS = (int)(2 * MAX_TRIALS / p);
		for (int i = 0; i < TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleInteger f = new SingleInteger(9);
			m.mutate(f);
			assertTrue("Verify mutation within radius", Math.abs(9 - f.get()) <= m.get(0));
			if (f.get() < 9) countLow++;
			else if (f.get() > 9) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials for p="+p, countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials for p="+p, countHigh > 0);
		final int N = m.length() > 3 ? 3 + m.length() : 6;
		for (int j = 0; j < N; j++) {
			int[] v = new int[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9 - k;
			}
			final int z = j;
			int[] low = new int[z];
			int[] high = new int[z];
			for (int i = 0; i < TRIALS; i++) {
				IntegerVector f = new IntegerVector(v.clone());
				m.mutate(f);
				boolean done = true;
				for (int k = 0; k < j; k++) {
					assertTrue("Verify mutation within radius for multivariate case", Math.abs(v[k] - f.get(k)) <= m.get(0));
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
	
	private void verifyMutate1(UniformMutation<IntegerValued> m, int K) {
		int countLow = 0;
		int countHigh = 0;
		for (int i = 0; i < MAX_TRIALS && (countLow==0 || countHigh==0); i++) {
			SingleInteger f = new SingleInteger(9);
			m.mutate(f);
			assertTrue("Verify mutation within radius", Math.abs(9 - f.get()) <= m.get(0));
			if (f.get() < 9) countLow++;
			else if (f.get() > 9) countHigh++;
		}
		assertTrue("Mutation failed to decrease value over large number of trials for k="+K, countLow > 0);
		assertTrue("Mutation failed to increase value over large number of trials for k="+K, countHigh > 0);
		final int N = m.length() > 3 ? 3 + m.length() : 6;
		for (int j = 0; j < N; j++) {
			int[] v = new int[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9 - k;
			}
			final int z = j; 
			int[] low = new int[z];
			int[] high = new int[z];
			final int TRIALS = (int)(2 * MAX_TRIALS / (K < j ? 1.0*K/j : 1));
			for (int i = 0; i < TRIALS; i++) {
				IntegerVector f = new IntegerVector(v.clone());
				m.mutate(f);
				boolean done = true;
				int kCount = 0;
				for (int k = 0; k < j; k++) {
					assertTrue("Verify mutation within radius for multivariate case", Math.abs(v[k] - f.get(k)) <= m.get(0));
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
	
	
	private void verifyUndo(UndoableUniformMutation<IntegerValued> m) {
		boolean changed = false;
		for (int i = 0; i < MAX_TRIALS; i++) {
			SingleInteger f = new SingleInteger(9);
			SingleInteger f2 = f.copy();
			m.mutate(f);
			if (!f.equals(f2)) {
				changed = true;
			}
			m.undo(f);
			assertEquals("mutate followed by undo should revert to original", f2, f);
		}
		assertTrue("verifying that mutate changed f at least once out of many trials", changed);
		for (int j = 0; j < 5; j++) {
			int[] v = new int[j];
			for (int k = 0; k < j; k++) {
				v[k] = 9 - k;
			}
			changed = false;
			for (int i = 0; i < MAX_TRIALS; i++) {
				IntegerVector f = new IntegerVector(v.clone());
				IntegerVector f2 = f.copy();
				m.mutate(f);
				if (!f.equals(f2)) {
					changed = true;
				}
				m.undo(f);
				assertEquals("mutate followed by undo should revert to original", f2, f);
			}
		}
	}
}
