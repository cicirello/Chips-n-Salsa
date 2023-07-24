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

package org.cicirello.search.sa;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * This class implements the linear cooling schedule for simulated annealing. In this cooling
 * schedule, the k-th temperature, t<sub>k</sub>, is determined as follows: t<sub>k</sub> =
 * t<sub>0</sub> - k * &Delta;t, where t<sub>0</sub> is the initial temperature and &Delta;t is the
 * difference between two consecutive temperature values. The new temperature is usually computed
 * incrementally from the previous with: t<sub>k</sub> = t<sub>k-1</sub> - &Delta;t. In some
 * applications, the temperature update occurs with each simulated annealing evaluation, while in
 * others it is updated periodically, such as every s steps (i.e., iterations) of simulated
 * annealing. This class supports this periodic update approach, with a default of every step. See
 * the parameters of the constructors for more information.
 *
 * <p>Additionally, if the temperature is ever less than 0.001, this class sets it to 0.001 for the
 * remainder of the run. For any foreseeable cost scale that a problem may have, a temperature value
 * of 0.001 is sufficiently low such that all moves that are worse than the current state will be
 * rejected, so further cooling would be superfluous.
 *
 * <p>The {@link #accept accept} methods of this class use the classic, and most common, Boltzmann
 * distribution for determining whether to accept a neighbor.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class LinearCooling implements AnnealingSchedule {

  private double t;
  private final double t0;
  private final double deltaT;
  private final int steps;
  private int stepCounter;
  private final EnhancedSplittableGenerator generator;

  /**
   * Constructs a linear cooling schedule for simulated annealing.
   *
   * @param t0 The initial temperature for the start of an annealing run. The value of t0 must be
   *     positive.
   * @param deltaT The difference between the current temperature and the next temperature. The
   *     value of deltaT must be positive.
   * @param steps The number of iterations of simulated annealing between cooling events. Steps must
   *     be positive. If 0 or a negative is passed for steps, steps is set to 1.
   * @throws IllegalArgumentException if t0 &le; 0 or deltaT &le; 0.
   */
  public LinearCooling(double t0, double deltaT, int steps) {
    if (t0 <= 0) throw new IllegalArgumentException("Initial temperature must be positive");
    if (deltaT <= 0) throw new IllegalArgumentException("deltaT must be positive");
    t = this.t0 = t0;
    this.deltaT = deltaT;
    this.steps = steps <= 0 ? 1 : steps;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Constructs a linear cooling schedule for simulated annealing.
   *
   * @param t0 The initial temperature for the start of an annealing run. The value of t0 must be
   *     positive.
   * @param deltaT The difference between the current temperature and the next temperature. The
   *     value of deltaT must be positive.
   * @throws IllegalArgumentException if t0 &le; 0 or deltaT &le; 0.
   */
  public LinearCooling(double t0, double deltaT) {
    this(t0, deltaT, 1);
  }

  /*
   * private copy constructor for internal use only
   */
  private LinearCooling(LinearCooling other) {
    t = t0 = other.t0;
    deltaT = other.deltaT;
    steps = other.steps;
    generator = other.generator.split();
  }

  @Override
  public void init(int maxEvals) {
    t = t0;
    stepCounter = 0;
  }

  @Override
  public boolean accept(double neighborCost, double currentCost) {
    boolean doAccept =
        neighborCost <= currentCost
            || generator.nextDouble() < Math.exp((currentCost - neighborCost) / t);
    stepCounter++;
    if (stepCounter == steps && t > 0.001) {
      stepCounter = 0;
      t -= deltaT;
      if (t < 0.001) t = 0.001;
    }
    return doAccept;
  }

  @Override
  public LinearCooling split() {
    return new LinearCooling(this);
  }

  /*
   * package-private for unit testing
   */
  double getTemperature() {
    return t;
  }
}
