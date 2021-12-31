/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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

import java.util.Arrays;
import org.cicirello.util.Copyable;


/**
 * A simple class for representing the parameters to a multivariate function.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class RealVector implements RealValued, Copyable<RealVector> {
	
	private final double[] x;
	
	/**
	 * Initializes this vector to all 0.0 values.
	 *
	 * @param n The length of the vector.
	 */
	public RealVector(int n) {
		x = new double[n];
	}
	
	/**
	 * Initializes the vector to the specified values.
	 * @param x The initial values for the vector.
	 */
	public RealVector(double[] x) {
		this.x = x.clone();
	}
	
	/**
	 * Initializes the vector as a copy of another.
	 * @param other The other vector to copy.
	 */
	public RealVector(RealVector other) {
		x = other.x.clone();
	}
	
	@Override
	public final int length() {
		return x.length;
	}
	
	@Override
	public final double get(int i) {
		return x[i];
	}
	
	@Override
	public final double[] toArray(double[] values) {
		if (values == null || values.length != x.length) 
			return x.clone();
		System.arraycopy(x, 0, values, 0, values.length);
		return values;
	}
	
	@Override
	public void set(int i, double value) {
		this.x[i] = value;
	}
	
	/**
	 * Exchanges a sequence of doubles between two RealVector objects.
	 *
	 * @param v1 The first RealVector.
	 * @param v2 The second RealVector.
	 * @param firstIndex The first index of the sequence to exchange, inclusive.
	 * @param lastIndex The last index of the sequence to exchange, inclusive.
	 *
	 * @throws IndexOutOfBoundsException if either index is negative, or if either index &ge; v1.length(),
	 *     or if either index &ge; v2.length()
	 */
	public static void exchange(RealVector v1, RealVector v2, int firstIndex, int lastIndex) {
		// Note: don't need to do bounds check... documented the behavior above.
		if (firstIndex > lastIndex) {
			int temp = firstIndex;
			firstIndex = lastIndex;
			lastIndex = temp;
		}
		if (canSimpleExchange(v1, v2)) {
			// Either not bounded, or bounds are the same....
			final int N = lastIndex-firstIndex+1;
			double[] temp = new double[N];
			System.arraycopy(v1.x, firstIndex, temp, 0, N);
			System.arraycopy(v2.x, firstIndex, v1.x, firstIndex, N);
			System.arraycopy(temp, 0, v2.x, firstIndex, N);
		} else {
			// different value-bounds so need to call set to check min and max
			for (int i = firstIndex; i <= lastIndex; i++) {
				double temp = v1.x[i];
				v1.set(i, v2.x[i]);
				v2.set(i, temp);
			}
		}
	}
	
	private static boolean canSimpleExchange(RealVector v1, RealVector v2) {
		if (v1 instanceof BoundedRealVector) {
			return v2 instanceof BoundedRealVector && ((BoundedRealVector)v1).sameBounds((BoundedRealVector)v2);
		} else if (v2 instanceof BoundedRealVector) {
			return false;
		}
		return true;
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public RealVector copy() {
		return new RealVector(this);
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * To be equal, the other object must be of the same runtime type and contain the
	 * same values.
	 * @param other The other object to compare.
	 * @return true if other is not null, is of the same runtime type as this, and contains
	 * the same values.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof RealVector)) return false;
		return Arrays.equals(x, ((RealVector)other).x);
	}
	
	/**
	 * Returns a hash code value for the function input object.
	 * @return a hash code value
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(x);
	}
}