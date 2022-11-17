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

import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.math.rand.RandomSampler;
import org.cicirello.search.representations.RealValued;
import org.cicirello.util.Copyable;

/**
 * This class implements a uniform mutation. Uniform mutation is for mutating floating-point values.
 * This class can be used to mutate objects of any of the classes that implement the {@link
 * RealValued} interface, including both univariate and multivariate function input objects.
 *
 * <p>In the form of uniform mutation implemented by this class, a value v is mutated by adding a
 * randomly generated m such that m is drawn uniformly at random from the interval [-radius,
 * radius].
 *
 * <p>This mutation operator also implements the {@link RealValued} interface to enable
 * implementation of metaheuristics that mutate their own mutation parameters. That is, you can pass
 * a UniformMutation object to the {@link #mutate} method of a UniformMutation object.
 *
 * <p>To construct a UniformMutation, you must use one of the factory methods. See the various
 * {@link #createUniformMutation} methods.
 *
 * @param <T> The specific RealValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class UniformMutation<T extends RealValued> extends AbstractRealMutation<T>
    implements Copyable<UniformMutation<T>> {

  /*
   * Internal constructor.  Constructs a Uniform mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param radius The radius parameter of the Uniform.
   *
   * @param transformer The functional transformation of the mutation.
   */
  UniformMutation(double radius, Transformation transformer) {
    super(radius, transformer);
  }

  /*
   * Internal constructor.  Constructs a Uniform mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param radius The radius parameter of the Uniform.
   *
   * @param transformer The functional transformation of the mutation.
   *
   * @param selector Chooses the indexes for a partial mutation.
   */
  UniformMutation(double radius, Transformation transformer, Selector selector) {
    super(radius, transformer, selector);
  }

  /*
   * internal copy constructor
   */
  UniformMutation(UniformMutation<T> other) {
    super(other);
  }

  /**
   * Creates a Uniform mutation operator with radius parameter equal to 1.
   *
   * @param <T> The specific RealValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends RealValued> UniformMutation<T> createUniformMutation() {
    return createUniformMutation(1.0);
  }

  /**
   * Creates a Uniform mutation operator.
   *
   * @param radius The radius parameter of the Uniform.
   * @param <T> The specific RealValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends RealValued> UniformMutation<T> createUniformMutation(double radius) {
    return new UniformMutation<T>(
        radius, (old, param) -> old + ThreadLocalRandom.current().nextDouble(-param, param));
  }

  /**
   * Creates a Uniform mutation operator, such that the mutate method constrains each mutated real
   * value to lie in the interval [lowerBound, upperBound].
   *
   * @param radius The radius parameter of the Uniform.
   * @param lowerBound A lower bound on the result of a mutation.
   * @param upperBound An upper bound on the result of a mutation.
   * @param <T> The specific RealValued type.
   * @return A Uniform mutation operator.
   */
  public static <T extends RealValued> UniformMutation<T> createUniformMutation(
      double radius, double lowerBound, double upperBound) {
    if (upperBound < lowerBound)
      throw new IllegalArgumentException("upperBound must be at least lowerBound");
    return new UniformMutation<T>(
        radius,
        (old, param) -> {
          double mutated = old + ThreadLocalRandom.current().nextDouble(-param, param);
          if (mutated <= lowerBound) return lowerBound;
          if (mutated >= upperBound) return upperBound;
          return mutated;
        });
  }

  /**
   * Create a Uniform mutation operator.
   *
   * @param radius The radius parameter of the Uniform mutation.
   * @param k The number of input variables that the {@link #mutate} method changes when called. The
   *     k input variables are chosen uniformly at random from among all subsets of size k. If there
   *     are less than k input variables, then all are mutated.
   * @param <T> The specific RealValued type.
   * @return A Uniform mutation operator
   * @throws IllegalArgumentException if k &lt; 1
   */
  public static <T extends RealValued> UniformMutation<T> createUniformMutation(
      double radius, int k) {
    if (k < 1) throw new IllegalArgumentException("k must be at least 1");
    return new UniformMutation<T>(
        radius,
        (old, param) -> old + ThreadLocalRandom.current().nextDouble(-param, param),
        n -> RandomSampler.sample(n, k < n ? k : n, (int[]) null));
  }

  /**
   * Create a Uniform mutation operator.
   *
   * @param radius The radius parameter of the Uniform mutation.
   * @param p The probability that the {@link #mutate} method changes an input variable. If there
   *     are n input variables, then n*p input variables will be mutated on average during a single
   *     call to the {@link #mutate} method.
   * @param <T> The specific RealValued type.
   * @return A Uniform mutation operator
   * @throws IllegalArgumentException if p &le; 0
   */
  public static <T extends RealValued> UniformMutation<T> createUniformMutation(
      double radius, double p) {
    if (p <= 0) throw new IllegalArgumentException("p must be positive");
    if (p >= 1) {
      return createUniformMutation(radius);
    }
    return new UniformMutation<T>(
        radius,
        (old, param) -> old + ThreadLocalRandom.current().nextDouble(-param, param),
        n -> RandomSampler.sample(n, p));
  }

  @Override
  public UniformMutation<T> split() {
    return new UniformMutation<T>(this);
  }

  /**
   * Creates an identical copy of this object.
   *
   * @return an identical copy of this object
   */
  @Override
  public UniformMutation<T> copy() {
    return new UniformMutation<T>(this);
  }
}
