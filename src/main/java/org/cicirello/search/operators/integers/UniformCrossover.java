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

import org.cicirello.math.rand.RandomSampler;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.representations.IntegerVector;

/**
 * Implementation of uniform crossover, but for IntegerVectors. In uniform crossover, instead of a
 * fixed number of cross points, the crossover operator is controlled by a parameter, p, that is the
 * probability that the values at a position are exchanged between the two parents. If the length of
 * the vectors is N, then N independent random decisions are made regarding whether to exchange each
 * position between the parents in forming the children. The expected number of values exchanged
 * between the parents during a single crossover event is thus p*N. The most common value for p=0.5,
 * which leads to each child inheriting on average half of the values from each of the two parents.
 * Uniform crossover originated for crossover of bit vectors within a genetic algorithm, but has
 * been adapted here for vectors of integers.
 *
 * @param <T> The specific IntegerVector type, such as {@link IntegerVector} or {@link
 *     org.cicirello.search.representations.BoundedIntegerVector BoundedIntegerVector}.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UniformCrossover<T extends IntegerVector> implements CrossoverOperator<T> {

  private final double p;

  /**
   * Constructs a uniform crossover operator with a probability of exchanging each value of p=0.5.
   */
  public UniformCrossover() {
    p = 0.5;
  }

  /**
   * Constructs a uniform crossover operator.
   *
   * @param p The per-position probability of exchanging each value between the parents in forming
   *     the children. The expected number of values exchanged during a single call to {@link
   *     #cross} is thus p*N, where N is the length of the vector.
   */
  public UniformCrossover(double p) {
    this.p = p <= 0.0 ? 0.0 : (p >= 1.0 ? 1.0 : p);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Behavior is undefined if the IntegerVectors are of different lengths.
   */
  @Override
  public void cross(IntegerVector c1, IntegerVector c2) {
    int[] indexes = RandomSampler.sample(c1.length(), p);
    for (int i : indexes) {
      int temp = c1.get(i);
      c1.set(i, c2.get(i));
      c2.set(i, temp);
    }
  }

  @Override
  public UniformCrossover<T> split() {
    // Maintains no mutable state, so just return this.
    return this;
  }
}
