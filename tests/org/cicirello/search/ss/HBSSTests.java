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
 * JUnit tests for HeuristicBiasedStochasticSampling.
 */
public class HBSSTests {
	
	@Test
	public void testWithIntCosts() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
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
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
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
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
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
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
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
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, originalTracker);
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
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, originalTracker);
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
			HeuristicBiasedStochasticSampling chOriginal = new HeuristicBiasedStochasticSampling(h);
			HeuristicBiasedStochasticSampling ch = chOriginal.split();
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
			HeuristicBiasedStochasticSampling chOriginal = new HeuristicBiasedStochasticSampling(h);
			HeuristicBiasedStochasticSampling ch = chOriginal.split();
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
	public void testPrecomputeBiases() {
		for (int n = 0; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
			double[] biases = ch.precomputeBiases(n);
			double expected = 0;
			for (int i = 1; i <= n; i++) {
				expected += 1.0 / i;
				assertEquals(expected, biases[i-1], 1E-10);
			}
		}
		for (int n = 0; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, 2.0);
			double[] biases = ch.precomputeBiases(n);
			double expected = 0;
			for (int i = 1; i <= n; i++) {
				expected += 1.0 / (i*i);
				assertEquals(expected, biases[i-1], 1E-10);
			}
		}
		for (int n = 0; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, true);
			double[] biases = ch.precomputeBiases(n);
			double expected = 0;
			for (int i = 1; i <= n; i++) {
				expected += Math.exp(-i);
				assertEquals(expected, biases[i-1], 1E-10);
			}
		}
		HeuristicBiasedStochasticSampling.BiasFunction bias = 
			new HeuristicBiasedStochasticSampling.BiasFunction() {
				@Override public double bias(int rank) { return 1.0/(rank*rank); }
			};
		for (int n = 0; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, bias);
			double[] biases = ch.precomputeBiases(n);
			double expected = 0;
			for (int i = 1; i <= n; i++) {
				expected += 1.0 / (i*i);
				assertEquals(expected, biases[i-1], 1E-10);
			}
		}
	}
	
	@Test
	public void testSelect() {
		for (int n = 2; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
			for (int k = 2; k <= n; k++) {
				double inc = 1.0 / k;
				double[] values = new double[n];
				values[0] = inc;
				for (int i = 1; i < k; i++) {
					values[i] = values[i-1] + inc;
				}
				double u = 0.0;
				for (int i = 0; i < k; i++, u+=inc) {
					assertEquals(i, ch.select(values, k, u));
				}
				u = inc / 2;
				for (int i = 0; i < k; i++, u+=inc) {
					assertEquals(i, ch.select(values, k, u));
				}
				u = 1.0 - 1E-10;
				for (int i = k-1; i >= 0; i--, u-=inc) {
					assertEquals(i, ch.select(values, k, u));
				}
			}
		}
	}
	
	@Test
	public void testRandomizedSelect() {
		for (int n = 0; n < 8; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h);
			for (int k = 1; k <= n; k++) {
				double[] values = new double[n];
				for (int i = 0; i < k; i++) values[i] = 100-i;
				for (int i = k; i < n; i++) values[i] = 9999;
				for (int chosenRank = 1; chosenRank <= k; chosenRank++) {
					int[] indexes = new int[n];
					for (int i = 0; i < n; i++) indexes[i] = i;
					String message = "Decreasing: chosenRank:" + chosenRank + " k:" + k;
					assertEquals(message, chosenRank-1, ch.randomizedSelect(indexes, values, k, chosenRank));
				}
			}
			for (int k = 1; k <= n; k++) {
				double[] values = new double[n];
				for (int i = 0; i < k; i++) values[i] = 2+i;
				for (int i = k; i < n; i++) values[i] = 9999;
				for (int chosenRank = 1; chosenRank <= k; chosenRank++) {
					int[] indexes = new int[n];
					for (int i = 0; i < n; i++) indexes[i] = i;
					String message = "Increasing: chosenRank:" + chosenRank + " k:" + k;
					assertEquals(message, k-chosenRank, ch.randomizedSelect(indexes, values, k, chosenRank));
				}
			}
		}
	}
	
	
	@Test
	public void testWithIntCostsExponent() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, 2.0);
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
	public void testWithDoubleCostsExponent() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, 2.0);
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
	public void testWithIntCostsExponentWithProgressTracker() {
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, 2.0, originalTracker);
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
	public void testWithDoubleCostsExponentWithProgressTracker() {
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, 2.0, originalTracker);
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
	public void testWithIntCostsExponentSplit() {
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling chOriginal = new HeuristicBiasedStochasticSampling(h, 2.0);
			HeuristicBiasedStochasticSampling ch = chOriginal.split();
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
	public void testWithDoubleCostsExponentSplit() {
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicBiasedStochasticSampling chOriginal = new HeuristicBiasedStochasticSampling(h, 2.0);
			HeuristicBiasedStochasticSampling ch = chOriginal.split();
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
	public void testWithIntCostsBias() {
		HeuristicBiasedStochasticSampling.BiasFunction bias = 
			new HeuristicBiasedStochasticSampling.BiasFunction() {
				@Override public double bias(int rank) { return 1.0/(rank*rank); }
			};
		for (int n = 0; n < 10; n++) {
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, bias);
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
	public void testWithDoubleCostsBias() {
		HeuristicBiasedStochasticSampling.BiasFunction bias = 
			new HeuristicBiasedStochasticSampling.BiasFunction() {
				@Override public double bias(int rank) { return 1.0/(rank*rank); }
			};
		for (int n = 0; n < 10; n++) {
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, bias);
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
	public void testWithIntCostsBiasWithProgressTracker() {
		HeuristicBiasedStochasticSampling.BiasFunction bias = 
			new HeuristicBiasedStochasticSampling.BiasFunction() {
				@Override public double bias(int rank) { return 1.0/(rank*rank); }
			};
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			IntProblem problem = new IntProblem();
			IntHeuristic h = new IntHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, bias, originalTracker);
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
	public void testWithDoubleCostsBiasWithProgressTracker() {
		HeuristicBiasedStochasticSampling.BiasFunction bias = 
			new HeuristicBiasedStochasticSampling.BiasFunction() {
				@Override public double bias(int rank) { return 1.0/(rank*rank); }
			};
		for (int n = 0; n < 10; n++) {
			ProgressTracker<Permutation> originalTracker = new ProgressTracker<Permutation>();
			DoubleProblem problem = new DoubleProblem();
			DoubleHeuristic h = new DoubleHeuristic(problem, n);
			HeuristicBiasedStochasticSampling ch = new HeuristicBiasedStochasticSampling(h, bias, originalTracker);
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
			if (element % 2 == 0) return n + element;
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
			if (element % 2 == 0) return n + element;
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