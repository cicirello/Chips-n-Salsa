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

import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.representations.RealValued;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements a uniform
 * mutation with support for the {@link #undo} method.  Uniform mutation is for
 * mutating floating-point values.  This class can be used to mutate
 * objects of any of the classes that implement the {@link RealValued}
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
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.10.2020
 */
public class UndoableUniformMutation<T extends RealValued> extends UniformMutation<T> implements UndoableMutationOperator<T> {
	
	double[] previous;
	double old;
	
	/*
	 * Internal constructor.  Constructs a Uniform mutation operator supporting the undo operation.
	 * Otherwise, must use the factory methods.
	 * @param radius The radius parameter of the Uniform.
	 */
	UndoableUniformMutation(double radius) { 
		super(radius);
	}
	
	/*
	 * internal copy constructor: not a true copy... doesn't copy state related to undo method
	 */
	UndoableUniformMutation(UndoableUniformMutation<T> other) {
		super(other);
	}
	
	/**
	 * Creates a Uniform mutation operator with radius parameter equal to 1
	 * that supports the undo operation.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator.
	 */
	public static <T extends RealValued> UndoableUniformMutation<T> createUniformMutation() {
		return new UndoableUniformMutation<T>(1.0);
	}
	
	/**
	 * Creates a Uniform mutation operator that supports the undo operation.
	 * @param radius The radius parameter of the Uniform.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator.
	 */
	public static <T extends RealValued> UndoableUniformMutation<T> createUniformMutation(double radius) {
		return new UndoableUniformMutation<T>(radius);
	}
	
	/**
	 * Create a Uniform mutation operator that supports the undo operation.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator
	 */
	public static <T extends RealValued> UndoableUniformMutation<T> createUniformMutation(double radius, int k) {
		return new UndoablePartialUniformMutation<T>(radius, k);
	}
	
	/**
	 * Create a Uniform mutation operator that supports the undo operation.  
	 * @param radius The radius parameter of the Uniform mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input variables will be mutated on average during
	 * a single call to the {@link #mutate} method.
	 * @param <T> The specific RealValued type.
	 * @return A Uniform mutation operator
	 */
	public static <T extends RealValued> UndoableUniformMutation<T> createUniformMutation(double radius, double p) {
		return p >= 1
			? new UndoableUniformMutation<T>(radius)
			: new UndoablePartialUniformMutation<T>(radius, p);
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
	 * @return an identical copy of this object
	 */
	@Override
	public UndoableUniformMutation<T> copy() {
		return new UndoableUniformMutation<T>(this);
	}
	
	private static final class UndoablePartialUniformMutation<T extends RealValued> extends UndoableUniformMutation<T> {
		
		private final int k;
		private final double p;
		private int[] indexes;
		
		UndoablePartialUniformMutation(double radius, int k) {
			super(radius);
			this.k = k < 0 ? 0 : k;
			p = -1;
		}
		
		UndoablePartialUniformMutation(double radius, double p) {
			super(radius);
			this.p = p < 0 ? 0 : (p > 1 ? 1 : p);
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
				indexes = p < 0 
					? RandomIndexer.sample(c.length(), k, indexes) 
					: RandomIndexer.sample(c.length(), p);
				if (previous == null || previous.length < indexes.length) {
					previous = new double[indexes.length];
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
		 * Indicates whether some other object is equal to this one.
		 * The objects are equal if they are the same type of operator
		 * with the same parameters.
		 * @param other the object with which to compare
		 * @return true if and only if the objects are equal
		 */
		@Override
		public boolean equals(Object other) {
			if (!super.equals(other)) return false;
			UndoablePartialUniformMutation g = (UndoablePartialUniformMutation)other;
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
		public UndoablePartialUniformMutation<T> split() {
			return new UndoablePartialUniformMutation<T>(this);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public UndoablePartialUniformMutation<T> copy() {
			return new UndoablePartialUniformMutation<T>(this);
		}
	}
	
	
	
	
}