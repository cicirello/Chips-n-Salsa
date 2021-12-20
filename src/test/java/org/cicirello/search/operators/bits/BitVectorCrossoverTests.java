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
			assertEquals(1, countNumberOfCrossPoints(b1));
			assertEquals(1, countNumberOfCrossPoints(b2));
			b2.not();
			assertEquals(b1, b2);
		}
		crossover = crossover.split();
		for (int n = 2; n <= 64; n*=2) {
			BitVector b1 = new BitVector(n);
			BitVector b2 = new BitVector(n, 1.0);
			crossover.cross(b1, b2);
			assertEquals(1, countNumberOfCrossPoints(b1));
			assertEquals(1, countNumberOfCrossPoints(b2));
			b2.not();
			assertEquals(b1, b2);
		}
	}
	
	private int countNumberOfCrossPoints(BitVector b) {
		// Assumes that parents were all 0s and all 1s
		int count = 0;
		for (int i = 1; i < b.length(); i++) {
			if (b.getBit(i) != b.getBit(i-1)) {
				count++;
			}
		}
		return count;
	}
}
