/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
import java.util.Scanner;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.permutations.Permutation;

/**
 * This class provides a representation of, and means of generating, instances of single machine
 * scheduling problems involving weights, due dates, and sequence-dependent setup times, but without
 * release dates (i.e., all jobs are released at the start of the problem at time 0, thus, the term
 * "static" in the class name).
 *
 * <p>This class generates instances using a procedure largely based upon that which was used to
 * generate the benchmark instances for weighted tardiness scheduling with sequence-dependent setups
 * described in the following paper, and with instances now available at the following Harvard
 * Dataverse site:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. <a
 *       href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness
 *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report,
 *       Intelligent Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon
 *       University, Pittsburgh, PA, February 2003.
 *   <li>Vincent A. Cicirello. <a href="http://dx.doi.org/10.7910/DVN/VHA0VQ">Weighted Tardiness
 *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Harvard Dataverse,
 *       doi:10.7910/DVN/VHA0VQ, June 2016.
 * </ul>
 *
 * <p>That approach was based on the description of generating instances for that scheduling problem
 * described in the following paper:
 *
 * <ul>
 *   <li>Lee, Y. H., Bhaskaran, K., and Pinedo, M. A heuristic to minimize the total weighted
 *       tardiness with sequence-dependent setups. IIE Transactions, 29:45–52, 1997.
 * </ul>
 *
 * <p>The Chips-n-Salsa library separates the representation of the scheduling problem instance data
 * (e.g., processing times, setup times, etc) from the implementations of scheduling cost functions
 * deliberately to enable defining additional problems (i.e., different cost functions to optimize)
 * using the same scheduling problem generators. This is very much like what was suggested in the
 * following paper:
 *
 * <ul>
 *   <li>Vincent A. Cicirello. <a
 *       href="https://www.cicirello.org/publications/cicirello2007icaps.html">The Challenge of
 *       Sequence-Dependent Setups: Proposal for a Scheduling Competition Track on One Machine
 *       Sequencing Problems</a>. Proceedings of the International Conference on Automated Planning
 *       and Scheduling (ICAPS) Workshop on Scheduling a Scheduling Competition. AAAI Press,
 *       September 2007.
 * </ul>
 *
 * <p>This is a reimplementation of the above problem instance generator, using a more efficient
 * random number generator, and a better estimator of makespan (which is used in determining the
 * random due dates).
 *
 * <p>The constructors that generate random scheduling problem instances take four parameters: n,
 * &tau;, r, and &eta;. These parameters are defined as follows. The number of jobs in the instance
 * is n. &tau; controls due date tightness, and r controls the due date range. &eta; controls the
 * severity of the setup times. The values of &tau;, r, and &eta; are required to be in the interval
 * [0.0, 1.0].
 *
 * <p>Instances are then generated randomly as follows. The processing time p[j] of job j is
 * generated uniformly at random from: [50, 150]. The mean processing time is thus: p&#772; = 100.
 * The weight w[j] of job j is generated uniformly at random from: [0, 10]. To generate the random
 * setup times, first define the mean setup time as: s&#772; = &eta;p&#772;. The setup time s[i][j]
 * of job j if it immediately follows job i on the machine is generated uniformly at random from:
 * [0, 2s&#772;]. To generate the random due dates, first define the mean due date d&#772; as
 * d&#772; = (1 - &tau;)C<sub>max</sub>, where C<sub>max</sub> is an estimate of makespan (see
 * below). With probability &tau;, we generate the due date d[j] of job j uniformly at random from
 * the interval: [d&#772; - rd&#772;, d&#772;]; and with probability (1 - &tau;), we generate it
 * uniformly at random from [d&#772;, d&#772; + r(C<sub>max</sub> - d&#772;)]. The makespan,
 * C<sub>max</sub>, is estimated based on Lee et al's (see references above) estimate:
 * C<sub>max</sub> = n(p&#772; + &beta;s&#772;), where &beta; &le; 1.0. Lee et al's rationale for
 * including the &beta; factor is that the optimal schedule will tend to favor job transitions that
 * involve lower than average setups. Lee et al's paper provided data on how &beta; varies for a
 * couple specific problem sizes n. They don't derive a general rule, however, an examination of the
 * limited data in their paper shows that &beta; appears to decrease logarithmically in n. We fit a
 * curve to the couple data points available in Lee et al's paper and estimate: &beta; = -0.097
 * ln(n) + 0.6876, but we don't allow &beta; to fall below 0.2.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.13.2021
 */
public final class WeightedStaticSchedulingWithSetups
    implements SingleMachineSchedulingProblemData {

  /**
   * Defines the minimum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MIN_PROCESS_TIME = 50;

  /**
   * Defines the maximum process times. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int MAX_PROCESS_TIME = 150;

  /**
   * Defines the average process time. Process times are generated uniformly at random from the
   * interval: [MIN_PROCESS_TIME, MAX_PROCESS_TIME].
   */
  public static final int AVERAGE_PROCESS_TIME = (MAX_PROCESS_TIME + MIN_PROCESS_TIME) / 2;

  /**
   * Defines the minimum weight. Weights are generated uniformly at random from the interval:
   * [MIN_WEIGHT, MAX_WEIGHT]. This follows the instance generation described by Lee, et al, but has
   * since been criticized since it implies some jobs are excluded from some cost functions (e.g.,
   * weighted tardiness), although those jobs can still be critical to the schedule due to how setup
   * times are generated.
   */
  public static final int MIN_WEIGHT = 0;

  /**
   * Defines the maximum weight. Weights are generated uniformly at random from the interval:
   * [MIN_WEIGHT, MAX_WEIGHT].
   */
  public static final int MAX_WEIGHT = 10;

  private final int[] process;
  private final int[] duedates;
  private final int[] weights;
  private final int[][] setups;

  private static final int PROCESS_TIME_SPAN = MAX_PROCESS_TIME - MIN_PROCESS_TIME + 1;
  private static final int WEIGHT_SPAN = MAX_WEIGHT - MIN_WEIGHT + 1;

  /**
   * Generates random single machine scheduling problem instances.
   *
   * @param n The number of jobs in the instance, must be positive.
   * @param tau The due date tightness, in the interval [0.0, 1.0]. The higher the value of tau, the
   *     tighter are the due dates (e.g., if tau=1.0, then all due dates will be 0, i.e., at the
   *     start of the schedule).
   * @param r The due date range, in the interval [0.0, 1.0]. The higher the value of r, the wider
   *     the range of due dates. For example, if r=0, all due dates will be the same.
   * @param eta The setup time severity, in the interval [0.0, 1.0]. The higher the value of eta,
   *     the greater the impact of setup times on scheduling difficulty. For example, if eta=0, then
   *     all setup times are 0.
   * @param seed A seed for the random number generator, to enable easily generating the same
   *     problem instance. If all parameters, including the seed are the same, then the same
   *     instance will be generated.
   * @throws IllegalArgumentException if n is not positive, or if any of tau, eta, or r are not in
   *     the interval [0.0, 1.0].
   */
  public WeightedStaticSchedulingWithSetups(int n, double tau, double r, double eta, long seed) {
    this(n, tau, r, eta, new SplittableRandom(seed));
  }

  /**
   * Generates random single machine scheduling problem instances.
   *
   * @param n The number of jobs in the instance, must be positive.
   * @param tau The due date tightness, in the interval [0.0, 1.0]. The higher the value of tau, the
   *     tighter are the due dates (e.g., if tau=1.0, then all due dates will be 0, i.e., at the
   *     start of the schedule).
   * @param r The due date range, in the interval [0.0, 1.0]. The higher the value of r, the wider
   *     the range of due dates. For example, if r=0, all due dates will be the same.
   * @param eta The setup time severity, in the interval [0.0, 1.0]. The higher the value of eta,
   *     the greater the impact of setup times on scheduling difficulty. For example, if eta=0, then
   *     all setup times are 0.
   * @throws IllegalArgumentException if n is not positive, or if any of tau, eta, or r are not in
   *     the interval [0.0, 1.0].
   */
  public WeightedStaticSchedulingWithSetups(int n, double tau, double r, double eta) {
    this(n, tau, r, eta, new SplittableRandom());
  }

  /**
   * Constructs a single machine scheduling problem instance by parsing an instance data file that
   * follows the format specified in the following paper, with instances available at the following
   * link:
   *
   * <ul>
   *   <li>Vincent A. Cicirello. <a
   *       href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness
   *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report,
   *       Intelligent Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon
   *       University, Pittsburgh, PA, February 2003.
   *   <li>Vincent A. Cicirello. <a href="http://dx.doi.org/10.7910/DVN/VHA0VQ">Weighted Tardiness
   *       Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Harvard Dataverse,
   *       doi:10.7910/DVN/VHA0VQ, June 2016.
   * </ul>
   *
   * <p>Note that the paper above describes a weighted tardiness scheduling problem, but the
   * instance data (process and setup times, weights, duedates) can also be used with other
   * scheduling objective functions
   *
   * @param filename The name of the data instance file, with path.
   * @throws FileNotFoundException if the named file does not exist, is a directory rather than a
   *     regular file, or for some other reason cannot be opened for reading
   */
  public WeightedStaticSchedulingWithSetups(String filename) throws FileNotFoundException {
    this(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
  }

  private WeightedStaticSchedulingWithSetups(
      int n, double tau, double r, double eta, RandomGenerator rand) {
    if (n <= 0) throw new IllegalArgumentException("n must be positive");
    if (tau < 0.0 || tau > 1.0) throw new IllegalArgumentException("tau must be in [0.0, 1.0]");
    if (r < 0.0 || r > 1.0) throw new IllegalArgumentException("r must be in [0.0, 1.0]");
    if (eta < 0.0 || eta > 1.0) throw new IllegalArgumentException("eta must be in [0.0, 1.0]");

    process = new int[n];
    duedates = new int[n];
    weights = new int[n];
    setups = new int[n][n];

    final double AVERAGE_SETUP = AVERAGE_PROCESS_TIME * eta;
    final int SETUP_BOUND = (int) (2 * AVERAGE_SETUP) + 1;
    final double BETA = n < 153 ? -0.097 * Math.log(n) + 0.6876 : 0.2;

    int totalProcessTime = 0;
    int sumSetupMatrix = 0;

    for (int i = 0; i < n; i++) {
      process[i] = MIN_PROCESS_TIME + RandomIndexer.nextInt(PROCESS_TIME_SPAN, rand);
      totalProcessTime += process[i];
      weights[i] = MIN_WEIGHT + RandomIndexer.nextInt(WEIGHT_SPAN, rand);
      for (int j = 0; j < n; j++) {
        setups[j][i] = RandomIndexer.nextInt(SETUP_BOUND, rand);
        sumSetupMatrix += setups[j][i];
      }
    }

    final double CMAX = totalProcessTime + BETA * sumSetupMatrix / (n + 1.0);
    final double AVERAGE_DUEDATE = CMAX * (1.0 - tau);
    final double DUEDATE_RANGE = r * CMAX;
    final int AVE_DUEDATE_INT = (int) AVERAGE_DUEDATE;
    final int MIN_DUEDATE = (int) (AVERAGE_DUEDATE - r * AVERAGE_DUEDATE);
    final int MAX_DUEDATE = (int) (AVERAGE_DUEDATE - r * AVERAGE_DUEDATE + DUEDATE_RANGE);
    final int D_SPAN_1 = AVE_DUEDATE_INT - MIN_DUEDATE + 1;
    final int D_SPAN_2 = MAX_DUEDATE - AVE_DUEDATE_INT + 1;

    for (int i = 0; i < n; i++) {
      if (AVERAGE_DUEDATE > 0.0) {
        if (rand.nextDouble() < tau) {
          duedates[i] = MIN_DUEDATE + RandomIndexer.nextInt(D_SPAN_1, rand);
        } else {
          duedates[i] = AVE_DUEDATE_INT + RandomIndexer.nextInt(D_SPAN_2, rand);
        }
      }
    }
  }

  /*
   * Parser for benchmark scheduling instance data files that is described in:
   * Vincent A. Cicirello (2003). "Weighted Tardiness Scheduling with
   * Sequence-Dependent Setups: A Benchmark Library". Technical Report,
   * Intelligent Coordination and Logistics Laboratory, Robotics Institute,
   * Carnegie Mellon University, Pittsburgh, PA, February 2003.
   *
   * Some instance files can be found at: https://www.cicirello.org/datasets/wtsds/
   * And also at: http://dx.doi.org/10.7910/DVN/VHA0VQ
   *
   * package-private to ease unit testing
   */
  WeightedStaticSchedulingWithSetups(Readable file) {
    Scanner in = new Scanner(file);
    while (!in.next().equals("Size:"))
      ;
    final int n = in.nextInt();
    duedates = new int[n];
    weights = new int[n];
    process = new int[n];
    setups = new int[n][n];
    while (!in.nextLine().equals("Process Times:"))
      ;
    for (int i = 0; i < n; i++) {
      process[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    for (int i = 0; i < n; i++) {
      weights[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    for (int i = 0; i < n; i++) {
      duedates[i] = in.nextInt();
    }
    while (!in.hasNextInt()) in.next();
    while (in.hasNextInt()) {
      int i = in.nextInt();
      int j = in.nextInt();
      int setup = in.nextInt();
      if (i == -1) i = j;
      setups[i][j] = setup;
    }
    in.close();
  }

  @Override
  public int[] getCompletionTimes(Permutation schedule) {
    if (schedule.length() != process.length) {
      throw new IllegalArgumentException("schedule is incorrect length");
    }
    int[] c = new int[process.length];
    int last = schedule.get(0);
    int time = 0;
    for (int i = 0; i < c.length; i++) {
      int j = schedule.get(i);
      time += process[j] + setups[last][j];
      c[j] = time;
      last = j;
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

  @Override
  public int getSetupTime(int i, int j) {
    return setups[i][j];
  }

  @Override
  public int getSetupTime(int j) {
    return setups[j][j];
  }

  @Override
  public boolean hasSetupTimes() {
    return true;
  }

  /**
   * Outputs a description of the instance data in the format based on that described in:
   *
   * <p>Vincent A. Cicirello. <a
   * href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness
   * Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report,
   * Intelligent Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon
   * University, Pittsburgh, PA, February 2003.
   *
   * <p>The data as output by this method varies from that format in that it does not output the
   * "Generator Parameters" section. Instead, it has the "Begin Generator Parameters" and the "End
   * Generator Parameters" block markers, but an empty block. The constructor of this class that
   * takes an instance data file as input can correctly parse both the original and this modified
   * format.
   *
   * <p>This method will output a -1 for the problem instance number. If you wish to have meaningful
   * problem instance numbers, use the version of the method that includes a parameter for this.
   *
   * @param filename The name of a file for the output.
   * @throws FileNotFoundException If the given string does not denote an existing, writable regular
   *     file and a new regular file of that name cannot be created, or if some other error occurs
   *     while opening or creating the file
   */
  public void toFile(String filename) throws FileNotFoundException {
    toFile(filename, -1);
  }

  /**
   * Outputs a description of the instance data in the format based on that described in:
   *
   * <p>Vincent A. Cicirello. <a
   * href="https://www.cicirello.org/publications/cicirello2003cmu.html">Weighted Tardiness
   * Scheduling with Sequence-Dependent Setups: A Benchmark Library</a>. Technical Report,
   * Intelligent Coordination and Logistics Laboratory, Robotics Institute, Carnegie Mellon
   * University, Pittsburgh, PA, February 2003.
   *
   * <p>The data as output by this method varies from that format in that it does not output the
   * "Generator Parameters" section. Instead, it has the "Begin Generator Parameters" and the "End
   * Generator Parameters" block markers, but an empty block. The constructor of this class that
   * takes an instance data file as input can correctly parse both the original and this modified
   * format.
   *
   * @param filename The name of a file for the output.
   * @param instanceNumber An id for the problem instance.
   * @throws FileNotFoundException If the given string does not denote an existing, writable regular
   *     file and a new regular file of that name cannot be created, or if some other error occurs
   *     while opening or creating the file
   */
  public void toFile(String filename, int instanceNumber) throws FileNotFoundException {
    PrintWriter out =
        new PrintWriter(
            new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8), true);
    toFile(out, instanceNumber);
    out.close();
  }

  /*
   * package-private to ease unit testing
   */
  void toFile(PrintWriter out, int instanceNumber) {
    out.print("Problem Instance: ");
    out.println(instanceNumber);
    out.print("Problem Size: ");
    out.println(numberOfJobs());
    out.println("Begin Generator Parameters");
    out.println("End Generator Parameters");
    out.println("Begin Problem Specification");
    out.println("Process Times:");
    for (int p : process) {
      out.println(p);
    }
    out.println("Weights:");
    for (int w : weights) {
      out.println(w);
    }
    out.println("Duedates:");
    for (int d : duedates) {
      out.println(d);
    }
    out.println("Setup Times:");
    for (int i = 0; i < setups.length; i++) {
      for (int j = 0; j < setups[i].length; j++) {
        if (i == j) {
          out.printf("%d %d %d\n", -1, j, setups[i][j]);
        } else {
          out.printf("%d %d %d\n", i, j, setups[i][j]);
        }
      }
    }
    out.println("End Problem Specification");
  }
}
