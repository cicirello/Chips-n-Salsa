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


/**
 * JUnit 4 test cases for the classes that implement Initializer for the
 * BitVector class.
 */
public class BitVectorInitializerTests {
	
	
	@Test
	public void testCreateCorrectLength() {
		for (int n = 0; n <= 33; n++) {
			BitVectorInitializer fact = new BitVectorInitializer(n);
			BitVector v = fact.createCandidateSolution();
			assertEquals(n, v.length());
		}
	}
	
	@Test
	public void testCreateDifferent() {
		for (int n = 1; n <= 33; n++) {
			BitVectorInitializer fact = new BitVectorInitializer(n);
			BitVector v = fact.createCandidateSolution();
			boolean foundOne = false;
			for (int i = 0; !foundOne && i < 100; i++) {
				BitVector v2 = fact.createCandidateSolution();
				if (!v.equals(v2)) foundOne = true;
			}
			assertTrue(foundOne);
		}
	}
	
}
