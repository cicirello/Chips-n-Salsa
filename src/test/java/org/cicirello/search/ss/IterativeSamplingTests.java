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
 
package org.cicirello.search.ss;


import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
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
 * JUnit tests for the IterativeSampling class.
 */
public class IterativeSamplingTests {
	
	@Test
	public void testConstructorExceptions() {
		TestProblem problem = new TestProblem();
		TestProblemInt problemInt = new TestProblemInt();		
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>((TestProblem)null, init, tracker)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>(problem, null, tracker)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>(problem, init, null)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>((TestProblemInt)null, init, tracker)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>(problemInt, null, tracker)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new IterativeSampling<TestObject>(problemInt, init, null)
		);
	}
	
	@Test
	public void testSetProgressTracker() {
		TestProblem problem = new TestProblem();
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		IterativeSampling<TestObject> is = new IterativeSampling<TestObject>(problem, init);
		is.setProgressTracker(tracker);
		assertEquals(tracker, is.getProgressTracker());
		is.setProgressTracker(null);
		assertEquals(tracker, is.getProgressTracker());
	}
	
	@Test
	public void testStoppedByAnotherThread() {
		TestProblem problem = new TestProblem();
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		IterativeSampling<TestObject> is = new IterativeSampling<TestObject>(problem, init, tracker);
		
		tracker.stop();
		SolutionCostPair<TestObject> solution = is.optimize();
		assertNull(solution);
		solution = is.optimize(1);
		assertNull(solution);
	}
	
	@Test
	public void testTrackerContainsBest() {
		TestProblem problem = new TestProblem();
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		IterativeSampling<TestObject> is = new IterativeSampling<TestObject>(problem, init, tracker);
		
		// replaced deprecated call to setFoundBest()
		tracker.update(0, new TestObject(0), true);
		SolutionCostPair<TestObject> solution = is.optimize();
		assertNull(solution);
		solution = is.optimize(1);
		assertNull(solution);
	}
	
	@Test
	public void test1() {
		verifyOptimize1("constructor 1", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifyOptimize1("constructor 2", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer()));
	}
	
	@Test
	public void testInt1() {
		verifyOptimizeInt1("constructor 1", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifyOptimizeInt1("constructor 2", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer()));
	}
	
	@Test
	public void testN() {
		verifyOptimizeN("constructor 1", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifyOptimizeN("constructor 2", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer()));
	}
	
	@Test
	public void testIntN() {
		verifyOptimizeIntN("constructor 1", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifyOptimizeIntN("constructor 2", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer()));
	}
	
	@Test
	public void testSplit() {
		verifySplit("constructor 1", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifySplit("constructor 2", new IterativeSampling<TestObject>(new TestProblem(), new TestInitializer()));
	}
	
	@Test
	public void testSplitInt() {
		verifySplitInt("constructor 1", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer(), new ProgressTracker<TestObject>()));
		verifySplitInt("constructor 2", new IterativeSampling<TestObject>(new TestProblemInt(), new TestInitializer()));
	}
	
	@Test
	public void testQuitsUponFindingBest() {
		TestProblemFindsMin problem = new TestProblemFindsMin();
		TestProblemFindsMinInt problemInt = new TestProblemFindsMinInt();
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		IterativeSampling<TestObject> is = new IterativeSampling<TestObject>(problem, init, tracker);
		
		SolutionCostPair<TestObject> solution = is.optimize(10);
		assertNotNull(solution);
		assertTrue(is.getProgressTracker().didFindBest());
		
		tracker = new ProgressTracker<TestObject>();
		init = new TestInitializer();
		is = new IterativeSampling<TestObject>(problemInt, init, tracker);
		solution = is.optimize(10);
		assertNotNull(solution);
		assertTrue(is.getProgressTracker().didFindBest());
	}
	
	@Test
	public void testStopFromAnotherThread() {
		class LongRunCallable implements Callable<SolutionCostPair<TestObject>> {
        
            IterativeSampling<TestObject> is;
			//volatile boolean started;
            
            LongRunCallable(IterativeSampling<TestObject> is) {
                this.is = is;
				//started = false;
            }
            
            @Override
            public SolutionCostPair<TestObject> call() {
				//started = true;
                return is.optimize(1000000);
            }
        }
		
		TestProblem problem = new TestProblem();
		TestInitializer init = new TestInitializer(); 
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		IterativeSampling<TestObject> is = new IterativeSampling<TestObject>(problem, init, tracker);
		
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		LongRunCallable thread = new LongRunCallable(is);
		Future<SolutionCostPair<TestObject>> future = threadPool.submit(thread);
		SolutionCostPair<TestObject> solution = null;
		try {
			do {
				Thread.sleep(10);
			} while (init.next == 100);
			tracker.stop();		
			solution = future.get();
			assertNotNull(solution);
		}
		catch (InterruptedException ex) { }
		catch (ExecutionException ex) { }
		threadPool.shutdown();
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifyOptimize1(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		OptimizationProblem<TestObject> problem = (OptimizationProblem<TestObject>)is.getProblem();
		for (int i = 0; i < 50; i++) {
			SolutionCostPair<TestObject> s = is.optimize();
			String message = which + "; i="+i;
			double expected = 100 - (i/2);
			double expectedTracked = expected;
			if (i%2==1) expected += 1; 
			assertEquals(message + "; sCost", expected, s.getCostDouble(), 0.0);
			assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()), 0.0);
			assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCostDouble(), 0.0);
			assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()), 0.0);
		}
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifyOptimizeInt1(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		IntegerCostOptimizationProblem<TestObject> problem = (IntegerCostOptimizationProblem<TestObject>)is.getProblem();
		for (int i = 0; i < 50; i++) {
			SolutionCostPair<TestObject> s = is.optimize();
			String message = which + "; i="+i;
			int expected = 100 - (i/2);
			int expectedTracked = expected;
			if (i%2==1) expected += 1; 
			assertEquals(message + "; sCost", expected, s.getCost());
			assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()));
			assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCost());
			assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()));
		}
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifyOptimizeN(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		OptimizationProblem<TestObject> problem = (OptimizationProblem<TestObject>)is.getProblem();
		SolutionCostPair<TestObject> s = is.optimize(11);
		String message = which + "; samples="+11;
		double expected = 95;
		double expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCostDouble(), 0.0);
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()), 0.0);
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCostDouble(), 0.0);
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()), 0.0);
		s = is.optimize(10);
		message = which + "; samples="+21;
		expected = 90;
		expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCostDouble(), 0.0);
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()), 0.0);
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCostDouble(), 0.0);
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()), 0.0);
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifyOptimizeIntN(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		IntegerCostOptimizationProblem<TestObject> problem = (IntegerCostOptimizationProblem<TestObject>)is.getProblem();
		SolutionCostPair<TestObject> s = is.optimize(11);
		String message = which + "; samples="+11;
		int expected = 95;
		int expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCost());
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()));
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCost());
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()));
		s = is.optimize(10);
		message = which + "; samples="+21;
		expected = 90;
		expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCost());
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()));
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCost());
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()));
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifySplit(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		OptimizationProblem<TestObject> problem = (OptimizationProblem<TestObject>)is.getProblem();
		SolutionCostPair<TestObject> s = is.optimize(18);
		IterativeSampling<TestObject> isSplit = is.split();
		assertTrue(isSplit.getProgressTracker() == tracker);
		assertTrue(isSplit.getProblem() == problem);
		s = isSplit.optimize(11);
		String message = which + "; samples="+11;
		double expected = 95;
		double expectedTracked = 92;
		assertEquals(message + "; sCost", expected, s.getCostDouble(), 0.0);
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()), 0.0);
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCostDouble(), 0.0);
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()), 0.0);
		s = isSplit.optimize(10);
		message = which + "; samples="+21;
		expected = 90;
		expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCostDouble(), 0.0);
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()), 0.0);
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCostDouble(), 0.0);
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()), 0.0);
	}
	
	@SuppressWarnings (value="unchecked")
	private void verifySplitInt(String which, IterativeSampling<TestObject> is) {
		ProgressTracker<TestObject> tracker = is.getProgressTracker();
		IntegerCostOptimizationProblem<TestObject> problem = (IntegerCostOptimizationProblem<TestObject>)is.getProblem();
		SolutionCostPair<TestObject> s = is.optimize(18);
		IterativeSampling<TestObject> isSplit = is.split();
		assertTrue(isSplit.getProgressTracker() == tracker);
		assertTrue(isSplit.getProblem() == problem);
		s = isSplit.optimize(11);
		String message = which + "; samples="+11;
		int expected = 95;
		int expectedTracked = 92;
		assertEquals(message + "; sCost", expected, s.getCost());
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()));
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCost());
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()));
		s = isSplit.optimize(10);
		message = which + "; samples="+21;
		expected = 90;
		expectedTracked = expected;
		assertEquals(message + "; sCost", expected, s.getCost());
		assertEquals(message + "; pCost", expected, problem.cost(s.getSolution()));
		assertEquals(message + "; tCost", expectedTracked, tracker.getSolutionCostPair().getCost());
		assertEquals(message + "; ptCost", expectedTracked, problem.cost(tracker.getSolutionCostPair().getSolution()));
	}
	
	private static class TestInitializer implements Initializer<TestObject> {
		
		private volatile int next;
		private boolean decrease;
		
		public TestInitializer() { next = 100; decrease = false; }
		
		public TestObject createCandidateSolution() {
			TestObject x = new TestObject(next);
			if (decrease) next -= 2;
			else next++;
			decrease = !decrease;
			return x;
		}	

		public TestInitializer split() { return new TestInitializer(); } 		
	}
	
	private static class TestObject implements Copyable<TestObject> {
		int cost;
		TestObject(int cost) { this.cost = cost; }
		public TestObject copy() { return new TestObject(cost); }
	}
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		public double cost(TestObject candidate) { return candidate.cost; }
		public double minCost() { return -99999999; }
		public boolean isMinCost(double cost) { return false; }
		public double value(TestObject candidate) { return cost(candidate); }
	}
	
	private static class TestProblemInt implements IntegerCostOptimizationProblem<TestObject> {
		public int cost(TestObject candidate) { return candidate.cost; }
		public int minCost() { return -99999999; }
		public boolean isMinCost(int cost) { return false; }
		public int value(TestObject candidate) { return cost(candidate); }
	}
	
	private static class TestProblemFindsMin extends TestProblem {
		public double minCost() { return 100; }
		public boolean isMinCost(double cost) { return cost <= minCost(); }
	}
	
	private static class TestProblemFindsMinInt extends TestProblemInt {
		public int minCost() { return 100; }
		public boolean isMinCost(int cost) { return cost <= minCost(); }
	}
}