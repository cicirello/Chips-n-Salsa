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

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** Helpers for JUnit tests of scheduling heuristics. */
public class SchedulingHeuristicValidation {

  static class FakeProblemDuedates implements SingleMachineSchedulingProblem {

    private FakeProblemData data;

    public FakeProblemDuedates(int[] d) {
      data = new FakeProblemData(d);
    }

    public FakeProblemDuedates(int[] d, int[] p) {
      data = new FakeProblemData(d, p, true);
    }

    public FakeProblemDuedates(int[] d, int[] p, int s) {
      data = new FakeProblemData(d, p, true);
      data.s = s;
    }

    @Override
    public SingleMachineSchedulingProblemData getInstanceData() {
      return data;
    }

    @Override
    public int cost(Permutation p) {
      return 10;
    }

    @Override
    public int value(Permutation p) {
      return 10;
    }
  }

  static class FakeProblemWeightsPTime implements SingleMachineSchedulingProblem {

    private FakeProblemData data;

    public FakeProblemWeightsPTime(int[] w, int[] p) {
      data = new FakeProblemData(w, p);
    }

    public FakeProblemWeightsPTime(int[] w, int[] p, int d) {
      data = new FakeProblemData(w, p, d);
    }

    public FakeProblemWeightsPTime(int[] w, int[] p, int d, int s) {
      data = new FakeProblemData(w, p, d, s);
    }

    public FakeProblemWeightsPTime(int[] w, int[] p, int[] d, int s) {
      data = new FakeProblemData(w, p, d, s);
    }

    public FakeProblemWeightsPTime(int[] w, int[] p, int[] d, int[][] s) {
      data = new FakeProblemDataSetups(w, p, d, s);
    }

    public FakeProblemWeightsPTime(int[] w, int[] p, int d, int[][] s) {
      data = new FakeProblemDataSetups(w, p, d, s);
    }

    @Override
    public SingleMachineSchedulingProblemData getInstanceData() {
      return data;
    }

    @Override
    public int cost(Permutation p) {
      return 10;
    }

    @Override
    public int value(Permutation p) {
      return 10;
    }
  }

  static class FakeEarlyTardyProblem implements SingleMachineSchedulingProblem {

    private FakeEarlyTardyProblemData data;

    public FakeEarlyTardyProblem(int[] p, int[] we, int[] wt, int[] d) {
      data = new FakeEarlyTardyProblemData(p, we, wt, d);
    }

    @Override
    public SingleMachineSchedulingProblemData getInstanceData() {
      return data;
    }

    @Override
    public int cost(Permutation p) {
      return 10;
    }

    @Override
    public int value(Permutation p) {
      return 10;
    }
  }

  static class FakeProblemDataSetups extends FakeProblemData {
    private int[][] s;

    public FakeProblemDataSetups(int[] w, int[] p, int d, int[][] s) {
      super(w, p, d);
      this.s = new int[s.length][];
      for (int i = 0; i < s.length; i++) {
        this.s[i] = s[i].clone();
      }
    }

    public FakeProblemDataSetups(int[] w, int[] p, int[] d, int[][] s) {
      super(w, p, d, 0);
      this.s = new int[s.length][];
      for (int i = 0; i < s.length; i++) {
        this.s[i] = s[i].clone();
      }
    }

    @Override
    public int getSetupTime(int j) {
      return s[j][j];
    }

    @Override
    public int getSetupTime(int i, int j) {
      return s[i][j];
    }

    @Override
    public boolean hasSetupTimes() {
      return true;
    }
  }

  static class FakeProblemData implements SingleMachineSchedulingProblemData {

    private int[] d;
    private int[] w;
    private int[] p;
    private int[][] smatrix;
    private int s;
    private int n;

    public FakeProblemData(int[] d) {
      this.d = d.clone();
      s = -1;
      n = d.length;
    }

    public FakeProblemData(int[] d, int[] p, boolean duedates) {
      if (duedates) {
        this.d = d.clone();
        this.p = p.clone();
      } else {
        throw new IllegalArgumentException();
      }
      s = -1;
      n = d.length;
    }

    public FakeProblemData(int[] w, int[] p) {
      this.w = w.clone();
      this.p = p.clone();
      s = -1;
      n = p.length;
    }

    public FakeProblemData(int[] w, int[] p, int d) {
      this.w = w.clone();
      this.p = p.clone();
      this.d = new int[w.length];
      for (int i = 0; i < this.d.length; i++) this.d[i] = d;
      s = -1;
      n = p.length;
    }

    public FakeProblemData(int[] w, int[] p, int d, int s) {
      this(w, p, d);
      this.s = s;
      n = p.length;
    }

    public FakeProblemData(int[] w, int[] p, int[] d, int s) {
      this.w = w.clone();
      this.p = p.clone();
      this.d = d.clone();
      this.s = s;
      n = p.length;
    }

    @Override
    public int numberOfJobs() {
      return n;
    }

    @Override
    public int getProcessingTime(int j) {
      return p == null ? 0 : p[j];
    }

    @Override
    public int[] getCompletionTimes(Permutation schedule) {
      return null;
    }

    @Override
    public boolean hasDueDates() {
      return d != null;
    }

    @Override
    public int getDueDate(int j) {
      return d[j];
    }

    @Override
    public boolean hasWeights() {
      return w != null;
    }

    @Override
    public int getWeight(int j) {
      return w[j];
    }

    @Override
    public int getSetupTime(int j) {
      return s;
    }

    @Override
    public int getSetupTime(int i, int j) {
      return 2 * i + j;
    }

    @Override
    public boolean hasSetupTimes() {
      return s > 0;
    }
  }

  static class FakeEarlyTardyProblemData implements SingleMachineSchedulingProblemData {

    private int[] d;
    private int[] we;
    private int[] wt;
    private int[] p;
    private int n;

    public FakeEarlyTardyProblemData(int[] p, int[] we, int[] wt, int[] d) {
      this.d = d.clone();
      this.we = we.clone();
      this.wt = wt.clone();
      this.p = p.clone();
      n = p.length;
    }

    @Override
    public int numberOfJobs() {
      return n;
    }

    @Override
    public int getProcessingTime(int j) {
      return p[j];
    }

    @Override
    public int[] getCompletionTimes(Permutation schedule) {
      return null;
    }

    @Override
    public boolean hasDueDates() {
      return true;
    }

    @Override
    public int getDueDate(int j) {
      return d[j];
    }

    @Override
    public boolean hasWeights() {
      return true;
    }

    @Override
    public int getWeight(int j) {
      return wt[j];
    }

    @Override
    public boolean hasEarlyWeights() {
      return true;
    }

    @Override
    public int getEarlyWeight(int j) {
      return we[j];
    }
  }
}
