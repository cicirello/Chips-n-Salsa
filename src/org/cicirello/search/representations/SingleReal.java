/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

import org.cicirello.util.Copyable;


/**
 * A simple class for representing the input to a univariate function.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021
 */
public class SingleReal implements RealValued, Copyable<SingleReal> {
	
	private double x;
	
	/**
	 * Initializes to 0.0.
	 */
	public SingleReal() {}
	
	/**
	 * Initializes to a specified value.
	 * @param x The initial value.
	 */
	public SingleReal(double x) {
		this.x = x;
	}
	
	/**
	 * Initializes as a copy of another.
	 * @param other The other to copy.
	 */
	public SingleReal(SingleReal other) {
		x = other.x;
	}
	
	@Override
	public final int length() {
		return 1;
	}
	
	/**
	 * Accesses the current value.
	 * @return The current value.
	 */
	public final double get() {
		return x;
	}
	
	/**
	 * Accesses the current value.
	 * This method originates with the {@link RealValued} interface.
	 * Since this is a univariate function, there is only 1 input variable by
	 * definition.  Rather than throw an exception for values of i other than 0,
	 * this method ignores the i parameter and is equivalent to
	 * the {@link #get()} method regardless of value passed for i.
	 * @param i The input to get (ignored by this implementation since this 
	 * is an input for a univariate function).
	 * @return The current value of this function input.
	 */
	@Override
	public final double get(int i) {
		return x;
	}
	
	@Override
	public final double[] toArray(double[] values) {
		if (values == null || values.length != 1) values = new double[1];
		values[0] = x;
		return values;
	}
	
	/**
	 * Sets to a specified value.
	 * @param x The new value.
	 */
	public void set(double x) {
		this.x = x;
	}
	
	/**
	 * <p>Sets this function input to a specified value.
	 * This method originates with the {@link RealValued} interface.
	 * Since this is a univariate function, there is only 1 input variable by
	 * definition.  Rather than throw an exception for values of i other than 0,
	 * this method ignores the i parameter and is equivalent to
	 * the {@link #set(double)} method regardless of value passed for i.</p>
	 * <p>This method delegates work to the {@link #set(double)} method, so the
	 * behavior of this method will be consistent with any subclasses that override 
	 * {@link #set(double)}.</p>
	 * @param i The input variable to set (ignored by this implementation since this 
	 * is an input for a univariate function).
	 * @param x The new value for this function input.
	 */
	@Override
	public final void set(int i, double x) {
		set(x);
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public SingleReal copy() {
		return new SingleReal(this);
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * To be equal, the other object must be of the same runtime type and contain the
	 * same value of the function input.
	 * @param other The other object to compare.
	 * @return true if other is not null, is of the same runtime type as this, and contains
	 * the same function input value.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SingleReal)) return false;
		return x == ((SingleReal)other).x;
	}
	
	/**
	 * Returns a hash code value for the function input object.
	 * @return a hash code value
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(x);
	}
}