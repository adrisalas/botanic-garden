[Click here to go back!](../README.md)

## APK Generation

1. Set this environment variables:

   - `API_ADDRESS` _(ex. api.example.org)_

2. (Optional), if your server use a self-signed certificate add the public key in `./app/src/main/res/raw/cert.pem`

3. Generate APK in Android Studio:
   > Build > Build Bundle(s)/APK(s) > Build APK(s)

## Local development

1. Download an emulator or connect an android phone via USB
   > Many features of the application depend on Bluetooth scanning, to scan beacons. Therefore, the use of a emulator is not recommended.
2. Run the application from Android Studio
