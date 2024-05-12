[Click here to go back!](../README.md)

## Statics generation

1. Install Angular CLI:

   ```sh
   npm install -g @angular/cli
   ```

2. Generate statics:

   ```sh
   ng build
   ```

3. Copy the files inside `./dist/bg-admin-app/browser/` into your server

## Local development

1. Modify `./src/proxy.conf.json` to point to your backend server

2. Run the development server:

   ```sh
   ng serve -o
   ```
