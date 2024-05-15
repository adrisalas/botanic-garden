# botanic-garden

Android App for a Botanic Garden using IoT Beacons

[![Server tests](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml/badge.svg?branch=main)](https://github.com/adrisalas/botanic-garden/actions/workflows/server-test.yml)

## Installation

1. Generate the executables/statics files for each component:

   - [Server](./server/README.md)
   - [Android](./android/README.md)
   - [Admin Panel](./web/README.md)

2. Get a PEM certificate:

   - **Option A:** Get it from a CA.
   - **Option B:** Generate a self-signed a certificate.
     ```sh
     openssl req -newkey rsa:4096 -new -nodes -x509 -days 36500 -keyout "bgserver-key.pem" -out "bgserver-cert.pem" -addext "subjectAltName=DNS:${fill_this},IP:${fill_this}"
     ```

3. Set these environment variables:

   - `POSTGRES_HOST` _(ex. localhost:5432)_
   - `POSTGRES_DATABASE` _(ex. test)_
   - `POSTGRES_USERNAME` _(ex. admin)_
   - `POSTGRES_PASSWORD` _(ex. password)_
   - `PEPPER_SECRET` _(ex. secretWordForPepper)_
   - `JWT_SECRET` _(ex. secretWordForJWT)_
   - `CERT_PATH` _(ex. /etc/ssl/bgserver-cert.pem)_
   - `PRIVATE_KEY_PATH` _(ex. /etc/ssl/bgserver-key.pem)_

4. Run the server and the web in your domain

   Feel free to run it as you want, in the folder [./.docker/](./.docker/) you can find a `nginx.conf` and a `docker-compose.yml` ready to be used.

   ```docker
   sudo docker compose up -d
   ```
