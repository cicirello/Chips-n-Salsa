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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.permutations.Permutation;

/**
 * This class represents and generates instances of a common duedate scheduling problem, in which
 * all jobs have both an earliness weight and a tardiness weight, and share a common duedate. The
 * library treats scheduling cost functions independently from the job data, however, the only
 * commonly used cost function that really makes sense with this combination of job characteristics
 * is the sum of weighted earliness plus weighted tardiness, implemented in the class {@link
 * WeightedEarlinessTardiness}. Consult the documentation of that class for details. There is
 * nothing to prevent you, however, from defining a scheduling problem involving the
 * CommonDuedateScheduling job characteristics with a different scheduling cost function. However,
 * be aware that the {@link #getCompletionTimes} method is implemented to assume the {@link
 * WeightedEarlinessTardiness} cost function, in that it computes the optimal delay to the start of
 * the schedule relative to the permutation it is passed as a parameter. That delay is likely
 * inappropriate for other cost functions.
 *
 * <p>This class generates instances using a procedure based on that used to generate the benchmark
 * instances for common duedate scheduling that are available in the <a
 * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/schinfo.html>OR-Library of J.E. Beasley</a>.
 * Note that this is NOT the implementation that generated those instances. Rather, this
 * implementation is based on the description of that generator. That <a
 * href=https://github.com/cicirello/scheduling-benchmarks/tree/master/common-due-date>description</a>,
 * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
 * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class CommonDuedateScheduling implements SingleMachineSchedulingProblemData {

  /**
   * Defines the minimum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MIN_PROCESS_TIME = 1;

  /**
   * Defines the maximum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MAX_PROCESS_TIME = 20;

  /**
   * Defines the minimum earliness weight. Weights are generated uniformly at random from the
   * interval: [MIN_EARLINESS_WEIGHT, MAX_EARLINESS_WEIGHT].
   */
  public static final int MIN_EARLINESS_WEIGHT = 1;

  /**
   * Defines the maximum earliness weight. Weights are generated uniformly at random from the
   * interval: [MIN_EARLINESS_WEIGHT, MAX_EARLINESS_WEIGHT].
   */
  public static final int MAX_EARLINESS_WEIGHT = 10;

  /**
   * Defines the minimum tardiness weight. Weights are generated uniformly at random from the
   * interval: [MIN_TARDINESS_WEIGHT, MAX_TARDINESS_WEIGHT].
   */
  public static final int MIN_TARDINESS_WEIGHT = 1;

  /**
   * Defines the maximum tardiness weight. Weights are generated uniformly at random from the
   * interval: [MIN_TARDINESS_WEIGHT, MAX_TARDINESS_WEIGHT].
   */
  public static final int MAX_TARDINESS_WEIGHT = 15;

  private final int[] process;
  private final int[] earlyWeights;
  private final int[] weights;
  private final int duedate;

  /**
   * Constructs a random instance of common duedate scheduling using an implementation of a process
   * based on that approach used to generate the instances from the OR-Library. The process times,
   * earliness weights, and tardiness weights are generated uniformly at random in the intervals
   * defined by the class constants. The common duedate is determined based on the parameter h.
   * Specifically, the common duedate for the instance is set to: floor(h * SumOfP), where SumOfP is
   * the sum of the processing times of the jobs.
   *
   * @param n The number of jobs for the instance, n &ge; 0.
   * @param h Controls the tightness of the common duedate for the instance, as a percentage of the
   *     sum of process times, 0.0 &le; h &le; 1.0.
   * @throws IllegalArgumentException if n &lt; 0 or h &lt; 0 or h &gt; 1
   */
  public CommonDuedateScheduling(int n, double h) {
    this(n, h, new SplittableRandom());
  }

  /**
   * Constructs a random instance of common duedate scheduling using an implementation of a process
   * based on that approach used to generate the instances from the OR-Library. The process times,
   * earliness weights, and tardiness weights are generated uniformly at random in the intervals
   * defined by the class constants. The common duedate is determined based on the parameter h.
   * Specifically, the common duedate for the instance is set to: floor(h * SumOfP), where SumOfP is
   * the sum of the processing times of the jobs.
   *
   * @param n The number of jobs for the instance, n &ge; 0.
   * @param h Controls the tightness of the common duedate for the instance, as a percentage of the
   *     sum of process times, 0.0 &le; h &le; 1.0.
   * @param seed The seed for the random number generator. Specifying a seed enables generating the
   *     same instance (e.g., same combination of n, h, and seed will lead to the same problem
   *     instance).
   * @throws IllegalArgumentException if n &lt; 0 or h &lt; 0 or h &gt; 1
   */
  public CommonDuedateScheduling(int n, double h, long seed) {
    this(n, h, new SplittableRandom(seed));
  }

  /**
   * Constructs a common duedate scheduling problem instance by parsing an instance data file that
   * follows the format specified in the <a
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
   * whitespace. Don't assume any specific number of whitespace characters. This seems to vary.
   * Lines may also begin with whitespace.
   *
   * <p>The h parameter (see documentation of constructors that generate instances) is not specified
   * in the file, and each instance in a file can be used to specify multiple benchmark instances
   * with varying degrees of duedate tightness. The instances in the OR-Library assume values of h
   * equal to 0.2, 0.4, 0.6, and 0.8 the OR-Library provides bounds on optimal solutions for those
   * values of h), but you can potentially define additional instances using additional values of h.
   * The only constraint on h is: 0.0 &le; h &le; 1.0. It is used to define the common duedate for
   * the instance as a percentage of the sum of process times.
   *
   * @param filename The name of the file containing the instances, with path.
   * @param instanceNumber The number of the instance to parse, where the first instance is instance
   *     0. The instanceNumber must be less than the number of instances indicated by the first line
   *     of the file.
   * @param h Controls the tightness of the common duedate for the instance, as a percentage of the
   *     sum of process times, 0.0 &le; h &le; 1.0.
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a
   *     regular file, or for some other reason cannot be opened for reading.
   * @throws IllegalArgumentException if instanceNumber is negative or greater than or equal to the
   *     number of instances in the file.
   * @throws IllegalArgumentException if h &lt; 0 or h &gt; 1
   */
  public CommonDuedateScheduling(String filename, int instanceNumber, double h)
      throws FileNotFoundException {
    CommonDuedateInstanceReader instanceReader =
        new CommonDuedateInstanceReader(
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8),
            instanceNumber,
            h);
    process = instanceReader.processTimes();
    earlyWeights = instanceReader.earlyWeights();
    weights = instanceReader.weights();
    duedate = instanceReader.duedate();
  }

  /*
   * Generates a random instance of common duedate scheduling according to
   * the description of the the instance generator used to generate the instances
   * in the OR-Library.
   */
  private CommonDuedateScheduling(int n, double h, RandomGenerator generator) {
    if (n < 0) throw new IllegalArgumentException("n must be nonnegative");
    if (h < 0 || h > 1) throw new IllegalArgumentException("h must be in [0.0, 1.0]");
    process = new int[n];
    earlyWeights = new int[n];
    weights = new int[n];
    final int P_RANGE = MAX_PROCESS_TIME - MIN_PROCESS_TIME + 1;
    final int E_RANGE = MAX_EARLINESS_WEIGHT - MIN_EARLINESS_WEIGHT + 1;
    final int T_RANGE = MAX_TARDINESS_WEIGHT - MIN_TARDINESS_WEIGHT + 1;
    int totalP = 0;
    for (int i = 0; i < n; i++) {
      process[i] = MIN_PROCESS_TIME + RandomIndexer.nextInt(P_RANGE, generator);
      totalP += process[i];
      earlyWeights[i] = MIN_EARLINESS_WEIGHT + RandomIndexer.nextInt(E_RANGE, generator);
      weights[i] = MIN_TARDINESS_WEIGHT + RandomIndexer.nextInt(T_RANGE, generator);
    }
    duedate = (int) (totalP * h);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation assumes that the cost function for the problem is sum of weighted
   * earliness and weighted tardiness (reasonable assumption since instances have both early and
   * tardy weights, and this is the cost function of the generator this is based upon). The reason
   * this assumption is important is that when computing completion times, this implementation does
   * not necessarily start the first job in the permutation at time 0. Instead, it delays the start
   * of the schedule to minimize the cost function weighted earliness plus weighted tardiness. This
   * delay is easy to compute for any fixed ordering of the jobs, such as specified by a given
   * permutation.
   */
  @Override
  public int[] getCompletionTimes(Permutation schedule) {
    if (schedule.length() != process.length) {
      throw new IllegalArgumentException("schedule is incorrect length");
    }
    int[] c = new int[process.length];
    int time = 0;
    int earlyTotal = 0;
    int tardyTotal = 0;
    int firstTardy = -1;
    int lastEarly = -1;
    for (int i = 0; i < c.length; i++) {
      int j = schedule.get(i);
      time += process[j];
      c[j] = time;
      if (time < duedate) {
        earlyTotal += earlyWeights[j];
        lastEarly = i;
      } else if (time > duedate) {
        tardyTotal += weights[j];
        if (firstTardy < 0) firstTardy = i;
      } // else { do nothing if time==duedate}
    }

    int delay = 0;

    // This was original condition. Code review revealed that first term impossible
    // as long as the common duedate is always computed based on h.  i.e., impossible
    // for all jobs to be early, even with h=1, which in that case will have exactly one
    // job precisely on time.
    // if (lastEarly == c.length-1 || (lastEarly == firstTardy - 1 && earlyTotal > tardyTotal)) {
    if (lastEarly == firstTardy - 1 && earlyTotal > tardyTotal) {
      int j = schedule.get(lastEarly);
      earlyTotal -= earlyWeights[j];
      lastEarly--;
      delay = duedate - c[j];
    }

    if (firstTardy - lastEarly > 1 || (lastEarly == c.length - 2 && firstTardy < 0)) {
      int notEarlyTotalOfTardy = tardyTotal + weights[schedule.get(lastEarly + 1)];
      while (lastEarly >= 0 && earlyTotal >= notEarlyTotalOfTardy) {
        int j = schedule.get(lastEarly);
        earlyTotal -= earlyWeights[j];
        notEarlyTotalOfTardy += weights[j];
        lastEarly--;
        delay = duedate - c[j];
      }
    }

    if (delay > 0) {
      for (int i = 0; i < c.length; i++) {
        c[i] += delay;
      }
    }

    return c;
  }

  @Override
  public int numberOfJobs() {
    return process.length;
  }

  @Override
  public int getProcessingTime(int j) {
    return process[j];
  }

  @Override
  public int getDueDate(int j) {
    return duedate;
  }

  @Override
  public boolean hasDueDates() {
    return true;
  }

  @Override
  public int getWeight(int j) {
    return weights[j];
  }

  @Override
  public boolean hasWeights() {
    return true;
  }

  @Override
  public int getEarlyWeight(int j) {
    return earlyWeights[j];
  }

  @Override
  public boolean hasEarlyWeights() {
    return true;
  }

  /**
   * Outputs a description of the instance in the format described by the <a
   * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/schinfo.html>OR-Library of J.E. Beasley</a>.
   * The <a
   * href=https://github.com/cicirello/scheduling-benchmarks/tree/master/common-due-date>description</a>,
   * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
   * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
   *
   * <p>The first line of the file that this produces has the number of instances in the file, which
   * will be equal to 1 since this only outputs a single instance. Although this may seem
   * unnecessary, it is included to enable using the same parser for the files from the OR-Library
   * as well as for those generated by this method. The second line indicates the number of jobs n.
   * This is then followed by n lines, one for each job, where the line consists of 3 integers:
   * process time, earliness weight, and tardiness weight. These are separated by whitespace.
   *
   * @param filename The name of a file for the output.
   * @throws FileNotFoundException If the given string does not denote an existing, writable regular
   *     file and a new regular file of that name cannot be created, or if some other error occurs
   *     while opening or creating the file
   */
  public void toFile(String filename) throws FileNotFoundException {
    PrintWriter out =
        new PrintWriter(
            new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8), true);
    toFile(out);
    out.close();
  }

  /*
   * package-private to ease unit testing
   */
  void toFile(PrintWriter out) {
    out.println(1);
    out.println(process.length);
    for (int i = 0; i < process.length; i++) {
      out.print(process[i]);
      out.print("\t");
      out.print(earlyWeights[i]);
      out.print("\t");
      out.println(weights[i]);
    }
  }
}
