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
 
package org.cicirello.search.problems;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;

/**
 * JUnit test cases for the cost function scaler classes.
 */
public class CostScalerTests {
	
	@Test
	public void testCostScaler() {
		TestProblem problem = new TestProblem();
		CostFunctionScaler<TestObject> cfs = new CostFunctionScaler<TestObject>(problem, 10);
		for (int i = 5; i <= 7; i++) {
			assertEquals(10.0*i, cfs.cost(new TestObject(i)), 0.0);
			assertEquals(1.0*i, cfs.value(new TestObject(i)), 0.0);
		}
		assertEquals(50.0, cfs.minCost(), 0.0);
		assertTrue(cfs.isMinCost(50.0));
		assertFalse(cfs.isMinCost(50.1));
		assertFalse(cfs.isMinCost(5.0));
		SolutionCostPair<TestObject> pair = cfs.getSolutionCostPair(new TestObject(6));
		assertEquals(6, pair.getSolution().c);
		assertEquals(60.0, pair.getCostDouble(), 0.0);
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostFunctionScaler<TestObject>(problem, 0)
		);
	}
	
	@Test
	public void testIntegerCostScaler() {
		TestProblemInt problem = new TestProblemInt();
		IntegerCostFunctionScaler<TestObject> cfs = new IntegerCostFunctionScaler<TestObject>(problem, 10);
		for (int i = 5; i <= 7; i++) {
			assertEquals(10*i, cfs.cost(new TestObject(i)));
			assertEquals(i, cfs.value(new TestObject(i)));
		}
		assertEquals(50, cfs.minCost());
		assertTrue(cfs.isMinCost(50));
		assertFalse(cfs.isMinCost(51));
		assertFalse(cfs.isMinCost(5));
		SolutionCostPair<TestObject> pair = cfs.getSolutionCostPair(new TestObject(6));
		assertEquals(6, pair.getSolution().c);
		assertEquals(60, pair.getCost());
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new IntegerCostFunctionScaler<TestObject>(problem, 0)
		);
	}
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		
		public double cost(TestObject candidate) {
			return candidate.c;
		}
		
		public double minCost() {
			return 5;
		}
		
		public double value(TestObject candidate) {
			return candidate.c;
		}
	}
	
	private static class TestProblemInt implements IntegerCostOptimizationProblem<TestObject> {
		
		public int cost(TestObject candidate) {
			return candidate.c;
		}
		
		public int minCost() {
			return 5;
		}
		
		public int value(TestObject candidate) {
			return candidate.c;
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		int c;
		public TestObject(int c) { this.c = c; }
		public TestObject copy() {
			return new TestObject(c);
		}
	}
}