# botanic-garden
Android App for a Botanic Garden using IoT Beacons

[![Server UTs](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml/badge.svg?branch=main)](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml)

⚠️ The current status of the project is work in progress

## Local setup

1. Set these environment variables:
    - `POSTGRES_HOST` _(ex: localhost:5432)_
    - `POSTGRES_DATABASE`
    - `POSTGRES_USERNAME`
    - `POSTGRES_PASSWORD`
2. Build the app
    ```sh
    cd server && ./gradlew clean build
    ```
3. Run server
    ```sh
    ./gradlew :bootRun
    ```

## Docker
1. Generate docker image:
    ```sh
    ./gradlew bootBuildImage --imageName=salastroya/bgserver
    ```
3. Run docker:
    ```docker
    sudo docker run -d -p 8080:8080 -e POSTGRES_HOST=${fill_this} -e POSTGRES_DATABASE=${fill_this} -e POSTGRES_USERNAME=${fill_this} -e POSTGRES_PASSWORD=${fill_this} --name bgserver salastroya/bgserver
    ```

- Save image:
    ```docker
    docker save salastroya/bgserver | gzip > bgserver.tar.gz
    ```

- Load image:
    ```docker
    docker load < bgserver.tar.gz
    ```
