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

package org.cicirello.search.operators.reals;

import java.util.function.DoubleBinaryOperator;
import java.util.function.IntFunction;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.representations.RealValued;

/**
 * Internal abstract base class for mutation operators on real-valued representations that support
 * the undo method.
 *
 * @param <T> The specific RealValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractUndoableRealMutation<T extends RealValued> extends AbstractRealMutation<T>
    implements UndoableMutationOperator<T> {

  private double[] previous;
  private double old;

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as sigma for a Gaussian, scale for a
   *     Cauchy, radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   */
  AbstractUndoableRealMutation(double param, DoubleBinaryOperator transformer) {
    super(param, transformer);
  }

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as sigma for a Gaussian, scale for a
   *     Cauchy, radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   * @param selector Chooses the indexes for a partial mutation.
   */
  AbstractUndoableRealMutation(
      double param, DoubleBinaryOperator transformer, IntFunction<int[]> selector) {
    super(param, transformer, selector);
  }

  AbstractUndoableRealMutation(AbstractUndoableRealMutation<T> other) {
    super(other);
  }

  @Override
  public final void mutate(T c) {
    if (c.length() > 1) previous = c.toArray(previous);
    else if (c.length() == 1) old = c.get(0);
    super.mutate(c);
  }

  @Override
  public final void undo(T c) {
    if (c.length() > 1) {
      c.set(previous);
    } else if (c.length() == 1) {
      c.set(0, old);
    }
  }

  @Override
  public abstract AbstractUndoableRealMutation<T> split();
}
