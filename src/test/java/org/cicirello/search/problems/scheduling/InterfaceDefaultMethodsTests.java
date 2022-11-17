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

package org.cicirello.search.problems.scheduling;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit tests for default methods of scheduling problem related interfaces. */
public class InterfaceDefaultMethodsTests {

  @Test
  public void testSingleMachineSchedulingProblemData() {

    SingleMachineSchedulingProblemData data =
        new SingleMachineSchedulingProblemData() {
          public int numberOfJobs() {
            return 0;
          }

          public int getProcessingTime(int j) {
            return 0;
          }

          public int[] getCompletionTimes(Permutation schedule) {
            return null;
          }
        };

    assertFalse(data.hasDueDates());
    assertFalse(data.hasReleaseDates());
    assertFalse(data.hasWeights());
    assertFalse(data.hasEarlyWeights());
    assertFalse(data.hasSetupTimes());
    assertEquals(0, data.getReleaseDate(0));
    assertEquals(1, data.getWeight(0));
    assertEquals(1, data.getEarlyWeight(0));
    assertEquals(0, data.getSetupTime(0));
    assertEquals(0, data.getSetupTime(0, 1));

    UnsupportedOperationException thrown =
        assertThrows(UnsupportedOperationException.class, () -> data.getDueDate(0));
  }
}
