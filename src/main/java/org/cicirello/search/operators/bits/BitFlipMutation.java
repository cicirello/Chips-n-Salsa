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
import org.cicirello.math.rand.RandomIndexer;

/**
 * <p>This class implements Bit Flip Mutation, the mutation operator commonly used
 * in genetic algorithms, but which can also be used with other metaheuristic search
 * algorithms such as simulated annealing to generate random neighbors.</p>
 *
 * <p>In a bit flip mutation, each bit of the BitVector is flipped with probability M,
 * known as the mutation rate.  Flipping a bit means changing it to 0 if it is currently
 * a 1, or changing it to 1 if it is currently a 0.  If the length of the BitVector is
 * N, then the expected number of bits flipped during a single call to the {@link #mutate} method
 * is NM.  However, there is no guarantee that any bits will be flipped by a call to 
 * the {@link #mutate} method.  This behavior is fine for genetic algorithms, but may be less
 * than desirable for other metaheuristics, such as those that operate on a single candidate
 * solution rather than a population of them.  If you have need for a mutation operator for
 * BitVectors that guarantees that all calls to the {@link #mutate} method will change
 * the BitVector, then consider using the {@link DefiniteBitFlipMutation} class instead.</p> 
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 4.25.2020
 */
public final class BitFlipMutation implements UndoableMutationOperator<BitVector> {
	
	private final double m;
	private int[] flipped;
	
	/**
	 * Constructs a BitFlipMutation operator with a specified mutation rate.
	 * @param m The mutation rate, which is the probability of flipping any individual bit.
	 * The expected number of bits flipped during a call to the {@link #mutate} method is
	 * m*N where N is the length of the mutated BitVector.  There is no guarantee that any bits will be flipped
	 * during a mutation (e.g., if m is close to 0).
	 * @throws IllegalArgumentException if m &le; 0 or if m &ge; 1.
	 */
	public BitFlipMutation(double m) {
		if (m <= 0 || m >= 1) throw new IllegalArgumentException("m constrained by: 0.0 < m < 1.0");
		this.m = m;
	}
	
	/*
	 * internal copy constructor
	 */
	private BitFlipMutation(BitFlipMutation other) {
		m = other.m;
	}
	
	@Override
	public void mutate(BitVector c) {
		flipped = RandomIndexer.sample(c.length(), m);
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
	public BitFlipMutation split() {
		return new BitFlipMutation(this);
	}
}