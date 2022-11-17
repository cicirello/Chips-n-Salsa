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

package org.cicirello.search.operators.bits;

import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.BitVector;

/**
 * Generates random {@link BitVector} objects for use in generating random initial solutions for
 * simulated annealing and other metaheuristics. Also used for copying such objects. A BitVector is
 * an indexable vector of bits.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BitVectorInitializer implements Initializer<BitVector> {

  private final int bitLength;

  /**
   * Construct a BitVectorInitializer for creating random BitVectors of a specified length.
   *
   * @param bitLength The length in bits of the BitVectors created by this initializer.
   * @throws IllegalArgumentException if bitLength is negative.
   */
  public BitVectorInitializer(int bitLength) {
    if (bitLength < 0) throw new IllegalArgumentException("bitLength must be non-negative.");
    this.bitLength = bitLength;
  }

  @Override
  public BitVector createCandidateSolution() {
    return new BitVector(bitLength, true);
  }

  @Override
  public BitVectorInitializer split() {
    // thread-safe so can simply return this.
    return this;
  }
}
