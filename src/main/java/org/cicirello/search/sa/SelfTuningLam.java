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

package org.cicirello.search.sa;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class implements the Self-Tuning Lam annealing schedule, which is an improved variation of
 * the Modified Lam annealing schedule. The original Modified Lam annealing schedule dynamically
 * adjusts simulated annealing's temperature parameter up and down to either decrease or increase
 * the neighbor acceptance rate as necessary to attempt to match a theoretically determined ideal.
 * The Modified Lam annealing schedule is a practical realization of Lam and Delosme's (1988)
 * schedule, refined first by Swartz (1993) and then further by Boyan (1998), and later optimized
 * further by Cicirello (2020). The Modified Lam annealing schedule is, however, somewhat sensitive
 * to the magnitude of cost function differences relative to neighboring solutions. In particular,
 * the temperature can be slow to converge to that needed to achieve the target track of the rate of
 * acceptance. The Self-Tuning Lam schedule is a new variation that uses early samples of the cost
 * function to fine-tune several annealing parameters to almost instantaneously achieve the target
 * rate of acceptance, and to better match it throughout the run. This Self-Tuning Lam schedule is
 * not sensitive to cost function scale, nor to run length.
 *
 * <p>The Self-Tuning Lam is introduced in the following paper, including detailed description of
 * the algorithm, derivations of the mechanisms used for self-tuning, and experiments across a range
 * of problems demonstrating its ability to consistently and accurately follow Lam and Delosme's
 * idealized rate of neighbor acceptance, independent of run length and cost function scale:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. 2021. <a
 *       href="https://www.cicirello.org/publications/applsci-11-09828.pdf">Self-Tuning Lam
 *       Annealing: Learning Hyperparameters While Problem Solving</a>, <i>Applied Sciences</i>,
 *       11(21), Article 9828 (November 2021). doi:<a
 *       href="https://doi.org/10.3390/app11219828">10.3390/app11219828</a>.
 * </ul>
 *
 * <p>For details of the original Modified Lam, as well as prior optimizations, see the following
 * papers:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. 2020. <a
 *       href=https://www.cicirello.org/publications/eai.16-12-2020.167653.pdf>Optimizing the
 *       Modified Lam Annealing Schedule</a>. <i>Industrial Networks and Intelligent Systems</i>,
 *       7(25): 1-11, Article e1 (December 2020). doi:<a
 *       href=https://doi.org/10.4108/eai.16-12-2020.167653>10.4108/eai.16-12-2020.167653</a>.
 *   <li>Lam, J., and Delosme, J. 1988. Performance of a new annealing schedule. In Proc. 25th
 *       ACM/IEEE DAC, 306–311.
 *   <li>Swartz, W. P. 1993. Automatic Layout of Analog and Digital Mixed Macro/Standard Cell
 *       Integrated Circuits. Ph.D. Dissertation, Yale University.
 *   <li>Boyan, J. A. 1998. Learning Evaluation Functions for Global Optimization. Ph.D.
 *       Dissertation, Carnegie Mellon University, Pittsburgh, PA.
 * </ul>
 *
 * <p>The Chips-n-Salsa library also includes an implementation of the original Modified Lam
 * schedule that is the result of a direct implementation of Boyan's description of the annealing
 * schedule, in the {@link ModifiedLamOriginal} class, as well as Cicirello's Optimized Modified Lam
 * in the {@link ModifiedLam} class.
 *
 * <p>The {@link #accept} methods of this class use the classic, and most common, Boltzmann
 * distribution for determining whether to accept a neighbor.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.16.2021
 */
public final class SelfTuningLam implements AnnealingSchedule {

  private double t;
  private double acceptRate;
  private double targetRate;
  private double phase0;
  private double phase1;
  private double phase2;
  private int iterationCount;

  private double termPhase1;
  private double multPhase1;
  private double multPhase3;

  private double deltaSum;
  private int sameCostCount;
  private int betterCostCount;

  private double alpha;
  private double beta;

  private int lastMaxEvals;

  private static final double LAM_RATE_001 = 0.9768670788789564;
  private static final double LAM_RATE_002 = 0.9546897506857566;
  private static final double LAM_RATE_01 = 0.8072615745900611;
  private static final double LAM_RATE_02 = 0.6808590431613767;

  /**
   * Default constructor. The Self-Tuning Lam annealing schedule, unlike other annealing schedules,
   * has no control parameters other than the run length (the maxEvals parameter of the {@link
   * #init} method), so no parameters need be passed to the constructor.
   */
  public SelfTuningLam() {
    lastMaxEvals = -1;
  }

  @Override
  public void init(int maxEvals) {
    if (maxEvals >= 10000) {
      targetRate = acceptRate = LAM_RATE_001;
    } else {
      targetRate = acceptRate = LAM_RATE_01;
    }
    termPhase1 = acceptRate - 0.44;
    sameCostCount = 0;
    betterCostCount = 0;
    deltaSum = 0.0;
    // very very short runs won't have a phase 0, so
    // default to initial t=0.5 (the initial t of original modified lam).
    t = 0.5;
    iterationCount = 0;
    if (lastMaxEvals != maxEvals) {
      // These don't change during the run, and only depend
      // on maxEvals.  So initialize only if run length
      // has changed.

      if (maxEvals >= 10000) {
        phase0 = 0.001 * maxEvals;

        // Set alpha for a 0.01N-"day" exponential moving average
        alpha = 2.0 / (1.0 + 0.01 * maxEvals);
      } else {
        phase0 = 0.01 * maxEvals;

        // Set alpha for a 0.01N-"day" exponential moving average,
        // but no greater than 0.2.
        alpha = phase0 > 9 ? 2.0 / (1.0 + phase0) : 0.2;

        // The temperature decay rate, beta, is set at the end of phase0,
        // but very short runs (less than 100 maxEvals) won't have
        // a phase0, so need to initialize it to something.
        // Given the default initial t of 0.5 for such short runs,
        // 100 temp decay steps would need a beta of 0.94 to drop to
        // approximately t=0.001, and approximately 50 temp decay steps
        // would need a beta of around 0.88 to reach a temp of t=0.001.
        // So we chose a beta somewhere in between as the default.
        // Note that runs of at least 100 maxEvals will end up changing
        // this at end of phase0.
        beta = 0.9;
      }
      phase1 = 0.15 * maxEvals;
      phase2 = 0.65 * maxEvals;
      multPhase1 = Math.pow(560, -1.0 / phase1);
      multPhase3 = Math.pow(440, -1.0 / (maxEvals - phase2));
      lastMaxEvals = maxEvals;
    }
  }

  @Override
  public boolean accept(double neighborCost, double currentCost) {
    iterationCount++;
    if (iterationCount <= phase0) {
      doPhaseZeroUpdate(neighborCost, currentCost);
      return true;
    } else {
      boolean doAccept =
          neighborCost <= currentCost
              || ThreadLocalRandom.current().nextDouble()
                  < Math.exp((currentCost - neighborCost) / t);
      updateSchedule(doAccept);
      return doAccept;
    }
  }

  @Override
  public SelfTuningLam split() {
    return new SelfTuningLam();
  }

  private void doPhaseZeroUpdate(double neighborCost, double currentCost) {
    double costDelta = currentCost - neighborCost;
    if (costDelta > 0.0) {
      betterCostCount++;
      deltaSum += costDelta;
    } else if (costDelta < 0.0) {
      deltaSum -= costDelta;
    } else {
      sameCostCount++;
    }
    if (iterationCount + 1 > phase0) {
      initializeTemperature();
    }
  }

  private void initializeTemperature() {
    int acceptedCount = sameCostCount + betterCostCount;
    double initialAcceptanceRate =
        (acceptedCount != iterationCount)
            ? ((double) acceptedCount) / iterationCount
            : acceptedCount / (1.0 + iterationCount);
    // if all of the tuning samples had same cost (e.g., starting on
    // a plateau), we assume an average cost delta equal to 1,
    // which for an integer-cost objective function is the smallest
    // possible non-zero cost difference.
    double costAverage =
        iterationCount == sameCostCount ? 1 : deltaSum / (iterationCount - sameCostCount);
    if (initialAcceptanceRate < acceptRate) {
      double denom = Math.log((acceptRate - initialAcceptanceRate) / (1.0 - initialAcceptanceRate));
      t = -costAverage / denom;

      double dropRate = lastMaxEvals >= 10000 ? LAM_RATE_002 : LAM_RATE_02;
      if (initialAcceptanceRate < dropRate) {
        beta =
            Math.pow(
                denom
                    / Math.log((dropRate - initialAcceptanceRate) / (1.0 - initialAcceptanceRate)),
                1.0 / phase0);
      } else {
        // The constants used here are -Zeta_{2M} where where M is phase0,
        // Zeta_{2M} is -1 / Math.log(0.001 / (1.001 - LamRate(2M))).
        beta =
            Math.pow(
                denom * (lastMaxEvals >= 10000 ? -0.260731492877931 : -0.17334743675123146),
                1.0 / phase0);
      }
    } else {
      // If the tuning samples have an approximated acceptance rate
      // greater than or equal to the initial Lam rate, we assume that
      // it is 0.001 less than the initial Lam rate when computing an initial
      // temperature.
      t = costAverage * (lastMaxEvals >= 10000 ? 0.3141120890121576 : 0.18987910472222955);
      // The above is logically equivalent to:
      // t = -costAverage / Math.log(0.001 / (1.001 - acceptRate));

      // The following is equivalent to computing:
      // The M-th root of Zeta_{2M} / Zeta_{M}, where M is phase0,
      // Zeta_M is -1 / Math.log(0.001 / (1.001 - LamRate(M))).
      beta = Math.pow(lastMaxEvals >= 10000 ? 0.8300587656396743 : 0.912935823058667, 1.0 / phase0);
    }
  }

  private void updateSchedule(boolean doAccept) {
    if (doAccept) acceptRate = (1 - alpha) * acceptRate + alpha;
    else acceptRate = (1 - alpha) * acceptRate;

    if (iterationCount <= phase1) {
      // Original Modified Lam schedule indicates that targetRate should
      // be set in phase 1 (first 15% of run)
      // to: 0.44 + 0.56 * Math.pow(560, -1.0*iterationCount/phase1);
      // That involves a pow for each phase 1 iteration.  We instead compute it
      // incrementally with 1 call to pow in the init, and 1 multiplication per
      // phase 1 update.
      termPhase1 *= multPhase1;
      targetRate = 0.44 + termPhase1;
    } else if (iterationCount > phase2) {
      // Original Modified Lam schedule indicates that targetRate should
      // be set in phase 3 (last 35% of run)
      // to: 0.44 * Math.pow(440, -(1.0*iterationCount/maxEvals - 0.65)/0.35);
      // That involves a pow for each phase 3 iteration.  We instead compute it
      // incrementally with 1 call to pow in the init, and 1 multiplication per
      // phase 3 update.
      // Also note that at the end of phase 2, targetRate will equal 0.44, where phase 3 begins.
      targetRate *= multPhase3;
    } else {
      // Phase 2 (50% of run beginning after phase 1): constant targetRate at 0.44.
      targetRate = 0.44;
    }

    if (acceptRate > targetRate) t *= beta;
    else t /= beta;
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
