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

package org.cicirello.search.problems.tsp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit tests for EuclideanDistance. */
public class EuclideanDistanceTests {

  private final EuclideanDistance distance;

  public EuclideanDistanceTests() {
    distance = new EuclideanDistance();
  }

  @Test
  public void testSameX() {
    assertEquals(3.25, distance.distance(4.2, 5.6, 4.2, 8.85));
    assertEquals(3.25, distance.distance(4.2, 8.85, 4.2, 5.6));
    assertEquals(3, distance.distanceAsInt(4.2, 5.6, 4.2, 8.85));
    assertEquals(3, distance.distanceAsInt(4.2, 8.85, 4.2, 5.6));
    assertEquals(3.75, distance.distance(4.2, 5.6, 4.2, 9.35));
    assertEquals(3.75, distance.distance(4.2, 9.35, 4.2, 5.6));
    assertEquals(4, distance.distanceAsInt(4.2, 5.6, 4.2, 9.35));
    assertEquals(4, distance.distanceAsInt(4.2, 9.35, 4.2, 5.6));
  }

  @Test
  public void testSameY() {
    assertEquals(3.25, distance.distance(5.6, 4.2, 8.85, 4.2));
    assertEquals(3.25, distance.distance(8.85, 4.2, 5.6, 4.2));
    assertEquals(3, distance.distanceAsInt(5.6, 4.2, 8.85, 4.2));
    assertEquals(3, distance.distanceAsInt(8.85, 4.2, 5.6, 4.2));
    assertEquals(3.75, distance.distance(5.6, 4.2, 9.35, 4.2));
    assertEquals(3.75, distance.distance(9.35, 4.2, 5.6, 4.2));
    assertEquals(4, distance.distanceAsInt(5.6, 4.2, 9.35, 4.2));
    assertEquals(4, distance.distanceAsInt(9.35, 4.2, 5.6, 4.2));
  }

  @Test
  public void test345Triangle() {
    double a = 1.75;
    double x1 = 1.25;
    double x2 = x1 + 3.0 * a;
    double y1 = 7.25;
    double y2 = y1 + 4.0 * a;
    double expected = 5.0 * a;
    int expectedInt = (int) Math.round(expected);
    assertEquals(expected, distance.distance(x1, y1, x2, y2));
    assertEquals(expected, distance.distance(x1, y2, x2, y1));
    assertEquals(expected, distance.distance(x2, y1, x1, y2));
    assertEquals(expected, distance.distance(x2, y2, x1, y1));
    assertEquals(expectedInt, distance.distanceAsInt(x1, y1, x2, y2));
    assertEquals(expectedInt, distance.distanceAsInt(x1, y2, x2, y1));
    assertEquals(expectedInt, distance.distanceAsInt(x2, y1, x1, y2));
    assertEquals(expectedInt, distance.distanceAsInt(x2, y2, x1, y1));
  }
}
