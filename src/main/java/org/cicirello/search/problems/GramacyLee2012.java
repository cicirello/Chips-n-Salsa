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

package org.cicirello.search.problems;

import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.reals.RealValueInitializer;
import org.cicirello.search.representations.SingleReal;

/**
 * A continuous function with a large number of local minimums, and a single global minimum, defined
 * for input x in [0.5, 2.5].
 *
 * <p>This minimization problem was introduced in:
 *
 * <ul>
 *   <li>Gramacy, R.B.; Lee, H.K.H. Cases for the nugget in modeling computer experiments.
 *       Statistics and Computing 2012, 22, 713–722. doi:10.1007/s11222-010-9224-x.
 * </ul>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class GramacyLee2012
    implements OptimizationProblem<SingleReal>, Initializer<SingleReal> {

  private final RealValueInitializer init;

  /** Constructs the GramacyLee2012 cost function. */
  public GramacyLee2012() {
    init = new RealValueInitializer(0.5, 2.5, 0.5, 2.5);
  }

  @Override
  public double cost(SingleReal candidate) {
    return 0.5 * Math.sin(10 * Math.PI * candidate.get()) / candidate.get()
        + Math.pow(candidate.get() - 1, 4);
  }

  @Override
  public double value(SingleReal candidate) {
    return cost(candidate);
  }

  @Override
  public SingleReal createCandidateSolution() {
    return init.createCandidateSolution();
  }

  @Override
  public GramacyLee2012 split() {
    return new GramacyLee2012();
  }
}
