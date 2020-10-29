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
 
package org.cicirello.search.problems;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.operators.integers.IntegerVectorInitializer;

/**
 * JUnit 4 test cases for the the BoundMax problem.
 */
public class BoundMaxTests {
	
	@Test
	public void testInitializerMethods() {
		for (int n = 0; n < 5; n++) {
			for (int bound = 0; bound < 5; bound++) {
				BoundMax b = new BoundMax(n, bound);
				for (int i = 0; i < 10; i++) {
					IntegerVector x = b.createCandidateSolution();
					assertEquals(n, x.length());
					for (int j = 0; j < n; j++) {
						int y = x.get(j);
						assertTrue(y <= bound && y >= 0);
					}
					IntegerVector z = x.copy(); 
					assertTrue(z!=x);
					assertEquals(x, z);
				}
			}
		}
	}
	
	@Test
	public void testValueCostMethods() {
		for (int n = 0; n < 5; n++) {
			for (int bound = 1; bound < 5; bound++) {
				BoundMax b = new BoundMax(n, bound);
				for (int count = 0; count <= n; count++) {
					int[] v = new int[n];
					for (int j = 0; j < count; j++) {
						v[j] = bound;
					}
					for (int j = count; j < n; j++) {
						v[j] = j % bound;
					}
					IntegerVector x = new IntegerVector(v);
					assertEquals(count, b.value(x));
					assertEquals(n-count, b.cost(x));
					assertEquals(0, b.value(null));
				}
			}
		}
		int n = 7;
		int bound = 4;
		BoundMax b = new BoundMax(n, bound);
		int[] vShort = { 4, 3, 4, 4, 3, 4 };
		IntegerVector x = new IntegerVector(vShort);
		assertEquals(4, b.value(x));
		assertEquals(3, b.cost(x));	
	}
	
	@Test
	public void testIsMinCost() {
		for (int n = 0; n < 5; n++) {
			for (int bound = 1; bound < 5; bound++) {
				BoundMax b = new BoundMax(n, bound);
				assertEquals(0, b.minCost());
				assertTrue(b.isMinCost(0));
				for (int i = 1; i < 5; i++) {
					assertFalse(b.isMinCost(i));
				}
			}
		}
	}
}