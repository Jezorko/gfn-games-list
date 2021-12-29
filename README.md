[![Master Build Status](https://img.shields.io/github/checks-status/Jezorko/gfn-games-list/master?label=build&logo=github)](https://github.com/Jezorko/gfn-games-list/actions?query=branch%3Amaster)
[![Heroku Deployment](https://img.shields.io/github/workflow/status/Jezorko/gfn-games-list/Deploy%20application%20to%20Heroku?label=deployment&logo=heroku)](https://gfn-games.herokuapp.com/)
[![License: WTFPL](https://img.shields.io/badge/License-WTFPL-red.svg)](http://www.wtfpl.net/txt/copying/)

# GeForce NOW Games List

This application is an alternative to [geforcenow-games.com](https://geforcenow-games.com/).

It was built as a pet-project with main goal of learning Kotlin Multiplatform setup.

## Running

### Locally

The application can be run locally using Gradle.

```shell
./gradlew run
```

Local execution requires a Docker environment (for Postgres database setup with Testcontainers).

### Production

Build the production files using either `stage` or `installDist` command.

```shell
./gradlew stage
```

Then execute the generated binaries.

```shell
build/install/gfn-games-list/bin/gfn-games-list
```

## Configuration

The configuration is defined by the [Configuration](src/jvmMain/kotlin/jezorko/github/gfngameslist/shared/Configuration.kt) object.
Configuration parameters are fetched from environment variables.