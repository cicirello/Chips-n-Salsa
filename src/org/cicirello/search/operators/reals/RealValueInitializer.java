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
 
package org.cicirello.search.operators.reals;

import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.representations.RealValued;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generating random {@link SingleReal} objects for use in generating random initial solutions
 * for simulated annealing and other metaheuristics, and for copying such objects.
 * This initializer supports both unbounded objects ({@link SingleReal}) 
 * as well as bounded reals, where the domain of values is bound in an interval.
 * In the bounded case, the objects created by this class enforce the bounds upon calls
 * to {@link SingleReal#set} such that the {@link SingleReal#set} method will set the value to
 * the min if a value is passed less than min (and similarly for max). 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021
 */
public class RealValueInitializer implements Initializer<SingleReal> {
	
	private final double a;
	private final double b;
	private final double min;
	private final double max;
	private final boolean bounded;
	
	/**
	 * Construct a RealValueInitializer that generates
	 * random solutions uniformly in the interval [a, b).  The 
	 * {@link SingleReal} objects returned by the
	 * {@link #createCandidateSolution} method are otherwise unbounded
	 * (i.e., future mutations may alter the value such that it leaves that
	 * interval).  Use the other constructor if you need to enforce bounds.
	 *
	 * @param a The lower end of the interval (inclusive).
	 * @param b The upper end of the interval (exclusive).
	 * @throws IllegalArgumentException if a &ge; b
	 */
	public RealValueInitializer(double a, double b) {
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		this.a = a;
		this.b = b;
		bounded = false;
		min = max = 0;
	}
	
	/**
	 * Construct a RealValueInitializer that generates
	 * random solutions uniformly in the interval [a, b), subject to
	 * bounds [min, max].  If this constructor is used, then the 
	 * {@link #createCandidateSolution} method will return 
	 * an object of a subclass of {@link SingleReal}, which will
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
	public RealValueInitializer(double a, double b, double min, double max) {
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
		this.a = a <= min ? min : a;
		this.b = b > max ? max + Math.ulp(max) : b;
		this.min = min;
		this.max = max;
		bounded = true;
	}
	
	
	@Override
	public final SingleReal createCandidateSolution() {
		if (bounded) {
			return new BoundedReal(ThreadLocalRandom.current().nextDouble(a,b));
		} else {
			return new SingleReal(ThreadLocalRandom.current().nextDouble(a,b));
		}
	}
	
	@Override
	public RealValueInitializer split() {
		//thread-safe so can simply return this.
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !getClass().equals(other.getClass())) return false;
		RealValueInitializer i = (RealValueInitializer)other;
		return a == i.a && b == i.b && min == i.min && max == i.max;
	}
	
	@Override
	public int hashCode() {
		return 31*(31*(31*(31 + Double.hashCode(a))+Double.hashCode(b))+Double.hashCode(min))+Double.hashCode(max);
	}
	
	/**
	 * Internal class for representing the input to a univariate function, where the
	 * input is bounded between a specified minimum and maximum value.  If an 
	 * attempt is made to set the value less
	 * than the minimum, then it is instead set to the minimum.  If an attempt is
	 * made to set the value greater than
	 * the maximum, then it is instead set to the maximum.
	 */
	private final class BoundedReal extends SingleReal {
	
		/**
		 * Initializes this function input.
		 * @param x The initial value for the function input.  If x is &lt; min, then
		 * this function input is initialized to min.  If x is &gt; max, then
		 * this function input is initialized to max.
		 * @throws IllegalArgumentException if min &gt; max.
		 */
		public BoundedReal(double x) {
			set(x);
		}
		
		/**
		 * Initializes with a copy of another.
		 * @param other The other parameters to copy.
		 */
		public BoundedReal(BoundedReal other) {
			super(other);
		}
		
		/**
		 * Sets the parameter to a specified value, subject to the
		 * lower and upper bounds for this function input.  If the specified
		 * new value is less than the min, then the function input is set to
		 * the min.  If the specified new value is greater than the max, then
		 * the function input is set to the max.  Otherwise, the function input
		 * is set to the specified value.
		 *
		 * @param x The new value for this function input.
		 */
		@Override
		public final void set(double x) {
			if (x < min) super.set(min);
			else if (x > max) super.set(max);
			else super.set(x);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public BoundedReal copy() {
			return new BoundedReal(this);
		}
		
		private RealValueInitializer getOuterThis() { return RealValueInitializer.this; };
		
		/**
		 * Indicates whether some other object is "equal to" this one.
		 * To be equal, the other object must be of the same runtime type and contain the
		 * same value and bounds of the parameters.
		 * @param other The other object to compare.
		 * @return true if other is not null, is of the same runtime type as this, and contains
		 * the same parameter value and bounds.
		 */
		@Override
		public boolean equals(Object other) {
			if (other==null || !(other instanceof BoundedReal) || !super.equals(other)) {
				return false;
			}
			BoundedReal a = (BoundedReal)other;
			return getOuterThis().equals(a.getOuterThis());
		}
		
		/**
		 * Returns a hash code value.
		 * @return a hash code value
		 */
		@Override
		public int hashCode() {
			long bitsMin = Double.doubleToLongBits(min);
			long bitsMax = Double.doubleToLongBits(max);
			return 31*(31*(31 + super.hashCode())+((int)(bitsMin ^ (bitsMin >>> 32))))+((int)(bitsMax ^ (bitsMax >>> 32)));
		}
	}
		
}