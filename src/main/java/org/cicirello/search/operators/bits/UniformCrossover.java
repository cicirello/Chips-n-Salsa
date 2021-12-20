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
 
package org.cicirello.search.operators.bits;

import org.cicirello.search.representations.BitVector;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * <p>Implementation of uniform crossover, a classic crossover operator for 
 * BitVectors. In uniform crossover, instead of a fixed number of cross points,
 * the crossover operator is controlled by a parameter, p, that is the probability
 * that a bit is exchanged between the two parents. If the BitVectors are N bits in
 * length, then N independent random decisions are made regarding whether to exchange each
 * bit between the parents in forming the children. The expected number of bits
 * exchanged between the parents during a single crossover event is thus p*N. The
 * most common value for p=0.5, which leads to each child inheriting on average half
 * of the bits from each of the two parents.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UniformCrossover implements CrossoverOperator<BitVector> {
	
	private final double p;
	
	/**
	 * Constructs a uniform crossover operator with a probability of 
	 * exchanging each bit of p=0.5.
	 */
	public UniformCrossover() {
		p = 0.5;
	}
	
	/**
	 * Constructs a uniform crossover operator.
	 *
	 * @param p The per-bit probability of exchanging each bit between the parents
	 * in forming the children. The expected number of bits exchanged during a single
	 * call to {@link #cross} is thus p*N, where N is the length of the BitVector.
	 */
	public UniformCrossover(double p) {
		this.p = p <= 0.0 ? 0.0 : (p >= 1.0 ? 1.0 : p);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException if c1.length() is not equal to c2.length()
	 */
	@Override
	public void cross(BitVector c1, BitVector c2) {
		BitVector.exchangeBits(c1, c2, new BitVector(c1.length(), p));
	}
	
	@Override
	public UniformCrossover split() {
		// Maintains no mutable state, so just return this.
		return this;
	}
}
