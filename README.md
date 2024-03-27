# botanic-garden
Android App for a Botanic Garden using IoT Beacons

[![Server UTs](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml/badge.svg?branch=main)](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml)

⚠️ The current status of the project is work in progress

## Local setup

1. Set these environment variables:
    - `POSTGRES_HOST`
    - `POSTGRES_DATABASE`
    - `POSTGRES_USERNAME`
    - `POSTGRES_PASSWORD`
2. Build the app
    - `cd ./server && ./gradlew clean build`
3. Run server
    - `./gradlew :bootRun`
