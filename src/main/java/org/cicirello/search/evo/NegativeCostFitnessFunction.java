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

import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.util.Copyable;

/**
 * This class provides a convenient mechanism for transforming optimization cost values to fitness
 * values. Most of the algorithms in the library require a cost function to minimize through a class
 * that implements either {@link OptimizationProblem} or {@link
 * org.cicirello.search.problems.IntegerCostOptimizationProblem IntegerCostOptimizationProblem}.
 * However, the evolutionary algorithms in the library require a fitness function such that higher
 * fitness implies better solution. Furthermore, some selection operators further assume that
 * fitness values are positive, such as {@link FitnessProportionalSelection} and {@link
 * StochasticUniversalSampling}.
 *
 * <p>This class transforms the cost of solution s to fitness with the following transformation:
 * fitness(s) = -cost(s), where cost(s) refers to the {@link OptimizationProblem#cost} method.
 *
 * <p>Because this transformation produces negative fitness values, it is not compatible with all
 * selection operators. However, many of the selection operators in the library work even with
 * negative fitness values, such as any selection operator that uses only relative fitness values.
 * For example, {@link TournamentSelection} and other selection operators that only care if one
 * fitness is higher or lower than another will work fine with negative fitness values.
 *
 * <p><b>Incompatible Selection Operators:</b> The library does include some selection operators
 * that require positive fitness values. Thus, the NegativeIntegerCostFitnessFunction is
 * incompatible with such selection operators, which include the following: {@link
 * FitnessProportionalSelection}, {@link StochasticUniversalSampling}, {@link
 * BiasedFitnessProportionalSelection}, and {@link BiasedStochasticUniversalSampling}. The behavior
 * of these selection operators that select population members with probabilities that are
 * proportional to their fitness is undefined for negative fitnesses.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class NegativeCostFitnessFunction<T extends Copyable<T>>
    implements FitnessFunction.Double<T> {

  private final OptimizationProblem<T> problem;

  /**
   * Constructs a fitness function that transforms the cost of solution s to fitness with the
   * following transformation: fitness(s) = -cost(s).
   *
   * @param problem The optimization problem.
   */
  public NegativeCostFitnessFunction(OptimizationProblem<T> problem) {
    this.problem = problem;
  }

  @Override
  public double fitness(T candidate) {
    return -problem.cost(candidate);
  }

  @Override
  public OptimizationProblem<T> getProblem() {
    return problem;
  }
}
