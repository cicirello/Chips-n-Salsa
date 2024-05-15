/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

package org.cicirello.search.problems.tsp;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.SolutionCostPair;
import org.junit.jupiter.api.*;

/** JUnit tests for the TSP base class. The nested subclasses are tested in other test classes. */
public class TSPTests {

  @Test
  public void testConstructorWithDistanceFunction() {
    class TSPSubClass extends TSP {
      private final int LENGTH;
      private final double WIDTH;

      public TSPSubClass(int n, double w) {
        super(
            n,
            w,
            (x1, y1, x2, y2) -> Math.abs(x1 - x2) + Math.abs(y1 - y2),
            new EnhancedSplittableGenerator(42),
            true);
        LENGTH = n;
        WIDTH = w;
      }

      @Override
      public double edgeCostForHeuristics(int i, int j) {
        // Tests don't use this.
        return -1;
      }

      @Override
      public double costAsDouble(Permutation c) {
        // tests don't use this
        return 1;
      }

      @Override
      public SolutionCostPair<Permutation> getSolutionCostPair(Permutation p) {
        // Tests don't use this.
        return new SolutionCostPair<Permutation>(p, -1, false);
      }

      public void validateDistanceFunction() {
        assertEquals(4.0, d.distance(2, 0, 6, 0), 1E-10);
        assertEquals(5.0, d.distance(7, 0, 2, 0), 1E-10);
        assertEquals(4.0, d.distance(0, 2, 0, 6), 1E-10);
        assertEquals(5.0, d.distance(0, 7, 0, 2), 1E-10);
        assertEquals(4, d.distanceAsInt(2, 0, 6, 0));
        assertEquals(5, d.distanceAsInt(7, 0, 2, 0));
        assertEquals(4, d.distanceAsInt(0, 2, 0, 6));
        assertEquals(5, d.distanceAsInt(0, 7, 0, 2));
        assertEquals(6.0, d.distance(2, 3, 6, 1), 1E-10);
        assertEquals(7.0, d.distance(7, 1, 2, 3), 1E-10);
        assertEquals(6.0, d.distance(3, 2, 1, 6), 1E-10);
        assertEquals(7.0, d.distance(1, 7, 3, 2), 1E-10);
        assertEquals(6, d.distanceAsInt(2, 3, 6, 1));
        assertEquals(7, d.distanceAsInt(7, 1, 2, 3));
        assertEquals(6, d.distanceAsInt(1, 2, 3, 6));
        assertEquals(7, d.distanceAsInt(3, 7, 1, 2));
      }

      public void validateCoordinates() {
        assertEquals(LENGTH, x.length);
        assertEquals(LENGTH, y.length);
        assertEquals(LENGTH, length());
        for (int i = 0; i < LENGTH; i++) {
          assertTrue(x[i] >= 0);
          assertTrue(y[i] >= 0);
          assertTrue(x[i] < WIDTH);
          assertTrue(y[i] < WIDTH);
          assertEquals(x[i], getX(i), 0.0);
          assertEquals(y[i], getY(i), 0.0);
        }
      }
    }
    TSPSubClass tsp = new TSPSubClass(10, 5);
    tsp.validateDistanceFunction();
    tsp.validateCoordinates();
    tsp = new TSPSubClass(5, 20);
    tsp.validateCoordinates();
  }

  @Test
  public void testConstructorWithoutDistanceFunction() {
    class TSPSubClass extends TSP {
      private final int LENGTH;
      private final double WIDTH;

      public TSPSubClass(int n, double w) {
        super(n, w, new EuclideanDistance(), new EnhancedSplittableGenerator(42), true);
        LENGTH = n;
        WIDTH = w;
      }

      @Override
      public double edgeCostForHeuristics(int i, int j) {
        // Tests don't use this.
        return -1;
      }

      @Override
      public double costAsDouble(Permutation c) {
        // tests don't use this
        return 1;
      }

      @Override
      public SolutionCostPair<Permutation> getSolutionCostPair(Permutation p) {
        // Tests don't use this.
        return new SolutionCostPair<Permutation>(p, -1, false);
      }

      public void validateDistanceFunction() {
        assertEquals(4.0, d.distance(2, 0, 6, 0), 1E-10);
        assertEquals(5.0, d.distance(7, 0, 2, 0), 1E-10);
        assertEquals(4.0, d.distance(0, 2, 0, 6), 1E-10);
        assertEquals(5.0, d.distance(0, 7, 0, 2), 1E-10);
        assertEquals(4, d.distanceAsInt(2, 0, 6, 0));
        assertEquals(5, d.distanceAsInt(7, 0, 2, 0));
        assertEquals(4, d.distanceAsInt(0, 2, 0, 6));
        assertEquals(5, d.distanceAsInt(0, 7, 0, 2));
        assertEquals(Math.sqrt(20), d.distance(2, 3, 6, 1), 1E-10);
        assertEquals(Math.sqrt(29), d.distance(7, 1, 2, 3), 1E-10);
        assertEquals(Math.sqrt(20), d.distance(3, 2, 1, 6), 1E-10);
        assertEquals(Math.sqrt(29), d.distance(1, 7, 3, 2), 1E-10);
        assertEquals(4, d.distanceAsInt(2, 3, 6, 1));
        assertEquals(5, d.distanceAsInt(7, 1, 2, 3));
        assertEquals(4, d.distanceAsInt(1, 2, 3, 6));
        assertEquals(5, d.distanceAsInt(3, 7, 1, 2));
      }

      public void validateCoordinates() {
        assertEquals(LENGTH, x.length);
        assertEquals(LENGTH, y.length);
        assertEquals(LENGTH, length());
        for (int i = 0; i < LENGTH; i++) {
          assertTrue(x[i] >= 0);
          assertTrue(y[i] >= 0);
          assertTrue(x[i] < WIDTH);
          assertTrue(y[i] < WIDTH);
          assertEquals(x[i], getX(i), 0.0);
          assertEquals(y[i], getY(i), 0.0);
        }
      }
    }
    TSPSubClass tsp = new TSPSubClass(10, 5);
    tsp.validateDistanceFunction();
    tsp.validateCoordinates();
    tsp = new TSPSubClass(5, 20);
    tsp.validateCoordinates();
  }
}
