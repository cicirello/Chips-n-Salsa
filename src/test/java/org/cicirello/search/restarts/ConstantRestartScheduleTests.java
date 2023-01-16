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

package org.cicirello.search.restarts;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.*;

/** JUnit test cases for ConstantRestartSchedule. */
public class ConstantRestartScheduleTests {

  @Test
  public void testConstantRestartSchedule() {
    for (int i = 1; i <= 128; i *= 2) {
      ConstantRestartSchedule r = new ConstantRestartSchedule(i);
      for (int j = 0; j < 3; j++) {
        assertEquals(i, r.nextRunLength());
      }
      r.reset();
      for (int j = 0; j < 3; j++) {
        assertEquals(i, r.nextRunLength());
      }
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new ConstantRestartSchedule(0));
  }

  @Test
  public void testConstantRestartScheduleCreateSchedules() {
    for (int n = 1; n <= 3; n++) {
      for (int i = 1; i <= 4; i *= 2) {
        List<ConstantRestartSchedule> schedules =
            ConstantRestartSchedule.createRestartSchedules(n, i);
        assertEquals(n, schedules.size());
        for (ConstantRestartSchedule r : schedules) {
          for (int j = 0; j < 3; j++) {
            assertEquals(i, r.nextRunLength());
          }
          r.reset();
          for (int j = 0; j < 3; j++) {
            assertEquals(i, r.nextRunLength());
          }
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> ConstantRestartSchedule.createRestartSchedules(0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> ConstantRestartSchedule.createRestartSchedules(1, 0));
  }

  @Test
  public void testConstantRestartScheduleSplit() {
    for (int i = 1; i <= 128; i *= 2) {
      for (int s = 0; s < 5; s++) {
        ConstantRestartSchedule r = new ConstantRestartSchedule(i);
        ConstantRestartSchedule split = null;
        for (int j = 0; j < 5; j++) {
          if (j == s) {
            split = r.split();
            for (int k = 0; k < 3; k++) {
              assertEquals(i, split.nextRunLength());
            }
          }
          assertEquals(i, r.nextRunLength());
        }
      }
    }
  }
}
