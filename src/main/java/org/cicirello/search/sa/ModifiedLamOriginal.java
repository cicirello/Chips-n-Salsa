/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

package org.cicirello.search.sa;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.internal.RandomnessFactory;

/**
 * This class implements the Modified Lam annealing schedule, which dynamically adjusts simulated
 * annealing's temperature parameter up and down to either decrease or increase the neighbor
 * acceptance rate as necessary to attempt to match a theoretically determined ideal. The Modified
 * Lam annealing schedule is a practical realization of Lam and Delosme's (1988) schedule, refined
 * first by Swartz (1993) and then further by Boyan (1998). For complete details of the Modified Lam
 * schedule, along with its origins and rationale, see the following references:
 *
 * <ul>
 *   <li>Lam, J., and Delosme, J. 1988. Performance of a new annealing schedule. In Proc. 25th
 *       ACM/IEEE DAC, 306–311.
 *   <li>Swartz, W. P. 1993. Automatic Layout of Analog and Digital Mixed Macro/Standard Cell
 *       Integrated Circuits. Ph.D. Dissertation, Yale University.
 *   <li>Boyan, J. A. 1998. Learning Evaluation Functions for Global Optimization. Ph.D.
 *       Dissertation, Carnegie Mellon University, Pittsburgh, PA.
 * </ul>
 *
 * <p>This class, ModifiedLamOriginal, is a direct implementation of the Modified Lam schedule as
 * described in the reference to Boyan above. In most cases, if you want to use the Modified Lam
 * schedule, you should prefer the {@link ModifiedLam} class, which includes a variety of
 * optimizations to speed up the updating of schedule parameters. This ModifiedLamOriginal class is
 * included in the library for investigating the benefit of the optimizations incorporated into the
 * {@link ModifiedLam} class (see that class's documentation for a description of the specific
 * optimizations made).
 *
 * <p>The {@link #accept} methods of this class use the classic, and most common, Boltzmann
 * distribution for determining whether to accept a neighbor.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ModifiedLamOriginal implements AnnealingSchedule {

  private double t;
  private double acceptRate;
  private double targetRate;
  private double phase1;
  private double phase2;
  private int iterationCount;

  private int lastMaxEvals;

  private final EnhancedSplittableGenerator generator;

  /**
   * Default constructor. The Modified Lam annealing schedule, unlike other annealing schedules, has
   * no control parameters other than the run length (the maxEvals parameter of the {@link #init}
   * method), so no parameters need be passed to the constructor.
   */
  public ModifiedLamOriginal() {
    lastMaxEvals = -1;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private ModifiedLamOriginal(ModifiedLamOriginal other) {
    lastMaxEvals = -1;
    generator = other.generator.split();
  }

  @Override
  public void init(int maxEvals) {
    t = 0.5;
    acceptRate = 0.5;
    targetRate = 1.0;
    iterationCount = 0;

    if (lastMaxEvals != maxEvals) {
      // These don't change during the run, and only depend
      // on maxEvals.  So initialize only if run length
      // has changed.
      phase1 = 0.15 * maxEvals;
      phase2 = 0.65 * maxEvals;
      lastMaxEvals = maxEvals;
    }
  }

  @Override
  public boolean accept(double neighborCost, double currentCost) {
    boolean doAccept =
        neighborCost <= currentCost
            || generator.nextDouble() < Math.exp((currentCost - neighborCost) / t);
    updateSchedule(doAccept);
    return doAccept;
  }

  @Override
  public ModifiedLamOriginal split() {
    return new ModifiedLamOriginal(this);
  }

  private void updateSchedule(boolean doAccept) {
    if (doAccept) acceptRate = 0.998 * acceptRate + 0.002;
    else acceptRate = 0.998 * acceptRate;

    iterationCount++;

    if (iterationCount <= phase1) {
      targetRate = 0.44 + 0.56 * Math.pow(560, -1.0 * iterationCount / phase1);
    } else if (iterationCount > phase2) {
      targetRate = 0.44 * Math.pow(440, -(1.0 * iterationCount / lastMaxEvals - 0.65) / 0.35);
    } else {
      // Phase 2 (50% of run beginning after phase 1): constant targetRate at 0.44.
      targetRate = 0.44;
    }

    if (acceptRate > targetRate) t *= 0.999;
    else t /= 0.999;
  }

  /*
   * package-private for unit testing
   */
  double getTargetRate() {
    return targetRate;
  }

  /*
   * package-private for unit testing
   */
  double getAcceptRate() {
    return acceptRate;
  }

  /*
   * package-private for unit testing
   */
  double getTemperature() {
    return t;
  }
}
