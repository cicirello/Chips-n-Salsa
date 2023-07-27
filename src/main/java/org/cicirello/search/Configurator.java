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

package org.cicirello.search;

import java.util.random.RandomGenerator;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * This class provides the ability to configure certain library-wide behavior. No library
 * configuration is necessary. And in most cases, the default configuration is preferred. See the
 * documentation of the methods of this class for details on what can be configured, and why you may
 * wish to do so in very limited use-cases.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class Configurator {

  /*
   * private to prevent instantiation as this is a utility class with all static methods
   */
  private Configurator() {}

  /**
   * Configures the random number generator. The library only supports random number generators that
   * are "splittable" (i.e., that implements {@link RandomGenerator.SplittableGenerator} rather than
   * the more general {@link RandomGenerator}). This is because the library's multithreaded
   * functionality relies on the ability to split it as necessary to provide multiple threads with
   * dedicated random number generators (e.g., most of Java's built-in random number generators are
   * not thread-safe, and those that are, are not thread efficient). Additionally, each operator and
   * any other component that requires random numbers, maintains their own instances. The split is
   * used to enable this as well.
   *
   * <p>It should not be necessary to use this method. The default has been chosen for its speed.
   * The specific default is not guaranteed to remain the same from one version of the library to
   * the next. Any changes to the default will be based upon the speed and other characteristics of
   * those that current availability in the Java API.
   *
   * <p>If you use this method to configure the choice of random number generator, please be aware
   * that it will only affect the random number generator in use by operators and other components
   * that are instantiated <b>after</b> the call to configureRandomGenerator. Additionally, any
   * operators or other components that are split, such as in multithreaded cases, from operators or
   * other components that had been instantiated prior to the call to configureRandomGenerator will
   * continue to use the previous random number generator algorithm, even if the split occurred
   * after the call to configureRandomGenerator. Thus, if you desire to configure the random number
   * generator, it is safest to call configureRandomGenerator before doing anything else with the
   * library.
   *
   * <p>Here are a few usage examples:
   *
   * <pre><code>
   * Configurator.configureRandomGenerator(new SplittableRandom());
   * Configurator.configureRandomGenerator(new SplittableRandom(100L));
   * Configurator.configureRandomGenerator(RandomGenerator.SplittableGenerator.of("SplittableRandom"));
   * Configurator.configureRandomGenerator(RandomGenerator.SplittableGenerator.of("L64X128MixRandom"));
   * </code></pre>
   *
   * <p>You are of course not limited to the random number generators built-in to the Java API. You
   * can use your own custom random number generator, as long as it is "splittable" and implements
   * the {@link RandomGenerator.SplittableGenerator} interface. Here is an example:
   *
   * <pre><code>
   * Configurator.configureRandomGenerator(new MyCustomSplittableGenerator());
   * </code></pre>
   *
   * <p>Also be aware regardless of whatever random number generator you pass here that the library
   * replaces the functionality of many of its methods with more efficient algorithms provided by
   * the dependency on the <a href="https://rho-mu.cicirello.org/">&rho;&mu;</a> library.
   *
   * @param r an instance of a class that implements RandomGenerator.SplittableGenerator. This
   *     parameter is null-safe, treating null as a no-op.
   */
  public static void configureRandomGenerator(RandomGenerator.SplittableGenerator r) {
    RandomnessFactory.configure(r);
  }

  /**
   * Configures the random number generator with a specified seed. This seeds the first random
   * number generator instantiated after the call as specified, with all subsequent random number
   * generators split from that one or from an ancestor. This is because the library's multithreaded
   * functionality relies on the ability to split it as necessary to provide multiple threads with
   * dedicated random number generators (e.g., most of Java's built-in random number generators are
   * not thread-safe, and those that are, are not thread efficient). Additionally, each operator and
   * any other component that requires random numbers, maintains their own instances. The split is
   * used to enable this as well.
   *
   * <p>This method specifically utilizes the library's default random number generator algorithm.
   * If you wish to set a different random number generator algorithm as well as a seed, then use
   * the {@link #configureRandomGenerator(RandomGenerator.SplittableGenerator)} method instead and
   * pass a pre-seeded instance of your desired random number generator.
   *
   * <p>If you use this method to seed the random number generator, please be aware that it will
   * only affect the random number generator in use by operators and other components that are
   * instantiated <b>after</b> the call to configureRandomGenerator. Additionally, any operators or
   * other components that are split, such as in multithreaded cases, from operators or other
   * components that had been instantiated prior to the call to configureRandomGenerator will
   * continue to use the previous random number generator, even if the split occurred after the call
   * to configureRandomGenerator. Thus, if you desire to seed the random number generator, it is
   * safest to call configureRandomGenerator before doing anything else with the library.
   *
   * <p>In principle, seeding the random number generator once at the start should enable recreating
   * the exact behavior by replicating the exact same sequence of random numbers. This should be
   * true even if you are using the library's multithreaded functionality. However, if your use-case
   * manages to somehow involve threads instantiating operators or other components concurrently,
   * then the non-deterministic ordering associated with concurrency may prevent a guarantee of
   * identical behavior for identical seed values. Under normal use of the library, this should not
   * be a concern as you would most likely be fully instantiating all parallel algorithms in the
   * main thread before execution of those algorithms begins. But none-the-less, the library makes
   * no guarantees of full replication of random number sequences in multithreaded applications.
   *
   * @param seed the seed for the random number generator
   */
  public static void configureRandomGenerator(long seed) {
    RandomnessFactory.configure(seed);
  }
}
