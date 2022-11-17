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

package org.cicirello.search.problems;

import org.cicirello.util.Copyable;

/**
 * This is a wrapper class for {@link IntegerCostOptimizationProblem} objects that enables scaling
 * all cost values by a positive constant. This transformation doesn't change what solution is
 * optimal, and doesn't change the topology of the search space. It simply scales the cost values.
 * For example, if you want to explore the effects of the range of the cost function on the behavior
 * of a search algorithm, you can use this class to scale the cost values of a problem whose cost
 * function range is known.
 *
 * <p>Note that this does not scale the results of the {@link #value} method, which will continue to
 * return the actual value of the candidate solution (see its documentation for details).
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.5.2021
 */
public final class IntegerCostFunctionScaler<T extends Copyable<T>>
    implements IntegerCostOptimizationProblem<T> {

  private final IntegerCostOptimizationProblem<T> problem;
  private final int scale;

  /**
   * Constructs the IntegerCostFunctionScaler.
   *
   * @param problem The original problem specification.
   * @param scale The scale factor, which must be positive. All cost values of the original problem
   *     will be multiplied by scale.
   * @throws IllegalArgumentException if scale &le; 0.
   */
  public IntegerCostFunctionScaler(IntegerCostOptimizationProblem<T> problem, int scale) {
    if (scale <= 0) throw new IllegalArgumentException("scale must be positive");
    this.scale = scale;
    this.problem = problem;
  }

  /**
   * {@inheritDoc}
   *
   * <p>In the case of the IntegerCostFunctionScaler, the cost values are all multiplied by the
   * scale factor.
   */
  @Override
  public int cost(T candidate) {
    return scale * problem.cost(candidate);
  }

  /**
   * {@inheritDoc}
   *
   * <p>In the case of the IntegerCostFunctionScaler, the cost values are all multiplied by the
   * scale factor.
   */
  @Override
  public int minCost() {
    return scale * problem.minCost();
  }

  @Override
  public int value(T candidate) {
    return problem.value(candidate);
  }
}
