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
import org.cicirello.math.rand.EnhancedRandomGenerator;
import org.cicirello.math.rand.EnhancedSplittableGenerator;

/**
 * This factory class is used by the library to create random number generator instances.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class RandomnessFactory {

  private static final String DEFAULT_ALGORITHM_NAME = "SplittableRandom";
  private static final String DEFAULT_SEEDED_ALGORITHM_NAME = "SplittableRandom";

  private static volatile RandomGenerator.SplittableGenerator next =
      RandomGenerator.SplittableGenerator.of(DEFAULT_ALGORITHM_NAME);

  private static final Object lock = new Object();

  /*
   * Also support ThreadLocal instances of EnhancedSplittableGenerator
   * for cases where it isn't convenient to maintain a generator within a component
   * of the library.
   */
  private static final ThreadLocal<EnhancedSplittableGenerator> threadLocal =
      new ThreadLocal<EnhancedSplittableGenerator>();

  /* private to prevent instantiation */
  private RandomnessFactory() {}

  /** Configures the RandomnessFactory with the default PRNG algorithm. */
  public static void configureDefault() {
    synchronized (lock) {
      next = RandomGenerator.SplittableGenerator.of(DEFAULT_ALGORITHM_NAME);
    }
  }

  /**
   * Configures the RandomnessFactory.
   *
   * @param next the next random number generator for this factory to base subsequent random
   *     generators
   */
  public static void configure(RandomGenerator.SplittableGenerator next) {
    if (next != null) {
      synchronized (lock) {
        RandomnessFactory.next = next;
      }
    }
  }

  /**
   * Configures the RandomnessFactory from a seed. That is, the initial random number generator is
   * seeded as specified, with all others split from it.
   *
   * @param seed the seed for the next random number generator
   */
  public static void configure(long seed) {
    RandomGeneratorFactory<RandomGenerator.SplittableGenerator> factory =
        RandomGeneratorFactory.of(DEFAULT_ALGORITHM_NAME);
    synchronized (lock) {
      next = factory.create(seed);
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
    return new EnhancedSplittableGenerator(createSplittableGenerator());
  }

  /**
   * Creates instances of RandomGenerator.SplittableGenerator from the currently configured
   * RandomGeneratorFactory. In most cases, you should be using {@link
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
    synchronized (lock) {
      RandomGenerator.SplittableGenerator result = next;
      next = result.split();
      return result;
    }
  }

  /*
   * Gets the ThreadLocal instance of EnhancedSplittableGenerator if one exists for the
   * current thread, creating one if necessary by splitting.
   *
   * @return the ThreadLocal instance of EnhancedSplittableGenerator for the current thread
   */
  public static EnhancedSplittableGenerator threadLocalEnhancedSplittableGenerator() {
    EnhancedSplittableGenerator local = threadLocal.get();
    if (local == null) {
      local = createEnhancedSplittableGenerator();
      threadLocal.set(local);
    }
    return local;
  }

  /**
   * Creates an instance of a seeded EnhancedRandomGenerator for cases where seeded randomness is
   * needed for reproducible random sequences (e.g., to enable recreating the same instance of a
   * problem.
   *
   * @param seed the seed
   */
  public static EnhancedRandomGenerator createSeededEnhancedRandomGenerator(long seed) {
    RandomGeneratorFactory<RandomGenerator> factory =
        RandomGeneratorFactory.of(DEFAULT_SEEDED_ALGORITHM_NAME);
    return new EnhancedRandomGenerator(factory.create(seed));
  }
}
