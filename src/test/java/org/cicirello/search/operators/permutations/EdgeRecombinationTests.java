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
 * JUnit test cases for EdgeRecombination.
 */
public class EdgeRecombinationTests {
	
	@Test
	public void testEdgeMapAnyRemaining() {
		int[] raw1 = {0, 1, 2, 3, 4, 5, 6, 7};
		int[] raw2 = {2, 7, 3, 6, 0, 5, 1, 4};
		boolean[] used = new boolean[raw1.length];
		EdgeRecombination.EdgeMap map = new EdgeRecombination.EdgeMap(raw1, raw2);
		for (int i = 0; i < raw1.length; i++) {
			int element = map.anyRemaining();
			assertFalse(used[element]);
			map.used(element);
			used[element] = true;
		}
		assertEquals(-1, map.anyRemaining());
	}
	
	@Test
	public void testEdgeMapPick() {
		int[] raw1 = {0, 1, 2, 3, 4, 5, 6, 7};
		int[] raw2 = {2, 7, 3, 6, 0, 5, 1, 4};
		boolean[] used = new boolean[raw1.length];
		EdgeRecombination.EdgeMap map2 = new EdgeRecombination.EdgeMap(raw1, raw2);
		EdgeRecombination.EdgeMap map1 = new EdgeRecombination.EdgeMap(map2);
		map1.used(0);
		int element = map1.pick(0);
		assertTrue(element == 7 || element == 1 || element == 5 || element == 6);
		map1.used(5);
		element = map1.pick(5);
		assertTrue(element == 1 || element == 6);
		map1.used(1);
		element = map1.pick(1);
		assertEquals(4, element);
		map1.used(4);
		element = map1.pick(4);
		assertEquals(2, element);
		map1.used(2);
		element = map1.pick(2);
		assertTrue(element == 3 || element == 7);
		map1.used(7);
		element = map1.pick(7);
		assertTrue(element == 3 || element == 6);
		map1.used(3);
		element = map1.pick(3);
		assertEquals(6, element);
		map1.used(6);
		element = map1.pick(6);
		assertEquals(-1, element);
	}
	
	@Test
	public void testEdgeMapPickAllCommon() {
		int[] raw1 = {7, 6, 5, 4, 3, 2, 1, 0};
		int[] raw2 = {0, 1, 2, 3,4 , 5, 6, 7};
		boolean[] used = new boolean[raw1.length];
		EdgeRecombination.EdgeMap map1 = new EdgeRecombination.EdgeMap(raw1, raw2);
		EdgeRecombination.EdgeMap map2 = new EdgeRecombination.EdgeMap(map1);
		map1.used(7);
		int element = map1.pick(7);
		assertTrue(element == 0 || element == 6);
		map1.used(0);
		element = map1.pick(0);
		assertEquals(1, element);
		map1.used(1);
		element = map1.pick(1);
		assertEquals(2, element);
		map1.used(2);
		element = map1.pick(2);
		assertEquals(3, element);
		map1.used(3);
		element = map1.pick(3);
		assertEquals(4, element);
		map1.used(4);
		element = map1.pick(4);
		assertEquals(5, element);
		map1.used(5);
		element = map1.pick(5);
		assertEquals(6, element);
		map1.used(6);
		element = map1.pick(6);
		assertEquals(-1, element);
	}
	
	@Test
	public void testEdgeMapLength2() {
		int[] raw1 = {1, 0};
		int[] raw2 = {0, 1};
		EdgeRecombination.EdgeMap map1 = new EdgeRecombination.EdgeMap(raw1, raw2);
		map1.used(1);
		int element = map1.pick(1);
		assertEquals(0, element);
		map1.used(0);
		element = map1.pick(0);
		assertEquals(-1, element);
	}
	
	@Test
	public void testEdgeRecombination() {
		EdgeRecombination er = new EdgeRecombination();
		for (int n = 1; n <= 32; n *= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation child1 = new Permutation(p1);
			Permutation child2 = new Permutation(p2);
			er.cross(child1, child2);
			assertTrue(validPermutation(child1));
			assertTrue(validPermutation(child2));
		}
		assertSame(er, er.split());
	}
	
	private boolean validPermutation(Permutation p) {
		boolean[] foundIt = new boolean[p.length()];
		for (int i = 0; i < p.length(); i++) {
			if (foundIt[p.get(i)]) return false;
			foundIt[p.get(i)] = true;
		}
		return true;
	}
}
