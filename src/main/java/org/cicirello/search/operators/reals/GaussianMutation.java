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

package org.cicirello.search.operators.reals;

import org.cicirello.search.representations.RealValued;
import org.cicirello.util.Copyable;

/**
 * This class implements Gaussian mutation. Gaussian mutation is for mutating floating-point values.
 * This class can be used to mutate objects of any of the classes that implement the {@link
 * RealValued} interface, including both univariate and multivariate function input objects.
 *
 * <p>In a Gaussian mutation, a value v is mutated by adding a randomly generated m such that m is
 * drawn from a Gaussian distribution with mean 0 and standard deviation sigma. It is commonly
 * employed in Evolution Strategies when mutating real valued parameters.
 *
 * <p>This mutation operator also implements the {@link RealValued} interface to enable
 * implementation of metaheuristics that mutate their own mutation parameters. That is, you can pass
 * a GaussianMutation object to the {@link #mutate} method of a GaussianMutation object.
 *
 * <p>To construct a GaussianMutation, you must use one of the factory methods. See the various
 * {@link #createGaussianMutation} methods.
 *
 * <p>Gaussian mutation was introduced in the following article:<br>
 * Hinterding, R. 1995. Gaussian mutation and self-adaption for numeric genetic algorithms. In IEEE
 * CEC. IEEE Press. 384–389.
 *
 * @param <T> The specific RealValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class GaussianMutation<T extends RealValued> extends AbstractRealMutation<T>
    implements Copyable<GaussianMutation<T>> {

  /*
   * Internal constructor.  Constructs a Gaussian mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param sigma The standard deviation of the Gaussian.
   *
   * @param transformer The functional transformation of the mutation.
   */
  GaussianMutation(double sigma, RandomizedDoubleBinaryOperator transformer) {
    super(sigma, transformer);
  }

  /*
   * Internal constructor.  Constructs a Gaussian mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param sigma The standard deviation of the Gaussian.
   *
   * @param transformer The functional transformation of the mutation.
   *
   * @param selector Chooses the indexes for a partial mutation.
   */
  GaussianMutation(
      double sigma, RandomizedDoubleBinaryOperator transformer, IndexSelector selector) {
    super(sigma, transformer, selector);
  }

  /*
   * internal copy constructor
   */
  GaussianMutation(GaussianMutation<T> other) {
    super(other);
  }

  /**
   * Creates a Gaussian mutation operator with standard deviation equal to 1.
   *
   * @param <T> The specific RealValued type.
   * @return A Gaussian mutation operator.
   */
  public static <T extends RealValued> GaussianMutation<T> createGaussianMutation() {
    return createGaussianMutation(1.0);
  }

  /**
   * Creates a Gaussian mutation operator.
   *
   * @param sigma The standard deviation of the Gaussian.
   * @param <T> The specific RealValued type.
   * @return A Gaussian mutation operator.
   */
  public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(double sigma) {
    return new GaussianMutation<T>(sigma, (old, param, r) -> old + r.nextGaussian(param));
  }

  /**
   * Creates a Gaussian mutation operator, such that the mutate method constrains each mutated real
   * value to lie in the interval [lowerBound, upperBound].
   *
   * @param sigma The standard deviation of the Gaussian.
   * @param lowerBound A lower bound on the result of a mutation.
   * @param upperBound An upper bound on the result of a mutation.
   * @param <T> The specific RealValued type.
   * @return A Gaussian mutation operator.
   */
  public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(
      double sigma, double lowerBound, double upperBound) {
    if (upperBound < lowerBound)
      throw new IllegalArgumentException("upperBound must be at least lowerBound");
    return new GaussianMutation<T>(
        sigma,
        (old, param, r) -> {
          double mutated = old + r.nextGaussian(param);
          if (mutated <= lowerBound) return lowerBound;
          if (mutated >= upperBound) return upperBound;
          return mutated;
        });
  }

  /**
   * Create a Gaussian mutation operator.
   *
   * @param sigma The standard deviation of the Gaussian mutation.
   * @param k The number of input variables that the {@link #mutate} method changes when called. The
   *     k input variables are chosen uniformly at random from among all subsets of size k. If there
   *     are less than k input variables, then all are mutated.
   * @param <T> The specific RealValued type.
   * @return A Gaussian mutation operator
   * @throws IllegalArgumentException if k &lt; 1
   */
  public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(
      double sigma, int k) {
    if (k < 1) throw new IllegalArgumentException("k must be at least 1");
    return new GaussianMutation<T>(
        sigma,
        (old, param, r) -> old + r.nextGaussian(param),
        (n, r) -> r.sample(n, k < n ? k : n, (int[]) null));
  }

  /**
   * Create a Gaussian mutation operator.
   *
   * @param sigma The standard deviation of the Gaussian mutation.
   * @param p The probability that the {@link #mutate} method changes an input variable. If there
   *     are n input variables, then n*p input variables will be mutated on average during a single
   *     call to the {@link #mutate} method.
   * @param <T> The specific RealValued type.
   * @return A Gaussian mutation operator
   * @throws IllegalArgumentException if p &le; 0
   */
  public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(
      double sigma, double p) {
    if (p <= 0) throw new IllegalArgumentException("p must be positive");
    if (p >= 1) {
      return createGaussianMutation(sigma);
    }
    return new GaussianMutation<T>(
        sigma, (old, param, r) -> old + r.nextGaussian(param), (n, r) -> r.sample(n, p));
  }

  @Override
  public GaussianMutation<T> split() {
    return new GaussianMutation<T>(this);
  }

  /**
   * Creates an identical copy of this object.
   *
   * @return an identical copy of this object
   */
  @Override
  public GaussianMutation<T> copy() {
    return new GaussianMutation<T>(this);
  }
}
