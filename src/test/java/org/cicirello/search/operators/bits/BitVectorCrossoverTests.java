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
 
package org.cicirello.search.operators.bits;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.BitVector;

public class BitVectorCrossoverTests {
	
	@Test
	public void testSinglePoint() {
		SinglePointCrossover crossover = new SinglePointCrossover();
		for (int n = 2; n <= 64; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertEquals(1, countNumberOfCrossPoints(b1, 1));
			assertEquals(1, countNumberOfCrossPoints(b2, 0));
			b2.not();
			assertEquals(b1, b2);
		}
		crossover = crossover.split();
		for (int n = 2; n <= 64; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertEquals(1, countNumberOfCrossPoints(b1, 1));
			assertEquals(1, countNumberOfCrossPoints(b2, 0));
			b2.not();
			assertEquals(b1, b2);
		}
		final SinglePointCrossover crossover2 = crossover;
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> crossover2.cross(new BitVector(1), new BitVector(1))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> crossover2.cross(new BitVector(5), new BitVector(4))
		);
	}
	
	@Test
	public void testTwoPoint() {
		TwoPointCrossover crossover = new TwoPointCrossover();
		// if n is 2, then equivalent to a single point
		BitVector b1 = new BitVector(2);
		BitVector b2 = new BitVector(2, 1.0);
		crossover.cross(b1, b2);
		assertEquals(1, countNumberOfCrossPoints(b1, 1));
		assertEquals(1, countNumberOfCrossPoints(b2, 0));
		b2.not();
		assertEquals(b1, b2);
		for (int n = 4; n <= 64; n*=2) {
			for (int i = 0; i < 5; i++) {
				b1 = new BitVector(n);
				b2 = new BitVector(n, 1.0);
				crossover.cross(b1, b2);
				int count1 = countNumberOfCrossPoints(b1, 0);
				int count2 = countNumberOfCrossPoints(b2, 1);
				assertEquals(2, count1);
				assertEquals(2, count2);
				b2.not();
				assertEquals(b1, b2);
			}
		}
		// if n is 2, then equivalent to a single point
		crossover = crossover.split();
		b1 = new BitVector(2);
		b2 = new BitVector(2, 1.0);
		crossover.cross(b1, b2);
		assertEquals(1, countNumberOfCrossPoints(b1, 1));
		assertEquals(1, countNumberOfCrossPoints(b2, 0));
		b2.not();
		assertEquals(b1, b2);
		for (int n = 4; n <= 64; n*=2) {
			for (int i = 0; i < 5; i++) {
				b1 = new BitVector(n);
				b2 = new BitVector(n, 1.0);
				crossover.cross(b1, b2);
				int count1 = countNumberOfCrossPoints(b1, 0);
				int count2 = countNumberOfCrossPoints(b2, 1);
				assertEquals(2, count1);
				assertEquals(2, count2);
				b2.not();
				assertEquals(b1, b2);
			}
		}
		final TwoPointCrossover crossover2 = crossover;
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> crossover2.cross(new BitVector(1), new BitVector(1))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> crossover2.cross(new BitVector(5), new BitVector(4))
		);
	}
	
	@Test
	public void testKPoint() {
		for (int k = 1; k < 8; k++) {
			KPointCrossover crossover = new KPointCrossover(k);
			final int MAX_N = 2*k*k;
			for (int n = k; n <= MAX_N; n*=2) {
				for (int i = 0; i < 5; i++) {
					BitVector b1 = new BitVector(n);
					BitVector b2 = new BitVector(n, 1.0);
					crossover.cross(b1, b2);
					int count1 = countNumberOfCrossPoints(b1, 0);
					int count2 = countNumberOfCrossPoints(b2, 1);
					assertEquals(k, count1);
					assertEquals(k, count2);
					b2.not();
					assertEquals(b1, b2);
				}
			}
			// test split version
			crossover = crossover.split();
			for (int n = k; n <= MAX_N; n*=2) {
				for (int i = 0; i < 5; i++) {
					BitVector b1 = new BitVector(n);
					BitVector b2 = new BitVector(n, 1.0);
					crossover.cross(b1, b2);
					int count1 = countNumberOfCrossPoints(b1, 0);
					int count2 = countNumberOfCrossPoints(b2, 1);
					assertEquals(k, count1);
					assertEquals(k, count2);
					b2.not();
					assertEquals(b1, b2);
				}
			}
			final KPointCrossover crossover2 = crossover;
			final int K_PRIME = k;
			IllegalArgumentException thrown = assertThrows( 
				IllegalArgumentException.class,
				() -> crossover2.cross(new BitVector(K_PRIME-1), new BitVector(K_PRIME-1))
			);
			thrown = assertThrows( 
				IllegalArgumentException.class,
				() -> crossover2.cross(new BitVector(K_PRIME+5), new BitVector(K_PRIME+4))
			);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new KPointCrossover(0)
		);
	}
	
	@Test
	public void testUniformCrossover() {
		UniformCrossover crossover = new UniformCrossover(0.0);
		for (int n = 1; n <= 64; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertTrue(b2.allOnes());
			assertTrue(b1.allZeros());
			b2.not();
			assertEquals(b1, b2);
		}
		crossover = new UniformCrossover(1.0);
		for (int n = 1; n <= 64; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertTrue(b1.allOnes());
			assertTrue(b2.allZeros());
			b2.not();
			assertEquals(b1, b2);
		}
		double[] rates = {0.33, 0.5, 0.66};
		for (double p : rates) {
			crossover = new UniformCrossover(p);
			for (int n = 32; n <= 128; n*=2) {
				BitVector b1 = new BitVector(n);
				BitVector b2 = new BitVector(n, 1.0);
				crossover.cross(b1, b2);
				assertFalse(b1.allOnes());
				assertFalse(b2.allOnes());
				assertFalse(b1.allZeros());
				assertFalse(b2.allZeros());
				b2.not();
				assertEquals(b1, b2);
			}
		}
		crossover = new UniformCrossover();
		for (int n = 32; n <= 128; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertFalse(b1.allOnes());
			assertFalse(b2.allOnes());
			assertFalse(b1.allZeros());
			assertFalse(b2.allZeros());
			b2.not();
			assertEquals(b1, b2);
		}
		// test split version
		crossover = crossover.split();
		for (int n = 32; n <= 128; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertFalse(b1.allOnes());
			assertFalse(b2.allOnes());
			assertFalse(b1.allZeros());
			assertFalse(b2.allZeros());
			b2.not();
			assertEquals(b1, b2);
		}
		final UniformCrossover crossover2 = crossover;
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> crossover2.cross(new BitVector(5), new BitVector(6))
		);
	}
	
	private int countNumberOfCrossPoints(BitVector b, int shouldStartWith) {
		// Assumes that parents were all 0s and all 1s
		int count = b.getBit(0) != shouldStartWith ? 1 : 0;
		for (int i = 1; i < b.length(); i++) {
			if (b.getBit(i) != b.getBit(i-1)) {
				count++;
			}
		}
		return count;
	}
}
