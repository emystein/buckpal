# Example Implementation of a Hexagonal Architecture

Based on [BuckPal](https://github.com/thombergs/buckpal) with a few modifications:

* Model all locks applied during a Transaction (see SendMoneyService)
* Single port for Account repository, merging original ports LoadAccountPort and UpdateAccountStatePort.

## Prerequisites

* JDK 8+
* Gradle
