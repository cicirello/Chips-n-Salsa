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
 * Package-access parser for benchmark common duedate scheduling instances from the OR-Library.
 * Parses an instance data file that follows the format specified in the <a
 * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/schinfo.html>OR-Library of J.E. Beasley</a>.
 * The <a
 * href=https://github.com/cicirello/scheduling-benchmarks/tree/master/common-due-date>description</a>,
 * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
 * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
 *
 * <p>The first line of the file has the number of instances in the file. This is then followed by
 * the data for each instance in the following form. Number of jobs, n, for the instance on a line
 * by itself. This is then followed by n lines, one for each job, where the line consists of 3
 * integers: process time, earliness weight, and tardiness weight. These are separated by
 * whitespace. Don't assume any specific number of whitespace characters. This seems to vary. Lines
 * may also begin with whitespace.
 *
 * <p>The h parameter is not specified in the file, and each instance in a file can be used to
 * specify multiple benchmark instances with varying degrees of duedate tightness. The instances in
 * the OR-Library assume values of h equal to 0.2, 0.4, 0.6, and 0.8 the OR-Library provides bounds
 * on optimal solutions for those values of h), but you can potentially define additional instances
 * using additional values of h. The only constraint on h is: 0.0 &le; h &le; 1.0. It is used to
 * define the common duedate for the instance as a percentage of the sum of process times.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class CommonDuedateInstanceReader {

  private final int[] process;
  private final int[] earlyWeights;
  private final int[] weights;
  private final int duedate;

  /**
   * Parses an instance of the common duedate scheduling instances from the OR-Library.
   *
   * @param file The file with the set of instances from the OR-library.
   * @param instanceNumber The number of the instance from the file to parse.
   * @param h Controls the tightness of the common duedate for the instance, as a percentage of the
   *     sum of process times, 0.0 &le; h &le; 1.0.
   */
  public CommonDuedateInstanceReader(Readable file, int instanceNumber, double h) {
    if (instanceNumber < 0)
      throw new IllegalArgumentException("instanceNumber must be nonnegative");
    if (h < 0 || h > 1) throw new IllegalArgumentException("h must be in [0.0, 1.0]");
    Scanner in = new Scanner(file);

    String line = in.nextLine();
    Scanner lineScanner = new Scanner(line);
    int numInstances = lineScanner.nextInt();
    lineScanner.close();
    if (instanceNumber >= numInstances) {
      in.close();
      throw new IllegalArgumentException("instanceNumber is too high.");
    }

    for (int i = 0; i < instanceNumber; i++) {
      skipInstance(in);
    }

    line = in.nextLine();
    lineScanner = new Scanner(line);
    int numJobs = lineScanner.nextInt();
    lineScanner.close();

    process = new int[numJobs];
    earlyWeights = new int[numJobs];
    weights = new int[numJobs];
    int totalP = 0;
    for (int i = 0; i < numJobs; i++) {
      lineScanner = new Scanner(in.nextLine());
      process[i] = lineScanner.nextInt();
      totalP += process[i];
      earlyWeights[i] = lineScanner.nextInt();
      weights[i] = lineScanner.nextInt();
      lineScanner.close();
    }
    duedate = (int) (totalP * h);

    in.close();
  }

  /**
   * Gets the array of process times.
   *
   * @return the process times
   */
  public int[] processTimes() {
    return process;
  }

  /**
   * Gets the array of early weights.
   *
   * @return the early weights
   */
  public int[] earlyWeights() {
    return earlyWeights;
  }

  /**
   * Gets the array of weights.
   *
   * @return the weights
   */
  public int[] weights() {
    return weights;
  }

  /**
   * Gets the common duedate.
   *
   * @return the common duedate
   */
  public int duedate() {
    return duedate;
  }

  private void skipInstance(Scanner in) {
    String line = in.nextLine();
    Scanner lineScanner = new Scanner(line);
    int numJobs = lineScanner.nextInt();
    lineScanner.close();
    for (int i = 0; i < numJobs; i++) {
      in.nextLine();
    }
  }
}
