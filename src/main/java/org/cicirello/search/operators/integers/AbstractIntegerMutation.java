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

package org.cicirello.search.operators.integers;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.IntegerValued;

/**
 * Internal abstract base class for mutation operators on int-valued representations.
 *
 * @param <T> The specific IntegerValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractIntegerMutation<T extends IntegerValued>
    implements MutationOperator<T>, IntegerValued {

  private int param;
  private final InternalMutator<T> m;
  private final EnhancedSplittableGenerator generator;

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   */
  AbstractIntegerMutation(int param, RandomizedIntegerBinaryOperator transformer) {
    this.param = param;
    m = new InternalTotalMutator<T>(transformer);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Constructs the mutation operator.
   *
   * @param param The parameter to the mutation operator, such as radius for a uniform, etc.
   * @param transformer The functional transformation of the mutation.
   * @param selector Chooses the indexes for a partial mutation.
   */
  AbstractIntegerMutation(
      int param, RandomizedIntegerBinaryOperator transformer, IndexSelector selector) {
    this.param = param;
    m = new InternalPartialMutator<T>(transformer, selector);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  AbstractIntegerMutation(AbstractIntegerMutation<T> other) {
    param = other.param;
    m = other.m;
    generator = other.generator.split();
  }

  @Override
  public void mutate(T c) {
    m.mutate(c, param, generator);
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
  public final int get(int i) {
    return param;
  }

  /**
   * Accesses the current value of the mutation parameter as an array. This method implemented
   * strictly to meet implementation requirements of IntegerValued interface.
   *
   * @param values An array to hold the result. If values is null or if values.length is not equal
   *     1, then a new array is constructed for the result.
   * @return An array containing the current value of the mutation parameter.
   */
  @Override
  public final int[] toArray(int[] values) {
    if (values == null || values.length != 1) values = new int[1];
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
  public final void set(int i, int value) {
    param = value;
  }

  /**
   * Sets the mutation parameter to a specified value.
   *
   * @param values The new value for the mutation parameter is in values[0], the rest is ignored.
   */
  @Override
  public final void set(int[] values) {
    param = values[0];
  }

  @Override
  public abstract AbstractIntegerMutation<T> split();

  @FunctionalInterface
  static interface RandomizedIntegerBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left the first operand
     * @param right the second operand
     * @param r the source of randomness
     */
    int applyAsInt(int left, int right, EnhancedSplittableGenerator r);
  }

  @FunctionalInterface
  static interface IndexSelector {

    /**
     * Applies this function to select indexes.
     *
     * @param length the length of the vector
     * @param r the source of randomness
     */
    int[] apply(int length, EnhancedSplittableGenerator r);
  }

  @FunctionalInterface
  private static interface InternalMutator<T1 extends IntegerValued> {

    /**
     * Mutates a int-valued candidate solution.
     *
     * @param c The int-valued candidate solution
     * @param param The mutation parameter
     * @param r the source of randomness
     */
    void mutate(T1 c, int param, EnhancedSplittableGenerator r);
  }

  private static class InternalTotalMutator<T1 extends IntegerValued>
      implements InternalMutator<T1> {

    private final RandomizedIntegerBinaryOperator mutator;

    private InternalTotalMutator(RandomizedIntegerBinaryOperator mutator) {
      this.mutator = mutator;
    }

    @Override
    public void mutate(T1 c, int param, EnhancedSplittableGenerator r) {
      final int n = c.length();
      for (int i = 0; i < n; i++) {
        c.set(i, mutator.applyAsInt(c.get(i), param, r));
      }
    }
  }

  private static class InternalPartialMutator<T1 extends IntegerValued>
      implements InternalMutator<T1> {

    private final RandomizedIntegerBinaryOperator mutator;
    private final IndexSelector selector;

    private InternalPartialMutator(
        RandomizedIntegerBinaryOperator mutator, IndexSelector selector) {
      this.mutator = mutator;
      this.selector = selector;
    }

    @Override
    public void mutate(T1 c, int param, EnhancedSplittableGenerator r) {
      int[] indexes = selector.apply(c.length(), r);
      for (int i : indexes) {
        c.set(i, mutator.applyAsInt(c.get(i), param, r));
      }
    }
  }
}
