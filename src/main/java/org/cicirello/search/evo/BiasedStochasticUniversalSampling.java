/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 * This class implements a variation of Stochastic Universal Sampling (SUS) that we call Biased
 * Stochastic Universal Sampling (Biased SUS), which integrates the use of a bias function with SUS
 * to enable transforming fitness values prior to the stochastic selection decisions. In this Biased
 * SUS, a member of the population is chosen randomly with probability proportional to a bias
 * function of its fitness relative to the total of such biased fitness of the population. For
 * example, if the fitness of population member i is f<sub>i</sub>, then the probability of
 * selecting population member i is: bias(f<sub>i</sub>) / &sum;<sub>j</sub> bias(f<sub>j</sub>),
 * for j &isin; { 1, 2, ..., N }, where N is the population size, and bias is a bias function.
 *
 * <p>As an example bias function, consider: bias(x) = x<sup>2</sup>, which would square each
 * fitness value x.
 *
 * <p>SUS and this Biased SUS are similar to fitness proportional selection and a biased variation
 * of fitness proportional selection. However, whereas fitness proportional selection is like
 * spinning a carnival wheel with a single pointer M times to select M members of the population,
 * SUS instead is like spinning a carnival wheel that has M equidistant pointers a single time to
 * select all M simultaneously. One statistical consequence of this is that it reduces the variance
 * of the selected copies of population members as compared to fitness proportional selection (and
 * its biased variation). Another consequence is that SUS and Biased SUS are typically much faster
 * since only a single random floating point number is needed per generation, compared to M random
 * floating-point numbers for fitness proportional selection. However, SUS and Biased SUS then must
 * randomize the ordering of the population to avoid all of the copies of a single population member
 * from being in sequence so that parent assignment is random, whereas fitness proportional
 * selection has this property built in.
 *
 * <p><b>This selection operator requires positive fitness values. Behavior is undefined if any
 * fitness values are less than or equal to 0.</b> If your fitness values may be negative, you can
 * use {@link FitnessShifter}, which transforms fitness values such that minimum fitness equals 1.
 *
 * <p>The runtime to select M population members from a population of size N is O(N + M), which
 * includes the need to generate only a single random double, and O(M) ints. This assumes that the
 * bias function has a constant runtime.
 *
 * <p>For the more common standard version of SUS, see the {@link StochasticUniversalSampling}
 * class.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class BiasedStochasticUniversalSampling extends StochasticUniversalSampling {

  private final FitnessBiasFunction bias;

  /**
   * Construct a biased stochastic universal sampling operator.
   *
   * @param bias A bias function
   */
  public BiasedStochasticUniversalSampling(FitnessBiasFunction bias) {
    this.bias = bias;
  }

  @Override
  public BiasedStochasticUniversalSampling split() {
    // The FitnessBiasFunction interface's contract is that implementations
    // must be threadsafe and thread efficient, so assume that the only state,
    // the bias function, meets that contract.
    return this;
  }

  @Override
  double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
    double[] p = new double[fitnesses.size()];
    p[0] = bias.bias(fitnesses.getFitness(0));
    for (int i = 1; i < p.length; i++) {
      p[i] = p[i - 1] + bias.bias(fitnesses.getFitness(i));
    }
    return p;
  }

  @Override
  double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
    double[] p = new double[fitnesses.size()];
    p[0] = bias.bias(fitnesses.getFitness(0));
    for (int i = 1; i < p.length; i++) {
      p[i] = p[i - 1] + bias.bias(fitnesses.getFitness(i));
    }
    return p;
  }
}
