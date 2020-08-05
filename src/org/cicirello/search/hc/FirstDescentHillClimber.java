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
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.MutationIterator;


/**
 * <p>This class implements first descent hill climbing.
 * In hill climbing, the search typically begins at a randomly generated
 * candidate solution.  It then iterates over the so called "neighbors"
 * of the current candidate solution, choosing to move to a neighbor
 * that locally appears better than the current candidate (i.e., has a lower
 * cost value).  This is then repeated until the search terminates 
 * when all neighbors of the current
 * candidate solution are worse than the current candidate solution.</p>
 *
 * <p>In first descent hill climbing, the search always picks the first neighbor
 * whose cost is lower than the current cost (rather than iterating over all neighbors).
 * If no such neighbor exists, the
 * search terminates with the current solution.</p> 
 *
 * @param <T> The type of object under optimization.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 8.5.2020
 */
public final class FirstDescentHillClimber<T extends Copyable<T>> extends AbstractHillClimber<T> {
	
	/**
	 * Constructs a first descent hill climber object for real-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public FirstDescentHillClimber(OptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer, ProgressTracker<T> tracker) {
		super(problem, mutation, initializer, tracker);
	}
	
	/**
	 * Constructs a first descent hill climber object for integer-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public FirstDescentHillClimber(IntegerCostOptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer, ProgressTracker<T> tracker) {
		super(problem, mutation, initializer, tracker);
	}
	
	/**
	 * Constructs a first descent hill climber object for real-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public FirstDescentHillClimber(OptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer) {
		super(problem, mutation, initializer, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a first descent hill climber object for integer-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param mutation A mutation operator.
	 * @param initializer The source of random initial states for each hill climb.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public FirstDescentHillClimber(IntegerCostOptimizationProblem<T> problem, IterableMutationOperator<T> mutation, Initializer<T> initializer) {
		super(problem, mutation, initializer, new ProgressTracker<T>());
	}
	
	/*
	 * private copy constructor in support of the split method.
	 * note: copies references to thread-safe components, and splits 
	 * potentially non-threadsafe components 
	 */
	private FirstDescentHillClimber(FirstDescentHillClimber<T> other) {
		super(other);
	}
	
	@Override
	public FirstDescentHillClimber<T> split() {
		return new FirstDescentHillClimber<T>(this);
	}
	
	OneClimb<T> initClimberInt() {
		return current -> {
				// compute cost of start
				int currentCost = pOptInt.cost(current);				
				boolean keepClimbing = true;
				while (keepClimbing) {
					keepClimbing = false;
					MutationIterator iter = mutation.iterator(current);
					while (iter.hasNext()) {
						iter.nextMutant();
						neighborCount++;
						int cost = pOptInt.cost(current);
						if (cost < currentCost) {
							currentCost = cost;
							keepClimbing = true;
							break;
						}
					}
					if (!keepClimbing) iter.rollback();
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
	
	OneClimb<T> initClimberDouble() {
		return current -> {
				// compute cost of start
				double currentCost = pOpt.cost(current);			
				boolean keepClimbing = true;
				while (keepClimbing) {
					keepClimbing = false;
					MutationIterator iter = mutation.iterator(current);
					while (iter.hasNext()) {
						iter.nextMutant();
						neighborCount++;
						double cost = pOpt.cost(current);
						if (cost < currentCost) {
							currentCost = cost;
							keepClimbing = true;
							break;
						}
					}
					if (!keepClimbing) iter.rollback();
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