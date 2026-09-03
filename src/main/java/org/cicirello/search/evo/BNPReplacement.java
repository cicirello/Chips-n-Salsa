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
import java.util.List;
import java.util.function.BiFunction;

/**
 * The Best-non-penalized (BNP) is a replacement strategy that promotes proper diversity levels
 * during the evolutionary process. It is an explicit diversity management technique that balances
 * the search toward exploration at initial stages and shifts the balance toward intensification at
 * the end of the optimization process. Thus, premature convergence and other issues may be avoided.
 * At each iteration, the BNP selects as survivors the candidate solutions that are sufficiently
 * distant from one another. Thus, a distance metric among the candidate solutions must be defined.
 * Initially, the distance threshold is computed based on the mean distance between all candidate
 * solutions. Then, such a threshold is linearly decreased.
 *
 * <p>The BNP has been demonstrated to be effective in continuous and combinatorial optimization
 * problems:
 *
 * <p>Cornejo-Acosta, J. A., Segura, C., Garcia-Diaz, J., and Perez-Sansalvador, J. C. (2026).
 * Diversity Management Techniques for the Upper-Bounded Hamiltonian p-Median Problem. Mathematical
 * and Computational Applications, 31(2). https://doi.org/10.3390/mca31020064
 *
 * <p>Segura, C., Lugo, L., Miranda, G., and Serrano Cardenas, E. D. (2024). PACE Solver
 * Description: CIMAT_Team. In E. Bonnet and P. Rzazewski (Eds.), 19th International Symposium on
 * Parameterized and Exact Computation (IPEC 2024) (Vol. 321, p. 31:1-31:4). Schloss Dagstuhl –
 * Leibniz-Zentrum fur Informatik. https://doi.org/10.4230/LIPIcs.IPEC.2024.31
 *
 * <p>Lugo, L., Segura, C., and Miranda, G. (2022). A diversity-aware memetic algorithm for the
 * linear ordering Problem. Memetic Computing, 14(4), 395-409.
 * https://doi.org/10.1007/s12293-022-00378-5
 *
 * <p>Hernandez Constantino, O., and Segura, C. (2022). A parallel memetic algorithm with explicit
 * management of diversity for the job shop scheduling problem. Applied Intelligence, 52(1),
 * 141–153. https://doi.org/10.1007/s10489-021-02406-2
 *
 * <p>Chacón Castillo, J., and Segura, C. (2020). Differential evolution with enhanced diversity
 * maintenance. Optimization Letters, 14(6), 1471–1490. https://doi.org/10.1007/s11590-019-01454-5
 *
 * <p>Romero Ruiz, E., and Segura, C. (2018). Memetic Algorithm with Hungarian Matching Based
 * Crossover and Diversity Preservation. Computación y Sistemas, 22(2), 347–361.
 * https://doi.org/10.13053/CyS-22-2-2951
 *
 * <p>Segura, C., Hernandez-Aguirre, A., Luna, F., and Alba, E. (2017). Improving Diversity in
 * Evolutionary Algorithms: New Best Solutions for Frequency Assignment. IEEE Transactions on
 * Evolutionary Computation, 21(4), 539–553. https://doi.org/10.1109/TEVC.2016.2641477
 *
 * @param <T> the representation of population members
 * @author <a>J. Alejandro Cornejo-Acosta</a>,
 */
public final class BNPReplacement<T> implements ReplacementStrategy<T> {

  private int currentGeneration;
  private int numGenerations;
  private double initialDiversity;
  private double initialDiversityFactor = 0.4;
  private final BiFunction<T, T, Double> distanceFunction;

  /**
   * Constructs the replacement strategy.
   *
   * @param distanceFunction Function to measure the distance between two candidate solutions.
   */
  public BNPReplacement(BiFunction<T, T, Double> distanceFunction) {
    this.distanceFunction = distanceFunction;
  }

  /**
   * Constructs the replacement strategy.
   *
   * @param distanceFunction Function to measure the distance between two candidate solutions.
   * @param initialDiversityFactor Factor to modify the initial diversity threshold. The default
   *     value is 0.4.
   */
  public BNPReplacement(BiFunction<T, T, Double> distanceFunction, double initialDiversityFactor) {
    this(distanceFunction);
    setInitialDiversityFactor(initialDiversityFactor);
  }

  /**
   * Auxiliar class for BNP replacement strategy. Represents an individual in a population used
   * within an evolutionary algorithm. This class is designed to encapsulate the properties of an
   * individual candidate, including its unique identifier, solution, fitness value, and a distance
   * measure used in the BNP replacement strategy.
   *
   * @param <T> The type of the solution represented by this individual.
   */
  private static final class Individual<T> {

    final int id;
    final T solution;
    final double fitness;
    double distance = Double.POSITIVE_INFINITY;

    Individual(int id, T solution, double fitness) {
      this.id = id;
      this.solution = solution;
      this.fitness = fitness;
    }
  }

  @Override
  public void replace(
      PopulationCandidates.IntegerFitness<T> parentPopulation,
      PopulationCandidates.IntegerFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {
    if (currentGeneration == 1) {
      // The initial diversity threshold is computed only at the beginning
      initializeDiversity(parentPopulation);
    }
    internalReplace(parentPopulation, childPopulation, replacements, targetPopulationSize);
    this.currentGeneration++;
  }

  @Override
  public void replace(
      PopulationCandidates.DoubleFitness<T> parentPopulation,
      PopulationCandidates.DoubleFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {
    if (this.currentGeneration > numGenerations) {
      throw new IllegalArgumentException(
          "The BNP replacement strategy was executed more times than expected.");
    }
    if (currentGeneration == 1) {
      // The initial diversity threshold is computed only at the beginning
      initializeDiversity(parentPopulation);
    }
    internalReplace(parentPopulation, childPopulation, replacements, targetPopulationSize);
    this.currentGeneration++;
  }

  /**
   * Selects a candidate from either the parent or child population based on the given individual ID
   * and updates the replacements accordingly.
   *
   * @param parentPopulation The population of parent candidates.
   * @param childPopulation The population of child candidates.
   * @param replacements A container for tracking replacement operations performed during this
   *     process.
   * @param individualId The ID of the individual to be selected from either the parent or child
   *     population. The IDs of the individuals are 0-based index integers. First, the parent
   *     population is indexed, then the offspring population. Thus, if the ID is less than the size
   *     of the parent population, the individual is chosen from the parent population; otherwise,
   *     it is selected from the child population.
   * @return The candidate solution from the corresponding population based on the individual ID.
   */
  private T addReplacement(
      PopulationCandidates<T> parentPopulation,
      PopulationCandidates<T> childPopulation,
      Replacements replacements,
      int individualId) {
    if (individualId < parentPopulation.size()) {
      replacements.chooseFromParentPopulation(individualId, 1);
      return parentPopulation.candidate(individualId);
    } else {
      replacements.chooseFromChildPopulation(individualId - parentPopulation.size(), 1);
      return childPopulation.candidate(individualId - parentPopulation.size());
    }
  }

  /**
   * Initializes the diversity threshold by calculating the mean distance between all pairs of
   * candidates of the initial population. The mean distance is then scaled by an initial diversity
   * factor to determine the initial diversity threshold. This method is called only at the
   * beginning. Thus, it uses the initial population.
   *
   * @param population The population of candidate solutions to be used, it usually refers to the
   *     initial population.
   */
  private void initializeDiversity(PopulationCandidates<T> population) {
    double meanDistance = 0.0;
    for (int i = 0; i < population.size(); i++) {
      for (int j = i + 1; j < population.size(); j++) {
        double distance = distanceFunction.apply(population.candidate(i), population.candidate(j));
        meanDistance += distance;
      }
    }
    meanDistance *= 2.0 / (population.size() * (population.size() - 1));
    this.initialDiversity = meanDistance * this.initialDiversityFactor;
  }

  @Override
  public ReplacementStrategy<T> split() {
    return new BNPReplacement<>(this.distanceFunction, this.initialDiversityFactor);
  }

  /**
   * This method uses the Best-non-penalized strategy to generate the next generation's population
   * by selecting candidates based on fitness and distance thresholds.
   *
   * <p>This method combines candidates from the parents and offspring populations into a unified
   * pool. The selection process promotes a balance between maintaining diversity, thereby enabling
   * exploration in the early stages, and shifting towards exploitation at the end of the
   * evolutionary process.
   *
   * @param parentPopulation The population of parent candidates.
   * @param childPopulation The population of child candidates.
   * @param replacements A container for tracking replacements performed during the operation.
   * @param targetPopulationSize The desired size of the resulting population after replacement.
   */
  private void internalReplace(
      PopulationCandidates<T> parentPopulation,
      PopulationCandidates<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {

    // join all individuals
    List<Individual<T>> allIndividuals =
        new ArrayList<>(parentPopulation.size() + childPopulation.size());
    int individualId = 0;
    for (int i = 0; i < parentPopulation.size(); i++) {
      allIndividuals.add(
          new Individual<>(
              individualId, parentPopulation.candidate(i), getFitness(parentPopulation, i)));
      individualId++;
    }
    for (int i = 0; i < childPopulation.size(); i++) {
      allIndividuals.add(
          new Individual<>(
              individualId, childPopulation.candidate(i), getFitness(childPopulation, i)));
      individualId++;
    }

    // get best of all individuals
    int bestIndividualIndex = 0;
    for (int i = 0; i < allIndividuals.size(); i++) {
      var individual = allIndividuals.get(i);
      if (individual.fitness < allIndividuals.get(bestIndividualIndex).fitness) {
        bestIndividualIndex = i;
      }
    }

    // add best to next replacements
    T survivor =
        addReplacement(
            parentPopulation,
            childPopulation,
            replacements,
            allIndividuals.get(bestIndividualIndex).id);
    allIndividuals.set(bestIndividualIndex, allIndividuals.getLast());
    allIndividuals.removeLast();
    int survivorCount = 1;

    double thresholdDistance =
        initialDiversity - initialDiversity * ((double) currentGeneration / numGenerations);

    while (survivorCount < targetPopulationSize) {
      // update distances
      for (var individual : allIndividuals) {
        individual.distance =
            Math.min(individual.distance, distanceFunction.apply(individual.solution, survivor));
      }

      // Select the next best non-penalized individual
      bestIndividualIndex = 0;
      for (int i = 0; i < allIndividuals.size(); i++) {
        var individual = allIndividuals.get(i);

        boolean betterInDist =
            (individual.distance > allIndividuals.get(bestIndividualIndex).distance);
        boolean eqInDist =
            (individual.distance == allIndividuals.get(bestIndividualIndex).distance);
        boolean betterInFit =
            (individual.fitness < allIndividuals.get(bestIndividualIndex).fitness);
        boolean eqInFit = (individual.fitness == allIndividuals.get(bestIndividualIndex).fitness);

        if (allIndividuals.get(bestIndividualIndex).distance
            < thresholdDistance) { // Do not fulfill distance requirement
          if ((betterInDist) || (eqInDist && betterInFit)) {
            bestIndividualIndex = i;
          }
        } else {
          if (individual.distance >= thresholdDistance) {
            if ((betterInFit) || (eqInFit && betterInDist)) {
              bestIndividualIndex = i;
            }
          }
        }
      }

      // Insert next best individual and remove from all individuals
      survivor =
          addReplacement(
              parentPopulation,
              childPopulation,
              replacements,
              allIndividuals.get(bestIndividualIndex).id);
      allIndividuals.set(bestIndividualIndex, allIndividuals.getLast());
      allIndividuals.removeLast();
      survivorCount++;
    }
  }

  /**
   * Returns the fitness of a candidate solution given its index. The method determines the type of
   * population (either double or integer fitness) and retrieves the corresponding fitness value.
   * This is an auxilar method to abstract the internal replace functionality.
   *
   * @param population The population of candidate solutions, which can be of type {@code
   *     PopulationCandidates.DoubleFitness} or {@code PopulationCandidates.IntegerFitness}.
   * @param index The index of the candidate whose fitness is to be retrieved.
   * @return The fitness value of the candidate at the specified index. If the population is of type
   *     {@code PopulationCandidates.DoubleFitness}, the exact fitness value is returned. If the
   *     population is of type {@code PopulationCandidates.IntegerFitness}, the fitness value is
   *     cast to a double.
   * @throws IllegalArgumentException If the population type is unknown or unsupported.
   */
  private double getFitness(PopulationCandidates<?> population, int index) {
    if (population instanceof PopulationCandidates.DoubleFitness casted) {
      return casted.fitness(index);
    } else if (population instanceof PopulationCandidates.IntegerFitness casted) {
      return casted.fitness(index);
    }
    throw new IllegalArgumentException("Unknown population type");
  }

  /**
   * Sets the initial diversity factor. The default value is 0.4.
   *
   * @param initialDiversityFactor the initial diversity factor
   */
  public void setInitialDiversityFactor(double initialDiversityFactor) {
    this.initialDiversityFactor = initialDiversityFactor;
  }

  /**
   * Initializes the replacement strategy with the specified number of generations.
   *
   * @param generations the number of generations to run the algorithm
   * @throws IllegalArgumentException if generations is less than or equal to 0
   */
  @Override
  public void init(int generations) {
    if (generations <= 0) {
      throw new IllegalArgumentException("generations must be greater than 0");
    }
    this.numGenerations = generations;
    this.currentGeneration = 1;
  }
}
