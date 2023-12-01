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

/**
 * This package includes classes that implement local search and evolutionary operators for
 * permutations, such as mutation operators, crossover operators, and initializers. The classes of
 * this package depend upon the {@link org.cicirello.permutations.Permutation Permutation} class,
 * and other classes, from <a href="https://jpt.cicirello.org/">JavaPermutationTools (JPT)</a>. More
 * information on JPT, see its website: <a
 * href="https://jpt.cicirello.org/">https://jpt.cicirello.org/</a>. Or you can go directly to <a
 * href="https://jpt.cicirello.org/api/org.cicirello.jpt/module-summary.html">JPT's API
 * documentation</a>.
 *
 * <p>For a survey covering the breadth of evolutionary operators available for permutations, along
 * with an analysis of crossover operators that identifies the permutation features (e.g.,
 * positions, edges, precedences, etc) that different crossover operators effectively optimize, see
 * the following paper:
 *
 * <p>Vincent A. Cicirello. <a
 * href="https://www.cicirello.org/publications/cicirello2023ecta.html">A Survey and Analysis of
 * Evolutionary Operators for Permutations</a>. In <i>Proceedings of the 15th International Joint
 * Conference on Computational Intelligence</i>, pages 288-299. November 2023. doi:<a
 * href="https://doi.org/10.5220/0012204900003595">10.5220/0012204900003595</a>. <a
 * href="https://www.cicirello.org/publications/cicirello2023ecta.pdf">[PDF]</a> <a
 * href="https://www.cicirello.org/publications/cicirello2023ecta.bib">[BIB]</a> <a
 * href="https://doi.org/10.5220/0012204900003595">[DOI]</a>
 *
 * <p>The following paper provides an analysis of many of the mutation operators that identifies the
 * permutation features (e.g., positions, edges, precedences, etc) that different crossover
 * operators effectively optimize:
 *
 * <p>Vincent A. Cicirello. <a
 * href="https://www.cicirello.org/publications/cicirello2023mone.html">On Fitness Landscape
 * Analysis of Permutation Problems: From Distance Metrics to Mutation Operator Selection</a>.
 * <i>Mobile Networks and Applications</i>, 28(2): 507-517, April 2023. doi:<a
 * href="https://doi.org/10.1007/s11036-022-02060-z">10.1007/s11036-022-02060-z</a>. <a
 * href="https://www.cicirello.org/publications/Cicirello-MONE-2022.pdf">[PDF]</a> <a
 * href="https://www.cicirello.org/publications/cicirello2023mone.bib">[BIB]</a> <a
 * href="https://doi.org/10.1007/s11036-022-02060-z">[DOI]</a> <a
 * href="https://rdcu.be/cZgYG">[PUB]</a> <a href="https://arxiv.org/abs/2208.11188">[arXiv]</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
package org.cicirello.search.operators.permutations;
