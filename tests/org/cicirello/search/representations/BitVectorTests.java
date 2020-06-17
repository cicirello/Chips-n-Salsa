/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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

import org.junit.*;
import static org.junit.Assert.*;
import java.util.Arrays;

/**
 * JUnit 4 test cases for the BitVector class.
 */
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
		int[] evenBits1 = { 0x55555555 };
		int[] oddBits1 = { 0xaaaaaaaa };
		int[] evenBits2 = { 0x55555555, 0x55555555 };
		int[] oddBits2 = { 0xaaaaaaaa, 0xaaaaaaaa };
		
		for (int n = 1; n <= 64; n++) {
			BitVector odd = new BitVector(n, n <= 32 ? oddBits1 : oddBits2);
			BitVector even = new BitVector(n, n <= 32 ? evenBits1 : evenBits2);
			assertEquals(n, odd.length());
			assertEquals(n, even.length());
			if ((n&1)==1) {
				assertEquals(n/2+1, even.countOnes());
				assertEquals(n/2+1, odd.countZeros());
				assertEquals(n/2, even.countZeros());
				assertEquals(n/2, odd.countOnes());
			} else {
				assertEquals(n/2, even.countOnes());
				assertEquals(n/2, odd.countZeros());
				assertEquals(n/2, even.countZeros());
				assertEquals(n/2, odd.countOnes());
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
		int[] values = { 0x55555555, 0xaaaaaaaa };
		for (int n = 1; n <= 64; n++) {
			BitVector b = new BitVector(n);
			for (int i = 0; i < n; i++) {
				assertEquals("all bits should be 0 initially", 0, b.getBit(i));
			}
			assertEquals("first block should be all 0 bits initially", 0, b.get32(0));
			if (n > 32) assertEquals("second block should be all 0 bits initially", 0, b.get32(1));
			b.set32(0, 0x55555555);
			if (n >= 32) {
				assertEquals("all even bits should be set in 0th block", 0x55555555, b.get32(0));
			} else {
				assertEquals("all even bits should be set in 0th block", 0x55555555 & (0xffffffff >>> (32-n)), b.get32(0));
			}
			if (n > 32) {
				b.set32(1, 0xaaaaaaaa);
				assertEquals("all even bits should still be set in 0th block", 0x55555555, b.get32(0));
				assertEquals("all odd bits should be set in block 1", 0xaaaaaaaa & (0xffffffff >>> (64-n)), b.get32(1));
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
				for (int i = 0; i <= (n-1)/32; i++) {
					assertEquals("get32("+i+") with n="+n, 0, b.get32(i));
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
				for (int i = 0; i <= (n-1)/32; i++) {
					assertEquals("get32("+i+") with n="+n, 0, b.get32(i));
				}
			}
			BitVector b2 = b.copy();
			assertTrue(b != b2);
			assertEquals(b, b2);
			assertEquals(b.hashCode(), b2.hashCode());
		}
	}
	
	@Test
	public void testConstructorRandom() {
		for (int n = 0; n <= 64; n++) {
			BitVector b = new BitVector(n, true);
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
			for (int i = 0; i < n; i++) {
				assertEquals(b.getBit(i), b2.getBit(i));
			}
		}
	}
	
	@Test
	public void testConstructorFromIntArray() {
		for (int k = 1; k <= 2; k++) {
			int[] zeros = new int[k];
			int[] ones = new int[k];
			for (int j = 0; j < k; j++) {
				ones[j] = 0xffffffff;
			}
			for (int n = 32*k-31; n <= 32*k; n++) {
				BitVector b0 = new BitVector(n, zeros);
				assertEquals(n, b0.length());
				assertEquals(0, b0.countOnes());
				assertEquals(n, b0.countZeros());
				for (int i = 0; i < n; i++) {
					assertEquals(0, b0.getBit(i));
					assertFalse(b0.isOne(i));
					assertTrue(b0.isZero(i));
				}
				if (n > 0) {
					for (int i = 0; i <= (n-1)/32; i++) {
						assertEquals("get32("+i+") with n="+n, 0, b0.get32(i));
					}
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
				if (n > 0) {
					for (int i = 0; i < (n-1)/32; i++) {
						assertEquals("get32("+i+") with n="+n, 0xffffffff, b1.get32(i));
					}
					assertEquals("get32("+((n-1)/32)+") with n="+n, 0xffffffff >>> (((n-1)/32+1)*32-n), b1.get32((n-1)/32));
				}
				BitVector b2 = b0.copy();
				assertEquals(n, b2.length());
				assertTrue(b0 != b2);
				assertEquals(b0, b2);
				assertEquals(b0.hashCode(), b2.hashCode());
				for (int i = 0; i < n; i++) {
					assertEquals(b0.getBit(i), b2.getBit(i));
				}
				b2 = b1.copy();
				assertEquals(n, b2.length());
				assertTrue(b1 != b2);
				assertEquals(b1, b2);
				assertEquals(b1.hashCode(), b2.hashCode());
				for (int i = 0; i < n; i++) {
					assertEquals(b1.getBit(i), b2.getBit(i));
				}
			}			
		}
	}
	
	@Test
	public void testBitIteratorNextBitBlock() {
		int[][] bits = {
			{ 0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
			{ 0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
			{ 0x88fac688, 0xc688fac6}, // use for k = 3
			{ 0x76543210, 0xfedcba98}, // use for k = 4
			{ 0x8a418820, 0xc5a92839}  // use for k = 5
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
				int k = i+1;
				BitVector.BitIterator iter = b.bitIterator(k);
				int counter = 0;
				int leftOver = n;
				int numCalls = 0;
				while (iter.hasNext()) {
					int x = iter.nextBitBlock();
					numCalls++;
					if (iter.hasNext()) {
						assertEquals("n,k="+n+","+k, counter, x);
					} else {
						int mask = 0xffffffff >>> (32-leftOver);
						assertEquals("n,k="+n+","+k, counter & mask, x);
					}
					leftOver -= k;
					counter++;
					counter %= modulus;
				}
				int expectedCalls = n / k;
				if (n % k != 0) expectedCalls++;
				assertEquals("verify number of calls", expectedCalls, numCalls);
				modulus *= 2;
			}
		}
	}
	
	@Test
	public void testBitIteratorNextBitBlockWithParam() {
		int[][] bits = {
			{ 0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
			{ 0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
			{ 0x88fac688, 0xc688fac6}, // use for k = 3
			{ 0x76543210, 0xfedcba98}, // use for k = 4
			{ 0x8a418820, 0xc5a92839}  // use for k = 5
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
				int k = i+1;
				// initialize with block size other than k to confirm default can be overridden
				BitVector.BitIterator iter = b.bitIterator( k!=32 ? 32 : 30);
				int counter = 0;
				int leftOver = n;
				int numCalls = 0;
				while (iter.hasNext()) {
					// get a block by overriding default block size
					int x = iter.nextBitBlock(k);
					numCalls++;
					if (iter.hasNext()) {
						assertEquals("n,k="+n+","+k, counter, x);
					} else {
						int mask = 0xffffffff >>> (32-leftOver);
						assertEquals("n,k="+n+","+k, counter & mask, x);
					}
					leftOver -= k;
					counter++;
					counter %= modulus;
				}
				int expectedCalls = n / k;
				if (n % k != 0) expectedCalls++;
				assertEquals("verify number of calls", expectedCalls, numCalls);
				modulus *= 2;
			}
		}
	}
	
	@Test
	public void testBitIteratorDefaultBlockSizeNextBitBlockWithParam() {
		int[][] bits = {
			{ 0xaaaaaaaa, 0xaaaaaaaa}, // use for k = 1
			{ 0xe4e4e4e4, 0xe4e4e4e4}, // use for k = 2
			{ 0x88fac688, 0xc688fac6}, // use for k = 3
			{ 0x76543210, 0xfedcba98}, // use for k = 4
			{ 0x8a418820, 0xc5a92839}  // use for k = 5
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
				int k = i+1;
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
						assertEquals("n,k="+n+","+k, counter, x);
					} else {
						int mask = 0xffffffff >>> (32-leftOver);
						assertEquals("n,k="+n+","+k, counter & mask, x);
					}
					leftOver -= k;
					counter++;
					counter %= modulus;
				}
				int expectedCalls = n / k;
				if (n % k != 0) expectedCalls++;
				assertEquals("verify number of calls", expectedCalls, numCalls);
				modulus *= 2;
			}
		}
	}
	
	@Test
	public void testBitIteratorNextBit() {
		int[][] bits = {
			{ 0xaaaaaaaa, 0xaaaaaaaa},
			{ 0xaaaaaaaa }
		};
		for (int n = 1; n <= 64; n++) {
			int[] array = (n > 32) ? bits[0] : bits[1];
			BitVector b = new BitVector(n, array);
			// deliberately have k != 1 to confirm nextBit ignores blocksize
			BitVector.BitIterator iter = b.bitIterator(10); 
			int counter = 0;
			int leftOver = n;
			while (iter.hasNext()) {
				int x = iter.nextBit();
				assertEquals("n="+n, counter % 2, x);
				leftOver--;
				counter++;
			}
			assertEquals("verify number of calls", n, counter);
		}
	}
	
	@Test
	public void testBitIteratorDefaultBlockSize() {
		int[][] bits = {
			{ 0xaaaaaaaa, 0xaaaaaaaa},
			{ 0xaaaaaaaa }
		};
		for (int n = 1; n <= 64; n++) {
			int[] array = (n > 32) ? bits[0] : bits[1];
			BitVector b = new BitVector(n, array);
			BitVector.BitIterator iter = b.bitIterator(); 
			int counter = 0;
			int leftOver = n;
			while (iter.hasNext()) {
				int x = iter.nextBitBlock();
				assertEquals("n="+n, counter % 2, x);
				leftOver--;
				counter++;
			}
			assertEquals("verify number of calls", n, counter);
		}
	}
	
	
	
	@Test
	public void testAND() {
		for (int n = 1; n <= 32; n++) {
			// Consider a truth table with the columns: x, y, x AND Y.
			// Each nibble of bits0 is the column for x in this truth table.
			// Each nibble of bits1 is the column for y in this truth table.
			// Each nibble of expected is the column for: x AND Y.
			int[] bits0 = { 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0x88888888 & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.and(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits ANDed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
		}
		for (int n = 33; n <= 64; n++) {
			// Consider a truth table with the columns: x, y, x AND Y.
			// Each nibble of bits0 is the column for x in this truth table.
			// Each nibble of bits1 is the column for y in this truth table.
			// Each nibble of expected is the column for: x AND Y.
			int[] bits0 = { 0xcccccccc, 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0x88888888, 0x88888888 & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.and(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits ANDed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
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
			int[] bits0 = { 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0xeeeeeeee & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.or(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits ORed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
		}
		for (int n = 33; n <= 64; n++) {
			// Consider a truth table with the columns: x, y, x OR Y.
			// Each nibble of bits0 is the column for x in this truth table.
			// Each nibble of bits1 is the column for y in this truth table.
			// Each nibble of expected is the column for: x OR Y.
			int[] bits0 = { 0xcccccccc, 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0xeeeeeeee, 0xeeeeeeee & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.or(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits ORed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
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
			int[] bits0 = { 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0x66666666 & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.xor(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits XORed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
		}
		for (int n = 33; n <= 64; n++) {
			// Consider a truth table with the columns: x, y, x XOR Y.
			// Each nibble of bits0 is the column for x in this truth table.
			// Each nibble of bits1 is the column for y in this truth table.
			// Each nibble of expected is the column for: x XOR Y.
			int[] bits0 = { 0xcccccccc, 0xcccccccc & (0xffffffff >>> (32-n)) };
			int[] bits1 = { 0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (32-n)) };
			int[] expected = { 0x66666666, 0x66666666 & (0xffffffff >>> (32-n))};
			BitVector b0 = new BitVector(n, bits0);
			BitVector b1 = new BitVector(n, bits1);
			BitVector b1copy = new BitVector(n, bits1);
			BitVector bExpected = new BitVector(n, expected);
			b0.xor(b1);
			assertEquals("explicit param shouldn't change", b1copy, b1);
			assertEquals("verify bits XORed correctly", bExpected, b0);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b0.getBit(i));
			}
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
			for (int i = 0; i < n; i++) {
				assertEquals(1, b0.getBit(i));
			}
			if (n >= 32) {
				assertEquals(0xffffffff, b0.get32(0));
				if (n > 32) assertEquals(0xffffffff >>> 64-n, b0.get32(1));
			} else if (n > 0) {
				assertEquals(0xffffffff >>> 32-n, b0.get32(0));
			}
			b0.not();
			for (int i = 0; i < n; i++) {
				assertEquals(0, b0.getBit(i));
			}
			if (n > 0) assertEquals(0, b0.get32(0));
			if (n > 32) assertEquals(0, b0.get32(1));
		}
		for (int n = 1; n <= 32; n++) {
			int[] bits = { 0x55555555 & (0xffffffff >>> (32-n)) };
			int[] expected = { 0xaaaaaaaa & (0xffffffff >>> (32-n))};
			BitVector b = new BitVector(n, bits);
			BitVector bExpected = new BitVector(n, expected);
			b.not();
			assertEquals(bExpected, b);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b.getBit(i));
			}
		}
		for (int n = 33; n <= 64; n++) {
			int[] bits = { 0x55555555, 0x55555555 & (0xffffffff >>> (64-n)) };
			int[] expected = { 0xaaaaaaaa, 0xaaaaaaaa & (0xffffffff >>> (64-n))};
			BitVector b = new BitVector(n, bits);
			BitVector bExpected = new BitVector(n, expected);
			b.not();
			assertEquals(bExpected, b);
			for (int i = 0; i < n; i++) {
				assertEquals(bExpected.getBit(i), b.getBit(i));
			}
		}
	}
	
	@Test
	public void testShiftLeft() {
		for (int n = 1; n <= 96; n++) {
			BitVector original = new BitVector(n);
			original.setBit(0, 1);
			original.setBit(n-1, 1);
			original.setBit(n/2, 1);
			for (int shift = 0; shift <= n; shift++) {
				BitVector shifted = original.copy();
				shifted.shiftLeft(shift);
				for (int i = 0; i < shift; i++) {
					assertEquals(0, shifted.getBit(i));
				}
				for (int i = shift; i < n; i++) {
					assertEquals(original.getBit(i-shift), shifted.getBit(i));
				}
			}
			BitVector shifted = original.copy();
			shifted.shiftLeft(n+1);
			for (int i = 0; i < n; i++) {
				assertEquals(0, shifted.getBit(i));
			}
		}
	}
	
	@Test
	public void testShiftRight() {
		for (int n = 1; n <= 96; n++) {
			BitVector original = new BitVector(n);
			original.setBit(0, 1);
			original.setBit(n-1, 1);
			original.setBit(n/2, 1);
			for (int shift = 0; shift <= n; shift++) {
				BitVector shifted = original.copy();
				shifted.shiftRight(shift);
				for (int i = 0; i + shift < n; i++) {
					assertEquals(original.getBit(i+shift), shifted.getBit(i));
				}
				for (int i = n - shift; i < n; i++) {
					assertEquals(0, shifted.getBit(i));
				}
			}
			BitVector shifted = original.copy();
			shifted.shiftRight(n+1);
			for (int i = 0; i < n; i++) {
				assertEquals(0, shifted.getBit(i));
			}
		}
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
		}
		for (int n = 0; n <= 96; n++) {
			// tests with all 1s
			BitVector b1 = new BitVector(n);
			b1.not();
			char[] c = new char[n];
			Arrays.fill(c, '1');
			String expected = new String(c);
			assertEquals(expected, b1.toString());
		}
		for (int n = 1; n <= 64; n++) {
			// test with exactly one 1.
			for (int i = 0; i < n; i++) {
				BitVector b = new BitVector(n);
				b.flip(i);
				char[] c = new char[n];
				Arrays.fill(c, '0');
				c[n-1-i] = '1';
				String expected = new String(c);
				assertEquals(expected, b.toString());
			}
		}
		for (int n = 1; n <= 64; n++) {
			// test with exactly one 0.
			for (int i = 0; i < n; i++) {
				BitVector b = new BitVector(n);
				b.flip(i);
				b.not();
				char[] c = new char[n];
				Arrays.fill(c, '1');
				c[n-1-i] = '0';
				String expected = new String(c);
				assertEquals(expected, b.toString());
			}
		}
	}
	
}
