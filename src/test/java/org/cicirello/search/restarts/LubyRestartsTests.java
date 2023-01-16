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

/** JUnit test cases for LubyRestarts. */
public class LubyRestartsTests {

  @Test
  public void testLuby() {
    int[] expected = {
      1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 16,
      1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 16,
      32
    };
    LubyRestarts r = new LubyRestarts();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], r.nextRunLength());
    }
    r.reset();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], r.nextRunLength());
    }
    r = new LubyRestarts(1000);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(1000 * expected[i], r.nextRunLength());
    }
    r.reset();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(1000 * expected[i], r.nextRunLength());
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new LubyRestarts(0));
  }

  @Test
  public void testLubyCreateSchedules() {
    for (int n = 1; n <= 3; n++) {
      int[] expected = {
        1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8,
        16, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4,
        8, 16, 32
      };
      List<LubyRestarts> schedules = LubyRestarts.createRestartSchedules(n);
      assertEquals(n, schedules.size());
      for (LubyRestarts r : schedules) {
        for (int i = 0; i < expected.length; i++) {
          assertEquals(expected[i], r.nextRunLength());
        }
        r.reset();
        for (int i = 0; i < expected.length; i++) {
          assertEquals(expected[i], r.nextRunLength());
        }
      }
      schedules = LubyRestarts.createRestartSchedules(n, 1000);
      assertEquals(n, schedules.size());
      for (LubyRestarts r : schedules) {
        for (int i = 0; i < expected.length; i++) {
          assertEquals(1000 * expected[i], r.nextRunLength());
        }
        r.reset();
        for (int i = 0; i < expected.length; i++) {
          assertEquals(1000 * expected[i], r.nextRunLength());
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> LubyRestarts.createRestartSchedules(0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> LubyRestarts.createRestartSchedules(1, 0));
  }

  @Test
  public void testLubySplit() {
    int[] expected = {
      1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 16,
      1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 16,
      32
    };
    for (int s = 0; s < 2 * expected.length; s++) {
      LubyRestarts r = new LubyRestarts();
      LubyRestarts split = null;
      for (int i = 0; i < expected.length; i++) {
        if (i == s) {
          split = r.split();
          for (int j = 0; j < expected.length; j++) {
            assertEquals(expected[j], split.nextRunLength());
          }
        }
        assertEquals(expected[i], r.nextRunLength());
      }
      r.reset();
      for (int i = 0; i < expected.length; i++) {
        if (split == null && expected.length + i == s) {
          split = r.split();
          for (int j = 0; j < expected.length; j++) {
            assertEquals(expected[j], split.nextRunLength());
          }
        }
        assertEquals(expected[i], r.nextRunLength());
      }
      r = new LubyRestarts(1000);
      split = null;
      for (int i = 0; i < expected.length; i++) {
        if (i == s) {
          split = r.split();
          for (int j = 0; j < expected.length; j++) {
            assertEquals(1000 * expected[j], split.nextRunLength());
          }
        }
        assertEquals(1000 * expected[i], r.nextRunLength());
      }
      r.reset();
      for (int i = 0; i < expected.length; i++) {
        if (split == null && expected.length + i == s) {
          split = r.split();
          for (int j = 0; j < expected.length; j++) {
            assertEquals(1000 * expected[j], split.nextRunLength());
          }
        }
        assertEquals(1000 * expected[i], r.nextRunLength());
      }
    }
  }
}
