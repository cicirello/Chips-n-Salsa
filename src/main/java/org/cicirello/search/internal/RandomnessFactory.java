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

package org.cicirello.search.internal;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import org.cicirello.math.rand.EnhancedSplittableGenerator;

/**
 * This factory class is used by the library to create random number generator instances.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class RandomnessFactory {

  private static RandomGeneratorFactory<RandomGenerator.SplittableGenerator> factory =
      RandomGeneratorFactory.of("SplittableRandom");

  /* private to prevent instantiation */
  private RandomnessFactory() {}

  /**
   * Configures the RandomnessFactory.
   *
   * @param factory a RandomGeneratorFactory that creates RandomGenerator.SplittableGenerators
   */
  public static void configure(
      RandomGeneratorFactory<RandomGenerator.SplittableGenerator> factory) {
    if (factory != null) {
      RandomnessFactory.factory = factory;
    }
  }

  /**
   * Creates instances of EnhancedSplittableGenerator from the currently configured
   * RandomGeneratorFactory. In most cases, this is the method you want to use.
   *
   * @return the next EnhancedSplittableGenerator from the currently configured
   *     RandomGeneratorFactory
   */
  public static EnhancedSplittableGenerator createEnhancedSplittableGenerator() {
    return new EnhancedSplittableGenerator(factory.create());
  }

  /**
   * Creates instances of RandomGenerator.SplittableGenerator from the currently configured
   * RandomGeneratorFactory. In most cases, you should be using {@.ink
   * #createEnhancedSplittableGenerator} instead. The one exception to that, where you should use
   * this method, is if your use-case involves passing the random number generator to a method that
   * uses one of rho-mu's utility classes but which is outside this library. For example, the
   * Permutation class of JPT has methods for scrambling a Permutation. Those methods in turn
   * already use rho-mu's faster random ints in an interval. Passing it an EnhancedRandomGenerator
   * would lead to an extra level on the call stack.
   *
   * @return the next RandomGenerator.SplittableGenerator from the currently configured
   *     RandomGeneratorFactory
   */
  public static RandomGenerator.SplittableGenerator createSplittableGenerator() {
    return factory.create();
  }
}
