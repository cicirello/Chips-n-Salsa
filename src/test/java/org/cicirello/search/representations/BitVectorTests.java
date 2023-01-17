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

import java.util.Arrays;
import org.junit.jupiter.api.*;

/** JUnit test cases for the BitVector class. */
public class BitVectorTests {

  @Test
  public void testFlipBit() {
    for (int n = 1; n <= 64; n++) {
      BitVector b = new BitVector(n);
      for (int i = 0; i < n; i++) {
        b.flip(i);
        for (int j = 0; j < n; j++) {
          if (j <= i) {
            assertEquals(1, b.getBit(j));
          } else {
            assertEquals(0, b.getBit(j));
          }
        }
      }
      for (int i = 0; i < n; i++) {
        b.flip(i);
        for (int j = 0; j < n; j++) {
          if (j <= i) {
            assertEquals(0, b.getBit(j));
          } else {
            assertEquals(1, b.getBit(j));
          }
        }
      }
    }
  }

  @Test
  public void testSetBitGetBit() {
    int[] evenBits1 = {0x55555555};
    int[] oddBits1 = {0xaaaaaaaa};
    int[] evenBits2 = {0x55555555, 0x55555555};
    int[] oddBits2 = {0xaaaaaaaa, 0xaaaaaaaa};

    for (int n = 1; n <= 64; n++) {
      BitVector odd = new BitVector(n, n <= 32 ? oddBits1 : oddBits2);
      BitVector even = new BitVector(n, n <= 32 ? evenBits1 : evenBits2);
      assertEquals(n, odd.length());
      assertEquals(n, even.length());
      if ((n & 1) == 1) {
        assertEquals(n / 2 + 1, even.countOnes());
        assertEquals(n / 2 + 1, odd.countZeros());
        assertEquals(n / 2, even.countZeros());
        assertEquals(n / 2, odd.countOnes());
      } else {
        assertEquals(n / 2, even.countOnes());
        assertEquals(n / 2, odd.countZeros());
        assertEquals(n / 2, even.countZeros());
        assertEquals(n / 2, odd.countOnes());
      }
      for (int i = 0; i < n; i++) {
        if ((i & 1) == 1) {
          assertEquals(1, odd.getBit(i));
          assertEquals(0, even.getBit(i));
          assertTrue(odd.isOne(i));
          assertFalse(even.isOne(i));
          assertFalse(odd.isZero(i));
          assertTrue(even.isZero(i));
        } else {
          assertEquals(0, odd.getBit(i));
          assertEquals(1, even.getBit(i));
          assertFalse(odd.isOne(i));
          assertTrue(even.isOne(i));
          assertTrue(odd.isZero(i));
          assertFalse(even.isZero(i));
        }
      }
      for (int j = 0; j < n; j++) {
        odd.setBit(j, 1);
        even.setBit(j, 0);
        for (int i = 0; i < n; i++) {
          if (i <= j || (i & 1) == 1) {
            assertEquals(1, odd.getBit(i));
            assertEquals(0, even.getBit(i));
            assertTrue(odd.isOne(i));
            assertFalse(even.isOne(i));
            assertFalse(odd.isZero(i));
            assertTrue(even.isZero(i));
          } else {
            assertEquals(0, odd.getBit(i));
            assertEquals(1, even.getBit(i));
            assertFalse(odd.isOne(i));
            assertTrue(even.isOne(i));
            assertTrue(odd.isZero(i));
            assertFalse(even.isZero(i));
          }
        }
      }
    }
  }

  @Test
  public void testSet32Get32() {
    int[] values = {0x55555555, 0xaaaaaaaa};
    for (int n = 1; n <= 64; n++) {
      BitVector b = new BitVector(n);
      for (int i = 0; i < n; i++) {
        assertEquals(0, b.getBit(i));
      }
      assertEquals(0, b.get32(0));
      if (n > 32) assertEquals(0, b.get32(1));
      b.set32(0, 0x55555555);
      if (n >= 32) {
        assertEquals(0x55555555, b.get32(0), "all even bits should be set in 0th block");
      } else {
        assertEquals(
            0x55555555 & (0xffffffff >>> (32 - n)),
            b.get32(0),
            "all even bits should be set in 0th block");
      }
      if (n > 32) {
        b.set32(1, 0xaaaaaaaa);
        assertEquals(0x55555555, b.get32(0), "all even bits should still be set in 0th block");
        assertEquals(
            0xaaaaaaaa & (0xffffffff >>> (64 - n)),
            b.get32(1),
            "all odd bits should be set in block 1");
      }
    }
  }

  @Test
  public void testPredicates() {
    // 0-length
    {
      BitVector b = new BitVector(0);
      assertTrue(b.allZeros());
      assertFalse(b.anyZeros());
      assertTrue(b.allOnes());
      assertFalse(b.anyOnes());
    }
    for (int n = 1; n <= 66; n++) {
      // All zeros
      BitVector b = new BitVector(n);
      assertTrue(b.allZeros());
      assertTrue(b.anyZeros());
      assertFalse(b.allOnes());
      assertFalse(b.anyOnes());
      // All ones
      b.not();
      assertFalse(b.allZeros());
      assertFalse(b.anyZeros());
      assertTrue(b.allOnes());
      assertTrue(b.anyOnes());
    }
    {
      // One 1-bit case, multiblocks in size
      BitVector b = new BitVector(66);
      for (int i = 0; i < 66; i++) {
        b.setBit(i, 1);
        assertFalse(b.allZeros());
        assertTrue(b.anyZeros());
        assertFalse(b.allOnes());
        assertTrue(b.anyOnes());
        b.setBit(i, 0);
      }
      // One 0-bit case, multiblocks in size
      b.not();
      for (int i = 0; i < 66; i++) {
        b.setBit(i, 0);
        assertFalse(b.allZeros());
        assertTrue(b.anyZeros());
        assertFalse(b.allOnes());
        assertTrue(b.anyOnes());
        b.setBit(i, 1);
      }
    }
  }

  @Test
  public void testConstructorAllZeros() {
    for (int n = 0; n <= 64; n++) {
      BitVector b = new BitVector(n);
      assertEquals(n, b.length());
      assertEquals(0, b.countOnes());
      assertEquals(n, b.countZeros());
      for (int i = 0; i < n; i++) {
        assertEquals(0, b.getBit(i));
        assertFalse(b.isOne(i));
        assertTrue(b.isZero(i));
      }
      if (n > 0) {
        for (int i = 0; i <= (n - 1) / 32; i++) {
          assertEquals(0, b.get32(i), "get32(" + i + ") with n=" + n);
        }
      }
      BitVector b2 = b.copy();
      assertTrue(b != b2);
      assertEquals(b, b2);
      assertEquals(b.hashCode(), b2.hashCode());
    }
    for (int n = 0; n <= 64; n++) {
      BitVector b = new BitVector(n, false);
      assertEquals(n, b.length());
      assertEquals(0, b.countOnes());
      assertEquals(n, b.countZeros());
      for (int i = 0; i < n; i++) {
        assertEquals(0, b.getBit(i));
        assertFalse(b.isOne(i));
        assertTrue(b.isZero(i));
      }
      if (n > 0) {
        for (int i = 0; i <= (n - 1) / 32; i++) {
          assertEquals(0, b.get32(i), "get32(" + i + ") with n=" + n);
        }
      }
      BitVector b2 = b.copy();
      assertTrue(b != b2);
      assertEquals(b, b2);
      assertEquals(b.hashCode(), b2.hashCode());
    }
  }

  @Test
  public void testConstructorBitMask() {
    // p = 0.0 case
    for (int n = 0; n <= 64; n++) {
      BitVector b = new BitVector(n, 0.0);
      assertEquals(n, b.length());
      assertEquals(0, b.countOnes());
      assertEquals(n, b.countZeros());
      for (int i = 0; i < n; i++) {
        assertEquals(0, b.getBit(i));
        assertFalse(b.isOne(i));
        assertTrue(b.isZero(i));
      }
      if (n > 0) {
        for (int i = 0; i <= (n - 1) / 32; i++) {
          assertEquals(0, b.get32(i), "get32(" + i + ") with n=" + n);
        }
      }
      BitVector b2 = b.copy();
      assertTrue(b != b2);
      assertEquals(b, b2);
      assertEquals(b.hashCode(), b2.hashCode());
    }
    // p = 1.0 case
    for (int n = 0; n <= 64; n++) {
      BitVector b = new BitVector(n, 1.0);
      validateAllOnes(b, n);
    }
    // p = 0.25, 0.5, 0.75 cases
    double[] pCases = {0.5, 0.25, 0.75};
    for (double p : pCases) {
      for (int n = 0; n <= 64; n++) {
        BitVector b = new BitVector(n, p);
        validateRandomBitVector(b, n);
      }
    }
  }

  @Test
  public void testConstructorRandom() {
    for (int n = 0; n <= 64; n++) {
      BitVector b = new BitVector(n, true);
      validateRandomBitVector(b, n);
    }
  }

  private void validateAllOnes(BitVector b, int n) {
    assertEquals(n, b.length());
    assertEquals(n, b.countOnes());
    assertEquals(0, b.countZeros());
    for (int i = 0; i < n; i++) {
      assertEquals(1, b.getBit(i));
      assertTrue(b.isOne(i));
      assertFalse(b.isZero(i));
    }
    if (n > 0) {
      for (int i = 0; i <= (n - 1) / 32 - 1; i++) {
        assertEquals(0xffffffff, b.get32(i), "get32(" + i + ") with n=" + n);
      }
      int i = (n - 1) / 32;
      assertEquals(0xffffffff >>> (32 - (n & 0x1f)), b.get32(i), "get32(" + i + ") with n=" + n);
    }
    BitVector b2 = b.copy();
    assertTrue(b != b2);
    assertEquals(b, b2);
    assertEquals(b.hashCode(), b2.hashCode());
  }

  @Test
  public void testConstructorFromIntArray() {
    for (int k = 1; k <= 2; k++) {
      int[] zeros = new int[k];
      int[] ones = new int[k];
      Arrays.fill(ones, 0xffffffff);

      for (int n = 32 * k - 31; n <= 32 * k; n++) {
        BitVector b0 = new BitVector(n, zeros);
        assertEquals(n, b0.length());
        assertEquals(0, b0.countOnes());
        assertEquals(n, b0.countZeros());
        for (int i = 0; i < n; i++) {
          assertEquals(0, b0.getBit(i));
          assertFalse(b0.isOne(i));
          assertTrue(b0.isZero(i));
        }
        for (int i = 0; i <= (n - 1) / 32; i++) {
          assertEquals(0, b0.get32(i), "get32(" + i + ") with n=" + n);
        }
        BitVector b1 = new BitVector(n, ones);
        assertEquals(n, b1.length());
        assertEquals(n, b1.countOnes());
        assertEquals(0, b1.countZeros());
        for (int i = 0; i < n; i++) {
          assertEquals(1, b1.getBit(i));
          assertTrue(b1.isOne(i));
          assertFalse(b1.isZero(i));
        }
        for (int i = 0; i < (n - 1) / 32; i++) {
          assertEquals(0xffffffff, b1.get32(i), "get32(" + i + ") with n=" + n);
        }
        assertEquals(
            0xffffffff >>> (((n - 1) / 32 + 1) * 32 - n),
            b1.get32((n - 1) / 32),
            "get32(" + ((n - 1) / 32) + ") with n=" + n);
        BitVector b2 = b0.copy();
        assertEquals(n, b2.length());
        assertTrue(b0 != b2);
        assertEquals(b0, b2);
        assertEquals(b0.hashCode(), b2.hashCode());
        validateSameBitsRange(b0, b2, 0, n);

        b2 = b1.copy();
        assertEquals(n, b2.length());
        assertTrue(b1 != b2);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        validateSameBitsRange(b1, b2, 0, n);
      }
    }
  }

  @Test
  public void testAND() {
    for (int n = 1; n <= 32; n++) {
      // Consider a truth table with the columns: x, y, x AND Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x AND Y.
      int[] bits0 = {0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0x88888888 & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.and(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    for (int n = 33; n <= 64; n++) {
      // Consider a truth table with the columns: x, y, x AND Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x AND Y.
      int[] bits0 = {0xcccccccc, 0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0x88888888, 0x88888888 & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.and(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    // verify that 0 length doesn't throw an exception
    BitVector b0 = new BitVector(0);
    BitVector b1 = new BitVector(0);
    b0.and(b1);
    assertEquals(0, b0.length());
    assertEquals(0, b1.length());
  }

  @Test
  public void testOR() {
    for (int n = 1; n <= 32; n++) {
      // Consider a truth table with the columns: x, y, x OR Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x OR Y.
      int[] bits0 = {0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0xeeeeeeee & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.or(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    for (int n = 33; n <= 64; n++) {
      // Consider a truth table with the columns: x, y, x OR Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x OR Y.
      int[] bits0 = {0xcccccccc, 0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0xeeeeeeee, 0xeeeeeeee & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.or(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    // verify that 0 length doesn't throw an exception
    BitVector b0 = new BitVector(0);
    BitVector b1 = new BitVector(0);
    b0.or(b1);
    assertEquals(0, b0.length());
    assertEquals(0, b1.length());
  }

  @Test
  public void testXOR() {
    for (int n = 1; n <= 32; n++) {
      // Consider a truth table with the columns: x, y, x XOR Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x XOR Y.
      int[] bits0 = {0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0x66666666 & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.xor(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    for (int n = 33; n <= 64; n++) {
      // Consider a truth table with the columns: x, y, x XOR Y.
      // Each nibble of bits0 is the column for x in this truth table.
      // Each nibble of bits1 is the column for y in this truth table.
      // Each nibble of expected is the column for: x XOR Y.
      int[] bits0 = {0xcccccccc, 0xcccccccc & (0xffffffff >>> (32 - n))};
      int[] bits1 = {0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      int[] expected = {0x66666666, 0x66666666 & (0xffffffff >>> (32 - n))};
      BitVector b0 = new BitVector(n, bits0);
      BitVector b1 = new BitVector(n, bits1);
      BitVector b1copy = new BitVector(n, bits1);
      BitVector bExpected = new BitVector(n, expected);
      b0.xor(b1);
      assertEquals(b1copy, b1, "explicit param shouldn't change");
      assertEquals(bExpected, b0);
      validateSameBitsRange(bExpected, b0, 0, n);
    }
    // verify that 0 length doesn't throw an exception
    BitVector b0 = new BitVector(0);
    BitVector b1 = new BitVector(0);
    b0.xor(b1);
    assertEquals(0, b0.length());
    assertEquals(0, b1.length());
  }

  @Test
  public void testNot() {
    for (int n = 0; n <= 64; n++) {
      BitVector b0 = new BitVector(n);
      b0.not();
      validateRange(b0, 0, n, 1);
      if (n >= 32) {
        assertEquals(0xffffffff, b0.get32(0));
        if (n > 32) assertEquals(0xffffffff >>> 64 - n, b0.get32(1));
      } else if (n > 0) {
        assertEquals(0xffffffff >>> 32 - n, b0.get32(0));
      }
      b0.not();
      validateRange(b0, 0, n, 0);
      if (n > 0) assertEquals(0, b0.get32(0));
      if (n > 32) assertEquals(0, b0.get32(1));
    }
    for (int n = 1; n <= 32; n++) {
      int[] bits = {0x55555555 & (0xffffffff >>> (32 - n))};
      int[] expected = {0xaaaaaaaa & (0xffffffff >>> (32 - n))};
      BitVector b = new BitVector(n, bits);
      BitVector bExpected = new BitVector(n, expected);
      b.not();
      assertEquals(bExpected, b);
      validateSameBitsRange(bExpected, b, 0, n);
    }
    for (int n = 33; n <= 64; n++) {
      int[] bits = {0x55555555, 0x55555555 & (0xffffffff >>> (64 - n))};
      int[] expected = {0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (64 - n))};
      BitVector b = new BitVector(n, bits);
      BitVector bExpected = new BitVector(n, expected);
      b.not();
      assertEquals(bExpected, b);
      validateSameBitsRange(bExpected, b, 0, n);
    }
  }

  @Test
  public void testShiftLeft() {
    for (int n = 1; n <= 96; n++) {
      BitVector original = new BitVector(n);
      original.setBit(0, 1);
      original.setBit(n - 1, 1);
      original.setBit(n / 2, 1);
      for (int shift = 0; shift <= n; shift++) {
        BitVector shifted = original.copy();
        shifted.shiftLeft(shift);
        validateRange(shifted, 0, shift, 0);
        for (int i = shift; i < n; i++) {
          assertEquals(original.getBit(i - shift), shifted.getBit(i));
        }
      }
      BitVector shifted = original.copy();
      shifted.shiftLeft(n + 1);
      validateRange(shifted, 0, n, 0);
    }
    BitVector original = new BitVector(0);
    original.shiftLeft(1);
    assertEquals(0, original.length());
  }

  @Test
  public void testShiftRight() {
    for (int n = 1; n <= 96; n++) {
      BitVector original = new BitVector(n);
      original.setBit(0, 1);
      original.setBit(n - 1, 1);
      original.setBit(n / 2, 1);
      for (int shift = 0; shift <= n; shift++) {
        BitVector shifted = original.copy();
        shifted.shiftRight(shift);
        for (int i = 0; i + shift < n; i++) {
          assertEquals(original.getBit(i + shift), shifted.getBit(i));
        }
        validateRange(shifted, n - shift, n, 0);
      }
      BitVector shifted = original.copy();
      shifted.shiftRight(n + 1);
      validateRange(shifted, 0, n, 0);
    }
    BitVector original = new BitVector(0);
    original.shiftRight(1);
    assertEquals(0, original.length());
  }

  @Test
  public void testNotEquals() {
    BitVector b1 = new BitVector(1);
    BitVector b2 = new BitVector(2);
    assertFalse(b1.equals("hello"));
    assertFalse(b1.equals(null));
    assertNotEquals(b1, b2);
  }

  @Test
  public void testToString() {
    for (int n = 0; n <= 96; n++) {
      // tests with all 0s.
      BitVector b0 = new BitVector(n);
      char[] c = new char[n];
      Arrays.fill(c, '0');
      String expected = new String(c);
      assertEquals(expected, b0.toString());

      // tests with all 1s
      BitVector b1 = new BitVector(n);
      b1.not();
      Arrays.fill(c, '1');
      expected = new String(c);
      assertEquals(expected, b1.toString());
    }
    for (int n = 1; n <= 64; n++) {
      for (int i = 0; i < n; i++) {
        // test with exactly one 1.
        BitVector b = new BitVector(n);
        b.flip(i);
        char[] c = new char[n];
        Arrays.fill(c, '0');
        c[n - 1 - i] = '1';
        String expected = new String(c);
        assertEquals(expected, b.toString());

        // test with exactly one 0.
        b = new BitVector(n);
        b.flip(i);
        b.not();
        Arrays.fill(c, '1');
        c[n - 1 - i] = '0';
        expected = new String(c);
        assertEquals(expected, b.toString());
      }
    }
  }

  @Test
  public void testExceptions() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new BitVector(-1));
    thrown = assertThrows(IllegalArgumentException.class, () -> new BitVector(-1, new int[1]));
    thrown = assertThrows(IllegalArgumentException.class, () -> new BitVector(-1, 0.5));
    thrown = assertThrows(IllegalArgumentException.class, () -> new BitVector(32, new int[2]));
    thrown = assertThrows(IllegalArgumentException.class, () -> new BitVector(33, new int[1]));
    final BitVector b = new BitVector(32);
    final BitVector b2 = new BitVector(31);
    thrown = assertThrows(IllegalArgumentException.class, () -> b.and(b2));
    thrown = assertThrows(IllegalArgumentException.class, () -> b.or(b2));
    thrown = assertThrows(IllegalArgumentException.class, () -> b.xor(b2));

    IndexOutOfBoundsException thrownBounds =
        assertThrows(IndexOutOfBoundsException.class, () -> b.getBit(-1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.getBit(32));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.setBit(-1, 1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.setBit(32, 1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.flip(-1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.flip(32));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.get32(-1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.get32(1));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.set32(-1, 0));
    thrownBounds = assertThrows(IndexOutOfBoundsException.class, () -> b.set32(1, 0));
  }

  private void validateRandomBitVector(BitVector b, int n) {
    assertEquals(n, b.length());
    assertEquals(n, b.countZeros() + b.countOnes());
    for (int i = 0; i < n; i++) {
      int bit = b.getBit(i);
      assertTrue(bit == 0 || bit == 1);
      assertNotEquals(b.isOne(i), b.isZero(i));
    }
    BitVector b2 = b.copy();
    assertEquals(n, b2.length());
    assertTrue(b != b2);
    assertEquals(b, b2);
    assertEquals(b.hashCode(), b2.hashCode());
    validateSameBitsRange(b, b2, 0, n);
  }

  /*
   * Validate all bits in a range equal to a specific bit value, range indexes are
   * from i (inclusive) to j (exclusive).
   */
  private void validateRange(BitVector b, int i, int j, int bitValue) {
    for (int k = i; k < j; k++) {
      assertEquals(bitValue, b.getBit(k));
    }
  }

  /*
   * Validate all bits in a range equal, range indexes are
   * from i (inclusive) to j (exclusive).
   */
  private void validateSameBitsRange(BitVector expected, BitVector b, int i, int j) {
    for (int k = i; k < j; k++) {
      assertEquals(expected.getBit(k), b.getBit(k));
    }
  }
}
