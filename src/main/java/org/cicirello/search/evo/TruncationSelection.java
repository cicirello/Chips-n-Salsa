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

package org.cicirello.search.evo;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * This class implements truncation selection for evolutionary algorithms. In truncation selection,
 * the proportion p &isin; [0.0, 1.0) of the population with greatest fitness is determined.
 * Selection then proceeds randomly, with each member of the next generation chosen uniformly at
 * random from among the p*N members of the population with highest fitness, where N is the size of
 * the population. For example, if p=0.5, and if the population size N=100, then truncation
 * selection will select individuals uniformly at random from among the 50 population members with
 * highest fitness.
 *
 * <p>Note that in this implementation, we modify the definition slightly, without loss of
 * generality. Specifically, rather than defining the operator in terms of a proportion, the
 * constructor of this class includes a parameter k, which is the absolute number of greatest
 * fitness population members to select from. For example, if population size is N, and if we want
 * the equivalent behavior for a proportion p=0.5, we would pass 50 for k.
 *
 * <p>This selection operator is compatible with all fitness functions, even in the case of negative
 * fitness values, since it simply compares which fitness values are higher.
 *
 * <p>The runtime to select M population members from a population of size N is O(N + M), which
 * includes generating O(M) random int values. In a typical generational model, M=N, and this is
 * simply O(N). Note that you will often see the runtime for truncation selection cited as O(N lg
 * N), mistakenly assuming that sorting the population by fitness is necessary. However, it is
 * possible to determine the k most fit members of the population without a full sort in O(N) time,
 * as we can partition the population into the set of the k most-fit and the set of the N-k least
 * fit using a modification of a typical order-statistics algorithm in linear time since we don't
 * actually need a total ordering over either of those sets.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TruncationSelection implements SelectionOperator {

  private final int k;
  private final EnhancedSplittableGenerator generator;

  /**
   * Constructs a truncation selection operator that selects uniformly at random from the k most fit
   * current members of the population.
   *
   * @param k The number of the most fit individuals to base selection upon. The value of k must be
   *     at least 1. However, 1 is not likely to be a good choice since this means that all
   *     offspring will be based upon the single most fit individual, which means crossover will
   *     always lead to the same children.
   * @throws IllegalArgumentException if k is less than 1.
   */
  public TruncationSelection(int k) {
    if (k < 1) throw new IllegalArgumentException();
    this.k = k;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private TruncationSelection(TruncationSelection other) {
    generator = other.generator.split();
    k = other.k;
  }

  @Override
  public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
    if (k < fitnesses.size()) {
      int[] selectFrom = initSelectFrom(fitnesses.size());
      int truncateCount = selectFrom.length - k;
      internalSelect(
          bestFitToRight(fitnesses, selectFrom, 0, selectFrom.length - 1, truncateCount),
          selected,
          truncateCount);
    } else {
      internalSelect(selected, fitnesses.size());
    }
  }

  @Override
  public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
    if (k < fitnesses.size()) {
      int[] selectFrom = initSelectFrom(fitnesses.size());
      int truncateCount = selectFrom.length - k;
      internalSelect(
          bestFitToRight(fitnesses, selectFrom, 0, selectFrom.length - 1, truncateCount),
          selected,
          truncateCount);
    } else {
      internalSelect(selected, fitnesses.size());
    }
  }

  @Override
  public TruncationSelection split() {
    return new TruncationSelection(this);
  }

  private void internalSelect(int[] selectFrom, int[] selected, int truncateCount) {
    for (int i = 0; i < selected.length; i++) {
      selected[i] = selectFrom[truncateCount + generator.nextInt(k)];
    }
  }

  private void internalSelect(int[] selected, int n) {
    // case when k is at least as large as population... thus nothing truncated
    for (int i = 0; i < selected.length; i++) {
      selected[i] = generator.nextInt(n);
    }
  }

  /*
   * package private to ease unit testing, but not actually used outside of this class.
   */
  final int[] initSelectFrom(int n) {
    final int[] selectFrom = new int[n];
    for (int i = 0; i < n; i++) {
      selectFrom[i] = i;
    }
    return selectFrom;
  }

  /*
   * package private to ease unit testing, but not actually used outside of this class.
   */
  final int[] bestFitToRight(
      PopulationFitnessVector.Integer fitnesses,
      int[] indexes,
      int first,
      int last,
      int truncateCount) {
    if (last > first) {
      int pivot = partition(fitnesses, indexes, first, last);
      /*
      // This case shouldn't happen since partition puts everything <= to pivot to the left,
      // so no duplicates to right.
      while (pivot < truncateCount && fitnesses.getFitness(indexes[pivot]) == fitnesses.getFitness(indexes[pivot+1])) {
      	pivot++;
      }
      */
      while (pivot > truncateCount
          && fitnesses.getFitness(indexes[pivot]) == fitnesses.getFitness(indexes[pivot - 1])) {
        pivot--;
      }
      if (pivot < truncateCount) {
        return bestFitToRight(fitnesses, indexes, pivot + 1, last, truncateCount);
      } else if (pivot > truncateCount) {
        return bestFitToRight(fitnesses, indexes, first, pivot - 1, truncateCount);
      }
    }
    return indexes;
  }

  /*
   * package private to ease unit testing, but not actually used outside of this class.
   */
  final int[] bestFitToRight(
      PopulationFitnessVector.Double fitnesses,
      int[] indexes,
      int first,
      int last,
      int truncateCount) {
    if (last > first) {
      int pivot = partition(fitnesses, indexes, first, last);
      /*
      // This case shouldn't happen since partition puts everything <= to pivot to the left,
      // so no duplicates to right.
      while (pivot < truncateCount && fitnesses.getFitness(indexes[pivot]) == fitnesses.getFitness(indexes[pivot+1])) {
      	pivot++;
      }
      */
      while (pivot > truncateCount
          && fitnesses.getFitness(indexes[pivot]) == fitnesses.getFitness(indexes[pivot - 1])) {
        pivot--;
      }
      if (pivot < truncateCount) {
        return bestFitToRight(fitnesses, indexes, pivot + 1, last, truncateCount);
      } else if (pivot > truncateCount) {
        return bestFitToRight(fitnesses, indexes, first, pivot - 1, truncateCount);
      }
    }
    return indexes;
  }

  private int partition(
      PopulationFitnessVector.Integer fitnesses, int[] indexes, int first, int last) {
    if (last > first + 1) {
      int m = indexOfMedian(fitnesses, indexes, first, last, (first + last) >> 1);
      int temp = indexes[m];
      indexes[m] = indexes[last];
      indexes[last] = temp;
    }
    int x = fitnesses.getFitness(indexes[last]);
    int i = first - 1;
    for (int j = first; j < last; j++) {
      if (fitnesses.getFitness(indexes[j]) <= x) {
        i++;
        int temp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = temp;
      }
    }
    int temp = indexes[i + 1];
    indexes[i + 1] = indexes[last];
    indexes[last] = temp;
    return i + 1;
  }

  private int partition(
      PopulationFitnessVector.Double fitnesses, int[] indexes, int first, int last) {
    if (last > first + 1) {
      int m = indexOfMedian(fitnesses, indexes, first, last, (first + last) >> 1);
      int temp = indexes[m];
      indexes[m] = indexes[last];
      indexes[last] = temp;
    }
    double x = fitnesses.getFitness(indexes[last]);
    int i = first - 1;
    for (int j = first; j < last; j++) {
      if (fitnesses.getFitness(indexes[j]) <= x) {
        i++;
        int temp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = temp;
      }
    }
    int temp = indexes[i + 1];
    indexes[i + 1] = indexes[last];
    indexes[last] = temp;
    return i + 1;
  }

  private int indexOfMedian(
      PopulationFitnessVector.Integer fitnesses, int[] indexes, int a, int b, int c) {
    return fitnesses.getFitness(indexes[a]) < fitnesses.getFitness(indexes[b])
        ? medianByInsertion(fitnesses, indexes, a, b, c)
        : medianByInsertion(fitnesses, indexes, b, a, c);
  }

  private int indexOfMedian(
      PopulationFitnessVector.Double fitnesses, int[] indexes, int a, int b, int c) {
    return fitnesses.getFitness(indexes[a]) < fitnesses.getFitness(indexes[b])
        ? medianByInsertion(fitnesses, indexes, a, b, c)
        : medianByInsertion(fitnesses, indexes, b, a, c);
  }

  /*
   * Handles case where relative order of 2 elements is known.
   */
  private static int medianByInsertion(
      PopulationFitnessVector.Integer fitnesses, int[] indexes, int low, int high, int unknown) {
    return fitnesses.getFitness(indexes[unknown]) < fitnesses.getFitness(indexes[low])
        ? low
        : fitnesses.getFitness(indexes[unknown]) > fitnesses.getFitness(indexes[high])
            ? high
            : unknown;
  }

  /*
   * Handles case where relative order of 2 elements is known.
   */
  private static int medianByInsertion(
      PopulationFitnessVector.Double fitnesses, int[] indexes, int low, int high, int unknown) {
    return fitnesses.getFitness(indexes[unknown]) < fitnesses.getFitness(indexes[low])
        ? low
        : fitnesses.getFitness(indexes[unknown]) > fitnesses.getFitness(indexes[high])
            ? high
            : unknown;
  }
}
