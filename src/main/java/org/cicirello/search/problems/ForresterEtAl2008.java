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
 * A continuous function with a single suboptimal local minimum, and a single global minimum, and a
 * 0 derivative inflexion point, defined for inputs x in [0.0, 1.0].
 *
 * <p>This minimization problem was introduced in:
 *
 * <ul>
 *   <li>Forrester, A.I.J.; Sóbester, A.; Keane, A.J. Appendix: Example Problems. In Engineering
 *       Design via Surrogate Modelling: A Practical Guide; John Wiley and Sons, Ltd, 2008; pp.
 *       195–203. doi:10.1002/9780470770801.app1.
 * </ul>
 *
 * <p>The lower fidelity variation is as defined <a
 * href="https://www.sfu.ca/~ssurjano/forretal08.html">https://www.sfu.ca/~ssurjano/forretal08.html</a>.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ForresterEtAl2008
    implements OptimizationProblem<SingleReal>, Initializer<SingleReal> {

  private final boolean ORIGINAL;
  private final double A;
  private final double B;
  private final double C;
  private final RealValueInitializer init;

  /** Constructs the original version of the ForresterEtAl2008 cost function. */
  public ForresterEtAl2008() {
    this(false);
  }

  /**
   * Constructs the ForresterEtAl2008 cost function.
   *
   * @param lowerFidelity - If false, constructs the original function. If true, constructs the
   *     lower fidelity version of the function.
   */
  public ForresterEtAl2008(boolean lowerFidelity) {
    ORIGINAL = !lowerFidelity;
    if (ORIGINAL) {
      A = 1;
      B = C = 0;
    } else {
      A = 0.5;
      B = 10;
      C = -5;
    }
    init = new RealValueInitializer(0.0, 1.0, 0.0, 1.0);
  }

  @Override
  public double cost(SingleReal candidate) {
    if (ORIGINAL) {
      return original(candidate);
    } else {
      return A * original(candidate) + B * (candidate.get() - 0.5) - C;
    }
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
  public ForresterEtAl2008 split() {
    return new ForresterEtAl2008(!ORIGINAL);
  }

  private double original(SingleReal candidate) {
    double term = 6 * candidate.get() - 2;
    return term * term * Math.sin(12 * candidate.get() - 4);
  }
}
