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
 
package org.cicirello.search.operators;

import org.cicirello.search.concurrent.Splittable;

/**
 * <p>Implement the MutationOperator interface to implement a mutation operator
 * for use in simulated annealing, genetic algorithms, and other evolutionary algorithms,
 * and other metaheuristics, that require a
 * way to generate random neighbors of a candidate solution.</p>  
 *
 * <p>If your mutation
 * operator is one in which the inverse operation (i.e., the operation that
 * reverts an object to its previous state from prior to the mutation) can be
 * implemented without substantially affecting the runtime of the mutation itself,
 * then consider implementing the {@link UndoableMutationOperator} interface 
 * instead, a subinterface of MutationOperator that adds an undo method.</p>
 *
 * <p>On the other hand, if implementing {@link UndoableMutationOperator#undo}
 * would require significant added cost in either time or memory to the 
 * {@link #mutate} method, then consider implementing two versions of your
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
public interface MutationOperator<T> extends Splittable<MutationOperator<T>> {
	
	/**
	 * Mutates a candidate solution to a problem, by randomly modifying its state.
	 * The mutant that is produced is in the local neighborhood of 
	 * the original candidate solution.
	 * 
	 * @param c The candidate solution subject to the mutation.  This method
	 * changes the state of c.
	 */
	void mutate(T c);	
}
