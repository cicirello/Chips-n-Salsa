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

package org.cicirello.search.problems.scheduling;

import java.util.Scanner;

/**
 * Package-access reader of a single machine scheduling problem instance by parsing an instance data
 * file that follows the format specified in the following paper, with instances available at the
 * following link:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. <a
 *       href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness
 *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report,
 *       Intelligent Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon
 *       University, Pittsburgh, PA, February 2003.
 *   <li>Vincent A. Cicirello. <a href="http://dx.doi.org/10.7910/DVN/VHA0VQ">Weighted Tardiness
 *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Harvard Dataverse,
 *       doi:10.7910/DVN/VHA0VQ, June 2016.
 * </ul>
 *
 * <p>Note that the paper above describes a weighted tardiness scheduling problem, but the instance
 * data (process and setup times, weights, duedates) can also be used with other scheduling
 * objective functions
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class WeightedStaticSchedulingWithSetupsReader {

  private final int[] process;
  private final int[] duedates;
  private final int[] weights;
  private final int[][] setups;

  /**
   * Parser for benchmark scheduling instance data files that is described in: Vincent A. Cicirello
   * (2003). "Weighted Tardiness Scheduling with Sequence-Dependent Setups: A Benchmark Library".
   * Technical Report, Intelligent Coordination and Logistics Laboratory, Robotics Institute,
   * Carnegie Mellon University, Pittsburgh, PA, February 2003.
   *
   * <p>Some instance files can be found at: https://www.cicirello.org/datasets/wtsds/ And also at:
   * http://dx.doi.org/10.7910/DVN/VHA0VQ
   *
   * @param file The file to parse
   */
  public WeightedStaticSchedulingWithSetupsReader(Readable file) {
    Scanner in = new Scanner(file);
    while (!in.next().equals("Size:"))
      ;
    final int n = in.nextInt();
    duedates = new int[n];
    weights = new int[n];
    process = new int[n];
    setups = new int[n][n];
    while (!in.nextLine().equals("Process Times:"))
      ;
    for (int i = 0; i < n; i++) {
      process[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    for (int i = 0; i < n; i++) {
      weights[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    for (int i = 0; i < n; i++) {
      duedates[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    while (in.hasNextInt()) {
      int i = in.nextInt();
      int j = in.nextInt();
      int setup = in.nextInt();
      if (i == -1) i = j;
      setups[i][j] = setup;
    }
    in.close();
  }

  /**
   * Gets the array of process times.
   *
   * @param the process times
   */
  public int[] process() {
    return process;
  }

  /**
   * Gets the array of weights.
   *
   * @param the weights
   */
  public int[] weights() {
    return weights;
  }

  /**
   * Gets the array of duedates.
   *
   * @param the duedates
   */
  public int[] duedates() {
    return duedates;
  }

  /**
   * Gets the array of setups.
   *
   * @param the setups
   */
  public int[][] setups() {
    return setups;
  }
}
