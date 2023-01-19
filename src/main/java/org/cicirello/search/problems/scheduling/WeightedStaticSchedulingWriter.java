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
 * Outputs a description of the instance data in the format described by the <a
 * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/wtinfo.html>OR-Library of J.E. Beasley</a>.
 * The <a href=https://github.com/cicirello/scheduling-benchmarks/tree/master/wt>description</a>,
 * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
 * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
 *
 * <p>The only different with that format is that this stores only the one instance in the file. But
 * for consistency with the original format, you do need to know the number of jobs for the instance
 * (or you can determine this by counting number of integers in the file and dividing by 3.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class WeightedStaticSchedulingWriter {

  private final WeightedStaticScheduling s;

  /**
   * Initialize the instance writer.
   *
   * @param s the instance
   */
  public WeightedStaticSchedulingWriter(WeightedStaticScheduling s) {
    this.s = s;
  }

  /**
   * Writes an instance.
   *
   * @param out The destination
   */
  public void toFile(PrintWriter out) {
    int n = s.numberOfJobs();
    for (int i = 0; i < n; i++) {
      out.print(s.getProcessingTime(i));
      if (i == n - 1) out.println();
      else out.print(" ");
    }
    for (int i = 0; i < n; i++) {
      out.print(s.getWeight(i));
      if (i == n - 1) out.println();
      else out.print(" ");
    }
    for (int i = 0; i < n; i++) {
      out.print(s.getDueDate(i));
      if (i == n - 1) out.println();
      else out.print(" ");
    }
  }
}
