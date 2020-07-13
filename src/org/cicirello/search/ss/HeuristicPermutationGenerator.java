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
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * <p>This class generates solutions to permutation optimization
 * problems using a constructive heuristic. Unless the heuristic
 * given to it is randomized, this class is completely deterministic
 * and has no randomized behavior. Thus, executing the {@link #optimize}
 * method multiple times should produce the same result each time.
 * When using a constructive heuristic, you begin with an empty solution,
 * in this case an empty permutation, and you then use a constructive
 * heuristic to choose which element to add to the partial solution,
 * in this case to the partial permutation.  This is repeated until
 * you derive a complete solution (i.e., a complete permutation).</p>
 *
 * <p>Strictly speaking, constructive heuristics are not necessarily 
 * just for permutations.  But at the present time, the library only
 * supports constructive heuristics for permutation optimization problems.</p>
 *
 * <p>Assuming that the length of the permutation is N, and that the runtime
 * of the heuristic is O(f(N)), the runtime to construct one permutation
 * using a constructive heuristic is O(N<sup>2</sup> f(N)).  If the cost, f(N), to
 * heuristically evaluate one permutation element is simply, O(1), constant
 * time, then the cost to heuristically construct 
 * a permutation is simply O(N<sup>2</sup>).</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.7.2020
 */
public final class HeuristicPermutationGenerator implements SimpleMetaheuristic<Permutation> {
	
	private final HeuristicGenerator generator;
	private ProgressTracker<Permutation> tracker;
	private int numGenerated;
	
	/**
	 * Constructs an HeuristicPermutationGenerator for generating solutions
	 * to an optimization problem using a constructive heuristic.  A ProgressTracker 
	 * is created for you.
	 * @param heuristic The constructive heuristic.
	 * @throws NullPointerException if heuristic is null
	 */
	public HeuristicPermutationGenerator(ConstructiveHeuristic heuristic) {
		this(heuristic, new ProgressTracker<Permutation>());
	}
	
	/**
	 * Constructs an HeuristicPermutationGenerator for generating solutions
	 * to an optimization problem using a constructive heuristic.
	 * @param heuristic The constructive heuristic.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicPermutationGenerator(ConstructiveHeuristic heuristic, ProgressTracker<Permutation> tracker) {
		if (heuristic == null || tracker == null) {
			throw new NullPointerException();
		}
		this.tracker = tracker;
		// default: numGenerated = 0;
		if (heuristic.getProblem() instanceof IntegerCostOptimizationProblem) {
			generator = new IntCost(heuristic);
		} else {
			generator = new DoubleCost(heuristic);
		}
	}
	
	/*
	 * private for use by split method
	 */
	private HeuristicPermutationGenerator(HeuristicPermutationGenerator other) {
		tracker = other.tracker;
		if (other.generator instanceof IntCost) {
			generator = new IntCost((IntCost)other.generator);
		} else {
			generator = new DoubleCost((DoubleCost)other.generator);
		}
		// default: numGenerated = 0;
	}
	
	@Override
	public SolutionCostPair<Permutation> optimize() {
		return generator.optimize();
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
		return generator.getProblem();
	}
	
	@Override
	public HeuristicPermutationGenerator split() {
		return new HeuristicPermutationGenerator(this);
	}
	
	private interface HeuristicGenerator {
		SolutionCostPair<Permutation> optimize();
		Problem<Permutation> getProblem();
	}
	
	/*
	 * private inner class for handling the case when costs are integers
	 */
	private final class IntCost implements HeuristicGenerator {
		
		private final ConstructiveHeuristic heuristic;
		
		public IntCost(ConstructiveHeuristic heuristic) {
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
			if (tracker.isStopped() || tracker.didFindBest()) {
				return null;
			}
			IncrementalEvaluation incEval = heuristic.createIncrementalEvaluation();
			int n = heuristic.completePermutationLength();
			PartialPermutation p = new PartialPermutation(n);
			while (!p.isComplete()) {
				int k = p.numExtensions();
				if (k==1) {
					incEval.extend(p, p.getExtension(0));
					p.extend(0);
				} else {
					double bestH = Double.NEGATIVE_INFINITY;
					int which = 0;
					for (int i = 0; i < k; i++) {
						double h = heuristic.h(p, p.getExtension(i), incEval);
						if (h > bestH) {
							bestH = h;
							which = i;
						}
					}
					incEval.extend(p, p.getExtension(which));
					p.extend(which);
				}
			}
			numGenerated++;
			Permutation complete = p.toComplete();
			SolutionCostPair<Permutation> solution = heuristic.getProblem().getSolutionCostPair(complete);
			tracker.update(solution.getCost(), complete);
			return solution; 
		}
		
		@Override
		public Problem<Permutation> getProblem() {
			return heuristic.getProblem();
		}
	}
	
	/*
	 * private inner class for handling the case when costs are doubles
	 */
	private final class DoubleCost implements HeuristicGenerator {
		
		private final ConstructiveHeuristic heuristic;
		
		public DoubleCost(ConstructiveHeuristic heuristic) {
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
			if (tracker.isStopped() || tracker.didFindBest()) {
				return null;
			}
			IncrementalEvaluation incEval = heuristic.createIncrementalEvaluation();
			int n = heuristic.completePermutationLength();
			PartialPermutation p = new PartialPermutation(n);
			while (!p.isComplete()) {
				int k = p.numExtensions();
				if (k==1) {
					incEval.extend(p, p.getExtension(0));
					p.extend(0);
				} else {
					double bestH = Double.NEGATIVE_INFINITY;
					int which = 0;
					for (int i = 0; i < k; i++) {
						double h = heuristic.h(p, p.getExtension(i), incEval);
						if (h > bestH) {
							bestH = h;
							which = i;
						}
					}
					incEval.extend(p, p.getExtension(which));
					p.extend(which);
				}
			}
			numGenerated++;
			Permutation complete = p.toComplete();
			SolutionCostPair<Permutation> solution = heuristic.getProblem().getSolutionCostPair(complete);
			tracker.update(solution.getCostDouble(), complete);
			return solution;
		}
		
		@Override
		public Problem<Permutation> getProblem() {
			return heuristic.getProblem();
		}
	}
}