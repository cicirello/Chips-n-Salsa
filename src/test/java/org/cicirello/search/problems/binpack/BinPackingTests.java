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
 
package org.cicirello.search.problems.binpack;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit test cases for the the BinPacking problem related classes.
 */
public class BinPackingTests {
	
	@Test
	public void testBin() {
		final Bin b = new Bin(100);
		assertEquals(0, b.size());
		assertEquals(100, b.space());
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> b.addItem(99, 101)
		);
		assertEquals(0, b.size());
		assertEquals(100, b.space());
		
		b.addItem(42, 34);
		assertEquals(1, b.size());
		assertEquals(66, b.space());
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> b.addItem(99, 67)
		);
		assertEquals(1, b.size());
		assertEquals(66, b.space());
		assertEquals(42, b.getItem(0));
		
		b.addItem(13, 40);
		assertEquals(2, b.size());
		assertEquals(26, b.space());
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> b.addItem(99, 27)
		);
		assertEquals(2, b.size());
		assertEquals(26, b.space());
		assertEquals(42, b.getItem(0));
		assertEquals(13, b.getItem(1));
		
		b.addItem(101, 26);
		assertEquals(3, b.size());
		assertEquals(0, b.space());
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> b.addItem(99, 1)
		);
		assertEquals(3, b.size());
		assertEquals(0, b.space());
		assertEquals(42, b.getItem(0));
		assertEquals(13, b.getItem(1));
		assertEquals(101, b.getItem(2));
	}
	
	@Test
	public void testBinPackingSolution() {
		int capacity = 100;
		int[] items = { 54, 16, 31, 30, 60, 10, 9 };
		int[] p1 = {0, 1, 2, 3, 4, 5, 6};
		BinPackingSolution solution = new BinPackingSolution(new Permutation(p1), capacity, items);
		assertEquals(3, solution.cost());
		Bin b = solution.getBin(0);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(0, b.getItem(0));
		assertEquals(1, b.getItem(1));
		assertEquals(3, b.getItem(2));
		b = solution.getBin(1);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(2, b.getItem(0));
		assertEquals(4, b.getItem(1));
		assertEquals(6, b.getItem(2));
		b = solution.getBin(2);
		assertEquals(90, b.space());
		assertEquals(1, b.size());
		assertEquals(5, b.getItem(0));
		
		int[] p2 = {6, 5, 4, 3, 2, 1, 0};
		solution = new BinPackingSolution(new Permutation(p2), capacity, items);
		assertEquals(3, solution.cost());
		b = solution.getBin(0);
		assertEquals(5, b.space());
		assertEquals(4, b.size());
		assertEquals(6, b.getItem(0));
		assertEquals(5, b.getItem(1));
		assertEquals(4, b.getItem(2));
		assertEquals(1, b.getItem(3));
		b = solution.getBin(1);
		assertEquals(39, b.space());
		assertEquals(2, b.size());
		assertEquals(3, b.getItem(0));
		assertEquals(2, b.getItem(1));
		b = solution.getBin(2);
		assertEquals(46, b.space());
		assertEquals(1, b.size());
		assertEquals(0, b.getItem(0));
	}
	
	@Test
	public void testBaseClassNotAllFull() {
		int capacity = 100;
		int[] items = { 54, 16, 31, 30, 60, 10, 9 };
		BinPacking problem = new BinPacking(capacity, items.clone());
		assertEquals(capacity, problem.getCapacity());
		assertEquals(items.length, problem.numItems());
		for (int i = 0; i < items.length; i++) {
			assertEquals(items[i], problem.getSize(i));
		}
		assertEquals(3, problem.minCost());
		
		int[] p1 = {0, 1, 2, 3, 4, 5, 6};
		Permutation perm = new Permutation(p1);
		BinPackingSolution solution = problem.permutationToBinPackingSolution(perm);
		assertEquals(3, solution.cost());
		assertEquals(3, problem.cost(perm));
		assertEquals(3, problem.value(perm));
		Bin b = solution.getBin(0);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(0, b.getItem(0));
		assertEquals(1, b.getItem(1));
		assertEquals(3, b.getItem(2));
		b = solution.getBin(1);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(2, b.getItem(0));
		assertEquals(4, b.getItem(1));
		assertEquals(6, b.getItem(2));
		b = solution.getBin(2);
		assertEquals(90, b.space());
		assertEquals(1, b.size());
		assertEquals(5, b.getItem(0));
		
		int[] p2 = {6, 5, 4, 3, 2, 1, 0};
		perm = new Permutation(p2);
		solution = problem.permutationToBinPackingSolution(perm);
		assertEquals(3, solution.cost());
		assertEquals(3, problem.cost(perm));
		assertEquals(3, problem.value(perm));
		b = solution.getBin(0);
		assertEquals(5, b.space());
		assertEquals(4, b.size());
		assertEquals(6, b.getItem(0));
		assertEquals(5, b.getItem(1));
		assertEquals(4, b.getItem(2));
		assertEquals(1, b.getItem(3));
		b = solution.getBin(1);
		assertEquals(39, b.space());
		assertEquals(2, b.size());
		assertEquals(3, b.getItem(0));
		assertEquals(2, b.getItem(1));
		b = solution.getBin(2);
		assertEquals(46, b.space());
		assertEquals(1, b.size());
		assertEquals(0, b.getItem(0));
	}
	
	@Test
	public void testBaseClassPossibleToFillAll() {
		int capacity = 100;
		int[] items = { 54, 16, 31, 30, 60, 10, 9, 90 };
		BinPacking problem = new BinPacking(capacity, items.clone());
		assertEquals(capacity, problem.getCapacity());
		assertEquals(items.length, problem.numItems());
		for (int i = 0; i < items.length; i++) {
			assertEquals(items[i], problem.getSize(i));
		}
		assertEquals(3, problem.minCost());
		
		int[] p1 = {0, 1, 2, 3, 4, 5, 6, 7};
		Permutation perm = new Permutation(p1);
		BinPackingSolution solution = problem.permutationToBinPackingSolution(perm);
		assertEquals(3, solution.cost());
		assertEquals(3, problem.cost(perm));
		assertEquals(3, problem.value(perm));
		Bin b = solution.getBin(0);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(0, b.getItem(0));
		assertEquals(1, b.getItem(1));
		assertEquals(3, b.getItem(2));
		b = solution.getBin(1);
		assertEquals(0, b.space());
		assertEquals(3, b.size());
		assertEquals(2, b.getItem(0));
		assertEquals(4, b.getItem(1));
		assertEquals(6, b.getItem(2));
		b = solution.getBin(2);
		assertEquals(0, b.space());
		assertEquals(2, b.size());
		assertEquals(5, b.getItem(0));
		assertEquals(7, b.getItem(1));
		
		int[] p2 = {6, 5, 4, 3, 2, 1, 0, 7};
		perm = new Permutation(p2);
		solution = problem.permutationToBinPackingSolution(perm);
		assertEquals(4, solution.cost());
		assertEquals(4, problem.cost(perm));
		assertEquals(4, problem.value(perm));
		b = solution.getBin(0);
		assertEquals(5, b.space());
		assertEquals(4, b.size());
		assertEquals(6, b.getItem(0));
		assertEquals(5, b.getItem(1));
		assertEquals(4, b.getItem(2));
		assertEquals(1, b.getItem(3));
		b = solution.getBin(1);
		assertEquals(39, b.space());
		assertEquals(2, b.size());
		assertEquals(3, b.getItem(0));
		assertEquals(2, b.getItem(1));
		b = solution.getBin(2);
		assertEquals(46, b.space());
		assertEquals(1, b.size());
		assertEquals(0, b.getItem(0));
		b = solution.getBin(3);
		assertEquals(10, b.space());
		assertEquals(1, b.size());
		assertEquals(7, b.getItem(0));
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BinPacking(90, new int[] {91, 1, 1})
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BinPacking(90, new int[] {1, 91, 1})
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BinPacking(90, new int[] {1, 1, 91})
		);
		
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BinPacking.UniformRandom(10, 100, 21, 20)
		);
		NegativeArraySizeException thrown2 = assertThrows( 
			NegativeArraySizeException.class,
			() -> new BinPacking.UniformRandom(-1, 100, 21, 30)
		);
		thrown2 = assertThrows( 
			NegativeArraySizeException.class,
			() -> new BinPacking.Triplet(-1)
		);
	}
	
	@Test
	public void testUniformRandomOnlyNumItems() {
		for (int numItems = 0; numItems <= 10; numItems++) {
			BinPacking.UniformRandom problem = new BinPacking.UniformRandom(numItems);
			assertEquals(150, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			for (int i = 0; i < numItems; i++) {
				assertTrue(problem.getSize(i) >= 20);
				assertTrue(problem.getSize(i) <= 100);
			}
		}
	}
	
	@Test
	public void testUniformRandomNumItemsSeed() {
		for (int numItems = 0; numItems <= 10; numItems++) {
			BinPacking.UniformRandom problem = new BinPacking.UniformRandom(numItems, 42);
			assertEquals(150, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			for (int i = 0; i < numItems; i++) {
				assertTrue(problem.getSize(i) >= 20);
				assertTrue(problem.getSize(i) <= 100);
			}
		}
	}
	
	@Test
	public void testUniformRandomAllButSeed() {
		for (int numItems = 0; numItems <= 10; numItems++) {
			BinPacking.UniformRandom problem = new BinPacking.UniformRandom(numItems, 99, 10, 20);
			assertEquals(99, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			for (int i = 0; i < numItems; i++) {
				assertTrue(problem.getSize(i) >= 10);
				assertTrue(problem.getSize(i) <= 20);
			}
		}
	}
	
	@Test
	public void testUniformRandomAllParams() {
		for (int numItems = 0; numItems <= 10; numItems++) {
			BinPacking.UniformRandom problem = new BinPacking.UniformRandom(numItems, 99, 10, 20, 42);
			assertEquals(99, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			for (int i = 0; i < numItems; i++) {
				assertTrue(problem.getSize(i) >= 10);
				assertTrue(problem.getSize(i) <= 20);
			}
		}
	}
	
	@Test
	public void testUniformRandomForceSize() {
		for (int numItems = 0; numItems <= 10; numItems++) {
			BinPacking.UniformRandom problem = new BinPacking.UniformRandom(numItems, 99, 8, 8);
			assertEquals(99, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			for (int i = 0; i < numItems; i++) {
				assertEquals(8, problem.getSize(i));
			}
		}
	}
	
	@Test
	public void testTripletNoSeed() {
		for (int numTriplets = 0; numTriplets <= 5; numTriplets++) {
			int numItems = numTriplets * 3;
			BinPacking.Triplet problem = new BinPacking.Triplet(numTriplets);
			assertEquals(1000, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			int total = 0;
			int count = 0;
			for (int i = 0; i < numItems; i++) {
				total += problem.getSize(i);
				if (problem.getSize(i) >= 380 && problem.getSize(i) <= 490) {
					count++;
				}
			}
			assertEquals(1000 * numTriplets, total);
			assertEquals(numTriplets, count);
		}
	}
	
	@Test
	public void testTripletSeed() {
		for (int numTriplets = 0; numTriplets <= 5; numTriplets++) {
			int numItems = numTriplets * 3;
			BinPacking.Triplet problem = new BinPacking.Triplet(numTriplets, 42);
			assertEquals(1000, problem.getCapacity());
			assertEquals(numItems, problem.numItems());
			int total = 0;
			int count = 0;
			for (int i = 0; i < numItems; i++) {
				total += problem.getSize(i);
				if (problem.getSize(i) >= 380 && problem.getSize(i) <= 490) {
					count++;
				}
			}
			assertEquals(1000 * numTriplets, total);
			assertEquals(numTriplets, count);
		}
	}
}
