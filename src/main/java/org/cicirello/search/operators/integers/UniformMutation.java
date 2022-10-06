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

package org.cicirello.search.operators.integers;

import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.IntegerValued;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.math.rand.RandomSampler;
import org.cicirello.util.Copyable;

/**
 * <p>This class implements a uniform
 * mutation for
 * mutating integer values.  This class can be used to mutate
 * objects of any of the classes that implement the 
 * {@link IntegerValued}
 * interface, including both univariates and multivariates.</p>
 *
 * <p>In the form of uniform mutation implemented by this class,
 * a value v is mutated by adding to it a randomly
 * generated integer m such that m is drawn uniformly at random from the interval [-radius, radius].</p>
 *
 * <p>This mutation operator also 
 * implements the {@link IntegerValued} 
 * interface to enable implementation
 * of metaheuristics that mutate their own mutation parameters.  That is, you can pass
 * a UniformMutation object to the {@link #mutate} method of a UniformMutation object
 * to mutate its radius.</p>
 *
 * <p>To construct a UniformMutation, you must use one of the factory methods.  See
 * the various {@link #createUniformMutation} methods.</p>
 *
 * @param <T> The specific IntegerValued type.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class UniformMutation<T extends IntegerValued> implements MutationOperator<T>, IntegerValued, Copyable<UniformMutation<T>> {
	
	private int radius;
	
	/*
	 * Internal constructor.  Constructs a Uniform mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param radius The radius parameter of the Uniform.
	 */
	UniformMutation(int radius) { 
		this.radius = radius;
	}
	
	/*
	 * internal copy constructor
	 */
	UniformMutation(UniformMutation<T> other) {
		radius = other.radius;
	}
	
	/**
	 * Creates a Uniform mutation operator.
	 * @param radius The radius parameter of the Uniform.
	 * @param <T> The specific IntegerValued type.
	 * @return A Uniform mutation operator.
	 */
	public static <T extends IntegerValued> UniformMutation<T> createUniformMutation(int radius) {
		return new UniformMutation<T>(Math.abs(radius));
	}
	
	/**
	 * Create a Uniform mutation operator.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific IntegerValued type.
	 * @return A Uniform mutation operator
	 * @throws IllegalArgumentException if k &lt; 1
	 */
	public static <T extends IntegerValued> UniformMutation<T> createUniformMutation(int radius, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
		return new PartialUniformMutation<T>(Math.abs(radius), k);
	}
	
	/**
	 * Create a Uniform mutation operator.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input 
	 * variables will be mutated on average during
	 * a single call to the {@link #mutate} method. A value of
	 * p &gt; 1 is treated as p = 1.
	 * @param <T> The specific IntegerValued type.
	 * @return A Uniform mutation operator
	 * @throws IllegalArgumentException if p &le; 0
	 */
	public static <T extends IntegerValued> UniformMutation<T> createUniformMutation(int radius, double p) {
		if (p <= 0) throw new IllegalArgumentException("p must be positive");
		return p >= 1
			? new UniformMutation<T>(Math.abs(radius))
			: new PartialUniformMutation<T>(Math.abs(radius), p);
	}
	
	@Override
	public void mutate(T c) {
		final int n = c.length();
		for (int i = 0; i < n; i++) {  
			c.set(i, c.get(i) + RandomIndexer.nextInt(radius + radius + 1) - radius);
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
		if (other == null || !(other instanceof UniformMutation)) return false;
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
		return radius;
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
	public final int get(int i) {
		return radius;
	}
	
	/**
	 * Accesses the current value of radius as an array.  This method implemented
	 * strictly to meet implementation requirements of IntegerValued interface.
	 * @param values An array to hold the result.  If values is null or
	 * if values.length is not equal 1, then a new array 
	 * is constructed for the result.
	 * @return An array containing the current value of radius.
	 */
	@Override
	public final int[] toArray(int[] values) {
		if (values==null || values.length != 1) values = new int[1];
		values[0] = radius;
		return values;
	}
	
	/**
	 * Sets radius to a specified value.
	 * @param i Ignored.
	 * @param value The new value for radius.
	 */
	@Override
	public final void set(int i, int value) {
		radius = value;
	}
	
	final void internalMutate(T c, int[] old) {
		for (int i = 0; i < old.length; i++) {
			c.set(i, old[i] + RandomIndexer.nextInt(radius + radius + 1) - radius);
		}
	}
	
	final void internalMutate(T c, int old) {
		c.set(0, old + RandomIndexer.nextInt(radius + radius + 1) - radius);
	}
	
	final void internalPartialMutation(T c, int[] indexes) {
		for (int j = 0; j < indexes.length; j++) {
			int i = indexes[j];
			c.set(i, c.get(i) + RandomIndexer.nextInt(radius + radius + 1) - radius);
		}
	}
	
	final void internalPartialMutation(T c, int[] indexes, int[] old) {
		for (int j = 0; j < indexes.length; j++) {
			c.set(indexes[j], old[j] + RandomIndexer.nextInt(radius + radius + 1) - radius);
		}
	}
	
	private static final class PartialUniformMutation<T extends IntegerValued> extends UniformMutation<T> {
		
		private final int k;
		private final double p;
		
		PartialUniformMutation(int radius, int k) {
			super(radius);
			this.k = k;
			p = -1;
		}
		
		PartialUniformMutation(int radius, double p) {
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
					? RandomSampler.sample(c.length(), k, (int[])null) 
					: RandomSampler.sample(c.length(), p);
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