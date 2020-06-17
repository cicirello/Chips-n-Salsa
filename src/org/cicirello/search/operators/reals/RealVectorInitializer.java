/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
import org.cicirello.search.representations.RealVector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random {@link RealVector} objects for use in generating random initial solutions
 * for simulated annealing and other metaheuristics, and for copying such objects.
 * This initializer supports both unbounded vectors ({@link RealVector}) 
 * as well as bounded vectors, where the domain of values is bound in an interval.
 * In the bounded case, the objects created by this class enforce the bounds upon calls
 * to {@link RealVector#set} such that the {@link RealVector#set} method will set the value to
 * the min if a value is passed less than min (and similarly for max). 
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.10.2020
 */
public class RealVectorInitializer implements Initializer<RealVector> {
	
	private final double[] x;
	private final double[] a;
	private final double[] b;
	private final double[] min;
	private final double[] max;
	
	/**
	 * Construct a RealVectorInitializer that generates
	 * random solutions such that the values of all n variables are chosen
	 * uniformly in the interval [a, b).  The 
	 * {@link RealVector} objects returned by the
	 * {@link #createCandidateSolution} method are otherwise unbounded
	 * (i.e., future mutations may alter the values such that it leaves that
	 * interval).  Use a different constructor if you need to enforce bounds.
	 *
	 * @param n The number of input variables for the function.
	 * @param a The lower end of the interval (inclusive).
	 * @param b The upper end of the interval (exclusive).
	 * @throws IllegalArgumentException if a &ge; b
	 * @throws NegativeArraySizeException if n &lt; 0
	 */
	public RealVectorInitializer(int n, double a, double b) {
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		x = new double[n];
		this.a = new double[] { a };
		this.b = new double[] { b };
		min = max = null;
	}
	
	/**
	 * Construct a RealVectorInitializer that generates
	 * random solutions such that the values of variable i is chosen
	 * uniformly in the interval [a[i], b[i]).  The 
	 * {@link RealVector} objects returned by the
	 * {@link #createCandidateSolution} method are otherwise unbounded
	 * (i.e., future mutations may alter the values such that it leaves that
	 * interval).  Use a different constructor if you need to enforce bounds.
	 *
	 * @param a An array of the left points of the intervals, inclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be at least a[i].
	 * @param b An array of the right points of the intervals, exclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be less than b[i].
	 * @throws IllegalArgumentException if the lengths of a and b are different, or if there
	 * exists an i, such that a[i] &ge; b[i].
	 */
	public RealVectorInitializer(double[] a, double[] b) {
		if (a.length != b.length) throw new IllegalArgumentException("lengths of a and b must be identical");
		for (int i = 0; i < a.length; i++) {
			if (a[i] >= b[i]) throw new IllegalArgumentException("a[i] must be less than b[i]");
		}
		x = new double[a.length];
		this.a = a.clone();
		this.b = b.clone();
		min = max = null;
	}
	
	/**
	 * Construct a RealVectorInitializer that generates
	 * random solutions such that the values of all n variables are chosen
	 * uniformly in the interval [a, b), subject to
	 * bounds [min, max].  If this constructor is used, then the 
	 * {@link #createCandidateSolution} method will return 
	 * an object of a subclass of {@link RealVector}, which will
	 * enforce the constraint that the values of the function inputs must
	 * remain in the interval [min, max] as mutation and other operators
	 * are applied.
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
		if (a >= b) throw new IllegalArgumentException("a must be less than b");
		if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
		x = new double[n];
		this.a = new double[] { a <= min ? min : a };
		this.b = new double[] { b > max ? max + Math.ulp(max) : b };
		this.min = new double[] { min };
		this.max = new double[] { max };
	}
	
	/**
	 * Construct a RealVectorInitializer that generates
	 * random solutions such that the values of variable i is chosen
	 * uniformly in the interval [a[i], b[i]), subject to
	 * bounds [min, max].  If this constructor is used, then the 
	 * {@link #createCandidateSolution} method will return 
	 * an object of a subclass of {@link RealVector}, which will
	 * enforce the constraint that the values of the function inputs must
	 * remain in the interval [min, max] as mutation and other operators
	 * are applied.
	 *
	 * @param a An array of the left points of the intervals, inclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be at least a[i].
	 * @param b An array of the right points of the intervals, exclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be less than b[i].
	 * @param min Lower bound on allowed values for the function inputs generated.
	 * @param max Upper bound on allowed values for the function inputs generated.
	 * @throws IllegalArgumentException if the lengths of a and b are different; or if there
	 * exists an i, such that a[i] &ge; b[i]; or if min &gt; max.
	 */
	public RealVectorInitializer(double[] a, double[] b, double min, double max) {
		if (a.length != b.length) throw new IllegalArgumentException("lengths of a and b must be identical");
		if (min > max) throw new IllegalArgumentException("min must be less than or equal to max");
		for (int i = 0; i < a.length; i++) {
			if (a[i] >= b[i]) throw new IllegalArgumentException("a[i] must be less than b[i]");
		}
		x = new double[a.length];
		this.a = new double[a.length];
		this.b = new double[b.length];
		for (int i = 0; i < a.length; i++) {
			this.a[i] = a[i] <= min ? min : a[i];
			this.b[i] = b[i] > max ? max + Math.ulp(max) : b[i];
		}
		this.min = new double[] { min };
		this.max = new double[] { max };
	}
	
	/**
	 * Construct a RealVectorInitializer that generates
	 * random solutions such that the values of variable i is chosen
	 * uniformly in the interval [a[i], b[i]), subject to
	 * bounds [min[i], max[i]].  If this constructor is used, then the 
	 * {@link #createCandidateSolution} method will return 
	 * an object of a subclass of {@link RealVector}, which will
	 * enforce the constraint that the values of the function inputs must
	 * remain in the interval [min[i], max[i]] as mutation and other operators
	 * are applied.
	 *
	 * @param a An array of the left points of the intervals, inclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be at least a[i].
	 * @param b An array of the right points of the intervals, exclusive.  The length
	 * of this array corresponds to the number of input variables for the function
	 * you are optimizing.  Variable x[i]'s initial value will be less than b[i].
	 * @param min An array of lower bounds on allowed values for the function inputs generated,
	 * such that x[i] will never be less than min[i].
	 * @param max An array of upper bounds on allowed values for the function inputs generated,
	 * such that x[i] will never be greater than max[i].
	 * @throws IllegalArgumentException if the lengths of a and b are different; or if there
	 * exists an i, such that a[i] &ge; b[i] or min[i] &gt; max[i].
	 */
	public RealVectorInitializer(double[] a, double[] b, double[] min, double[] max) {
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
		x = new double[a.length];
		this.a = new double[a.length];
		this.b = new double[b.length];
		for (int i = 0; i < a.length; i++) {
			this.a[i] = a[i] <= min[i] ? min[i] : a[i];
			this.b[i] = b[i] > max[i] ? max[i] + Math.ulp(max[i]) : b[i];
		}
		this.min = min.clone();
		this.max = max.clone();
	}
	
	
	@Override
	public final RealVector createCandidateSolution() {
		if (a.length > 1) {
			for (int i = 0; i < x.length; i++) {
				x[i] = ThreadLocalRandom.current().nextDouble(a[i],b[i]);
			}
		} else {
			for (int i = 0; i < x.length; i++) {
				x[i] = ThreadLocalRandom.current().nextDouble(a[0],b[0]);
			}
		}
		if (min != null) {
			return new BoundedRealVector(x);
		} else {
			return new RealVector(x);
		}
	}
	
	@Override
	public RealVectorInitializer split() {
		//thread-safe so can simply return this.
		return this;
	}
	
	/**
	 * Internal class for representing the input to a multivariate function, where the
	 * input values are bounded between a specified minimum and maximum value.  If an 
	 * attempt is made to set any of the values of the function inputs to a value less
	 * than the minimum, then it is instead set to the minimum.  If an attempt is
	 * made to set the value of this function input to a value greater than
	 * the maximum, then it is instead set to the maximum.
	 *
	 */
	private final class BoundedRealVector extends RealVector {
		
		/**
		 * Initializes the parameters, with one pair of min and max bounds
		 * that apply to all parameters.
		 * @param min The minimum value allowed for the parameters.
		 * @param max The maximum value allowed for the parameters.
		 * @param x An array of the initial values for the parameters.  
		 * If x[i] is &lt; min, then
		 * function input i is initialized to min.  If x[i] is &gt; max, then
		 * function input i is initialized to max.
		 * @throws IllegalArgumentException if min &gt; max.
		 */
		public BoundedRealVector(double[] x) {
			super(x.length);
			setAll(x);
		}
		
		/**
		 * Initializes the parameters as a copy of another.
		 * @param other The other function input to copy.
		 */
		public BoundedRealVector(BoundedRealVector other) {
			super(other);
		}
		
		/**
		 * Sets a parameter to a specified value, subject to the
		 * lower and upper bounds for this function input.  If the specified
		 * new value is less than the min, then the function input is set to
		 * the min.  If the specified new value is greater than the max, then
		 * the function input is set to the max.  Otherwise, the function input
		 * is set to the specified value.
		 * @param i The input to set.
		 * @param value The new value for the i-th function input variable.
		 * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
		 */
		@Override
		public final void set(int i, double value) {
			if (min.length == 1) {
				if (value < min[0]) super.set(i, min[0]);
				else if (value > max[0]) super.set(i, max[0]);
				else super.set(i, value);
			} else {
				if (value < min[i]) super.set(i, min[i]);
				else if (value > max[i]) super.set(i, max[i]);
				else super.set(i, value);
			}
		}
		
		private void setAll(double[] x) {
			if (min.length == 1) {
				for (int i = 0; i < x.length; i++) {
					if (x[i] < min[0]) super.set(i, min[0]);
					else if (x[i] > max[0]) super.set(i, max[0]);
					else super.set(i, x[i]);
				}
			} else {
				for (int i = 0; i < x.length; i++) {
					if (x[i] < min[i]) super.set(i, min[i]);
					else if (x[i] > max[i]) super.set(i, max[i]);
					else super.set(i, x[i]);
				}
			}
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public BoundedRealVector copy() {
			return new BoundedRealVector(this);
		}
		
		private RealVectorInitializer getOuterThis() { return RealVectorInitializer.this; };
		
		
		/**
		 * Indicates whether some other object is "equal to" this one.
		 * To be equal, the other object must be of the same runtime type and contain the
		 * same values and bounds.
		 * @param other The other object to compare.
		 * @return true if other is not null, is of the same runtime type as this, and contains
		 * the same values and bounds.
		 */
		@Override
		public boolean equals(Object other) {
			if (!super.equals(other)) return false;
			BoundedRealVector b = (BoundedRealVector)other;
			return RealVectorInitializer.this == b.getOuterThis();
		}
		
		/**
		 * Returns a hash code value for the function input object.
		 * @return a hash code value
		 */
		@Override
		public int hashCode() {
			int hash;
			if (min.length == 1) {
				long bitsMin = Double.doubleToLongBits(min[0]);
				long bitsMax = Double.doubleToLongBits(max[0]);
				hash = 31 * (31 + ((int)(bitsMin ^ (bitsMin >>> 32)))) + ((int)(bitsMax ^ (bitsMax >>> 32)));
			} else {
				hash = 1;
				for (double v : min) {
					long bitsV = Double.doubleToLongBits(v);
					hash = 31 * hash + ((int)(bitsV ^ (bitsV >>> 32)));
				}
				for (double v : max) {
					long bitsV = Double.doubleToLongBits(v);
					hash = 31 * hash + ((int)(bitsV ^ (bitsV >>> 32)));
				}
			}
			int L = length();
			for (int i = 0; i < L; i++) {
				long bitsV = Double.doubleToLongBits(get(i));
				hash = 31 * hash + ((int)(bitsV ^ (bitsV >>> 32)));
			}	
			return hash;
		}
	}
}