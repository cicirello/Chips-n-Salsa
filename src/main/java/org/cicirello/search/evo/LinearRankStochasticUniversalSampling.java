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

/**
 * This class implements linear rank selection using Stochastic Universal Sampling (SUS). Linear
 * rank selection begins be determining the rank of each population member, where the least fit
 * member of the population has rank 1, and the most fit member of the population has rank N, where
 * the population size is N. During selection, the population member with rank r is chosen randomly
 * with probability proportional to: 2 - c + 2(r - 1)(c - 1)/(N - 1). The c is a real-valued
 * parameter that must be in the interval [1, 2]. When c is equal to 1, all population members are
 * equally likely chosen. When c is equal to 2, the expected number of times the most fit population
 * member will be chosen is 2, the least fit member won't be selected at all, and the expected
 * number of times the other population members will be chosen in a generation will vary between 0
 * and 2 based upon rank. To avoid a probability of 0 of choosing the least fit population member,
 * then ensure that c is less than 2. To ensure that the selection operator doesn't degenerate into
 * a uniform random selection, then set c greater than 1. The value of c can be interpreted as the
 * expected number of times the most fit population member will be selected in a generation.
 *
 * <p>Linear rank selection was introduced by Baker (1985). According to "An Introduction to Genetic
 * Algorithms" (Melanie Mitchell, 1998), Baker recommended c = 1.1.
 *
 * <p>However, whereas the standard form of linear rank selection is like spinning a carnival wheel
 * with a single pointer M times to select M members of the population, this SUS version instead is
 * like spinning a carnival wheel that has M equidistant pointers a single time to select all M
 * simultaneously. One statistical consequence of this is that it reduces the variance of the
 * selected copies of population members as compared to the other approach. Another consequence is
 * that SUS is typically much faster since only a single random floating point number is needed per
 * generation, compared to M random floating-point numbers.
 *
 * <p>The runtime to select M population members from a population of size N is O(N lg N + M), which
 * includes the need to generate only a single random double, and O(M) random ints..
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class LinearRankStochasticUniversalSampling extends StochasticUniversalSampling {

  private final double c;

  /**
   * Construct a linear rank selection operator that uses stochastic universal sampling.
   *
   * @param c The expected number of times the most fit population member should be selected during
   *     one generation, which must be in the interval [1.0, 2.0].
   * @throws IllegalArgumentException if c is less than 1 or greater than 2.
   */
  public LinearRankStochasticUniversalSampling(double c) {
    super();
    if (c < 1 || c > 2) throw new IllegalArgumentException("c must be int he interval [1.0, 2.0].");
    this.c = c;
  }

  private LinearRankStochasticUniversalSampling(LinearRankStochasticUniversalSampling other) {
    super(other);
    c = other.c;
  }

  @Override
  public LinearRankStochasticUniversalSampling split() {
    return new LinearRankStochasticUniversalSampling(this);
  }

  @Override
  final double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
    return computeWeightRunningSumRanks(
        sortedIndexes(fitnesses), r -> 2 - c + 2 * r * (c - 1) / (fitnesses.size() - 1.0));
  }

  @Override
  final double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
    return computeWeightRunningSumRanks(
        sortedIndexes(fitnesses), r -> 2 - c + 2 * r * (c - 1) / (fitnesses.size() - 1.0));
  }
}
