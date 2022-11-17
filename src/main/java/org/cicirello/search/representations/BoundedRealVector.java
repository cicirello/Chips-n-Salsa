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

package org.cicirello.search.representations;

/**
 * A class for representing the input to a multivariate function, with real values (floating-point)
 * that are bounded in some interval [min, max].
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BoundedRealVector extends RealVector {

  private final double min;
  private final double max;

  /**
   * Initializes the vector to the specified values, subject to the given bounds [min, max].
   *
   * @param x The initial values for the vector. Any values that are less than min will instead be
   *     set to min. Any values that are greater than max will instead be set to max.
   * @param min The minimum allowed value for each integer.
   * @param max The maximum allowed value for each integer.
   * @throws IllegalArgumentException if min &gt; max
   */
  public BoundedRealVector(double[] x, double min, double max) {
    super(x.length);
    if (min > max) throw new IllegalArgumentException("max must be greater than or equal to min");
    this.min = min;
    this.max = max;
    for (int i = 0; i < x.length; i++) {
      set(i, x[i]);
    }
  }

  /**
   * Copies a BoundedRealVector.
   *
   * @param other The other BoundedRealVector
   */
  public BoundedRealVector(BoundedRealVector other) {
    super(other);
    min = other.min;
    max = other.max;
  }

  /**
   * Sets a parameter to a specified value, subject to the lower and upper bounds for this function
   * input. If the specified new value is less than the min, then the function input is set to the
   * min. If the specified new value is greater than the max, then the function input is set to the
   * max. Otherwise, the function input is set to the specified value.
   *
   * @param i The input to set.
   * @param value The new value for the i-th function input.
   * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
   */
  @Override
  public final void set(int i, double value) {
    if (value < min) super.set(i, min);
    else if (value > max) super.set(i, max);
    else super.set(i, value);
  }

  /**
   * Sets from an array, subject to the lower and upper bounds for this vector. If a specified new
   * value is less than the min, then it is set to the min. If the specified new value is greater
   * than the max, then the it is set to the max. Otherwise, it is set to the specified value.
   *
   * @param values The values to set.
   */
  @Override
  public final void set(double[] values) {
    for (int i = 0; i < values.length; i++) {
      set(i, values[i]);
    }
  }

  /**
   * Checks if the bounds of this BoundedRealVector are the same as those of another.
   *
   * @param other The other vector.
   * @return true if the vectors have the same bounds and false otherwise
   */
  public final boolean sameBounds(BoundedRealVector other) {
    return min == other.min && max == other.max;
  }

  /**
   * Creates an identical copy of this object.
   *
   * @return an identical copy of this object
   */
  @Override
  public BoundedRealVector copy() {
    return new BoundedRealVector(this);
  }

  /**
   * Indicates whether some other object is "equal to" this one. To be equal, the other object must
   * be of the same runtime type and contain the same values and bounds.
   *
   * @param other The other object to compare.
   * @return true if other is not null, is of the same runtime type as this, and contains the same
   *     values and bounds.
   */
  @Override
  public boolean equals(Object other) {
    if (!super.equals(other) || !(other instanceof BoundedRealVector)) return false;
    return sameBounds((BoundedRealVector) other);
  }

  /**
   * Returns a hash code value.
   *
   * @return a hash code value
   */
  @Override
  public int hashCode() {
    return 31 * (31 * (31 + Double.hashCode(min)) + Double.hashCode(max)) + super.hashCode();
  }
}
