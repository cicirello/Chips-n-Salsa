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
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * JUnit tests for the HeuristicPermutationGenerator class,
 * and its interaction with the ConstructiveHeuristic interface, etc.
 */
public class ConstructiveHeuristicTests {
	
	// Base class, HeuristicSolutionGenerator, specific tests.
	
	@Test
	public void testConstructorExceptions() {
		IntProblem problem = new IntProblem();
		IntHeuristic h = new IntHeuristic(problem, 3);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new HeuristicSolutionGenerator<Permutation>(h, null)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new HeuristicSolutionGenerator<Permutation>(null, new ProgressTracker<Permutation>())
		);
	}
	
	@Test
	public void testBaseClassSplit() {
		for (int n = 0; n < 3; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicSolutionGenerator<Permutation> chOriginal = new HeuristicSolutionGenerator<Permutation>(h);
			HeuristicSolutionGenerator<Permutation> ch = chOriginal.split();
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testTrackerStoppedBeforeOptimize() {
		IntProblem problem = new IntProblem();
		IntHeuristic h = new IntHeuristic(problem, 3);
		HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
		ProgressTracker<Permutation> tracker = ch.getProgressTracker();
		tracker.stop();
		assertNull(ch.optimize());
		assertEquals(0, ch.getTotalRunLength());
	}
	
	@Test
	public void testTrackerFoundBestBeforeOptimize() {
		IntProblem problem = new IntProblem();
		IntHeuristic h = new IntHeuristic(problem, 3);
		HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
		ProgressTracker<Permutation> tracker = ch.getProgressTracker();
		// replaced deprecated call to setFoundBest()
		tracker.update(0, new Permutation(1), true);
		assertNull(ch.optimize());
		assertEquals(0, ch.getTotalRunLength());
	}
	
	@Test
	public void testOptimizeFindsOptimalInt() {
		IntProblemOptimal problem = new IntProblemOptimal();
		IntHeuristic h = new IntHeuristic(problem, 1);
		HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
		ProgressTracker<Permutation> tracker = ch.getProgressTracker();
		SolutionCostPair<Permutation> solution = ch.optimize();
		assertEquals(solution.getSolution(), tracker.getSolution());
		assertTrue(tracker.containsIntCost());
		assertTrue(tracker.didFindBest());
		assertEquals(solution.getCost(), tracker.getCost());
	}
	
	@Test
	public void testOptimizeFindsOptimalDouble() {
		DoubleProblemOptimal problem = new DoubleProblemOptimal();
		DoubleHeuristic h = new DoubleHeuristic(problem, 1);
		HeuristicSolutionGenerator<Permutation> ch = new HeuristicSolutionGenerator<Permutation>(h);
		ProgressTracker<Permutation> tracker = ch.getProgressTracker();
		SolutionCostPair<Permutation> solution = ch.optimize();
		assertEquals(solution.getSolution(), tracker.getSolution());
		assertFalse(tracker.containsIntCost());
		assertTrue(tracker.didFindBest());
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 1E-10);
	}
	
	// HeuristicPermutationGenerator tests start here
	
	@Test
	public void testWithIntCosts() {
		for (int n = 0; n < 5; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicPermutationGenerator ch = new HeuristicPermutationGenerator(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertEquals(tracker, ch.getProgressTracker());
			ch.setProgressTracker(null);
			assertEquals(tracker, ch.getProgressTracker());
		}
	}
	
	@Test
	public void testHeuristicNullIncremental() {
		for (int n = 0; n < 5; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristicNullIncremental h = new IntHeuristicNullIncremental(problem, n);
			HeuristicPermutationGenerator ch = new HeuristicPermutationGenerator(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertEquals(tracker, ch.getProgressTracker());
			ch.setProgressTracker(null);
			assertEquals(tracker, ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCosts() {
		for (int n = 0; n < 5; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicPermutationGenerator ch = new HeuristicPermutationGenerator(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithIntCostsWithProgressTracker() {
		for (int n = 0; n < 5; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicPermutationGenerator ch = new HeuristicPermutationGenerator(h, originalTracker);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			assertTrue(originalTracker == tracker);
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsWithProgressTracker() {
		for (int n = 0; n < 5; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicPermutationGenerator ch = new HeuristicPermutationGenerator(h, originalTracker);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			assertTrue(originalTracker == tracker);
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithIntCostsSplit() {
		for (int n = 0; n < 5; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicPermutationGenerator chOriginal = new HeuristicPermutationGenerator(h);
			HeuristicPermutationGenerator ch = chOriginal.split();
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsSplit() {
		for (int n = 0; n < 5; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicPermutationGenerator chOriginal = new HeuristicPermutationGenerator(h);
			HeuristicPermutationGenerator ch = chOriginal.split();
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			int evenStart = (n%2==0) ? n-2 : n-1;
			int oddStart = (n%2==0) ? n-1 : n-2;
			int i = 0;
			for (int expected = evenStart; expected >= 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			for (int expected = oddStart; expected > 0 && i < n; expected -= 2, i++) {
				assertEquals(expected, p.get(i));
			}
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	
	/*
	 * Fake heuristic designed for predictable test cases:
	 * designed to prefer even permutation elements (largest to smallest), followed by odd
	 * (largest to smallest).
	 */
	private static class IntHeuristicNullIncremental extends IntHeuristic {
		public IntHeuristicNullIncremental(IntProblem problem, int n) {
			super(problem, n);
		}
		@Override public IntIncEval createIncrementalEvaluation() {
			return null;
		}
	}
	
	/*
	 * Fake heuristic designed for predictable test cases:
	 * designed to prefer even permutation elements (largest to smallest), followed by odd
	 * (largest to smallest).
	 */
	private static class IntHeuristic implements ConstructiveHeuristic<Permutation> {
		private IntProblem problem;
		private int n;
		public IntHeuristic(IntProblem problem, int n) { this.problem = problem; this.n = n; }
		@Override public IntProblem getProblem() { return problem; }
		@Override public int completeLength() { return n; }
		@Override public IntIncEval createIncrementalEvaluation() {
			return new IntIncEval();
		}
		@Override public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
			IntIncEval inc = (IntIncEval)incEval;
			if (element % 2 == 0) return n + element;
			else return element;
		}
		@Override
		public final Partial<Permutation> createPartial(int n) {
			return new PartialPermutation(n);
		}
	}
	
	/*
	 * Fake heuristic designed for predictable test cases:
	 * designed to prefer even permutation elements (largest to smallest), followed by odd
	 * (largest to smallest).
	 */
	private static class DoubleHeuristic implements ConstructiveHeuristic<Permutation> {
		private DoubleProblem problem;
		private int n;
		public DoubleHeuristic(DoubleProblem problem, int n) { this.problem = problem; this.n = n; }
		@Override public DoubleProblem getProblem() { return problem; }
		@Override public int completeLength() { return n; }
		@Override public DoubleIncEval createIncrementalEvaluation() {
			return new DoubleIncEval();
		}
		@Override public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
			DoubleIncEval inc = (DoubleIncEval)incEval;
			if (element % 2 == 0) return n + element;
			else return element;
		}
		@Override
		public final Partial<Permutation> createPartial(int n) {
			return new PartialPermutation(n);
		}
	}
	
	/*
	 * Fake designed for predictable test cases.
	 */
	private static class IntIncEval implements IncrementalEvaluation<Permutation> {
		private int sum;
		@Override public void extend(Partial<Permutation> p, int element) { sum += element + 1; }
	}
	
	/*
	 * Fake designed for predictable test cases.
	 */
	private static class DoubleIncEval implements IncrementalEvaluation<Permutation> {
		private int sum;
		@Override public void extend(Partial<Permutation> p, int element) { sum += element + 1; }
	}
	
	/*
	 * We need a problem for the tests.
	 * Fake problem. Doesn't really matter for what we are testing.
	 */
	private static class IntProblem implements IntegerCostOptimizationProblem<Permutation> {
		@Override public int cost(Permutation candidate) { 
			int sum = 0;
			for (int i = 0; i < candidate.length(); i++) {
				sum += candidate.get(i);
			}
			return sum + candidate.length(); 
		}
		@Override public int value(Permutation candidate) { return cost(candidate); }
	}
	
	/*
	 * We need a problem for the tests.
	 * Fake problem. Doesn't really matter for what we are testing.
	 */
	private static class DoubleProblem implements OptimizationProblem<Permutation> {
		@Override public double cost(Permutation candidate) { 
			int sum = 0;
			for (int i = 0; i < candidate.length(); i++) {
				sum += candidate.get(i);
			}
			return sum + candidate.length(); 
		}
		@Override public double value(Permutation candidate) { return cost(candidate); }
	}
	
	private static class IntProblemOptimal extends IntProblem {
		// minCost will occur with a Permutation of length 1 (for testing)
		@Override public int minCost() { return 1; }
	}
	
	private static class DoubleProblemOptimal extends DoubleProblem {
		// minCost will occur with a Permutation of length 1 (for testing)
		@Override public double minCost() { return 1; }
	}
	
}