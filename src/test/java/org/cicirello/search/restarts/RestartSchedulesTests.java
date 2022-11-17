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

package org.cicirello.search.restarts;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.*;

/** JUnit test cases for restart schedules. */
public class RestartSchedulesTests {

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

  @Test
  public void testVAL() {
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
    VariableAnnealingLength r = new VariableAnnealingLength();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], r.nextRunLength());
    }
    r.reset();
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], r.nextRunLength());
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
    r = new VariableAnnealingLength(1);
    for (int i = 0; i < expected1.length; i++) {
      assertEquals(expected1[i], r.nextRunLength());
    }
    r.reset();
    for (int i = 0; i < expected1.length; i++) {
      assertEquals(expected1[i], r.nextRunLength());
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new VariableAnnealingLength(0));
  }

  @Test
  public void testVALcreateSchedules() {
    for (int n = 1; n <= 3; n++) {
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
      List<VariableAnnealingLength> schedules = VariableAnnealingLength.createRestartSchedules(n);
      assertEquals(n, schedules.size());
      for (VariableAnnealingLength r : schedules) {
        for (int i = 0; i < expected.length; i++) {
          assertEquals(expected[i], r.nextRunLength());
        }
        r.reset();
        for (int i = 0; i < expected.length; i++) {
          assertEquals(expected[i], r.nextRunLength());
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
      schedules = VariableAnnealingLength.createRestartSchedules(n, 1);
      assertEquals(n, schedules.size());
      for (VariableAnnealingLength r : schedules) {
        for (int i = 0; i < expected1.length; i++) {
          assertEquals(expected1[i], r.nextRunLength());
        }
        r.reset();
        for (int i = 0; i < expected1.length; i++) {
          assertEquals(expected1[i], r.nextRunLength());
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> VariableAnnealingLength.createRestartSchedules(0, 1));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> VariableAnnealingLength.createRestartSchedules(1, 0));
  }

  @Test
  public void testVALSplit() {
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
    for (int s = 0; s < 2 * expected.length; s++) {
      VariableAnnealingLength r = new VariableAnnealingLength();
      VariableAnnealingLength split = null;
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
    for (int s = 0; s < 2 * expected1.length; s++) {
      VariableAnnealingLength r = new VariableAnnealingLength(1);
      VariableAnnealingLength split = null;
      for (int i = 0; i < expected1.length; i++) {
        if (i == s) {
          split = r.split();
          for (int j = 0; j < expected1.length; j++) {
            assertEquals(expected1[j], split.nextRunLength());
          }
        }
        assertEquals(expected1[i], r.nextRunLength());
      }
      r.reset();
      for (int i = 0; i < expected1.length; i++) {
        if (split == null && expected1.length + i == s) {
          split = r.split();
          for (int j = 0; j < expected1.length; j++) {
            assertEquals(expected1[j], split.nextRunLength());
          }
        }
        assertEquals(expected1[i], r.nextRunLength());
      }
    }
  }

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
