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
 * Outputs a description of the instance data in the format based on that described in:
 *
 * <p>Vincent A. Cicirello. <a
 * href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness Scheduling
 * with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report, Intelligent
 * Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon University,
 * Pittsburgh, PA, February 2003.
 *
 * <p>The data as output by this method varies from that format in that it does not output the
 * "Generator Parameters" section. Instead, it has the "Begin Generator Parameters" and the "End
 * Generator Parameters" block markers, but an empty block. The constructor of this class that takes
 * an instance data file as input can correctly parse both the original and this modified format.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class WeightedStaticSchedulingWithSetupsWriter {

  private final WeightedStaticSchedulingWithSetups s;

  /**
   * Initialize the instance writer.
   *
   * @param s the instance
   */
  public WeightedStaticSchedulingWithSetupsWriter(WeightedStaticSchedulingWithSetups s) {
    this.s = s;
  }

  /**
   * Writes an instance.
   *
   * @param out The destination
   * @param instanceNumber An id for the problem instance.
   */
  public void toFile(PrintWriter out, int instanceNumber) {
    out.print("Problem Instance: ");
    out.println(instanceNumber);
    out.print("Problem Size: ");
    int n = s.numberOfJobs();
    out.println(n);
    out.println("Begin Generator Parameters");
    out.println("End Generator Parameters");
    out.println("Begin Problem Specification");
    out.println("Process Times:");
    for (int i = 0; i < n; i++) {
      out.println(s.getProcessingTime(i));
    }
    out.println("Weights:");
    for (int i = 0; i < n; i++) {
      out.println(s.getWeight(i));
    }
    out.println("Duedates:");
    for (int i = 0; i < n; i++) {
      out.println(s.getDueDate(i));
    }
    out.println("Setup Times:");
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i == j) {
          out.printf("%d %d %d\n", -1, j, s.getSetupTime(i, j));
        } else {
          out.printf("%d %d %d\n", i, j, s.getSetupTime(i, j));
        }
      }
    }
    out.println("End Problem Specification");
  }
}
