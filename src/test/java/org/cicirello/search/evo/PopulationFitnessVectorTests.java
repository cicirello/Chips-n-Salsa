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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/**
 * JUnit test cases for default and static methods of the PopulationFitnessVector interface and
 * nested interfaces.
 */
public class PopulationFitnessVectorTests {

  @Test
  public void testDoubleFitnesses() {
    final int[] lengths = {0, 1, 2, 4, 8};
    for (int N : lengths) {
      double[] fitnesses = new double[N];
      for (int i = 0; i < N; i++) {
        fitnesses[i] = 100 + i;
      }
      PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());
      assertEquals(N, vector.size());
      for (int i = 0; i < N; i++) {
        assertEquals(fitnesses[i], vector.getFitness(i));
      }
      double[] d = vector.toDoubleArray();
      assertFalse(fitnesses == d);
      assertArrayEquals(fitnesses, d);
    }
  }

  @Test
  public void testIntegerFitnesses() {
    final int[] lengths = {0, 1, 2, 4, 8};
    for (int N : lengths) {
      int[] fitnesses = new int[N];
      for (int i = 0; i < N; i++) {
        fitnesses[i] = 100 + i;
      }
      PopulationFitnessVector.Integer vector =
          PopulationFitnessVector.Integer.of(fitnesses.clone());
      assertEquals(N, vector.size());
      for (int i = 0; i < N; i++) {
        assertEquals(fitnesses[i], vector.getFitness(i));
      }
      int[] v = vector.toIntArray();
      assertFalse(fitnesses == v);
      assertArrayEquals(fitnesses, v);
      double[] d = vector.toDoubleArray();
      for (int i = 0; i < N; i++) {
        assertEquals(fitnesses[i], d[i]);
      }
    }
  }
}
