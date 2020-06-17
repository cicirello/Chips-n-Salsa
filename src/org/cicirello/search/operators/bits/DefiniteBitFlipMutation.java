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
 
package org.cicirello.search.operators.bits;

import org.cicirello.search.representations.BitVector;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>DefiniteBitFlipMutation implements a variation of Bit Flip Mutation.
 * The form of bit flip mutation commonly used in genetic algorithms 
 * (and implemented in the class {@link BitFlipMutation}) is not guaranteed
 * to change any bits during a mutation.  For a metaheuristic that operates on a single
 * solution rather than a population of solutions, such as simulated annealing and hill climbers,
 * where we might use a mutation operator to generate neighbors, then we will want mutation to
 * always make some change.  The DefiniteBitFlipMutation class is a variation of the classic
 * bit flip that guarantees at least 1 bit will be flipped during each invocation of the mutation
 * operator.</p>
 *
 * <p>Genetic Algorithm style Bit Flip Mutation: 
 * In a bit flip mutation, each bit is flipped with probability M,
 * known as the mutation rate.  Flipping a bit means changing it to 0 if it is currently
 * a 1, or changing it to 1 if it is currently a 0.  If the length of the BitVector is
 * N, then the expected number of bits flipped during a single mutation operation 
 * is NM.  However, there is no guarantee that any bits will be flipped during a genetic algorithm
 * style bit flip mutation.  This behavior is fine for genetic algorithms, but may be less
 * than desirable for other metaheuristics, such as those that operate on a single candidate
 * solution rather than a population of them.</p>  
 *
 * <p>Definite Bit Flip Mutation: This class does not implement the genetic algorithm style
 * bit flip mutation.  Instead, it implements a variation of it that we call Definite Bit Flip,
 * which guarantees that at least 1 bit will be flipped during a call to the {@link #mutate} method.
 * Instead of a mutation parameter, the Definite Bit Flip Mutation uses a parameter B which is an
 * upper bound on the number of bits that can be flipped during a single call to the {@link #mutate} method.
 * When the {@link #mutate} method is called, the mutation operator picks a number of bits to flip, f,
 * uniformly at random from the interval [1, B].  It then flips f randomly selected bits, where all
 * combinations of f bits are equally likely.  The expected number of bits flipped during a single
 * call to the {@link #mutate} method is (1+B)/2.</p> 
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 4.27.2020
 */
public final class DefiniteBitFlipMutation implements UndoableMutationOperator<BitVector>, IterableMutationOperator<BitVector> {
	
	private final int b;
	private int[] flipped;
	
	/**
	 * Constructs a DefiniteBitFlipMutation operator.
	 * @param b The maximum number of bits to flip during a single call to the {@link #mutate} method.
	 * The number of bits flipped during each call to {@link #mutate} method is chosen uniformly at random
	 * from the interval [1, b].
	 * @throws IllegalArgumentException if b is less than 1.
	 */
	public DefiniteBitFlipMutation(int b) {
		if (b < 1) throw new IllegalArgumentException("b must be at least 1");
		this.b = b;
	}
	
	/*
	 * internal copy constructor
	 */
	private DefiniteBitFlipMutation(DefiniteBitFlipMutation other) {
		b = other.b;
	}
	
	@Override
	public void mutate(BitVector c) {
		flipped = RandomIndexer.sample(c.length(), RandomIndexer.nextBiasedInt(min(b,c.length())) + 1, (int[])null);
		for (int i = 0; i < flipped.length; i++) {
			c.flip(flipped[i]);
		}
	}
	
	@Override
	public void undo(BitVector c) {
		if (flipped != null) {
			for (int i = 0; i < flipped.length; i++) {
				c.flip(flipped[i]);
			}
		}
	}
	
	@Override
	public DefiniteBitFlipMutation split() {
		return new DefiniteBitFlipMutation(this);
	}
	
	@Override
	public MutationIterator iterator(BitVector c) {
		return new BitFlipIterator(c, min(b, c.length()));
	}
	
	
	private static int min(int x, int y) {
		return x < y ? x : y;
	}
}