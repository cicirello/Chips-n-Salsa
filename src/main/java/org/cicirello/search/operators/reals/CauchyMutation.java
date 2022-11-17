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

import org.cicirello.math.rand.RandomSampler;
import org.cicirello.math.rand.RandomVariates;
import org.cicirello.search.representations.RealValued;
import org.cicirello.util.Copyable;

/**
 * This class implements Cauchy mutation. Cauchy mutation is for mutating floating-point values.
 * This class can be used to mutate objects of any of the classes that implement the {@link
 * RealValued} interface, including both univariate and multivariate function input objects.
 *
 * <p>In a Cauchy mutation, a value v is mutated by adding a randomly generated m such that m is
 * drawn from a Cauchy distribution with location parameter 0 (i.e., median 0) and scale parameter,
 * scale. It is commonly employed in evolutionary computation when mutating real valued parameters.
 * It is an alternative to the slightly more common Gaussian mutation (see the {@link
 * GaussianMutation} class). Gaussian mutation has better convergence properties, however, due to
 * the heavy-tailed nature of the Cauchy distribution, Cauchy mutation can sometimes escape local
 * optima better than Gaussian mutation (i.e., Cauchy mutation is more likely than Gaussian mutation
 * to make large jumps).
 *
 * <p>This mutation operator also implements the {@link RealValued} interface to enable
 * implementation of metaheuristics that mutate their own mutation parameters. That is, you can pass
 * a CauchyMutation object to the {@link #mutate} method of a CauchyMutation object.
 *
 * <p>To construct a CauchyMutation, you must use one of the factory methods. See the various {@link
 * #createCauchyMutation} methods.
 *
 * <p>Cauchy mutation was introduced in the following article:<br>
 * H.H. Szu and R.L. Hartley. Nonconvex optimization by fast simulated annealing. Proceedings of the
 * IEEE, 75(11): 1538–1540, November 1987.
 *
 * @param <T> The specific RealValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class CauchyMutation<T extends RealValued> extends AbstractRealMutation<T>
    implements Copyable<CauchyMutation<T>> {

  /*
   * Internal constructor.  Constructs a Cauchy mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param scale The scale parameter of the Cauchy.
   *
   * @param transformer The functional transformation of the mutation.
   */
  CauchyMutation(double scale, Transformation transformer) {
    super(scale, transformer);
  }

  /*
   * Internal constructor.  Constructs a Cauchy mutation operator.
   * Otherwise, must use the factory methods.
   *
   * @param scale The scale parameter of the Cauchy.
   *
   * @param transformer The functional transformation of the mutation.
   *
   * @param selector Chooses the indexes for a partial mutation.
   */
  CauchyMutation(double scale, Transformation transformer, Selector selector) {
    super(scale, transformer, selector);
  }

  /*
   * internal copy constructor
   */
  CauchyMutation(CauchyMutation<T> other) {
    super(other);
  }

  /**
   * Creates a Cauchy mutation operator with scale parameter equal to 1.
   *
   * @param <T> The specific RealValued type.
   * @return A Cauchy mutation operator.
   */
  public static <T extends RealValued> CauchyMutation<T> createCauchyMutation() {
    return createCauchyMutation(1.0);
  }

  /**
   * Creates a Cauchy mutation operator.
   *
   * @param scale The scale parameter of the Cauchy.
   * @param <T> The specific RealValued type.
   * @return A Cauchy mutation operator.
   */
  public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(double scale) {
    return new CauchyMutation<T>(scale, (old, param) -> old + RandomVariates.nextCauchy(param));
  }

  /**
   * Creates a Cauchy mutation operator, such that the mutate method constrains each mutated real
   * value to lie in the interval [lowerBound, upperBound].
   *
   * @param scale The scale parameter of the Cauchy.
   * @param lowerBound A lower bound on the result of a mutation.
   * @param upperBound An upper bound on the result of a mutation.
   * @param <T> The specific RealValued type.
   * @return A Cauchy mutation operator.
   */
  public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(
      double scale, double lowerBound, double upperBound) {
    if (upperBound < lowerBound)
      throw new IllegalArgumentException("upperBound must be at least lowerBound");
    return new CauchyMutation<T>(
        scale,
        (old, param) -> {
          double mutated = old + RandomVariates.nextCauchy(param);
          if (mutated <= lowerBound) return lowerBound;
          if (mutated >= upperBound) return upperBound;
          return mutated;
        });
  }

  /**
   * Create a Cauchy mutation operator.
   *
   * @param scale The scale parameter of the Cauchy mutation.
   * @param k The number of input variables that the {@link #mutate} method changes when called. The
   *     k input variables are chosen uniformly at random from among all subsets of size k. If there
   *     are less than k input variables, then all are mutated.
   * @param <T> The specific RealValued type.
   * @return A Cauchy mutation operator
   * @throws IllegalArgumentException if k &lt; 1
   */
  public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(double scale, int k) {
    if (k < 1) throw new IllegalArgumentException("k must be at least 1");
    return new CauchyMutation<T>(
        scale,
        (old, param) -> old + RandomVariates.nextCauchy(param),
        n -> RandomSampler.sample(n, k < n ? k : n, (int[]) null));
  }

  /**
   * Create a Cauchy mutation operator.
   *
   * @param scale The scale parameter of the Cauchy mutation.
   * @param p The probability that the {@link #mutate} method changes an input variable. If there
   *     are n input variables, then n*p input variables will be mutated on average during a single
   *     call to the {@link #mutate} method.
   * @param <T> The specific RealValued type.
   * @return A Cauchy mutation operator
   * @throws IllegalArgumentException if p &le; 0
   */
  public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(
      double scale, double p) {
    if (p <= 0) throw new IllegalArgumentException("p must be positive");
    if (p >= 1) {
      return createCauchyMutation(scale);
    }
    return new CauchyMutation<T>(
        scale,
        (old, param) -> old + RandomVariates.nextCauchy(param),
        n -> RandomSampler.sample(n, p));
  }

  @Override
  public CauchyMutation<T> split() {
    return new CauchyMutation<T>(this);
  }

  /**
   * Creates an identical copy of this object.
   *
   * @return an identical copy of this object
   */
  @Override
  public CauchyMutation<T> copy() {
    return new CauchyMutation<T>(this);
  }
}
