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

package org.cicirello.search.operators.bits;

import org.cicirello.math.rand.RandomSampler;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.representations.BitVector;

/**
 * Implementation of K-point crossover, a classic crossover operator for BitVectors. K-point
 * crossover is a generalization of two-point crossover. In a K-point crossover, K random cross
 * points are chosen uniformly along the length of the bit vector parents. Both parents are cut at
 * the K cross points, breaking the parents into multiple segments. The bits within every other
 * segment are then swapped between the two parents to form the two children.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class KPointCrossover implements CrossoverOperator<BitVector> {

  private final int[] indexes;

  /**
   * Constructs a K-point crossover operator.
   *
   * @param k The number of cross points, which must be at least 1. Although, if you want only a
   *     single cross point, you should instead use {@link SinglePointCrossover}; and likewise, if
   *     you only want 2 cross points, you should instead use {@link TwoPointCrossover}.
   * @throws IllegalArgumentException if k is less than 1
   */
  public KPointCrossover(int k) {
    if (k < 1) {
      throw new IllegalArgumentException("Must specify at least k=1 cross points");
    }
    indexes = new int[k];
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if c1.length() is not equal to c2.length()
   * @throws IllegalArgumentException if c1.length() is less than k.
   */
  @Override
  public void cross(BitVector c1, BitVector c2) {
    RandomSampler.sample(c1.length(), indexes.length, indexes);
    sort(indexes);
    int i = 1;
    for (; i < indexes.length; i += 2) {
      BitVector.exchangeBits(c1, c2, indexes[i - 1], indexes[i] - 1);
    }
    i--;
    if (i < indexes.length) {
      BitVector.exchangeBits(c1, c2, indexes[i], c1.length() - 1);
    }
  }

  @Override
  public KPointCrossover split() {
    // Need to construct a fresh instance.
    // Maintains state that cannot be shared.
    return new KPointCrossover(indexes.length);
  }

  private void sort(int[] indexes) {
    // Indexes should probably be small, and might be sorted or nearly sorted
    // depending upon which sampling method Random.sample chose... e.g., for small
    // k relative to length, the alg it chooses will return indexes sorted (though
    // that is not part of the contract so can't assume that to be the case.
    //
    // For small length, especially if sorted or nearly sorted, an insertion sort
    // though quadratic worst case will likely be faster than an n log n sort.
    // If sorted or nearly sorted will run in linear time. Otherwise, due to
    // probably short length since k is likely small, will probably be faster
    // than mergesort due to effects of constants.
    for (int i = 1; i < indexes.length; i++) {
      int element = indexes[i];
      int j = i - 1;
      while (j >= 0 && indexes[j] > element) {
        indexes[j + 1] = indexes[j];
        j--;
      }
      indexes[j + 1] = element;
    }
  }
}
