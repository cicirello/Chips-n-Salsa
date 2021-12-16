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
 * JUnit 4 test cases for BasePopulation.
 */
public class GenerationalEATests {
	
	@Test
	public void testExceptions() {
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5, 
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5, 
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5, 
				new CountCrossoverCalls(),
				0.5,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5,
				new CountCrossoverCalls(),
				0.5,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				0.5, 
				null,
				0.5,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				0.5,
				null,
				0.5,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5, 
				new CountCrossoverCalls(),
				0.5,
				false,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				null, 
				0.5,
				new CountCrossoverCalls(),
				0.5,
				false,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				0.5, 
				null,
				0.5,
				false,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				0.5,
				null,
				0.5,
				false,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				-1E-10, 
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				-1E-10, 
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				1E-10, 
				new CountCrossoverCalls(),
				-1E-10,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				1E-10, 
				new CountCrossoverCalls(),
				-1E-10,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				-1E-10, 
				new CountCrossoverCalls(),
				1E-10,
				new TestInitializer(), 
				new TestFitnessDouble(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				-1E-10, 
				new CountCrossoverCalls(),
				1E-10,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new GenerationalEvolutionaryAlgorithm<TestObject>(
				5, 
				new CountMutationCalls(), 
				0.5, 
				new CountCrossoverCalls(),
				0.5+1E-10,
				true,
				new TestInitializer(), 
				new TestFitnessInteger(), 
				new TestSelectionOp(), 
				new ProgressTracker<TestObject>()
			)
		);
	}
	
	@Test
	public void testStoppedByTracker() {
		class TestThread extends Thread {
			
			GenerationalEvolutionaryAlgorithm<TestObject> ea;
			
			public TestThread(GenerationalEvolutionaryAlgorithm<TestObject> ea) {
				this.ea = ea;
			}
			
			@Override
			public void run() {
				ea.optimize(1000);
			}
		}
		
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5, 
			initializer, 
			f, 
			selection, 
			tracker
		);
		try {
			TestThread t = new TestThread(ea);
			t.start();
			while (selection.calledCount < 1);
			tracker.stop();
			t.join();
			assertTrue(selection.calledCount < 1000);
		} catch (InterruptedException ex) {
			fail("This test case shouldn't throw exceptions");
		}
	}
	
	// double fitness
	
	@Test
	public void testBothOpsDoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5,
			crossover,
			0.5,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOpsAlwaysMutateDoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			0.5,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_1_0_DoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			1.0,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(3*N-3, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(8*N-8, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(10*N-10, crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testMutationOnlyDoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5, 
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testAlwaysDoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(f.count, 2*N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	// double fitness, mutually exclusive operators
	
	@Test
	public void testBothOpsDoubleFitness_Mutual() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.25,
			crossover,
			0.25,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertTrue(crossover.count + mutation.count > 0 && crossover.count + mutation.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertTrue(crossover.count + mutation.count > oldCrossoverCount + oldMutationCount && crossover.count + mutation.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertTrue(crossover.count + mutation.count > oldCrossoverCount + oldMutationCount && crossover.count + mutation.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_MutualSumTo1_DoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5, 
			crossover,
			0.5,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count + crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count + crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count + crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_MutualAllCross_DoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.0, 
			crossover,
			1.0,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_Mutual_CNear1_DoubleFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			Math.ulp(1.0), 
			crossover,
			1.0-Math.ulp(1.0),
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	// double fitness, full generation but explicitly passing false for mutually exclusive ops
	
	@Test
	public void testBothOpsDoubleFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5,
			crossover,
			0.5,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOpsAlwaysMutateDoubleFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			0.5,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_1_0_DoubleFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			1.0,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(3*N-3, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(8*N-8, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(10*N-10, crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	
	// int fitness
	
	@Test
	public void testBothOpsIntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5,
			crossover,
			0.5,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOpsAlwaysMutateIntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			0.5,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_1_0_IntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			1.0,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(3*N-3, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(8*N-8, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(10*N-10, crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testMutationOnlyIntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5, 
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testAlwaysIntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(f.count, N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(f.count, 2*N + mutation.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	// int fitness, mutually exclusive operators
	
	@Test
	public void testBothOpsIntegerFitness_Mutual() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.25,
			crossover,
			0.25,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertTrue(crossover.count + mutation.count > 0 && crossover.count + mutation.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertTrue(crossover.count + mutation.count > oldCrossoverCount + oldMutationCount && crossover.count + mutation.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertTrue(crossover.count + mutation.count > oldCrossoverCount + oldMutationCount && crossover.count + mutation.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_MutualSumTo1_IntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5, 
			crossover,
			0.5,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count + crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count + crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count + crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_MutualAllCross_IntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.0, 
			crossover,
			1.0,
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_Mutual_CNear1_IntegerFitness() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			Math.ulp(1.0), 
			crossover,
			1.0-Math.ulp(1.0),
			true,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, crossover.count);
		assertEquals(0, mutation.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	// int fitness, full generation but explicitly passing false for mutually exclusive ops
	
	@Test
	public void testBothOpsIntegerFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			0.5,
			crossover,
			0.5,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > 0 && mutation.count < 3*N);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 8*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertTrue(mutation.count > oldMutationCount && mutation.count < 10*N);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOpsAlwaysMutateIntegerFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		final int N = 100;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			0.5,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertTrue(crossover.count > 0 && crossover.count < 3*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		int oldCrossoverCount = crossover.count;
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 8*N);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		oldCrossoverCount = crossover.count;
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertTrue(crossover.count > oldCrossoverCount && crossover.count < 10*N);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
	}
	
	@Test
	public void testBothOps_1_0_IntegerFitness_MutualFalse() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		TestInitializer initializer = new TestInitializer();
		CountMutationCalls mutation = new CountMutationCalls();
		CountCrossoverCalls crossover = new CountCrossoverCalls();
		
		// Deliberately odd compared to other tests.
		final int N = 101;
		
		GenerationalEvolutionaryAlgorithm<TestObject> ea = new GenerationalEvolutionaryAlgorithm<TestObject>(
			N, 
			mutation, 
			1.0, 
			crossover,
			1.0,
			false,
			initializer, 
			f, 
			selection, 
			tracker
		);
		
		assertTrue(tracker == ea.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		ea.setProgressTracker(tracker);
		assertTrue(tracker == ea.getProgressTracker());
		assertTrue(f.getProblem() == ea.getProblem());
		
		assertEquals(0L, ea.getTotalRunLength());
		
		assertEquals(0, selection.initCount);
		
		// optimize
		SolutionCostPair<TestObject> solution = ea.optimize(3);
		assertEquals(N, initializer.count);
		assertEquals(3*N, mutation.count);
		assertEquals(3*N-3, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(3, selection.calledCount);
		assertEquals(tracker.getSolutionCostPair().getSolution(), solution.getSolution());
		assertTrue(tracker.getSolutionCostPair().getSolution() != solution.getSolution());
		assertEquals(1, selection.initCount);
		
		// split it
		GenerationalEvolutionaryAlgorithm<TestObject> ea2 = ea.split();
		
		// reoptimize
		solution = ea.reoptimize(5);
		assertNotNull(solution);
		assertEquals(N, initializer.count);
		assertEquals(8*N, mutation.count);
		assertEquals(8*N-8, crossover.count);
		assertEquals(f.count, N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(8, selection.calledCount);
		assertEquals(2, selection.initCount);
		
		// another optimize
		solution = ea.optimize(2);
		assertNotNull(solution);
		assertEquals(2*N, initializer.count);
		assertEquals(10*N, mutation.count);
		assertEquals(10*N-10, crossover.count);
		assertEquals(f.count, 2*N + mutation.count + crossover.count);
		assertEquals((long)f.count, ea.getTotalRunLength());
		assertEquals(10, selection.calledCount);
		assertEquals(3, selection.initCount);
		
		tracker.stop();
		assertNull(ea.optimize(2));
		assertNull(ea.reoptimize(2));
		assertEquals(3, selection.initCount);
		
		// Try the split version
		assertEquals(0L, ea2.getTotalRunLength());
		assertNull(ea2.optimize(2));
		assertNull(ea2.reoptimize(2));
		tracker.start();
		int oldMutationCount = mutation.count;
		int oldCrossoverCount = crossover.count;
		int oldFCount = f.count;
		solution = ea2.optimize(1);
		assertEquals((long)(f.count-oldFCount), ea2.getTotalRunLength());
		assertNotNull(solution);
		// These should change since it should have been split.
		assertEquals(2*N, initializer.count);
		assertEquals(oldMutationCount, mutation.count);
		assertEquals(oldCrossoverCount, crossover.count);
		// This can be shared so should increase
		assertTrue(f.count > oldFCount);
		
		assertEquals(3, selection.initCount);
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