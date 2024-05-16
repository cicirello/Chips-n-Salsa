/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

package org.cicirello.search.evo;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SingleSolutionMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.internal.ReferenceValidator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class implements a (1+1)-EA. In a (1+1)-EA, the evolutionary algorithm has a population size
 * of 1, in each cycle of the algorithm a single mutant is created from that single population
 * member, forming a population of size 2, and finally the EA keeps the better of the two solutions.
 * This is perhaps the simplest case of an EA. This class supports optimizing arbitrary structures,
 * specified by the generic type parameter.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class OnePlusOneEvolutionaryAlgorithm<T extends Copyable<T>>
    implements SingleSolutionMetaheuristic<T> {

  private final IntegerCostOptimizationProblem<T> pOptInt;
  private final OptimizationProblem<T> pOpt;
  private final Initializer<T> initializer;
  private final UndoableMutationOperator<T> mutation;
  private int elapsedEvals;
  private ProgressTracker<T> tracker;
  private final SingleRun<T> sr;

  /**
   * Creates a OnePlusOneEvolutionaryAlgorithm instance for real-valued optimization problems. A
   * {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states.
   * @throws NullPointerException if any of the parameters are null
   */
  public OnePlusOneEvolutionaryAlgorithm(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    this(problem, mutation, initializer, new ProgressTracker<T>());
  }

  /**
   * Creates a OnePlusOneEvolutionaryAlgorithm instance for integer-valued optimization problems. A
   * {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states.
   * @throws NullPointerException if any of the parameters are null
   */
  public OnePlusOneEvolutionaryAlgorithm(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    this(problem, mutation, initializer, new ProgressTracker<T>());
  }

  /**
   * Creates a OnePlusOneEvolutionaryAlgorithm instance for real-valued optimization problems.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null
   */
  public OnePlusOneEvolutionaryAlgorithm(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    ReferenceValidator.nullCheck(problem);
    ReferenceValidator.nullCheck(mutation);
    ReferenceValidator.nullCheck(initializer);
    ReferenceValidator.nullCheck(tracker);
    this.initializer = initializer;
    this.mutation = mutation;
    this.tracker = tracker;
    pOpt = problem;
    pOptInt = null;
    // default on purpose: elapsedEvals = 0;
    sr = new DoubleCostSingleRun();
  }

  /**
   * Creates a OnePlusOneEvolutionaryAlgorithm instance for integer-valued optimization problems.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null
   */
  public OnePlusOneEvolutionaryAlgorithm(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    ReferenceValidator.nullCheck(problem);
    ReferenceValidator.nullCheck(mutation);
    ReferenceValidator.nullCheck(initializer);
    ReferenceValidator.nullCheck(tracker);
    this.initializer = initializer;
    this.mutation = mutation;
    this.tracker = tracker;
    pOptInt = problem;
    pOpt = null;
    // default on purpose: elapsedEvals = 0;
    sr = new IntCostSingleRun();
  }

  /*
   * package-private copy constructor in support of the split method, and so subclass can also use.
   * note: copies references to thread-safe components, and splits potentially non-threadsafe components
   */
  OnePlusOneEvolutionaryAlgorithm(OnePlusOneEvolutionaryAlgorithm<T> other) {
    // these are threadsafe, so just copy references
    pOpt = other.pOpt;
    pOptInt = other.pOptInt;

    // this one must be shared.
    tracker = other.tracker;

    // split these: not threadsafe
    initializer = other.initializer.split();
    mutation = other.mutation.split();

    sr = pOptInt != null ? new IntCostSingleRun() : new DoubleCostSingleRun();
  }

  @SuppressWarnings("deprecation")
  @Override
  protected final void finalize() {
    // Prevents potential finalizer vulnerability from exceptions thrown from constructors.
    // See:
    // https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions
  }

  /**
   * Continues optimizing starting from the previous best found solution contained in the tracker
   * object, rather than from a random one. If no prior run had been performed, then this method
   * starts the run from a randomly generated solution.
   *
   * @param maxEvals The maximum number of evaluations (i.e., iterations) to execute.
   * @return the current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this instance's {@link ProgressTracker}, which contains the best of all runs. Returns null
   *     if the run did not execute, such as if the ProgressTracker already contains the theoretical
   *     best solution.
   */
  @Override
  public final SolutionCostPair<T> reoptimize(int maxEvals) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    T start = tracker.getSolution();
    if (start == null) start = initializer.createCandidateSolution();
    else start = start.copy();
    return sr.optimizeSingleRun(maxEvals, start);
  }

  /**
   * Runs the EA beginning at a random initial solution.
   *
   * @param maxEvals The maximum number of evaluations (i.e., iterations) to execute.
   * @return the current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this instance's {@link ProgressTracker}, which contains the best of all runs. Returns null
   *     if the run did not execute, such as if the ProgressTracker already contains the theoretical
   *     best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int maxEvals) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    return sr.optimizeSingleRun(maxEvals, initializer.createCandidateSolution());
  }

  /**
   * Runs the EA beginning at a specified initial solution.
   *
   * @param maxEvals The maximum number of evaluations (i.e., iterations) to execute.
   * @param start The desired starting solution.
   * @return the current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this instance's {@link ProgressTracker}, which contains the best of all runs. Returns null
   *     if the run did not execute, such as if the ProgressTracker already contains the theoretical
   *     best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int maxEvals, T start) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    return sr.optimizeSingleRun(maxEvals, start.copy());
  }

  @Override
  public final Problem<T> getProblem() {
    return (pOptInt != null) ? pOptInt : pOpt;
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
  public OnePlusOneEvolutionaryAlgorithm<T> split() {
    return new OnePlusOneEvolutionaryAlgorithm<T>(this);
  }

  /**
   * Gets the total number of evaluations (iterations) performed by this EA object. This is the
   * total number of such evaluations across all calls to the optimize and reoptimize methods. This
   * may differ from the combined number of maxEvals passed as a parameter to those methods. For
   * example, those methods terminate if they find the theoretical best solution, and also
   * immediately return if a prior call found the theoretical best. In such cases, the total run
   * length may be less than the requested maxEvals.
   *
   * @return the total number of evaluations
   */
  @Override
  public long getTotalRunLength() {
    return elapsedEvals;
  }

  private interface SingleRun<T extends Copyable<T>> {
    SolutionCostPair<T> optimizeSingleRun(int maxEvals, T current);
  }

  private class IntCostSingleRun implements SingleRun<T> {

    @Override
    public final SolutionCostPair<T> optimizeSingleRun(int maxEvals, T current) {
      // compute cost of start
      int currentCost = pOptInt.cost(current);

      // initialize best cost, etc
      int bestCost = tracker.getCost();
      if (currentCost < bestCost) {
        boolean isMinCost = pOptInt.isMinCost(currentCost);
        bestCost = tracker.update(currentCost, current, isMinCost);
        if (tracker.didFindBest()) {
          // found theoretical best so no point in proceeding
          return new SolutionCostPair<T>(current, currentCost, isMinCost);
        }
      }

      // main EA loop
      for (int i = 1; i <= maxEvals; i++) {
        if (tracker.isStopped()) {
          // some other thread signaled to stop
          elapsedEvals += (i - 1);
          return new SolutionCostPair<T>(current, currentCost, pOptInt.isMinCost(currentCost));
        }
        mutation.mutate(current);
        int neighborCost = pOptInt.cost(current);
        if (neighborCost <= currentCost) {
          // switching to better solution
          currentCost = neighborCost;
          if (currentCost < bestCost) {
            boolean isMinCost = pOptInt.isMinCost(currentCost);
            bestCost = tracker.update(currentCost, current, isMinCost);
            if (tracker.didFindBest()) {
              // found theoretical best so no point in proceeding
              elapsedEvals += i;
              return new SolutionCostPair<T>(current, currentCost, isMinCost);
            }
          }
        } else {
          // reject the mutant and revert back to previous state
          mutation.undo(current);
        }
      }
      elapsedEvals += maxEvals;
      return new SolutionCostPair<T>(current, currentCost, pOptInt.isMinCost(currentCost));
    }
  }

  private class DoubleCostSingleRun implements SingleRun<T> {

    @Override
    public final SolutionCostPair<T> optimizeSingleRun(int maxEvals, T current) {
      // compute cost of start
      double currentCost = pOpt.cost(current);

      // initialize best cost, etc
      double bestCost = tracker.getCostDouble();
      if (currentCost < bestCost) {
        boolean isMinCost = pOpt.isMinCost(currentCost);
        bestCost = tracker.update(currentCost, current, isMinCost);
        if (tracker.didFindBest()) {
          // found theoretical best so no point in proceeding
          return new SolutionCostPair<T>(current, currentCost, isMinCost);
        }
      }

      // main EA loop
      for (int i = 1; i <= maxEvals; i++) {
        if (tracker.isStopped()) {
          // some other thread signaled to stop
          elapsedEvals += (i - 1);
          return new SolutionCostPair<T>(current, currentCost, pOpt.isMinCost(currentCost));
        }
        mutation.mutate(current);
        double neighborCost = pOpt.cost(current);
        if (neighborCost <= currentCost) {
          // accepting the mutant
          currentCost = neighborCost;
          if (currentCost < bestCost) {
            boolean isMinCost = pOpt.isMinCost(currentCost);
            bestCost = tracker.update(currentCost, current, isMinCost);
            if (tracker.didFindBest()) {
              // found theoretical best so no point in proceeding
              elapsedEvals += i;
              return new SolutionCostPair<T>(current, currentCost, isMinCost);
            }
          }
        } else {
          // reject the mutant and revert back to previous state
          mutation.undo(current);
        }
      }
      elapsedEvals += maxEvals;
      return new SolutionCostPair<T>(current, currentCost, pOpt.isMinCost(currentCost));
    }
  }
}
