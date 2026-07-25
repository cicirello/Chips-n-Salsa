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

import java.util.ArrayList;
import java.util.Iterator;
import org.cicirello.util.Copyable;

/**
 * Vector of members of the population. Package level access.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class PopulationVector {

  /** private to prevent instantiation */
  private PopulationVector() {}

  /** Vector of members of the population with integer-valued fitnesses. */
  static final class IntegerFitness<T extends Copyable<T>>
      implements PopulationCandidates.IntegerFitness<T>,
          Iterable<PopulationMember.IntegerFitness<T>> {

    private final ArrayList<PopulationMember.IntegerFitness<T>> p;

    public IntegerFitness(int initialBuffer) {
      p = new ArrayList<PopulationMember.IntegerFitness<T>>(initialBuffer);
    }

    @Override
    public int size() {
      return p.size();
    }

    @Override
    public int fitness(int i) {
      return p.get(i).fitness();
    }

    @Override
    public T candidate(int i) {
      return p.get(i).candidate();
    }

    @Override
    public Iterator<PopulationMember.IntegerFitness<T>> iterator() {
      return p.iterator();
    }

    PopulationMember.IntegerFitness<T> get(int i) {
      return p.get(i);
    }

    void add(PopulationMember.IntegerFitness<T> e) {
      p.add(e);
    }

    void clear() {
      p.clear();
    }
  }

  /** Vector of members of the population with double-valued fitnesses. */
  static final class DoubleFitness<T extends Copyable<T>>
      implements PopulationCandidates.DoubleFitness<T>,
          Iterable<PopulationMember.DoubleFitness<T>> {

    private final ArrayList<PopulationMember.DoubleFitness<T>> p;

    public DoubleFitness(int initialBuffer) {
      p = new ArrayList<PopulationMember.DoubleFitness<T>>(initialBuffer);
    }

    @Override
    public int size() {
      return p.size();
    }

    @Override
    public double fitness(int i) {
      return p.get(i).fitness();
    }

    @Override
    public T candidate(int i) {
      return p.get(i).candidate();
    }

    @Override
    public Iterator<PopulationMember.DoubleFitness<T>> iterator() {
      return p.iterator();
    }

    PopulationMember.DoubleFitness<T> get(int i) {
      return p.get(i);
    }

    void add(PopulationMember.DoubleFitness<T> e) {
      p.add(e);
    }

    void clear() {
      p.clear();
    }
  }
}
