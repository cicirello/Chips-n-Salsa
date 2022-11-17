[![Chips-n-Salsa - A Java library of customizable, hybridizable, iterative, parallel, stochastic, and self-adaptive local search algorithms](https://chips-n-salsa.cicirello.org/images/chips-n-salsa640.png)](#chips-n-salsa---a-java-library-of-customizable-hybridizable-iterative-parallel-stochastic-and-self-adaptive-local-search-algorithms)

# Chips-n-Salsa [![Mentioned in Awesome Machine Learning](https://awesome.re/mentioned-badge.svg)](https://github.com/josephmisiti/awesome-machine-learning)

Copyright (C) 2002-2022 [Vincent A. Cicirello](https://www.cicirello.org/).

Website: https://chips-n-salsa.cicirello.org/

API documentation: https://chips-n-salsa.cicirello.org/api/

| __Publications About the Library__ | [![DOI](https://joss.theoj.org/papers/10.21105/joss.02448/status.svg)](https://doi.org/10.21105/joss.02448) |
| :--- | :--- |
| __Packages and Releases__ | [![Maven Central](https://img.shields.io/maven-central/v/org.cicirello/chips-n-salsa.svg?label=Maven%20Central&logo=apachemaven)](https://search.maven.org/artifact/org.cicirello/chips-n-salsa) [![GitHub release (latest by date)](https://img.shields.io/github/v/release/cicirello/Chips-n-Salsa?logo=GitHub)](https://github.com/cicirello/Chips-n-Salsa/releases) [![JitPack](https://jitpack.io/v/org.cicirello/chips-n-salsa.svg)](https://jitpack.io/#org.cicirello/chips-n-salsa) |
| __Build Status__ | [![build](https://github.com/cicirello/Chips-n-Salsa/workflows/build/badge.svg)](https://github.com/cicirello/Chips-n-Salsa/actions/workflows/build.yml) [![docs](https://github.com/cicirello/Chips-n-Salsa/workflows/docs/badge.svg)](https://chips-n-salsa.cicirello.org/api/) [![CodeQL](https://github.com/cicirello/Chips-n-Salsa/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/cicirello/Chips-n-Salsa/actions/workflows/codeql-analysis.yml) |
| __JaCoCo Test Coverage__ | [![coverage](https://raw.githubusercontent.com/cicirello/Chips-n-Salsa/badges/jacoco.svg)](https://github.com/cicirello/Chips-n-Salsa/actions/workflows/build.yml) [![branches coverage](https://raw.githubusercontent.com/cicirello/Chips-n-Salsa/badges/branches.svg)](https://github.com/cicirello/Chips-n-Salsa/actions/workflows/build.yml) |
| __Security__ | [![Snyk security score](https://snyk-widget.herokuapp.com/badge/mvn/org.cicirello/chips-n-salsa/badge.svg)](https://snyk.io/vuln/maven%3Aorg.cicirello%3Achips-n-salsa) [![Snyk Known Vulnerabilities](https://snyk.io/test/github/cicirello/Chips-n-Salsa/badge.svg)](https://snyk.io/test/github/cicirello/Chips-n-Salsa) |
| __DOI__ | [![DOI](https://zenodo.org/badge/273074441.svg)](https://zenodo.org/badge/latestdoi/273074441) |
| __Other Information__ | [![GitHub](https://img.shields.io/github/license/cicirello/Chips-n-Salsa)](https://github.com/cicirello/Chips-n-Salsa/blob/master/LICENSE) [![style](https://img.shields.io/badge/style-Google%20Java%20Style-informational)](https://google.github.io/styleguide/javaguide.html) |
| __Support__ | [![GitHub Sponsors](https://img.shields.io/badge/sponsor-30363D?logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/cicirello) [![Liberapay](https://img.shields.io/badge/Liberapay-F6C915?logo=liberapay&logoColor=black)](https://liberapay.com/cicirello) [![Ko-Fi](https://img.shields.io/badge/Ko--fi-F16061?logo=ko-fi&logoColor=white)](https://ko-fi.com/cicirello) | 

## How to Cite

If you use this library in your research, please cite the following paper:

> Cicirello, V. A., (2020). Chips-n-Salsa: A Java Library of Customizable, Hybridizable, Iterative, Parallel, Stochastic, and Self-Adaptive Local Search Algorithms. *Journal of Open Source Software*, 5(52), 2448, https://doi.org/10.21105/joss.02448 .

## Overview

Chips-n-Salsa is a Java library of customizable, hybridizable, iterative, parallel, stochastic, and self-adaptive local search algorithms. The library includes implementations of several stochastic local search algorithms, including simulated annealing, hill climbers, as well as constructive search algorithms such as stochastic sampling. Chips-n-Salsa now also includes genetic algorithms as well as evolutionary algorithms more generally. The library very extensively supports simulated annealing. It includes several classes for representing solutions to a variety of optimization problems. For example, the library includes a BitVector class that implements vectors of bits, as well as classes for representing solutions to problems where we are searching for an optimal vector of integers or reals. For each of the built-in representations, the library provides the most common mutation operators for generating random neighbors of candidate solutions, as well as common crossover operators for use with evolutionary algorithms. Additionally, the library provides extensive support for permutation optimization problems, including implementations of many different mutation operators for permutations, and utilizing the efficiently implemented Permutation class of the [JavaPermutationTools (JPT)](https://jpt.cicirello.org/) library.

Chips-n-Salsa is customizable, making extensive use of Java's generic types, enabling using the library to optimize other types of representations beyond what is provided in the library. It is hybridizable, providing support for integrating multiple forms of local search (e.g., using a hill climber on a solution generated by simulated annealing), creating hybrid mutation operators (e.g., local search using multiple mutation operators), as well as support for running more than one type of search for the same problem concurrently using multiple threads as a form of algorithm portfolio. Chips-n-Salsa is iterative, with support for multistart metaheuristics, including implementations of several restart schedules for varying the run lengths across the restarts. It also supports parallel execution of multiple instances of the same, or different, stochastic local search algorithms for an instance of a problem to accelerate the search process. The library supports self-adaptive search in a variety of ways, such as including implementations of adaptive annealing schedules for simulated annealing, such as the Modified Lam schedule, implementations of the simpler annealing schedules but which self-tune the initial temperature and other parameters, and restart schedules that adapt to run length.

### Table of Contents

The rest of this README is organized as follows:
* [Java Requirements](#java-requirements): Minimum supported Java version information
* [Versioning Scheme](#versioning-scheme): Explanation of the library's version numbers
* [Building the Library (with Maven)](#building-the-library-with-maven)
* [Example Programs](#example-programs): Information on example library usage
* [Java Modules](#java-modules): Information for those whose projects use Java modules
* [Importing from Package Repositories](#importing-from-package-repositories)
  * [Importing the Library from Maven Central](#importing-the-library-from-maven-central)
  * [Importing the Library from GitHub Packages](#importing-the-library-from-gitHub-packages)
  * [Importing the Library from JitPack](#importing-the-library-from-jitpack)
* [Downloading Jar Files](#downloading-jar-files): Information on where you can download pre-compiled jars
* [License](#license): Licensing information
* [Contribute](#contribute): Information for those who wish to contribute

## Java Requirements

Chips-n-Salsa currently supports Java 17+. Our development process utilizes OpenJDK 17, and
all jar files released to Maven Central, GitHub Packages, and GitHub Releases are built for a Java 17 target.
See the following table for a mapping between library version and minimum supported Java version.

| version | Java requirements |
| --- | --- |
| 5.x.y to 6.x.y | Java 17+ |
| 3.x.y to 4.x.y | Java 11+ |
| 1.x.y to 2.x.y | Java 8+ |

## Versioning Scheme

Chips-n-Salsa uses [Semantic Versioning](https://semver.org/) with 
version numbers of the form: MAJOR.MINOR.PATCH, where differences 
in MAJOR correspond to incompatible API changes, differences in MINOR 
correspond to introduction of backwards compatible new functionality, 
and PATCH corresponds to backwards compatible bug fixes.

## Building the Library (with Maven)

The Chips-n-Salsa library is built using Maven. The root of the
repository contains a Maven `pom.xml`.  To build the library, 
execute `mvn package` at the root of the repository, which
will compile all classes, run all tests, run javadoc, and generate 
jar files of the library, the sources, and the javadocs.  In addition
to the jar of the library itself, this will also generate a
jar of the library that includes all dependencies.  The file names
make this distinction explicit.  All build outputs will then
be found in the directory `target`.

To include generation of a code coverage report during the build,
execute `mvn package -Pcoverage` at the root of the repository to 
enable a Maven profile that executes JaCoCo during the test phase.

## Example Programs

There are several example programs available in a separate repository:
[cicirello/chips-n-salsa-examples](https://github.com/cicirello/chips-n-salsa-examples).
The examples repository contains example usage of several of the 
classes of the library. Each of the examples contains detailed
comments within the source code explaining the example. Running the 
examples without reading the source comments is not advised.   

## Java Modules

This library provides a Java module, `org.cicirello.chips_n_salsa`. If your Java
project utilizes modules, then add the following to your `module-info.java`:

```Java
module your.module.name.here {
	requires org.cicirello.chips_n_salsa;
}
```

The `org.cicirello.chips_n_salsa` module, in turn, transitively requires
the `org.cicirello.jpt` module because the functionality for optimizing
permutational problems uses the Permutation class as both parameters and
return types of methods. 

If you are directly utilizing the functionality of the dependencies, then 
you may instead need some combination of the following:

```Java
module your.module.name.here {
	requires org.cicirello.chips_n_salsa;
	requires org.cicirello.jpt;
	requires org.cicirello.rho_mu;
	requires org.cicirello.core;
}
```

## Importing from Package Repositories

Prebuilt artifacts are regularly published to Maven Central, GitHub Packages, and JitPack. In most
cases, you'll want to use Maven Central. JitPack may be useful if you want to build your project against
the latest unreleased version, essentially against the default branch of the repository, or a specific commit.
Releases are published to JitPack and GitHub Packages mainly as a fall-back in the unlikely scenario that
Maven Central is unavailable.

### Importing the Library from Maven Central

Add this to the dependencies section of your pom.xml, replacing 
the version number with the version that you want to use.

```XML
<dependency>
  <groupId>org.cicirello</groupId>
  <artifactId>chips-n-salsa</artifactId>
  <version>6.0.0</version>
</dependency>
```

### Importing the Library from GitHub Packages

If you'd prefer to import from GitHub Packages, rather than Maven Central, 
then: (1) add the dependency as indicated in previous section above, and (2) add 
the following to the repositories section of your pom.xml:

```XML
<repository>
  <id>github</id>
  <name>GitHub cicirello Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/cicirello/Chips-n-Salsa</url>
</repository>
```

Note that GitHub Packages requires authenticating to GitHub (even for public artifacts). Thus, it 
is likely less convenient than importing from Maven Central. We mainly provide this option as a backup 
source of artifacts.

### Importing the Library from JitPack

You can also import from JitPack. As above, you need to first add JitPack to
the repositories section of your pom.xml, such as:

```XML
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

JitPack works a bit differently than Maven Central. Specifically, JitPack builds
artifacts on-demand from the GitHub repository the first time a version is requested. We have
configured our domain on JitPack for the groupId, so you can still specify the dependency 
as (just replace `x.y.z` with the version that you want):

```XML
<dependency>
  <groupId>org.cicirello</groupId>
  <artifactId>chips-n-salsa</artifactId>
  <version>x.y.z</version>
</dependency>
```

We have primarily configured JitPack as a source of SNAPSHOT builds. If you want to build
your project against the latest commit, specify the dependency as:

```XML
<dependency>
  <groupId>org.cicirello</groupId>
  <artifactId>chips-n-salsa</artifactId>
  <version>master-SNAPSHOT</version>
</dependency>
```

You can also build against a specific commit using the commit hash as the version.


## Downloading Jar Files

If you don't use a dependency manager that supports importing from Maven Central,
or if you simply prefer to download manually, prebuilt jars are also attached to 
each [GitHub Release](https://github.com/cicirello/Chips-n-Salsa/releases).

In addition to the regular jar of the library, we also regularly publish 
a `jar-with-dependencies`. The `jar-with-dependencies` 
does not contain any module declarations (unlike the regular jar file). Therefore, the
`jar-with-dependencies` should only be used on the classpath.

## License

The Chips-n-Salsa library is licensed under the [GNU General Public License 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

## Contribute

If you would like to contribute to Chips-n-Salsa in any way, such 
as reporting bugs, suggesting new functionality, or code contributions 
such as bug fixes or implementations of new functionality, then start 
by reading 
the [contribution guidelines](https://github.com/cicirello/.github/blob/main/CONTRIBUTING.md).
This project has adopted 
the [Contributor Covenant Code of Conduct](https://github.com/cicirello/.github/blob/main/CODE_OF_CONDUCT.md).
