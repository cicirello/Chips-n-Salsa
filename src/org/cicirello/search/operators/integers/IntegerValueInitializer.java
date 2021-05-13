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
 
package org.cicirello.search.operators.integers;

import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.SingleInteger;
import org.cicirello.math.rand.RandomIndexer;

/**
 * Generates random {@link SingleInteger} objects for use in generating random initial solutions
 * for simulated annealing and other metaheuristics, and for copying such objects.
 * This initializer supports both unbounded objects ({@link SingleInteger}) 
 * as well as bounded reals, where the domain of values is bound in an interval.
 * In the bounded case, the objects created by this class enforce the bounds upon calls
 * to {@link SingleInteger#set} such that the {@link SingleInteger#set} method will set the value to
 * the min if a value is passed less than min (and similarly for max). 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021
 */
public class IntegerValueInitializer implements Initializer<SingleInteger> {
	
	private final int a;
	private final int b;
	private final int min;
	private final int max;
	private final boolean bounded;
	
	/**
	 * Construct a IntegerValueInitializer that generates
	 * random solutions uniformly in the interval [a, b).  The 
	 * {@link SingleInteger} objects returned by the
	 * {@link #createCandidateSolution} method are otherwise unbounded
	 * (i.e., future mutations may alter the value such that it leaves that
	 * interval).  Use the other constructor if you need to enforce bounds.
	 *
	 * @param a The lower end of the interval (inclusive).
	 * @param b The upper end of the interval (exclusive).
	 * @throws IllegalArgumentException if a &ge; b
	 */
	public IntegerValueInitializer(int a, int b) {
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		this.a = a;
		this.b = b;
		bounded = false;
		min = max = 0;
	}
	
	/**
	 * Construct a IntegerValueInitializer that generates
	 * random solutions uniformly in the interval [a, b), subject to
	 * bounds [min, max].  If this constructor is used, then the 
	 * {@link #createCandidateSolution} method will return 
	 * an object of a subclass of {@link SingleInteger}, which will
	 * enforce the constraint that the value of the function inputs must
	 * remain in the interval [min, max] as mutation and other operators
	 * are applied.
	 *
	 * @param a The lower end of the interval (inclusive).
	 * @param b The upper end of the interval (exclusive).
	 * @param min Lower bound on allowed values for the function inputs generated.
	 * @param max Upper bound on allowed values for the function inputs generated.
	 * @throws IllegalArgumentException if a &ge; b
	 */
	public IntegerValueInitializer(int a, int b, int min, int max) {
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
		this.a = a <= min ? min : a;
		this.b = b > max + 1 ? max + 1 : b;
		this.min = min;
		this.max = max;
		bounded = true;
	}
	
	
	@Override
	public final SingleInteger createCandidateSolution() {
		if (bounded) {
			return new BoundedInteger(a + RandomIndexer.nextInt(b-a));
		} else {
			return new SingleInteger(a + RandomIndexer.nextInt(b-a));
		}
	}
	
	@Override
	public IntegerValueInitializer split() {
		//thread-safe so can simply return this.
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof IntegerValueInitializer)) {
			return false;
		}
		IntegerValueInitializer i = (IntegerValueInitializer)other;
		return a == i.a && b == i.b && min == i.min && max == i.max && bounded == i.bounded;
	}
	
	@Override
	public int hashCode() {
		return 31*(31*(31*(31*(31 + a)+b)+min)+max)+(bounded?1:0);
	}
	
	/**
	 * Internal class for representing the input to a univariate function, such that
	 * the input is an integer, and where the
	 * input is bounded between a specified minimum and maximum value.  If an 
	 * attempt is made to set the value of this function input to a value less
	 * than the minimum, then it is instead set to the minimum.  If an attempt is
	 * made to set the value of this function input to a value greater than
	 * the maximum, then it is instead set to the maximum.
	 */
	private final class BoundedInteger extends SingleInteger {
		
		/**
		 * Initializes this function input.
		 * @param x The initial value for the function input.  If x is &lt; min, then
		 * this function input is initialized to min.  If x is &gt; max, then
		 * this function input is initialized to max.
		 * @throws IllegalArgumentException if min &gt; max.
		 */
		public BoundedInteger(int x) {
			set(x);
		}
		
		/**
		 * Initializes this function input as a copy of another.
		 * @param other The other function input to copy.
		 */
		public BoundedInteger(BoundedInteger other) {
			super(other);
		}
		
		/**
		 * Sets this function input to a specified value, subject to the
		 * lower and upper bounds for this function input.  If the specified
		 * new value is less than the min, then the function input is set to
		 * the min.  If the specified new value is greater than the max, then
		 * the function input is set to the max.  Otherwise, the function input
		 * is set to the specified value.
		 *
		 * @param x The new value for this function input.
		 */
		@Override
		public final void set(int x) {
			if (x < min) super.set(min);
			else if (x > max) super.set(max);
			else super.set(x);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public BoundedInteger copy() {
			return new BoundedInteger(this);
		}
		
	
		private IntegerValueInitializer getOuterThis() { return IntegerValueInitializer.this; };
		
		
		/**
		 * Indicates whether some other object is "equal to" this one.
		 * To be equal, the other object must be of the same runtime type and contain the
		 * same value and bounds of the function input.
		 * @param other The other object to compare.
		 * @return true if other is not null, is of the same runtime type as this, and contains
		 * the same function input value and bounds.
		 */
		@Override
		public boolean equals(Object other) {
			if (other==null || !(other instanceof BoundedInteger) || !super.equals(other)) {
				return false;
			}
			BoundedInteger a = (BoundedInteger)other;
			return IntegerValueInitializer.this.min == a.getOuterThis().min &&
				IntegerValueInitializer.this.max == a.getOuterThis().max;
		}
		
		/**
		 * Returns a hash code value.
		 * @return a hash code value
		 */
		@Override
		public int hashCode() {
			return 31*(31*(31 + super.hashCode())+min)+max;
		}
	}
}