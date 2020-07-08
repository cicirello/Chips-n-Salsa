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

package org.cicirello.search.ss;

import org.cicirello.permutations.Permutation;
 
/**
 * <p>A PartialPermutation represents a permutation that is being 
 * iteratively constructed as a solution to an optimization problem
 * over the space of permutations.
 * This class supports the implementation of constructive heuristics 
 * for permutation optimization problems, as well as for stochastic
 * sampling algorithms that rely on constructive heuristics.</p>
 *
 * <p>In the context of this library, a permutation of length n is
 * a permutation of the integers { 0, 1, ..., (n-1)}.</p> 
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.6.2020
 */
public final class PartialPermutation {
	
	private final int[] partial;
	private final int[] remainingElements;
	private int size;
	private int remaining;
	
	/**
	 * Constructs a PartialPermutation that will iteratively be
	 * transformed into a Permutation.
	 * @param n The desired length of the final Permutation, which must be non-negative.
	 * @throws IllegalArgumentException if n is less than 0
	 */
	public PartialPermutation(int n) {
		if (n < 0) throw new IllegalArgumentException("n must not be negative");
		partial = new int[n];
		remainingElements = new int[n];
		for (int i = 1; i < n; i++) {
			remainingElements[i] = i;
		}
		remaining = n;
		// deliberately using default: size=0;
	}
	
	/**
	 * Generates a Permutation object that is consistent with this
	 * PartialPermutation.  That is, elements already added to the
	 * PartialPermutation will retain their positions, while elements
	 * not already in the PartialPermutation will be added in an undefined
	 * order at the end such that the result is a valid Permutation.
	 * @return a valid Permutation consistent with the current state
	 * of this PartialPermutation
	 */
	public Permutation toComplete() {
		if (remaining > 0) {
			System.arraycopy(remainingElements, 0, partial, size, remaining);
		}
		return new Permutation(partial);
	}
	
	/**
	 * Checks if the PartialPermutation is actually a complete permutation
	 * (i.e., all elements added to it).
	 * @return true if the PartialPermutation is a complete permutation.
	 */
	public boolean isComplete() {
		return remaining==0;
	}
	
	/**
	 * Gets the element in the PartialPermutation at position index.
	 * @param index The position, which must be less than size().
	 * The valid index values are [0, 1, ..., (size()-1)].
	 * @return the element in position index.
	 * @throws ArrayIndexOutOfBoundsException 
	 * if index is greater than or equal to size(), or if index is less than 0
	 */
	public int get(int index) {
		if (index >= size) {
			throw new ArrayIndexOutOfBoundsException("index must be less than size");
		}
		return partial[index];
	}
	
	/**
	 * Gets the element in the current last position of the PartialPermutation,
	 * i.e., the element at index: size()-1.
	 * @return The element in the current last position of the PartialPermutation.
	 * @throws ArrayIndexOutOfBoundsException if size() is 0
	 */ 
	public int getLast() {
		return partial[size-1];
	}
	
	/**
	 * Gets the size of the PartialPermutation, which is the number of elements
	 * that have already been added to it.
	 * @return size The size of the PartialPermutation
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Gets the number of elements not yet added to the PartialPermutation.
	 * We refer to these as extensions since adding an element will extend the
	 * size of the PartialPermutation.
	 * @return the number of elements not yet added to the PartialPermutation
	 */
	public int numExtensions() {
		return remaining;
	}
	
	/**
	 * Gets the element in position index of the list of extensions.   
	 * This method gets the element at position index from the list of those elements
	 * that have not yet been added to the PartialPermutation. Note that each time
	 * {@link #extend} is called that the remaining elements may be reordered.
	 * @param extensionIndex An index into the list of elements not yet added to the 
	 * PartialPermutation.  The valid extensionIndex values are [0, 1, ..., (numExtensions()-1)].
	 * @return the element at the designated index in the list of elements 
	 * not yet added to the PartialPermutation. 
	 * @throws ArrayIndexOutOfBoundsException 
	 * if extensionIndex is greater than or equal to numExtensions(), 
	 * or if extensionIndex is less than 0
	 */
	public int getExtension(int extensionIndex) {
		if (extensionIndex >= remaining) {
			throw new ArrayIndexOutOfBoundsException("extensionIndex must be less than numExtensions()");
		}
		return remainingElements[extensionIndex];
	}
	
	/**
	 * <p>Extends the PartialPermutation by adding an element to the
	 * end of the PartialPermutation.  If size() is the size of the
	 * PartialPermutation before the extension, then the new element
	 * is added to position size() and the size() is increased by 1.</p>
	 *
	 * @param extensionIndex An index into the list of elements not yet added to the 
	 * PartialPermutation.  The valid extensionIndex values are [0, 1, ..., (numExtensions()-1)].
	 * @throws ArrayIndexOutOfBoundsException 
	 * if extensionIndex is greater than or equal to numExtensions(), 
	 * or if extensionIndex is less than 0
	 */
	public void extend(int extensionIndex) {
		if (extensionIndex >= remaining) {
			throw new ArrayIndexOutOfBoundsException("extensionIndex must be less than numExtensions()");
		}
		partial[size] = remainingElements[extensionIndex];
		size++;
		remaining--;
		remainingElements[extensionIndex] = remainingElements[remaining];
	}
	
}