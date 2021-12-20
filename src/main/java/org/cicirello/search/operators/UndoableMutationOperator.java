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

/**
 * <p>Implement the UndoableMutationOperator interface to implement a mutation operator
 * for use in simulated annealing, and other metaheuristics, that require a
 * way to generate random neighbors of a candidate solution, and which supports
 * an undo method.</p>  
 *
 * <p>The purpose of this subinterface is to enable 
 * efficient implementation of metaheuristics
 * such as simulated annealing, which iteratively generate random neighbors moving to
 * some and not moving to others.  When the search chooses not to keep a neighbor,
 * it needs an efficient way to return to the previous state.  Without an undo method,
 * it would need to save a copy of the original.  Generating a copy of a candidate
 * solution c is likely an operation whose cost is linear in the size of c, while
 * in many cases it may be possible to implement undo in constant time.</p>
 *
 * <p>If your mutation
 * operator is one in which the inverse operation (i.e., the operation that
 * reverts an object to its previous state from prior to the mutation) can be
 * implemented without substantially affecting the runtime of the mutation itself,
 * then implement the UndoableMutationOperator interface.</p>
 *
 * <p>On the other hand, if implementing {@link #undo}
 * would require significant added cost in either time or memory to the 
 * {@link MutationOperator#mutate} method, then consider implementing two versions of your
 * mutation operator, one that implements MutationOperator and a second that implements
 * {@link UndoableMutationOperator}.  In this way, you can use the first version
 * with metaheuristics that do not utilize the undo method, and the second for those
 * that do.</p>
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface UndoableMutationOperator<T> extends MutationOperator<T> {
	
	/**
	 * <p>Returns a candidate solution to its previous state prior to the
	 * most recent mutation performed.</p>  
	 * <p>For example, consider the following.
	 * Let c' be the current state of c.  Let c'' be the state of c after mutate(c);
	 * If we then call undo(c), the state of c should revert back to c'.</p>
	 * <p>The behavior of undo is undefined if c is altered by some other process between
	 * the calls to mutate and undo.  The behavior is also undefined if a different
	 * candidate is given to undo then the last given to mutate.  For example,
	 * if the following two statements are executed, mutate(c); undo(d);, the effect
	 * on d is undefined as it wasn't the most recently mutated candidate solution.</p>
	 *
	 * @param c The candidate solution to revert.
	 */
	void undo(T c);
	
	@Override
	UndoableMutationOperator<T> split();
}