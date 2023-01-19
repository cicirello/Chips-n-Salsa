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

/** JUnit tests for CommoDuedateInstanceReader and CommonDuedateInstanceWriter. */
public class CommonDuedateInstanceIOTests {

  @BeforeAll
  public static void createOutputDirectory() {
    File directory = new File("target/testcasedata");
    if (!directory.exists()) {
      directory.mkdir();
    }
  }

  @Test
  public void testExceptions() {

    String contents = "2\n3\n1\t2\t3\n1\t2\t3\n1\t2\t3\n4\n1\t1\t1\n2\t2\t2\n3\t3\t3\n4\t4\t4\n";
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new CommonDuedateInstanceReader(new StringReader(contents), 1, -0.000001));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new CommonDuedateInstanceReader(new StringReader(contents), 1, 1.000001));
  }

  @Test
  public void testReadSkippingInstance() {
    String contents = "2\n3\n1\t2\t3\n1\t2\t3\n1\t2\t3\n4\n1\t1\t1\n2\t2\t2\n3\t3\t3\n4\t4\t4\n";
    CommonDuedateInstanceReader instanceReader =
        new CommonDuedateInstanceReader(new StringReader(contents), 1, 0.5);
    int[] process = instanceReader.processTimes();
    int[] earlyWeights = instanceReader.earlyWeights();
    int[] weights = instanceReader.weights();
    int duedate = instanceReader.duedate();
    assertEquals(4, process.length);
    assertEquals(4, weights.length);
    assertEquals(4, earlyWeights.length);
    for (int job = 0; job < 4; job++) {
      assertEquals(job + 1, process[job]);
      assertEquals(job + 1, earlyWeights[job]);
      assertEquals(job + 1, weights[job]);
    }
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new CommonDuedateInstanceReader(new StringReader(contents), -1, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new CommonDuedateInstanceReader(new StringReader(contents), 2, 0.5));
  }

  @Test
  public void testReadWriteInstanceData() {
    double[] h = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int n = 1; n <= 5; n++) {
      for (int i = 0; i < h.length; i++) {
        CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
        CommonDuedateInstanceWriter instanceWriter = new CommonDuedateInstanceWriter(s);
        StringWriter sOut = new StringWriter();
        PrintWriter out = new PrintWriter(sOut);
        instanceWriter.toFile(out);
        CommonDuedateInstanceReader instanceReader =
            new CommonDuedateInstanceReader(new StringReader(sOut.toString()), 0, h[i]);
        int[] process = instanceReader.processTimes();
        int[] earlyWeights = instanceReader.earlyWeights();
        int[] weights = instanceReader.weights();
        int duedateFromReader = instanceReader.duedate();
        assertEquals(s.numberOfJobs(), process.length);
        assertEquals(s.numberOfJobs(), weights.length);
        assertEquals(s.numberOfJobs(), earlyWeights.length);

        int duedate = s.getDueDate(0);
        assertEquals(duedate, duedateFromReader);
        for (int job = 0; job < n; job++) {
          assertEquals(s.getProcessingTime(job), process[job]);
          assertEquals(s.getEarlyWeight(job), earlyWeights[job]);
          assertEquals(s.getWeight(job), weights[job]);
          assertEquals(s.getDueDate(job), duedateFromReader);
          assertEquals(duedate, duedateFromReader);
        }
      }
    }
  }

  @Test
  public void testReadWriteToFile() {
    CommonDuedateScheduling original = new CommonDuedateScheduling(7, 0.5, 42);
    try {
      String file = "target/testcasedata/cdd.testcase.data";
      original.toFile(file);
      CommonDuedateScheduling s = new CommonDuedateScheduling(file, 0, 0.5);
      assertEquals(7, s.numberOfJobs());
      int duedate = s.getDueDate(0);
      assertEquals(original.getDueDate(0), duedate);
      for (int job = 0; job < 7; job++) {
        assertEquals(original.getProcessingTime(job), s.getProcessingTime(job));
        assertEquals(original.getEarlyWeight(job), s.getEarlyWeight(job));
        assertEquals(original.getWeight(job), s.getWeight(job));
        assertEquals(original.getDueDate(job), s.getDueDate(job));
        assertEquals(duedate, s.getDueDate(job));
      }
    } catch (FileNotFoundException ex) {
      fail("File reading/writing caused exception: " + ex);
    }
  }
}
