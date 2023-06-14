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

import org.cicirello.math.rand.RandomSampler;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.representations.IntegerValued;

/**
 * This class implements a uniform mutation on integer valued parameters, with support for the
 * {@link #undo} method. This class can be used to mutate objects of any of the classes that
 * implement the {@link IntegerValued} interface, including both univariates and multivariates.
 *
 * <p>In the form of uniform mutation implemented by this class, a value v is mutated by adding to
 * it a randomly generated integer m such that m is drawn uniformly at random from the interval
 * [-radius, radius].
 *
 * <p>This mutation operator also implements the {@link IntegerValued} interface to enable
 * implementation of metaheuristics that mutate their own mutation parameters. That is, you can pass
 * a UniformMutation object to the {@link #mutate} method of a UniformMutation object to mutate its
 * radius.
 *
 * <p>To construct a UniformMutation, you must use one of the factory methods. See the various
 * {@link #createUniformMutation} methods.
 *
 * @param <T> The specific IntegerValued type.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class UndoableUniformMutation<T extends IntegerValued> extends UniformMutation<T>
    implements UndoableMutationOperator<T> {

  int[] previous;
  int old;

  /*
   * Internal constructor.  Constructs a Uniform mutation operator supporting the undo operation.
   * Otherwise, must use the factory methods.
   * @param radius The radius parameter of the Uniform.
   */
  UndoableUniformMutation(int radius) {
    super(radius);
  }

  /*
   * internal copy constructor: not a true copy... doesn't copy state related to undo method
   */
  UndoableUniformMutation(UndoableUniformMutation<T> other) {
    super(other);
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
    return new UndoableUniformMutation<T>(Math.abs(radius));
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
    return new UndoablePartialUniformMutation<T>(Math.abs(radius), k);
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
    return p >= 1
        ? new UndoableUniformMutation<T>(Math.abs(radius))
        : new UndoablePartialUniformMutation<T>(Math.abs(radius), p);
  }

  @Override
  public void mutate(T c) {
    if (c.length() > 1) internalMutate(c, previous = c.toArray(previous));
    else if (c.length() == 1) internalMutate(c, old = c.get(0));
  }

  @Override
  public void undo(T c) {
    if (c.length() > 1) {
      for (int i = 0; i < c.length(); i++) {
        c.set(i, previous[i]);
      }
    } else if (c.length() == 1) {
      c.set(0, old);
    }
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

  @Override
  public boolean equals(Object other) {
    return super.equals(other) && other instanceof UndoableUniformMutation;
  }

  private static final class UndoablePartialUniformMutation<T extends IntegerValued>
      extends UndoableUniformMutation<T> {

    private final int k;
    private final double p;
    private int[] indexes;

    UndoablePartialUniformMutation(int radius, int k) {
      super(radius);
      this.k = k;
      p = -1;
    }

    UndoablePartialUniformMutation(int radius, double p) {
      super(radius);
      this.p = p;
      k = 0;
    }

    UndoablePartialUniformMutation(UndoablePartialUniformMutation<T> other) {
      super(other);
      k = other.k;
      p = other.p;
    }

    @Override
    public void mutate(T c) {
      if (k >= c.length()) {
        super.mutate(c);
      } else {
        indexes =
            p < 0
                ? RandomSampler.sample(c.length(), k, indexes)
                : RandomSampler.sample(c.length(), p);
        if (previous == null || previous.length < indexes.length) {
          previous = new int[indexes.length];
        }
        for (int i = 0; i < indexes.length; i++) {
          previous[i] = c.get(indexes[i]);
        }
        internalPartialMutation(c, indexes, previous);
      }
    }

    @Override
    public void undo(T c) {
      if (k >= c.length()) {
        super.undo(c);
      } else {
        for (int i = 0; i < indexes.length; i++) {
          c.set(indexes[i], previous[i]);
        }
      }
    }

    /**
     * Indicates whether some other object is equal to this one. The objects are equal if they are
     * the same type of operator with the same parameters.
     *
     * @param other the object with which to compare
     * @return true if and only if the objects are equal
     */
    @Override
    public boolean equals(Object other) {
      if (!super.equals(other) || !(other instanceof UndoablePartialUniformMutation)) {
        return false;
      }
      UndoablePartialUniformMutation g = (UndoablePartialUniformMutation) other;
      return k == g.k && p == g.p;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash
     * tables such as those provided by HashMap.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
      return 31 * super.hashCode() + (p < 0 ? k : Double.hashCode(p));
    }

    @Override
    public UndoablePartialUniformMutation<T> split() {
      return new UndoablePartialUniformMutation<T>(this);
    }

    /**
     * Creates an identical copy of this object.
     *
     * @return an identical copy of this object
     */
    @Override
    public UndoablePartialUniformMutation<T> copy() {
      return new UndoablePartialUniformMutation<T>(this);
    }
  }
}
