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

package org.cicirello.search.problems.scheduling;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;

/**
 * <p>This heuristic is the smallest setup first.
 * We define it as: h(j) = 1 / (1 + s[i][j]),
 * where s[i][j] is the setup time of job j if it
 * follows job i on the machine.</p>
 *
 * <p>The heuristic values are computed each time the
 * {@link #h} method is called. Therefore, for a many
 * iterations of stochastic sampling, the same 
 * heuristic values may be computed repeatedly. If your
 * problem instance is small enough to be able to afford
 * the extra memory, you might consider instead using
 * the {@link SmallestSetupPrecompute} class, which
 * implements the same heuristic, but it precomputes
 * a table of heuristic values upon constructing the
 * heuristic object.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 2.16.2021
 */
public final class SmallestSetup extends SchedulingHeuristic {
	
	/**
	 * Constructs an SmallestSetup heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public SmallestSetup(SingleMachineSchedulingProblem problem) {
		super(problem);
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		if (HAS_SETUPS) {
			double s = 1.0 /
				(1.0 +
					(p.size()==0 ? data.getSetupTime(element) 
					: data.getSetupTime(p.getLast(), element))
				); 
			return s <= MIN_H ? MIN_H : s;
		} else {
			return 1;
		}
	}
}