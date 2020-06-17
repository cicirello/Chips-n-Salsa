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
 
package org.cicirello.search.operators.integers;

import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.representations.IntegerValued;

/**
 * This mutation operator (supporting the undo operation) 
 * is for integer valued representations, and replaces an
 * integer value with a different random integer value from the domain.  The domain
 * is specified with an interval: [a, b].  The parameter p specifies the probability
 * of mutating an integer.  E.g., if the {@link IntegerValued} object undergoing
 * mutation has n integers, then on average the {@link #mutate} method will mutate k=n*p
 * of those integers. The k integers chosen for mutation are chosen uniformly at random.
 * For each of those k integers, the new value is chosen uniformly at random from [a, b]
 * but excluding its current value.  For example, let [a, b]=[0,4], and consider mutating
 * an integer v whose value is currently v=3.  The new value for v will be chosen uniformly at random
 * from the set {0, 1, 2, 4}.  Note that when a=0 and b=1, this mutation operator
 * becomes equivalent to the traditional bit-flip mutation commonly used in genetic algorithms
 * when solutions are represented as bit strings, although use of this class and the 
 * {@link IntegerValued} class for that purpose is not recommended as there are much more efficient
 * ways of representing strings of bits (e.g., using bit level operators).  
 *
 * @param <T> The specific IntegerValued type.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.5.2020
 */
public final class UndoableRandomValueChangeMutation<T extends IntegerValued> extends RandomValueChangeMutation<T> implements UndoableMutationOperator<T> {
	
	private int[] oldA;
	private int old;
	
	/**
	 * Constructs a UndoableRandomValueChangeMutation operator that always 
	 * mutates exactly one integer from the IntegerValued.
	 * If the IntegerValued is a univariate, then it mutates the
	 * one and only one integer.  If it is a multivariate, then one integer parameter
	 * is chosen for mutation uniformly at random.
	 * @param a The lower bound of the domain from which to choose random values.
	 * @param b The upper bound of the domain from which to choose random values.  b must be
	 * greater than a (i.e., there must be at least 2 values in the domain).
	 * @throws IllegalArgumentException if a &ge; b.
	 */
	public UndoableRandomValueChangeMutation(int a, int b) {
		super(a, b, 0.0, 1);
	}
	
	/**
	 * Constructs a UndoableRandomValueChangeMutation operator.
	 * If the IntegerValued undergoing mutation contains n integer parameters, 
	 * then this mutation operator will mutate n*p of those integers on average during
	 * calls to {@link #mutate}.  Since this is a randomized process, it is possible
	 * that no integers will be mutated during a call to mutate (e.g., if p is low
	 * relative to n).
	 * @param a The lower bound of the domain from which to choose random values.
	 * @param b The upper bound of the domain from which to choose random values.  b must be
	 * greater than a (i.e., there must be at least 2 values in the domain).
	 * @param p The probability of mutating an individual integer.  Negative p are treated as p=0.
	 * If p is greater than 1, it is treated as p=1.
	 */
	public UndoableRandomValueChangeMutation(int a, int b, double p) {
		super(a, b, p, 0);
	}
	
	/*
	 * internal copy constructor to support split method
	 */
	UndoableRandomValueChangeMutation(UndoableRandomValueChangeMutation<T> other) {
		super(other);
	}
	
	/**
	 * Constructs a UndoableRandomValueChangeMutation operator.
	 * If the IntegerValued undergoing mutation contains n integer parameters, 
	 * then this mutation operator will mutate n*p of those integers on average during
	 * calls to {@link #mutate}, but will definitely mutate at least k of them.  
	 * Use this constructor if you want to insure that every call to {@link #mutate} changes 
	 * the IntegerValued undergoing mutation by specifying a minimum k to mutate.
	 * @param a The lower bound of the domain from which to choose random values.
	 * @param b The upper bound of the domain from which to choose random values.  b must be
	 * greater than a (i.e., there must be at least 2 values in the domain).
	 * @param p The probability of mutating an individual integer.  Negative p are treated as p=0.
	 * If p is greater than 1, it is treated as p=1.
	 * @param k The minimum number of integer parameters of the IntegerValued undergoing
	 * mutation to mutate during calls to the {@link #mutate} method.  Negative k are treated as k=0.
	 * @throws IllegalArgumentException if a &ge; b or if p is negative.
	 */
	public UndoableRandomValueChangeMutation(int a, int b, double p, int k) {
		super(a, b, p, k);
	}
	
	@Override
	public void mutate(T c) {
		if (c.length() > 0) {
			if (oldA == null || oldA.length < c.length()) oldA = new int[c.length()];
			restorableMutate(c, oldA);
		}
	}
	
	@Override
	public void undo(T c) {
		if (c.length() > 0) {
			restore(c, oldA);
		}
	}
	
	@Override
	public UndoableRandomValueChangeMutation<T> split() {
		return new UndoableRandomValueChangeMutation<T>(this);
	}
}