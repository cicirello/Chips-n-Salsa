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
 
package org.cicirello.search.operators;

import org.cicirello.math.rand.RandomIndexer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A HybridMutation enables using multiple mutation operators for the
 * search, such that each time the {@link #mutate} method is called,
 * a randomly chosen mutation operator is applied to the candidate
 * solution.  The random choice of mutation operator is approximately uniform
 * from among the available mutation operators.  This implementation supports
 * the {@link #undo} method.
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a> 
 */
public final class HybridUndoableMutation<T> implements UndoableMutationOperator<T> {
	
	private final ArrayList<UndoableMutationOperator<T>> mutationOps;
	private int last;
	
	/**
	 * Constructs a HybridUndoableMutation from a Collection of MutationOperators.
	 * @param mutationOps A Collection of MutationOperators.
	 * @throws IllegalArgumentException if mutationOps doesn't contain any MutationOperators.
	 */
	public HybridUndoableMutation(Collection<? extends UndoableMutationOperator<T>> mutationOps) {
		if (mutationOps.size() == 0) throw new IllegalArgumentException("Must pass at least 1 UndoableMutationOperator.");
		this.mutationOps = new ArrayList<UndoableMutationOperator<T>>(mutationOps.size());
		for (UndoableMutationOperator<T> op : mutationOps) {
			this.mutationOps.add(op);
		}
		last = -1;
	}
	
	/*
	 * private constructor to support split method
	 */
	private HybridUndoableMutation(HybridUndoableMutation<T> other) {
		mutationOps = new ArrayList<UndoableMutationOperator<T>>(other.mutationOps.size());
		for (UndoableMutationOperator<T> op : other.mutationOps) {
			mutationOps.add(op.split());
		}
		last = -1;
	}
	
	@Override
	public void mutate(T c) {
		mutationOps.get(last = RandomIndexer.nextBiasedInt(mutationOps.size())).mutate(c);
	}
	
	@Override
	public void undo(T c) {
		if (last >= 0) mutationOps.get(last).undo(c);
	}
	
	@Override
	public HybridUndoableMutation<T> split() {
		return new HybridUndoableMutation<T>(this);
	}
}