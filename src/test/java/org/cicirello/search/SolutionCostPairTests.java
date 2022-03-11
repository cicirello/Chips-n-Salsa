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
 
package org.cicirello.search;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.util.Copyable;

/**
 * JUnit test cases for the SolutionCostPair.
 */
public class SolutionCostPairTests {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testSolutionCostPair() {
		TestCopyable s = new TestCopyable(5);
		SolutionCostPair<TestCopyable> pairInt = new SolutionCostPair<TestCopyable>(s, 10, false);
		assertEquals(s, pairInt.getSolution());
		assertTrue(pairInt.containsIntCost());
		assertEquals(10, pairInt.getCost());
		assertEquals(10.0, pairInt.getCostDouble(), EPSILON);
		assertFalse(pairInt.containsKnownOptimal());
		
		pairInt = new SolutionCostPair<TestCopyable>(s, 10, true);
		assertTrue(pairInt.containsKnownOptimal());
		
		SolutionCostPair<TestCopyable> pairDouble = new SolutionCostPair<TestCopyable>(s, 10.0, false);
		assertEquals(s, pairDouble.getSolution());
		assertFalse(pairDouble.containsIntCost());
		assertEquals(10.0, pairDouble.getCostDouble(), EPSILON);
		assertFalse(pairDouble.containsKnownOptimal());
		
		pairDouble = new SolutionCostPair<TestCopyable>(s, 10.0, true);
		assertTrue(pairDouble.containsKnownOptimal());
	}
	
	@Test
	public void testCompareTo() {
		SolutionCostPair<TestCopyable> p1 = new SolutionCostPair<TestCopyable>(new TestCopyable(1), 5, false);
		SolutionCostPair<TestCopyable> p2 = new SolutionCostPair<TestCopyable>(new TestCopyable(2), 5, false);
		SolutionCostPair<TestCopyable> p3 = new SolutionCostPair<TestCopyable>(new TestCopyable(3), 4, false);
		SolutionCostPair<TestCopyable> p4 = new SolutionCostPair<TestCopyable>(new TestCopyable(4), 6, false);
		SolutionCostPair<TestCopyable> p5 = new SolutionCostPair<TestCopyable>(new TestCopyable(5), 3, false);
		SolutionCostPair<TestCopyable> p6 = new SolutionCostPair<TestCopyable>(new TestCopyable(6), 7, false);
		assertEquals(0, p1.compareTo(p2));
		assertEquals(0, p2.compareTo(p1));
		assertEquals(1, p1.compareTo(p3));
		assertEquals(-1, p3.compareTo(p1));
		assertEquals(-1, p1.compareTo(p4));
		assertEquals(1, p4.compareTo(p1));
		assertEquals(2, p1.compareTo(p5));
		assertEquals(-2, p5.compareTo(p1));
		assertEquals(-2, p1.compareTo(p6));
		assertEquals(2, p6.compareTo(p1));
		p1 = new SolutionCostPair<TestCopyable>(new TestCopyable(1), 5.0, false);
		p2 = new SolutionCostPair<TestCopyable>(new TestCopyable(2), 5.0, false);
		p3 = new SolutionCostPair<TestCopyable>(new TestCopyable(3), 4.0, false);
		p4 = new SolutionCostPair<TestCopyable>(new TestCopyable(4), 6.0, false);
		p5 = new SolutionCostPair<TestCopyable>(new TestCopyable(5), 3.0, false);
		p6 = new SolutionCostPair<TestCopyable>(new TestCopyable(6), 7.0, false);
		assertEquals(0, p1.compareTo(p2));
		assertEquals(0, p2.compareTo(p1));
		assertEquals(1, p1.compareTo(p3));
		assertEquals(-1, p3.compareTo(p1));
		assertEquals(-1, p1.compareTo(p4));
		assertEquals(1, p4.compareTo(p1));
		assertEquals(1, p1.compareTo(p5));
		assertEquals(-1, p5.compareTo(p1));
		assertEquals(-1, p1.compareTo(p6));
		assertEquals(1, p6.compareTo(p1));
	}
	
	private static class TestCopyable implements Copyable<TestCopyable> {
		
		int a;
		
		public TestCopyable(int a) { this.a = a; }
		
		@Override
		public TestCopyable copy() {
			return new TestCopyable(a);
		}
		
		@Override
		public boolean equals(Object other) {
			return other != null && ((TestCopyable)other).a == a;
		}
	}
}