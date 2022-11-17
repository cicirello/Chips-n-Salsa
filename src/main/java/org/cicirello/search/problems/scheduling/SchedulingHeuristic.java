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
import org.cicirello.search.problems.Problem;
import org.cicirello.search.ss.ConstructiveHeuristic;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.PartialPermutation;

/**
 * This class serves as an abstract base class for the scheduling heuristics of the library,
 * handling common functionality such as maintaining the scheduling problem instance.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
abstract class SchedulingHeuristic implements ConstructiveHeuristic<Permutation> {

  /**
   * The minimum heuristic value. If the heuristic value as calculated is lower than MIN_H, then
   * MIN_H is used as the heuristic value. The reason is related to the primary purpose of the
   * constructive heuristics in the library: heuristic guidance for stochastic sampling algorithms,
   * which assume positive heuristic values (e.g., an h of 0 would be problematic).
   */
  public static final double MIN_H = 0.00001;

  /** The instance of the scheduling problem that is the target of the heuristic. */
  final SingleMachineSchedulingProblem problem;

  /** The instance data of the scheduling problem that is the target of the heuristic. */
  final SingleMachineSchedulingProblemData data;

  final boolean HAS_SETUPS;

  /**
   * Initializes the abstract base class for scheduling heuristics.
   *
   * @param problem The instance of a scheduling problem that is the target of the heuristic.
   */
  public SchedulingHeuristic(SingleMachineSchedulingProblem problem) {
    this.problem = problem;
    data = problem.getInstanceData();
    HAS_SETUPS = data.hasSetupTimes();
  }

  @Override
  public final Problem<Permutation> getProblem() {
    return problem;
  }

  @Override
  public final Partial<Permutation> createPartial(int n) {
    return new PartialPermutation(n);
  }

  @Override
  public final int completeLength() {
    return data.numberOfJobs();
  }

  /*
   * package-private rather than private to enable test case access
   */
  int sumOfProcessingTimes() {
    int total = 0;
    int n = data.numberOfJobs();
    for (int i = 0; i < n; i++) {
      total += data.getProcessingTime(i);
    }
    return total;
  }

  /*
   * package-private rather than private to enable test case access
   */
  int sumOfSetupTimes() {
    if (!HAS_SETUPS) return 0;
    int total = 0;
    int n = data.numberOfJobs();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        total += data.getSetupTime(i, j);
      }
    }
    return total;
  }

  /*
   * package-private rather than private to enable test case access
   */
  class IncrementalTimeCalculator implements IncrementalEvaluation<Permutation> {

    private int currentTime;

    @Override
    public void extend(Partial<Permutation> p, int element) {
      currentTime += data.getProcessingTime(element);
      if (HAS_SETUPS) {
        currentTime +=
            p.size() == 0 ? data.getSetupTime(element) : data.getSetupTime(p.getLast(), element);
      }
    }

    /**
     * Gets the current time at the end of the current partial schedule.
     *
     * @return current time
     */
    public final int currentTime() {
      return currentTime;
    }

    /**
     * Computes the slackness of a job given the current partial schedule.
     *
     * @param element The job whose slackness to compute.
     * @return slackness of job element.
     */
    public final int slack(int element) {
      return data.getDueDate(element) - currentTime - data.getProcessingTime(element);
    }

    /**
     * Computes the slackness of a job given the current partial schedule.
     *
     * @param element The job whose slackness to compute.
     * @param p The partial schedule to evaluate this relative to (needed only for setups).
     * @return slackness of job element.
     */
    public final int slack(int element, Partial<Permutation> p) {
      int s = slack(element);
      if (HAS_SETUPS) {
        s -= p.size() == 0 ? data.getSetupTime(element) : data.getSetupTime(p.getLast(), element);
      }
      return s;
    }

    /**
     * Computes the (non-negative) slackness of a job given the current partial schedule.
     *
     * @param element The job whose slackness to compute.
     * @return slackness of job element.
     */
    public final int slackPlus(int element) {
      int s = slack(element);
      return s > 0 ? s : 0;
    }

    /**
     * Computes the (non-negative) slackness of a job given the current partial schedule.
     *
     * @param element The job whose slackness to compute.
     * @param p The partial schedule to evaluate this relative to (needed only for setups).
     * @return slackness of job element.
     */
    public final int slackPlus(int element, Partial<Permutation> p) {
      int s = slack(element);
      if (HAS_SETUPS && s > 0) {
        s -= p.size() == 0 ? data.getSetupTime(element) : data.getSetupTime(p.getLast(), element);
      }
      return s > 0 ? s : 0;
    }
  }

  /*
   * package-private rather than private to enable test case access
   */
  class IncrementalAverageProcessingCalculator extends IncrementalTimeCalculator {

    // total processing time of remaining jobs
    private int totalP;

    // num jobs left
    int n;

    public IncrementalAverageProcessingCalculator(int sumOfP) {
      super();
      n = data.numberOfJobs();
      totalP = sumOfP;
    }

    @Override
    public void extend(Partial<Permutation> p, int element) {
      super.extend(p, element);
      totalP -= data.getProcessingTime(element);
      n--;
    }

    /**
     * Gets the total processing time of unscheduled jobs.
     *
     * @return total processing time of unscheduled jobs
     */
    public int totalProcessingTime() {
      return totalP;
    }

    /**
     * Gets the average processing time of unscheduled jobs.
     *
     * @return average processing time of unscheduled jobs
     */
    public double averageProcessingTime() {
      return ((double) totalP) / n;
    }
  }
}
