/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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

import org.cicirello.math.stats.Statistics;

/**
 * Implements sigma scaling by wrapping your chosen selection operator. The SigmaScaling instance
 * applies sigma scaling to the fitnesses of the population, passing the scaled fitnesses to the
 * true selection operator.
 *
 * <p>The fitness, f<sub>i</sub>, of population member i is transformed as follows: f'<sub>i</sub> =
 * f<sub>i</sub> - ( f&#773; - c * &sigma; ), where c is a parameter usually between 1 and 3,
 * inclusive. &sigma; is the standard deviation of the fitnesses of the population, and f&#773; is
 * average fitness. For any members of the population for which this transformation produces a
 * fitness less than 0.001, it is reset to 0.001.
 *
 * <p>The intended use-case is to use in combination with a fitness weighted selection operator,
 * such as {@link FitnessProportionalSelection}, {@link StochasticUniversalSampling}, {@link
 * BiasedFitnessProportionalSelection}, or {@link BiasedStochasticUniversalSampling}. Here is an
 * example of how to instantiate an instance of a selection operator using SigmaScaling:
 *
 * <pre><code>
 * SelectionOperator selection = new SigmaScaling(new FitnessProportionalSelection(), 2);
 * </code></pre>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class SigmaScaling implements SelectionOperator {

  private final SelectionOperator selection;
  private final double c;

  /**
   * The minimum scaled fitness, such that any fitnesses that lead to scaled fitness less than this
   * value, are reset to this value.
   */
  public static final double MIN_SCALED_FITNESS = 0.001;

  /**
   * Constructs a new SigmaScaling object, to transform fitness values via sigma scaling prior to
   * selection by a wrapped selection operator. Uses a default c = 2.0.
   *
   * @param selection The selection operator.
   */
  public SigmaScaling(SelectionOperator selection) {
    this(selection, 2.0);
  }

  /**
   * Constructs a new SigmaScaling object, to transform fitness values via sigma scaling prior to
   * selection by a wrapped selection operator.
   *
   * @param selection The selection operator.
   * @param c See class documentation for details of this parameter.
   */
  public SigmaScaling(SelectionOperator selection, double c) {
    this.selection = selection;
    this.c = c;
  }

  @Override
  public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
    selection.select(PopulationFitnessVector.Double.of(scale(fitnesses.toDoubleArray())), selected);
  }

  @Override
  public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
    selection.select(PopulationFitnessVector.Double.of(scale(fitnesses.toDoubleArray())), selected);
  }

  @Override
  public void init(int generations) {
    selection.init(generations);
  }

  @Override
  public SigmaScaling split() {
    return new SigmaScaling(selection.split(), c);
  }

  private double[] scale(double[] f) {
    double adjustment = Statistics.mean(f) - c * Statistics.stdev(f);
    for (int i = 0; i < f.length; i++) {
      f[i] -= adjustment;
      if (f[i] < MIN_SCALED_FITNESS) {
        f[i] = MIN_SCALED_FITNESS;
      }
    }
    return f;
  }
}
