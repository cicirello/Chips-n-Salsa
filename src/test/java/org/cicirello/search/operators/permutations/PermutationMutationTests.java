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
 
package org.cicirello.search.operators.permutations;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * JUnit test cases for mutation operators on permutations.
 */
public class PermutationMutationTests {
	
	// For tests involving randomness, number of trials to include in test case.
	private static final int NUM_RAND_TESTS = 20;
	
	@Test
	public void testAdjacentSwap() {
		AdjacentSwapMutation m = new AdjacentSwapMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are adjacent swaps
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertEquals(a, b-1);
				assertEquals(p.get(a), mutant.get(b));
				assertEquals(p.get(b), mutant.get(a));
			}
		}
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[] indexes = new boolean[n-1];
			int numSamples = (n-1)*20;
			Permutation p = new Permutation(n);
			for (int i = 0; i < numSamples; i++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				indexes[a] = true;
			}
			for (int i = 0; i < indexes.length; i++) {
				assertTrue(indexes[i]);
			}
		}
	}
	
	@Test
	public void testBlockInterchange() {
		BlockInterchangeMutation m = new BlockInterchangeMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are block interchanges
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b, c, d;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (d = p.length()-1; d >= 0 && p.get(d) == mutant.get(d); d--);
				for (b = a; b < p.length() && p.get(b) != mutant.get(d); b++);
				for (c = d; c > b && p.get(c) != mutant.get(a); c--);
				assertTrue(a <= b && b < c && c <= d);
				int i, j;
				for (i=a, j=c; j <= d; i++, j++) {
					assertEquals(p.get(j), mutant.get(i));
				}
				for (j=b+1; j < c; i++, j++) {
					assertEquals(p.get(j), mutant.get(i));
				}
				for (j=a; j <= b; j++, i++) {
					assertEquals(p.get(j), mutant.get(i));
				}
			}
		}
		// Check distribution of random indexes
		for (int n = 2; n <= 7; n++) {
			boolean[][] firstPair = new boolean[n-1][n-1];
			boolean[][] secondPair = new boolean[n-1][n-1];
			int numSamples = (n-1)*(n-1)*80;
			int[] indexes = new int[4];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				firstPair[indexes[0]][indexes[1]] = true;
				secondPair[indexes[2]-1][indexes[3]-1] = true;
			}
			checkIndexQuartets(firstPair, secondPair);
		}
	}
	
	@Test
	public void testInsertion() {
		InsertionMutation m = new InsertionMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are insertions
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				if (mutant.get(b) == p.get(a)) {
					for (int i = a; i < b; i++) {
						assertEquals(p.get(i+1), mutant.get(i));
					}
				} else if (mutant.get(a) == p.get(b)) {
					for (int i = a+1; i <= b; i++) {
						assertEquals(p.get(i-1), mutant.get(i));
					}
				} else {
					fail("Not an insertion.");
				}
			}
		}
	}
	
	@Test
	public void testReversal() {
		ReversalMutation m = new ReversalMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are reversals
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				while (a <= b) {
					assertEquals(p.get(a), mutant.get(b));
					a++;
					b--;
				}
			}
		}
	}
	
	@Test
	public void testCycleAlphaComputeK0() {
		double[] U = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.99};
		CycleAlphaMutation m = new CycleAlphaMutation(Math.ulp(0.0));
		for (int n = 2; n <= 128; n *= 2) {
			for (double u : U) {
				assertEquals(2, m.computeK(n, u));
			}
		}
	}
	
	@Test
	public void testCycleAlphaComputeK999() {
		double[] U =     {0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1.0-Math.ulp(1.0)};
		int[] expected = { 2,    2,     3,    4,    5,    6,     7,    8,    9};
		CycleAlphaMutation m = new CycleAlphaMutation(0.999);
		int n = 9;
		for (int i = 0; i < U.length; i++) {
			double u = U[i];
			assertEquals(expected[i], m.computeK(n, u), "u:"+u);
		}
	}
	
	@Test
	public void testCycleAlphaComputeK05() {
		double[] U =     {0.0, 0.5, 0.50197,  0.75, 0.75295, 0.875, 0.8785, 0.9375, 0.942, 0.96875, 0.9726, 0.984375, 0.9883, 0.9961, 1.0-Math.ulp(1.0)};
		int[] expected = { 2,  2,     3,       3,       4,    4,      5,      5,     6,      6,       7,       7,      8,    9,        9};
		CycleAlphaMutation m = new CycleAlphaMutation(0.5);
		int n = 9;
		for (int i = 0; i < U.length; i++) {
			double u = U[i];
			assertEquals(expected[i], m.computeK(n, u), "u:"+u);
		}
	}
	
	@Test
	public void testCycleAlphaNear0() {
		CycleAlphaMutation m = new CycleAlphaMutation(Math.ulp(0.0));
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// When alpha is near 0, with extremely high probability all mutations
		// should be 2-cycles, i.e., swaps.
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				assertEquals(p.get(a), mutant.get(b));
				assertEquals(p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals(p.get(i), mutant.get(i));
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CycleAlphaMutation(0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CycleAlphaMutation(1.0)
		);
	}
	
	@Test
	public void testCycleAlphaNear1() {
		CycleAlphaMutation m = new CycleAlphaMutation(0.999);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// When alpha is very near 1.0, cycle length should be approximately uniform in [2, n]
		boolean[] foundCycleLength = new boolean[7];
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			int[] indexes = new int[n];
			
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int size = 0;
				for (int i = 0; i < p.length(); i++) {
					if (p.get(i) != mutant.get(i)) {
						indexes[size] = i;
						size++;
					}
				}
				foundCycleLength[size] = true;
				int[] inv = p.getInverse();
				boolean[] cycleCheck = new boolean[n];
				int j = indexes[0];
				int next = p.get(j);
				for (int i = 0; i < size; i++) {
					assertFalse(cycleCheck[next]);
					cycleCheck[next] = true;
					next = mutant.get(j);
					j = inv[next];
					assertNotEquals(p.get(j), mutant.get(j));
				}
				assertTrue(cycleCheck[next]);
			}
		}
		for (int i = 2; i < foundCycleLength.length; i++) {
			assertTrue(foundCycleLength[i], "i="+i);
		}
	}
	
	@Test
	public void testCycleAlpha05() {
		CycleAlphaMutation m = new CycleAlphaMutation(0.5);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			int[] indexes = new int[n];
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int size = 0;
				for (int i = 0; i < p.length(); i++) {
					if (p.get(i) != mutant.get(i)) {
						indexes[size] = i;
						size++;
					}
				}
				int[] inv = p.getInverse();
				boolean[] cycleCheck = new boolean[n];
				int j = indexes[0];
				int next = p.get(j);
				for (int i = 0; i < size; i++) {
					assertFalse(cycleCheck[next]);
					cycleCheck[next] = true;
					next = mutant.get(j);
					j = inv[next];
					assertNotEquals(p.get(j), mutant.get(j));
				}
				assertTrue(cycleCheck[next]);
			}
		}
	}
	
	@Test
	public void testCycle2() {
		CycleMutation m = new CycleMutation(2);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are 2-cycles (i.e., swaps)
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				assertEquals(p.get(a), mutant.get(b));
				assertEquals(p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals(p.get(i), mutant.get(i));
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CycleMutation(1)
		);
	}
	
	@Test
	public void testCycle3() {
		CycleMutation m = new CycleMutation(3);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are 2-cycles or 3-cycles
		int[] indexes = new int[3];
		boolean[] foundCycleLength = new boolean[4];
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int size = 0;
				for (int i = 0; i < p.length(); i++) {
					if (p.get(i) != mutant.get(i)) {
						if (size == indexes.length) {
							fail("cycle is too large");
						}
						indexes[size] = i;
						size++;
					}
				}
				foundCycleLength[size] = true;
				int[] inv = p.getInverse();
				boolean[] cycleCheck = new boolean[n];
				int j = indexes[0];
				int next = p.get(j);
				for (int i = 0; i < size; i++) {
					assertFalse(cycleCheck[next]);
					cycleCheck[next] = true;
					next = mutant.get(j);
					j = inv[next];
					assertNotEquals(p.get(j), mutant.get(j));
				}
				assertTrue(cycleCheck[next]);
			}
		}
		for (int i = 2; i < foundCycleLength.length; i++) {
			assertTrue(foundCycleLength[i]);
		}
	}
	
	@Test
	public void testCycle4() {
		CycleMutation m = new CycleMutation(4);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are 2-cycles, 3-cycles, or 4-cycles
		int[] indexes = new int[4];
		boolean[] foundCycleLength = new boolean[5];
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int size = 0;
				for (int i = 0; i < p.length(); i++) {
					if (p.get(i) != mutant.get(i)) {
						if (size == indexes.length) {
							fail("cycle is too large");
						}
						indexes[size] = i;
						size++;
					}
				}
				foundCycleLength[size] = true;
				int[] inv = p.getInverse();
				boolean[] cycleCheck = new boolean[n];
				int j = indexes[0];
				int next = p.get(j);
				for (int i = 0; i < size; i++) {
					assertFalse(cycleCheck[next]);
					cycleCheck[next] = true;
					next = mutant.get(j);
					j = inv[next];
					assertNotEquals(p.get(j), mutant.get(j));
				}
				assertTrue(cycleCheck[next]);
			}
		}
		for (int i = 2; i < foundCycleLength.length; i++) {
			assertTrue(foundCycleLength[i]);
		}
	}
	
	@Test
	public void testSwap() {
		SwapMutation m = new SwapMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are swaps
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				assertEquals(p.get(a), mutant.get(b));
				assertEquals(p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals(p.get(i), mutant.get(i));
				}
			}
		}
	}
	
	@Test
	public void testBlockMove() {
		BlockMoveMutation m = new BlockMoveMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][][] indexTriples = new boolean[n][n][n];
			int numSamples = n*(n-1)*(n+1)*40/6;
			int[] indexes = new int[3];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
			}
			checkIndexTriples(indexTriples);
		}
		// Verify mutations are block moves
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				int c;
				for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
				// block of p from index a to index (a+b-c) should be same as mutant index c to index b
				int e = a;
				for (int d = c; d <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
				// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
				for (int d = a; e <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
			}
		}
	}
	
	@Test
	public void testScramble() {
		ScrambleMutation m = new ScrambleMutation();
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testUniformScramble() {
		UniformScrambleMutation m = new UniformScrambleMutation(0.0, true);
		mutateTester(m);
		splitTester(m);
		m = new UniformScrambleMutation(1.0);
		mutateTester(m);
		splitTester(m);
		m = new UniformScrambleMutation(0.5, true);
		mutateTester(m);
		splitTester(m);
		m = new UniformScrambleMutation(0.0, false);
		for (int n = 0; n <= 6; n++) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			m.mutate(p2);
			assertEquals(p1, p2);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UniformScrambleMutation(-0.000001)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UniformScrambleMutation(1.000001)
		);
	}
	
	@Test
	public void testUndoableUniformScramble() {
		UndoableUniformScrambleMutation m = new UndoableUniformScrambleMutation(0.0, true);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		m = new UndoableUniformScrambleMutation(1.0);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		m = new UndoableUniformScrambleMutation(0.5, true);
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		m = new UndoableUniformScrambleMutation(0.0, false);
		for (int n = 0; n <= 6; n++) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			m.mutate(p2);
			assertEquals(p1, p2);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UndoableUniformScrambleMutation(-0.000001)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UndoableUniformScrambleMutation(1.000001)
		);
	}
	
	@Test
	public void testUndoableScramble() {
		UndoableScrambleMutation m = new UndoableScrambleMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testTwoChange() {
		TwoChangeMutation m = new TwoChangeMutation();
		undoTester(m);
		mutateTester(m, 4);
		splitTester(m);
		// For n < 4, this mutation operator should do nothing:
		for (int n = 0; n < 4; n++) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			m.mutate(p2);
			assertEquals(p1, p2);
		}
		// test internal mutate for n = 4
		int[] perm = {0, 1, 2, 3};
		Permutation[] expected = {
			new Permutation(new int[] {1, 0, 2, 3}),
			new Permutation(new int[] {0, 2, 1, 3}),
			new Permutation(new int[] {0, 1, 3, 2}),
			new Permutation(new int[] {0, 2, 1, 3})
		};
		for (int i = 0; i < expected.length; i++) {
			Permutation p = new Permutation(perm);
			m.internalMutate(p, i, 1);
			assertEquals(expected[i], p);
		}
		// test internal mutate for n = 5
		perm = new int[] {0, 1, 2, 3, 4};
		Permutation[][] expected2 = {
			{ 
				new Permutation(new int[] {1, 0, 2, 3, 4}),
				new Permutation(new int[] {0, 2, 1, 3, 4}),
				new Permutation(new int[] {0, 1, 3, 2, 4}),
				new Permutation(new int[] {0, 1, 2, 4, 3}),
				new Permutation(new int[] {4, 1, 2, 3, 0})
			},
			{ 
				new Permutation(new int[] {0, 1, 2, 4, 3}),
				new Permutation(new int[] {4, 1, 2, 3, 0}),
				new Permutation(new int[] {1, 0, 2, 3, 4}),
				new Permutation(new int[] {0, 2, 1, 3, 4}),
				new Permutation(new int[] {0, 1, 3, 2, 4})
			}
		};
		for (int i = 0; i < expected2.length; i++) {
			for (int j = 0; j < expected2[i].length; j++) {
				Permutation p = new Permutation(perm);
				m.internalMutate(p, j, i+1);
				assertEquals(expected2[i][j], p);
			}
		}
		// test internal mutate for n = 6
		perm = new int[] {0, 1, 2, 3, 4, 5};
		expected2 = new Permutation[][] {
			{ 
				new Permutation(new int[] {1, 0, 2, 3, 4, 5}),
				new Permutation(new int[] {0, 2, 1, 3, 4, 5}),
				new Permutation(new int[] {0, 1, 3, 2, 4, 5}),
				new Permutation(new int[] {0, 1, 2, 4, 3, 5}),
				new Permutation(new int[] {0, 1, 2, 3, 5, 4}),
				new Permutation(new int[] {5, 1, 2, 3, 4, 0})
			},
			{ 
				new Permutation(new int[] {2, 1, 0, 3, 4, 5}),
				new Permutation(new int[] {0, 3, 2, 1, 4, 5}),
				new Permutation(new int[] {0, 1, 4, 3, 2, 5}),
				new Permutation(new int[] {0, 1, 2, 5, 4, 3}),
				new Permutation(new int[] {0, 3, 2, 1, 4, 5}),
				new Permutation(new int[] {0, 1, 4, 3, 2, 5})
			},
			{ 
				new Permutation(new int[] {0, 1, 2, 3, 5, 4}),
				new Permutation(new int[] {5, 1, 2, 3, 4, 0}),
				new Permutation(new int[] {1, 0, 2, 3, 4, 5}),
				new Permutation(new int[] {0, 2, 1, 3, 4, 5}),
				new Permutation(new int[] {0, 1, 3, 2, 4, 5}),
				new Permutation(new int[] {0, 1, 2, 4, 3, 5})
			}
		};
		for (int i = 0; i < expected2.length; i++) {
			for (int j = 0; j < expected2[i].length; j++) {
				Permutation p = new Permutation(perm);
				m.internalMutate(p, j, i+1);
				assertEquals(expected2[i][j], p);
			}
		}
		// test internal mutate for n = 7.
		perm = new int[] {0, 1, 2, 3, 4, 5, 6};
		expected2 = new Permutation[][] {
			{ 
				new Permutation(new int[] {1, 0, 2, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 2, 1, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 3, 2, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 4, 3, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 3, 5, 4, 6}),
				new Permutation(new int[] {0, 1, 2, 3, 4, 6, 5}),
				new Permutation(new int[] {6, 1, 2, 3, 4, 5, 0})
			},
			{ 
				new Permutation(new int[] {2, 1, 0, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 3, 2, 1, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 4, 3, 2, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 5, 4, 3, 6}),
				new Permutation(new int[] {0, 1, 2, 3, 6, 5, 4}),
				new Permutation(new int[] {5, 1, 2, 3, 4, 0, 6}),
				new Permutation(new int[] {0, 6, 2, 3, 4, 5, 1})
			},
			{ 
				new Permutation(new int[] {0, 1, 2, 3, 6, 5, 4}),
				new Permutation(new int[] {5, 1, 2, 3, 4, 0, 6}),
				new Permutation(new int[] {0, 6, 2, 3, 4, 5, 1}),
				new Permutation(new int[] {2, 1, 0, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 3, 2, 1, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 4, 3, 2, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 5, 4, 3, 6})
			},
			{ 
				new Permutation(new int[] {0, 1, 2, 3, 4, 6, 5}),
				new Permutation(new int[] {6, 1, 2, 3, 4, 5, 0}),
				new Permutation(new int[] {1, 0, 2, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 2, 1, 3, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 3, 2, 4, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 4, 3, 5, 6}),
				new Permutation(new int[] {0, 1, 2, 3, 5, 4, 6})
			}
		};
		for (int i = 0; i < expected2.length; i++) {
			for (int j = 0; j < expected2[i].length; j++) {
				Permutation p = new Permutation(perm);
				m.internalMutate(p, j, i+1);
				assertEquals(expected2[i][j], p);
			}
		}
	}
	
	@Test
	public void testRotation() {
		RotationMutation m = new RotationMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		Permutation[] testcases = {
			new Permutation(new int[] {0}),
			new Permutation(new int[] {0, 1}), 
			new Permutation(new int[] {0, 2, 1}), 
			new Permutation(new int[] {2, 0, 3, 1})
		};
		Permutation[][] expectedRaw = {
			{},
			{ new Permutation(new int[] {1, 0}) },
			{ new Permutation(new int[] {2, 1, 0}), new Permutation(new int[] {1, 0, 2}) },
			{ new Permutation(new int[] {0, 3, 1, 2}), new Permutation(new int[] {3, 1, 2, 0}), new Permutation(new int[] {1, 2, 0, 3}) }
		};
		ArrayList<HashSet<Permutation>> expected = new ArrayList<HashSet<Permutation>>();
		for (int i = 0; i < expectedRaw.length; i++) {
			HashSet<Permutation> set = new HashSet<Permutation>();
			for (int j = 0; j < expectedRaw[i].length; j++) {
				set.add(expectedRaw[i][j]);
			}
			expected.add(set);
		}
		for (int i = 1; i < testcases.length; i++) {
			for (int j = 0; j < 8; j++) {
				Permutation mutant = new Permutation(testcases[i]);
				m.mutate(mutant);
				assertTrue(expected.get(i).contains(mutant));
			}				
		}
		for (int i = 0; i < testcases.length; i++) {
			Permutation p = new Permutation(testcases[i]);
			final MutationIterator iter = m.iterator(p);
			HashSet<Permutation> observed = new HashSet<Permutation>();
			int count = 0;
			while (iter.hasNext()) {
				iter.nextMutant();
				count++;
				observed.add(p);
			}
			assertEquals(count, observed.size());
			assertEquals(count, expected.get(i).size());
			assertEquals(expected.get(i), observed);
			iter.rollback();
			assertEquals(testcases[i], p);
		}
		for (int i = 1; i < testcases.length; i++) {
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				HashSet<Permutation> observed = new HashSet<Permutation>();
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					observed.add(p);
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				assertEquals(count, observed.size());
				assertEquals(count, expected.get(i).size());
				assertEquals(expected.get(i), observed);
			}
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
						break;
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				IllegalStateException thrown = assertThrows( 
					IllegalStateException.class,
					() -> iter.nextMutant()
				);
			}
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
					} else if (s==count-1) {
						break;
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				assertFalse(iter.hasNext());
				iter.rollback();
				assertEquals(pExp, p);
			}
		}
	}
	
	
	@Test
	public void testWindowLimitedInsertion() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are insertions
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
					if (mutant.get(b) == p.get(a)) {
						for (int i = a; i < b; i++) {
							assertEquals(p.get(i+1), mutant.get(i));
						}
					} else if (mutant.get(a) == p.get(b)) {
						for (int i = a+1; i <= b; i++) {
							assertEquals(p.get(i-1), mutant.get(i));
						}
					} else {
						fail("Not an insertion.");
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedInsertion() {
		WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are insertions
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				if (mutant.get(b) == p.get(a)) {
					for (int i = a; i < b; i++) {
						assertEquals(p.get(i+1), mutant.get(i));
					}
				} else if (mutant.get(a) == p.get(b)) {
					for (int i = a+1; i <= b; i++) {
						assertEquals(p.get(i-1), mutant.get(i));
					}
				} else {
					fail("Not an insertion.");
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedReversal() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedReversalMutation m = new WindowLimitedReversalMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are reversals
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
					while (a <= b) {
						assertEquals(p.get(a), mutant.get(b));
						a++;
						b--;
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedReversal() {
		WindowLimitedReversalMutation m = new WindowLimitedReversalMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are reversals
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				while (a <= b) {
					assertEquals(p.get(a), mutant.get(b));
					a++;
					b--;
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedSwap() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedSwapMutation m = new WindowLimitedSwapMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are swaps
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
					assertEquals(p.get(a), mutant.get(b));
					assertEquals(p.get(b), mutant.get(a));
					for (int i = a+1; i < b; i++) {
						assertEquals(p.get(i), mutant.get(i));
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedSwap() {
		WindowLimitedSwapMutation m = new WindowLimitedSwapMutation();
		undoTester(m, 4);
		mutateTester(m, 4);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are swaps
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				assertEquals(p.get(a), mutant.get(b));
				assertEquals(p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals(p.get(i), mutant.get(i));
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedBlockMove() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][][] indexTriples = new boolean[n][n][n];
				int numSamples = n*(n-1)*(n+1)*40/6;
				int[] indexes = new int[3];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
				}
				checkIndexTriples(indexTriples, window);
			}
			// Verify mutations are block moves
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
					int c;
					for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
					// block of p from index a to index (a+b-c) should be same as mutant index c to index b
					int e = a;
					for (int d = c; d <= b; d++, e++) {
						assertEquals(p.get(e), mutant.get(d));
					}
					// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
					for (int d = a; e <= b; d++, e++) {
						assertEquals(p.get(e), mutant.get(d));
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedBlockMove() {
		WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][][] indexTriples = new boolean[n][n][n];
			int numSamples = n*(n-1)*(n+1)*40/6;
			int[] indexes = new int[3];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
			}
			checkIndexTriples(indexTriples);
		}
		// Verify mutations are block moves
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue(a <= b);
				int c;
				for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
				// block of p from index a to index (a+b-c) should be same as mutant index c to index b
				int e = a;
				for (int d = c; d <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
				// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
				for (int d = a; e <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedScramble() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation(window);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// verify window constraints
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedScramble() {
		WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation();
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testWindowLimitedUndoableScramble() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// verify window constraints
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue(a <= b);
					assertTrue(b-a <= window);
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedUndoableScramble() {
		WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testWindowLimitedMutationConstructorExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedUndoableScrambleMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedScrambleMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedBlockMoveMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedSwapMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedReversalMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedInsertionMutation(0)
		);
	}
	
	
	private void undoTester(UndoableMutationOperator<Permutation> m) {
		undoTester(m, 0);
	}
	
	private void undoTester(UndoableMutationOperator<Permutation> m, int minPermLength) {
		// iterate over different length permutations beginning with 0 length
		for (int i = minPermLength; i <= 6; i++) {
			Permutation p = new Permutation(i);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				m.undo(mutant);
				assertEquals(p, mutant);
			}
		}
	}
	
	private void mutateTester(MutationOperator<Permutation> m) {
		mutateTester(m, 0);
	}
	
	private void mutateTester(MutationOperator<Permutation> m, int minPermLength) {
		for (int i = minPermLength; i <= 6; i++) {
			Permutation p = new Permutation(i);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				// verify mutation produced a valid permutation
				validate(mutant);
				if (i < 2) {
					assertEquals(p, mutant);
				} else {
					assertNotEquals(p, mutant);
				}
			}
		}
	}
	
	private void validate(Permutation p) {
		boolean[] inP = new boolean[p.length()];
		for (int i = 0; i < inP.length; i++) {
			int j = p.get(i);
			assertTrue(j >= 0 && j < inP.length);
			inP[j] = true;
		}
		for (int i = 0; i < inP.length; i++) {
			assertTrue(inP[i]);
		}
	}
	
	private void splitTester(MutationOperator<Permutation> m) {
		MutationOperator<Permutation> s = m.split();
		assertEquals(m.getClass(), s.getClass());
		if (m instanceof UndoableMutationOperator) {
			for (int i = 0; i < 10; i++) {
				Permutation p1 = new Permutation(10);
				Permutation p2 = p1.copy();
				Permutation p3 = p1.copy();
				m.mutate(p2);
				assertNotEquals(p1, p2);
				UndoableMutationOperator<Permutation> m2 = (UndoableMutationOperator<Permutation>)m.split();
				m2.mutate(p3);
				assertNotEquals(p1, p3);
				((UndoableMutationOperator<Permutation>)m).undo(p2);
				assertEquals(p1, p2);
				m2.undo(p3);
				assertEquals(p1, p3);
			}
		}
	}
	
	private void checkIndexPairs(boolean[][] pairs) {
		// verify that each pair was generated over many trials
		for (int i = 0; i < pairs.length; i++) {
			for (int j = 0; j < pairs.length; j++) {
				if (i!=j) {
					assertTrue(pairs[i][j], "failed to generate: (" + i + ", " + j + ")");
				}
			}
		}
	}
	
	private void checkIndexPairs(boolean[][] pairs, int window) {
		// verify that each pair within window was generated over many trials
		// and verify no window violations
		for (int i = 0; i < pairs.length; i++) {
			for (int j = 0; j < pairs.length; j++) {
				if (i!=j) {
					if (Math.abs(i-j) <= window) {
						assertTrue(pairs[i][j], "failed to generate: w=" + window + " pair=(" + i + ", " + j + ")");
					} else {
						assertFalse(pairs[i][j], "window violation: w=" + window + " pair=(" + i + ", " + j + ")");
					}
				}
			}
		}
	}
	
	private void checkIndexTriples(boolean[][][] triples) {
		// verify that each triple was generated over many trials
		for (int i = 0; i < triples.length; i++) {
			for (int j = i+1; j < triples.length; j++) {
				for (int k = j; k < triples.length; k++) {
					assertTrue(triples[i][j][k], "failed to generate: (" + i + ", " + j + ", " + k + ")");
				}
			}
		}
	}
	
	private void checkIndexTriples(boolean[][][] triples, int window) {
		// verify that each triple was generated over many trials
		// and verify no window violations
		for (int i = 0; i < triples.length; i++) {
			for (int j = i+1; j < triples.length; j++) {
				for (int k = j; k < triples.length; k++) {
					if (k-i <= window) {
						assertTrue(triples[i][j][k], "failed to generate: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")");
					} else {
						assertFalse(triples[i][j][k], "window violation: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")");
					}
				}
			}
		}
	}
	
	private void checkIndexQuartets(boolean[][] firstPair, boolean[][] secondPair) {
		// verify that each of the possible left blocks was generated at least once
		// and each of the possible right blocks was generated at least once over
		// many trials.
		for (int i = 0; i < firstPair.length; i++) {
			for (int j = i; j < firstPair[i].length; j++) {
				assertTrue(firstPair[i][j], "failed to generate left pair: (" + i + ", " + j + ")");
				assertTrue(secondPair[i][j], "failed to generate right pair: (" + (i+1) + ", " + (j+1) + ")");
			}
		}
	}
}
