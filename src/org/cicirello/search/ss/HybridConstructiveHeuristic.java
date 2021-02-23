/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import org.cicirello.math.rand.RandomIndexer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntSupplier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>A HybridConstructiveHeuristic maintains a set of 
 * {@link ConstructiveHeuristic} objects for a problem,
 * for use in a multiheuristic stochastic sampling search,
 * where each full iteration of the stochastic sampler
 * uses a single heuristic for all decisions, but where 
 * a different heuristic is chosen for each iteration.</p>
 *
 * <p>The HybridConstructiveHeuristic supports the following
 * heuristic selection strategies</p>
 * <ul>
 * <li>Choose a heuristic uniformly at random at the start
 * of the iteration.</li>
 * <li>Use a round robin strategy that uses the heuristics
 * in order as determined by the order they were passed to the
 * constructor, cycling around to the start of the list when
 * necessary.</li>
 * </ul>
 *
 * @param <T> The type of Partial object for which this 
 * HybridConstructiveHeuristic guides construction, which is 
 * assumed to be an object that is a sequence of integers (e.g., vector of integers,
 * permutation, or some other indexable type that stores integers).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 2.23.2021
 */
public final class HybridConstructiveHeuristic<T extends Copyable<T>> implements ConstructiveHeuristic<T> {
	
	private final ArrayList<ConstructiveHeuristic<T>> heuristics;
	private final int NUM_H;
	private final IntSupplier heuristicSelector;
	
	/**
	 * Constructs the HybridConstructiveHeuristic, where the heuristic
	 * is chosen uniformly at random at the start of each iteration of the
	 * stochastic sampler (i.e., each time {@link #createIncrementalEvaluation}
	 * is called).
	 * @param heuristics A collection of ConstructiveHeuristic, all of which must
	 * be configured to solve the same problem instance. The collection of heuristics
	 * must be non-empty.
	 * @throws IllegalArgumentException if not all of the heuristics are configured
	 * for the same problem instance.
	 * @throws IllegalArgumentException if heuristics.size() equals 0.
	 */
	public HybridConstructiveHeuristic(Collection<? extends ConstructiveHeuristic<T>> heuristics) {
		this(heuristics, false);
	}
	
	/**
	 * Constructs the HybridConstructiveHeuristic, where the heuristic
	 * is either chosen uniformly at random at the start of each iteration of the
	 * stochastic sampler (i.e., each time {@link #createIncrementalEvaluation}
	 * is called), or using the round robin strategy.
	 * @param heuristics A collection of ConstructiveHeuristic, all of which must
	 * be configured to solve the same problem instance. The collection of heuristics
	 * must be non-empty.
	 * @param roundRobin If true, then each time {@link #createIncrementalEvaluation}
	 * is called, the HybridConstructiveHeuristic cycles to the next heuristic systematically.
	 * Otherwise, if false, it chooses uniformly at random.
	 * @throws IllegalArgumentException if not all of the heuristics are configured
	 * for the same problem instance.
	 * @throws IllegalArgumentException if heuristics.size() equals 0.
	 */
	public HybridConstructiveHeuristic(Collection<? extends ConstructiveHeuristic<T>> heuristics, boolean roundRobin) {
		this.heuristics = initializeHeuristics(heuristics);
		NUM_H = heuristics.size();
		if (roundRobin) {
			heuristicSelector = new IntSupplier() {
				AtomicInteger lastHeuristic = new AtomicInteger(NUM_H-1);
				public int getAsInt() {
					return lastHeuristic.updateAndGet(
						(h) -> {
							h++;
							if (h==NUM_H) h=0;
							return h;
						}
					);
				}
			};
		} else {
			heuristicSelector = () -> RandomIndexer.nextBiasedInt(NUM_H);
		}
	}
	
	private ArrayList<ConstructiveHeuristic<T>> initializeHeuristics(Collection<? extends ConstructiveHeuristic<T>> heuristics) {
		if (heuristics.size()==0) {
			throw new IllegalArgumentException("Must pass at least one heuristic.");
		}
		ConstructiveHeuristic<T> first = null;
		for (ConstructiveHeuristic<T> h : heuristics) {
			if (first==null) {
				first = h;
			} else if (h.getProblem() != first.getProblem()) {
				throw new IllegalArgumentException("All heuristics must be configured for the same problem.");
			} 
		}
		return new ArrayList<ConstructiveHeuristic<T>>(heuristics);
	}
	
	/**
	 * This method handles choosing the heuristic for the next iteration
	 * of the stochastic sampling search, and then delegates the usual function
	 * of this method to the chosen heuristic. See the {@link ConstructiveHeuristic}
	 * interface for full details of the functionality of this method.
	 *
	 * @return An IncrementalEvaluation for an empty Partial 
	 * to be used for incrementally computing any data required by the {@link #h} method.
	 */
	public IncrementalEvaluation<T> createIncrementalEvaluation() {
		int which = heuristicSelector.getAsInt();
		IncrementalEvaluationWrapper<T> wrapped = new IncrementalEvaluationWrapper<T>(
			heuristics.get(which).createIncrementalEvaluation(),
			which
		);
		return wrapped;
	}
	
	@Override
	public double h(Partial<T> p, int element, IncrementalEvaluation<T> incEval) {
		IncrementalEvaluationWrapper<T> wrapped = (IncrementalEvaluationWrapper<T>)incEval;
		return heuristics.get(wrapped.which).h(p, element, wrapped.incEval);
	}
	
	@Override
	public Partial<T> createPartial(int n) {
		return heuristics.get(0).createPartial(n);
	}
	
	@Override
	public int completeLength() {
		return heuristics.get(0).completeLength();
	}
	
	@Override
	public Problem<T> getProblem() {
		return heuristics.get(0).getProblem();
	}
	
	private static class IncrementalEvaluationWrapper<U extends Copyable<U>> implements IncrementalEvaluation<U> {
		private final IncrementalEvaluation<U> incEval;
		private final int which;
		
		/**
		 * Constructs an IncrementalEvaluationWrapper.
		 * @param incEval The IncrementalEvaluation to wrap.
		 * @param which The heuristic index to which incEval corresponds.
		 */
		public IncrementalEvaluationWrapper(IncrementalEvaluation<U> incEval, int which) {
			this.incEval = incEval;
			this.which = which;
		}
		
		@Override
		public void extend​(Partial<U> p, int element) {
			incEval.extend(p, element);
		}
	}
}