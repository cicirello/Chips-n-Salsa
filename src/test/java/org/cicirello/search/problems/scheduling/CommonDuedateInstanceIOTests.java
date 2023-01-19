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

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.*;

/** JUnit tests for CommoDuedateInstanceReader. */
public class CommonDuedateInstanceIOTests {

  /*
  	@BeforeAll
    public static void createOutputDirectory() {
      File directory = new File("target/testcasedata");
      if (!directory.exists()) {
        directory.mkdir();
      }
    }
  */

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
        StringWriter sOut = new StringWriter();
        PrintWriter out = new PrintWriter(sOut);
        s.toFile(out);
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

  /*
  @Test
  public void testReadWriteToFile() {
    String contents = "1\n3\n1\t2\t3\n4\t5\t6\n7\t8\t9\n";
    CommonDuedateScheduling original =
        new CommonDuedateScheduling(new StringReader(contents), 0, 0.5);
    try {
      String file = "target/testcasedata/cdd.testcase.data";
      original.toFile(file);
      CommonDuedateScheduling s = new CommonDuedateScheduling(file, 0, 0.5);
      assertEquals(3, s.numberOfJobs());
      int duedate = s.getDueDate(0);
      assertEquals(6, duedate);
      for (int job = 0; job < 3; job++) {
        assertEquals(3 * job + 1, s.getProcessingTime(job));
        assertEquals(3 * job + 2, s.getEarlyWeight(job));
        assertEquals(3 * job + 3, s.getWeight(job));
        assertEquals(duedate, s.getDueDate(job));
      }
    } catch (FileNotFoundException ex) {
      fail("File reading/writing caused exception: " + ex);
    }
  }*/

}
