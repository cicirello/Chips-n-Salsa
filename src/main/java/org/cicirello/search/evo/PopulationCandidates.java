/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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
 * An interface to the candidates for the next generation's population, consisting of the current
 * population as well as the relevant pool of children.
 *
 * @param <T> the representation of population members
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface PopulationCandidates<T> extends PopulationFitnessVector {

  /**
   * Accesses the i-th candidate. Implementations of {@link ReplacementStrategy} must not mutate the
   * state of the candidate here. By the time they get to this point, the candidates have already
   * been crossed, mutated, etc as necessary, and fitness has already been computed. If you change
   * the state of a candidate here, the behavior of the evolutionary algorithm may be unexpected.
   * Deliberately not returning an independent copy since some representations may be large and
   * complex.
   *
   * @param i the index of the candidate, which begins at 0
   * @return the i-th candidate
   * @throws IndexOutOfBoundsException if i is negative or if i is greater than or equal to size()
   */
  T candidate(int i);

  /**
   * An interface to the candidates for the next generation's population, consisting of the current
   * population as well as the relevant pool of children. This interface is for the case when
   * fitness values are ints.
   *
   * @param <T> the representation of population members
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  interface IntegerFitness<T>
      extends PopulationCandidates<T>, PopulationFitnessVector.IntegerFitness {}

  /**
   * An interface to the candidates for the next generation's population, consisting of the current
   * population as well as the relevant pool of children. This interface is for the case when
   * fitness values are doubles.
   *
   * @param <T> the representation of population members
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  interface DoubleFitness<T>
      extends PopulationCandidates<T>, PopulationFitnessVector.DoubleFitness {}
}
