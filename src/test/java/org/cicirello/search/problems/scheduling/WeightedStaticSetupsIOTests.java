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
import java.util.Scanner;
import org.junit.jupiter.api.*;

/**
 * JUnit tests for WeightedStaticSchedulingWithSetupsReader and
 * WeightedStaticSchedulingWithSetupsWriter.
 */
public class WeightedStaticSetupsIOTests {

  @BeforeAll
  public static void createOutputDirectory() {
    File directory = new File("target/testcasedata");
    if (!directory.exists()) {
      directory.mkdir();
    }
  }

  @Test
  public void testReadWriteInstanceData() {
    double[] tau = {0.0, 0.5, 1.0};
    double[] r = {0.0, 0.5, 1.0};
    double[] eta = {0.0, 0.5, 1.0};
    int instance = 0;
    for (int n = 1; n <= 5; n++) {
      for (int i = 0; i < tau.length; i++) {
        for (int j = 0; j < r.length; j++) {
          for (int k = 0; k < eta.length; k++) {
            WeightedStaticSchedulingWithSetups s =
                new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
            WeightedStaticSchedulingWithSetupsWriter instanceWriter =
                new WeightedStaticSchedulingWithSetupsWriter(s);
            StringWriter sOut = new StringWriter();
            PrintWriter out = new PrintWriter(sOut);
            instanceWriter.toFile(out, instance);
            WeightedStaticSchedulingWithSetupsReader instanceReader =
                new WeightedStaticSchedulingWithSetupsReader(new StringReader(sOut.toString()));
            int[] process = instanceReader.process();
            int[] duedates = instanceReader.duedates();
            int[] weights = instanceReader.weights();
            int[][] setups = instanceReader.setups();
            assertEquals(s.numberOfJobs(), process.length);
            assertEquals(s.numberOfJobs(), weights.length);
            assertEquals(s.numberOfJobs(), duedates.length);
            assertEquals(s.numberOfJobs(), setups.length);

            for (int job = 0; job < n; job++) {
              assertEquals(s.numberOfJobs(), setups[job].length);
              assertEquals(s.getProcessingTime(job), process[job]);
              assertEquals(s.getDueDate(job), duedates[job]);
              assertEquals(s.getWeight(job), weights[job]);
              assertEquals(s.getSetupTime(job), setups[job][job]);
              for (int job2 = 0; job2 < n; job2++) {
                assertEquals(s.getSetupTime(job, job2), setups[job][job2]);
              }
            }
            Scanner scan = new Scanner(sOut.toString());
            String line = scan.nextLine();
            Scanner lineScanner = new Scanner(line);
            assertEquals("Problem", lineScanner.next());
            assertEquals("Instance:", lineScanner.next());
            assertEquals(instance, lineScanner.nextInt(), "Instance number");
            lineScanner.close();
            line = scan.nextLine();
            lineScanner = new Scanner(line);
            assertEquals("Problem", lineScanner.next());
            assertEquals("Size:", lineScanner.next());
            assertEquals(n, lineScanner.nextInt(), "Number of jobs");
            lineScanner.close();
            assertEquals("Begin Generator Parameters", scan.nextLine());
            assertEquals("End Generator Parameters", scan.nextLine());
            assertEquals("Begin Problem Specification", scan.nextLine());
            assertEquals("Process Times:", scan.nextLine());
            for (int x = 0; x < n; x++) scan.nextLine();
            assertEquals("Weights:", scan.nextLine());
            for (int x = 0; x < n; x++) scan.nextLine();
            assertEquals("Duedates:", scan.nextLine());
            for (int x = 0; x < n; x++) scan.nextLine();
            assertEquals("Setup Times:", scan.nextLine());
            int n2 = n * n;
            for (int x = 0; x < n2; x++) scan.nextLine();
            assertEquals("End Problem Specification", scan.nextLine());
            scan.close();
            instance++;
          }
        }
      }
    }
  }

  @Test
  public void testReadWriteToFile() {
    WeightedStaticSchedulingWithSetups original =
        new WeightedStaticSchedulingWithSetups(7, 0.5, 0.5, 0.5, 42);
    try {
      String file = "target/testcasedata/wss.testcase.data";
      original.toFile(file);
      WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(file);
      assertEquals(7, s.numberOfJobs());
      for (int job = 0; job < 7; job++) {
        assertEquals(original.getProcessingTime(job), s.getProcessingTime(job));
        assertEquals(original.getWeight(job), s.getWeight(job));
        assertEquals(original.getDueDate(job), s.getDueDate(job));
        assertEquals(original.getSetupTime(job), s.getSetupTime(job));
        for (int from = 0; from < 7; from++) {
          if (from != job) {
            assertEquals(original.getSetupTime(from, job), s.getSetupTime(from, job));
          }
        }
      }
    } catch (FileNotFoundException ex) {
      fail("File reading/writing caused exception: " + ex);
    }
  }
}
