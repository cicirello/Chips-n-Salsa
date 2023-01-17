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

/** JUnit test cases for the BitVector.BitIterator class. */
public class BitVectorBitIteratorTests {

  @Test
  public void testIteratorExceptions() {
    final BitVector b = new BitVector(32);
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> b.bitIterator(0));
    thrown = assertThrows(IllegalArgumentException.class, () -> b.bitIterator(33));
    final BitVector.BitIterator iter = b.bitIterator();
    thrown = assertThrows(IllegalArgumentException.class, () -> iter.nextBitBlock(0));
    thrown = assertThrows(IllegalArgumentException.class, () -> iter.nextBitBlock(33));
  }

  @Test
  public void testIteratorSkip() {
    for (int s = 0; s <= 65; s++) {
      BitVector b = new BitVector(s + 1);
      b.flip(s);
      final BitVector.BitIterator iter = b.bitIterator();
      iter.skip(s);
      assertEquals(1, iter.nextBit());
      IllegalArgumentException thrown =
          assertThrows(IllegalArgumentException.class, () -> iter.skip(1));
    }
    for (int s = 0; s <= 65; s++) {
      BitVector b = new BitVector(s + 32);
      b.flip(31 + s);
      BitVector.BitIterator iter = b.bitIterator();
      iter.skip(31);
      iter.skip(s);
      assertEquals(1, iter.nextBit());
    }
  }

  @Test
  public void testBitIteratorNextLargeBitBlock() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
      {0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
      {0x88fac688, 0xc688fac6}, // use for k = 3
      {0x76543210, 0xfedcba98}, // use for k = 4
      {0x8a418820, 0xc5a92839} // use for k = 5
    };
    // small block cases
    for (int n = 1; n <= 64; n++) {
      int modulus = 2;
      for (int i = 0; i < bits.length; i++) {
        int[] array;
        if (n > 32) array = bits[i];
        else {
          array = new int[1];
          array[0] = bits[i][0];
        }
        BitVector b = new BitVector(n, array);
        int k = i + 1;
        BitVector.BitIterator iter = b.bitIterator(k);
        int counter = 0;
        int leftOver = n;
        int numCalls = 0;
        while (iter.hasNext()) {
          int[] x = iter.nextLargeBitBlock(Math.min(k, iter.numRemainingBits()));
          assertEquals(1, x.length);
          numCalls++;
          if (iter.hasNext()) {
            assertEquals(counter, x[0], "n,k=" + n + "," + k);
          } else {
            int mask = 0xffffffff >>> (32 - leftOver);
            assertEquals(counter & mask, x[0], "n,k=" + n + "," + k);
          }
          leftOver -= k;
          counter++;
          counter %= modulus;
        }
        int expectedCalls = n / k;
        if (n % k != 0) expectedCalls++;
        assertEquals(expectedCalls, numCalls);
        modulus *= 2;
      }
    }
    // large block cases
    final int FIRST_32 = 0xF8F4F2F1;
    for (int i = 0; i < bits.length; i++) {
      int[] temp = bits[i];
      bits[i] = new int[3];
      bits[i][0] = FIRST_32;
      bits[i][1] = temp[0];
      bits[i][2] = temp[1];
    }
    int[] last4 = {0xA, 0x4, 0x8, 0x0, 0x0};
    for (int i = 0; i < bits.length; i++) {
      BitVector b = new BitVector(96, bits[i]);
      BitVector.BitIterator iter = b.bitIterator();
      int[] observed = iter.nextLargeBitBlock(36);
      assertEquals(2, observed.length);
      assertEquals(FIRST_32, observed[0]);
      assertEquals(last4[i], observed[1]);
      iter = b.bitIterator();
      observed = iter.nextLargeBitBlock(32);
      assertEquals(1, observed.length);
      assertEquals(FIRST_32, observed[0]);
    }
    last4 = new int[] {0xA, 0x4, 0x6, 0x8, 0x9};
    for (int i = 0; i < bits.length; i++) {
      BitVector b = new BitVector(96, bits[i]);
      BitVector.BitIterator iter = b.bitIterator();
      int[] observed = iter.nextLargeBitBlock(68);
      assertEquals(3, observed.length);
      assertEquals(FIRST_32, observed[0]);
      assertEquals(bits[i][1], observed[1]);
      assertEquals(last4[i], observed[2]);
      iter = b.bitIterator();
      observed = iter.nextLargeBitBlock(64);
      assertEquals(2, observed.length);
      assertEquals(FIRST_32, observed[0]);
      assertEquals(bits[i][1], observed[1]);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              BitVector b = new BitVector(35);
              BitVector.BitIterator iter = b.bitIterator();
              iter.nextLargeBitBlock(36);
            });
  }

  @Test
  public void testBitIteratorNextBitBlock() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
      {0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
      {0x88fac688, 0xc688fac6}, // use for k = 3
      {0x76543210, 0xfedcba98}, // use for k = 4
      {0x8a418820, 0xc5a92839} // use for k = 5
    };
    for (int n = 1; n <= 64; n++) {
      int modulus = 2;
      for (int i = 0; i < bits.length; i++) {
        int[] array;
        if (n > 32) array = bits[i];
        else {
          array = new int[1];
          array[0] = bits[i][0];
        }
        BitVector b = new BitVector(n, array);
        int k = i + 1;
        BitVector.BitIterator iter = b.bitIterator(k);
        int counter = 0;
        int leftOver = n;
        int numCalls = 0;
        while (iter.hasNext()) {
          int x = iter.nextBitBlock();
          numCalls++;
          if (iter.hasNext()) {
            assertEquals(counter, x, "n,k=" + n + "," + k);
          } else {
            int mask = 0xffffffff >>> (32 - leftOver);
            assertEquals(counter & mask, x, "n,k=" + n + "," + k);
          }
          leftOver -= k;
          counter++;
          counter %= modulus;
        }
        int expectedCalls = n / k;
        if (n % k != 0) expectedCalls++;
        assertEquals(expectedCalls, numCalls);
        modulus *= 2;
      }
    }
  }

  @Test
  public void testBitIteratorNextBitBlockWithParam() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
      {0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
      {0x88fac688, 0xc688fac6}, // use for k = 3
      {0x76543210, 0xfedcba98}, // use for k = 4
      {0x8a418820, 0xc5a92839} // use for k = 5
    };
    for (int n = 1; n <= 64; n++) {
      int modulus = 2;
      for (int i = 0; i < bits.length; i++) {
        int[] array;
        if (n > 32) array = bits[i];
        else {
          array = new int[1];
          array[0] = bits[i][0];
        }
        BitVector b = new BitVector(n, array);
        int k = i + 1;
        // initialize with block size other than k to confirm default can be overridden
        final BitVector.BitIterator iter = b.bitIterator(k != 32 ? 32 : 30);
        int counter = 0;
        int leftOver = n;
        int numCalls = 0;
        while (iter.hasNext()) {
          // get a block by overriding default block size
          int x = iter.nextBitBlock(k);
          numCalls++;
          if (iter.hasNext()) {
            assertEquals(counter, x, "n,k=" + n + "," + k);
          } else {
            int mask = 0xffffffff >>> (32 - leftOver);
            assertEquals(counter & mask, x, "n,k=" + n + "," + k);
          }
          leftOver -= k;
          counter++;
          counter %= modulus;
        }
        IllegalStateException thrown =
            assertThrows(IllegalStateException.class, () -> iter.nextBitBlock(k));
        int expectedCalls = n / k;
        if (n % k != 0) expectedCalls++;
        assertEquals(expectedCalls, numCalls);
        modulus *= 2;
      }
    }
  }

  @Test
  public void testBitIteratorDefaultBlockSizeNextBitBlockWithParam() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
      {0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
      {0x88fac688, 0xc688fac6}, // use for k = 3
      {0x76543210, 0xfedcba98}, // use for k = 4
      {0x8a418820, 0xc5a92839} // use for k = 5
    };
    for (int n = 1; n <= 64; n++) {
      int modulus = 2;
      for (int i = 0; i < bits.length; i++) {
        int[] array;
        if (n > 32) array = bits[i];
        else {
          array = new int[1];
          array[0] = bits[i][0];
        }
        BitVector b = new BitVector(n, array);
        int k = i + 1;
        // initialize with default block size of 1 to confirm can be overridden
        BitVector.BitIterator iter = b.bitIterator();
        int counter = 0;
        int leftOver = n;
        int numCalls = 0;
        while (iter.hasNext()) {
          // get a block by overriding default block size
          int x = iter.nextBitBlock(k);
          numCalls++;
          if (iter.hasNext()) {
            assertEquals(counter, x, "n,k=" + n + "," + k);
          } else {
            int mask = 0xffffffff >>> (32 - leftOver);
            assertEquals(counter & mask, x, "n,k=" + n + "," + k);
          }
          leftOver -= k;
          counter++;
          counter %= modulus;
        }
        int expectedCalls = n / k;
        if (n % k != 0) expectedCalls++;
        assertEquals(expectedCalls, numCalls);
        modulus *= 2;
      }
    }
  }

  @Test
  public void testBitIteratorNextBit() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa},
      {0xaaaaaaaa}
    };
    for (int n = 1; n <= 64; n++) {
      int[] array = (n > 32) ? bits[0] : bits[1];
      BitVector b = new BitVector(n, array);
      // deliberately have k != 1 to confirm nextBit ignores blocksize
      BitVector.BitIterator iter = b.bitIterator(10);
      int counter = 0;
      int leftOver = n;
      while (iter.hasNext()) {
        assertEquals(n - counter, iter.numRemainingBits());
        int x = iter.nextBit();
        assertEquals(counter % 2, x, "n=" + n);
        leftOver--;
        counter++;
      }
      assertEquals(n, counter);
      assertEquals(0, iter.numRemainingBits());
    }
  }

  @Test
  public void testBitIteratorDefaultBlockSize() {
    int[][] bits = {
      {0xaaaaaaaa, 0xaaaaaaaa},
      {0xaaaaaaaa}
    };
    for (int n = 1; n <= 64; n++) {
      int[] array = (n > 32) ? bits[0] : bits[1];
      BitVector b = new BitVector(n, array);
      final BitVector.BitIterator iter = b.bitIterator();
      int counter = 0;
      int leftOver = n;
      while (iter.hasNext()) {
        int x = iter.nextBitBlock();
        assertEquals(counter % 2, x, "n=" + n);
        leftOver--;
        counter++;
      }
      assertEquals(n, counter);
      IllegalStateException thrown =
          assertThrows(IllegalStateException.class, () -> iter.nextBitBlock());
      thrown = assertThrows(IllegalStateException.class, () -> iter.nextBit());
    }
  }
}
