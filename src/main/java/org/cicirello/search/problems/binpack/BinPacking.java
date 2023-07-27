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

package org.cicirello.search.problems.binpack;

import org.cicirello.math.rand.EnhancedRandomGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.util.IntegerList;

/**
 * This class, and its nested classes, implements the Bin Packing problem. Although you won't
 * instantiate this class directly (it doesn't have a public constructor), its methods can be used
 * to compute the cost of solutions, or to map a Permutation to the details of the solution that it
 * represents (i.e., which items are in which bins). This class also provides methods for getting
 * the sizes of items, and other information about the instance. To generate instances of the Bin
 * Packing problem, use the constructors of the nested subclasses.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class BinPacking implements IntegerCostOptimizationProblem<Permutation> {

  private final int[] items;
  private final int capacity;
  private final int lowerBound;

  /*
   * package-private constructor. IMPORTANT: the items parameter is directly set
   * as instance field, so be careful not to expose where this array is initialized.
   */
  BinPacking(int capacity, int[] items) {
    this.capacity = capacity;
    this.items = items;
    int total = 0;
    for (int i = 0; i < items.length; i++) {
      if (items[i] > capacity) {
        throw new IllegalArgumentException("at least one item is large than the bin capacity");
      }
      total += items[i];
    }
    lowerBound = (total / capacity) + (total % capacity > 0 ? 1 : 0);
  }

  /**
   * Gets the bin capacity for the instance.
   *
   * @return the bin capacity
   */
  public final int getCapacity() {
    return capacity;
  }

  /**
   * Gets the number of items in the instance.
   *
   * @return the number of items
   */
  public final int numItems() {
    return items.length;
  }

  /**
   * Gets the size of an item.
   *
   * @param i The index of the item.
   * @return the size of item i
   * @throws IndexOutOfBoundsException if i is either negative or greater than or equal to
   *     numItems()
   */
  public final int getSize(int i) {
    return items[i];
  }

  /**
   * {@inheritDoc}
   *
   * <p>Computes the cost of the solution formed by applying a first-fit heuristic using the item
   * ordering implied by the Permutation candidate. The optimal solution will have index of items
   * for a bin grouped together in the permutation.
   */
  @Override
  public final int cost(Permutation candidate) {
    IntegerList bins = new IntegerList();
    for (int i = 0; i < candidate.length(); i++) {
      int id = candidate.get(i);
      boolean added = false;
      for (int j = 0; j < bins.size(); j++) {
        int space = bins.get(j);
        if (space >= items[id]) {
          bins.set(j, space - items[id]);
          added = true;
          break;
        }
      }
      if (!added) {
        bins.add(capacity - items[id]);
      }
    }
    return bins.size();
  }

  @Override
  public final int value(Permutation candidate) {
    return cost(candidate);
  }

  @Override
  public final int minCost() {
    return lowerBound;
  }

  /**
   * Determines the bin packing solution that corresponds to a given permutation, such that the
   * solution is formed by applying a first-fit heuristic using the item ordering implied by the
   * Permutation p.
   *
   * @param p A Permutation
   * @return the solution that p represents.
   */
  public final BinPackingSolution permutationToBinPackingSolution(Permutation p) {
    return new BinPackingSolution(p, capacity, items);
  }

  /**
   * Generates instances of the Bin Packing problem with item sizes that are generated uniformly at
   * random.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class UniformRandom extends BinPacking {

    /**
     * Generates a random instance of the Bin Packing problem, with a default bin capacity of 150,
     * and with item sizes generated uniformly at random from the interval [20, 100]. These are the
     * defaults of the uniform instances from the OR-Library.
     *
     * @param numItems The number of items.
     * @throws NegativeArraySizeException if numItems is negative.
     */
    public UniformRandom(int numItems) {
      this(numItems, 150, 20, 100);
    }

    /**
     * Generates a random instance of the Bin Packing problem, with a default bin capacity of 150,
     * and with item sizes generated uniformly at random from the interval [20, 100]. These are the
     * defaults of the uniform instances from the OR-Library.
     *
     * @param numItems The number of items.
     * @param seed The seed for the random number generator to enable reproducible problem
     *     instances.
     * @throws NegativeArraySizeException if numItems is negative.
     */
    public UniformRandom(int numItems, long seed) {
      this(numItems, 150, 20, 100, seed);
    }

    /**
     * Generates a random instance of the Bin Packing problem, with item sizes generated uniformly
     * at random from a specified interval.
     *
     * @param numItems The number of items.
     * @param capacity The bin capacity.
     * @param minSize The minimum size for items.
     * @param maxSize The maximum size for items.
     * @throws IllegalArgumentException if minSize is greater than maxSize.
     * @throws NegativeArraySizeException if numItems is negative.
     */
    public UniformRandom(int numItems, int capacity, int minSize, int maxSize) {
      super(
          capacity,
          createItems(
              numItems,
              minSize,
              maxSize,
              RandomnessFactory.threadLocalEnhancedSplittableGenerator()));
    }

    /**
     * Generates a random instance of the Bin Packing problem, with item sizes generated uniformly
     * at random from a specified interval.
     *
     * @param numItems The number of items.
     * @param capacity The bin capacity.
     * @param minSize The minimum size for items.
     * @param maxSize The maximum size for items.
     * @param seed The seed for the random number generator to enable reproducible problem
     *     instances.
     * @throws IllegalArgumentException if minSize is greater than maxSize.
     * @throws NegativeArraySizeException if numItems is negative.
     */
    public UniformRandom(int numItems, int capacity, int minSize, int maxSize, long seed) {
      super(
          capacity,
          createItems(
              numItems,
              minSize,
              maxSize,
              RandomnessFactory.createSeededEnhancedRandomGenerator(seed)));
    }

    private static int[] createItems(
        int numItems, int minSize, int maxSize, EnhancedRandomGenerator gen) {
      if (minSize > maxSize)
        throw new IllegalArgumentException("min and max sizes are inconsistent");
      int[] items = new int[numItems];
      int bound = maxSize - minSize + 1;
      for (int i = 0; i < numItems; i++) {
        items[i] = minSize + gen.nextInt(bound);
      }
      return items;
    }
  }

  /**
   * Generates instances of the Bin Packing problem where the optimal solution is comprised of all
   * full triplet bins (each bin in optimal solution has exactly 3 items that fills the bin to
   * capacity). The cost of the optimal solution to each such instance is one third the number of
   * items.
   *
   * <p>The bin capacity for the instances generated by this method is 1000. When generating the
   * instance, each triplet is generated as follows. The size of one item is uniform at random from
   * the interval [380, 490]. The second item's size is generated uniformly at random from [250,
   * s/2), where s is 1000 - size of the first item. The third item for the triplet has size 1000 -
   * s - t, where s and t are the sizes of the first two items.
   *
   * <p>The triplets, as defined above, are as described in: "A Hybrid Grouping Genetic Algorithm
   * for Bin Packing" by Emanuel Falkenauer.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class Triplet extends BinPacking {

    private static final int MIN_INTERVAL_1 = 380;
    private static final int MIN_INTERVAL_2 = 250;
    private static final int BOUND_INTERVAL_1 = 490 - MIN_INTERVAL_1 + 1;
    private static final int CAPACITY = 1000;

    /**
     * Generates a triplet instance of the Bin Packing Problem.
     *
     * @param numItemTriplets The number of triplets. The number of items in the instance will equal
     *     3 * numItemTriplets.
     * @throws NegativeArraySizeException if numItemTriplets is negative.
     */
    public Triplet(int numItemTriplets) {
      super(
          CAPACITY,
          createItems(numItemTriplets, RandomnessFactory.threadLocalEnhancedSplittableGenerator()));
    }

    /**
     * Generates a triplet instance of the Bin Packing Problem.
     *
     * @param numItemTriplets The number of triplets. The number of items in the instance will equal
     *     3 * numItemTriplets.
     * @param seed The seed for the random number generator to enable reproducible problem
     *     instances.
     * @throws NegativeArraySizeException if numItemTriplets is negative.
     */
    public Triplet(int numItemTriplets, long seed) {
      super(
          CAPACITY,
          createItems(
              numItemTriplets, RandomnessFactory.createSeededEnhancedRandomGenerator(seed)));
    }

    private static int[] createItems(int numItemTriplets, EnhancedRandomGenerator gen) {
      int[] items = new int[numItemTriplets * 3];
      int i = 0;
      for (int t = 0; t < numItemTriplets; t++) {
        items[i] = MIN_INTERVAL_1 + gen.nextInt(BOUND_INTERVAL_1);
        int space = CAPACITY - items[i];
        i++;
        int bound = (space >> 1) - MIN_INTERVAL_2 + ((space & 0x1) != 0 ? 1 : 0);
        items[i] = MIN_INTERVAL_2 + gen.nextInt(bound);
        space -= items[i];
        i++;
        items[i] = space;
        i++;
      }
      shuffle(items, gen);
      return items;
    }

    private static void shuffle(int[] items, EnhancedRandomGenerator gen) {
      for (int bound = items.length; bound > 1; bound--) {
        int j = gen.nextInt(bound);
        int i = bound - 1;
        if (i != j) {
          int temp = items[i];
          items[i] = items[j];
          items[j] = temp;
        }
      }
    }
  }
}
