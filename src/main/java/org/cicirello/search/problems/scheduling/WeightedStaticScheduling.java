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
 * This class provides a representation of, and means of generating, instances of single machine
 * scheduling problems involving weights and due dates, but without release dates (i.e., all jobs
 * are released at the start of the problem at time 0, thus, the term "static" in the class name).
 *
 * <p>This class generates instances using a procedure based on that used to generate the benchmark
 * instances for weighted tardiness scheduling that are available in the <a
 * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/wtinfo.html>OR-Library of J.E. Beasley</a>.
 * Note that this is NOT the implementation that generated those instances. Rather, this
 * implementation is based on the description of that generator. That <a
 * href=https://github.com/cicirello/scheduling-benchmarks/tree/master/wt>description</a>, along
 * with a set of benchmark instances, is mirrored in the following GitHub repository: <a
 * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
 *
 * <p>The Chips-n-Salsa library separates the representation of the scheduling problem instance data
 * (e.g., processing times, weights, etc) from the implementations of scheduling cost functions
 * deliberately to enable defining additional problems (i.e., different cost functions to optimize)
 * using the same scheduling problem generators. This is very much like what was suggested in the
 * following paper:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. <a
 *       href=https://www.cicirello.org/publications/cicirello2007icaps.html>The Challenge of
 *       Sequence-Dependent Setups: Proposal for a Scheduling Competition Track on One Machine
 *       Sequencing Problems</a>. Proceedings of the International Conference on Automated Planning
 *       and Scheduling (ICAPS) Workshop on Scheduling a Scheduling Competition. AAAI Press,
 *       September 2007.
 * </ul>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class WeightedStaticScheduling implements SingleMachineSchedulingProblemData {

  /**
   * Defines the minimum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MIN_PROCESS_TIME = 1;

  /**
   * Defines the maximum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MAX_PROCESS_TIME = 100;

  /**
   * Defines the minimum weight. Weights are generated uniformly at random from the interval:
   * [MIN_WEIGHT, MAX_WEIGHT].
   */
  public static final int MIN_WEIGHT = 1;

  /**
   * Defines the maximum weight. Weights are generated uniformly at random from the interval:
   * [MIN_WEIGHT, MAX_WEIGHT].
   */
  public static final int MAX_WEIGHT = 10;

  private final int[] process;
  private final int[] duedates;
  private final int[] weights;

  private final int PROCESS_TIME_SPAN = MAX_PROCESS_TIME - MIN_PROCESS_TIME + 1;
  private final int WEIGHT_SPAN = MAX_WEIGHT - MIN_WEIGHT + 1;

  /**
   * Generates random single machine scheduling problem instances.
   *
   * @param n The number of jobs in the instance, must be positive.
   * @param rdd The relative range of duedates. See the links in the class comments for an
   *     explanation of this parameter. Must be in the interval (0.0, 1.0].
   * @param tf The tardiness factor. See the links in the class comments for an explanation of this
   *     parameter. Must be in the interval [0.0, 1.0].
   * @param seed A seed for the random number generator, to enable easily generating the same
   *     problem instance. If all parameters, including the seed are the same, then the same
   *     instance will be generated.
   * @throws IllegalArgumentException if n is not positive, or rdd &le; 0 or rdd &gt; 1 or tf &lt; 0
   *     or tf &gt; 0.
   */
  public WeightedStaticScheduling(int n, double rdd, double tf, long seed) {
    this(n, rdd, tf, new SplittableRandom(seed));
  }

  /**
   * Generates random single machine scheduling problem instances.
   *
   * @param n The number of jobs in the instance, must be positive.
   * @param rdd The relative range of duedates. See the links in the class comments for an
   *     explanation of this parameter. Must be in the interval (0.0, 1.0].
   * @param tf The tardiness factor. See the links in the class comments for an explanation of this
   *     parameter. Must be in the interval [0.0, 1.0].
   * @throws IllegalArgumentException if n is not positive, or rdd &le; 0 or rdd &gt; 1 or tf &lt; 0
   *     or tf &gt; 0.
   */
  public WeightedStaticScheduling(int n, double rdd, double tf) {
    this(n, rdd, tf, new SplittableRandom());
  }

  /**
   * Constructs a single machine scheduling problem instance by parsing an instance data file that
   * follows the format specified in the <a
   * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/wtinfo.html>OR-Library of J.E. Beasley</a>.
   * The <a href=https://github.com/cicirello/scheduling-benchmarks/tree/master/wt>description</a>,
   * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
   * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
   *
   * <p>The format from the benchmark library is a bit unusual. Each file contains many instances.
   * There is no labeling info, so you need to know the number of jobs, n, contained in the file.
   * The first instance is listed first with n process times, followed by n weights, followed by n
   * duedates. This is then followed by second instance (n process times, n weights, and then n
   * duedates), etc, with no instance separators.
   *
   * @param filename The name of the file containing the instances, with path.
   * @param n The number of jobs in one instance. Behavior is undefined if n is inconsistent with
   *     the actual number of jobs of the instances contained in the file.
   * @param instanceNumber The number of the instance to parse, where the first instance is instance
   *     0. Behavior is undefined if instanceNumber is too high.
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a
   *     regular file, or for some other reason cannot be opened for reading
   */
  public WeightedStaticScheduling(String filename, int n, int instanceNumber)
      throws FileNotFoundException {
    WeightedStaticSchedulingReader instanceReader =
        new WeightedStaticSchedulingReader(
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8),
            n,
            instanceNumber);
    this.process = instanceReader.process();
    this.duedates = instanceReader.duedates();
    this.weights = instanceReader.weights();
  }

  private WeightedStaticScheduling(int n, double rdd, double tf, RandomGenerator rand) {
    if (n <= 0) throw new IllegalArgumentException("n must be positive");
    if (rdd <= 0.0 || rdd > 1.0) throw new IllegalArgumentException("rdd must be in (0.0, 1.0]");
    if (tf < 0.0 || tf > 1.0) throw new IllegalArgumentException("tf must be in [0.0, 1.0]");

    process = new int[n];
    duedates = new int[n];
    weights = new int[n];

    int totalProcessTime = 0;
    for (int i = 0; i < n; i++) {
      process[i] = MIN_PROCESS_TIME + RandomIndexer.nextInt(PROCESS_TIME_SPAN, rand);
      totalProcessTime += process[i];
      weights[i] = MIN_WEIGHT + RandomIndexer.nextInt(WEIGHT_SPAN, rand);
    }
    final double D_LOWER_BOUND = totalProcessTime * (1.0 - tf - rdd / 2.0);
    final double D_UPPER_BOUND = totalProcessTime * (1.0 - tf + rdd / 2.0);
    for (int i = 0; i < n; i++) {
      int d = (int) Math.round(rand.nextDouble(D_LOWER_BOUND, D_UPPER_BOUND));
      duedates[i] = d < 0 ? 0 : d;
    }
  }

  @Override
  public int[] getCompletionTimes(Permutation schedule) {
    if (schedule.length() != process.length) {
      throw new IllegalArgumentException("schedule is incorrect length");
    }
    int[] c = new int[process.length];
    int time = 0;
    for (int i = 0; i < c.length; i++) {
      int j = schedule.get(i);
      time += process[j];
      c[j] = time;
    }
    return c;
  }

  @Override
  public int numberOfJobs() {
    return weights.length;
  }

  @Override
  public int getProcessingTime(int j) {
    return process[j];
  }

  @Override
  public int getDueDate(int j) {
    return duedates[j];
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

  /**
   * Outputs a description of the instance data in the format described by the <a
   * href=http://people.brunel.ac.uk/~mastjjb/jeb/orlib/wtinfo.html>OR-Library of J.E. Beasley</a>.
   * The <a href=https://github.com/cicirello/scheduling-benchmarks/tree/master/wt>description</a>,
   * along with a set of benchmark instances, is mirrored in the following GitHub repository: <a
   * href=https://github.com/cicirello/scheduling-benchmarks>https://github.com/cicirello/scheduling-benchmarks</a>
   *
   * <p>The only different with that format is that this stores only the one instance in the file.
   * But for consistency with the original format, you do need to know the number of jobs for the
   * instance (or you can determine this by counting number of integers in the file and dividing by
   * 3.
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
    WeightedStaticSchedulingWriter instanceWriter = new WeightedStaticSchedulingWriter(this);
    instanceWriter.toFile(out);
    out.close();
  }
}
