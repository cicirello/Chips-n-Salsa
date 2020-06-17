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
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>A WeightedHybridMutation enables using multiple mutation operators for the
 * search, such that each time the {@link #mutate} method is called,
 * a randomly chosen mutation operator is applied to the candidate
 * solution.  The random choice of mutation operator is weighted proportionately based on
 * an array of weights passed upon construction. This implementation supports
 * the {@link #undo} method.</p>
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
public final class WeightedHybridUndoableMutation<T> implements UndoableMutationOperator<T> {
	
	private final ArrayList<UndoableMutationOperator<T>> mutationOps;
	private int last;
	private final int[] choice;
	
	/**
	 * Constructs a WeightedHybridUndoableMutation from a Collection of UndoableMutationOperator.
	 * @param mutationOps A Collection of UndoableMutationOperator.
	 * @param weights The array of weights, whose length must be equal to mutationOps.size().
	 * Every element of weights must be greater than 0.
	 * @throws IllegalArgumentException if mutationOps doesn't contain any UndoableMutationOperator.
	 * @throws IllegalArgumentException if mutationOps.size() is not equal to weights.length.
	 * @throws IllegalArgumentException if any weights are non-positive.
	 */
	public WeightedHybridUndoableMutation(Collection<? extends UndoableMutationOperator<T>> mutationOps, int[] weights) {
		if (mutationOps.size() == 0) throw new IllegalArgumentException("Must pass at least 1 UndoableMutationOperator.");
		if (mutationOps.size() != weights.length) throw new IllegalArgumentException("Number of weights must be same as number of mutation operators.");
		choice = weights.clone();
		for (int i = 1; i < choice.length; i++) {
			if (choice[i-1] <= 0) throw new IllegalArgumentException("The weights must be positive.");
			choice[i] = choice[i-1] + choice[i];
		}
		if (choice[choice.length-1] <= 0) throw new IllegalArgumentException("The weights must be positive.");
		this.mutationOps = new ArrayList<UndoableMutationOperator<T>>(mutationOps.size());
		for (UndoableMutationOperator<T> op : mutationOps) {
			this.mutationOps.add(op);
		}
		last = -1;
	}
	
	/*
	 * private constructor to support split method
	 */
	private WeightedHybridUndoableMutation(WeightedHybridUndoableMutation<T> other) {
		mutationOps = new ArrayList<UndoableMutationOperator<T>>(other.mutationOps.size());
		for (UndoableMutationOperator<T> op : other.mutationOps) {
			mutationOps.add(op.split());
		}
		last = -1;
		choice = other.choice.clone();
	}
	
	@Override
	public void mutate(T c) {
		int value = RandomIndexer.nextInt(choice[choice.length-1]);
		last = Arrays.binarySearch(choice, value);
		if (last < 0) last = -(last + 1);
		else last++;
		mutationOps.get(last).mutate(c);
	}
	
	@Override
	public void undo(T c) {
		if (last >= 0) mutationOps.get(last).undo(c);
	}
	
	@Override
	public WeightedHybridUndoableMutation<T> split() {
		return new WeightedHybridUndoableMutation<T>(this);
	}
}