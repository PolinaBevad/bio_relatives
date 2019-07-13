# bio_relatives
[![Build Status](https://travis-ci.com/PolinaBevad/bio_relatives.svg?branch=master)](https://travis-ci.com/PolinaBevad/bio_relatives)
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/samtools/PolinaBevad/bio_relatives)

Program, which determines the relationship between people at the level of mother / father / child.

## Table of Contents
-   [Requirements](#Requirements)
-   [Installing](#Installing)
-   [Building](#Building)
    -   [Creation of the project in IntelliJ](#Creation-of-the-project-using-IntelliJ)
-   [Running integration tests](#Running-integration-tests)
-   [Usage](#Usage)
    -   [Options](#Options)
    -   [Examples](#Examples)
        -   [Comparison of the genomes of two persons](#Comparison-of-the-genomes-of-two-persons)
        -   [Comparison of the genomes of three persons](#Comparison-of-the-genomes-of-three-persons)
        -   [Comparison of the genomes with intermediate output](#Comparison-of-the-genomes-with-intermediate-output)
-   [Maintainers](#Maintainers)
-   [Contributing](#Contributing)
-   [License](#License)

## Requirements
* To run bio_relatives project:
    * Java 8 JDK or higher
* To build bio_relatives project:
    * Java 8 JDK or higher
    * Git 2.5 or higher
    * Gradle 3.1 or higher. We recommend using the `./gradlew` script, which will
          download and use an appropriate gradle version automatically (see examples below).
## Installing

You can download the latest version of the project from [github releases page](https://github.com/PolinaBevad/bio_relatives/releases).

## Building

bio_relatives project can be built using [gradle](http://gradle.org/).

A wrapper script `./gradlew` will download the appropriate version of the gradle on the first invocation.

Examples of gradle usage from the bio_relatives root directory:
 - compile and build a jar 
 ```
 ./gradlew
 ```
 or
 ```
 ./gradlew jar
 ```
 The jar file will be in `build/libs/bio_relatives-\<version\>.jar`. Version is based on the current git commit.

 - run tests
 ```
 ./gradlew test
```
 - clean project directory
 ```
 ./gradlew clean
 ```

 - build a jar file that includes all of bio_relatives's dependencies
 ```
 ./gradlew shadowJar
 ```
 
 - create a snapshot and install it into your local maven repository
 ```
 ./gradlew install
 ```

 - for an exhaustive list of all available targets
 ```
 ./gradlew tasks
 ```

### Creation of the project using IntelliJ
To create the project in IntelliJ IDE for bio_relatives do the following:

1. Select fom the menu: `File -> New -> Project from Existing Sources`
2. In the resulting dialog, chose `Import from existing model`, select `Gradle` and `Next`
3. Choose the `default gradle wrapper` and `Finish`.

## Running integration tests
To run the integration tests, go to the `src/test/bash/`. Then, run the script `integration_test.sh` the usual way(the script does not require any arguments): `./integration_test.sh`.
The output of each test can be found in the `src/test/bash/output/` folder. You can see the expected test results in the `src/test/bash/expected/` folder. You can see the difference between the test and expected data in the `src/test/bash/output` 

## Usage
    java -jar bio_relatives.jar [-h | --help] [-io | --intermediateOutput][-c2 | --compare2 <first> <second> <bed>] [-c3 | --compare3 <father> <mother> <son> <bed>] [-m | --mode <L | XY>] [-th | --threadsNumber <number>]
### Options

`-h`, `--help` - show help message.

`-io`, `--intermediateOutput` - key, which enables intermediate results output.

`-c2`, `--compare2` - compare genomes of two persons.

`-c3`, `--compare3` - compare genomes of three persons (father/mother/son).

`-m`, `--mode` - defines which comparator will be used.

`-th`, `--threadsNumber` - defines number of threads that should be created to process the information analysis.

### Examples
#### Comparison of the genomes of two persons
```
java -jar bio_relatives.jar --compare2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed
```
#### Comparison of the genomes of three persons
```
java -jar bio_relatives.jar --compare3 ~/path/to/father.bam ~/path/to/mother.bam ~/path/to/son.bam ~/path/to/file.bed
```
#### Comparison of the genomes with intermediate output
```
java -jar bio_relatives.jar -io -c2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed
```
## Maintainers
-   [Polina Bevad](https://github.com/PolinaBevad)
-   [Sergey Hvatov](https://github.com/SHvatov)
-   [Vladislav Marchenko](https://github.com/MarchenkoVladislav)
## Contributing

Please read [Contributing.md](CONTRIBUTING.md) for more details about our code of conduct, and the process of submitting pull requests to us.

## License
This project is licenced under the terms of the [MIT](LICENSE) license.
