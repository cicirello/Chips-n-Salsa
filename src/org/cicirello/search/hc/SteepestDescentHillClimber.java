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
 
package org.cicirello.search.hc;

import org.cicirello.util.Copyable;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.SimpleLocalMetaheuristic;


/**
 * <p>This class implements steepest descent hill climbing.
 * In hill climbing, the search typically begins at a randomly generated
 * candidate solution.  It then iterates over the so called "neighbors"
 * of the current candidate solution, choosing to move to a neighbor
 * that locally appears better than the current candidate (i.e., has a lower
 * cost value).  This is then repeated until the search terminates 
 * when all neighbors of the current
 * candidate solution are worse than the current candidate solution.</p>
 *
 * <p>In steepest descent hill climbing, the search always iterates over all of the
 * neighbors of the current candidate before deciding which to move to.  It then
 * picks the neighbor with lowest cost value from among all those neighbors
 * whose cost is lower than the current cost.  If no such neighbor exists, the
 * search terminates with the current solution.</p> 
 *
 * @param <T> The type of object under optimization.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public final class SteepestDescentHillClimber<T extends Copyable<T>> implements Metaheuristic<T>, SimpleLocalMetaheuristic<T> {
	
	private final OptimizationProblem<T> pOpt;
	private final IntegerCostOptimizationProblem<T> pOptInt;
	private final Initializer<T> initializer;
	private ProgressTracker<T> tracker;
	private final IterableMutationOperator<T> mutation;
	private final OneClimb<T> climber;
	private long neighborCount;
	
	/**
	 * Constructs a steepest descent hill climber object for real-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public SteepestDescentHillClimber(OptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer, ProgressTracker<T> tracker) {
		if (problem == null || mutation == null || initializer == null || tracker == null) {
			throw new NullPointerException();
		}
		pOpt = problem;
		pOptInt = null;
		this.mutation = mutation;
		this.initializer = initializer;
		this.tracker = tracker;
		climber = initClimberDouble();
	}
	
	/**
	 * Constructs a steepest descent hill climber object for integer-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public SteepestDescentHillClimber(IntegerCostOptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer, ProgressTracker<T> tracker) {
		if (problem == null || mutation == null || initializer == null || tracker == null) {
			throw new NullPointerException();
		}
		pOptInt = problem;
		pOpt = null;
		this.mutation = mutation;
		this.initializer = initializer;
		this.tracker = tracker;
		climber = initClimberInt();
	}
	
	/**
	 * Constructs a steepest descent hill climber object for real-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public SteepestDescentHillClimber(OptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer) {
		this(problem, mutation, initializer, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a steepest descent hill climber object for integer-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public SteepestDescentHillClimber(IntegerCostOptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer) {
		this(problem, mutation, initializer, new ProgressTracker<T>());
	}
	
	/*
	 * private copy constructor in support of the split method.
	 * note: copies references to thread-safe components, and splits potentially non-threadsafe components 
	 */
	private SteepestDescentHillClimber(SteepestDescentHillClimber<T> other) {
		// these are threadsafe, so just copy references
		pOpt = other.pOpt;
		pOptInt = other.pOptInt;
		
		// this one must be shared.
		tracker = other.tracker;
		
		// split: not threadsafe
		mutation = other.mutation.split();
		initializer = other.initializer.split();
		
		climber = pOptInt != null ? initClimberInt() : initClimberDouble();
		
		// use default of 0 for this one: neighborCount
	}
	
	@Override
	public SolutionCostPair<T> optimize() {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		neighborCount++;
		return climber.climbOnce(initializer.createCandidateSolution());
	}
	
	@Override
	public SolutionCostPair<T> optimize(T start) {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		return climber.climbOnce(start.copy());
	}
	
	/**
	 * <p>Executes multiple restarts of the hill climber.  Each restart begins from a new
	 * random starting solution.  Returns the best solution across the restarts.</p>
	 *
	 * @param numRestarts The number of restarts of the hill climber.
	 * @return The best solution of this set of restarts, which may or may not be the 
	 * same as the solution contained
	 * in this hill climber's {@link org.cicirello.search.ProgressTracker ProgressTracker}, 
	 * which contains the best of all runs
	 * across all calls to the various optimize methods.
	 * Returns null if no runs executed, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	@Override
	public SolutionCostPair<T> optimize(int numRestarts) {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		SolutionCostPair<T> best = null;
		for (int i = 0; i < numRestarts && !tracker.didFindBest() && !tracker.isStopped(); i++) {
			SolutionCostPair<T> current = climber.climbOnce(initializer.createCandidateSolution());
			neighborCount++;
			if (best == null || current.compareTo(best) < 0) best = current;
		}
		return best;
	}
	
	@Override
	public ProgressTracker<T> getProgressTracker() {
		return tracker;
	}
	
	@Override
	public void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) this.tracker = tracker;
	}
	
	@Override
	public Problem<T> getProblem() {
		return (pOptInt != null) ? pOptInt : pOpt;
	}
	
	/**
	 * <p>Gets the total run length, where run length is number of candidate solutions
	 * generated by the hill climber.  This is the total run length
	 * across all calls to the search.</p>
	 *
	 * @return the total number of candidate solutions generated by the search, across
	 * all calls to the various optimize methods.
	 */
	@Override
	public long getTotalRunLength() {
		return neighborCount;
	}
	
	@Override
	public SteepestDescentHillClimber<T> split() {
		return new SteepestDescentHillClimber<T>(this);
	}
	
	private interface OneClimb<T extends Copyable<T>> {
		SolutionCostPair<T> climbOnce(T current);
	}
	
	private OneClimb<T> initClimberInt() {
		return current -> {
				// compute cost of start
				int currentCost = pOptInt.cost(current);
				boolean keepClimbing = true;
				while (keepClimbing) {
					MutationIterator iter = mutation.iterator(current);
					int bestNeighborCost = currentCost;
					while (iter.hasNext()) {
						iter.nextMutant();
						neighborCount++;
						int cost = pOptInt.cost(current);
						if (cost < bestNeighborCost) {
							iter.setSavepoint();
							bestNeighborCost = cost;
						}
					}
					iter.rollback();
					if (bestNeighborCost == currentCost) {
						keepClimbing = false;
					} else {
						currentCost = bestNeighborCost;
					}
				}
				// update tracker
				if (currentCost < tracker.getCost()) {
					tracker.update(currentCost, current);
					if (currentCost == pOptInt.minCost()) {
						tracker.setFoundBest();
					}
				}
				return new SolutionCostPair<T>(current, currentCost);
		};
	}
	
	private OneClimb<T> initClimberDouble() {
		return current -> {
				// compute cost of start
				double currentCost = pOpt.cost(current);				
				boolean keepClimbing = true;
				while (keepClimbing) {
					MutationIterator iter = mutation.iterator(current);
					double bestNeighborCost = currentCost;
					while (iter.hasNext()) {
						iter.nextMutant();
						neighborCount++;
						double cost = pOpt.cost(current);
						if (cost < bestNeighborCost) {
							iter.setSavepoint();
							bestNeighborCost = cost;
						}
					}
					iter.rollback();
					if (bestNeighborCost == currentCost) {
						keepClimbing = false;
					} else {
						currentCost = bestNeighborCost;
					}
				}
				// update tracker
				if (currentCost < tracker.getCostDouble()) {
					tracker.update(currentCost, current);
					if (currentCost == pOpt.minCost()) {
						tracker.setFoundBest();
					}
				}
				return new SolutionCostPair<T>(current, currentCost);
		};
	}
	
}