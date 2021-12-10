/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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

package org.cicirello.search.evo;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.util.Copyable;

/**
 * JUnit 4 test cases for CostToFitness.
 */
public class CostToFitnessTests {
	
	@Test
	public void testDoubleToFitness1() {
		TestProblemDouble problem = new TestProblemDouble(0);
		CostToFitness<TestObject> fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(3)), 1E-10);
		
		problem = new TestProblemDouble(1);
		fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(2)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(4)), 1E-10);
		
		problem = new TestProblemDouble(-1);
		fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(-1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(2)), 1E-10);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostToFitness<TestObject>(new TestProblemDouble(0), 0.0)
		);
		
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostToFitness<TestObject>(new TestProblemDouble(Double.POSITIVE_INFINITY))
		);
	}
	
	@Test
	public void testDoubleToFitness2() {
		TestProblemDouble problem = new TestProblemDouble(0);
		CostToFitness<TestObject> fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(2)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(6)), 1E-10);
		
		problem = new TestProblemDouble(1);
		fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(3)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(7)), 1E-10);
		
		problem = new TestProblemDouble(-1);
		fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(-1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(5)), 1E-10);
	}
	
	@Test
	public void testIntegerToFitness1() {
		TestProblemInteger problem = new TestProblemInteger(0);
		CostToFitness<TestObject> fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(3)), 1E-10);
		
		problem = new TestProblemInteger(1);
		fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(2)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(4)), 1E-10);
		
		problem = new TestProblemInteger(-1);
		fitness = new CostToFitness<TestObject>(problem);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(-1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(2)), 1E-10);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostToFitness<TestObject>(new TestProblemInteger(0), 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostToFitness<TestObject>(new TestProblemInteger(Integer.MAX_VALUE))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CostToFitness<TestObject>(new TestProblemInteger(Integer.MIN_VALUE))
		);
	}
	
	@Test
	public void testIntegerToFitness2() {
		TestProblemInteger problem = new TestProblemInteger(0);
		CostToFitness<TestObject> fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(0)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(2)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(6)), 1E-10);
		
		problem = new TestProblemInteger(1);
		fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(3)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(7)), 1E-10);
		
		problem = new TestProblemInteger(-1);
		fitness = new CostToFitness<TestObject>(problem, 2);
		assertTrue(problem == fitness.getProblem());
		assertEquals(1.0, fitness.fitness(new TestObject(-1)), 1E-10);
		assertEquals(0.5, fitness.fitness(new TestObject(1)), 1E-10);
		assertEquals(0.25, fitness.fitness(new TestObject(5)), 1E-10);
	}
	
	
	private static class TestProblemDouble implements OptimizationProblem<TestObject> {
		private final double min;
		public TestProblemDouble(double min) {
			this.min = min;
		}
		public double cost(TestObject t) {
			return t.id;
		}
		public double value(TestObject t) {
			return cost(t);
		}
		public double minCost() {
			return min;
		}
	}
	private static class TestProblemInteger implements IntegerCostOptimizationProblem<TestObject> {
		private final int min;
		public TestProblemInteger(int min) {
			this.min = min;
		}
		public int cost(TestObject t) {
			return t.id;
		}
		public int value(TestObject t) {
			return cost(t);
		}
		public int minCost() {
			return min;
		}
	}
	private static class TestObject implements Copyable<TestObject> {
		private int id;
		public TestObject(int id) {
			this.id = id;
		}
		public TestObject copy() {
			return new TestObject(id);
		}
	}
}