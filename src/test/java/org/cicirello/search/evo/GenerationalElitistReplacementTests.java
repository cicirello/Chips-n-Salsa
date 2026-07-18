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

package org.cicirello.search.evo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test cases for GenerationalElitistReplacement. */
public class GenerationalElitistReplacementTests {

  @Test
  public void testSplit() {
    GenerationalElitistReplacement<String> replacement =
        new GenerationalElitistReplacement<String>();
    assertSame(replacement, replacement.split());
  }

  @Test
  public void testDouble3() {
    int[][] parentFitnessArrays = {
      {15, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17},
      {17, 16, 15, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 17, 16, 15},
      {4, 5, 6, 17, 16, 15, 7, 8, 9, 10},
      {4, 5, 6, 15, 16, 17, 7, 8, 9, 10}
    };
    int[][] expectedFromParents = {
      {0, 1, 2},
      {7, 8, 9},
      {0, 1, 2},
      {7, 8, 9},
      {3, 4, 5},
      {3, 4, 5}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14},
      {14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6},
      {0, 1, 2, 3, 4, 5, 6}
    };
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesDouble parents = new CandidatesDouble(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesDouble children = new CandidatesDouble(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(3);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testDouble1() {
    int[][] parentFitnessArrays = {
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17},
      {17, 16, 15, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 15, 16, 17, 7, 8, 9, 10}
    };
    int[][] expectedFromParents = {{9}, {0}, {5}};
    int[][] childFitnessArrays = {
      {1, 2, 3, 4, 5, 11, 12, 13, 14},
      {14, 13, 12, 11, 5, 4, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8},
      {0, 1, 2, 3, 4, 5, 6, 7, 8}
    };
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesDouble parents = new CandidatesDouble(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesDouble children = new CandidatesDouble(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>();
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testDoubleDuplicates2() {
    int[][] parentFitnessArrays = {
      {16, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 16, 16, 17}
    };
    int[][] expectedFromParents = {
      {0, 2},
      {7, 9}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14, 15},
      {15, 14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6, 7},
      {0, 1, 2, 3, 4, 5, 6, 7}
    };
    String[][] parentElements = {
      {"P1", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9"},
      {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P8", "P9"}
    };
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesDouble parents = new CandidatesDouble(parentFitnessArrays[i], parentElements[i]);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesDouble children = new CandidatesDouble(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(2);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testDoubleAllElite() {
    int[][] parentFitnessArrays = {
      {15, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17}
    };
    int[][] expectedFromParents = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14},
      {14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {{}, {}};
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesDouble parents = new CandidatesDouble(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesDouble children = new CandidatesDouble(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(parentFitnessArrays[i].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testInteger3() {
    int[][] parentFitnessArrays = {
      {15, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17},
      {17, 16, 15, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 17, 16, 15},
      {4, 5, 6, 17, 16, 15, 7, 8, 9, 10},
      {4, 5, 6, 15, 16, 17, 7, 8, 9, 10}
    };
    int[][] expectedFromParents = {
      {0, 1, 2},
      {7, 8, 9},
      {0, 1, 2},
      {7, 8, 9},
      {3, 4, 5},
      {3, 4, 5}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14},
      {14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6},
      {0, 1, 2, 3, 4, 5, 6}
    };
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesInteger parents = new CandidatesInteger(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesInteger children = new CandidatesInteger(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(3);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testInteger1() {
    int[][] parentFitnessArrays = {
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17},
      {17, 16, 15, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 15, 16, 17, 7, 8, 9, 10}
    };
    int[][] expectedFromParents = {{9}, {0}, {5}};
    int[][] childFitnessArrays = {
      {1, 2, 3, 4, 5, 11, 12, 13, 14},
      {14, 13, 12, 11, 5, 4, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8},
      {0, 1, 2, 3, 4, 5, 6, 7, 8}
    };
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesInteger parents = new CandidatesInteger(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesInteger children = new CandidatesInteger(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>();
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testIntegerDuplicates2() {
    int[][] parentFitnessArrays = {
      {16, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 16, 16, 17}
    };
    int[][] expectedFromParents = {
      {0, 2},
      {7, 9}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14, 15},
      {15, 14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {
      {0, 1, 2, 3, 4, 5, 6, 7},
      {0, 1, 2, 3, 4, 5, 6, 7}
    };
    String[][] parentElements = {
      {"P1", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9"},
      {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P8", "P9"}
    };
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesInteger parents = new CandidatesInteger(parentFitnessArrays[i], parentElements[i]);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesInteger children = new CandidatesInteger(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(2);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  @Test
  public void testIntegerAllElite() {
    int[][] parentFitnessArrays = {
      {15, 16, 17, 4, 5, 6, 7, 8, 9, 10},
      {4, 5, 6, 7, 8, 9, 10, 15, 16, 17}
    };
    int[][] expectedFromParents = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
    };
    int[][] childFitnessArrays = {
      {1, 2, 3, 11, 12, 13, 14},
      {14, 13, 12, 11, 3, 2, 1}
    };
    int[][] expectedFromChildren = {{}, {}};
    String[] parentElements = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
    String[] childElements = {"C1", "C2", "C3", "C4", "C5", "C6", "C7"};
    for (int i = 0; i < parentFitnessArrays.length; i++) {
      CandidatesInteger parents = new CandidatesInteger(parentFitnessArrays[i], parentElements);
      for (int j = 0; j < childFitnessArrays.length; j++) {
        CandidatesInteger children = new CandidatesInteger(childFitnessArrays[j], childElements);
        ReplacementsValidator validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        GenerationalElitistReplacement<String> replacement =
            new GenerationalElitistReplacement<String>(parentFitnessArrays[i].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
        replacement.init(1000);
        validator =
            new ReplacementsValidator(parentFitnessArrays[i].length, childFitnessArrays[j].length);
        replacement.replace(parents, children, validator, parentFitnessArrays[i].length);
        validator.validate(expectedFromParents[i], expectedFromChildren[j]);
      }
    }
  }

  private static class ReplacementsValidator implements ReplacementStrategy.Replacements {

    private boolean[] addedParents;
    private boolean[] addedChildren;
    private int count;

    public ReplacementsValidator(int p, int c) {
      addedParents = new boolean[p];
      addedChildren = new boolean[c];
    }

    @Override
    public void addFromParentPopulation(int i) {
      assertFalse(addedParents[i]);
      addedParents[i] = true;
      count++;
    }

    @Override
    public void addFromChildPopulation(int i) {
      assertFalse(addedChildren[i]);
      addedChildren[i] = true;
      count++;
    }

    public void validate(int[] expectedP, int[] expectedC) {
      for (int p : expectedP) {
        assertTrue(addedParents[p], "Parent:" + p);
      }
      for (int c : expectedC) {
        assertTrue(addedChildren[c], "Child:" + c);
      }
      assertEquals(expectedP.length + expectedC.length, count);
    }
  }

  private static class CandidatesDouble implements PopulationCandidates.DoubleFitness<String> {

    private String[] c;
    private int[] f;

    public CandidatesDouble(int[] f, String[] c) {
      this.f = f.clone();
      this.c = c.clone();
    }

    @Override
    public int size() {
      return f.length;
    }

    @Override
    public double fitness(int i) {
      return f[i];
    }

    @Override
    public String candidate(int i) {
      return c[i];
    }
  }

  private static class CandidatesInteger implements PopulationCandidates.IntegerFitness<String> {

    private String[] c;
    private int[] f;

    public CandidatesInteger(int[] f, String[] c) {
      this.f = f.clone();
      this.c = c.clone();
    }

    @Override
    public int size() {
      return f.length;
    }

    @Override
    public int fitness(int i) {
      return f[i];
    }

    @Override
    public String candidate(int i) {
      return c[i];
    }
  }
}
