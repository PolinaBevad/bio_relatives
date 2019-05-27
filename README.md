# bio_relatives
[![Build Status][travis_badge]][travis_link]
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/samtools/PolinaBevad/bio_relatives)

Program, which determines the relationship between people at the level of mother / father / child.

## Table of Contents
-   [Requirements](#Requirements)
-   [Installing](#Installing)
-   [Building](#Building)
    -   [Creation of the project in IntelliJ](#Creation-of-the-project-using-IntelliJ)
-   [Usage](#Usage)
    -   [Options](#Options)
    -   [Examples](#Examples)
        -   [Comparison of the genomes of two persons](#Comparison-of-the-genomes-of-two-persons)
        -   [Comparison of the genomes of three persons](#Comparison-of-the-genomes-of-three-persons)
        -   [Comparison of the genomes with intermediate output](#Comparison-of-the-genomes-with-intermediate-output)
-   [Maintainer](#Maintainer)
-   [Contributing](#Contributing)
-   [Contributors](#Contributors)
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

## Usage
    java -jar bio_relatives.jar [-h | --help] [-io | --intermediateOutput][-c2 | --compare2 <first> <second> <bed>] [-c3 | --compare3 <father> <mother> <son> <bed>] 
### Options

`-h`, `--help` - show help message.

`-io`, `--intermediateOutput` - key, which enables intermediate results output.

`-c2`, `--compare2` - compare genomes of two persons.

`-c3`, `--compare3` - compare genomes of three persons (father/mother/son).

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
## Maintainer
[Polina Bevad](https://github.com/PolinaBevad)

## Contributing

Please read [Contributing.md](CONTRIBUTING.md) for more details about our code of conduct, and the process of submitting pull requests to us.

## Contributors
-   [Polina Bevad](https://github.com/PolinaBevad)
-   [Sergey Hvatov](https://github.com/SHvatov)
-   [Vladislav Marchenko](https://github.com/MarchenkoVladislav)

## License
This project is licenced under the terms of the [MIT](LICENSE) license.

[travis_link]: https://travis-ci.org/PolinaBevad/bio_relatives

[travis_badge]: https://travis-ci.org/PolinaBevad/bio_relatives.svg?branch=master