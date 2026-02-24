# Secure Random - Angelos Project™
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f91c16d5b7c349f0972e2402b1ba8033)](https://app.codacy.com/gh/angelos-project/angelos-project-secrand/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Coverage Status](https://coveralls.io/repos/github/angelos-project/angelos-project-secrand/badge.svg?branch=main)](https://coveralls.io/github/angelos-project/angelos-project-secrand?branch=main)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/angelos-project/angelos-project-secrand/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/angelos-project/angelos-project-secrand/tree/main)

The `angelos-project-secrand` package is written with the purpose to offer full freedom and insight in entropy and random data generation.
It is written without any external dependencies, except the Kotlin monotonic clock from `stdlib`, and is designed for any Kotlin/Multiplatform project or compatible use.

The package offers several out of the box random generators:

* `SecureRandom` - a ready to use secure random generator.
* `Uuid` - a UUID generator only dependent on the underlying `SecureFeed` generator, offering raw UUID and UUIDv4.
* `GarbageGarbler` - a secure random generator freely reseedable with any given entropy. Requires reseeding every 1 Gb of generated data.

Other utility classes or functions for internal or 3rd party use:

* `AbstractSponge*` - six sponges of 256, 512 and 1024 bits visible state to be used for most cryptographic use, with absorb and squeeze capabilities.
* `JitterEntropy` - a jitter based random generator building on the monotonic clock.
* `BenchmarkTester` - a utility framework for benchmarking data samples, includes Monte Carlo and Avalanche Effect testers.
* `BitStatistic` - a runtime cryptographic health checker, for detecting bias in small samples between 1-32 Kb.

## Usage

For use in a Kotlin/Multiplatform project, or compatible, publish the library to your own local maven repository.

1. Run `./gradlew publishToMavenLocal`
2. Add the dependency `org.angproj.sec:angelos-project-secrand:X.Y.Z`
3. Replace `X.Y.X` with version number in `library/build.gradle.kts`

## Tests

Run `./gradlew clean build allTests` for unit tests.


## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository.

## Getting involved

If you want to contribute to the project, please read the projects:

* [Mission statement](https://github.com/angelos-project/.github/blob/master/profile/README.md)
* [Contributor License Agreement (CLA)](https://github.com/angelos-project/.github/blob/master/misc/ADMISSION.md)
* [Code of conduct](https://github.com/angelos-project/.github/blob/master/docs/CODE_OF_CONDUCT.md)
* [Contributing guidelines](https://github.com/angelos-project/.github/blob/master/docs/CONTRIBUTING.md)

We welcome contributions of all kinds, including bug fixes, new features, and documentation improvements. please fork the repository and submit a pull request with your updates.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.