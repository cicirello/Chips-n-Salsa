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

package org.cicirello.search.problems.tsp;

import org.cicirello.math.rand.EnhancedRandomGenerator;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;

/**
 * This class and its nested classes implement the Traveling Salesperson Problem (TSP), such that
 * cities are 2D points, and edge costs is the distance between them. The default distance is
 * Euclidean distance, but any distance function can be configured by implementing the {@link
 * TSPEdgeDistance} interface. The TSP class provides two inner classes where edge weights are
 * computed as needed, one for edge costs that are floating-point valued (class {@link Double}), and
 * one for integer cost edges (class {@link Integer}); and it also provides two inner classes where
 * the edge weights are precomputed, one for edge costs that are floating-point valued (class {@link
 * DoubleMatrix}), and one for integer cost edges (class {@link IntegerMatrix}). The nested classes
 * with precomputed edge weights ({@link DoubleMatrix} and {@link IntegerMatrix}) may lead to faster
 * runtimes for most search algorithms, but uses quadratic memory which may be prohibitive for TSP
 * instances with a very large number of cities. Whereas the other two classes that compute each
 * edge weight each time it is needed ({@link Double} and {@link Integer}), may be slower (by a
 * constant factor) but only use linear memory, and are thus applicable to much larger TSP
 * instances.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public abstract class TSP extends BaseTSP {

  /* These are package-private to provide access to the static inner classes. */
  final double[] x;
  final double[] y;
  final TSPEdgeDistance d;

  /* package-private constructor */
  TSP(int n, double w, EnhancedRandomGenerator gen) {
    this(n, w, new EuclideanDistance(), gen);
  }

  /* package-private constructor */
  TSP(int n, double w, TSPEdgeDistance distance, EnhancedRandomGenerator gen) {
    if (n < 2) {
      throw new IllegalArgumentException("Must be at least 2 cities.");
    }
    if (w <= 0.0) {
      throw new IllegalArgumentException("Width of region must be positive.");
    }
    x = new double[n];
    y = new double[n];
    for (int i = 0; i < n; i++) {
      x[i] = gen.nextDouble(w);
      y[i] = gen.nextDouble(w);
    }
    d = distance;
  }

  /* package-private constructor */
  TSP(double[] x, double[] y) {
    this(x, y, new EuclideanDistance());
  }

  /* package-private constructor */
  TSP(double[] x, double[] y, TSPEdgeDistance distance) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("Arrays must be same length.");
    }
    if (x.length < 2) {
      throw new IllegalArgumentException("Must be at least 2 cities.");
    }
    this.x = x.clone();
    this.y = y.clone();
    d = distance;
  }

  /**
   * Gets the number of cities in the TSP instance.
   *
   * @return number of cities
   */
  @Override
  public final int length() {
    return x.length;
  }

  /**
   * Gets the x coordinate of a city.
   *
   * @param i The city index.
   * @return the x coordinate of city i.
   * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
   */
  public final double getX(int i) {
    return x[i];
  }

  /**
   * Gets the y coordinate of a city.
   *
   * @param i The city index.
   * @return the y coordinate of city i.
   * @throws ArrayIndexOutOfBoundsException if i &lt; 0 or i &ge; length().
   */
  public final double getY(int i) {
    return y[i];
  }

  /**
   * Cost function for the Traveling Salesperson Problem (TSP), where edge costs are floating-point
   * valued. This implementation only requires linear memory, but must recompute an edge cost every
   * time it is needed. If your instance of the TSP is small enough to afford quadratic memory, then
   * you may prefer the {@link TSP.DoubleMatrix} class which precomputes all edge costs between all
   * pairs of cities.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class Double extends TSP implements OptimizationProblem<Permutation> {

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Double(int n, double w) {
      super(n, w, RandomnessFactory.createEnhancedSplittableGenerator());
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Double(int n, double w, TSPEdgeDistance distance) {
      super(n, w, distance, RandomnessFactory.createEnhancedSplittableGenerator());
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Double(int n, double w, long seed) {
      super(n, w, new EnhancedSplittableGenerator(seed));
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Double(int n, double w, TSPEdgeDistance distance, long seed) {
      super(n, w, distance, new EnhancedSplittableGenerator(seed));
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public Double(double[] x, double[] y) {
      super(x, y);
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public Double(double[] x, double[] y, TSPEdgeDistance distance) {
      super(x, y, distance);
    }

    @Override
    public double cost(Permutation candidate) {
      if (candidate.length() != x.length) {
        throw new IllegalArgumentException("Permutation must be same length as number of cities.");
      }
      int j = candidate.get(0);
      int i = candidate.get(candidate.length() - 1);
      double total = d.distance(x[i], y[i], x[j], y[j]);
      for (int k = 1; k < candidate.length(); k++) {
        j = candidate.get(k);
        i = candidate.get(k - 1);
        total = total + d.distance(x[i], y[i], x[j], y[j]);
      }
      return total;
    }

    @Override
    public double value(Permutation candidate) {
      return cost(candidate);
    }

    @Override
    public double minCost() {
      return 0;
    }

    /*
     * package private to support implementing heuristics in same package.
     */
    @Override
    final double edgeCostForHeuristics(int i, int j) {
      return d.distance(x[i], y[i], x[j], y[j]);
    }
  }

  /**
   * Cost function for the Traveling Salesperson Problem (TSP), where edge costs are integer valued.
   * This implementation only requires linear memory, but must recompute an edge cost every time it
   * is needed. If your instance of the TSP is small enough to afford quadratic memory, then you may
   * prefer the {@link TSP.IntegerMatrix} class which precomputes all edge costs between all pairs
   * of cities.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class Integer extends TSP
      implements IntegerCostOptimizationProblem<Permutation> {

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Integer(int n, double w) {
      super(n, w, RandomnessFactory.createEnhancedSplittableGenerator());
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Integer(int n, double w, TSPEdgeDistance distance) {
      super(n, w, distance, RandomnessFactory.createEnhancedSplittableGenerator());
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Integer(int n, double w, long seed) {
      super(n, w, new EnhancedSplittableGenerator(seed));
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public Integer(int n, double w, TSPEdgeDistance distance, long seed) {
      super(n, w, distance, new EnhancedSplittableGenerator(seed));
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public Integer(double[] x, double[] y) {
      super(x, y);
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public Integer(double[] x, double[] y, TSPEdgeDistance distance) {
      super(x, y, distance);
    }

    @Override
    public int cost(Permutation candidate) {
      if (candidate.length() != x.length) {
        throw new IllegalArgumentException("Permutation must be same length as number of cities.");
      }
      int j = candidate.get(0);
      int i = candidate.get(candidate.length() - 1);
      int total = d.distanceAsInt(x[i], y[i], x[j], y[j]);
      for (int k = 1; k < candidate.length(); k++) {
        j = candidate.get(k);
        i = candidate.get(k - 1);
        total = total + d.distanceAsInt(x[i], y[i], x[j], y[j]);
      }
      return total;
    }

    @Override
    public int value(Permutation candidate) {
      return cost(candidate);
    }

    @Override
    public int minCost() {
      return 0;
    }

    /*
     * package private to support implementing heuristics in same package.
     */
    @Override
    final double edgeCostForHeuristics(int i, int j) {
      // Although return type is double, must return the int edge cost version for this class.
      // This method only used by heuristics.
      return d.distanceAsInt(x[i], y[i], x[j], y[j]);
    }
  }

  /**
   * Cost function for the Traveling Salesperson Problem (TSP), where edge costs are floating-point
   * valued, and where all edge costs between pairs of cities are precomputed. This implementation
   * requires quadratic memory, and may be prohibitive for large instances, in which case you may
   * prefer to use the {@link TSP.Double} class, that only requires linear memory but recomputes an
   * edge cost every time it is needed.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class DoubleMatrix extends TSP implements OptimizationProblem<Permutation> {

    private final double[][] weights;

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public DoubleMatrix(int n, double w) {
      super(n, w, RandomnessFactory.createEnhancedSplittableGenerator());
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public DoubleMatrix(int n, double w, TSPEdgeDistance distance) {
      super(n, w, distance, RandomnessFactory.createEnhancedSplittableGenerator());
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public DoubleMatrix(int n, double w, long seed) {
      super(n, w, new EnhancedSplittableGenerator(seed));
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public DoubleMatrix(int n, double w, TSPEdgeDistance distance, long seed) {
      super(n, w, distance, new EnhancedSplittableGenerator(seed));
      weights = computeWeights();
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates. The
     * edge cost of a pair of cities is the Euclidean distance between them.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public DoubleMatrix(double[] x, double[] y) {
      super(x, y);
      weights = computeWeights();
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public DoubleMatrix(double[] x, double[] y, TSPEdgeDistance distance) {
      super(x, y, distance);
      weights = computeWeights();
    }

    @Override
    public double cost(Permutation candidate) {
      if (candidate.length() != x.length) {
        throw new IllegalArgumentException("Permutation must be same length as number of cities.");
      }
      double total = weights[candidate.get(candidate.length() - 1)][candidate.get(0)];
      for (int k = 1; k < candidate.length(); k++) {
        total = total + weights[candidate.get(k - 1)][candidate.get(k)];
      }
      return total;
    }

    @Override
    public double value(Permutation candidate) {
      return cost(candidate);
    }

    @Override
    public double minCost() {
      return 0;
    }

    private double[][] computeWeights() {
      double[][] w = new double[x.length][x.length];
      for (int i = 0; i < w.length; i++) {
        for (int j = i + 1; j < w.length; j++) {
          w[i][j] = w[j][i] = d.distance(x[i], y[i], x[j], y[j]);
        }
      }
      return w;
    }

    /*
     * package private to support implementing heuristics in same package.
     */
    @Override
    final double edgeCostForHeuristics(int i, int j) {
      return weights[i][j];
    }
  }

  /**
   * Cost function for the Traveling Salesperson Problem (TSP), where edge costs are integer valued,
   * and where all edge costs between pairs of cities are precomputed. This implementation requires
   * quadratic memory, and may be prohibitive for large instances, in which case you may prefer to
   * use the {@link TSP.Integer} class, that only requires linear memory but recomputes an edge cost
   * every time it is needed.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  public static final class IntegerMatrix extends TSP
      implements IntegerCostOptimizationProblem<Permutation> {

    private final int[][] weights;

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public IntegerMatrix(int n, double w) {
      super(n, w, RandomnessFactory.createEnhancedSplittableGenerator());
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public IntegerMatrix(int n, double w, TSPEdgeDistance distance) {
      super(n, w, distance, RandomnessFactory.createEnhancedSplittableGenerator());
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public IntegerMatrix(int n, double w, long seed) {
      super(n, w, new EnhancedSplittableGenerator(seed));
      weights = computeWeights();
    }

    /**
     * Constructs a random TSP instance with cities randomly distributed within a square region.
     *
     * @param n The number of cities.
     * @param w The width (and height) of a square region containing the cities.
     * @param distance The distance function to use for the edge costs.
     * @param seed The seed for the random number generator to enable reproducing the same instance
     *     for experiment reproducibility.
     * @throws IllegalArgumentException if n &lt; 2.
     * @throws IllegalArgumentException if w &#x2264; 0.0.
     */
    public IntegerMatrix(int n, double w, TSPEdgeDistance distance, long seed) {
      super(n, w, distance, new EnhancedSplittableGenerator(seed));
      weights = computeWeights();
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates. The
     * edge cost of a pair of cities is the Euclidean distance between them rounded to the nearest
     * integer.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public IntegerMatrix(double[] x, double[] y) {
      super(x, y);
      weights = computeWeights();
    }

    /**
     * Constructs a TSP instance with city locations specified by arrays of x and y coordinates.
     *
     * @param x Array of x coordinates.
     * @param y Array of y coordinates.
     * @param distance The distance function to use for the edge costs.
     * @throws IllegalArgumentException if x.length is not equal to y.length.
     * @throws IllegalArgumentException if the length of the arrays is less than 2.
     */
    public IntegerMatrix(double[] x, double[] y, TSPEdgeDistance distance) {
      super(x, y, distance);
      weights = computeWeights();
    }

    @Override
    public int cost(Permutation candidate) {
      if (candidate.length() != x.length) {
        throw new IllegalArgumentException("Permutation must be same length as number of cities.");
      }
      int total = weights[candidate.get(candidate.length() - 1)][candidate.get(0)];
      for (int k = 1; k < candidate.length(); k++) {
        total = total + weights[candidate.get(k - 1)][candidate.get(k)];
      }
      return total;
    }

    @Override
    public int value(Permutation candidate) {
      return cost(candidate);
    }

    @Override
    public int minCost() {
      return 0;
    }

    private int[][] computeWeights() {
      int[][] w = new int[x.length][x.length];
      for (int i = 0; i < w.length; i++) {
        for (int j = i + 1; j < w.length; j++) {
          w[i][j] = w[j][i] = d.distanceAsInt(x[i], y[i], x[j], y[j]);
        }
      }
      return w;
    }

    /*
     * package private to support implementing heuristics in same package.
     */
    @Override
    final double edgeCostForHeuristics(int i, int j) {
      return weights[i][j];
    }
  }
}
