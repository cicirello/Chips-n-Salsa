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

package org.cicirello.search.operators.integers;

import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.representations.IntegerValued;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.math.rand.RandomSampler;
import org.cicirello.math.rand.RandomVariates;

/**
 * This mutation operator is for integer valued representations, and replaces an
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
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class RandomValueChangeMutation<T extends IntegerValued> implements MutationOperator<T> {
	
	private final double p;
	private final int a;
	private final int b;
	private final int range;
	private final int min_k;
	private int[] indexes;
	private int lastK;
	
	/**
	 * Constructs a RandomValueChangeMutation operator that always 
	 * mutates exactly one integer from the IntegerValued.
	 * If the IntegerValued is a univariate, then it mutates the
	 * one and only one integer.  If it is a multivariate, then one integer parameter
	 * is chosen for mutation uniformly at random.
	 * @param a The lower bound of the domain from which to choose random values.
	 * @param b The upper bound of the domain from which to choose random values.  b must be
	 * greater than a (i.e., there must be at least 2 values in the domain).
	 * @throws IllegalArgumentException if a &ge; b.
	 */
	public RandomValueChangeMutation(int a, int b) {
		this(a, b, 0.0, 1);
	}
	
	/**
	 * Constructs a RandomValueChangeMutation operator.
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
	public RandomValueChangeMutation(int a, int b, double p) {
		this(a, b, p, 0);
	}
	
	/**
	 * Constructs a RandomValueChangeMutation operator.
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
	public RandomValueChangeMutation(int a, int b, double p, int k) {
		range = b - a + 1;
		if (range <= 1) throw new IllegalArgumentException("b must be greater than a");
		this.a = a;
		this.b = b;
		this.p = p <= 0.0 ? 0.0 : (p >= 1.0 ? 1.0 : p);
		min_k = k <= 0 ? 0 : k;
	}
	
	/*
	 * internal copy constructor to support split method
	 */
	RandomValueChangeMutation(RandomValueChangeMutation<T> other) {
		a = other.a;
		b = other.b;
		p = other.p;
		min_k = other.min_k;
		range = other.range;
	}
	
	@Override
	public void mutate(T c) {
		if (c.length() == 0) return;
		int min = c.length() < min_k ? c.length() : min_k;
		lastK = p > 0 ? RandomVariates.nextBinomial(c.length(), p) : min;
		if (lastK < min) lastK = min;
		indexes = RandomSampler.sample(c.length(), lastK, indexes);
		for (int i = 0; i < lastK; i++) {
			int v = a + RandomIndexer.nextInt(range-1);
			if (v >= c.get(indexes[i])) v++;
			c.set(indexes[i], v);
		}
	}
	
	@Override
	public RandomValueChangeMutation<T> split() {
		return new RandomValueChangeMutation<T>(a, b, p, min_k);
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
		if (other == null || !(other instanceof RandomValueChangeMutation)) {
			return false;
		}
		RandomValueChangeMutation m = (RandomValueChangeMutation)other;
		return m.a==a && m.b==b && m.p==p && m.min_k==min_k;
	}
	
	/**
	 * Returns a hash code value for the object.
	 * This method is supported for the benefit of hash 
	 * tables such as those provided by HashMap.
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return 31 * (31 * (31 * Double.hashCode(p) + a) + b) + min_k;
	}
	
	/*
	 * internal package-private helper in support of undo method in Undoable version of operator.
	 */
	void restorableMutate(T c, int[] old) {
		int min = c.length() < min_k ? c.length() : min_k;
		lastK = p > 0 ? RandomVariates.nextBinomial(c.length(), p) : min;
		if (lastK < min) lastK = min;
		indexes = RandomSampler.sample(c.length(), lastK, indexes);
		for (int i = 0; i < lastK; i++) {
			int v = a + RandomIndexer.nextInt(range-1);
			old[i] = c.get(indexes[i]);
			if (v >= old[i]) v++;
			c.set(indexes[i], v);
		}
	 }
	
	/*
	 * internal package-private helper in support of undo method in Undoable version of operator.
	 */
	void restore(T c, int[] old) {
		for (int i = 0; i < lastK; i++) {
			c.set(indexes[i], old[i]);
		}
	}
}