# botanic-garden

Android App for a Botanic Garden using IoT Beacons

[![Server UTs](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml/badge.svg?branch=main)](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml)

⚠️ The current status of the project is work in progress

## Local setup

1. Generate a PKCS#12 certificate and store it somewhere:

   ```sh
   openssl req -newkey rsa:409 -new -nodes -x509 -days 365000 -keyout "bgserver-key.pem" -out "bgserver-cert.pem" -addext "subjectAltName=DNS:${fill_this},IP:${fill_this}"
   ```

   ```sh
   openssl pkcs12 -export -out "bgserver.p12" -inkey "bgserver-key.pem" -in "bgserver-cert.pem"
   ```

2. Set these environment variables:
   - `POSTGRES_HOST` _(ex: localhost:5432)_
   - `POSTGRES_DATABASE`
   - `POSTGRES_USERNAME`
   - `POSTGRES_PASSWORD`
   - `PKCS12_PATH` _(ex: /etc/certs/bgserver20240101.p12)_
   - `PKCS12_PASSWORD`
   - `JWT_KEY`
3. Build the app
   ```sh
   cd server && ./gradlew clean build
   ```
4. Run server
   ```sh
   ./gradlew :bootRun
   ```

## Docker

1. Generate docker image:

   ```sh
   ./gradlew bootBuildImage --imageName=salastroya/bgserver
   ```

   - Save image:

     ```docker
     docker save salastroya/bgserver | gzip > bgserver.tar.gz
     ```

   - Load image:
     ```docker
     docker load < bgserver.tar.gz
     ```

1. Obtain your SSL certificate from a trusted CA

1. Run docker:
   - Via docker run:
   ```sh
   sudo docker run -d -p 8080:8080 -e POSTGRES_HOST=${fill_this} -e POSTGRES_DATABASE=${fill_this} -e POSTGRES_USERNAME=${fill_this } -e POSTGRES_PASSWORD=${fill_this} -e PKCS12_PATH=${fill_this} -e PKCS12_PASSWORD=${fill_this} -e JWT_KEY=${fill_this} -v ${fill_this}:${fill_this} --name bgserver salastroya/bgserver
   ```
   - Via a `docker-compose.yml`
   ```docker
   name: bgserver
   services:
       bgserver:
           container_name: bgserver
           ports:
               - 8080:8080
           environment:
               - POSTGRES_HOST=${fill_this}
               - POSTGRES_DATABASE=${fill_this}
               - POSTGRES_USERNAME=${fill_this}
               - POSTGRES_PASSWORD=${fill_this}
               - PKCS12_PATH=${fill_this}
               - PKCS12_PASSWORD=${fill_this}
               - JWT_KEY=${fill_this}
           volumes:
               - ${fill_this}:${fill_this}
           image: salastroya/bgserver
   ```
   ```sh
   sudo docker compose up -d
   ```
