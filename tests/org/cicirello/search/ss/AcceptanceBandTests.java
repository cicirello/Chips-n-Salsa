/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
import org.cicirello.permutations.Permutation;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * JUnit tests for AcceptanceBandSampling.
 */
public class AcceptanceBandTests {
	
	@Test
	public void testWithIntCosts() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCosts() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
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
	public void testWithIntCostsMultipleSamples() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize(5);
			assertEquals(5, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize(2);
			assertEquals(7, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsMultipleSamples() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize(5);
			assertEquals(5, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize(2);
			assertEquals(7, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithIntCostsWithProgressTracker() {
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, originalTracker);
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
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsWithProgressTracker() {
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, originalTracker);
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
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithIntCostsSplit() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling chOriginal = new AcceptanceBandSampling(h);
			AcceptanceBandSampling ch = chOriginal.split();
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsSplit() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling chOriginal = new AcceptanceBandSampling(h);
			AcceptanceBandSampling ch = chOriginal.split();
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testChooseBetaMax() {
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 1.0);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					for (int i = 0; i < k; i++) values[i] = 0.1 * (i+1);
					double max = 0.1 * k;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertTrue(chosen < k);
					assertTrue(chosen >= 0);
					for (int i = 0; i < k; i++) {
						assertEquals(i, eq[i]);
					}
					for (int i = k; i < n; i++) {
						assertEquals(-1, eq[i]);
					}
				}
			}
		}
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 1.0);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					double max = 0.1 * k;
					for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertTrue(chosen < k);
					assertTrue(chosen >= 0);
					for (int i = 0; i < k; i++) {
						assertEquals(i, eq[i]);
					}
					for (int i = k; i < n; i++) {
						assertEquals(-1, eq[i]);
					}
				}
			}
		}
	}
	
	@Test
	public void testChooseBetaMin() {
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.0);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					for (int i = 0; i < k; i++) values[i] = 0.1 * (i+1);
					double max = 0.1 * k;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertEquals(k-1, chosen);
					assertEquals(k-1, eq[0]);
					for (int i = 1; i < n; i++) {
						assertEquals(-1, eq[i]);
					}
				}
			}
		}
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.0);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					double max = 0.1 * k;
					for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertEquals(0, chosen);
					assertEquals(0, eq[0]);
					for (int i = 1; i < n; i++) {
						assertEquals(-1, eq[i]);
					}
				}
			}
		}
	}
	
	@Test
	public void testChooseBeta05() {
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.5);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					for (int i = 0; i < k; i++) values[i] = 0.1 * (i+1);
					double max = 0.1 * k;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertTrue(chosen < k);
					assertTrue(chosen >= 0);
					for (int i = 0; i < (k+2)/2; i++) {
						String message = "increasing, i="+i+", k="+k;
						assertEquals(message, (k-1)/2+i, eq[i]);
					}
					for (int i = (k+2)/2; i < n; i++) {
						String message = "increasing, i="+i+", k="+k;
						assertEquals(message, -1, eq[i]);
					}
				}
			}
		}
		for (int n = 1; n <= 10; n++) {
			for (int k = 1; k <= n; k++) {
				IntProblem problem = new IntProblem();
				IntHeuristic h = new IntHeuristic(problem, n);
				AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.5);
				for (int trial = 0; trial < 10; trial++) {
					double[] values = new double[n];
					double max = 0.1 * k;
					for (int i = 0; i < k; i++) values[i] = max - 0.1 * i;
					for (int i = k; i < n; i++) values[i] = 99999;
					int[] eq = new int[n];
					for (int i = 0; i < n; i++) eq[i] = -1;
					int chosen = ch.choose(values, k, max, eq);
					assertTrue(chosen < k);
					assertTrue(chosen >= 0);
					for (int i = 0; i < (k+2)/2; i++) {
						String message = "decreasing, i="+i+", k="+k;
						assertEquals(message, i, eq[i]);
					}
					for (int i = (k+2)/2; i < n; i++) {
						String message = "decreasing, i="+i+", k="+k;
						assertEquals(message, -1, eq[i]);
					}
				}
			}
		}
	}
	
	@Test
	public void testWithIntCostsBetaMax() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 1.0);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsBetaMax() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 1.0);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
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
	public void testWithIntCostsBetaMin() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.0);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCost());
			assertEquals((n+1)*n/2, tracker.getCost());
			tracker = new ProgressTracker<Permutation>();
			ch.setProgressTracker(tracker);
			assertTrue(tracker == ch.getProgressTracker());
		}
	}
	
	@Test
	public void testWithDoubleCostsBetaMin() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			AcceptanceBandSampling ch = new AcceptanceBandSampling(h, 0.0);
			assertEquals(0, ch.getTotalRunLength());
			assertTrue(problem == ch.getProblem());
			ProgressTracker<Permutation> tracker = ch.getProgressTracker();
			SolutionCostPair<Permutation> solution = ch.optimize();
			assertEquals(1, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
			Permutation p = solution.getSolution();
			assertEquals(n, p.length());
			solution = ch.optimize();
			assertEquals(2, ch.getTotalRunLength());
			assertEquals((n+1)*n/2, solution.getCostDouble(), 1E-10);
			assertEquals((n+1)*n/2, tracker.getCostDouble(), 1E-10);
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
	private static class IntHeuristic implements ConstructiveHeuristic {
		private IntProblem problem;
		private int n;
		public IntHeuristic(IntProblem problem, int n) { this.problem = problem; this.n = n; }
		@Override public IntProblem getProblem() { return problem; }
		@Override public int completePermutationLength() { return n; }
		@Override public IntIncEval createIncrementalEvaluation() {
			return new IntIncEval();
		}
		@Override public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
			IntIncEval inc = (IntIncEval)incEval;
			if (element % 2 == 0) return 20 + element;
			else return element;
		}
	}
	
	/*
	 * Fake heuristic designed for predictable test cases:
	 * designed to prefer even permutation elements (largest to smallest), followed by odd
	 * (largest to smallest).
	 */
	private static class DoubleHeuristic implements ConstructiveHeuristic {
		private DoubleProblem problem;
		private int n;
		public DoubleHeuristic(DoubleProblem problem, int n) { this.problem = problem; this.n = n; }
		@Override public DoubleProblem getProblem() { return problem; }
		@Override public int completePermutationLength() { return n; }
		@Override public DoubleIncEval createIncrementalEvaluation() {
			return new DoubleIncEval();
		}
		@Override public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
			DoubleIncEval inc = (DoubleIncEval)incEval;
			if (element % 2 == 0) return 20 + element;
			else return element;
		}
	}
	
	/*
	 * Fake designed for predictable test cases.
	 */
	private static class IntIncEval implements IncrementalEvaluation {
		private int sum;
		@Override public void extend(PartialPermutation p, int element) { sum += element + 1; }
	}
	
	/*
	 * Fake designed for predictable test cases.
	 */
	private static class DoubleIncEval implements IncrementalEvaluation {
		private int sum;
		@Override public void extend(PartialPermutation p, int element) { sum += element + 1; }
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
	
}