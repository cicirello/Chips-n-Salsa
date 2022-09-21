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
 
package org.cicirello.search.evo;

import java.util.concurrent.ThreadLocalRandom;

import org.cicirello.util.Copyable;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.operators.reals.GaussianMutation;

/**
 * This is a package-access support class for evolutionary algorithms with 
 * evolvable control parameters.
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class EncodingWithParameters<T extends Copyable<T>> implements Copyable<EncodingWithParameters<T>> {
	
	final T candidate;
	private final RealVector params;
	private final GaussianMutation<RealVector> mutator;
	private final static GaussianMutation<GaussianMutation> mutationMutator = GaussianMutation.createGaussianMutation(0.01, 0.01, 0.2);
	
	EncodingWithParameters(T candidate, int numParams) {
		this(candidate, numParams, 0.1, 1.0);
	}
	
	EncodingWithParameters(T candidate, int numParams, double minRate, double maxRate) {
		this.candidate = candidate;
		double[] initial = new double[numParams];
		for (int i = 0; i < numParams; i++) {
			initial[i] = ThreadLocalRandom.current().nextDouble(minRate, maxRate);
		}
		params = new RealVector(initial);
		mutator = GaussianMutation.createGaussianMutation(ThreadLocalRandom.current().nextDouble(0.05, 0.15), minRate, maxRate);
	}
	
	private EncodingWithParameters(EncodingWithParameters<T> other) {
		candidate = other.candidate.copy();
		params = other.params.copy();
		mutator = other.mutator.copy();
	}
	
	/**
	 * Mutates the parameters.
	 */
	public final void mutate() {
		mutator.mutate(params);
		mutationMutator.mutate(mutator);
	}
	
	/**
	 * Gets the candidate solution.
	 * @return the candidate solution.
	 */
	public final T getCandidate() {
		return candidate;
	}
	
	/**
	 * Gets the vector of parameters.
	 * @return the vector of parameters
	 */
	public final RealVector getParameters() {
		return params;
	}
	
	@Override
	public EncodingWithParameters<T> copy() {
		return new EncodingWithParameters<T>(this);
	}
	
	@Override
	public int hashCode() {
		// hashCode and equals need to be strictly for whether the encapsulated
		// candidates are equal to function properly with the elite sets, and some
		// other stuff within the package.
		return candidate.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		// hashCode and equals need to be strictly for whether the encapsulated
		// candidates are equal to function properly with the elite sets, and some
		// other stuff within the package.
		if (other instanceof EncodingWithParameters) {
			EncodingWithParameters casted = (EncodingWithParameters)other;
			return candidate.equals(casted.candidate);
		}
		return false;
	}
}
