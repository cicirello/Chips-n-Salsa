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

package org.cicirello.search.representations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test cases for the BitVector.exchangeBits static methods. */
public class BitVectorExchangeBitsTests {

  @Test
  public void testExchangeBits_Exceptions() {
    IndexOutOfBoundsException thrown =
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(32), -1, 1));
    thrown =
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(32), 1, -1));
    thrown =
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(32), 32, 1));
    thrown =
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(32), 1, 32));
    IllegalArgumentException thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(33), 1, 1));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(32), new BitVector(31)));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () -> BitVector.exchangeBits(new BitVector(32), new BitVector(31), new BitVector(32)));
    thrown2 =
        assertThrows(
            IllegalArgumentException.class,
            () -> BitVector.exchangeBits(new BitVector(31), new BitVector(32), new BitVector(32)));
  }

  @Test
  public void testExchangeBitsViaMask() {
    int[] maskBits = {
      0xffffffff, 0x00000000, 0xff00ff00, 0x00ff00ff, 0x00ff00ff, 0xff00ff00, 0xff00ff00, 0x00ff00ff
    };
    int[] bits1 = {
      0x00000000, 0x00000000, 0x00000000, 0x00000000, 0xff00ff00, 0x00ff00ff, 0xff00ff00, 0x00ff00ff
    };
    int[] bits2 = {
      0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0x00ff00ff, 0xff00ff00, 0x00ff00ff, 0xff00ff00
    };
    int[] expected1 = {
      0xffffffff, 0x00000000, 0xff00ff00, 0x00ff00ff, 0xffffffff, 0xffffffff, 0x00000000, 0x00000000
    };
    int[] expected2 = {
      0x00000000, 0xffffffff, 0x00ff00ff, 0xff00ff00, 0x00000000, 0x00000000, 0xffffffff, 0xffffffff
    };
    BitVector b1 = new BitVector(bits1.length * 32, bits1);
    BitVector b2 = new BitVector(bits2.length * 32, bits2);
    BitVector bMask = new BitVector(maskBits.length * 32, maskBits);
    BitVector bExpect1 = new BitVector(expected1.length * 32, expected1);
    BitVector bExpect2 = new BitVector(expected2.length * 32, expected2);
    BitVector.exchangeBits(b1, b2, bMask);
    assertEquals(bExpect1, b1);
    assertEquals(bExpect2, b2);
  }

  @Test
  public void testExchangeBits_PartialBlocksAtEnds() {
    // Swapped bits opposite
    for (int n = 96; n <= 128; n += 32) {
      // b1 0s
      for (int i = 1; i < 32; i++) {
        for (int j = n - 32; j < n - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
      // b2 0s
      for (int i = 1; i < 32; i++) {
        for (int j = n - 32; j < n - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 96; n <= 128; n += 32) {
      for (int i = 1; i < 32; i++) {
        for (int j = n - 32; j < n - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 96; n <= 128; n += 32) {
      for (int i = 1; i < 32; i++) {
        for (int j = n - 32; j < n - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
    }
  }

  @Test
  public void testExchangeBits_MultipleWholeBlocks() {
    // Swapped bits opposite
    for (int n = 32; n <= 128; n += 32) {
      // b1 0s
      for (int i = 0; i < n; i += 32) {
        for (int j = i + 63; j < n; j += 32) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
      // b2 0s
      for (int i = 0; i < n; i += 32) {
        for (int j = i + 63; j < n; j += 32) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 128; n += 32) {
      for (int i = 0; i < n; i += 32) {
        for (int j = i + 63; j < n; j += 32) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 128; n += 32) {
      for (int i = 0; i < n; i += 32) {
        for (int j = i + 63; j < n; j += 32) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
    }
  }

  @Test
  public void testExchangeBits_SingleWholeBlock() {
    // Swapped bits opposite
    for (int n = 32; n <= 96; n += 32) {
      // b1 0s
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b2.not();
        BitVector.exchangeBits(b1, b2, i, j);
        for (int k = 0; k < i; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
        for (int k = i; k <= j; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
        for (int k = j + 1; k < i; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
      }
      // b2 0s
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b1.not();
        BitVector.exchangeBits(b1, b2, i, j);
        for (int k = 0; k < i; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
        for (int k = i; k <= j; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
        for (int k = j + 1; k < i; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        BitVector.exchangeBits(b1, b2, i, j);
        for (int k = 0; k < n; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b1.not();
        b2.not();
        BitVector.exchangeBits(b1, b2, i, j);
        for (int k = 0; k < n; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
      }
    }
  }

  @Test
  public void testExchangeBits_SinglePartialBlock0() {
    // Swapped bits opposite
    for (int n = 32; n <= 96; n += 32) {
      // b1 0s
      for (int i = 0; i < n; i += 32) {
        for (int j = i; j < i + 31; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
      // b2 0s
      for (int i = 0; i < n; i += 32) {
        for (int j = i; j < i + 31; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        for (int j = i; j < i + 31; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        for (int j = i; j < i + 31; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
    }
  }

  @Test
  public void testExchangeBits_SinglePartialBlock31() {
    // Swapped bits opposite
    for (int n = 32; n <= 96; n += 32) {
      // b1 0s
      for (int j = 31; j < n; j += 32) {
        for (int i = j; i > j - 31; i--) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
      // b2 0s
      for (int j = 31; j < n; j += 32) {
        for (int i = j; i > j - 31; i--) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 96; n += 32) {
      for (int j = 31; j < n; j += 32) {
        for (int i = j; i > j - 31; i--) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 96; n += 32) {
      for (int j = 31; j < n; j += 32) {
        for (int i = j; i > j - 31; i--) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
    }
  }

  @Test
  public void testExchangeBits_SinglePartialBlockMiddle() {
    // Swapped bits opposite
    for (int n = 32; n <= 96; n += 32) {
      // b1 0s
      for (int i = 1; i < n; i += 2) {
        int m = (i / 32) * 32 + 32;
        for (int j = i; j < m - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
      // b2 0s
      for (int i = 1; i < n; i += 2) {
        int m = (i / 32) * 32 + 32;
        for (int j = i; j < m - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
          for (int k = i; k <= j; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
          for (int k = j + 1; k < i; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 1; i < n; i += 2) {
        int m = (i / 32) * 32 + 32;
        for (int j = i; j < m - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(0, b1.getBit(k));
            assertEquals(0, b2.getBit(k));
          }
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 1; i < n; i += 2) {
        int m = (i / 32) * 32 + 32;
        for (int j = i; j < m - 1; j++) {
          BitVector b1 = new BitVector(n);
          BitVector b2 = new BitVector(n);
          b1.not();
          b2.not();
          BitVector.exchangeBits(b1, b2, i, j);
          for (int k = 0; k < n; k++) {
            assertEquals(1, b1.getBit(k));
            assertEquals(1, b2.getBit(k));
          }
        }
      }
    }
  }

  @Test
  public void testExchangeBits_SingleWholeBlock_IndexesReversed() {
    // Swapped bits opposite
    for (int n = 32; n <= 96; n += 32) {
      // b1 0s
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b2.not();
        BitVector.exchangeBits(b1, b2, j, i);
        for (int k = 0; k < i; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
        for (int k = i; k <= j; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
        for (int k = j + 1; k < i; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
      }
      // b2 0s
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b1.not();
        BitVector.exchangeBits(b1, b2, j, i);
        for (int k = 0; k < i; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
        for (int k = i; k <= j; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
        for (int k = j + 1; k < i; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
      }
    }
    // Swapped bits same as 0.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        BitVector.exchangeBits(b1, b2, j, i);
        for (int k = 0; k < n; k++) {
          assertEquals(0, b1.getBit(k));
          assertEquals(0, b2.getBit(k));
        }
      }
    }
    // Swapped bits same as 1.
    for (int n = 32; n <= 96; n += 32) {
      for (int i = 0; i < n; i += 32) {
        int j = i + 31;
        BitVector b1 = new BitVector(n);
        BitVector b2 = new BitVector(n);
        b1.not();
        b2.not();
        BitVector.exchangeBits(b1, b2, j, i);
        for (int k = 0; k < n; k++) {
          assertEquals(1, b1.getBit(k));
          assertEquals(1, b2.getBit(k));
        }
      }
    }
  }
}
