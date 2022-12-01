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

package org.cicirello.search.hc;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.util.Copyable;

/**
 * This class implements steepest descent hill climbing. In hill climbing, the search typically
 * begins at a randomly generated candidate solution. It then iterates over the so called
 * "neighbors" of the current candidate solution, choosing to move to a neighbor that locally
 * appears better than the current candidate (i.e., has a lower cost value). This is then repeated
 * until the search terminates when all neighbors of the current candidate solution are worse than
 * the current candidate solution.
 *
 * <p>In steepest descent hill climbing, the search always iterates over all of the neighbors of the
 * current candidate before deciding which to move to. It then picks the neighbor with lowest cost
 * value from among all those neighbors whose cost is lower than the current cost. If no such
 * neighbor exists, the search terminates with the current solution.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class SteepestDescentHillClimber<T extends Copyable<T>>
    extends AbstractHillClimber<T> {

  private final IterableMutationOperator<T> mutation;

  /**
   * Constructs a steepest descent hill climber object for real-valued optimization problem.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator.
   * @param initializer The source of random initial states for each hill climb.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SteepestDescentHillClimber(
      OptimizationProblem<T> problem,
      IterableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    super(problem, initializer, tracker);
    if (mutation == null) {
      throw new NullPointerException();
    }
    this.mutation = mutation;
  }

  /**
   * Constructs a steepest descent hill climber object for integer-valued optimization problem.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator.
   * @param initializer The source of random initial states for each hill climb.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SteepestDescentHillClimber(
      IntegerCostOptimizationProblem<T> problem,
      IterableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    super(problem, initializer, tracker);
    if (mutation == null) {
      throw new NullPointerException();
    }
    this.mutation = mutation;
  }

  /**
   * Constructs a steepest descent hill climber object for real-valued optimization problem. A
   * {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator.
   * @param initializer The source of random initial states for each hill climb.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SteepestDescentHillClimber(
      OptimizationProblem<T> problem,
      IterableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    super(problem, initializer, new ProgressTracker<T>());
    if (mutation == null) {
      throw new NullPointerException();
    }
    this.mutation = mutation;
  }

  /**
   * Constructs a steepest descent hill climber object for integer-valued optimization problem. A
   * {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator.
   * @param initializer The source of random initial states for each hill climb.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SteepestDescentHillClimber(
      IntegerCostOptimizationProblem<T> problem,
      IterableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    super(problem, initializer, new ProgressTracker<T>());
    if (mutation == null) {
      throw new NullPointerException();
    }
    this.mutation = mutation;
  }

  /*
   * private copy constructor in support of the split method.
   * note: copies references to thread-safe components, and splits potentially non-threadsafe components
   */
  private SteepestDescentHillClimber(SteepestDescentHillClimber<T> other) {
    super(other);

    // split: not threadsafe
    mutation = other.mutation.split();
  }

  @Override
  public SteepestDescentHillClimber<T> split() {
    return new SteepestDescentHillClimber<T>(this);
  }

  @Override
  OneClimb<T> initClimberInt() {
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
      boolean isMinCost = pOptInt.isMinCost(currentCost);
      if (currentCost < tracker.getCost()) {
        tracker.update(currentCost, current, isMinCost);
      }
      return new SolutionCostPair<T>(current, currentCost, isMinCost);
    };
  }

  @Override
  OneClimb<T> initClimberDouble() {
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
      boolean isMinCost = pOpt.isMinCost(currentCost);
      if (currentCost < tracker.getCostDouble()) {
        tracker.update(currentCost, current, isMinCost);
      }
      return new SolutionCostPair<T>(current, currentCost, isMinCost);
    };
  }
}
