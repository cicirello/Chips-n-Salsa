/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

import java.util.Scanner;
import java.util.function.IntBinaryOperator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;

/**
 * Parser for TSPLIB problem instances. Initially, it supports TSPLIB problem instances that are
 * instances of the symmetric traveling salesperson problem. Support for the other problem types
 * provided in the TSPLIB benchmark problems may be added later.
 *
 * <p>The file format is described here: <a
 * href="https://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/tsp/tsp95.pdf">TSPLIB file
 * specification</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class TSPLIB implements IntegerCostOptimizationProblem<Permutation> {

  private final IntBinaryOperator distance;
  private final String name;

  private TSPLIB(IntBinaryOperator distance, String name) {
    this.distance = distance;
    this.name = name;
  }

  @Override
  public int cost(Permutation candidate) {
    int tourCost = distance.applyAsInt(candidate.get(candidate.length() - 1), candidate.get(0));
    for (int i = 1; i < candidate.length(); i++) {
      tourCost += distance.applyAsInt(candidate.get(i - 1), candidate.get(i));
    }
    return tourCost;
  }

  @Override
  public int value(Permutation candidate) {
    return cost(candidate);
  }

  @Override
  public int minCost() {
    return 0;
  }

  /**
   * Gets the name of the instance.
   *
   * @return the instance name
   */
  public String instanceName() {
    return name;
  }

  /**
   * Parses a TSPLIB formatted file, creating the corresponding problem instance. The problem
   * instance created by this method calculates the edge weights every time required from the node
   * coordinates. This enables storing only the node coordinates in memory rather than an entire
   * matrix.
   *
   * @param instanceData a Readble containing the contents of a TSPLIB formatted problem instance
   * @return if instanceData contains an instance of a supported problem type, then this returns the
   *     TSPLIB problem instance. otherwise it returns null.
   */
  public static TSPLIB createMinimalMemory(Readable instanceData) {
    Scanner in = new Scanner(instanceData);
    Specification spec = parseSpecification(in);
    TSPLIB instance = spec.hasNodeCoords() && spec.type().equals("TSP") ? parseTSP(in, spec) : null;
    in.close();
    return instance;
  }

  /**
   * Parses a TSPLIB formatted file, creating the corresponding problem instance. The problem
   * instance created by this method pre-computes an edge weight matrix, requiring quadratic memory
   * in the number of cities. If memory constraints are tight, consider using {@link
   * #createMinimalMemory} instead.
   *
   * @param instanceData a Readble containing the contents of a TSPLIB formatted problem instance
   * @return if instanceData contains an instance of a supported problem type, then this returns the
   *     TSPLIB problem instance. otherwise it returns null.
   */
  public static TSPLIB createFullMatrix(Readable instanceData) {
    Scanner in = new Scanner(instanceData);
    Specification spec = parseSpecification(in);
    TSPLIB instance =
        spec.hasNodeCoords() && spec.type().equals("TSP") ? parseTSPMatrix(in, spec) : null;
    in.close();
    return instance;
  }

  /**
   * Parses a TSPLIB formatted file, creating the corresponding problem instance. The problem
   * instance created by this method pre-computes a triangular edge weight matrix, requiring
   * quadratic memory in the number of cities, but half that of the full matrix. If memory
   * constraints are very tight, consider using {@link #createMinimalMemory} instead.
   *
   * @param instanceData a Readble containing the contents of a TSPLIB formatted problem instance
   * @return if instanceData contains an instance of a supported problem type, then this returns the
   *     TSPLIB problem instance. otherwise it returns null.
   */
  public static TSPLIB createTriangleMatrix(Readable instanceData) {
    Scanner in = new Scanner(instanceData);
    Specification spec = parseSpecification(in);
    TSPLIB instance =
        spec.hasNodeCoords() && spec.type().equals("TSP") ? parseTSPTriangleMatrix(in, spec) : null;
    in.close();
    return instance;
  }

  private static TSPLIB parseTSPMatrix(Scanner in, Specification spec) {
    String[][] tokens = parseTokens(in, spec.n());
    if (!validateNodeIds(tokens)) {
      return null;
    }
    if (hasIntCoords(tokens)) {
      return createMatrixIntCoordsTSP(parseIntCoords(tokens), spec.edgeWeightType(), spec.name());
    } else {
      return createMatrixDoubleCoordsTSP(
          parseDoubleCoords(tokens), spec.edgeWeightType(), spec.name());
    }
  }

  private static TSPLIB parseTSPTriangleMatrix(Scanner in, Specification spec) {
    String[][] tokens = parseTokens(in, spec.n());
    if (!validateNodeIds(tokens)) {
      return null;
    }
    if (hasIntCoords(tokens)) {
      return createTriangleMatrixIntCoordsTSP(
          parseIntCoords(tokens), spec.edgeWeightType(), spec.name());
    } else {
      return createTriangleMatrixDoubleCoordsTSP(
          parseDoubleCoords(tokens), spec.edgeWeightType(), spec.name());
    }
  }

  private static TSPLIB parseTSP(Scanner in, Specification spec) {
    String[][] tokens = parseTokens(in, spec.n());
    if (!validateNodeIds(tokens)) {
      return null;
    }
    if (hasIntCoords(tokens)) {
      return createIntCoordsTSP(parseIntCoords(tokens), spec.edgeWeightType(), spec.name());
    } else {
      return createDoubleCoordsTSP(parseDoubleCoords(tokens), spec.edgeWeightType(), spec.name());
    }
  }

  private static TSPLIB createIntCoordsTSP(int[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createEuc2D(coords), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static TSPLIB createDoubleCoordsTSP(
      double[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createEuc2D(coords), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static TSPLIB createMatrixIntCoordsTSP(
      int[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createFullMatrix(createEuc2D(coords), coords.length), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static TSPLIB createMatrixDoubleCoordsTSP(
      double[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createFullMatrix(createEuc2D(coords), coords.length), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static TSPLIB createTriangleMatrixIntCoordsTSP(
      int[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createTriangleMatrix(createEuc2D(coords), coords.length), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static TSPLIB createTriangleMatrixDoubleCoordsTSP(
      double[][] coords, String edgeWeightType, String name) {
    return switch (edgeWeightType) {
      case "EUC_2D" -> new TSPLIB(createTriangleMatrix(createEuc2D(coords), coords.length), name);
      default -> null;
        // other edge types not yet supported
    };
  }

  private static IntBinaryOperator createTriangleMatrix(IntBinaryOperator dist, int n) {
    final int[][] matrix = new int[n][];
    for (int i = 0; i < n; i++) {
      matrix[i] = new int[i + 1];
      for (int j = 0; j < i; j++) {
        matrix[i][j] = dist.applyAsInt(i, j);
      }
    }
    return (i, j) -> j < i ? matrix[i][j] : matrix[j][i];
  }

  private static IntBinaryOperator createFullMatrix(IntBinaryOperator dist, int n) {
    final int[][] matrix = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        matrix[i][j] = matrix[j][i] = dist.applyAsInt(i, j);
      }
    }
    return (i, j) -> matrix[i][j];
  }

  private static IntBinaryOperator createEuc2D(double[][] coords) {
    return (i, j) -> {
      double xd = coords[i][0] - coords[j][0];
      double yd = coords[i][1] - coords[j][1];
      return (int) (0.5 + Math.sqrt(xd * xd + yd * yd));
    };
  }

  private static IntBinaryOperator createEuc2D(int[][] coords) {
    return (i, j) -> {
      double xd = coords[i][0] - coords[j][0];
      double yd = coords[i][1] - coords[j][1];
      return (int) (0.5 + Math.sqrt(xd * xd + yd * yd));
    };
  }

  private static int[][] parseIntCoords(String[][] tokens) {
    int[][] coords = new int[tokens.length][2];
    for (int i = 0; i < tokens.length; i++) {
      int id = Integer.parseInt(tokens[i][0]);
      coords[id - 1][0] = Integer.parseInt(tokens[i][1]);
      coords[id - 1][1] = Integer.parseInt(tokens[i][2]);
    }
    return coords;
  }

  private static double[][] parseDoubleCoords(String[][] tokens) {
    double[][] coords = new double[tokens.length][2];
    for (int i = 0; i < tokens.length; i++) {
      int id = Integer.parseInt(tokens[i][0]);
      coords[id - 1][0] = Double.parseDouble(tokens[i][1]);
      coords[id - 1][1] = Double.parseDouble(tokens[i][2]);
    }
    return coords;
  }

  private static boolean hasIntCoords(String[][] tokens) {
    for (int i = 0; i < tokens.length; i++) {
      if (tokens[i][1].contains(".") || tokens[i][2].contains(".")) {
        return false;
      }
    }
    return true;
  }

  private static boolean validateNodeIds(String[][] tokens) {
    boolean[] hasID = new boolean[tokens.length];
    for (int i = 0; i < hasID.length; i++) {
      int id = Integer.parseInt(tokens[i][0]);
      if (hasID[id - 1]) {
        return false;
      }
      hasID[id - 1] = true;
    }
    return true;
  }

  private static String[][] parseTokens(Scanner in, int n) {
    String[][] tokens = new String[n][];
    for (int i = 0; i < n; i++) {
      tokens[i] = in.nextLine().strip().split("[\\s]+");
    }
    return tokens;
  }

  private static Specification parseSpecification(Scanner in) {
    String name = "";
    String type = "";
    int n = 0;
    String edgeWeightType = "";
    boolean hasNodeCoords = false;
    loop:
    while (in.hasNextLine()) {
      String line = in.nextLine().strip();
      String[] tokens = line.split(":");
      switch (tokens[0].strip()) {
        case "NAME" -> {
          name = tokens[1].strip();
        }
        case "TYPE" -> {
          type = tokens[1].strip();
        }
        case "DIMENSION" -> {
          n = Integer.parseInt(tokens[1].strip());
        }
        case "EDGE_WEIGHT_TYPE" -> {
          edgeWeightType = tokens[1].strip();
        }
        case "NODE_COORD_SECTION" -> {
          hasNodeCoords = true;
          break loop;
        }

          // These cases are not implemented:
          // "COMMENT", "CAPACITY", "EDGE_WEIGHT_FORMAT", "EDGE_DATA_FORMAT"
          // "NODE_COORD_TYPE", "DISPLAY_DATA_TYPE", "EOF"
      }
    }
    return new Specification(name, type, n, edgeWeightType, hasNodeCoords);
  }

  private static record Specification(
      String name, String type, int n, String edgeWeightType, boolean hasNodeCoords) {}
}
