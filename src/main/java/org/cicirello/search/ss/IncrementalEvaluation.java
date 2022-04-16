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
 * <p>The implementations of constructive heuristics and
 * stochastic samplers biased by constructive heuristics
 * in the library support incremental updates, 
 * as the solution is heuristically assembled,
 * to problem and/or heuristic data utilized by the heuristic. 
 * Implementations of this interface depend upon the
 * specific optimization problem, as well as
 * the specific constructive heuristic.  For example,
 * for a scheduling problem, it would be more efficient to
 * compute the current time in the schedule incrementally as
 * the schedule is built than it would be to recompute it
 * each time a job is added to the schedule.  Or, for example,
 * there may be a constructive heuristic for a problem that utilizes
 * a characteristic of the portion of the problem not yet solved
 * for which it would be more efficient to compute incrementally
 * than to recompute fresh each step of the problem solving process.</p>
 *
 * @param <T> The type of Partial object that this IncrementalEvaluation evaluates, which is 
 * assumed to be an object that is a sequence of integers (e.g., vector of integers,
 * permutation, or some other indexable type that stores integers).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface IncrementalEvaluation<T extends Copyable<T>> {
	
	/**
	 * <p>Extends an 
	 * incremental evaluation,
	 * to account for extending a Partial by the addition of 
	 * one element.</p>
	 *
	 * <p>This method assumes that this IncrementalEvaluation object
	 * is consistent and up to date with the Partial p passed
	 * as a parameter.</p>
	 *
	 * <p>This extension is problem-dependent and this IncrementalEvaluation
	 * includes maintenance of any data for which incremental updates
	 * as the Partial is gradually transformed into a full T
	 * is beneficial to performance.  For example, for a scheduling problem,
	 * the IncrementalEvaluation might keep track of current time in the schedule,
	 * accounting for all of the jobs already scheduled, to avoid having to 
	 * recompute it with each extension (if the heuristic uses that to evaluate
	 * a job).</p>
	 *
	 * @param p The current state of the Partial 
	 * (assumed to be the Partial
	 * that is the subject of the IncrementalEvaluation).
	 * @param element The element that will be added to the Partial.
	 * This method should not actually add element to p. Rather, it should update
	 * the IncrementalEvaluation to coincide with the addition of element to p.
	 */
	void extend(Partial<T> p, int element);
}