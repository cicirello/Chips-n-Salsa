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

/**
 * JUnit test cases for permutation solution factories.
 */
public class PermutationInitializerTests {
	
	@Test
	public void testPermutationInitializer() {
		for (int n = 0; n <= 10; n++) {
			PermutationInitializer f = new PermutationInitializer(n);
			Permutation p = f.createCandidateSolution();
			assertEquals(n, p.length());
			validatePermutation(p);
			Permutation copy = p.copy();
			assertEquals(p, copy);
			assertTrue(p != copy);
			
			// split and create from split
			PermutationInitializer split = f.split();
			p = split.createCandidateSolution();
			assertEquals(n, p.length());
			validatePermutation(p);
			
			// test original initializer after the split
			p = f.createCandidateSolution();
			assertEquals(n, p.length());
			validatePermutation(p);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PermutationInitializer(-1)
		);
	}
	
	private void validatePermutation(Permutation p) {
		boolean[] a = new boolean[p.length()];
		for (int i = 0; i < a.length; i++) {
			a[p.get(i)] = true;
		}
		for (int i = 0; i < a.length; i++) {
			assertTrue(a[i], "Testing for valid permutation");
		}
	}
}
