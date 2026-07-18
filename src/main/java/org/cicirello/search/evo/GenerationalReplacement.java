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
 * Generational replacement replaces the entire population each generation. This is the classic
 * replacement strategy of the simple genetic algorithm and other generational models.
 *
 * @param <T> the representation of population members
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class GenerationalReplacement<T> implements ReplacementStrategy<T> {

  /** Constructs the replacement strategy. */
  public GenerationalReplacement() {}

  @Override
  public void replace(
      PopulationCandidates.IntegerFitness<T> parentPopulation,
      PopulationCandidates.IntegerFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {
    final int N = Math.min(targetPopulationSize, childPopulation.size());
    for (int i = 0; i < N; i++) {
      replacements.addFromChildPopulation(i);
    }
  }

  @Override
  public void replace(
      PopulationCandidates.DoubleFitness<T> parentPopulation,
      PopulationCandidates.DoubleFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {
    final int N = Math.min(targetPopulationSize, childPopulation.size());
    for (int i = 0; i < N; i++) {
      replacements.addFromChildPopulation(i);
    }
  }

  @Override
  public ReplacementStrategy<T> split() {
    // This operator doesn't maintain state across calls, so it is safe to
    // share across threads. Thus, just return this.
    return this;
  }
}
