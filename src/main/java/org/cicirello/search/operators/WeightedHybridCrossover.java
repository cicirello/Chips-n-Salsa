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

package org.cicirello.search.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * A WeightedHybridCrossover enables using multiple crossover operators, such that each time the
 * {@link #cross} method is called, a randomly chosen crossover operator is applied to the candidate
 * solutions. The random choice of crossover operator is weighted proportionately based on an array
 * of weights passed upon construction.
 *
 * <p>Consider the following weights: w = [ 1, 2, 3]. In this example, the first crossover operator
 * will be used with probability 0.167, the second crossover operator will be used with probability
 * 2/6 = 0.333, and the third crossover operator will be used with probability 3/6 = 0.5.
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class WeightedHybridCrossover<T> implements CrossoverOperator<T> {

  private final ArrayList<CrossoverOperator<T>> ops;
  private final int[] choice;
  private final EnhancedSplittableGenerator generator;

  /**
   * Constructs a WeightedHybridCrossover from a Collection of CrossoverOperators.
   *
   * @param ops A Collection of CrossoverOperators.
   * @param weights The array of weights, whose length must be equal to ops.size(). Every element of
   *     weights must be greater than 0.
   * @throws IllegalArgumentException if ops doesn't contain any CrossoverOperators.
   * @throws IllegalArgumentException if ops.size() is not equal to weights.length.
   * @throws IllegalArgumentException if any weights are non-positive.
   */
  public WeightedHybridCrossover(Collection<? extends CrossoverOperator<T>> ops, int[] weights) {
    if (ops.size() == 0)
      throw new IllegalArgumentException("Must pass at least 1 CrossoverOperator.");
    if (ops.size() != weights.length)
      throw new IllegalArgumentException(
          "Number of weights must be same as number of crossover operators.");
    choice = weights.clone();
    if (choice[0] <= 0) throw new IllegalArgumentException("The weights must be positive.");
    for (int i = 1; i < choice.length; i++) {
      if (choice[i] <= 0) throw new IllegalArgumentException("The weights must be positive.");
      choice[i] = choice[i - 1] + choice[i];
    }
    this.ops = new ArrayList<CrossoverOperator<T>>(ops.size());
    for (CrossoverOperator<T> op : ops) {
      this.ops.add(op);
    }
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /*
   * private constructor to support split method
   */
  private WeightedHybridCrossover(WeightedHybridCrossover<T> other) {
    ops = new ArrayList<CrossoverOperator<T>>(other.ops.size());
    for (CrossoverOperator<T> op : other.ops) {
      ops.add(op.split());
    }
    choice = other.choice.clone();
    generator = other.generator.split();
  }

  @Override
  public void cross(T c1, T c2) {
    int value = generator.nextInt(choice[choice.length - 1]);
    int i = Arrays.binarySearch(choice, value);
    if (i < 0) i = -(i + 1);
    else i++;
    ops.get(i).cross(c1, c2);
  }

  @Override
  public WeightedHybridCrossover<T> split() {
    return new WeightedHybridCrossover<T>(this);
  }
}
