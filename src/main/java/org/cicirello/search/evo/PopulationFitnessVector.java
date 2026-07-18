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
 * An interface to a vector of fitnesses of a population.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface PopulationFitnessVector {

  /**
   * The size of the population.
   *
   * @return the size of the population
   */
  int size();

  /**
   * An interface to a vector of fitnesses, each an int, of a population.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  interface IntegerFitness extends PopulationFitnessVector {

    /**
     * Gets the fitness of a population member.
     *
     * @param i The index into the population, which must be in the interval [0, size()).
     * @return the fitness of population member i.
     */
    int fitness(int i);

    /**
     * Creates a PopulationFitnessVector.IntegerFitness wrapping a primitive int array.
     *
     * @param f The int array to wrap, returned object holds a reference to f and does not clone it.
     * @return a PopulationFitnessVector.IntegerFitness wrapping f
     */
    static PopulationFitnessVector.IntegerFitness of(int[] f) {
      class IntegerArrayAsFitnessVector implements PopulationFitnessVector.IntegerFitness {
        private final int[] f;

        public IntegerArrayAsFitnessVector(int[] f) {
          this.f = f;
        }

        @Override
        public int size() {
          return f.length;
        }

        @Override
        public int fitness(int i) {
          return f[i];
        }
      }
      ;
      return new IntegerArrayAsFitnessVector(f);
    }

    /**
     * Creates a new array of ints containing the elements of this PopulationFitnessVector.Integer.
     *
     * @return an array of ints
     */
    default int[] toIntArray() {
      int[] f = new int[size()];
      for (int i = 0; i < f.length; i++) {
        f[i] = fitness(i);
      }
      return f;
    }

    /**
     * Creates a new array of doubles containing the elements of this
     * PopulationFitnessVector.Integer.
     *
     * @return an array of doubles
     */
    default double[] toDoubleArray() {
      double[] f = new double[size()];
      for (int i = 0; i < f.length; i++) {
        f[i] = fitness(i);
      }
      return f;
    }
  }

  /**
   * An interface to a vector of fitnesses, each a double, of a population.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  interface DoubleFitness extends PopulationFitnessVector {

    /**
     * Gets the fitness of a population member.
     *
     * @param i The index into the population, which must be in the interval [0, size()).
     * @return the fitness of population member i.
     */
    double fitness(int i);

    /**
     * Creates a PopulationFitnessVector.DoubleFitness wrapping a primitive double array.
     *
     * @param f The double array to wrap, returned object holds a reference to f and does not clone
     *     it.
     * @return a PopulationFitnessVector.DoubleFitness wrapping f
     */
    static PopulationFitnessVector.DoubleFitness of(double[] f) {
      class DoubleArrayAsFitnessVector implements PopulationFitnessVector.DoubleFitness {
        private final double[] f;

        public DoubleArrayAsFitnessVector(double[] f) {
          this.f = f;
        }

        @Override
        public int size() {
          return f.length;
        }

        @Override
        public double fitness(int i) {
          return f[i];
        }
      }
      ;
      return new DoubleArrayAsFitnessVector(f);
    }

    /**
     * Creates a new array of doubles containing the elements of this
     * PopulationFitnessVector.Double.
     *
     * @return an array of doubles
     */
    default double[] toDoubleArray() {
      double[] d = new double[size()];
      for (int i = 0; i < d.length; i++) {
        d[i] = fitness(i);
      }
      return d;
    }
  }
}
