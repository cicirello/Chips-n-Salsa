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
 * Parses a single machine scheduling problem instance data file that follows the format specified
 * in the <a href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/wtinfo.html>OR-Library of J.E.
 * Beasley</a>. The <a
 * href=https://github.com/cicirello/scheduling-benchmarks/tree/master/wt>description</a>, along
 * with a set of benchmark instances, is mirrored in the following GitHub repository: <a
 * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
 *
 * <p>The format from the benchmark library is a bit unusual. Each file contains many instances.
 * There is no labeling info, so you need to know the number of jobs, n, contained in the file. The
 * first instance is listed first with n process times, followed by n weights, followed by n
 * duedates. This is then followed by second instance (n process times, n weights, and then n
 * duedates), etc, with no instance separators.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class WeightedStaticSchedulingReader {

  private final int[] process;
  private final int[] duedates;
  private final int[] weights;

  /**
   * Parses an instance.
   *
   * @param file the file
   * @param n the number of jobs
   * @param instanceNumber the instance number (the file may contain many instances). for the first
   *     instance, this should be 0.
   */
  public WeightedStaticSchedulingReader(Readable file, int n, int instanceNumber) {
    try (Scanner in = new Scanner(file)) {
      // Format from the benchmark library is a bit weird:
      // - It contains many instances.
      // - No labeling info, so you need to know the number of jobs, n.
      // - First instance: n process times, followed by n weights, followed
      //   by n duedates.  This is then followed by second instance (n process times,
      //   n weights, and then n duedates), etc.  No instance separators.

      // First, skip to the instance we need:
      int skipCount = instanceNumber * n * 3;
      for (int i = 0; i < skipCount; i++) in.nextInt();

      // Get process times:
      process = new int[n];
      for (int i = 0; i < n; i++) {
        process[i] = in.nextInt();
      }
      // Get weights times:
      weights = new int[n];
      for (int i = 0; i < n; i++) {
        weights[i] = in.nextInt();
      }
      // Get duedates times:
      duedates = new int[n];
      for (int i = 0; i < n; i++) {
        duedates[i] = in.nextInt();
      }
    }
  }

  /**
   * Gets the process times.
   *
   * @return array of process times
   */
  public int[] process() {
    return process;
  }

  /**
   * Gets the weights.
   *
   * @return array of weights
   */
  public int[] weights() {
    return weights;
  }

  /**
   * Gets the duedates.
   *
   * @return array of duedates
   */
  public int[] duedates() {
    return duedates;
  }
}
