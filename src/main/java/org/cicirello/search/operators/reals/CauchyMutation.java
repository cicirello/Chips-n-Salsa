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
 
package org.cicirello.search.operators.reals;

import org.cicirello.math.rand.RandomVariates;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.RealValued;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.util.Copyable;

/**
 * <p>This class implements Cauchy
 * mutation.  Cauchy mutation is for
 * mutating floating-point values.  This class can be used to mutate
 * objects of any of the classes that implement the {@link RealValued}
 * interface, including both univariate and multivariate function input
 * objects.</p>
 *
 * <p>In a Cauchy mutation, a value v is mutated by adding a randomly
 * generated m such that m is drawn from a Cauchy distribution with location parameter 0 (i.e., median 0)
 * and scale parameter, scale.  It is commonly employed in evolutionary computation
 * when mutating real valued parameters.  It is an alternative to the slightly more common
 * Gaussian mutation (see the {@link GaussianMutation} class).  Gaussian mutation 
 * has better convergence properties, however, due to the heavy-tailed nature of the Cauchy
 * distribution, Cauchy mutation can sometimes escape local optima better than Gaussian mutation
 * (i.e., Cauchy mutation is more likely than Gaussian mutation to make large jumps).</p>
 *
 * <p>This mutation operator also 
 * implements the {@link RealValued} 
 * interface to enable implementation
 * of metaheuristics that mutate their own mutation parameters.  That is, you can pass
 * a CauchyMutation object to the {@link #mutate} method of a CauchyMutation object.</p>
 *
 * <p>To construct a CauchyMutation, you must use one of the factory methods.  See
 * the various {@link #createCauchyMutation} methods.</p>
 *
 * <p>Cauchy mutation was introduced in the following article:<br>
 * H.H. Szu and R.L. Hartley. Nonconvex optimization by fast simulated annealing. 
 * Proceedings of the IEEE, 75(11): 1538–1540, November 1987.</p>
 *
 * @param <T> The specific RealValued type.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class CauchyMutation<T extends RealValued> implements MutationOperator<T>, RealValued, Copyable<CauchyMutation<T>> {
	
	private double scale;
	
	/*
	 * Internal constructor.  Constructs a Cauchy mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param scale The scale parameter of the Cauchy.
	 */
	CauchyMutation(double scale) { 
		this.scale = scale;
	}
	
	/*
	 * internal copy constructor
	 */
	CauchyMutation(CauchyMutation<T> other) {
		scale = other.scale;
	}
	
	/**
	 * Creates a Cauchy mutation operator with scale parameter equal to 1.
	 * @param <T> The specific RealValued type.
	 * @return A Cauchy mutation operator.
	 */
	public static <T extends RealValued> CauchyMutation<T> createCauchyMutation() {
		return new CauchyMutation<T>(1.0);
	}
	
	/**
	 * Creates a Cauchy mutation operator.
	 * @param scale The scale parameter of the Cauchy.
	 * @param <T> The specific RealValued type.
	 * @return A Cauchy mutation operator.
	 */
	public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(double scale) {
		return new CauchyMutation<T>(scale);
	}
	
	/**
	 * Create a Cauchy mutation operator.  
	 * @param scale The scale parameter of the Cauchy mutation.
	 * @param k The number of input variables that the {@link #mutate} 
	 * method changes when called.
	 * The k input variables are chosen uniformly at random from among all subsets of size k.
	 * If there are less than k input variables, then all are mutated.
	 * @param <T> The specific RealValued type.
	 * @return A Cauchy mutation operator
	 * @throws IllegalArgumentException if k &lt; 1
	 */
	public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(double scale, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
		return new PartialCauchyMutation<T>(scale, k);
	}
	
	/**
	 * Create a Cauchy mutation operator.  
	 * @param scale The scale parameter of the Cauchy mutation.
	 * @param p The probability that the {@link #mutate} 
	 * method changes an input variable.
	 * If there are n input variables, then n*p input variables will be mutated on average during
	 * a single call to the {@link #mutate} method.
	 * @param <T> The specific RealValued type.
	 * @return A Cauchy mutation operator
	 * @throws IllegalArgumentException if p &le; 0
	 */
	public static <T extends RealValued> CauchyMutation<T> createCauchyMutation(double scale, double p) {
		if (p <= 0) throw new IllegalArgumentException("p must be positive");
		return p >= 1
			? new CauchyMutation<T>(scale)
			: new PartialCauchyMutation<T>(scale, p);
	}
	
	@Override
	public void mutate(T c) {
		final int n = c.length();
		for (int i = 0; i < n; i++) {
			c.set(i, c.get(i) + RandomVariates.nextCauchy(scale));
		}
	}
	
	@Override
	public CauchyMutation<T> split() {
		return new CauchyMutation<T>(this);
	}
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public CauchyMutation<T> copy() {
		return new CauchyMutation<T>(this);
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
		if (other == null || !(other instanceof CauchyMutation)) return false;
		CauchyMutation g = (CauchyMutation)other;
		return scale==g.scale;
	}
	
	/**
	 * Returns a hash code value for the object.
	 * This method is supported for the benefit of hash 
	 * tables such as those provided by HashMap.
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(scale);
	}
	
	@Override
	public final int length() {
		return 1;
	}
	
	/**
	 * Accesses the current value of scale.
	 * @param i Ignored.
	 * @return The current value of scale.
	 */
	@Override
	public final double get(int i) {
		return scale;
	}
	
	/**
	 * Accesses the current value of scale as an array.  This method implemented
	 * strictly to meet implementation requirements of RealValued interface.
	 * @param values An array to hold the result.  If values is null or
	 * if values.length is not equal 1, then a new array 
	 * is constructed for the result.
	 * @return An array containing the current value of scale.
	 */
	@Override
	public final double[] toArray(double[] values) {
		if (values == null || values.length != 1) values = new double[1];
		values[0] = scale;
		return values;
	}
	
	/**
	 * Sets scale to a specified value.
	 * @param i Ignored.
	 * @param value The new value for scale.
	 */
	@Override
	public final void set(int i, double value) {
		scale = value;
	}
	
	final void internalMutate(T c, double[] old) {
		for (int i = 0; i < old.length; i++) {
			c.set(i, old[i] + RandomVariates.nextCauchy(scale));
		}
	}
	
	final void internalMutate(T c, double old) {
		c.set(0, old + RandomVariates.nextCauchy(scale));
	}
	
	final void internalPartialMutation(T c, int[] indexes) {
		for (int j = 0; j < indexes.length; j++) {
			int i = indexes[j];
			c.set(i, c.get(i) + RandomVariates.nextCauchy(scale));
		}
	}
	
	final void internalPartialMutation(T c, int[] indexes, double[] old) {
		for (int j = 0; j < indexes.length; j++) {
			c.set(indexes[j], old[j] + RandomVariates.nextCauchy(scale));
		}
	}
	
	private static final class PartialCauchyMutation<T extends RealValued> extends CauchyMutation<T> {
		
		private final int k;
		private final double p;
		
		PartialCauchyMutation(double scale, int k) {
			super(scale);
			this.k = k;
			p = -1;
		}
		
		PartialCauchyMutation(double scale, double p) {
			super(scale);
			this.p = p;
			k = 0;
		}
		
		PartialCauchyMutation(PartialCauchyMutation<T> other) {
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
			if (other==null || !(other instanceof PartialCauchyMutation) || !super.equals(other)) {
				return false;
			}
			PartialCauchyMutation g = (PartialCauchyMutation)other;
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
		public PartialCauchyMutation<T> split() {
			return new PartialCauchyMutation<T>(this);
		}
		
		/**
		 * Creates an identical copy of this object.
		 * @return an identical copy of this object
		 */
		@Override
		public PartialCauchyMutation<T> copy() {
			return new PartialCauchyMutation<T>(this);
		}
	}
}