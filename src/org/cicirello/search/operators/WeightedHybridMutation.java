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
 
package org.cicirello.search.operators;

import org.cicirello.math.rand.RandomIndexer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

/**
 * <p>A WeightedHybridMutation enables using multiple mutation operators for the
 * search, such that each time the {@link #mutate} method is called,
 * a randomly chosen mutation operator is applied to the candidate
 * solution.  The random choice of mutation operator is weighted proportionately based on
 * an array of weights passed upon construction.</p>
 *
 * <p>Consider the following weights: w = [ 1, 2, 3].  In this example, the
 * first mutation operator will be used with probability 0.167, the second
 * mutation operator will be used with probability 2/6 = 0.333, and the third 
 * mutation operator will be used with probability 3/6 = 0.5.</p>
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.28.2020 
 */
public final class WeightedHybridMutation<T> implements MutationOperator<T> {
	
	private final ArrayList<MutationOperator<T>> mutationOps;
	private final int[] choice;
	
	/**
	 * Constructs a WeightedHybridMutation from a Collection of MutationOperators.
	 * @param mutationOps A Collection of MutationOperators.
	 * @param weights The array of weights, whose length must be equal to mutationOps.size().
	 * Every element of weights must be greater than 0.
	 * @throws IllegalArgumentException if mutationOps doesn't contain any MutationOperators.
	 * @throws IllegalArgumentException if mutationOps.size() is not equal to weights.length.
	 * @throws IllegalArgumentException if any weights are non-positive.
	 */
	public WeightedHybridMutation(Collection<? extends MutationOperator<T>> mutationOps, int[] weights) {
		if (mutationOps.size() == 0) throw new IllegalArgumentException("Must pass at least 1 MutationOperator.");
		if (mutationOps.size() != weights.length) throw new IllegalArgumentException("Number of weights must be same as number of mutation operators.");
		choice = weights.clone();
		if (choice[0] <= 0) throw new IllegalArgumentException("The weights must be positive.");
		for (int i = 1; i < choice.length; i++) {
			if (choice[i] <= 0) throw new IllegalArgumentException("The weights must be positive.");
			choice[i] = choice[i-1] + choice[i];
		}
		this.mutationOps = new ArrayList<MutationOperator<T>>(mutationOps.size());
		for (MutationOperator<T> op : mutationOps) {
			this.mutationOps.add(op);
		}
	}
	
	/*
	 * private constructor to support split method
	 */
	private WeightedHybridMutation(WeightedHybridMutation<T> other) {
		mutationOps = new ArrayList<MutationOperator<T>>(other.mutationOps.size());
		for (MutationOperator<T> op : other.mutationOps) {
			mutationOps.add(op.split());
		}
		choice = other.choice.clone();
	}
	
	@Override
	public void mutate(T c) {
		int value = RandomIndexer.nextInt(choice[choice.length-1]);
		int i = Arrays.binarySearch(choice, value);
		if (i < 0) i = -(i + 1);
		else i++;
		mutationOps.get(i).mutate(c);
	}
	
	@Override
	public WeightedHybridMutation<T> split() {
		return new WeightedHybridMutation<T>(this);
	}
}