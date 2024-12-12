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

package org.cicirello.search.operators.reals;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.BoundedRealVector;
import org.cicirello.search.representations.RealVector;

/**
 * Generates random {@link RealVector} objects for use in generating random initial solutions for
 * simulated annealing and other metaheuristics, and for copying such objects. This initializer
 * supports both unbounded vectors ({@link RealVector}) as well as bounded vectors, where the domain
 * of values is bound in an interval. In the bounded case, the objects created by this class enforce
 * the bounds upon calls to {@link RealVector#set} such that the {@link RealVector#set} method will
 * set the value to the min if a value is passed less than min (and similarly for max).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class RealVectorInitializer implements Initializer<RealVector> {

  private final EnhancedSplittableGenerator generator;
  private final double[] x;
  private final BiConsumer<double[], EnhancedSplittableGenerator> initializer;
  private final Function<double[], RealVector> creator;
  private final double[] min;
  private final double[] max;

  /**
   * Construct a RealVectorInitializer that generates random solutions such that the values of all n
   * variables are chosen uniformly in the interval [a, b). The {@link RealVector} objects returned
   * by the {@link #createCandidateSolution} method are otherwise unbounded (i.e., future mutations
   * may alter the values such that it leaves that interval). Use a different constructor if you
   * need to enforce bounds.
   *
   * @param n The number of input variables for the function.
   * @param a The lower end of the interval (inclusive).
   * @param b The upper end of the interval (exclusive).
   * @throws IllegalArgumentException if a &ge; b
   * @throws NegativeArraySizeException if n &lt; 0
   */
  public RealVectorInitializer(int n, double a, double b) {
    if (a >= b) {
      throw new IllegalArgumentException("a must be less than b");
    }
    x = new double[n];
    initializer = (x, generator) -> singleIntervalInit(generator, x, a, b);
    creator = x -> new RealVector(x);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    min = max = null;
  }

  /**
   * Construct a RealVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]). The {@link RealVector} objects
   * returned by the {@link #createCandidateSolution} method are otherwise unbounded (i.e., future
   * mutations may alter the values such that it leaves that interval). Use a different constructor
   * if you need to enforce bounds.
   *
   * @param a An array of the left points of the intervals, inclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be at least a[i].
   * @param b An array of the right points of the intervals, exclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be less than b[i].
   * @throws IllegalArgumentException if the lengths of a and b are different, or if there exists an
   *     i, such that a[i] &ge; b[i].
   */
  public RealVectorInitializer(double[] a, double[] b) {
    validateAB(a, b);
    x = new double[a.length];
    final double[] a2 = a.clone();
    final double[] b2 = b.clone();
    initializer = (x, generator) -> multiIntervalInit(generator, x, a2, b2);
    creator = x -> new RealVector(x);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    min = max = null;
  }

  /**
   * Construct a RealVectorInitializer that generates random solutions such that the values of all n
   * variables are chosen uniformly in the interval [a, b), subject to bounds [min, max]. If this
   * constructor is used, then the {@link #createCandidateSolution} method will return an object of
   * a subclass of {@link RealVector}, which will enforce the constraint that the values of the
   * function inputs must remain in the interval [min, max] as mutation and other operators are
   * applied.
   *
   * @param n The number of input variables for the function.
   * @param a The lower end of the interval (inclusive).
   * @param b The upper end of the interval (exclusive).
   * @param min Lower bound on allowed values for the function inputs generated.
   * @param max Upper bound on allowed values for the function inputs generated.
   * @throws IllegalArgumentException if a &ge; b or if min &gt; max
   * @throws NegativeArraySizeException if n &lt; 0
   */
  public RealVectorInitializer(int n, double a, double b, double min, double max) {
    if (a >= b) {
      throw new IllegalArgumentException("a must be less than b");
    }
    if (min > max) {
      throw new IllegalArgumentException("min must be less than or equal to max");
    }
    x = new double[n];
    final double a2 = Math.max(a, min);
    final double b2 = Math.min(b, max + Math.ulp(max));
    initializer = (x, generator) -> singleIntervalInit(generator, x, a2, b2);
    creator = x -> new BoundedRealVector(x, min, max);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    this.min = this.max = null;
  }

  /**
   * Construct a RealVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]), subject to bounds [min, max]. If
   * this constructor is used, then the {@link #createCandidateSolution} method will return an
   * object of a subclass of {@link RealVector}, which will enforce the constraint that the values
   * of the function inputs must remain in the interval [min, max] as mutation and other operators
   * are applied.
   *
   * @param a An array of the left points of the intervals, inclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be at least a[i].
   * @param b An array of the right points of the intervals, exclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be less than b[i].
   * @param min Lower bound on allowed values for the function inputs generated.
   * @param max Upper bound on allowed values for the function inputs generated.
   * @throws IllegalArgumentException if the lengths of a and b are different; or if there exists an
   *     i, such that a[i] &ge; b[i]; or if min &gt; max.
   */
  public RealVectorInitializer(double[] a, double[] b, double min, double max) {
    if (min > max) {
      throw new IllegalArgumentException("min must be less than or equal to max");
    }
    validateAB(a, b);
    x = new double[a.length];
    final double[] a2 = a.clone();
    final double[] b2 = b.clone();
    for (int i = 0; i < a2.length; i++) {
      a2[i] = Math.max(a2[i], min);
      b2[i] = Math.min(b2[i], max + Math.ulp(max));
    }
    initializer = (x, generator) -> multiIntervalInit(generator, x, a2, b2);
    creator = x -> new BoundedRealVector(x, min, max);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    this.min = this.max = null;
  }

  /**
   * Construct a RealVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]), subject to bounds [min[i],
   * max[i]]. If this constructor is used, then the {@link #createCandidateSolution} method will
   * return an object of a subclass of {@link RealVector}, which will enforce the constraint that
   * the values of the function inputs must remain in the interval [min[i], max[i]] as mutation and
   * other operators are applied.
   *
   * @param a An array of the left points of the intervals, inclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be at least a[i].
   * @param b An array of the right points of the intervals, exclusive. The length of this array
   *     corresponds to the number of input variables for the function you are optimizing. Variable
   *     x[i]'s initial value will be less than b[i].
   * @param min An array of lower bounds on allowed values for the function inputs generated, such
   *     that x[i] will never be less than min[i].
   * @param max An array of upper bounds on allowed values for the function inputs generated, such
   *     that x[i] will never be greater than max[i].
   * @throws IllegalArgumentException if the lengths of a and b are different; or if there exists an
   *     i, such that a[i] &ge; b[i] or min[i] &gt; max[i].
   */
  public RealVectorInitializer(double[] a, double[] b, double[] min, double[] max) {
    if (min.length != max.length || a.length != min.length) {
      throw new IllegalArgumentException("lengths of a, b, min, and max must be identical");
    }
    validateAB(a, b);
    for (int i = 0; i < min.length; i++) {
      if (min[i] > max[i]) {
        throw new IllegalArgumentException("min[i] must be less than or equal to max[i]");
      }
    }
    x = new double[a.length];
    final double[] a2 = a.clone();
    final double[] b2 = b.clone();
    for (int i = 0; i < a.length; i++) {
      a2[i] = Math.max(a2[i], min[i]);
      b2[i] = Math.min(b2[i], max[i] + Math.ulp(max[i]));
    }
    initializer = (x, generator) -> multiIntervalInit(generator, x, a2, b2);
    this.min = min.clone();
    this.max = max.clone();
    creator = x -> new MultiBoundedRealVector(x);
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private RealVectorInitializer(RealVectorInitializer other) {
    // these should be safe to share
    min = other.min;
    max = other.max;
    initializer = other.initializer;
    creator = other.creator;

    // each instance needs their own independent instances of these
    x = new double[other.x.length];
    generator = other.generator.split();
  }

  @Override
  public final RealVector createCandidateSolution() {
    initializer.accept(x, generator);
    return creator.apply(x);
  }

  @Override
  public RealVectorInitializer split() {
    return new RealVectorInitializer(this);
  }

  private static void singleIntervalInit(
      EnhancedSplittableGenerator generator, double[] x, double a, double b) {
    for (int i = 0; i < x.length; i++) {
      x[i] = generator.nextDouble(a, b);
    }
  }

  private static void multiIntervalInit(
      EnhancedSplittableGenerator generator, double[] x, double[] a, double[] b) {
    for (int i = 0; i < x.length; i++) {
      x[i] = generator.nextDouble(a[i], b[i]);
    }
  }

  private static void validateAB(double[] a, double[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("lengths of a and b must be identical");
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] >= b[i]) {
        throw new IllegalArgumentException("a[i] must be less than b[i]");
      }
    }
  }

  /**
   * Internal class for representing the input to a multivariate function, where the input values
   * are bounded between a specified minimum and maximum value. If an attempt is made to set any of
   * the values of the function inputs to a value less than the minimum, then it is instead set to
   * the minimum. If an attempt is made to set the value of this function input to a value greater
   * than the maximum, then it is instead set to the maximum.
   */
  private final class MultiBoundedRealVector extends RealVector {

    /**
     * Initializes the parameters, with one pair of min and max bounds that apply to all parameters.
     *
     * @param min The minimum value allowed for the parameters.
     * @param max The maximum value allowed for the parameters.
     * @param x An array of the initial values for the parameters. If x[i] is &lt; min, then
     *     function input i is initialized to min. If x[i] is &gt; max, then function input i is
     *     initialized to max.
     * @throws IllegalArgumentException if min &gt; max.
     */
    public MultiBoundedRealVector(double[] x) {
      super(x.length);
      setAll(x);
    }

    /**
     * Initializes the parameters as a copy of another.
     *
     * @param other The other function input to copy.
     */
    public MultiBoundedRealVector(MultiBoundedRealVector other) {
      super(other);
    }

    /**
     * Sets a parameter to a specified value, subject to the lower and upper bounds for this
     * function input. If the specified new value is less than the min, then the function input is
     * set to the min. If the specified new value is greater than the max, then the function input
     * is set to the max. Otherwise, the function input is set to the specified value.
     *
     * @param i The input to set.
     * @param value The new value for the i-th function input variable.
     * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
     */
    @Override
    public final void set(int i, double value) {
      super.set(i, Math.max(min[i], Math.min(value, max[i])));
    }

    /*
     * Only used internally by constructor,
     * and constructor only used by createCandidateSolution of
     * surrounding class,
     * and surrounding class constructors set a, b, min, and max
     * such that elements of x must already be within min, max.
     * Thus, safe to just set values without checking min, max.
     */
    private void setAll(double[] x) {
      for (int i = 0; i < x.length; i++) {
        super.set(i, x[i]);
      }
    }

    /**
     * Creates an identical copy of this object.
     *
     * @return an identical copy of this object
     */
    @Override
    public MultiBoundedRealVector copy() {
      return new MultiBoundedRealVector(this);
    }
  }
}
