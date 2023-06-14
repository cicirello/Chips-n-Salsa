/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.representations.IntegerVector;

/**
 * Implementation of two-point crossover, but for IntegerVectors. In a two-point crossover, two
 * random cross points are chosen uniformly along the length of the parents. Both parents are cut at
 * the two cross points. The elements between the two cross points are then swapped between the two
 * parents to form the two children. Two-point crossover originated for crossover of bit vectors
 * within a genetic algorithm, but has been adapted here for vectors of integers.
 *
 * @param <T> The specific IntegerVector type, such as {@link IntegerVector} or {@link
 *     org.cicirello.search.representations.BoundedIntegerVector BoundedIntegerVector}.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TwoPointCrossover<T extends IntegerVector> implements CrossoverOperator<T> {

  private final int[] indexes;
  private final EnhancedSplittableGenerator generator;

  /** Constructs a two-point crossover operator. */
  public TwoPointCrossover() {
    indexes = new int[2];
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /* private to support split() only */
  private TwoPointCrossover(TwoPointCrossover other) {
    indexes = new int[2];
    generator = other.generator.split();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Behavior is undefined if the IntegerVectors are of different lengths.
   */
  @Override
  public void cross(IntegerVector c1, IntegerVector c2) {
    generator.nextIntPair(c1.length(), indexes);
    if (indexes[1] > indexes[0]) {
      IntegerVector.exchange(c1, c2, indexes[0], indexes[1] - 1);
    } else {
      IntegerVector.exchange(c1, c2, indexes[1], indexes[0] - 1);
    }
  }

  @Override
  public TwoPointCrossover<T> split() {
    return new TwoPointCrossover<T>(this);
  }
}
