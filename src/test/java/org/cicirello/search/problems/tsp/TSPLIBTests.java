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

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit tests for TSPLIB. */
public class TSPLIBTests {

  private static final String testFileIntCoords =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    NODE_COORD_SECTION
    1 1 1
    2 4 1
    3 4 5
    4 4 9
    5 1 9
    6 1 5
    """;

  private static final String testFileDoubleCoords =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    NODE_COORD_SECTION
    1 1.5 1.5
    2 4.5 1.5
    3 4.5 5.5
    4 4.5 9.5
    5 1.5 9.5
    6 1.5 5.5
    """;

  private static final String testFileIntCoords2 =
      """
    NAME: fake TSP instance
    COMMENT: This is a comment.
    COMMENT: This is another comment.
    TYPE: TSP
    DIMENSION: 6
    EDGE_WEIGHT_TYPE: EUC_2D
    NODE_COORD_SECTION
    1 1 1
    2 4 1
    3 4 5
    4 4 9
    5 1 9
    6 1 5
    """;

  private static final String testFileDoubleCoords2 =
      """
    NAME: fake TSP instance
    COMMENT: This is a comment.
    COMMENT: This is another comment.
    TYPE: TSP
    DIMENSION: 6
    EDGE_WEIGHT_TYPE: EUC_2D
    NODE_COORD_SECTION
    1 1.5 1.5
    2 4.5 1.5
    3 4.5 5.5
    4 4.5 9.5
    5 1.5 9.5
    6 1.5 5.5
    """;

  private static final String testFileDoubleCoords3 =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    NODE_COORD_SECTION
    1 1 1.0
    2 4.0 1
    3 4 5.0
    4 4.0 9
    5 1 9.0
    6 1.0 5
    """;

  private static final String testFileUnsupportedType =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : NOT_TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    NODE_COORD_SECTION
    1 1 1
    2 4 1
    3 4 5
    4 4 9
    5 1 9
    6 1 5
    """;

  private static final String testFileUnsupportedEdgeTypeInt =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : SOME_UNKNOWN_TYPE
    NODE_COORD_SECTION
    1 1 1
    2 4 1
    3 4 5
    4 4 9
    5 1 9
    6 1 5
    """;

  private static final String testFileUnsupportedEdgeTypeDouble =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : SOME_UNKNOWN_TYPE
    NODE_COORD_SECTION
    1 1.2 1.2
    2 4.2 1.2
    3 4.2 5.2
    4 4.2 9.2
    5 1.2 9.2
    6 1.2 5.2
    """;

  private static final String testFileInvalidIDs =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    NODE_COORD_SECTION
    1 1 1
    2 4 1
    3 4 5
    4 4 9
    1 1 9
    5 1 5
    """;

  private static final String testFileMissingNodeCoords =
      """
    NAME : fake TSP instance
    COMMENT : This is a comment.
    COMMENT : This is another comment.
    TYPE : TSP
    DIMENSION : 6
    EDGE_WEIGHT_TYPE : EUC_2D
    """;

  private static final String expectedTour =
      """
    NAME: fake TSP instance
    COMMENT: Tour length 26
    TYPE: TOUR
    DIMENSION: 6
    TOUR_SECTION
    1
    3
    5
    4
    6
    2
    """;

  private static final String expectedTourComment =
      """
    NAME: fake TSP instance
    COMMENT: Tour length 26
    COMMENT: hello
    TYPE: TOUR
    DIMENSION: 6
    TOUR_SECTION
    1
    3
    5
    4
    6
    2
    """;

  @Test
  public void testToString() {
    StringReader reader = new StringReader(testFileIntCoords);
    TSPLIB problem = TSPLIB.createMinimalMemory(reader);
    Permutation p = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(expectedTour, problem.toString(p));
    assertEquals(expectedTourComment, problem.toString(p, "hello"));
  }

  @Test
  public void testWrongFormat() {
    StringReader reader = new StringReader(testFileUnsupportedType);
    assertNull(TSPLIB.createMinimalMemory(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeInt);
    assertNull(TSPLIB.createMinimalMemory(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeDouble);
    assertNull(TSPLIB.createMinimalMemory(reader));
    reader.close();
    reader = new StringReader(testFileInvalidIDs);
    assertNull(TSPLIB.createMinimalMemory(reader));
    reader.close();
    reader = new StringReader(testFileMissingNodeCoords);
    assertNull(TSPLIB.createMinimalMemory(reader));
    reader.close();
  }

  @Test
  public void testMatrixWrongFormat() {
    StringReader reader = new StringReader(testFileUnsupportedType);
    assertNull(TSPLIB.createFullMatrix(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeInt);
    assertNull(TSPLIB.createFullMatrix(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeDouble);
    assertNull(TSPLIB.createFullMatrix(reader));
    reader.close();
    reader = new StringReader(testFileInvalidIDs);
    assertNull(TSPLIB.createFullMatrix(reader));
    reader.close();
    reader = new StringReader(testFileMissingNodeCoords);
    assertNull(TSPLIB.createFullMatrix(reader));
    reader.close();
  }

  @Test
  public void testTriangleMatrixWrongFormat() {
    StringReader reader = new StringReader(testFileUnsupportedType);
    assertNull(TSPLIB.createTriangleMatrix(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeInt);
    assertNull(TSPLIB.createTriangleMatrix(reader));
    reader.close();
    reader = new StringReader(testFileUnsupportedEdgeTypeDouble);
    assertNull(TSPLIB.createTriangleMatrix(reader));
    reader.close();
    reader = new StringReader(testFileInvalidIDs);
    assertNull(TSPLIB.createTriangleMatrix(reader));
    reader.close();
    reader = new StringReader(testFileMissingNodeCoords);
    assertNull(TSPLIB.createTriangleMatrix(reader));
    reader.close();
  }

  @Test
  public void testIntCoords() {
    StringReader reader = new StringReader(testFileIntCoords);
    TSPLIB problem = TSPLIB.createMinimalMemory(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileIntCoords2);
    problem = TSPLIB.createMinimalMemory(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }

  @Test
  public void testDoubleCoords() {
    StringReader reader = new StringReader(testFileDoubleCoords);
    TSPLIB problem = TSPLIB.createMinimalMemory(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords2);
    problem = TSPLIB.createMinimalMemory(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords3);
    problem = TSPLIB.createMinimalMemory(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }

  @Test
  public void testMatrixIntCoords() {
    StringReader reader = new StringReader(testFileIntCoords);
    TSPLIB problem = TSPLIB.createFullMatrix(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileIntCoords2);
    problem = TSPLIB.createFullMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }

  @Test
  public void testMatrixDoubleCoords() {
    StringReader reader = new StringReader(testFileDoubleCoords);
    TSPLIB problem = TSPLIB.createFullMatrix(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords2);
    problem = TSPLIB.createFullMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords3);
    problem = TSPLIB.createFullMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }

  @Test
  public void testTriangleIntCoords() {
    StringReader reader = new StringReader(testFileIntCoords);
    TSPLIB problem = TSPLIB.createTriangleMatrix(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileIntCoords2);
    problem = TSPLIB.createTriangleMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }

  @Test
  public void testTriangleDoubleCoords() {
    StringReader reader = new StringReader(testFileDoubleCoords);
    TSPLIB problem = TSPLIB.createTriangleMatrix(reader);
    Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4, 5});
    Permutation p2 = new Permutation(new int[] {0, 1, 5, 4, 3, 2});
    Permutation p3 = new Permutation(new int[] {0, 2, 4, 3, 5, 1});
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords2);
    problem = TSPLIB.createTriangleMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();

    reader = new StringReader(testFileDoubleCoords3);
    problem = TSPLIB.createTriangleMatrix(reader);
    assertEquals(0, problem.minCost());
    assertEquals("fake TSP instance", problem.instanceName());
    for (int i = 0; i < 5; i++) {
      assertEquals(22, problem.cost(p1));
      assertEquals(22, problem.value(p1));
      p1.rotate(1);
      assertEquals(24, problem.cost(p2));
      assertEquals(24, problem.value(p2));
      p2.rotate(1);
      assertEquals(26, problem.cost(p3));
      assertEquals(26, problem.value(p3));
      p3.rotate(1);
    }
    reader.close();
  }
}
