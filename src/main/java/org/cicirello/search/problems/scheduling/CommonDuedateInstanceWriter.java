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

import java.io.PrintWriter;

/**
 * Package-access file writer for common duedate scheduling instances in the format of the
 * OR-Library. Writes an instance data file that follows the format specified in the <a
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
 * whitespace.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class CommonDuedateInstanceWriter {

  private final CommonDuedateScheduling s;

  /**
   * Initialize the instance writer.
   *
   * @param s the instance
   */
  public CommonDuedateInstanceWriter(CommonDuedateScheduling s) {
    this.s = s;
  }

  /**
   * Writes an instance.
   *
   * @param out The destination
   */
  public void toFile(PrintWriter out) {
    out.println(1);
    int n = s.numberOfJobs();
    out.println(n);
    for (int i = 0; i < n; i++) {
      out.print(s.getProcessingTime(i));
      out.print("\t");
      out.print(s.getEarlyWeight(i));
      out.print("\t");
      out.println(s.getWeight(i));
    }
  }
}
