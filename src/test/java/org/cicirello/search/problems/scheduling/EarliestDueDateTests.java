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

package org.cicirello.search.problems.scheduling;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit tests for EarliestDueDate. */
public class EarliestDueDateTests extends SchedulingHeuristicValidation {

  @Test
  public void testEDD() {
    int[] duedates = {3, 0, 1, 7, (int) Math.ceil(2.0 / EarliestDueDate.MIN_H - 1.0)};
    double[] expected = {0.25, 1.0, 0.5, 0.125, EarliestDueDate.MIN_H};
    FakeProblemDuedates problem = new FakeProblemDuedates(duedates);
    EarliestDueDate h = new EarliestDueDate(problem);
    for (int j = 0; j < duedates.length; j++) {
      assertEquals(expected[j], h.h(null, j, null), 1E-10);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              int[] p = {1, 1};
              int[] w = {1, 1};
              FakeProblemWeightsPTime pr = new FakeProblemWeightsPTime(p, w);
              new EarliestDueDate(pr);
            });
  }
}
