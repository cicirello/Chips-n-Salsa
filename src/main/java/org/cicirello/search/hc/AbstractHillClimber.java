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

import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SimpleLocalMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class serves as an abstract base class for the Hill Climbing implementations, including the
 * common functionality.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractHillClimber<T extends Copyable<T>>
    implements Metaheuristic<T>, SimpleLocalMetaheuristic<T> {

  final OptimizationProblem<T> pOpt;
  final IntegerCostOptimizationProblem<T> pOptInt;
  private final Initializer<T> initializer;
  ProgressTracker<T> tracker;

  private final OneClimb<T> climber;

  long neighborCount;

  /**
   * Constructs a first descent hill climber object for real-valued optimization problem.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param initializer The source of random initial states for each hill climb.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  AbstractHillClimber(
      OptimizationProblem<T> problem, Initializer<T> initializer, ProgressTracker<T> tracker) {
    if (problem == null || initializer == null || tracker == null) {
      throw new NullPointerException();
    }
    pOpt = problem;
    pOptInt = null;
    this.initializer = initializer;
    this.tracker = tracker;
    climber = initClimberDouble();
  }

  /**
   * Constructs a first descent hill climber object for integer-valued optimization problem.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param initializer The source of random initial states for each hill climb.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  AbstractHillClimber(
      IntegerCostOptimizationProblem<T> problem,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    if (problem == null || initializer == null || tracker == null) {
      throw new NullPointerException();
    }
    pOptInt = (IntegerCostOptimizationProblem<T>) problem;
    pOpt = null;
    this.initializer = initializer;
    this.tracker = tracker;
    climber = initClimberInt();
  }

  /*
   * package-private copy constructor in support of the split method.
   * note: copies references to thread-safe components, and splits potentially non-threadsafe components
   */
  AbstractHillClimber(AbstractHillClimber<T> other) {
    // these are threadsafe, so just copy references
    pOpt = other.pOpt;
    pOptInt = other.pOptInt;

    // this one must be shared.
    tracker = other.tracker;

    // split: not threadsafe
    initializer = other.initializer.split();

    climber = pOptInt != null ? initClimberInt() : initClimberDouble();

    // use default of 0 for this one: neighborCount
  }

  @Override
  public final SolutionCostPair<T> optimize() {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    neighborCount++;
    return climber.climbOnce(initializer.createCandidateSolution());
  }

  @Override
  public final SolutionCostPair<T> optimize(T start) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    return climber.climbOnce(start.copy());
  }

  /**
   * Executes multiple restarts of the hill climber. Each restart begins from a new random starting
   * solution. Returns the best solution across the restarts.
   *
   * @param numRestarts The number of restarts of the hill climber.
   * @return The best solution of this set of restarts, which may or may not be the same as the
   *     solution contained in this hill climber's {@link org.cicirello.search.ProgressTracker
   *     ProgressTracker}, which contains the best of all runs across all calls to the various
   *     optimize methods. Returns null if no runs executed, such as if the ProgressTracker already
   *     contains the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int numRestarts) {
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
  public final ProgressTracker<T> getProgressTracker() {
    return tracker;
  }

  @Override
  public final void setProgressTracker(ProgressTracker<T> tracker) {
    if (tracker != null) this.tracker = tracker;
  }

  @Override
  public final Problem<T> getProblem() {
    return (pOptInt != null) ? pOptInt : pOpt;
  }

  /**
   * Gets the total run length, where run length is number of candidate solutions generated by the
   * hill climber. This is the total run length across all calls to the search.
   *
   * @return the total number of candidate solutions generated by the search, across all calls to
   *     the various optimize methods.
   */
  @Override
  public final long getTotalRunLength() {
    return neighborCount;
  }

  @Override
  public abstract AbstractHillClimber<T> split();

  interface OneClimb<T extends Copyable<T>> {
    SolutionCostPair<T> climbOnce(T current);
  }

  abstract OneClimb<T> initClimberInt();

  abstract OneClimb<T> initClimberDouble();
}
