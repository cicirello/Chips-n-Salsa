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
 
package org.cicirello.search.operators.permutations;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit 4 test cases for permutation solution factories.
 */
public class PermutationInitializerTests {
	
	@Test
	public void testPermutationInitializer() {
		for (int n = 0; n <= 10; n++) {
			PermutationInitializer f = new PermutationInitializer(n);
			Permutation p = f.createCandidateSolution();
			assertEquals("Testing length of generated permutations.", n, p.length());
			validatePermutation(p);
			Permutation copy = p.copy();
			assertEquals("Testing p.copy() copied correctly", p, copy);
			assertTrue("Testing copy is different object", p != copy);
			
			// split and create from split
			PermutationInitializer split = f.split();
			p = split.createCandidateSolution();
			assertEquals("Testing length of generated permutations.", n, p.length());
			validatePermutation(p);
			
			// test original initializer after the split
			p = f.createCandidateSolution();
			assertEquals("Testing length of generated permutations.", n, p.length());
			validatePermutation(p);
		}
	}
	
	private void validatePermutation(Permutation p) {
		boolean[] a = new boolean[p.length()];
		for (int i = 0; i < a.length; i++) {
			a[p.get(i)] = true;
		}
		for (int i = 0; i < a.length; i++) {
			assertTrue("Testing for valid permutation", a[i]);
		}
	}
}