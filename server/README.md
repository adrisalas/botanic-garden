[Click here to go back!](../README.md)

## Image generation (Docker)

- Generate image:

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

## Local development

1. Set these environment variables:
   - `POSTGRES_HOST` _(ex. localhost:5432)_
   - `POSTGRES_DATABASE` _(ex. test)_
   - `POSTGRES_USERNAME` _(ex. admin)_
   - `POSTGRES_PASSWORD` _(ex. password)_
   - `PEPPER_SECRET` _(ex. secretWordForPepper)_
   - `JWT_SECRET` _(ex. secretWordForJWT)_
   - `CERT_PATH` _(ex. /etc/ssl/bgserver-cert.pem)_
   - `PRIVATE_KEY_PATH` _(ex. /etc/ssl/bgserver-key.pem)_
2. Build the app
   ```sh
   cd server && ./gradlew clean build
   ```
3. Run server

   ```sh
   ./gradlew :bootRun
   ```
