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
 * This class implements Boltzmann selection using Stochastic Universal Sampling (SUS). Boltzmann
 * selection is similar to a fitness proportional selection, except instead of a population member
 * being weighted by its fitness f in the randomized selection process, Boltzmann selection weights
 * it by e<sup>f/T</sup>, where f is the fitness of the individual, and T is a temperature
 * parameter, much like that of simulated annealing. T typically decreases over the run of the
 * evolutionary algorithm.
 *
 * <p>This implementation supports a constant temperature T, as well as two cooling schedules:
 * linear cooling and exponential cooling. In both cases, at the start of the evolutionary
 * algorithm, the temperature T is initialized to a t0. In linear cooling, at the end of each
 * generation, T is updated according to: T = T - r. In exponential cooling, at the end of each
 * generation, T is updated according to: T = r * T. In both cases, if T ever falls below some tMin,
 * it is reset to tMin.
 *
 * <p>Unlike many other fitness proportional related selection operators, Boltzmann selection,
 * including this SUS version, is applicable even if fitness values can be negative.
 *
 * <p>SUS and this Boltzmann SUS are similar to fitness proportional selection and a variation of
 * fitness proportional selection biasing selection by the Boltzmann distribution. However, whereas
 * fitness proportional selection is like spinning a carnival wheel with a single pointer M times to
 * select M members of the population, SUS instead is like spinning a carnival wheel that has M
 * equidistant pointers a single time to select all M simultaneously. One statistical consequence of
 * this is that it reduces the variance of the selected copies of population members as compared to
 * fitness proportional selection. Another consequence is that SUS is typically much faster since
 * only a single random floating point number is needed per generation, compared to M random
 * floating-point numbers for fitness proportional selection. However, SUS then must randomize the
 * ordering of the population to avoid all of the copies of a single population member from being in
 * sequence so that parent assignment is random, whereas fitness proportional selection has this
 * property built in.
 *
 * <p>The runtime to select M population members from a population of size N is O(N + M), which
 * includes the need to generate only a single random double, and O(M) ints.
 *
 * <p>For the basic version of Boltzmann selection, see the {@link BoltzmannSelection} class. And
 * for the standard version of SUS, see the {@link StochasticUniversalSampling} class.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BoltzmannStochasticUniversalSampling extends BiasedStochasticUniversalSampling {

  private final BoltzmannBiasFunction boltzmann;

  /**
   * Construct a Boltzmann selection operator with a constant temperature.
   *
   * @param t The temperature, which must be positive.
   * @throws IllegalArgumentException if t is not positive
   */
  public BoltzmannStochasticUniversalSampling(double t) {
    this(new ConstantBoltzmannBiasFunction(t));
    if (t <= 0.0) throw new IllegalArgumentException("The temperature must be positive.");
  }

  /**
   * Construct a Boltzmann selection operator, with either a linear cooling schedule or an
   * exponential cooling schedule.
   *
   * @param t0 The initial temperature, which must be positive.
   * @param tMin The minimum temperature. If an update would decrease temperature below tMin, it is
   *     set to tMin. Must be positive and no greater than t0.
   * @param r The update value, which must be positive for linear cooling, and must be in (0.0, 1.0)
   *     for exponential cooling.
   * @param linearCooling If true, uses a linear cooling schedule, and if false it uses an
   *     exponential cooling schedule.
   * @throws IllegalArgumentException if tMin is not positive or if t0 is less than tMin or if
   *     linear cooling with non-positive r or if exponential cooling with r not in (0,0, 1.0).
   */
  public BoltzmannStochasticUniversalSampling(
      double t0, double tMin, double r, boolean linearCooling) {
    this(
        linearCooling
            ? new LinearCoolingBiasFunction(t0, r, tMin)
            : new ExponentialCoolingBiasFunction(t0, r, tMin));
    if (tMin <= 0.0) throw new IllegalArgumentException("Minimum temperature must be positive.");
    if (t0 < tMin)
      throw new IllegalArgumentException(
          "Minimum temperature must be no greater than initial temperature.");
    if (r <= 0.0) throw new IllegalArgumentException("r must be positive");
    if (!linearCooling && r >= 1.0)
      throw new IllegalArgumentException("For exponential cooling, r must be positive.");
  }

  private BoltzmannStochasticUniversalSampling(BoltzmannBiasFunction boltzmann) {
    super(boltzmann);
    this.boltzmann = boltzmann;
  }

  private BoltzmannStochasticUniversalSampling(BoltzmannStochasticUniversalSampling other) {
    this(other.boltzmann.split());
  }

  @Override
  public void init(int generations) {
    boltzmann.init();
  }

  @Override
  public BoltzmannStochasticUniversalSampling split() {
    return new BoltzmannStochasticUniversalSampling(this);
  }

  @Override
  final double[] computeWeightRunningSum(PopulationFitnessVector.Integer fitnesses) {
    double[] result = super.computeWeightRunningSum(fitnesses);
    boltzmann.update();
    return result;
  }

  @Override
  final double[] computeWeightRunningSum(PopulationFitnessVector.Double fitnesses) {
    double[] result = super.computeWeightRunningSum(fitnesses);
    boltzmann.update();
    return result;
  }
}
