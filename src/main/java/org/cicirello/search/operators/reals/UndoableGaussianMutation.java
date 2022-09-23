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
 */
public final class UndoableGaussianMutation<T extends RealValued> extends GaussianMutation<T> implements UndoableMutationOperator<T> {
	
	private double[] previous;
	private double old;
	
	/*
	 * Internal constructor.  Constructs a Gaussian mutation operator supporting the undo operation.
	 * Otherwise, must use the factory methods.
	 * @param sigma The standard deviation of the Gaussian.
	 */
	UndoableGaussianMutation(double sigma) { 
		super(sigma);
	}
	
	/*
	 * Internal constructor.  Constructs a Gaussian mutation operator.
	 * Otherwise, must use the factory methods.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param m The internal mutator
	 */
	UndoableGaussianMutation(double sigma, InternalMutator<T> m) { 
		super(sigma, m);
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
	 * Creates a Gaussian mutation operator that supports the undo operation, such that the mutate method
	 * constrains each mutated real value to lie in the interval [lowerBound, upperBound].
	 *
	 * @param sigma The standard deviation of the Gaussian.
	 * @param lowerBound A lower bound on the result of a mutation.
	 * @param upperBound An upper bound on the result of a mutation.
	 *
	 * @param <T> The specific RealValued type.
	 * @return A Gaussian mutation operator.
	 */
	public static <T extends RealValued> UndoableGaussianMutation<T> createGaussianMutation(double sigma, double lowerBound, double upperBound) {
		if (upperBound < lowerBound) throw new IllegalArgumentException("upperBound must be at least lowerBound");
		return new UndoableGaussianMutation<T>(sigma, new ConstrainedMutator<T>(lowerBound, upperBound));
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
		return new UndoableGaussianMutation<T>(sigma, new PartialK<T>(k));
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
		if (p >= 1) {
			return new UndoableGaussianMutation<T>(sigma);
		}
		return new UndoableGaussianMutation<T>(sigma, new PartialP<T>(p));
	}
	
	@Override
	public void mutate(T c) {
		if (c.length() > 1) previous = c.toArray(previous);
		else if (c.length() == 1) old = c.get(0);
		super.mutate(c);
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
}
