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

package org.cicirello.search.evo;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.util.Copyable;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * JUnit test cases for adaptive EAs.
 */
public class AdaptiveEvolutionaryAlgorithmTests {
	
	// all params
	
	@Test
	public void testAdaptiveEA_Double_All() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			1,
			tracker
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	@Test
	public void testAdaptiveEA_Integer_All() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			1,
			tracker
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	// no tracker
	
	@Test
	public void testAdaptiveEA_Double_NoTracker() {
		TestObject.reinit();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			1
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	@Test
	public void testAdaptiveEA_Integer_NoTracker() {
		TestObject.reinit();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			1
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	// No elite
	
	@Test
	public void testAdaptiveEA_Double_NoElite() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			tracker
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	@Test
	public void testAdaptiveEA_Integer_NoElite() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection,
			tracker
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	// no optional params
	
	@Test
	public void testAdaptiveEA_Double_NoOptional() {
		TestObject.reinit();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	@Test
	public void testAdaptiveEA_Integer_NoOptional() {
		TestObject.reinit();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 20;
		
		AdaptiveEvolutionaryAlgorithm<TestObject> ea = new AdaptiveEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			crossover,
			initializer, 
			f, 
			selection
		);
		assertNotNull(ea.optimize(5));
		
		AdaptiveEvolutionaryAlgorithm<TestObject> s = ea.split();
		assertNotNull(s.optimize(5));
	}
	
	// HELPER CLASSES
	
	private static class CountCrossoverCalls implements CrossoverOperator<TestObject> {
		
		private volatile int count;
		
		public CountCrossoverCalls() {
			count=0;
		}
		
		@Override
		public void cross(TestObject candidate1, TestObject candidate2) {
			assertTrue(candidate1 != candidate2);
			// add 2 since we're counting number changed by population
			count+=2;
		}
		
		@Override
		public CountCrossoverCalls split() {
			return new CountCrossoverCalls();
		}
	}
	
	private static class CountMutationCalls implements MutationOperator<TestObject> {
		
		private volatile int count;
		
		public CountMutationCalls() {
			count=0;
		}
		
		@Override
		public void mutate(TestObject candidate) {
			count++;
		}
		
		@Override
		public CountMutationCalls split() {
			return new CountMutationCalls();
		}
	}
	
	private static class TestSelectionOp implements SelectionOperator {
		
		volatile int calledCount;
		volatile int initCount;
		
		public TestSelectionOp() {
			calledCount = 0;
			initCount = 0;
		}
		@Override
		public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
			int next = selected.length - 1;
			for (int i = 0; i < selected.length; i++) {
				selected[i] = next;
				next--;
			}
			calledCount++;
		}
		@Override
		public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
			int next = selected.length - 1;
			for (int i = 0; i < selected.length; i++) {
				selected[i] = next;
				next--;
			}
			calledCount++;
		}
		@Override
		public void init(int generations) {
			initCount++;
		}
		@Override
		public TestSelectionOp split() {
			return new TestSelectionOp();
		}
	}
	
	private static class TestFitnessDouble implements FitnessFunction.Double<TestObject> {
		
		private TestProblemDouble problem;
		private int adjustment;
		private volatile int count;
		
		public TestFitnessDouble() {
			problem = new TestProblemDouble();
			count = 0;
		}
		
		public double fitness(TestObject c) {
			count++;
			return c.id + 0.4 + adjustment;
		}
		
		public Problem<TestObject> getProblem() {
			return problem;
		}
		
		public void changeFitness(int adjustment) {
			this.adjustment = adjustment;
		}
	}
	
	private static class TestFitnessInteger implements FitnessFunction.Integer<TestObject> {
		
		private TestProblemDouble problem;
		private int adjustment;
		private volatile int count;
		
		public TestFitnessInteger() {
			problem = new TestProblemDouble();
			count = 0;
		}
		
		public int fitness(TestObject c) {
			count++;
			return c.id + 10 + adjustment;
		}
		
		public Problem<TestObject> getProblem() {
			return problem;
		}
		
		public void changeFitness(int adjustment) {
			this.adjustment = adjustment;
		}
	}

	
	private static class TestProblemDouble implements OptimizationProblem<TestObject> {
		public double cost(TestObject c) {
			return 1.0 / (1.0 + c.id);
		}
		public double value(TestObject c) {
			return cost(c);
		}
	}
	
	private static class TestInitializer implements Initializer<TestObject> {
		volatile int count;
		public TestInitializer() {
			count = 0;
		}
		public TestObject createCandidateSolution() {
			count++;
			return new TestObject();
		}
		public TestInitializer split() {
			return new TestInitializer();
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		
		private static int IDENTIFIER = 0;
		private static boolean increase = true;
		public static void reinit() {
			increase = true;
			IDENTIFIER = 0;
		}
		
		private int id;
		
		public TestObject() {
			if (increase) IDENTIFIER++;
			else IDENTIFIER--;
			id = IDENTIFIER;
			if (IDENTIFIER == 6) increase = false;
		}
		
		private TestObject(int id) {
			this.id = id;
		}
		
		public TestObject copy() {
			return new TestObject(id);
		}
		
		public boolean equals(Object other) {
			return id == ((TestObject)other).id;
		}
	}
}
