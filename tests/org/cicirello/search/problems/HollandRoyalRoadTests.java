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
 
package org.cicirello.search.problems;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.BitVector;

/**
 * JUnit 4 test cases for the HollandRoyalRoad problem.
 */
public class HollandRoyalRoadTests {
	
	@Test
	public void testWithJonesExample() {
		HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
		assertEquals(0.0, problem.minCost(), 0.0);
		assertTrue(problem.isMinCost(0.0));
		assertTrue(problem.isMinCost(-1E-10));
		assertFalse(problem.isMinCost(1E-10));
		assertEquals(240, problem.supportedBitVectorLength());
		BitVector v = new BitVector(240);
		v.not();
		assertEquals(12.8, problem.value(v), 1E-10);
		assertEquals(0, problem.cost(v), 1E-10);
		int bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(12.8, problem.value(v), 1E-10);
		assertEquals(0, problem.cost(v), 1E-10);
	}
	
	@Test
	public void testWithAllZeros() {
		HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
		BitVector v = new BitVector(240);
		assertEquals(0, problem.value(v), 1E-10);
		assertEquals(12.8, problem.cost(v), 1E-10);
		int bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(0, problem.value(v), 1E-10);
		assertEquals(12.8, problem.cost(v), 1E-10);
	}
	
	@Test
	public void testMaximizePenalty() {
		HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
		BitVector v = new BitVector(240);
		v.not();
		for (int i = 7; i < 240; i += 15) {
			v.flip(i);
		}
		assertEquals(-0.06*16, problem.value(v), 1E-10);
		assertEquals(12.8+0.06*16, problem.cost(v), 1E-10);
		int bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(-0.06*16, problem.value(v), 1E-10);
		assertEquals(12.8+0.06*16, problem.cost(v), 1E-10);
	}
	
	@Test
	public void testAlternatingBlocksComplete() {
		HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
		BitVector v = new BitVector(240);
		v.not();
		for (int i = 15; i < 240; i += 30) {
			v.flip(i);
		}
		double expected = 3.1 - 0.48;
		assertEquals(expected, problem.value(v), 1E-10);
		assertEquals(12.8-expected, problem.cost(v), 1E-10);
		int bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(expected, problem.value(v), 1E-10);
		assertEquals(12.8-expected, problem.cost(v), 1E-10);
		
		v = new BitVector(240);
		v.not();
		for (int i = 0; i < 240; i += 30) {
			v.flip(i);
		}
		assertEquals(expected, problem.value(v), 1E-10);
		assertEquals(12.8-expected, problem.cost(v), 1E-10);
		bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(expected, problem.value(v), 1E-10);
		assertEquals(12.8-expected, problem.cost(v), 1E-10);
	}
	
	@Test
	public void testBlockSizeSameAsMstar() {
		HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 8, 0.02, 1.0, 0.3);
		BitVector v = new BitVector(240);
		v.not();
		assertEquals(12.8, problem.value(v), 1E-10);
		assertEquals(0, problem.cost(v), 1E-10);
		int bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(12.8, problem.value(v), 1E-10);
		assertEquals(0, problem.cost(v), 1E-10);
		
		v = new BitVector(240);
		assertEquals(0.0, problem.value(v), 1E-10);
		assertEquals(12.8, problem.cost(v), 1E-10);
		bit = 0;
		do {
			bit += 8;
			for (int i = 0; i < 7; i++) {
				v.flip(bit);
				bit++;
			}
		} while (bit < 240);
		assertEquals(0.0, problem.value(v), 1E-10);
		assertEquals(12.8, problem.cost(v), 1E-10);
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(-1, 1, 0, 0, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 0, 0, 0, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, -1, 0, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, 0, -1, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, 0, 1, -1E-10, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, 0, 1, 0.0, -1E-10, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, 0, 1, 0.0, 0.0, -1E-10)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 1, 0, 2, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HollandRoyalRoad(0, 8, 0, 9, 0.0, 0.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
				problem.value(new BitVector(239));
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
				problem.value(new BitVector(241));
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
				problem.cost(new BitVector(239));
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				HollandRoyalRoad problem = new HollandRoyalRoad(4, 8, 7, 4, 0.02, 1.0, 0.3);
				problem.cost(new BitVector(241));
			}
		);
	}
}