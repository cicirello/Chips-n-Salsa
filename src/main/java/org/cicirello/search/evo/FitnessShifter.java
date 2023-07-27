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
 * FitnessShifter wraps another SelectionOperator, shifting all fitness values by the minimum
 * fitness minus one, such that the least fit population member's transformed fitness is equal to 1,
 * with the wrapped SelectionOperator than performing selection using the transformed fitnesses. One
 * benefit is that this provides a mechanism for dealing with negative fitnesses in the case of a
 * fitness proportional selection operator.
 *
 * <p>The fitness, f<sub>i</sub>, of population member i is transformed as follows: f'<sub>i</sub> =
 * f<sub>i</sub> - f<sub>min</sub> + 1, where f<sub>min</sub> is the minimum fitness in the
 * population.
 *
 * <p>The intended use-case is to use in combination with a fitness weighted selection operator,
 * such as {@link FitnessProportionalSelection}, {@link StochasticUniversalSampling}, {@link
 * BiasedFitnessProportionalSelection}, or {@link BiasedStochasticUniversalSampling}. Here is an
 * example of how to instantiate an instance of a selection operator using SigmaScaling:
 *
 * <pre><code>
 * SelectionOperator selection = new FitnessShifter(new FitnessProportionalSelection());
 * </code></pre>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class FitnessShifter implements SelectionOperator {

  private final SelectionOperator selection;

  /**
   * Constructs a new FitnessShifter object, to transform fitness values so that minumum fitness is
   * 1.
   *
   * @param selection The selection operator.
   */
  public FitnessShifter(SelectionOperator selection) {
    this.selection = selection;
  }

  @Override
  public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
    selection.select(PopulationFitnessVector.Integer.of(shift(fitnesses.toIntArray())), selected);
  }

  @Override
  public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
    selection.select(PopulationFitnessVector.Double.of(shift(fitnesses.toDoubleArray())), selected);
  }

  @Override
  public void init(int generations) {
    selection.init(generations);
  }

  @Override
  public FitnessShifter split() {
    return new FitnessShifter(selection.split());
  }

  private int[] shift(int[] f) {
    int adjustment = f[0];
    for (int i = 1; i < f.length; i++) {
      if (f[i] < adjustment) {
        adjustment = f[i];
      }
    }
    adjustment--;
    for (int i = 0; i < f.length; i++) {
      f[i] -= adjustment;
    }
    return f;
  }

  private double[] shift(double[] f) {
    double adjustment = f[0];
    for (int i = 1; i < f.length; i++) {
      if (f[i] < adjustment) {
        adjustment = f[i];
      }
    }
    adjustment -= 1.0;
    for (int i = 0; i < f.length; i++) {
      f[i] -= adjustment;
    }
    return f;
  }
}
