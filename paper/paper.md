---
title: 'Chips-n-Salsa: A Java Library of Customizable, Hybridizable, Iterative, Parallel, Stochastic, and Self-Adaptive Local Search Algorithms'
tags:
  - stochastic local search
  - parallel metaheuristics
  - self-adaptive search
  - simulated annealing
  - discrete optimization
  - combinatorial optimization
  - Java
authors:
  - name: Vincent A. Cicirello
    orcid: 0000-0003-1072-8559
    affiliation: 1
affiliations:
  - name: Computer Science, School of Business, Stockton University, Galloway, NJ 08205
    index: 1
date: 23 July 2020
bibliography: paper.bib
---

# Summary

Discrete optimization problems are often both of practical real-world importance as well as computationally intractable. For example, the optimization variants of the traveling salesperson, bin packing, and longest common subsequence problems are NP-Hard, as is resource constrained scheduling, and even many single-machine scheduling problems [@Garey1979]. Polynomial time algorithms for such problems are unlikely to exist, and the best known algorithms that are guaranteed to optimally solve these problems have a worst-case exponential runtime. It is thus common to use stochastic local search and other metaheuristics [@Gonzalez2018] for these and related problems. Stochastic local search algorithms begin at a random search state, and apply a sequence of neighbor transitions to nearby similar search states. This includes perturbative [@Hoos2018] algorithms like simulated annealing [@Delahaye2019] and hill climbers [@Hoos2018], where each search state is a complete candidate feasible solution, and a mutation operator (or neighborhood operator) makes a small modification (usually randomly) to move to another local candidate solution; and also includes constructive [@Hoos2018] algorithms like stochastic samplers [@Grasas2017; @ReyesRubiano2020; @Cicirello2005; @Bresina1996; @Langley1992], where each search state is a partial solution that is iteratively transformed into a complete feasible solution. Stochastic local search algorithms are not guaranteed to find optimal solutions. However, they often find near-optimal solutions in much less time than systematic search. They also are characterized by an anytime property [@Jesus2020; @Zilberstein1996], such that the quality of the solution improves with runtime.

Chips-n-Salsa is a Java library of customizable, hybridizable, iterative, parallel, stochastic, and self-adaptive local search algorithms. Its focus is discrete optimization, but also supports continuous optimization. The library provides a variety of solution representations. For example, it includes BitVector and IntegerVector classes, indexable vectors of bits and integers, respectively, along with mutation operators for these types. The library also utilizes our significant prior research on permutation optimization problems, and provides an extensive set of mutation operators for permutations [@Cicirello2016; @Cicirello2013], including window-limited mutation [@Cicirello2014]. It uses the efficiently implemented Permutation class of the JavaPermutationTools (JPT) [@Cicirello2018b] library for representing solutions to such problems. For continuous problems, Chips-n-Salsa provides a RealVector class, Gaussian mutation [@Petrowski2017], Cauchy mutation [@Petrowski2017], and uniform mutation. The library also includes optimization problems useful for benchmarking new metaheuristic implementations, such as the well-known OneMax problem [@Doerr2019], BoundMax (generalization of OneMax to integers), the Permutation in a Haystack problem [@Cicirello2016], and polynomial root finding.

The repository (https://github.com/cicirello/Chips-n-Salsa) contains source code of the library, and programs that provide example usage of key functionality. API and other documentation is hosted on the web (https://chips-n-salsa.cicirello.org/). 

# Chips-n-Salsa Features and Functionality:

* __Stochastic Local Search Algorithms__: The library extensively supports simulated annealing [@Delahaye2019], with implementations of all of the common annealing schedules, as well as more advanced annealing schedules. It also includes common forms of hill climbing [@Hoos2018], and several stochastic sampling [@Grasas2017] search algorithms, such as iterative sampling [@Langley1992], Heuristic Biased Stochastic Sampling (HBSS) [@Bresina1996], Value Biased Stochastic Sampling (VBSS) [@Cicirello2005; @Cicirello2003], and a stochastic sampler using acceptance bands [@Oddi1997; @Gomes1998]. We optimized random number generation based on our prior research to minimize the runtime impact of one of the more costly operations of a metaheuristic [@Cicirello2018a].
* __Customizable__: Chips-n-Salsa uses generic types enabling using the library to optimize other representations beyond what is provided in the library. It is also designed to allow easily integrating new mutation operators, and to enable customizing all stages of the local search (e.g., choice of annealing schedule for simulated annealing).
* __Hybridizable__: Chips-n-Salsa supports integrating multiple forms of local search (e.g., hybrids of hill climbing with simulated annealing), creating hybrid mutation operators (e.g., combining multiple mutation operators), as well as support for running more than one type of search for the same problem concurrently using multiple threads as a form of algorithm portfolio [@Tong2019; @Gomes2001].
* __Iterative__: Chips-n-Salsa supports multistart metaheuristics, including implementations of several restart schedules [@Luby1993; @Cicirello2017] for varying the run lengths across the restarts. 
* __Parallel__: Chips-n-Salsa supports parallel execution of multiple instances of the same, or different, stochastic local search algorithms for an instance of a problem to accelerate the search process by exploiting multicore architectures [@Cicirello2017]. 
* __Self-Adaptive__: Chips-n-Salsa includes adaptive annealing schedules [@Hubin2019; @Stefankovic2009] for simulated annealing, such as the Modified Lam schedule [@Boyan1998; @Cicirello2007], self-tuning variations of the simpler annealing schedules, and restart schedules that adapt to run length [@Cicirello2017].

# Statement of Need

The target audience of Chips-n-Salsa includes those conducting computational research in diverse domains, wherever applications of NP-Hard optimization problems is important. Existing local search and metaheuristic implementations that are freely available are often intimately tied to a specific problem, or at best to a specific representation; whereas Chips-n-Salsa is not only problem-independent, but also representation-independent. Its support for parallel execution enables easily exploiting multicore processor architectures to speed up problem solving.  The self-adaptive and self-tuning features streamline the development process, eliminating the often tedious stage, control parameter optimization, of applying stochastic local search to a new problem.

# References
