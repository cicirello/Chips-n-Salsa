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

import org.cicirello.search.representations.IntegerValued;
import org.cicirello.util.Copyable;

/**
 * This class implements a uniform mutation with support for the {@link #undo} method. This uniform
 * mutation is for mutating integer values. This class can be used to mutate objects of any of the
 * classes that implement the {@link IntegerValued} interface, including both univariate and
 * multivariate objects.
 *
 * <p>In the form of uniform mutation implemented by this class, a value v is mutated by adding a
 * randomly generated m such that m is drawn uniformly at random from the interval [-radius,
 * radius].
 *
 * <p>This mutation operator also implements the {@link IntegerValued} interface to enable
 * implementation of metaheuristics that mutate their own mutation parameters. That is, you can pass
 * an UndoableUniformMutation object to the {@link #mutate} method of a UndoableUniformMutation
 * object.
 *
 * <p>To construct an UndoableUniformMutation, you must use one of the factory methods. See the
 * various {@link #createUniformMutation} methods.
 *
 * @param <T> The specific IntegerValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class UndoableUniformMutation<T extends IntegerValued>
    extends AbstractUndoableIntegerMutation<T> implements Copyable<UndoableUniformMutation<T>> {

  /*
   * Internal constructor.  Constructs a Uniform mutation operator supporting the undo operation.
   * Otherwise, must use the factory methods.
   *
   * @param radius The radius parameter of the Uniform.
   *
   * @param transformer The functional transformation of the mutation.
   */
  UndoableUniformMutation(int radius, RandomizedIntegerBinaryOperator transformer) {
    super(radius, transformer);
  }

  /*
   * Internal constructor.  Constructs a Uniform mutation operator supporting the undo operation.
   * Otherwise, must use the factory methods.
   *
   * @param radius The radius parameter of the Uniform.
   *
   * @param transformer The functional transformation of the mutation.
   *
   * @param selector Chooses the indexes for a partial mutation.
   */
  UndoableUniformMutation(
      int radius, RandomizedIntegerBinaryOperator transformer, IndexSelector selector) {
    super(radius, transformer, selector);
  }

  /*
   * internal copy constructor: not a true copy... doesn't copy state related to undo method
   */
  UndoableUniformMutation(UndoableUniformMutation<T> other) {
    super(other);
  }

  /**
   * Creates a Uniform mutation operator with radius parameter equal to 1 that supports the undo
   * operation.
   *
   * @param <T> The specific IntegerValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends IntegerValued> UndoableUniformMutation<T> createUniformMutation() {
    return createUniformMutation(1);
  }

  /**
   * Creates a Uniform mutation operator that supports the undo operation.
   *
   * @param radius The radius parameter of the Uniform.
   * @param <T> The specific IntegerValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends IntegerValued> UndoableUniformMutation<T> createUniformMutation(
      int radius) {
    return new UndoableUniformMutation<T>(
        radius, (old, param, r) -> old + r.nextInt(-param, param + 1));
  }

  /**
   * Creates a Uniform mutation operator, such that the mutate method constrains each mutated int
   * value to lie in the interval [lowerBound, upperBound].
   *
   * @param radius The radius parameter of the Uniform.
   * @param lowerBound A lower bound on the result of a mutation.
   * @param upperBound An upper bound on the result of a mutation.
   * @param <T> The specific IntegerValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends IntegerValued> UndoableUniformMutation<T> createUniformMutation(
      int radius, int lowerBound, int upperBound) {
    if (upperBound < lowerBound)
      throw new IllegalArgumentException("upperBound must be at least lowerBound");
    return new UndoableUniformMutation<T>(
        radius,
        (old, param, r) -> {
          int mutated = old + r.nextInt(-param, param + 1);
          if (mutated <= lowerBound) return lowerBound;
          if (mutated >= upperBound) return upperBound;
          return mutated;
        });
  }

  /**
   * Create a Uniform mutation operator that supports the undo operation.
   *
   * @param radius The radius parameter of the Uniform mutation.
   * @param k The number of input variables that the {@link #mutate} method changes when called. The
   *     k input variables are chosen uniformly at random from among all subsets of size k. If there
   *     are less than k input variables, then all are mutated.
   * @param <T> The specific IntegerValued type.
   * @return A Uniform mutation operator
   * @throws IllegalArgumentException if k &lt; 1
   */
  public static <T extends IntegerValued> UndoableUniformMutation<T> createUniformMutation(
      int radius, int k) {
    if (k < 1) throw new IllegalArgumentException("k must be at least 1");
    return new UndoableUniformMutation<T>(
        radius,
        (old, param, r) -> old + r.nextInt(-param, param + 1),
        (n, r) -> r.sample(n, k < n ? k : n, (int[]) null));
  }

  /**
   * Create a Uniform mutation operator that supports the undo operation.
   *
   * @param radius The radius parameter of the Uniform mutation.
   * @param p The probability that the {@link #mutate} method changes an input variable. If there
   *     are n input variables, then n*p input variables will be mutated on average during a single
   *     call to the {@link #mutate} method.
   * @param <T> The specific IntegerValued type.
   * @return A Uniform mutation operator
   * @throws IllegalArgumentException if p &le; 0
   */
  public static <T extends IntegerValued> UndoableUniformMutation<T> createUniformMutation(
      int radius, double p) {
    if (p <= 0) throw new IllegalArgumentException("p must be positive");
    if (p >= 1) {
      return createUniformMutation(radius);
    }
    return new UndoableUniformMutation<T>(
        radius, (old, param, r) -> old + r.nextInt(-param, param + 1), (n, r) -> r.sample(n, p));
  }

  @Override
  public UndoableUniformMutation<T> split() {
    return new UndoableUniformMutation<T>(this);
  }

  /**
   * Creates an identical copy of this object.
   *
   * @return an identical copy of this object
   */
  @Override
  public UndoableUniformMutation<T> copy() {
    return new UndoableUniformMutation<T>(this);
  }
}
