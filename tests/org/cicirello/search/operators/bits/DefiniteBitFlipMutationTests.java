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
 
package org.cicirello.search.operators.bits;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.BitVector;
import java.util.HashSet;
import org.cicirello.search.operators.MutationIterator;


/**
 * JUnit 4 test cases for DefiniteBitFlipMutation.
 */
public class DefiniteBitFlipMutationTests {
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new DefiniteBitFlipMutation(0)
		);
	}
	
	@Test
	public void testMutateChange() {
		// Verify that mutate changes the BitVector.
		for (int b = 1; b < 10; b++) {
			DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(b);
			for (int trial = 0; trial < 10; trial++) {
				BitVector v1 = new BitVector(100, true);
				BitVector v2 = v1.copy();
				mutation.mutate(v2);
				assertNotEquals(v1, v2);
			}		
		}
	}
	
	@Test
	public void testMutateExpected() {
		// Verify that mutate changes a number of bits in the desired interval.
		for (int b = 1; b < 10; b++) {
			DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(b);
			for (int trial = 0; trial < 10; trial++) {
				// start with 100 zeros.
				BitVector v1 = new BitVector(100);
				// mutate it
				mutation.mutate(v1);
				int numBitsFlipped = v1.countOnes();
				assertTrue("verify: 1 <= numFlipped <= b", numBitsFlipped >= 1 && numBitsFlipped <= b);
			}		
		}
	}
	
	@Test
	public void testUndo() {
		for (int b = 1; b < 10; b++) {
			DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(b);
			for (int trial = 0; trial < 10; trial++) {
				BitVector v1 = new BitVector(100, true);
				BitVector v2 = v1.copy();
				mutation.mutate(v2);
				mutation.undo(v2);
				assertEquals(v1, v2);
			}		
		}
		DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(5);
		BitVector v1 = new BitVector(100, true);
		BitVector v2 = v1.copy();
		mutation.undo(v2);
		assertEquals(v1, v2);
	}
	
	@Test
	public void testSplit() {
		for (int b = 1; b < 10; b++) {
			DefiniteBitFlipMutation mutationOriginal = new DefiniteBitFlipMutation(b);
			DefiniteBitFlipMutation mutation = mutationOriginal.split();
			for (int trial = 0; trial < 10; trial++) {
				// start with 100 zeros.
				BitVector v1 = new BitVector(100);
				// mutate it
				mutation.mutate(v1);
				int numBitsFlipped = v1.countOnes();
				assertTrue("verify: 1 <= numFlipped <= b", numBitsFlipped >= 1 && numBitsFlipped <= b);
			}		
		}
	}
	
	@Test
	public void testSplitUndo() {
		DefiniteBitFlipMutation mutationOriginal = new DefiniteBitFlipMutation(5);
		for (int i = 0; i < 10; i++) {
			BitVector v1 = new BitVector(64);
			BitVector v2 = v1.copy();
			BitVector v3 = v1.copy();
			mutationOriginal.mutate(v2);
			assertNotEquals(v1, v2);
			DefiniteBitFlipMutation mutation = mutationOriginal.split();
			mutation.mutate(v3);
			assertNotEquals(v1, v3);
			mutationOriginal.undo(v2);
			assertEquals(v1, v2);
			mutation.undo(v3);
			assertEquals(v1, v3);
		}
	}
	
	@Test
	public void testMutationIterator() {
		for (int n = 0; n <= 10; n++) {
			int[] expectedCount = new int[4];
			expectedCount[0] = n;
			expectedCount[1] = expectedCount[0] + n*(n-1)/2;
			expectedCount[2] = expectedCount[1] + n*(n-1)*(n-2)/6;
			expectedCount[3] = expectedCount[2] + n*(n-1)*(n-2)*(n-3)/24;
			for (int b = 1; b <= 4; b++) {
				HashSet<BitVector> set = new HashSet<BitVector>(); 
				int bits = b <= n || n==0 ? b : n;
				DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(bits);
				BitVector v1 = new BitVector(n);
				MutationIterator iter = mutation.iterator(v1);
				int numIters = 0;
				while (iter.hasNext()) {
					iter.nextMutant();
					int count = v1.countOnes();
					assertTrue(count >= 1 && count <= bits);
					set.add(v1.copy());
					numIters++;
				}
				assertEquals("n,b="+n+","+b, numIters, set.size());
				assertEquals("expected="+expectedCount[b-1]+"; n,b="+n+","+b, expectedCount[b-1], numIters);
			}
		}
	}
		
	@Test
	public void testMutationIteratorSavepointAndRollback() {
		// Test setSavepoint and rollback
		for (int n = 1; n <= 5; n++) {
			int[] expectedCount = new int[4];
			expectedCount[0] = n;
			expectedCount[1] = expectedCount[0] + n*(n-1)/2;
			expectedCount[2] = expectedCount[1] + n*(n-1)*(n-2)/6;
			for (int b = 1; b <= 3; b++) {
				int bits = b <= n || n==0 ? b : n;
				DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(bits);
				for (int s = 0; s <= expectedCount[b-1]; s++) {
					HashSet<BitVector> set = new HashSet<BitVector>(); 
					BitVector v1 = new BitVector(n);
					MutationIterator iter = mutation.iterator(v1);
					int numIters = 0;
					BitVector saved=null;
					while (iter.hasNext()) {
						if (numIters==s) {
							saved = v1.copy();
							iter.setSavepoint();
						}
						iter.nextMutant();
						set.add(v1.copy());
						numIters++;
					}
					if (s==expectedCount[b-1]) {
						saved = v1.copy();
						iter.setSavepoint();
					}
					iter.rollback();
					assertEquals(saved, v1);
					assertFalse(iter.hasNext());
					assertEquals("n,b="+n+","+b, numIters, set.size());
					assertEquals("expected="+expectedCount[b-1]+"; n,b="+n+","+b, expectedCount[b-1], numIters);
					// an extra rollback should do nothing
					iter.rollback();
					assertEquals(saved, v1);
					IllegalStateException thrown = assertThrows( 
						IllegalStateException.class,
						() -> iter.nextMutant()
					);
				}
			}
		}
	}
	
	@Test
	public void testMutationIteratorEarlyRollback() {
		// Test setSavepoint and rollback
		for (int n = 1; n <= 5; n++) {
			int[] expectedCount = new int[4];
			expectedCount[0] = n;
			expectedCount[1] = expectedCount[0] + n*(n-1)/2;
			expectedCount[2] = expectedCount[1] + n*(n-1)*(n-2)/6;
			for (int b = 1; b <= 3; b++) {
				int bits = b <= n || n==0 ? b : n;
				DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(bits);
				for (int s = 0; s < expectedCount[b-1]-3; s++) {
					HashSet<BitVector> set = new HashSet<BitVector>(); 
					BitVector v1 = new BitVector(n);
					MutationIterator iter = mutation.iterator(v1);
					int numIters = 0;
					BitVector saved=null;
					while (iter.hasNext()) {
						if (numIters==s) {
							saved = v1.copy();
							iter.setSavepoint();
						} else if (numIters==s+3) {
							break;
						}
						iter.nextMutant();
						set.add(v1.copy());
						numIters++;
					}
					if (s==expectedCount[b-1]) {
						saved = v1.copy();
						iter.setSavepoint();
					}
					iter.rollback();
					assertEquals(saved, v1);
					assertFalse(iter.hasNext());
					IllegalStateException thrown = assertThrows( 
						IllegalStateException.class,
						() -> iter.nextMutant()
					);
				}
			}
		}
	}
}
