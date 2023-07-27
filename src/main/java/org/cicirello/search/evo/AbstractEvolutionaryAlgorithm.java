/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * Abstract base class for EA implementations.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractEvolutionaryAlgorithm<T extends Copyable<T>>
    implements ReoptimizableMetaheuristic<T> {

  private final Population<T> pop;
  private final Problem<T> problem;
  private final Generation<T> generation;
  private long numFitnessEvals;

  /*
   * Internal constructor for use by subclasses in same package.
   * Initializes the base class.
   */
  AbstractEvolutionaryAlgorithm(Population<T> pop, Problem<T> problem, Generation<T> generation) {
    this.pop = pop;
    this.problem = problem;
    this.generation = generation;
  }

  /*
   * Internal constructor for use by split method.
   * package private so subclasses in same package can use it for initialization for their own split methods.
   */
  AbstractEvolutionaryAlgorithm(AbstractEvolutionaryAlgorithm<T> other) {
    // Must be split
    pop = other.pop.split();
    generation = other.generation.split();

    // Threadsafe so just copy reference or values
    problem = other.problem;

    // Each instance must maintain its own count of evals.
    numFitnessEvals = 0;
  }

  /**
   * Runs the evolutionary algorithm beginning from a randomly generated population. If this method
   * is called multiple times, each call begins at a new randomly generated population.
   *
   * @param numGenerations The number of generations to run.
   * @return The best solution found during this set of generations, which may or may not be the
   *     same as the solution contained in the {@link ProgressTracker}, which contains the best
   *     across all calls to optimize as well as {@link #reoptimize}. Returns null if the run did
   *     not execute, such as if the ProgressTracker already contains the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int numGenerations) {
    if (pop.evolutionIsPaused()) return null;
    pop.init();
    pop.initOperators(numGenerations);
    numFitnessEvals = numFitnessEvals + pop.size();
    internalOptimize(numGenerations);
    return pop.getMostFit();
  }

  /**
   * Runs the evolutionary algorithm continuing from the final population from the most recent call
   * to either {@link #optimize} or {@link #reoptimize}, or from a random population if this is the
   * first call to either method.
   *
   * @param numGenerations The number of generations to run.
   * @return The best solution found during this set of generations, which may or may not be the
   *     same as the solution contained in the {@link ProgressTracker}, which contains the best
   *     across all calls to optimize as well as {@link #optimize}. Returns null if the run did not
   *     execute, such as if the ProgressTracker already contains the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> reoptimize(int numGenerations) {
    if (pop.evolutionIsPaused()) return null;
    pop.initOperators(numGenerations);
    internalOptimize(numGenerations);
    return pop.getMostFit();
  }

  @Override
  public final ProgressTracker<T> getProgressTracker() {
    return pop.getProgressTracker();
  }

  @Override
  public final void setProgressTracker(ProgressTracker<T> tracker) {
    pop.setProgressTracker(tracker);
  }

  @Override
  public final Problem<T> getProblem() {
    return problem;
  }

  /**
   * Gets the total run length in number of fitness evaluations. This is the total run length across
   * all calls to {@link #optimize} and {@link #reoptimize}. This may differ from what may be
   * expected based on run lengths. For example, the search terminates if it finds the theoretical
   * best solution, and also immediately returns if a prior call found the theoretical best. In such
   * cases, the total run length may be less than the requested run length.
   *
   * @return The total number of generations completed across all calls to {@link #optimize} and
   *     {@link #reoptimize}.
   */
  @Override
  public long getTotalRunLength() {
    return numFitnessEvals;
  }

  @Override
  public abstract AbstractEvolutionaryAlgorithm<T> split();

  private void internalOptimize(int numGenerations) {
    for (int i = 0; i < numGenerations && !pop.evolutionIsPaused(); i++) {
      numFitnessEvals = numFitnessEvals + generation.apply(pop);
    }
  }
}
