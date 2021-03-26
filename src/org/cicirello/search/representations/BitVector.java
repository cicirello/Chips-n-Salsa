/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.util.Copyable;
import java.util.Arrays;

/**
 * A BitVector is an indexable vector of bits.  It supports operations for manipulating the bits in
 * a variety of ways, such as setting and getting, flipping, left and right shifts, computing 
 * bitwise operators (and, or, xor) between pairs of equal length BitVectors, etc.  It also supports
 * iterating over the bits, either one bit at a time, or groups of bits.  Indexes into a BitVector
 * begin at 0, and index 0 refers to the least significant bit (&quot;right-most&quot; bit).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.26.2021
 */
public final class BitVector implements Copyable<BitVector> {
	
	private final int[] bits;
	private final int bitLength;
	private final int lastIntMask;
	
	/**
	 * Initializes the bit vector to a vector of all 0 bits.
	 *
	 * @param bitLength The length of the bit vector in number of bits.
	 *
	 * @throws IllegalArgumentException if bitLength &lt; 0.
	 */
	public BitVector(int bitLength) {
		this(bitLength, false);
	}
	
	/**
	 * Initializes the bit vector.
	 *
	 * @param bitLength The length of the bit vector in number of bits.
	 * @param randomize if true, then the vector is initialized with random bit values;
	 * and otherwise initializes it to a vector of all 0 bits.
	 *
	 * @throws IllegalArgumentException if bitLength &lt; 0.
	 */
	public BitVector(int bitLength, boolean randomize) {
		if (bitLength < 0) throw new IllegalArgumentException("bitLength must be non-negative");
		bits = new int[(bitLength + 31) >> 5];
		this.bitLength = bitLength;
		lastIntMask = 0xffffffff >>> ((bits.length << 5) - bitLength);
		if (randomize && bits.length > 0) {
			for (int i = 0; i < bits.length; i++) {
				bits[i] = ThreadLocalRandom.current().nextInt();
			}
			bits[bits.length-1] &= lastIntMask;
		}
	}
	
	/**
	 * Initializes a bit vector from an array of ints.
	 *
	 * @param bitLength The length of the bit vector in number of bits.
	 * @param bits An array of ints containing the bits for initializing the BitVector.
	 *
	 * @throws IllegalArgumentException if bitLength &lt; 0 or if bits.length is longer than
	 * necessary for a vector of bitLength bits or if bits.length is too short for a vector of 
	 * bitLength bits.
	 */
	public BitVector(int bitLength, int[] bits) {
		if (bitLength < 0) throw new IllegalArgumentException("bitLength must be non-negative");
		if (((bitLength + 31) >> 5) != bits.length) throw new IllegalArgumentException("bits.length is inconsistent with bitLength");
		this.bits = bits.clone();
		this.bitLength = bitLength;
		lastIntMask = 0xffffffff >>> ((bits.length << 5) - bitLength);
		this.bits[this.bits.length-1] &= lastIntMask;
	}
	
	/*
	 * Internal copy constructor.
	 */
	private BitVector(BitVector other) {
		bits = other.bits.clone();
		bitLength = other.bitLength;
		lastIntMask = other.lastIntMask;
	}
	
	/**
	 * Gets the length of the bit vector in number of bits.
	 * @return The length of the bit vector in number of bits.
	 */
	public int length() {
		return bitLength;
	}

	/**
	 * Gets the value of the bit at a designated index.
	 *
	 * @param index The index into the bit vector, which must be 0 &le; index &lt; length().
	 * @return The value of the bit at position index, as an int. The least significant bit 
	 * of the returned int will hold the value of the bit.  The other 31 bits will be 0.
	 *
	 * @throws IndexOutOfBoundsException if index is negative, or if index &ge; length()
	 */
	public int getBit(int index) {
		if (index < 0 || index >= bitLength) {
			throw new IndexOutOfBoundsException("index is not in the bounds of the BitVector");
		}
		int i = index >> 5;
		return (bits[i] >>> (index - (i << 5))) & 1;
	}
	
	/**
	 * Sets the value of the bit at a designated index.
	 *
	 * @param index The index into the bit vector, which must be 0 &le; index &lt; length().
	 * @param bitValue The value to set for the bit at position index.  The least significant bit
	 * of this int is used.
	 *
	 * @throws IndexOutOfBoundsException if index is negative, or if index &ge; length()
	 */
	public void setBit(int index, int bitValue) {
		if (index < 0 || index >= bitLength) {
			throw new IndexOutOfBoundsException("index is not in the bounds of the BitVector");
		}
		int i = index >> 5;
		int value = bitValue & 1;
		if (value == 0) {
			bits[i] &= ~(1 << (index - (i << 5)));
		} else {
			bits[i] |= (1 << (index - (i << 5)));
		}
	}
	
	/**
	 * Flips the value of the bit at the specified index (e.g., if it is a 0, then
	 * change to a 1; if it is a 1, change to a 0).
	 *
	 * @param index The index into the bit vector, which must be 0 &le; index &lt; length().
	 *
	 * @throws IndexOutOfBoundsException if index is negative, or if index &ge; length()
	 */
	public void flip(int index) {
		if (index < 0 || index >= bitLength) {
			throw new IndexOutOfBoundsException("index is not in the bounds of the BitVector");
		}
		int i = index >> 5;
		bits[i] ^= (1 << (index - (i << 5)));
	}
	
	/**
	 * Checks if the bit at a designated index is equal to a 1.
	 *
	 * @param index The index into the bit vector, which must be 0 &le; index &lt; length().
	 * @return true if and only if the value of the bit at position index is equal to 1
	 *
	 * @throws IndexOutOfBoundsException if index is negative, or if index &ge; length()
	 */
	public boolean isOne(int index) {
		return getBit(index) == 1;
	}
	
	/**
	 * Checks if the bit at a designated index is equal to a 0.
	 *
	 * @param index The index into the bit vector, which must be 0 &le; index &lt; length().
	 * @return true if and only if the value of the bit at position index is equal to 0
	 *
	 * @throws IndexOutOfBoundsException if index is negative, or if index &ge; length()
	 */
	public boolean isZero(int index) {
		return getBit(index) == 0;
	}
	
	/**
	 * Gets a block of up to 32 bits.
	 *
	 * @param i The block of bits accessed by this method begins at the bit with index 32*i.
	 *
	 * @return Up to 32 bits beginning at the bit at index 32*i.  For example, if i is 0, then
	 * this method will return the 32 bits beginning at index 32*0=0, i.e., bits 0 through 31.
	 * Bit 0 will be in the least significant position, and bit 31 will be in the most significant
	 * position.
	 * If i is 1, then this method returns the 32 bits beginning with the bit at index 32 (i.e.,
	 * bits 32 through 63).  If there are less than 32 bits beginning at index 32*i, then the
	 * relevant bits will be in the least significant positions of the returned int.
	 *
	 * @throws IndexOutOfBoundsException if i is negative, or if 32*i &ge; length()
	 */
	public int get32(int i) {
		if (i < 0 || i >= bits.length) {
			throw new IndexOutOfBoundsException("i is not in the bounds of the BitVector");
		}
		return bits[i];
	}
	
	/**
	 * Sets a block of up to 32 bits.
	 *
	 * @param i The block of bits set by this method begins at the bit with index 32*i.
	 * For example, if i is 0, then
	 * this method will set the 32 bits beginning at index 32*0=0, i.e., bits 0 through 31.
	 * Bit 0 will be in the least significant position, and bit 31 will be in the most significant
	 * position.
	 * If i is 1, then this method sets the 32 bits beginning with the bit at index 32 (i.e.,
	 * bits 32 through 63).  
	 * @param block The block of bits to set.  The least significant bit of block will be stored
	 * at index 32*i.
	 *
	 * @throws IndexOutOfBoundsException if i is negative, or if 32*i &ge; length()
	 */
	public void set32(int i, int block) {
		if (i < 0 || i >= bits.length) {
			throw new IndexOutOfBoundsException("i is not in the bounds of the BitVector");
		}
		if (i == bits.length-1) block &= lastIntMask;
		bits[i] = block;
	}
	
	/**
	 * Counts the number of bits in the bit vector whose value is a 1.
	 * @return the count of the number of bits equal to 1.
	 */
	public int countOnes() {
		int count = 0;
		for (int i = 0; i < bits.length; i++) {
			int b = bits[i];
			//counting 1-bits of one int from Hacker's Delight
			b -= ((b >>> 1) & 0x55555555);
			b = (b & 0x33333333) + ((b >>> 2) & 0x33333333);
			b = (b + (b >>> 4)) & 0x0f0f0f0f;
			b += (b >>> 8);
			b += (b >>> 16);
			count += (b & 0x3f);
		}
        return count;
	}
	
	/**
	 * Counts the number of bits in the bit vector whose value is a 0.
	 * @return the count of the number of bits equal to 0.
	 */
	public int countZeros() {
		return bitLength - countOnes();
	}
	
	/**
	 * Computes the bitwise AND of this BitVector and another BitVector.  This BitVector
	 * is updated with the result.
	 *
	 * @param other The other BitVector.
	 *
	 * @throws IllegalArgumentException if the BitVectors are of different lengths.
	 */
	public void and(BitVector other) {
		if (bitLength != other.bitLength) throw new IllegalArgumentException("Both BitVectors must be of same length.");
		for (int i = 0; i < bits.length; i++) {
			bits[i] &= other.bits[i];
		}
	}
	
	/**
	 * Computes the bitwise OR of this BitVector and another BitVector.  This BitVector
	 * is updated with the result.
	 *
	 * @param other The other BitVector.
	 *
	 * @throws IllegalArgumentException if the BitVectors are of different lengths.
	 */
	public void or(BitVector other) {
		if (bitLength != other.bitLength) throw new IllegalArgumentException("Both BitVectors must be of same length.");
		for (int i = 0; i < bits.length; i++) {
			bits[i] |= other.bits[i];
		}
	}
	
	/**
	 * Computes the bitwise XOR of this BitVector and another BitVector.  This BitVector
	 * is updated with the result.
	 *
	 * @param other The other BitVector.
	 *
	 * @throws IllegalArgumentException if the BitVectors are of different lengths.
	 */
	public void xor(BitVector other) {
		if (bitLength != other.bitLength) throw new IllegalArgumentException("Both BitVectors must be of same length.");
		for (int i = 0; i < bits.length; i++) {
			bits[i] ^= other.bits[i];
		}
	}
	
	/**
	 * Computes the bitwise complement of this BitVector.  This BitVector
	 * is updated with the result.
	 */
	public void not() {
		if (bits.length > 0) {
			for (int i = 0; i < bits.length; i++) {
				bits[i] = ~bits[i];
			}
			bits[bits.length-1] &= lastIntMask;
		}
	}
	
	/**
	 * Performs a left shift of the bits of the BitVector, with 0s filling in on
	 * the right.  Specifically, the value of the bit in index i before a call to
	 * shiftLeft, will move to index (i + numBits).  The method does not change the
	 * length of the BitVector, so the numBits bits on the left end of the BitVector
	 * before the call are lost.
	 * @param numBits The number of bits to shift to the left.
	 */
	public void shiftLeft(int numBits) {
		if (bitLength > 0) {
			if (numBits < bitLength) {
				if (numBits >= 32) {
					int numInts = numBits >> 5;
					System.arraycopy(bits, 0, bits, numInts, bits.length - numInts);
					Arrays.fill(bits, 0, numInts, 0);
					bits[bits.length-1] &= lastIntMask;
					numBits -= numInts << 5;
				}
				if (numBits > 0) {
					leftUpTo31(numBits);
				}
			} else {
				Arrays.fill(bits, 0);
			}
		}
	}
	
	/**
	 * Performs a right shift of the bits of the BitVector, with 0s filling in on
	 * the left.  Specifically, the value of the bit in index i before a call to
	 * shiftLeft, will move to index (i - numBits).  The 
	 * numBits bits on the right end of the BitVector
	 * before the call are lost.
	 * @param numBits The number of bits to shift to the right.
	 */
	public void shiftRight(int numBits) {
		if (bitLength > 0) {
			if (numBits < bitLength) {
				if (numBits >= 32) {
					int numInts = numBits >> 5;
					System.arraycopy(bits, numInts, bits, 0, bits.length - numInts);
					Arrays.fill(bits, bits.length - numInts, bits.length, 0);
					numBits -= numInts << 5;
				}
				if (numBits > 0) {
					rightUpTo31(numBits);
				}
			} else {
				Arrays.fill(bits, 0);
			}
		}
	}
	
	private void leftUpTo31(int numBits) {
		for (int i = bits.length - 1; i > 0; i--) {
			bits[i] = (bits[i] << numBits) | (bits[i-1] >>> (32-numBits));
		}
		bits[0] <<= numBits;
		bits[bits.length-1] &= lastIntMask;
	}
	
	private void rightUpTo31(int numBits) {
		for (int i = 0; i < bits.length - 1; i++) {
			bits[i] = (bits[i] >>> numBits) | (bits[i+1] << (32-numBits));
		}
		bits[bits.length - 1] >>>= numBits;
	}
	
	
	/**
	 * Creates an identical copy of this object.
	 * @return an identical copy of this object
	 */
	@Override
	public BitVector copy() {
		return new BitVector(this);
	}
	
	/**
	 * Indicates whether some other object is equal to this one.
	 * The objects are equal if they are the same type of operator
	 * with the same parameters.
	 * @param other the object with which to compare
	 * @return true if and only if the objects are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !getClass().equals(other.getClass())) return false;
		BitVector b = (BitVector)other;
		return bitLength == b.bitLength && Arrays.equals(bits, b.bits); 
	}
	
	/**
	 * Returns a hash code value for the object.
	 * This method is supported for the benefit of hash 
	 * tables such as those provided by HashMap.
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		int h = bitLength;
		for (int i = 0; i < bits.length; i++) {
			h = 31*h + bits[i];
		}
		return h;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bitLength);
		int blockSize = (bitLength & 0x1f);
		if (blockSize==0) blockSize = 32;
		String filler = "0000000000000000000000000000000";
		for (int i = bits.length-1; i >= 0; i--) {
			String str = Integer.toBinaryString(bits[i]);
			int numZeros = blockSize - str.length();
			if (numZeros > 0) {
				sb.append(filler.substring(0,numZeros)); 
			}
			sb.append(str);
			blockSize=32;
		}
		return sb.toString();
	}
	
	/**
	 * Creates a BitIterator to iterate over the bits of
	 * this BitVector.
	 *
	 * @param k The number of bits for the BitIterator to return at a time.
	 * For example, if k is 3, then each call to {@link BitIterator#nextBitBlock()} will
	 * return an int containing the next 3 bits from the BitVector in the
	 * least significant 3 places.  The value of k must be 0 &lt; k &le; 32.
	 *
	 * @return A BitIterator object to iterate over the bits of this BitVector.
	 *
	 * @see BitIterator
	 *
	 * @throws IllegalArgumentException if k &le; 0 or if k &gt; 32
	 */
	public BitIterator bitIterator(int k) {
		if (k <= 0 || k > 32) throw new IllegalArgumentException("k is outside the range [1,32]");
		return new BitIterator(k);
	}
	
	/**
	 * Creates a BitIterator to iterate over the bits of
	 * this BitVector, one bit at a time.
	 *
	 * @return A BitIterator object to iterate over the bits of this BitVector.
	 *
	 * @see BitIterator
	 *
	 */
	public BitIterator bitIterator() {
		return new BitIterator();
	}
	
	/**
	 * <p>The BitIterator class enables iterating over the bits of a BitVector.
	 * In particular, it enables iterating over fixed length blocks within 
	 * the BitVector.  BitIterators are created via the {@link BitVector#bitIterator(int)}
	 * method or the {@link BitVector#bitIterator()} method.</p>
	 *
	 * <p>An example usage of the BitIterator class is as follows.  In this example,
	 * we are iterating over the bits of a random BitVector of 128 bits, 4 bits at a time,
	 * using the {@link #nextBitBlock()} method.</p>
	 * <pre><code>
	 * BitVector b = new BitVector(128, true);
	 * BitVector.BitIterator iter = b.bitIterator(4);
	 * while (iter.hasNext()) {
	 *      int fourBits = iter.nextBitBlock();
	 *      ....
	 * }
	 * </code></pre>
	 *
	 * <p>The BitIterator class also includes a {@link #nextBit} method that gets the
	 * next single bit (returned as an int) regardless of the block length used when
	 * the BitIterator was initiated.  This example shows how to iterate over
	 * individual bits.</p>
	 * <pre><code>
	 * int k = ...; // doesn't matter what is passed as block length
	 * BitVector b = new BitVector(128, true);
	 * BitVector.BitIterator iter = b.bitIterator(k);
	 * while (iter.hasNext()) {
	 *      int singleBit = iter.nextBit();
	 *      ....
	 * }
	 * </code></pre>
	 *
	 * <p>You can even use a combination of calls to the {@link #nextBitBlock()} method and
	 * the {@link #nextBit} method.  In this example, we alternate getting blocks of 4 bits
	 * with getting a single bit.</p>
	 * <pre><code>
	 * BitVector b = new BitVector(128, true);
	 * BitVector.BitIterator iter = b.bitIterator(4);
	 * while (iter.hasNext()) {
	 *      int fourBits = iter.nextBitBlock();
	 *      ....
	 *      if (iter.hasNext()) {
	 *           int singleBit = iter.nextBit();
	 *           ....
	 *      }
	 * }
	 * </code></pre>
	 *
	 * <p>There is also a {@link #nextBitBlock(int)} method, which enables overriding the
	 * default block size for some calls.  The class supports usages involving combinations of
	 * calls to {@link #nextBitBlock()}, {@link #nextBitBlock(int)}, and {@link #nextBit()}.
	 * This next example initiates a BitIterator with a default block size of 4, and then gets
	 * the default of 4 bits, followed by a block of 7 bits, followed by a single bit, and then 
	 * the default of 4 bits again.</p>
	 * <pre><code>
	 * BitVector b = new BitVector(128, true);
	 * BitVector.BitIterator iter = b.bitIterator(4);
	 * if (iter.hasNext()) {
	 *      // the default block size was set to 4 above
	 *      int fourBits = iter.nextBitBlock();
	 *      ....
	 * }
	 * if (iter.hasNext()) {
	 *      // override the default block size to get 7 bits
	 *      int sevenBits = iter.nextBitBlock(7);
	 *      ....
	 * }
	 * if (iter.hasNext()) {
	 *      // get a single bit
	 *      int singleBit = iter.nextBit();
	 *      ....
	 * }
	 * if (iter.hasNext()) {
	 *      // get the default block size again
	 *      int fourBits = iter.nextBitBlock();
	 *      ....
	 * }
	 * </code></pre>
	 *
	 */
	public final class BitIterator {
		
		private final int k;
		private final int mask;
		private int count;
		private int index;
		private int remaining;
		
		/*
		 * private constructor for use by surrounding class only
		 */
		private BitIterator(int k) {
			this.k = k;
			mask = 0xffffffff >>> (32-k);
			remaining = 32;
			/* default values for these is intentional
			count = 0;
			index = 0;
			*/
		}
		
		/*
		 * private constructor for use by surrounding class only
		 */
		private BitIterator() {
			this.k = 1;
			mask = 1;
			remaining = 32;
			/* default values for these is intentional
			count = 0;
			index = 0;
			*/
		}
		
		/**
		 * Verifies if there are more bits to get, and it is safe to call {@link nextBitBlock}.
		 * @return true if there are more bits, and false otherwise.  If this method returns false,
		 * then calls to {@link nextBitBlock} will throw an exception.
		 */
		public boolean hasNext() {
			return count < bitLength;
		}
		
		/**
		 * Gets the number of bits remaining in the iterator.
		 * @return the number of bits not yet iterated over.
		 */
		public int numRemainingBits() {
			return count >= bitLength ? 0 : bitLength - count;
		}
		
		/**
		 * Gets the next block of bits from the BitVector. 
		 * @return the next block of bits, which will be in the least significant places of the
		 * returned int value.
		 * @throws IllegalStateException if there are no more blocks to get
		 */
		public int nextBitBlock() {
			if (count >= bitLength) throw new IllegalStateException();
			if (remaining == 0) {
				index++;
				remaining = 32; 
			}
			int block;
			if (remaining >= k) {
				block = (bits[index] >>> (32 - remaining)) & mask;
				remaining -= k;
				count += k;
			} else {
				block = (bits[index] >>> (32 - remaining));
				index++;
				if (index < bits.length) {
					block |= (bits[index] << remaining) & mask;
					remaining += 32 - k; 
					count += k;
				} else {
					count += k;
					remaining = 0;
				}
			}
			return block;
		}
		
		/**
		 * Gets the next block of bits from the BitVector. 
		 * @param k The block size, which overrides the default block size
		 * that was used when the BitIterator was initiated for this call
		 * only.  
		 * For example, if k is 3, then this call will
		 * return an int containing the next 3 bits from the BitVector in the
		 * least significant 3 places.  The value of k must be 0 &lt; k &le; 32.
		 *
		 * @return the next block of bits, which will be in the least significant places of the
		 * returned int value.
		 * @throws IllegalArgumentException if k &le; 0 or if k &gt; 32
		 * @throws IllegalStateException if there are no more blocks to get
		 */
		public int nextBitBlock(int k) {
			if (k <= 0 || k > 32) throw new IllegalArgumentException("k is outside the range [1,32]");
			if (count >= bitLength) throw new IllegalStateException();
			return internalNextBitBlock(k);
		}
		
		/**
		 * Gets the next block of bits from the BitVector. 
		 * @param k The block size, which overrides the default block size
		 * that was used when the BitIterator was initiated for this call
		 * only. Unlike the {@link #nextBitBlock} methods, this method
		 * is not limited in block size, other than by the number of
		 * remaining bits.    
		 *
		 * @return the next block of bits, as an array of ints, with the
		 * first 32 bits of the block filled from least significant to most
		 * significant bit of a[0], where a is the array that is returned,
		 * and the next 32 bits filled similarly into a[1], etc.
		 * @throws IllegalArgumentException if there are fewer than k bits remaining.
		 */
		public int[] nextLargeBitBlock(int k) {
			if (count + k > bitLength) {
				throw new IllegalArgumentException("requested more bits than remain");
			}
			int numBlocksOf32 = k >> 5;
			int numExtraBits = k & 0x1F;
			int n = numBlocksOf32;
			if (numExtraBits > 0) n++;
			int[] block = new int[n];
			for (int i = 0; i < numBlocksOf32; i++) {
				block[i] = internalNextBitBlock(32);
			}
			if (numExtraBits > 0) {
				block[numBlocksOf32] = internalNextBitBlock(numExtraBits);
			}
			return block;
		}
		
		/**
		 * Skips this BitIterator past a segment of bits.
		 * @param k The number of bits to skip.
		 * @throws IllegalArgumentException if there are fewer than k bits remaining.
		 */
		public void skip(int k) {
			if (count + k > bitLength) {
				throw new IllegalArgumentException("requested more bits than remain");
			}
			int numBlocksOf32 = k >> 5;
			if (numBlocksOf32 > 0) {
				index += numBlocksOf32;
				count += (numBlocksOf32 << 5);
			}
			k = k & 0x1F;
			if (k > 0) {
				if (remaining >= k) {
					remaining -= k;
					count += k;
				} else {
					remaining = 32 - (k - remaining);
					index++;
					count += k;
				}
			}
		}
		
		/**
		 * Gets the next bit from the BitVector.  Unlike the {@link #nextBitBlock()} method,
		 * this method just gets 1 bit at a time, rather than a block of bits.
		 * @return the next bit, which will be in the least significant place of the
		 * returned int value.
		 * @throws IllegalStateException if there are no more bits to get
		 */
		public int nextBit() {
			if (count >= bitLength) throw new IllegalStateException();
			if (remaining == 0) {
				index++;
				remaining = 32; 
			}
			int bit = (bits[index] >>> (32 - remaining)) & 1;
			remaining--;
			count++; 
			return bit;
		}
		
		private int internalNextBitBlock(int k) {
			if (remaining == 0) {
				index++;
				remaining = 32; 
			}
			int block;
			int mask = 0xffffffff >>> (32-k);
			if (remaining >= k) {
				block = (bits[index] >>> (32 - remaining)) & mask;
				remaining -= k;
				count += k;
			} else {
				block = (bits[index] >>> (32 - remaining));
				index++;
				if (index < bits.length) {
					block |= (bits[index] << remaining) & mask;
					remaining += 32 - k; 
					count += k;
				} else {
					count += k;
					remaining = 0;
				}
			}
			return block;
		}
	}
}