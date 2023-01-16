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

/** JUnit test cases for ParallelVariableAnnealingLength. */
public class ParallelVariableAnnealingLengthTests {

  @Test
  public void testPVAL() {
    int[] expected = {
      1000,
      2000,
      4000,
      8000,
      16000,
      32000,
      64000,
      128000,
      256000,
      512000,
      1024000,
      2048000,
      4096000,
      8192000,
      16384000,
      32768000,
      65536000,
      131072000,
      262144000,
      524288000,
      1048576000,
      2097152000,
      0x7fffffff,
      0x7fffffff,
      0x7fffffff
    };
    for (int t = 1; t < 12; t++) {
      List<ParallelVariableAnnealingLength> r =
          ParallelVariableAnnealingLength.createRestartSchedules(t);
      assertEquals(t, r.size());
      int groupSize = t < 4 ? t : 4;
      for (int i = 0; i < expected.length; i++) {
        int j = i % groupSize;
        for (int k = j; k < r.size(); k += 4) {
          assertEquals(expected[i], r.get(k).nextRunLength());
        }
      }
      for (int i = 4; i < r.size(); i++) {
        assertTrue(r.get(i) != r.get(i - 4));
      }
    }
    int[] expected1 = {
      1,
      2,
      4,
      8,
      16,
      32,
      64,
      128,
      256,
      512,
      1024,
      2048,
      4096,
      8192,
      16384,
      32768,
      65536,
      131072,
      262144,
      524288,
      1048576,
      2097152,
      4194304,
      8388608,
      16777216,
      33554432,
      67108864,
      134217728,
      268435456,
      536870912,
      1073741824,
      0x7fffffff,
      0x7fffffff,
      0x7fffffff
    };
    for (int t = 1; t < 12; t++) {
      List<ParallelVariableAnnealingLength> r =
          ParallelVariableAnnealingLength.createRestartSchedules(t, 1);
      assertEquals(t, r.size());
      int groupSize = t < 4 ? t : 4;
      for (int i = 0; i < expected1.length; i++) {
        int j = i % groupSize;
        for (int k = j; k < r.size(); k += 4) {
          assertEquals(expected1[i], r.get(k).nextRunLength());
        }
      }
      for (int i = 4; i < r.size(); i++) {
        assertTrue(r.get(i) != r.get(i - 4));
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> ParallelVariableAnnealingLength.createRestartSchedules(0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> ParallelVariableAnnealingLength.createRestartSchedules(1, 0));
  }

  @Test
  public void testPVALSplit() {
    int[] expected = {
      1000,
      2000,
      4000,
      8000,
      16000,
      32000,
      64000,
      128000,
      256000,
      512000,
      1024000,
      2048000,
      4096000,
      8192000,
      16384000,
      32768000,
      65536000,
      131072000,
      262144000,
      524288000,
      1048576000,
      2097152000,
      0x7fffffff,
      0x7fffffff,
      0x7fffffff
    };
    for (int t = 1; t < 12; t++) {
      List<ParallelVariableAnnealingLength> r =
          ParallelVariableAnnealingLength.createRestartSchedules(t);
      assertEquals(t, r.size());
      int groupSize = t < 4 ? t : 4;
      for (int i = 0; i < expected.length; i++) {
        if (i == expected.length / 2) {
          ParallelVariableAnnealingLength[] splits = new ParallelVariableAnnealingLength[r.size()];
          for (int x = 0; x < splits.length; x++) {
            splits[x] = r.get(x).split();
          }
          for (int i2 = 0; i2 < expected.length; i2++) {
            int j = i2 % groupSize;
            for (int k = j; k < splits.length; k += 4) {
              assertEquals(expected[i2], splits[k].nextRunLength());
            }
          }
        }
        int j = i % groupSize;
        for (int k = j; k < r.size(); k += 4) {
          assertEquals(expected[i], r.get(k).nextRunLength());
        }
      }
      for (int i = 4; i < r.size(); i++) {
        assertTrue(r.get(i) != r.get(i - 4));
      }
    }
    int[] expected1 = {
      1,
      2,
      4,
      8,
      16,
      32,
      64,
      128,
      256,
      512,
      1024,
      2048,
      4096,
      8192,
      16384,
      32768,
      65536,
      131072,
      262144,
      524288,
      1048576,
      2097152,
      4194304,
      8388608,
      16777216,
      33554432,
      67108864,
      134217728,
      268435456,
      536870912,
      1073741824,
      0x7fffffff,
      0x7fffffff,
      0x7fffffff
    };
    for (int t = 1; t < 12; t++) {
      List<ParallelVariableAnnealingLength> r =
          ParallelVariableAnnealingLength.createRestartSchedules(t, 1);
      assertEquals(t, r.size());
      int groupSize = t < 4 ? t : 4;
      for (int i = 0; i < expected1.length; i++) {
        if (i == expected1.length / 2) {
          ParallelVariableAnnealingLength[] splits = new ParallelVariableAnnealingLength[r.size()];
          for (int x = 0; x < splits.length; x++) {
            splits[x] = r.get(x).split();
          }
          for (int i2 = 0; i2 < expected1.length; i2++) {
            int j = i2 % groupSize;
            for (int k = j; k < splits.length; k += 4) {
              assertEquals(expected1[i2], splits[k].nextRunLength());
            }
          }
        }
        int j = i % groupSize;
        for (int k = j; k < r.size(); k += 4) {
          assertEquals(expected1[i], r.get(k).nextRunLength());
        }
      }
      for (int i = 4; i < r.size(); i++) {
        assertTrue(r.get(i) != r.get(i - 4));
      }
    }
  }

  @Test
  public void testResetPVAL() {
    int[] expected = {
      1000,
      2000,
      4000,
      8000,
      16000,
      32000,
      64000,
      128000,
      256000,
      512000,
      1024000,
      2048000,
      4096000,
      8192000,
      16384000,
      32768000,
      65536000,
      131072000,
      262144000,
      524288000,
      1048576000,
      2097152000,
      0x7fffffff,
      0x7fffffff,
      0x7fffffff
    };
    for (int t = 1; t < 5; t++) {
      List<ParallelVariableAnnealingLength> r =
          ParallelVariableAnnealingLength.createRestartSchedules(t);
      for (ParallelVariableAnnealingLength schedule : r) {
        for (int i = 0; i < 2; i++) {
          schedule.nextRunLength();
        }
        schedule.reset();
      }
      int groupSize = t < 4 ? t : 4;
      for (int i = 0; i < expected.length; i++) {
        int j = i % groupSize;
        for (int k = j; k < r.size(); k += 4) {
          assertEquals(expected[i], r.get(k).nextRunLength());
        }
      }
      for (int i = 4; i < r.size(); i++) {
        assertTrue(r.get(i) != r.get(i - 4));
      }
    }
  }
}
