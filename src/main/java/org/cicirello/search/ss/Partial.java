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

package org.cicirello.search.ss;

import org.cicirello.util.Copyable;

/**
 * <p>A Partial represents a partial solution to a problem (e.g.,
 * a partial permutation or a partial integer vector) that is being 
 * iteratively constructed as a solution to an optimization problem.
 * This class supports the implementation of constructive heuristics 
 * for optimization problems, as well as for stochastic
 * sampling algorithms that rely on constructive heuristics.</p>
 *
 * @param <T> The type of object that this Partial can become, which is 
 * assumed to be an object that is a sequence of integers (e.g., vector of integers,
 * permutation, or some other indexable type that stores integers).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
 public interface Partial<T extends Copyable<T>> {
	 
	/**
	 * Generates a complete instance that is consistent with this
	 * Partial.  That is, elements already added to the
	 * Partial will retain their positions, while elements
	 * not already in the Partial will be added in an undefined
	 * order such that the result is a valid complete object of type T.
	 * @return a valid object of type T consistent with the current state
	 * of this Partial
	 */
	T toComplete();
	
	/**
	 * Checks if the Partial is actually a complete T.
	 * @return true if the Partial is a complete T.
	 */
	boolean isComplete();
	
	/**
	 * Gets the element in the Partial at position index.
	 * @param index The position, which must be less than size().
	 * The valid index values are [0, 1, ..., (size()-1)].
	 * @return the element in position index.
	 * @throws ArrayIndexOutOfBoundsException 
	 * if index is greater than or equal to size(), or if index is less than 0
	 */
	int get(int index);
	
	/**
	 * Gets the element in the current last position of the Partial,
	 * i.e., the element at index: size()-1.
	 * @return The element in the current last position of the Partial.
	 * @throws ArrayIndexOutOfBoundsException if size() is 0
	 */ 
	int getLast();
	
	/**
	 * Gets the size of the Partial, which is the number of elements
	 * that have already been added to it.  Note that this is NOT the
	 * size of the final complete T.  Rather, it is the current size
	 * of the Partial.
	 * @return size The size of the Partial.
	 */
	int size();
	
	/**
	 * Gets the number of elements not yet added to the Partial.
	 * We refer to these as extensions since adding an element will extend the
	 * size of the Partial.
	 * @return the number of elements not yet added to the Partial
	 */
	int numExtensions();
	
	/**
	 * Gets the element in position index of the list of possible extensions.   
	 * This method gets the element at position index from the list of those elements
	 * that can be added next to the Partial. For example, if this is a partial
	 * permutation, then this would get one of the elements not yet added to the permutation.
	 * Or for example, if this is a partial vector of integers, then this would get one
	 * of the values that is allowed to be added to the next position of the vector.
	 * Note that each time
	 * {@link #extend} is called that the remaining elements may be reordered, so you cannot
	 * assume that the extensions remain in the same positions once you call extend.
	 * @param extensionIndex An index into the list of elements that can be added to the Partial next. 
	 * The valid extensionIndex values are [0, 1, ..., (numExtensions()-1)].
	 * @return the element at the designated index in the list of elements 
	 * that can be added to the Partial. 
	 * @throws ArrayIndexOutOfBoundsException 
	 * if extensionIndex is greater than or equal to numExtensions(), 
	 * or if extensionIndex is less than 0
	 */
	int getExtension(int extensionIndex);
	
	/**
	 * <p>Extends the Partial by adding an element to the
	 * end of the Partial.  If size() is the size of the
	 * Partial before the extension, then the new element
	 * is added to position size() and the size() is increased by 1.</p>
	 *
	 * @param extensionIndex An index into the list of elements that can be added to the 
	 * Partial.  The valid extensionIndex values are [0, 1, ..., (numExtensions()-1)].
	 * @throws ArrayIndexOutOfBoundsException 
	 * if extensionIndex is greater than or equal to numExtensions(), 
	 * or if extensionIndex is less than 0
	 */
	void extend(int extensionIndex);
	
 }
