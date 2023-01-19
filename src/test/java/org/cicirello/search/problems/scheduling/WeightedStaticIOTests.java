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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.*;

/** JUnit tests for WeightedStaticSchedulingReader and WeightedStaticSchedulingWriter. */
public class WeightedStaticIOTests {

  @BeforeAll
  public static void createOutputDirectory() {
    File directory = new File("target/testcasedata");
    if (!directory.exists()) {
      directory.mkdir();
    }
  }

  @Test
  public void testReadSkippingInstance() {
    int n = 3;
    String contents = "1 1 1\n2 2 2\n3 3 3\n9 8 7\n6 5 4\n3 2 1\n";
    WeightedStaticSchedulingReader instanceReader =
        new WeightedStaticSchedulingReader(new StringReader(contents), n, 1);
    int[] process = instanceReader.process();
    int[] weights = instanceReader.weights();
    int[] duedates = instanceReader.duedates();
    assertEquals(n, process.length);
    assertEquals(n, weights.length);
    assertEquals(n, duedates.length);
    for (int job = 0; job < n; job++) {
      assertEquals(9 - job, process[job]);
      assertEquals(6 - job, weights[job]);
      assertEquals(3 - job, duedates[job]);
    }
  }

  @Test
  public void testReadWriteInstanceData() {
    double[] rdd = {0.25, 0.5, 0.75, 1.0};
    double[] tf = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int n = 1; n < 5; n++) {
      for (int r = 0; r < rdd.length; r++) {
        for (int t = 0; t < tf.length; t++) {
          WeightedStaticScheduling s = new WeightedStaticScheduling(n, rdd[r], tf[t], 42);
          WeightedStaticSchedulingWriter instanceWriter = new WeightedStaticSchedulingWriter(s);
          StringWriter sOut = new StringWriter();
          PrintWriter out = new PrintWriter(sOut);
          instanceWriter.toFile(out);
          WeightedStaticSchedulingReader instanceReader =
              new WeightedStaticSchedulingReader(new StringReader(sOut.toString()), n, 0);
          int[] process = instanceReader.process();
          int[] weights = instanceReader.weights();
          int[] duedates = instanceReader.duedates();
          assertEquals(s.numberOfJobs(), process.length);
          assertEquals(s.numberOfJobs(), weights.length);
          assertEquals(s.numberOfJobs(), duedates.length);

          for (int job = 0; job < n; job++) {
            assertEquals(s.getProcessingTime(job), process[job]);
            assertEquals(s.getDueDate(job), duedates[job]);
            assertEquals(s.getWeight(job), weights[job]);
          }
        }
      }
    }
  }

  @Test
  public void testReadWriteToFile() {
    WeightedStaticScheduling original = new WeightedStaticScheduling(7, 0.5, 0.5, 42);
    try {
      String file = "target/testcasedata/ws.testcase.data";
      original.toFile(file);
      WeightedStaticScheduling s = new WeightedStaticScheduling(file, 7, 0);
      assertEquals(7, s.numberOfJobs());
      for (int job = 0; job < 7; job++) {
        assertEquals(original.getProcessingTime(job), s.getProcessingTime(job));
        assertEquals(original.getWeight(job), s.getWeight(job));
        assertEquals(original.getDueDate(job), s.getDueDate(job));
      }
    } catch (FileNotFoundException ex) {
      fail("File reading/writing caused exception: " + ex);
    }
  }
}
