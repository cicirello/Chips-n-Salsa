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

/**
 * <p>The implementations of constructive heuristics and
 * stochastic samplers biased by constructive heuristics
 * in the library support incrementally evaluating the
 * permutations as they are heuristically assembled. Classes
 * that implement the IncrementalEvaluation interface support
 * this.  Implementations of this interface depend upon the
 * specific optimization problem, and in some cases, may even
 * depend upon the specific constructive heuristic.  For example,
 * for a scheduling problem, it would be more efficient to
 * compute the current time in the schedule incrementally as
 * the schedule is built than it would be to recompute it
 * each time a job is added to the schedule.  Or, for example,
 * there may be a constructive heuristic for a problem that utilizes
 * a characteristic of the portion of the problem not yet solved
 * for which it would be more efficient to compute incrementally
 * than to recompute fresh each step of the problem solving process.</p>
 *
 * @param <T> The type of value for the cost function of the
 * optimization problem (Integer or Double).
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.7.2020
 */
public interface IncrementalEvaluation<T extends Number> {
	
	/**
	 * <p>Extends an 
	 * incremental evaluation,
	 * to account for extending a PartialPermutation by the addition of 
	 * one element.</p>
	 *
	 * <p>This method assumes that this IncrementalEvaluation object
	 * is consistent and up to date with the PartialPermutation p passed
	 * as a parameter.</p>
	 *
	 * <p>This extension is problem-dependent and this IncrementalEvaluation
	 * includes maintenance of any data for which incremental updates
	 * as the PartialPermutation is gradually transformed into a full Permutation
	 * is beneficial to performance.  For example, for a scheduling problem,
	 * the IncrementalEvaluation may keep track of current time in the schedule,
	 * accounting for all of the jobs already scheduled, to avoid having to 
	 * recompute it with each extension.  This would be in addition to an incremental
	 * calculation of whatever the scheduling objective is.</p>
	 *
	 * @param p The current state of the PartialPermutation 
	 * (assumed to be the PartialPermutation
	 * that is the subject of the IncrementalEvaluation).
	 * @param element The element that will be added to the PartialPermutation.
	 * This method should not actually add element to p. Rather, it should update
	 * the IncrementalEvaluation to coincide with the addition of element to p.
	 */
	void extend(PartialPermutation p, int element);
	
	/**
	 * The current cost of this IncrementalEvaluation, which includes
	 * costs associated with the part of the permutation already assembled.
	 * @return The current cost of this IncrementalEvaluation.
	 */
	T cost();
}