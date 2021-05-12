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

import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.representations.RealValued;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements Gaussian
 * mutation with support for the {@link #undo} method.  Gaussian mutation is for
 * mutating floating-point values.  This class can be used to mutate
 * objects of any of the classes that implement the {@link RealValued}
 * interface, including both univariate and multivariate function input
 * objects.</p>
 *
 * <p>In a Gaussian mutation, a value v is mutated by adding a randomly
 * generated m such that m is drawn from a Gaussian distribution with mean 0
 * and standard deviation sigma.  It is commonly employed in Evolution Strategies
 * when mutating real valued parameters.</p>
 *
 * <p>This mutation operator also 
 * implements the {@link RealValued} 
 * interface to enable implementation
 * of metaheuristics that mutate their own mutation parameters.  That is, you can pass
 * an UndoableGaussianMutation object to the {@link #mutate} method of a UndoableGaussianMutation object.</p>
 *
 * <p>To construct an UndoableGaussianMutation, you must use one of the factory methods.  See
 * the various {@link #createGaussianMutation} methods.</p>
 *
 * <p>Gaussian mutation was introduced in the following article:<br>
 * Hinterding, R. 1995. Gaussian mutation and self-adaption for numeric 
 * genetic algorithms. In IEEE CEC. IEEE Press. 384–389.</p>
 *
 * @param <T> The specific RealValued type.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.12.2021
 */
public class UndoableGaussianMutation<T extends RealValued> extends GaussianMutation<T> implements UndoableMutationOperator<T> {
	
	double[] previous;
	double old;
	
	/*
	 * Internal constructor.  Constructs a Gaussian mutation operator supporting the undo operation.
	 * Otherwise, must use the factory methods.
	 * @param sigma The standard deviation of the Gaussian.
	 */
	UndoableGaussianMutation(double sigma) { 
		super(sigma);
	}
	
	/*
	 * internal copy constructor: not a true copy... doesn't copy state related to undo method
	 */
	UndoableGaussianMutation(UndoableGaussianMutation<T> other) {
		super(other);
	}
	
	/**
	 * Creates a Gaussian mutation operator with standard deviation equal to 1
	 * that supports the undo operation.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> UndoableGaussianMutation<T> createGaussianMutation() {
		return new UndoableGaussianMutation<T>(1.0);
	}
	
	/**
	 * Creates a Gaussian mutation operator that supports the undo operation.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> UndoableGaussianMutation<T> createGaussianMutation(double sigma) {
		return new UndoableGaussianMutation<T>(sigma);
	}
	
	/**
	 * Create a Gaussian mutation operator that supports the undo operation.  
	 * @param sigma The standard deviation of the Gaussian mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator
	 * @throws IllegalArgumentException if k &lt; 1
	 */
	public static <T extends RealValued> UndoableGaussianMutation<T> createGaussianMutation(double sigma, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
		return new UndoablePartialGaussianMutation<T>(sigma, k);
	}
	
	/**
	 * Create a Gaussian mutation operator that supports the undo operation.  
	 * @param sigma The standard deviation of the Gaussian mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input variables will be mutated on average during
	 * a single call to the {@link #mutate} method.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator
	 * @throws IllegalArgumentException if p &le; 0
	 */
	public static <T extends RealValued> UndoableGaussianMutation<T> createGaussianMutation(double sigma, double p) {
		if (p <= 0) throw new IllegalArgumentException("p must be positive");
		return p >= 1
			? new UndoableGaussianMutation<T>(sigma)
			: new UndoablePartialGaussianMutation<T>(sigma, p);
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
	public UndoableGaussianMutation<T> split() {
		return new UndoableGaussianMutation<T>(this);
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public UndoableGaussianMutation<T> copy() {
		return new UndoableGaussianMutation<T>(this);
	}
	
	@Override
	public boolean equals(Object other) {
		return super.equals(other) && other instanceof UndoableGaussianMutation;
	}
	
	private static final class UndoablePartialGaussianMutation<T extends RealValued> extends UndoableGaussianMutation<T> {
		
		private final int k;
		private final double p;
		private int[] indexes;
		
		UndoablePartialGaussianMutation(double sigma, int k) {
			super(sigma);
			this.k = k;
			p = -1;
		}
		
		UndoablePartialGaussianMutation(double sigma, double p) {
			super(sigma);
			this.p = p;
			k = 0;
		}
		
		UndoablePartialGaussianMutation(UndoablePartialGaussianMutation<T> other) {
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
			if (!super.equals(other) || !(other instanceof UndoablePartialGaussianMutation)) {
				return false;
			}
			UndoablePartialGaussianMutation g = (UndoablePartialGaussianMutation)other;
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
		public UndoablePartialGaussianMutation<T> split() {
			return new UndoablePartialGaussianMutation<T>(this);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public UndoablePartialGaussianMutation<T> copy() {
			return new UndoablePartialGaussianMutation<T>(this);
		}
	}
	
	
	
	
}