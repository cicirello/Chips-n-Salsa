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
 
package org.cicirello.search.operators.reals;

import org.cicirello.math.rand.RandomVariates;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.RealValued;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.util.Copyable;
import org.cicirello.search.concurrent.Splittable;

/**
 * <p>This class implements Gaussian
 * mutation.  Gaussian mutation is for
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
 * a GaussianMutation object to the {@link #mutate} method of a GaussianMutation object.</p>
 *
 * <p>To construct a GaussianMutation, you must use one of the factory methods.  See
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
 */
public class GaussianMutation<T extends RealValued> implements MutationOperator<T>, RealValued, Copyable<GaussianMutation<T>> {
	
	private double sigma;
	private final InternalMutator<T> m;
	
	/*
	 * Internal constructor.  Constructs a Gaussian mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param sigma The standard deviation of the Gaussian.
	 */
	GaussianMutation(double sigma) { 
		this(sigma, new InternalMutator<T>());
	}
	
	/*
	 * Internal constructor.  Constructs a Gaussian mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param m The internal mutator
	 */
	GaussianMutation(double sigma, InternalMutator<T> m) { 
		this.sigma = sigma;
		this.m = m;
	}
	
	/*
	 * internal copy constructor
	 */
	GaussianMutation(GaussianMutation<T> other) {
		this(other.sigma, other.m.split());
	}
	
	/**
	 * Creates a Gaussian mutation operator with standard deviation equal to 1.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> GaussianMutation<T> createGaussianMutation() {
		return new GaussianMutation<T>(1.0);
	}
	
	/**
	 * Creates a Gaussian mutation operator.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(double sigma) {
		return new GaussianMutation<T>(sigma);
	}
	
	/**
	 * Creates a Gaussian mutation operator, such that the mutate method
	 * constrains each mutated real value to lie in the interval [lowerBound, upperBound].
	 *
	 * @param sigma The standard deviation of the Gaussian.
	 * @param lowerBound A lower bound on the result of a mutation.
	 * @param upperBound An upper bound on the result of a mutation.
	 *
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(double sigma, double lowerBound, double upperBound) {
		if (upperBound < lowerBound) throw new IllegalArgumentException("upperBound must be at least lowerBound");
		return new GaussianMutation<T>(sigma, new ConstrainedMutator<T>(lowerBound, upperBound));
	}
	
	/**
	 * Create a Gaussian mutation operator.  
	 * @param sigma The standard deviation of the Gaussian mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator
	 * @throws IllegalArgumentException if k &lt; 1
	 */
	public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(double sigma, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
		return new GaussianMutation<T>(sigma, new PartialK<T>(k));
	}
	
	/**
	 * Create a Gaussian mutation operator.  
	 * @param sigma The standard deviation of the Gaussian mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input variables will be mutated on average during
	 * a single call to the {@link #mutate} method.
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator
	 * @throws IllegalArgumentException if p &le; 0
	 */
	public static <T extends RealValued> GaussianMutation<T> createGaussianMutation(double sigma, double p) {
		if (p <= 0) throw new IllegalArgumentException("p must be positive");
		if (p >= 1) {
			return new GaussianMutation<T>(sigma);
		}
		return new GaussianMutation<T>(sigma, new PartialP<T>(p));
	}
	
	@Override
	public void mutate(T c) {
		m.mutate(c, sigma);
	}
	
	@Override
	public GaussianMutation<T> split() {
		return new GaussianMutation<T>(this);
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public GaussianMutation<T> copy() {
		return new GaussianMutation<T>(this);
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
		if (other == null || !(other instanceof GaussianMutation)) {
			return false;
		}
		GaussianMutation g = (GaussianMutation)other;
		return sigma==g.sigma && m.equals(g.m);
	}
	
	/**
	 * Returns a hash code value for the object.
	 * This method is supported for the benefit of hash 
	 * tables such as those provided by HashMap.
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(sigma) + 31 * m.hashCode();
	}
	
	@Override
	public final int length() {
		return 1;
	}
	
	/**
	 * Accesses the current value of sigma.
	 * @param i Ignored.
	 * @return The current value of sigma.
	 */
	@Override
	public final double get(int i) {
		return sigma;
	}
	
	/**
	 * Accesses the current value of sigma as an array.  This method implemented
	 * strictly to meet implementation requirements of RealValued interface.
	 * @param values An array to hold the result.  If values is null or
	 * if values.length is not equal 1, then a new array 
	 * is constructed for the result.
	 * @return An array containing the current value of sigma.
	 */
	@Override
	public final double[] toArray(double[] values) {
		if (values == null || values.length != 1) values = new double[1];
		values[0] = sigma;
		return values;
	}
	
	/**
	 * Sets sigma to a specified value.
	 * @param i Ignored.
	 * @param value The new value for sigma.
	 */
	@Override
	public final void set(int i, double value) {
		sigma = value;
	}
	
	/**
	 * Sets sigma to a specified value.
	 * @param values The new value for sigma is in values[0], the rest is ignored.
	 */
	@Override
	public final void set(double[] values) {
		sigma = values[0];
	}
	
	/*
	 * package private so subclasses in same package can access
	 */
	static class InternalMutator<T1 extends RealValued> implements Splittable<InternalMutator<T1>> {
		
		public void mutate(T1 c, double sigma) {
			final int n = c.length();
			for (int i = 0; i < n; i++) {
				c.set(i, c.get(i) + RandomVariates.nextGaussian(sigma));
			}
		}
		
		public void partialMutate(T1 c, double sigma, int[] indexes) {
			for (int i : indexes) {
				c.set(i, c.get(i) + RandomVariates.nextGaussian(sigma));
			}
		}
		
		@Override
		public InternalMutator<T1> split() {
			return this;
		}
		
		@Override
		public boolean equals(Object other) {
			// impossible for other to be null
			return other.getClass() == getClass();
		}
		
		@Override
		public int hashCode() {
			return 100003;
		}
	}
	
	static final class PartialP<T1 extends RealValued> extends InternalMutator<T1> {
			
		private final double p;
		
		PartialP(double p) {
			this.p = p;
		}
		
		@Override
		public void mutate(T1 c, double sigma) {
			partialMutate(c, sigma, RandomIndexer.sample(c.length(), p));
		}
		
		@Override
		public PartialP<T1> split() {
			// no mutable state so should be safe to return this
			return this;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof PartialP) {
				return ((PartialP)other).p == p;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Double.hashCode(p);
		}
	}
	
	static final class PartialK<T1 extends RealValued> extends InternalMutator<T1> {
			
		private final int k;
		
		PartialK(int k) {
			this.k = k;
		}
		
		@Override
		public void mutate(T1 c, double sigma) {
			if (k >= c.length()) {
				super.mutate(c, sigma);
			} else {
				partialMutate(c, sigma, RandomIndexer.sample(c.length(), k, (int[])null));
			}
		}
		
		@Override
		public PartialK<T1> split() {
			// no mutable state so should be safe to return this
			return this;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof PartialK) {
				return ((PartialK)other).k == k;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return k;
		}
	}
	
	static final class ConstrainedMutator<T1 extends RealValued> extends InternalMutator<T1> {
		
		private final double lowerBound;
		private final double upperBound;
		
		ConstrainedMutator(double lowerBound, double upperBound) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}
		
		@Override
		public void mutate(T1 c, double sigma) {
			super.mutate(c, sigma);
			final int n = c.length();
			for (int i = 0; i < n; i++) {
				if (c.get(i) < lowerBound) {
					c.set(i, lowerBound);
				} else if (c.get(i) > upperBound) {
					c.set(i, upperBound);
				}
			}
		}
		
		@Override
		public ConstrainedMutator<T1> split() {
			// no mutable state so should be safe to return this
			return this;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof ConstrainedMutator) {
				ConstrainedMutator casted = (ConstrainedMutator)other;
				return casted.lowerBound == lowerBound && casted.upperBound == upperBound;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return 31 * Double.hashCode(lowerBound) + Double.hashCode(upperBound);
		}
	}
}
