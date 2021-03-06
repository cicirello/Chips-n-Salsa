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

import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.RealValued;
import org.cicirello.math.rand.RandomIndexer;
import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.util.Copyable;

/**
 * <p>This class implements a uniform
 * mutation.  Uniform mutation is for
 * mutating floating-point values.  This class can be used to mutate
 * objects of any of the classes that implement the 
 * {@link RealValued}
 * interface, including both univariate and multivariate function input
 * objects.</p>
 *
 * <p>In the form of uniform mutation implemented by this class,
 * a value v is mutated by adding a randomly
 * generated m such that m is drawn uniformly at random from the interval [-radius, radius].</p>
 *
 * <p>This mutation operator also 
 * implements the {@link RealValued} 
 * interface to enable implementation
 * of metaheuristics that mutate their own mutation parameters.  That is, you can pass
 * a UniformMutation object to the {@link #mutate} method of a UniformMutation object.</p>
 *
 * <p>To construct a UniformMutation, you must use one of the factory methods.  See
 * the various {@link #createUniformMutation} methods.</p>
 *
 * @param <T> The specific RealValued type.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021
 */
public class UniformMutation<T extends RealValued> implements MutationOperator<T>, RealValued, Copyable<UniformMutation<T>> {
	
	private double radius;
	
	/*
	 * Internal constructor.  Constructs a Uniform mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param radius The radius parameter of the Uniform.
	 */
	UniformMutation(double radius) { 
		this.radius = radius;
	}
	
	/*
	 * internal copy constructor
	 */
	UniformMutation(UniformMutation<T> other) {
		radius = other.radius;
	}
	
	/**
	 * Creates a Uniform mutation operator with radius parameter equal to 1.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator.
	 */
	public static <T extends RealValued> UniformMutation<T> createUniformMutation() {
		return new UniformMutation<T>(1.0);
	}
	
	/**
	 * Creates a Uniform mutation operator.
	 * @param radius The radius parameter of the Uniform.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator.
	 */
	public static <T extends RealValued> UniformMutation<T> createUniformMutation(double radius) {
		return new UniformMutation<T>(radius);
	}
	
	/**
	 * Create a Uniform mutation operator.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator
	 * @throws IllegalArgumentException if k &lt; 1
	 */
	public static <T extends RealValued> UniformMutation<T> createUniformMutation(double radius, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
		return new PartialUniformMutation<T>(radius, k);
	}
	
	/**
	 * Create a Uniform mutation operator.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input variables will be mutated on average during
	 * a single call to the {@link #mutate} method.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator
	 * @throws IllegalArgumentException if p &le; 0
	 */
	public static <T extends RealValued> UniformMutation<T> createUniformMutation(double radius, double p) {
		if (p <= 0) throw new IllegalArgumentException("p must be positive");
		return p >= 1
			? new UniformMutation<T>(radius)
			: new PartialUniformMutation<T>(radius, p);
	}
	
	@Override
	public void mutate(T c) {
		final int n = c.length();
		for (int i = 0; i < n; i++) {
			c.set(i, c.get(i) + 2 * radius * ThreadLocalRandom.current().nextDouble() - radius);
		}
	}
	
	@Override
	public UniformMutation<T> split() {
		return new UniformMutation<T>(this);
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public UniformMutation<T> copy() {
		return new UniformMutation<T>(this);
	}
	
	/**
	 * Indicates whether some other object is equal to this one.
	 * The objects are equal if they are the same type of operator
	 * with the same parameters.
	 * @param other the object with which to compare
	 * @return true if and only if the objects are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof UniformMutation)) {
			return false;
		}
		UniformMutation g = (UniformMutation)other;
		return radius==g.radius;
	}
	
	/**
	 * Returns a hash code value for the object.
	 * This method is supported for the benefit of hash 
	 * tables such as those provided by HashMap.
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(radius);
	}
	
	@Override
	public final int length() {
		return 1;
	}
	
	/**
	 * Accesses the current value of radius.
	 * @param i Ignored.
	 * @return The current value of radius.
	 */
	@Override
	public final double get(int i) {
		return radius;
	}
	
	/**
	 * Accesses the current value of radius as an array.  This method implemented
	 * strictly to meet implementation requirements of RealValued interface.
	 * @param values An array to hold the result.  If values is null or
	 * if values.length is not equal 1, then a new array 
	 * is constructed for the result.
	 * @return An array containing the current value of radius.
	 */
	@Override
	public final double[] toArray(double[] values) {
		if (values == null || values.length != 1) values = new double[1];
		values[0] = radius;
		return values;
	}
	
	/**
	 * Sets radius to a specified value.
	 * @param i Ignored.
	 * @param value The new value for radius.
	 */
	@Override
	public final void set(int i, double value) {
		radius = value;
	}
	
	final void internalMutate(T c, double[] old) {
		for (int i = 0; i < old.length; i++) {
			c.set(i, old[i] + 2 * radius * ThreadLocalRandom.current().nextDouble() - radius);
		}
	}
	
	final void internalMutate(T c, double old) {
		c.set(0, old + 2 * radius * ThreadLocalRandom.current().nextDouble() - radius);
	}
	
	final void internalPartialMutation(T c, int[] indexes) {
		for (int j = 0; j < indexes.length; j++) {
			int i = indexes[j];
			c.set(i, c.get(i) + 2 * radius * ThreadLocalRandom.current().nextDouble() - radius);
		}
	}
	
	final void internalPartialMutation(T c, int[] indexes, double[] old) {
		for (int j = 0; j < indexes.length; j++) {
			c.set(indexes[j], old[j] + 2 * radius * ThreadLocalRandom.current().nextDouble() - radius);
		}
	}
	
	private static final class PartialUniformMutation<T extends RealValued> extends UniformMutation<T> {
		
		private final int k;
		private final double p;
		
		PartialUniformMutation(double radius, int k) {
			super(radius);
			this.k = k;
			p = -1;
		}
		
		PartialUniformMutation(double radius, double p) {
			super(radius);
			this.p = p;
			k = 0;
		}
		
		PartialUniformMutation(PartialUniformMutation<T> other) {
			super(other);
			k = other.k;
			p = other.p;
		}
		
		@Override
		public void mutate(T c) {
			if (k >= c.length()) {
				super.mutate(c);
			} else {
				int[] indexes = p < 0 
					? RandomIndexer.sample(c.length(), k, (int[])null) 
					: RandomIndexer.sample(c.length(), p);
				internalPartialMutation(c, indexes);
			}
		}
		
		/**
		 * Indicates whether some other object is equal to this one.
		 * The objects are equal if they are the same type of operator
		 * with the same parameters.
		 * @param other the object with which to compare
		 * @return true if and only if the objects are equal
		 */
		@Override
		public boolean equals(Object other) {
			if (!super.equals(other) || !(other instanceof PartialUniformMutation)) {
				return false;
			}
			PartialUniformMutation g = (PartialUniformMutation)other;
			return k==g.k && p==g.p;
		}
				
		/**
		 * Returns a hash code value for the object.
		 * This method is supported for the benefit of hash 
		 * tables such as those provided by HashMap.
		 * @return a hash code value for this object
		 */
		@Override
		public int hashCode() {
			return 31 * super.hashCode() + (p < 0 ? k : Double.hashCode(p));
		}
		
		@Override
		public PartialUniformMutation<T> split() {
			return new PartialUniformMutation<T>(this);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public PartialUniformMutation<T> copy() {
			return new PartialUniformMutation<T>(this);
		}
	}
}