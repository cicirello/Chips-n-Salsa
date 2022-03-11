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
 
package org.cicirello.search.ss;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit tests for the PartialPermutation class.
 */
public class PartialPermutationTests {
	
	@Test
	public void testConstructor() {
		for (int n = 0; n < 3; n++) {
			final PartialPermutation partial = new PartialPermutation(n);
			if (n > 0) assertFalse(partial.isComplete());
			else assertTrue(partial.isComplete());
			assertEquals(0, partial.size());
			assertEquals(n, partial.numExtensions());
			for (int i = 0; i < n; i++) {
				assertEquals(i, partial.getExtension(i));
			}
			final int m = n;
			ArrayIndexOutOfBoundsException thrown = assertThrows( 
				ArrayIndexOutOfBoundsException.class,
				() -> partial.getExtension(m)
			);
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialPermutation(-1)
		);
	}
	
	@Test
	public void testToComplete() {
		for (int n = 0; n < 5; n++) {
			PartialPermutation partial = new PartialPermutation(n);
			Permutation p = partial.toComplete();
			assertNotNull(p);
			if (n > 0) assertFalse(partial.isComplete());
			else assertTrue(partial.isComplete());
			for (int i = 0; i < n; i++) {
				assertEquals(i, p.get(i));
			}
			for (int j = 0; j < n; j++) {
				partial.extend(n-1-j);
				p = partial.toComplete();
				assertNotNull(p);
				for (int i = 0; i <= j; i++) {
					assertEquals(n-1-i, p.get(i));
				}
				for (int i = j+1, k=0; i < n; i++, k++) {
					assertEquals(k, p.get(i));
				}
				if (j < n - 1) assertFalse(partial.isComplete());
				else assertTrue(partial.isComplete());
			}
		}
	}
	
	@Test
	public void testExtend() {
		for (int n = 1; n < 5; n++) {
			final PartialPermutation partial = new PartialPermutation(n);
			for (int i = 0; i < n; i++) {
				assertFalse(partial.isComplete());
				assertEquals(n-i, partial.numExtensions());
				for (int j = 0; j < n-i; j++) {
					assertEquals(j, partial.getExtension(j));
				}
				assertEquals(i, partial.size());
				final int m = n - i;
				ArrayIndexOutOfBoundsException thrown = assertThrows( 
					ArrayIndexOutOfBoundsException.class,
					() -> partial.getExtension(m)
				);
				for (int j = 0; j < i; j++) {
					assertEquals(n-1-j, partial.get(j));
				}
				final int k = i;
				thrown = assertThrows( 
					ArrayIndexOutOfBoundsException.class,
					() -> partial.get(k)
				);
				partial.extend(n-1-i);
			}
			assertTrue(partial.isComplete());
			assertEquals(0, partial.numExtensions());
			assertEquals(n, partial.size());
			for (int j = 0; j < n; j++) {
				assertEquals(n-1-j, partial.get(j));
			}
		}
		for (int n = 1; n < 5; n++) {
			PartialPermutation partial = new PartialPermutation(n);
			for (int i = 0; i < n; i++) {
				assertFalse(partial.isComplete());
				assertEquals(n-i, partial.numExtensions());
				assertEquals(i, partial.size());
				partial.extend(0);
				assertEquals(n-i-1, partial.numExtensions());
				assertEquals(i+1, partial.size());
				assertEquals(0, partial.get(0));
				for (int j = 1; j < i; j++) {
					assertEquals(n-j, partial.get(j));
				}
				if (i < n-1) {
					assertEquals(n-1-i, partial.getExtension(0));
					for (int j = 1; j < n-i-1; j++) {
						assertEquals(j, partial.getExtension(j));
					}
				}
				final int m = n-i-1;
				ArrayIndexOutOfBoundsException thrown = assertThrows( 
					ArrayIndexOutOfBoundsException.class,
					() -> partial.extend(m)
				);
			}
			assertTrue(partial.isComplete());
			assertEquals(0, partial.numExtensions());
			assertEquals(n, partial.size());
		}
	}
	
}