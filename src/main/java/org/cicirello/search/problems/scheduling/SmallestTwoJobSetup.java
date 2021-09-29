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

package org.cicirello.search.problems.scheduling;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;

/**
 * <p>This heuristic is smallest two-job setup.
 * We define it as: h(j) = 1 / (1 + s[i][j] + min<sub>k</sub>(s[j][k])),
 * where s[i][j] is the setup time of job j if it
 * follows job i on the machine, job i is the most recently schedule job,
 * and min<sub>k</sub>(s[j][k]) is the minimum setup of potential
 * successors of job j.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public final class SmallestTwoJobSetup extends SchedulingHeuristic {
	
	/**
	 * Constructs an SmallestTwoJobSetup heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public SmallestTwoJobSetup(SingleMachineSchedulingProblem problem) {
		super(problem);
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		if (HAS_SETUPS) {
			double denominator = 1.0 +
					(p.size()==0 ? data.getSetupTime(element) 
					: data.getSetupTime(p.getLast(), element));
			int n = p.numExtensions();
			if (n > 1) {
				int minS = Integer.MAX_VALUE;
				for (int i = 0; i < n; i++) {
					int k = p.getExtension(i);
					if (k != element) {
						int nextS = data.getSetupTime(element, k);
						if (nextS < minS) minS = nextS;
					}
				}
				denominator += minS;
			}
			double s = 1.0 / denominator; 
			return s <= MIN_H ? MIN_H : s;
		} else {
			return 1;
		}
	}
}