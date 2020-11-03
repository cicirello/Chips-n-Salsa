# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 2020-11-2

### Added

### Changed

### Deprecated

### Removed

### Fixed
* Fixed missing parameter bounds check in LubyRestarts constructor.

### CI/CD


## [2.5.0] - 2020-11-2

### Added
* EXPET heuristic, a constructive heuristic for common duedate scheduling problems.

### Changed
* Minor optimizations in scheduling problem classes (heuristics and cost functions).

### Fixed
* Fixed bug in parallel searches involving handling exceptions thrown by component searches.
* Added missing parameter checks in constructors of parallel metaheuristic implementations.
* Added missing parameter bounds check in LINET scheduling heuristic implementation.
* Added test cases to improve coverage.

### CI/CD
* Code coverage reporting, via JaCoCo, added to build process.


## [2.4.1] - 2020-10-15

### Fixed
* Bug in release workflow: attaching jars to GitHub Release.


## [2.4.0] - 2020-10-15

### Added
* VBSS support for exponential bias functions.

### Changed
* Build process migrated from Ant to Maven.
* Improved formatting of description field in pom.xml.

### Removed
* The example programs have been removed from the Chips-n-Salsa repository, and now reside in their own dedicated repository: https://github.com/cicirello/chips-n-salsa-examples
* Distribution jar files removed from repository.  The distribution jars are available via GitHub Releases, GitHub Packages, and Maven Central.


## [2.3.0] - 2020-10-13

### Added
* Problem instance generator for Common Duedate Scheduling, based on the method used to generate the instances from the OR-Lib.
* Parser for the file format specified in OR-Lib for the Common Duedate Scheduling benchmarks.
* WLPT heuristic, a constructive heuristic for common duedate scheduling problems.
* LINET heuristic, a constructive heuristic for common duedate scheduling problems.



## [2.2.0] - 2020-09-24

### Added
* ModifiedLamOriginal: This class is a direct implementation of the Modified Lam annealing schedule as originally described by Swartz and Boyan. Introduced to the library to enable comparing with the optimized version already included in the library in the class ModifiedLam.
* Instance generator for static scheduling with weights and duedates.

### Changed
* Upgraded dependency to JPT 2.2.0.
* Modifications to API documentation website to improve browsing on mobile web browsers.


## [2.1.0] - 2020-09-18

### Added
* Parser for benchmark scheduling instance data files for single machine scheduling problems with weights, duedates, and sequence-dependent setup times.

### Changed
* Revised README to include instructions for importing from Maven Central
* API documentation website (https://chips-n-salsa.cicirello.org) updated to html5


## [2.0.0] - 2020-09-16

First release available through Maven Central. New major release, 2.0.0, due 
to incompatible interface changes within the org.cicirello.search.ss package. Code 
that doesn't depend upon any of the stochastic sampling search algorithm 
implementations should be unaffected by these changes, and in those cases should 
be safe to simply upgrade to this new version without need to change anything in 
dependent code.

### Added
* An interface Partial to enable generalizing the various stochastic sampling search algorithms from optimizing the space of permutations to more general types.
* A method was added to ConstructiveHeuristic for creating empty Partials of the appropriate length needed by the heuristic.
* Class HeuristicSolutionGenerator, which is a generalization of existing class HeuristicPermutationGenerator, for constructing solutions to optimization problems via constructive heuristics (HeuristicPermutationGenerator now extends this new class).
* PartialIntegerVector class to support using stochastic sampling search algorithms to generate vectors of integers.
* BoundedIntegerVector and BoundedRealVector classes added to the package org.cicirello.search.representations

### Changed
* ConstructiveHeuristic and IncrementalEvaluation now have a type parameter.
* ValueBiasedStochasticSampling, HeuristicBiasedStochasticSampling, and AcceptanceBandSampling also now have a type parameter.
* All of the scheduling heuristics were modified based on interface changes.
* Renamed ConstructiveHeuristic.completePermutationLength to ConstructiveHeuristic.completeLength to be more general.
* Minor edits to the example program SchedulingWithVBSS related to other changes (added missing type parameter to construction of the ValueBiasedStochasticSampling instance to avoid compiler warning).
* Workflows and pom.xml to enable publishing to both Maven Central as well as Github Packages


## [1.4.0] - 2020-08-18
### Added
* .zenodo.json to provide project metadata to Zenodo during releases
* Enabled DependaBot to keep dependencies up to date.

### Changed
* Refactored SimulatedAnnealing to simplify implementation.
* Minor modifications to example programs that involve the SimulatedAnnealing class to use simplified constructors.
* Refactored HBSS, VBSS, AcceptanceBandSampling, and IterativeSampling to use a common abstract base class to remove code redundancy.
* Refactored HeuristicPermutationGenerator to simplify implementation.
* Updated dependency versions.

## [1.3.0] - 2020-08-06
### Added
* ATCS scheduling heuristic (as described by the heuristic's authors).
* DynamicATCS scheduling heuristic (dynamically updates heuristic's parameters during schedule construction).
* An example program for an industrial scheduling problem, demonstrating using a stochastic sampling search algorithm, VBSS, as well as a hybrid of VBSS and hill climbing.
* CHANGELOG.md
* Automated sitemap.xml generation for the API website.

### Changed
* Improved representation of setup times for scheduling problems with sequence-dependent setups.
* Refactored ATC and Montagne scheduling heuristics, and scheduling heuristic abstract base class for performance and style.
* Refactored hill climbing classes, introducing an abstract base class, to eliminate redundant code.

### Removed
* Unnecessary maven settings.xml.

### Fixed
* Fixed classpath bug in Makefile (Windows use of semicolon vs Linux, etc use of colon as delimiter). Now adjusts based on OS.

## [1.2.0]
### Added
* Several constructive heuristics for scheduling problems for use with stochastic sampling search algorithms.
* Heuristics include: EDD, MST, SPT, WSPT, Smallest Setup, Smallest Two-Job Setup, Montagne, Weighted COVERT, Weighted Critical Ratio, ATC.
* Heuristics also include variations of several of the above that adjust for setup times for scheduling problems with sequence-dependent setups.
* Contributing guidelines.

### Changed
* Refactored scheduling heuristic classes to improve readability.
* Some code optimizations within the scheduling heuristic classes.
* Improvements to README.

## [1.1.0]
### Added
* Constructive heuristics for single machine scheduling problems.
* Prebuilt jars now included with release.

## [1.0.2]
### Changed
* Minor non-functional changes related to code style.
* Edits to JOSS paper submission.

## [1.0.1]
### Other
* Added a workflow to the repo to automatically publish to the GitHub Package Registry on release. This release is simply to confirm that it works and to publish the package to the GPR.

## [1.0.0]
### First public release
Chips-n-Salsa is a Java library of customizable, hybridizable, iterative, parallel, stochastic, and self-adaptive local search algorithms. The library includes implementations of several stochastic local search algorithms, including simulated annealing, hill climbers, as well as constructive search algorithms such as stochastic sampling. The library most extensively supports simulated annealing. It includes several classes for representing solutions to a variety of optimization problems. For example, the library includes a BitVector class that implements vectors of bits, as well as classes for representing solutions to problems where we are searching for an optimal vector of integers or reals. For each of the built-in representations, the library provides the most common mutation operators for generating random neighbors of candidate solutions. Additionally, the library provides extensive support for permutation optimization problems, including implementations of many different mutation operators for permutations, and utilizing the efficiently implemented Permutation class of the JavaPermutationTools (JPT) library.

Chips-n-Salsa is customizable, making extensive use of Java's generic types, enabling using the library to optimize other types of representations beyond what is provided in the library. It is hybridizable, providing support for integrating multiple forms of local search (e.g., using a hill climber on a solution generated by simulated annealing), creating hybrid mutation operators (e.g., local search using multiple mutation operators), as well as support for running more than one type of search for the same problem concurrently using multiple threads as a form of algorithm portfolio. Chips-n-Salsa is iterative, with support for multistart metaheuristics, including implementations of several restart schedules for varying the run lengths across the restarts. It also supports parallel execution of multiple instances of the same, or different, stochastic local search algorithms for an instance of a problem to accelerate the search process. The library supports self-adaptive search in a variety of ways, such as including implementations of adaptive annealing schedules for simulated annealing, such as the Modified Lam schedule, implementations of the simpler annealing schedules but which self-tune the initial temperature and other parameters, and restart schedules that adapt to run length.
