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
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.RealValued;

/**
 * Internal abstract base class for mutation operators on real-valued representations.
 *
 * @param <T> The specific RealValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractRealMutation<T extends RealValued>
    implements MutationOperator<T>, RealValued {

  private double param;
  private final InternalMutator<T> m;

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as sigma for a Gaussian, scale for a
   *     Cauchy, radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   */
  AbstractRealMutation(double param, DoubleBinaryOperator transformer) {
    this.param = param;
    m = new InternalTotalMutator<T>(transformer);
  }

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as sigma for a Gaussian, scale for a
   *     Cauchy, radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   * @param selector Chooses the indexes for a partial mutation.
   */
  AbstractRealMutation(
      double param, DoubleBinaryOperator transformer, IntFunction<int[]> selector) {
    this.param = param;
    m = new InternalPartialMutator<T>(transformer, selector);
  }

  AbstractRealMutation(AbstractRealMutation<T> other) {
    param = other.param;
    m = other.m;
  }

  @Override
  public void mutate(T c) {
    m.mutate(c, param);
  }

  @Override
  public final int length() {
    return 1;
  }

  /**
   * Accesses the current value of the mutation parameter.
   *
   * @param i Ignored.
   * @return The current value of the mutation parameter.
   */
  @Override
  public final double get(int i) {
    return param;
  }

  /**
   * Accesses the current value of the mutation parameter as an array. This method implemented
   * strictly to meet implementation requirements of RealValued interface.
   *
   * @param values An array to hold the result. If values is null or if values.length is not equal
   *     1, then a new array is constructed for the result.
   * @return An array containing the current value of the mutation parameter.
   */
  @Override
  public final double[] toArray(double[] values) {
    if (values == null || values.length != 1) values = new double[1];
    values[0] = param;
    return values;
  }

  /**
   * Sets the mutation parameter to a specified value.
   *
   * @param i Ignored.
   * @param value The new value for the mutation parameter.
   */
  @Override
  public final void set(int i, double value) {
    param = value;
  }

  /**
   * Sets the mutation parameter to a specified value.
   *
   * @param values The new value for the mutation parameter is in values[0], the rest is ignored.
   */
  @Override
  public final void set(double[] values) {
    param = values[0];
  }

  @Override
  public abstract AbstractRealMutation<T> split();

  @FunctionalInterface
  private static interface InternalMutator<T1 extends RealValued> {

    /**
     * Mutates a real-valued candidate solution.
     *
     * @param c The real-valued candidate solution
     * @param param The mutation parameter
     */
    void mutate(T1 c, double param);
  }

  private static class InternalTotalMutator<T1 extends RealValued> implements InternalMutator<T1> {

    private final DoubleBinaryOperator mutator;

    private InternalTotalMutator(DoubleBinaryOperator mutator) {
      this.mutator = mutator;
    }

    @Override
    public void mutate(T1 c, double param) {
      final int n = c.length();
      for (int i = 0; i < n; i++) {
        c.set(i, mutator.applyAsDouble(c.get(i), param));
      }
    }
  }

  private static class InternalPartialMutator<T1 extends RealValued>
      implements InternalMutator<T1> {

    private final DoubleBinaryOperator mutator;
    private final IntFunction<int[]> selector;

    private InternalPartialMutator(DoubleBinaryOperator mutator, IntFunction<int[]> selector) {
      this.mutator = mutator;
      this.selector = selector;
    }

    @Override
    public void mutate(T1 c, double param) {
      int[] indexes = selector.apply(c.length());
      for (int i : indexes) {
        c.set(i, mutator.applyAsDouble(c.get(i), param));
      }
    }
  }
}
