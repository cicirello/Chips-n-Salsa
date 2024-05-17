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

package org.cicirello.search.ss;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SimpleMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.internal.ReferenceValidator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class generates solutions to optimization problems using a constructive heuristic. Unless
 * the heuristic given to it is randomized, this class is completely deterministic and has no
 * randomized behavior. Thus, executing the {@link #optimize} method multiple times should produce
 * the same result each time. When using a constructive heuristic, you begin with an empty solution,
 * (e.g., an empty permutation for a permutation optimization problem), and you then use a
 * constructive heuristic to choose which element to add to the partial solution. This is repeated
 * until you derive a complete solution.
 *
 * @param <T> The problem representation we are optimizing
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class HeuristicSolutionGenerator<T extends Copyable<T>> implements SimpleMetaheuristic<T> {

  private final OptimizationProblem<T> pOpt;
  private final IntegerCostOptimizationProblem<T> pOptInt;
  private final ConstructiveHeuristic<T> heuristic;
  private ProgressTracker<T> tracker;
  private int numGenerated;

  /**
   * Package-private constructor: use factory methods to create an instance. Constructs an
   * HeuristicSolutionGenerator for generating solutions to an optimization problem using a
   * constructive heuristic.
   *
   * @param heuristic The constructive heuristic.
   * @param tracker A ProgressTracker
   * @throws NullPointerException if heuristic or tracker is null
   */
  HeuristicSolutionGenerator(ConstructiveHeuristic<T> heuristic, ProgressTracker<T> tracker) {
    this.tracker = tracker;
    this.heuristic = heuristic;
    // default: numGenerated = 0;
    Problem<T> problem = heuristic.getProblem();
    if (heuristic.getProblem() instanceof IntegerCostOptimizationProblem) {
      pOptInt = (IntegerCostOptimizationProblem<T>) problem;
      pOpt = null;
    } else {
      pOpt = (OptimizationProblem<T>) problem;
      pOptInt = null;
    }
  }

  /**
   * Creates a HeuristicSolutionGenerator for generating solutions to an optimization problem using
   * a constructive heuristic. A ProgressTracker is created for you.
   *
   * @param heuristic The constructive heuristic.
   * @param <T> The problem representation we are optimizing
   * @return the HeuristicSolutionGenerator
   * @throws NullPointerException if heuristic is null
   */
  public static <T extends Copyable<T>>
      HeuristicSolutionGenerator<T> createHeuristicSolutionGenerator(
          ConstructiveHeuristic<T> heuristic) {
    ReferenceValidator.nullCheck(heuristic);
    return new HeuristicSolutionGenerator<T>(heuristic, new ProgressTracker<T>());
  }

  /**
   * Creates a HeuristicSolutionGenerator for generating solutions to an optimization problem using
   * a constructive heuristic.
   *
   * @param heuristic The constructive heuristic.
   * @param tracker A ProgressTracker
   * @param <T> The problem representation we are optimizing
   * @return the HeuristicSolutionGenerator
   * @throws NullPointerException if heuristic or tracker is null
   */
  public static <T extends Copyable<T>>
      HeuristicSolutionGenerator<T> createHeuristicSolutionGenerator(
          ConstructiveHeuristic<T> heuristic, ProgressTracker<T> tracker) {
    ReferenceValidator.nullCheck(heuristic);
    ReferenceValidator.nullCheck(tracker);
    return new HeuristicSolutionGenerator<T>(heuristic, tracker);
  }

  /*
   * package-private for use by split method, and subclasses in same package
   */
  HeuristicSolutionGenerator(HeuristicSolutionGenerator<T> other) {
    heuristic = other.heuristic.split();

    // these are threadsafe, so just copy references
    pOpt = other.pOpt;
    pOptInt = other.pOptInt;

    // this one must be shared.
    tracker = other.tracker;

    // default: numGenerated = 0;
  }

  @Override
  public final SolutionCostPair<T> optimize() {
    if (tracker.isStopped() || tracker.didFindBest()) {
      return null;
    }
    numGenerated++;
    return generate();
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
  public final long getTotalRunLength() {
    return numGenerated;
  }

  @Override
  public final Problem<T> getProblem() {
    return (pOptInt != null) ? pOptInt : pOpt;
  }

  @Override
  public HeuristicSolutionGenerator<T> split() {
    return new HeuristicSolutionGenerator<T>(this);
  }

  private SolutionCostPair<T> evaluateAndPackageSolution(T complete) {
    if (pOptInt != null) {
      SolutionCostPair<T> solution = pOptInt.getSolutionCostPair(complete);
      int cost = solution.getCost();
      if (cost < tracker.getCost()) {
        tracker.update(cost, complete, pOptInt.isMinCost(cost));
      }
      return solution;
    } else {
      SolutionCostPair<T> solution = pOpt.getSolutionCostPair(complete);
      double cost = solution.getCostDouble();
      if (cost < tracker.getCostDouble()) {
        tracker.update(cost, complete, pOpt.isMinCost(cost));
      }
      return solution;
    }
  }

  private SolutionCostPair<T> generate() {
    IncrementalEvaluation<T> incEval = heuristic.createIncrementalEvaluation();
    int n = heuristic.completeLength();
    Partial<T> p = heuristic.createPartial(n);
    while (!p.isComplete()) {
      int k = p.numExtensions();
      if (k == 1) {
        if (incEval != null) {
          incEval.extend(p, p.getExtension(0));
        }
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
        if (incEval != null) {
          incEval.extend(p, p.getExtension(which));
        }
        p.extend(which);
      }
    }
    T complete = p.toComplete();
    return evaluateAndPackageSolution(complete);
  }
}
