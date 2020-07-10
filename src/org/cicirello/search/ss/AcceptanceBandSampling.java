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

import org.cicirello.permutations.Permutation;
import org.cicirello.search.SimpleMetaheuristic;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>The AcceptanceBandSampling class implements a form of stochastic sampling 
 * search that uses a constructive heuristic to guide the random decisions.
 * When making a random decision, all options are evaluated with the heuristic.
 * An acceptance band is then defined based upon the option with the highest
 * heuristic evaluation.  Specifically, all options with a heuristic evaluation 
 * within B% of the highest heuristic evaluation of the available options are
 * considered equivalent.  A choice is then made uniformly at random from the
 * set of equivalents.</p>
 * 
 * <p>The search generates N random candidate solutions to the 
 * problem, using a problem-specific heuristic for guidance.
 * It evaluates each of the N candidate solutions with respect to the optimization
 * problem's cost function, and returns the best of the N candidate solutions.</p>
 *
 * <p>Although the algorithm itself is not technically restricted to 
 * permutation problems, the AcceptanceBandSampling class only supports
 * optimization problems over the space of permutations.</p>
 *
 * <p>The acceptance bands are defined in terms of a parameter &beta;,
 * which must be in the interval [0.0, 1.0].  Imagine that we have a set of k
 * alternatives, a[0], a[1], ..., a[k], to pick from that have heuristic 
 * values: h[0], h[1], ..., h[k].
 * We assume in this implementation that higher heuristic values imply the
 * options perceived better by the heuristic.  We compute:
 * h' = max {h[0], h[1], ..., h[k]}.  We define an acceptance threshold:
 * T = (1.0 - beta)h'.  The set S of equivalent choices is then
 * computed as: S = { a[k] | h[k] &ge; T}.  We then choose uniformly at
 * random from the set S.</p>
 *
 * <p>If beta=1.0, then all alternatives are considered equivalent (we assume
 * heuristic values are non-negative) since the threshold T would be 0.0
 * regardless of the heuristic values.  If beta=0.0, then only the choices
 * whose heuristic value is equal to the highest heuristic value are considered,
 * since in this case the threshold T is h'.  This implementation allows you
 * to specify your choice of beta, and also provides a default of beta=0.1.
 * That default means that all choices within 10% of the option perceived as
 * best by the heuristic are considered equivalent.</p>
 *
 * <p>To use this implementation of acceptance bands, 
 * you will need to implement a constructive
 * heuristic for your problem using the {@link ConstructiveHeuristic} interface.</p>
 *
 * <p>Assuming that the length of the permutation is L, and that the runtime
 * of the constructive heuristic is O(f(L)), the runtime to construct one permutation
 * using acceptance bands is O(L<sup>2</sup> f(L)).  If the cost, f(L), to
 * heuristically evaluate one permutation element is simply, O(1), constant
 * time, then the cost to heuristically construct 
 * one permutation is simply O(L<sup>2</sup>).</p>
 *
 * <p>The term "acceptance bands", as we use here, was introduced
 * to describe a stochastic sampling algorithm for finding feasible
 * solutions to a job scheduling problem, as an alternative to systematic backtracking
 * in the following paper:</p>
 * <ul>
 * <li>Angelo Oddi and Stephen F. Smith. 1997. Stochastic procedures for 
 * generating feasible schedules. Proceedings of the 14th National Conference on 
 * Artificial Intelligence. AAAI Press, 308–314.</li>
 * </ul>
 * <p>An approach, referred to as "heuristic equivalency", has been used
 * to randomize variable-ordering/value-ordering heuristics within a 
 * systematic backtracking search.  The randomization technique of heuristic
 * equivalency is the same as that of acceptance bands, but for a different
 * purpose.  Heuristic equivalency was used within a backtracking search, while
 * acceptance bands was used to replace backtracking.  Heuristic equivalency
 * is described in the following paper:</p>
 * <ul>
 * <li>Carla P. Gomes, Bart Selman, and Henry Kautz. 1998. 
 * Boosting combinatorial search through randomization. 
 * Proceedings of the 15th National Conference on Artificial Intelligence. 
 * AAAI Press, 431–437.</li>
 * </ul>
 * <p>The implementation of the AcceptanceBandSampling class in our library
 * implements the stochastic sampling version, and does not involve any
 * backtracking.</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.10.2020
 */
public final class AcceptanceBandSampling implements SimpleMetaheuristic<Permutation>, Metaheuristic<Permutation> {
	
	private final Sampler sampler;
	private final double acceptancePercentage;
	private ProgressTracker<Permutation> tracker;
	private int numGenerated;
	
	/**
	 * Constructs an AcceptanceBandSampling search object.  
	 * Uses a default value of beta = 0.1. This default has the effect
	 * of considering all heuristic values within 10% of that of the option
	 * perceived best by the heuristic to be considered equivalent.
	 * A ProgressTracker 
	 * is created for you.  
	 * @param heuristic The constructive heuristic.
	 * @throws NullPointerException if heuristic is null
	 */
	public AcceptanceBandSampling(ConstructiveHeuristic<? extends Number> heuristic) {
		this(heuristic, 0.1, new ProgressTracker<Permutation>());
	}
	
	/**
	 * Constructs an AcceptanceBandSampling search object.    
	 * Uses a default value of beta = 0.1. This default has the effect
	 * of considering all heuristic values within 10% of that of the option
	 * perceived best by the heuristic to be considered equivalent.
	 * @param heuristic The constructive heuristic.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public AcceptanceBandSampling(ConstructiveHeuristic<? extends Number> heuristic, ProgressTracker<Permutation> tracker) {
		this(heuristic, 0.1, tracker);
	}
	
	/**
	 * Constructs an AcceptanceBandSampling search object.  A ProgressTracker 
	 * is created for you.
	 * @param heuristic The constructive heuristic.
	 * @param beta The acceptance band parameter. When making a decision, if h is the max
	 * of the heuristic evaluations of all of the options, then the search will consider all
	 * options whose heuristic evaluation is at least h(1.0 - beta) as equivalent and choose
	 * uniformly at random from among those equivalent options.  The value of beta must satisfy:
	 * 0.0 &le; beta &le; 1.0.  If beta is closer to 0.0, then heuristic values must be closer
	 * to the heuristic value of the perceived best option to be considered equivalent to it.
	 * If beta is 1.0, then all options will be considered equivalent.
	 * @throws NullPointerException if heuristic is null
	 * @throws IllegalArgumentException if beta is less than 0.0 or greater than 1.0.
	 */
	public AcceptanceBandSampling(ConstructiveHeuristic<? extends Number> heuristic, double beta) {
		this(heuristic, beta, new ProgressTracker<Permutation>());
	}
	
	/**
	 * Constructs an AcceptanceBandSampling search object.
	 * @param heuristic The constructive heuristic.
	 * @param beta The acceptance band parameter. When making a decision, if h is the max
	 * of the heuristic evaluations of all of the options, then the search will consider all
	 * options whose heuristic evaluation is at least h(1.0 - beta) as equivalent and choose
	 * uniformly at random from among those equivalent options.  The value of beta must satisfy:
	 * 0.0 &le; beta &le; 1.0.  If beta is closer to 0.0, then heuristic values must be closer
	 * to the heuristic value of the perceived best option to be considered equivalent to it.
	 * If beta is 1.0, then all options will be considered equivalent.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 * @throws IllegalArgumentException if beta is less than 0.0 or greater than 1.0.
	 */
	public AcceptanceBandSampling(ConstructiveHeuristic<? extends Number> heuristic, double beta, ProgressTracker<Permutation> tracker) {
		if (heuristic == null || tracker == null) {
			throw new NullPointerException();
		}
		if (beta < 0.0 || beta > 1.0) {
			throw new IllegalArgumentException("beta must be in the interval: [0.0, 1.0].");
		}
		this.tracker = tracker;
		acceptancePercentage = 1.0 - beta;
		if (heuristic.getProblem() instanceof IntegerCostOptimizationProblem) {
			@SuppressWarnings("unchecked")
			ConstructiveHeuristic<Integer> h = (ConstructiveHeuristic<Integer>)heuristic;
			sampler = new IntCost(h);
		} else {
			@SuppressWarnings("unchecked")
			ConstructiveHeuristic<Double> h = (ConstructiveHeuristic<Double>)heuristic;
			sampler = new DoubleCost(h);
		}
		// default: numGenerated = 0;
	}
	
	/*
	 * private for use by split method
	 */
	private AcceptanceBandSampling(AcceptanceBandSampling other) {
		tracker = other.tracker;
		acceptancePercentage = other.acceptancePercentage;
		if (other.sampler instanceof IntCost) {
			sampler = new IntCost((IntCost)other.sampler);
		} else {
			sampler = new DoubleCost((DoubleCost)other.sampler);
		}
		// default: numGenerated = 0;
	}
	
	@Override
	public SolutionCostPair<Permutation> optimize() {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		numGenerated++;
		return sampler.optimize();
	}
	
	/**
	 * <p>Generates multiple samples using Acceptance Band Sampling.  
	 * Returns the best solution of the set of samples.</p>
	 *
	 * @param numSamples The number of samples to perform.
	 * @return The best solution of this set of samples, which may or may not be the 
	 * same as the solution contained
	 * in this search's {@link org.cicirello.search.ProgressTracker ProgressTracker}, 
	 * which contains the best of all runs
	 * across all calls to the various optimize methods.
	 * Returns null if no runs executed, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	@Override
	public SolutionCostPair<Permutation> optimize(int numSamples) {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		SolutionCostPair<Permutation> best = null;
		for (int i = 0; i < numSamples && !tracker.didFindBest() && !tracker.isStopped(); i++) {
			SolutionCostPair<Permutation> current = sampler.optimize();
			numGenerated++;
			if (best == null || current.compareTo(best) < 0) best = current;
		}
		return best;
	}
	
	@Override
	public ProgressTracker<Permutation> getProgressTracker() {
		return tracker;
	}
	
	@Override
	public void setProgressTracker(ProgressTracker<Permutation> tracker) {
		if (tracker != null) this.tracker = tracker;
	}
	
	@Override
	public long getTotalRunLength() {
		return numGenerated;
	}
	
	@Override
	public Problem<Permutation> getProblem() {
		return sampler.getProblem();
	}
	
	@Override
	public AcceptanceBandSampling split() {
		return new AcceptanceBandSampling(this);
	}
	
	/*
	 * package-private rather than private to support unit testing
	 */
	int choose(double[] values, int k, double max, int[] equivalents) {
		double threshold = max * acceptancePercentage;
		int n = 0;
		for (int i = 0; i < k; i++) {
			if (values[i] >= threshold) {
				equivalents[n] = i;
				n++;
			}
		}
		return equivalents[RandomIndexer.nextInt(n)];
	}
	
	private interface Sampler {
		SolutionCostPair<Permutation> optimize();
		Problem<Permutation> getProblem();
	}
	
	/*
	 * private inner class for handling the case when costs are integers
	 */
	private final class IntCost implements Sampler {
		
		private final ConstructiveHeuristic<Integer> heuristic;
		
		public IntCost(ConstructiveHeuristic<Integer> heuristic) {
			this.heuristic = heuristic;
		}
		
		/*
		 * private for use by split method
		 */
		private IntCost(IntCost other) {
			heuristic = other.heuristic;
		}
		
		@Override
		public SolutionCostPair<Permutation> optimize() {
			IncrementalEvaluation<Integer> incEval = heuristic.createIncrementalEvaluation();
			int n = heuristic.completePermutationLength();
			PartialPermutation p = new PartialPermutation(n);
			double[] v = new double[n];
			int[] equivalents = new int[n];
			while (!p.isComplete()) {
				int k = p.numExtensions();
				if (k==1) {
					incEval.extend(p, p.getExtension(0));
					p.extend(0);
				} else {
					double max = Double.NEGATIVE_INFINITY;
					for (int i = 0; i < k; i++) {
						v[i] = heuristic.h(p, p.getExtension(i), incEval);
						if (v[i] > max) max = v[i];
					}
					int which = choose(v, k, max, equivalents);		
					incEval.extend(p, p.getExtension(which));
					p.extend(which);
				}
			}
			int cost = incEval.cost();
			Permutation complete = p.toComplete();
			if (cost < tracker.getCost()) {
				tracker.update(cost, complete);
			}
			return new SolutionCostPair<Permutation>(complete, cost);
		}
		
		@Override
		public Problem<Permutation> getProblem() {
			return heuristic.getProblem();
		}
	}
	
	/*
	 * private inner class for handling the case when costs are doubles
	 */
	private final class DoubleCost implements Sampler {
		
		private final ConstructiveHeuristic<Double> heuristic;
		
		public DoubleCost(ConstructiveHeuristic<Double> heuristic) {
			this.heuristic = heuristic;
		}
		
		/*
		 * private for use by split method
		 */
		private DoubleCost(DoubleCost other) {
			heuristic = other.heuristic;
		}
		
		@Override
		public SolutionCostPair<Permutation> optimize() {
			IncrementalEvaluation<Double> incEval = heuristic.createIncrementalEvaluation();
			int n = heuristic.completePermutationLength();
			PartialPermutation p = new PartialPermutation(n);
			double[] v = new double[n];
			int[] equivalents = new int[n];
			while (!p.isComplete()) {
				int k = p.numExtensions();
				if (k==1) {
					incEval.extend(p, p.getExtension(0));
					p.extend(0);
				} else {
					double max = Double.NEGATIVE_INFINITY;
					for (int i = 0; i < k; i++) {
						v[i] = heuristic.h(p, p.getExtension(i), incEval);
						if (v[i] > max) max = v[i];
					}
					int which = choose(v, k, max, equivalents);
					incEval.extend(p, p.getExtension(which));
					p.extend(which);
				}
			}
			double cost = incEval.cost();
			Permutation complete = p.toComplete();
			if (cost < tracker.getCostDouble()) {
				tracker.update(cost, complete);
			}
			return new SolutionCostPair<Permutation>(complete, cost);
		}
		
		@Override
		public Problem<Permutation> getProblem() {
			return heuristic.getProblem();
		}
	}
}