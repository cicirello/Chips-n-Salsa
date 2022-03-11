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
 
package org.cicirello.search.sa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.ProgressTracker;
import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

/**
 * JUnit test cases for the SimulatedAnnealing class.
 */
public class SimulatedAnnealingTests {
	
	private static final double EPSILON = 1e-10;
	
	private SimulatedAnnealing<TestObject> d_unknown;
	private SimulatedAnnealing<TestObject> d_known;
	private SimulatedAnnealing<TestObject> i_unknown;
	private SimulatedAnnealing<TestObject> i_known;
	private OptimizationProblem<TestObject> pd_unknown;
	private OptimizationProblem<TestObject> pd_known;
	private IntegerCostOptimizationProblem<TestObject> pi_unknown;
	private IntegerCostOptimizationProblem<TestObject> pi_known;
	
	@BeforeEach
    public void setUp() {
		pd_unknown = new TestProblem();
		pd_known = new TestProblemKnownMin();
		pi_unknown = new TestProblemInt();
		pi_known = new TestProblemIntKnownMin();
		d_unknown = new SimulatedAnnealing<TestObject>(pd_unknown, new TestMutation(), new TestInitializer());
		d_known = new SimulatedAnnealing<TestObject>(pd_known, new TestMutation(), new TestInitializer());
		i_unknown = new SimulatedAnnealing<TestObject>(pi_unknown, new TestMutation(), new TestInitializer());
		i_known = new SimulatedAnnealing<TestObject>(pi_known, new TestMutation(), new TestInitializer());
	}
	
	@Test
	public void testConstructorExceptions() {
		NullPointerException thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>((OptimizationProblem<TestObject>)null, new TestMutation(), new TestInitializer(), new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pd_known, null, new TestInitializer(), new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pd_known, new TestMutation(), null, new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pd_known, new TestMutation(), new TestInitializer(), null, new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pd_known, new TestMutation(), new TestInitializer(), new ModifiedLam(), (ProgressTracker<TestObject>)null)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>((IntegerCostOptimizationProblem<TestObject>)null, new TestMutation(), new TestInitializer(), new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pi_known, null, new TestInitializer(), new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pi_known, new TestMutation(), null, new ModifiedLam(), new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pi_known, new TestMutation(), new TestInitializer(), null, new ProgressTracker<TestObject>())
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new SimulatedAnnealing<TestObject>(pi_known, new TestMutation(), new TestInitializer(), new ModifiedLam(), (ProgressTracker<TestObject>)null)
		);
	}
	
	@Test
	public void testConstructors() {
		TestMutation mutation = new TestMutation();
		ModifiedLam anneal = new ModifiedLam();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestInitializer init = new TestInitializer();
		
		SimulatedAnnealing<TestObject> sa = new SimulatedAnnealing<TestObject>(pi_known, mutation, init, anneal, tracker);
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pi_known, sa.getProblem());
		assertTrue(tracker.containsIntCost());
		
		tracker = new ProgressTracker<TestObject>();
		sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, anneal, tracker);
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pd_known, sa.getProblem());
		assertFalse(tracker.containsIntCost());
		
		tracker = new ProgressTracker<TestObject>();
		sa = new SimulatedAnnealing<TestObject>(pi_known, mutation, init, tracker);
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pi_known, sa.getProblem());
		assertTrue(tracker.containsIntCost());
		
		tracker = new ProgressTracker<TestObject>();
		sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, tracker);
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pd_known, sa.getProblem());
		assertFalse(tracker.containsIntCost());
		
		sa = new SimulatedAnnealing<TestObject>(pi_known, mutation, init, anneal);
		tracker = sa.getProgressTracker();
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pi_known, sa.getProblem());
		assertTrue(tracker.containsIntCost());
		
		sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, anneal);
		tracker = sa.getProgressTracker();
		sa.optimize(1);
		assertEquals(tracker, sa.getProgressTracker());
		assertEquals(1, sa.getTotalRunLength());
		assertEquals(pd_known, sa.getProblem());
		assertFalse(tracker.containsIntCost());
	}
	
	@Test
	public void testStartsWithOptimal() {
		TestMutation mutation = new TestMutation();
		ModifiedLam anneal = new ModifiedLam();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestInitializer init = new TestInitializer();
		
		SimulatedAnnealing<TestObject> sa = new SimulatedAnnealing<TestObject>(pi_known, mutation, init, anneal, tracker);
		TestObject optimal = new TestObject(600);
		SolutionCostPair<TestObject> solution = sa.optimize(1, optimal);
		assertTrue(tracker.didFindBest());
		assertEquals(optimal, tracker.getSolution());
		assertEquals(optimal, solution.getSolution());
		
		tracker = new ProgressTracker<TestObject>();
		sa = new SimulatedAnnealing<TestObject>(pd_known, mutation, init, anneal, tracker);
		solution = sa.optimize(1, optimal);
		assertTrue(tracker.didFindBest());
		assertEquals(optimal, tracker.getSolution());
		assertEquals(optimal, solution.getSolution());
	}
	
	@Test
	public void testStopFromAnotherThread() {
		class LongRunCallable implements Callable<SolutionCostPair<TestObject>> {
        
            SimulatedAnnealing<TestObject> sa;
			volatile boolean started;
            
            LongRunCallable(SimulatedAnnealing<TestObject> sa) {
                this.sa = sa;
				started = false;
            }
            
            @Override
            public SolutionCostPair<TestObject> call() {
				started = true;
                return sa.optimize(1000000);
            }
        }
		
		TestMutation mutation = new TestMutation();
		ModifiedLam anneal = new ModifiedLam();
		TestInitializer init = new TestInitializer();
		
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		SimulatedAnnealing<TestObject> sa = new SimulatedAnnealing<TestObject>(pi_unknown, mutation, init, anneal, tracker);
		
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		LongRunCallable thread = new LongRunCallable(sa);
		Future<SolutionCostPair<TestObject>> future = threadPool.submit(thread);
		try {
			do {
				Thread.sleep(10);
			} while (!thread.started);
			tracker.stop();		
			SolutionCostPair<TestObject> pair = future.get();
		}
		catch (InterruptedException ex) { }
		catch (ExecutionException ex) { }
		
		tracker = new ProgressTracker<TestObject>();
		sa = new SimulatedAnnealing<TestObject>(pd_unknown, mutation, init, anneal, tracker);
		
		thread = new LongRunCallable(sa);
		future = threadPool.submit(thread);
		try {
			do {
				Thread.sleep(10);
			} while (!thread.started);
			tracker.stop();		
			SolutionCostPair<TestObject> pair = future.get();
		}
		catch (InterruptedException ex) { }
		catch (ExecutionException ex) { }
		threadPool.shutdown();
	}
	
	@Test
	public void testGetProblem() {
		assertEquals(pd_unknown, d_unknown.getProblem());
		assertEquals(pd_known, d_known.getProblem());
		assertEquals(pi_unknown, i_unknown.getProblem());
		assertEquals(pi_known, i_known.getProblem());
	}
	
	@Test
	public void testSetProgressTracker() {
		ProgressTracker<TestObject> tracker = i_known.getProgressTracker();
		i_known.setProgressTracker(null);
		assertEquals(tracker, i_known.getProgressTracker());
		ProgressTracker<TestObject> tracker2 = new ProgressTracker<TestObject>(); 
		i_known.setProgressTracker(tracker2);
		assertEquals(tracker2, i_known.getProgressTracker());
	}
	
	@Test
	public void testFoundBest() {
		TestObject t = new TestObject(100);
		// replaced call to deprecated setFoundBest()
		i_known.getProgressTracker().update(0, new TestObject(0), true);
		assertNull(i_known.optimize(10));
		assertNull(i_known.optimize(10, t));
		assertNull(i_known.reoptimize(10));
		d_known.getProgressTracker().stop();
		assertNull(d_known.optimize(10));
		assertNull(d_known.optimize(10, t));
		assertNull(d_known.reoptimize(10));
	}
	
	@Test
	public void testReoptimize() {
		// Test with unknown min solution: double costs
		int elapsed = 0;
		ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
		assertNull(t.getSolution(), "Initial best solution should be null");
		assertEquals(elapsed, d_unknown.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			SolutionCostPair<TestObject> result;
			assertNotNull(result = d_unknown.reoptimize(100));
			double expected = i <= 6 ? 1000.0 - 100*i : 400.0;
			assertEquals(expected, t.getCostDouble(), EPSILON);
			assertEquals(expected, pd_unknown.cost(t.getSolution()), EPSILON);
			if (i <= 6) assertEquals(100*i, result.getSolution().bar);
			elapsed += 100;
			assertEquals(elapsed, d_unknown.getTotalRunLength());
		}
		
		// Test with unknown min solution: int costs
		elapsed = 0;
		t = i_unknown.getProgressTracker();
		assertNull(t.getSolution(), "Initial best solution should be null");
		assertEquals(elapsed, i_unknown.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			SolutionCostPair<TestObject> result;
			assertNotNull(result = i_unknown.reoptimize(100));
			int expected = i <= 6 ? 1000 - 100*i : 400;
			assertEquals(expected, t.getCost());
			assertEquals(expected, pi_unknown.cost(t.getSolution()));
			if (i <= 6) assertEquals(100*i, result.getSolution().bar);
			elapsed += 100;
			assertEquals(elapsed, i_unknown.getTotalRunLength());
		}
		
		// Test with known min solution: double costs
		elapsed = 0;
		t = d_known.getProgressTracker();
		assertNull(t.getSolution(), "Initial best solution should be null");
		assertEquals(elapsed, d_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			SolutionCostPair<TestObject> result;
			if (i<=6) {
				assertNotNull(result = d_known.reoptimize(100));
				assertEquals(100*i, result.getSolution().bar);
			} else assertNull(result = d_known.reoptimize(100));
			double expected = i <= 6 ? 1000.0 - 100*i : 400.0;
			assertEquals(expected, t.getCostDouble(), EPSILON);
			assertEquals(expected, pd_known.cost(t.getSolution()), EPSILON);
			elapsed += i <= 6 ? 100 : 0;
			assertEquals(elapsed, d_known.getTotalRunLength());
		}
		
		// Test with known min solution: int costs
		elapsed = 0;
		t = i_known.getProgressTracker();
		assertNull(t.getSolution(), "Initial best solution should be null");
		assertEquals(elapsed, i_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			SolutionCostPair<TestObject> result;
			if (i<=6) {
				assertNotNull(result = i_known.reoptimize(100));
				assertEquals(100*i, result.getSolution().bar);
			} else assertNull(result = i_known.reoptimize(100));
			int expected = i <= 6 ? 1000 - 100*i : 400;
			assertEquals(expected, t.getCost());
			assertEquals(expected, pi_known.cost(t.getSolution()));
			elapsed += i <= 6 ? 100 : 0;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
	}
	
	@Test
	public void testReoptimizeSplit() {
		// Test with known min solution: int costs
		int elapsed = 0;
		ProgressTracker<TestObject> t = i_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			SolutionCostPair<TestObject> result;
			if (i==2) {
				SimulatedAnnealing<TestObject> split = i_known.split();
				assertEquals(t, split.getProgressTracker());
				assertNotNull(result = split.reoptimize(100));
				assertEquals(200, result.getSolution().bar);
				assertNotNull(result = split.reoptimize(100));
				assertEquals(300, result.getSolution().bar);
			}
			if (i<2) {
				assertNotNull(result = i_known.reoptimize(100));
				assertEquals(100*i, result.getSolution().bar);
			} else if (i <= 4) {
				assertNotNull(result = i_known.reoptimize(100));
				assertEquals(100*(i+2), result.getSolution().bar);
			} else assertNull(result = i_known.reoptimize(100));
			int expected = 400;
			if (i < 2) {
				expected = 1000 - 100*i; 
			} else if (i<=4) {
				expected = 1000 - 100*(i+2);
			}
			assertEquals(expected, t.getCost());
			assertEquals(expected, pi_known.cost(t.getSolution()));
			elapsed += i <= 4 ? 100 : 0;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
	}
	
	@Test
	public void testOptimize() {
		// Test with unknown min solution: double costs
		int elapsed = 0;
		ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, d_unknown.getTotalRunLength());
		SolutionCostPair<TestObject> result;
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = d_unknown.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900.0, t.getCostDouble(), EPSILON);
			assertEquals(900.0, pd_unknown.cost(t.getSolution()), EPSILON);
			elapsed += 100;
			assertEquals(elapsed, d_unknown.getTotalRunLength());
		}
		assertNotNull(result = d_unknown.optimize(1000));
		assertEquals(400.0, t.getCostDouble(), EPSILON);
		assertEquals(400.0, pd_unknown.cost(t.getSolution()), EPSILON);
		elapsed += 1000;
		assertEquals(elapsed, d_unknown.getTotalRunLength());
		
		// Test with unknown min solution: int costs
		elapsed = 0;
		t = i_unknown.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_unknown.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = i_unknown.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900, t.getCost());
			assertEquals(900, pi_unknown.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_unknown.getTotalRunLength());
		}
		assertNotNull(result = i_unknown.optimize(1000));
		assertEquals(400, t.getCost());
		assertEquals(400, pi_unknown.cost(t.getSolution()));
		elapsed += 1000;
		assertEquals(elapsed, i_unknown.getTotalRunLength());
		
		// Test with known min solution: double costs
		elapsed = 0;
		t = d_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, d_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = d_known.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900.0, t.getCostDouble(), EPSILON);
			assertEquals(900.0, pd_known.cost(t.getSolution()), EPSILON);
			elapsed += 100;
			assertEquals(elapsed, d_known.getTotalRunLength());
		}
		assertNotNull(result = d_known.optimize(1000));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400.0, t.getCostDouble(), EPSILON);
		assertEquals(400.0, pd_known.cost(t.getSolution()), EPSILON);
		elapsed += 600;
		assertEquals(elapsed, d_known.getTotalRunLength());
		
		// Test with known min solution: int costs
		elapsed = 0;
		t = i_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = i_known.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900, t.getCost());
			assertEquals(900, pi_known.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
		assertNotNull(result = i_known.optimize(1000));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400, t.getCost());
		assertEquals(400, pi_known.cost(t.getSolution()));
		elapsed += 600;
		assertEquals(elapsed, i_known.getTotalRunLength());
	}
	
	@Test
	public void testOptimizeSplit() {
		// Test with known min solution: int costs
		int elapsed = 0;
		ProgressTracker<TestObject> t = i_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_known.getTotalRunLength());
		SolutionCostPair<TestObject> result;
		for (int i = 1; i <= 15; i++) {
			if (i==3) {
				SimulatedAnnealing<TestObject> split = i_known.split();
				assertEquals(t, split.getProgressTracker());
				assertNotNull(result = split.optimize(100));
				assertEquals(100, result.getSolution().bar);
				assertNotNull(result = split.optimize(100));
				assertEquals(100, result.getSolution().bar);
			}
			assertNotNull(result = i_known.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900, t.getCost());
			assertEquals(900, pi_known.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
		assertNotNull(result = i_known.optimize(1000));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400, t.getCost());
		assertEquals(400, pi_known.cost(t.getSolution()));
		elapsed += 600;
		assertEquals(elapsed, i_known.getTotalRunLength());
		// Test with known min solution: double costs
		elapsed = 0;
		t = d_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, d_known.getTotalRunLength());
		result = null;
		for (int i = 1; i <= 15; i++) {
			if (i==3) {
				SimulatedAnnealing<TestObject> split = d_known.split();
				assertEquals(t, split.getProgressTracker());
				assertNotNull(result = split.optimize(100));
				assertEquals(100, result.getSolution().bar);
				assertNotNull(result = split.optimize(100));
				assertEquals(100, result.getSolution().bar);
			}
			assertNotNull(result = d_known.optimize(100));
			assertEquals(100, result.getSolution().bar);
			assertEquals(900.0, t.getCostDouble(), EPSILON);
			assertEquals(900.0, pd_known.cost(t.getSolution()), EPSILON);
			elapsed += 100;
			assertEquals(elapsed, d_known.getTotalRunLength());
		}
		assertNotNull(result = d_known.optimize(1000));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400.0, t.getCostDouble(), EPSILON);
		assertEquals(400.0, pd_known.cost(t.getSolution()), EPSILON);
		elapsed += 600;
		assertEquals(elapsed, d_known.getTotalRunLength());
	}
	
	@Test
	public void testOptimizeSpecifiedStart() {
		TestObject start = new TestObject(50);
		
		// Test with unknown min solution: double costs
		int elapsed = 0;
		ProgressTracker<TestObject> t = d_unknown.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, d_unknown.getTotalRunLength());
		SolutionCostPair<TestObject> result;
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = d_unknown.optimize(100, start));
			assertEquals(150, result.getSolution().bar);
			assertEquals(850.0, t.getCostDouble(), EPSILON);
			assertEquals(850.0, pd_unknown.cost(t.getSolution()), EPSILON);
			elapsed += 100;
			assertEquals(elapsed, d_unknown.getTotalRunLength());
		}
		assertNotNull(result = d_unknown.optimize(1000, start));
		assertEquals(400.0, t.getCostDouble(), EPSILON);
		assertEquals(400.0, pd_unknown.cost(t.getSolution()), EPSILON);
		elapsed += 1000;
		assertEquals(elapsed, d_unknown.getTotalRunLength());
		
		// Test with unknown min solution: int costs
		elapsed = 0;
		t = i_unknown.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_unknown.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = i_unknown.optimize(100, start));
			assertEquals(150, result.getSolution().bar);
			assertEquals(850, t.getCost());
			assertEquals(850, pi_unknown.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_unknown.getTotalRunLength());
		}
		assertNotNull(result = i_unknown.optimize(1000, start));
		assertEquals(400, t.getCost());
		assertEquals(400, pi_unknown.cost(t.getSolution()));
		elapsed += 1000;
		assertEquals(elapsed, i_unknown.getTotalRunLength());
		
		// Test with known min solution: double costs
		elapsed = 0;
		t = d_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, d_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = d_known.optimize(100, start));
			assertEquals(150, result.getSolution().bar);
			assertEquals(850.0, t.getCostDouble(), EPSILON);
			assertEquals(850.0, pd_known.cost(t.getSolution()), EPSILON);
			elapsed += 100;
			assertEquals(elapsed, d_known.getTotalRunLength());
		}
		assertNotNull(result = d_known.optimize(1000, start));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400.0, t.getCostDouble(), EPSILON);
		assertEquals(400.0, pd_known.cost(t.getSolution()), EPSILON);
		elapsed += 550;
		assertEquals(elapsed, d_known.getTotalRunLength());
		
		// Test with known min solution: int costs
		elapsed = 0;
		t = i_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_known.getTotalRunLength());
		for (int i = 1; i <= 15; i++) {
			assertNotNull(result = i_known.optimize(100, start));
			assertEquals(150, result.getSolution().bar);
			assertEquals(850, t.getCost());
			assertEquals(850, pi_known.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
		assertNotNull(result = i_known.optimize(1000, start));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400, t.getCost());
		assertEquals(400, pi_known.cost(t.getSolution()));
		elapsed += 550;
		assertEquals(elapsed, i_known.getTotalRunLength());
	}
	
	
	@Test
	public void testOptimizeSpecifiedStartSplit() {
		TestObject start = new TestObject(50);
		
		int elapsed = 0;
		ProgressTracker<TestObject> t = i_known.getProgressTracker();
		assertNull(t.getSolution());
		assertEquals(elapsed, i_known.getTotalRunLength());
		SolutionCostPair<TestObject> result;
		for (int i = 1; i <= 15; i++) {
			if (i==3) {
				SimulatedAnnealing<TestObject> split = i_known.split();
				assertEquals(t, split.getProgressTracker());
				assertNotNull(result = split.optimize(100, start));
				assertEquals(150, result.getSolution().bar);
				assertNotNull(result = split.optimize(100, start));
				assertEquals(150, result.getSolution().bar);
			}
			assertNotNull(result = i_known.optimize(100, start));
			assertEquals(150, result.getSolution().bar);
			assertEquals(850, t.getCost());
			assertEquals(850, pi_known.cost(t.getSolution()));
			elapsed += 100;
			assertEquals(elapsed, i_known.getTotalRunLength());
		}
		assertNotNull(result = i_known.optimize(1000, start));
		assertEquals(600, result.getSolution().bar);
		assertEquals(400, t.getCost());
		assertEquals(400, pi_known.cost(t.getSolution()));
		elapsed += 550;
		assertEquals(elapsed, i_known.getTotalRunLength());
	}
	
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		@Override public double cost(TestObject c) { return 1000 - c.bar % 601; }
		@Override public double value(TestObject c) { return cost(c); }
	}
	
	private static class TestProblemKnownMin extends TestProblem {
		@Override public double minCost() { return 400; }
		@Override public boolean isMinCost(double c) { return c == minCost(); }
	}
	
	private static class TestProblemInt implements IntegerCostOptimizationProblem<TestObject> {
		@Override public int cost(TestObject c) { return 1000 - c.bar % 601; }
		@Override public int value(TestObject c) { return cost(c); }
	}
	
	private static class TestProblemIntKnownMin extends TestProblemInt {
		@Override public int minCost() { return 400; }
		@Override public boolean isMinCost(int c) { return c == minCost(); }
	}
	
	private static class TestMutation implements UndoableMutationOperator<TestObject> {
		@Override public void mutate(TestObject c) { c.bar++; }
		@Override public void undo(TestObject c) { c.bar--; }
		@Override public TestMutation split() { return new TestMutation(); }
	}
	
	private static class TestInitializer implements Initializer<TestObject> {
		// for testing always start with same solution rather than random for predictable results
		@Override public TestObject createCandidateSolution() { return new TestObject(0); }
		@Override public TestInitializer split() {return this;}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		int bar;
		public TestObject(int bar) {
			this.bar = bar;
		}
		@Override public TestObject copy() { return new TestObject(bar); }
		@Override public boolean equals(Object other) {
			return ((TestObject)other).bar == bar;
		}
	}	
}