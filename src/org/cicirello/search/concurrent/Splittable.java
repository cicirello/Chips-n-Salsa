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
 
package org.cicirello.search.concurrent;

/**
 * The Splittable interface provides multithreaded search algorithms
 * with the ability to generate functionally identical copies
 * of operators, and even entire metaheuristics, at the point of spawning new search threads.
 * The state of the object
 * that is returned may or may not be identical to that of the original.  Thus,
 * this is a distinct concept from the functionality of the {@link org.cicirello.util.Copyable} interface.
 * Additionally, the {@link #split} method is allowed to simply return the this reference, provided that it is
 * both safe and efficient for multiple threads to share a single copy of the Splittable object.
 *
 * @param <T> The type of object that supports splitting.
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.9.2020
 */
public interface Splittable<T extends Splittable<T>> {
	
	/**
	 * Generates a functionally identical copy of this object, for use in
	 * multithreaded implementations of search algorithms.  The state of the object
	 * that is returned may or may not be identical to that of the original.  Thus,
	 * this is a distinct concept from the functionality of the {@link org.cicirello.util.Copyable} interface.
	 * Classes that implement this interface must ensure that the object returned 
	 * performs the same functionality, and that it does not share any state data
	 * that would be either unsafe or inefficient for concurrent access by multiple threads.
	 * The split method is allowed to simply return the this reference, provided that it is
	 * both safe and efficient for multiple threads to share a single copy of the Splittable object.
	 * The intention is to provide a multithreaded search with the capability to 
	 * provide spawned threads with their own distinct search operators.
	 * Such multithreaded algorithms can call the split method for each thread it spawns
	 * to generate a functionally identical copy of the operator, but with independent
	 * state.
	 *
	 * @return A functionally identical copy of the object, or a reference to this if
	 * it is both safe and efficient for multiple threads to share a single instance of this
	 * Splittable object.
	 */
	T split();
}