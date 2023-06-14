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

import java.util.Arrays;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.BoundedIntegerVector;
import org.cicirello.search.representations.IntegerVector;

/**
 * Generating random {@link IntegerVector} objects for use in generating random initial solutions
 * for simulated annealing and other metaheuristics, and for copying such objects. This initializer
 * supports both unbounded vectors ({@link IntegerVector}) as well as bounded vectors, where the
 * domain of values is bound in an interval. In the bounded case, the objects created by this class
 * enforce the bounds upon calls to {@link IntegerVector#set} such that the {@link
 * IntegerVector#set} method will set the value to the min if a value is passed less than min (and
 * similarly for max).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class IntegerVectorInitializer implements Initializer<IntegerVector> {

  private final int[] x;
  private final int[] a;
  private final int[] b;
  private final int[] min;
  private final int[] max;
  private final EnhancedSplittableGenerator generator;

  /**
   * Construct a IntegerVectorInitializer that generates random solutions such that the values of
   * all n variables are chosen uniformly in the interval [a, b). The {@link IntegerVector} objects
   * returned by the {@link #createCandidateSolution} method are otherwise unbounded (i.e., future
   * mutations may alter the values such that it leaves that interval). Use a different constructor
   * if you need to enforce bounds.
   *
   * @param n The number of input variables for the function.
   * @param a The lower end of the interval (inclusive).
   * @param b The upper end of the interval (exclusive).
   * @throws IllegalArgumentException if a &ge; b
   * @throws NegativeArraySizeException if n &lt; 0
   */
  public IntegerVectorInitializer(int n, int a, int b) {
    if (a >= b) throw new IllegalArgumentException("a must be less than b");
    x = new int[n];
    this.a = new int[] {a};
    this.b = new int[] {b};
    min = max = null;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Construct a IntegerVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]). The {@link IntegerVector} objects
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
  public IntegerVectorInitializer(int[] a, int[] b) {
    if (a.length != b.length)
      throw new IllegalArgumentException("lengths of a and b must be identical");
    for (int i = 0; i < a.length; i++) {
      if (a[i] >= b[i]) throw new IllegalArgumentException("a[i] must be less than b[i]");
    }
    x = new int[a.length];
    this.a = a.clone();
    this.b = b.clone();
    min = max = null;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Construct a IntegerVectorInitializer that generates random solutions such that the values of
   * all n variables are chosen uniformly in the interval [a, b), subject to bounds [min, max]. If
   * this constructor is used, then the {@link #createCandidateSolution} method will return an
   * object of a subclass of {@link IntegerVector}, which will enforce the constraint that the
   * values of the function inputs must remain in the interval [min, max] as mutation and other
   * operators are applied.
   *
   * @param n The number of input variables for the function.
   * @param a The lower end of the interval (inclusive).
   * @param b The upper end of the interval (exclusive).
   * @param min Lower bound on allowed values for the function inputs generated.
   * @param max Upper bound on allowed values for the function inputs generated.
   * @throws IllegalArgumentException if a &ge; b or if min &gt; max
   * @throws NegativeArraySizeException if n &lt; 0
   */
  public IntegerVectorInitializer(int n, int a, int b, int min, int max) {
    if (a >= b) throw new IllegalArgumentException("a must be less than b");
    if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
    x = new int[n];
    this.a = new int[] {a <= min ? min : a};
    this.b = new int[] {b > max + 1 ? max + 1 : b};
    this.min = new int[] {min};
    this.max = new int[] {max};
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Construct a IntegerVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]), subject to bounds [min, max]. If
   * this constructor is used, then the {@link #createCandidateSolution} method will return an
   * object of a subclass of {@link IntegerVector}, which will enforce the constraint that the
   * values of the function inputs must remain in the interval [min, max] as mutation and other
   * operators are applied.
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
  public IntegerVectorInitializer(int[] a, int[] b, int min, int max) {
    if (a.length != b.length)
      throw new IllegalArgumentException("lengths of a and b must be identical");
    if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
    for (int i = 0; i < a.length; i++) {
      if (a[i] >= b[i]) throw new IllegalArgumentException("a[i] must be less than b[i]");
    }
    x = new int[a.length];
    this.a = new int[a.length];
    this.b = new int[b.length];
    for (int i = 0; i < a.length; i++) {
      this.a[i] = a[i] <= min ? min : a[i];
      this.b[i] = b[i] > max + 1 ? max + 1 : b[i];
    }
    this.min = new int[] {min};
    this.max = new int[] {max};
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Construct a IntegerVectorInitializer that generates random solutions such that the values of
   * variable i is chosen uniformly in the interval [a[i], b[i]), subject to bounds [min[i],
   * max[i]]. If this constructor is used, then the {@link #createCandidateSolution} method will
   * return an object of a subclass of {@link IntegerVector}, which will enforce the constraint that
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
  public IntegerVectorInitializer(int[] a, int[] b, int[] min, int[] max) {
    if (a.length != b.length || min.length != max.length || a.length != min.length) {
      throw new IllegalArgumentException("lengths of a, b, min, and max must be identical");
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] >= b[i]) {
        throw new IllegalArgumentException("a[i] must be less than b[i]");
      }
      if (min[i] > max[i]) {
        throw new IllegalArgumentException("min[i] must be less than or equal to max[i]");
      }
    }
    x = new int[a.length];
    this.a = new int[a.length];
    this.b = new int[b.length];
    for (int i = 0; i < a.length; i++) {
      this.a[i] = a[i] <= min[i] ? min[i] : a[i];
      this.b[i] = b[i] > max[i] + 1 ? max[i] + 1 : b[i];
    }
    this.min = min.clone();
    this.max = max.clone();
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private IntegerVectorInitializer(IntegerVectorInitializer other) {
    min = other.min == null ? null : other.min.clone();
    max = other.max == null ? null : other.max.clone();
    a = other.a.clone();
    b = other.b.clone();
    x = new int[a.length];
    generator = other.generator.split();
  }

  @Override
  public final IntegerVector createCandidateSolution() {
    if (a.length > 1) {
      for (int i = 0; i < x.length; i++) {
        x[i] = a[i] + generator.nextInt(b[i] - a[i]);
      }
    } else {
      for (int i = 0; i < x.length; i++) {
        x[i] = a[0] + generator.nextInt(b[0] - a[0]);
      }
    }
    if (min != null) {
      return min.length > 1
          ? new MultiBoundedIntegerVector(x)
          : new BoundedIntegerVector(x, min[0], max[0]);
    } else {
      return new IntegerVector(x);
    }
  }

  @Override
  public IntegerVectorInitializer split() {
    return new IntegerVectorInitializer(this);
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || !(other instanceof IntegerVectorInitializer)) {
      return false;
    }
    IntegerVectorInitializer i = (IntegerVectorInitializer) other;
    return ((min == null && i.min == null)
            || (Arrays.equals(min, i.min) && Arrays.equals(max, i.max)))
        && Arrays.equals(a, i.a)
        && Arrays.equals(b, i.b);
  }

  @Override
  public int hashCode() {
    int h = 31 * Arrays.hashCode(a) + Arrays.hashCode(b);
    if (min != null) {
      h = 31 * (31 * h + Arrays.hashCode(min)) + Arrays.hashCode(max);
    }
    return h;
  }

  /**
   * Internal class for representing the input to a multivariate function, where the input
   * parameters are integers and are bounded between a specified minimum and maximum value. If an
   * attempt is made to set any of the values of the function inputs to a value less than the
   * minimum, then it is instead set to the minimum. If an attempt is made to set the value of this
   * function input to a value greater than the maximum, then it is instead set to the maximum.
   */
  private final class MultiBoundedIntegerVector extends IntegerVector {

    /**
     * Initializes the parameters, with one pair of min and max bounds that apply to all parameters.
     *
     * @param x An array of the initial values for the function parameters. If x[i] is &lt; min,
     *     then function parameter i is initialized to min. If x[i] is &gt; max, then function
     *     parameter i is initialized to max.
     * @throws IllegalArgumentException if min &gt; max.
     */
    public MultiBoundedIntegerVector(int[] x) {
      super(x.length);
      setAll(x);
    }

    /**
     * Initializes as a copy of another.
     *
     * @param other The other to copy.
     */
    public MultiBoundedIntegerVector(MultiBoundedIntegerVector other) {
      super(other);
    }

    /**
     * Sets a parameter to a specified value, subject to the lower and upper bounds for this
     * function input. If the specified new value is less than the min, then the function input is
     * set to the min. If the specified new value is greater than the max, then the function input
     * is set to the max. Otherwise, the function input is set to the specified value.
     *
     * @param i The input to set.
     * @param value The new value for the i-th function input.
     * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
     */
    @Override
    public final void set(int i, int value) {
      if (value < min[i]) super.set(i, min[i]);
      else if (value > max[i]) super.set(i, max[i]);
      else super.set(i, value);
    }

    /*
     * Only used internally by constructor,
     * and constructor only used by createCandidateSolution of
     * surrounding class,
     * and surrounding class constructors set a, b, min, and max
     * such that elements of x must already be within min, max.
     * Thus, safe to just set values without checking min, max.
     */
    private void setAll(int[] x) {
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
    public MultiBoundedIntegerVector copy() {
      return new MultiBoundedIntegerVector(this);
    }

    private IntegerVectorInitializer getOuterThis() {
      return IntegerVectorInitializer.this;
    }
    ;

    /**
     * Indicates whether some other object is "equal to" this one. To be equal, the other object
     * must be of the same runtime type and contain the same values and bounds.
     *
     * @param other The other object to compare.
     * @return true if other is not null, is of the same runtime type as this, and contains the same
     *     values and bounds.
     */
    @Override
    public boolean equals(Object other) {
      if (other == null || !(other instanceof MultiBoundedIntegerVector) || !super.equals(other)) {
        return false;
      }
      MultiBoundedIntegerVector b = (MultiBoundedIntegerVector) other;
      return Arrays.equals(IntegerVectorInitializer.this.min, b.getOuterThis().min)
          && Arrays.equals(IntegerVectorInitializer.this.max, b.getOuterThis().max);
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     */
    @Override
    public int hashCode() {
      int hash = 1;
      for (int v : min) {
        hash = 31 * hash + v;
      }
      for (int v : max) {
        hash = 31 * hash + v;
      }
      int L = length();
      for (int i = 0; i < L; i++) {
        hash = 31 * hash + get(i);
      }
      return hash;
    }
  }
}
