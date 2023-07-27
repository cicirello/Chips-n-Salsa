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
 * This functional interface is used to provide a bias function to the {@link
 * BiasedFitnessProportionalSelection} operator as well as the {@link
 * BiasedStochasticUniversalSampling} operator.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@FunctionalInterface
public interface FitnessBiasFunction {

  /**
   * Applies a bias function to a fitness value. Implementations must ensure that this method always
   * returns positive values. It may assume that the parameter fitness is positive. Implementations
   * must also be both threadsafe as well as thread efficient, because if evolutionary algorithms
   * are used in combination with the parallel search functionality of the library, it may provide
   * multiple threads with references to the same FitnessBiasFunction object. Ideally,
   * implementations of this interface should avoid mutable state.
   *
   * @param fitness A fitness value, which is assumed positive.
   * @return A biased fitness.
   */
  double bias(double fitness);
}
