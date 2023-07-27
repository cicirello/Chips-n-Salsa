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
 * This class implements tournament selection for evolutionary algorithms. In tournament selection,
 * a member of the population is chosen in the following manner. First, choose k members of the
 * population uniformly at random (with replacement). Next, from those k members, select the one
 * with greatest fitness. Repeat this process as many times as needed to form the population for the
 * generation. When k=2, it is known as binary tournament selection.
 *
 * <p>This selection operator is compatible with all fitness functions, even in the case of negative
 * fitness values, since it simply compares which fitness values are higher.
 *
 * <p>The runtime to select M population members from a population of size N is O(k M), which
 * includes generating O(k M) random int values.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TournamentSelection implements SelectionOperator {

  private final int k;
  private final EnhancedSplittableGenerator generator;

  /** Constructs a binary tournament selection operator, i.e., k = 2. */
  public TournamentSelection() {
    k = 2;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  /**
   * Constructs a tournament selection operator.
   *
   * @param k The tournament size, which must be at least 2.
   * @throws IllegalArgumentException if k is less than 2.
   */
  public TournamentSelection(int k) {
    if (k < 2) throw new IllegalArgumentException("The tournament size must be at least 2.");
    this.k = k;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private TournamentSelection(TournamentSelection other) {
    generator = other.generator.split();
    k = other.k;
  }

  @Override
  public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
    for (int i = 0; i < selected.length; i++) {
      selected[i] = tournament(fitnesses);
    }
  }

  @Override
  public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
    for (int i = 0; i < selected.length; i++) {
      selected[i] = tournament(fitnesses);
    }
  }

  @Override
  public TournamentSelection split() {
    return new TournamentSelection(this);
  }

  private int tournament(PopulationFitnessVector.Integer fitnesses) {
    int choose = generator.nextInt(fitnesses.size());
    for (int j = 1; j < k; j++) {
      int other = generator.nextInt(fitnesses.size());
      if (fitnesses.getFitness(other) > fitnesses.getFitness(choose)) {
        choose = other;
      }
    }
    return choose;
  }

  private int tournament(PopulationFitnessVector.Double fitnesses) {
    int choose = generator.nextInt(fitnesses.size());
    for (int j = 1; j < k; j++) {
      int other = generator.nextInt(fitnesses.size());
      if (fitnesses.getFitness(other) > fitnesses.getFitness(choose)) {
        choose = other;
      }
    }
    return choose;
  }
}
