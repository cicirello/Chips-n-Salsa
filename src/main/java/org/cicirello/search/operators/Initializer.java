/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 
package org.cicirello.search.operators;

import org.cicirello.search.concurrent.Splittable;

/**
 * <p>Implement the Initializer interface to provide
 * metaheuristics and other search algorithms with a way
 * to generate initial candidate solutions to a problem.</p>
 *
 * <p>Many such algorithms start with a randomized initial candidate solution,
 * while some start with a heuristic solution.  Classes that implement
 * this interface serve that functionality.</p>
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface Initializer<T> extends Splittable<Initializer<T>> {
	
	/**
	 * Creates one candidate solution to a problem.
	 *
	 * @return a candidate solution to a problem instance.
	 */
	T createCandidateSolution();
}