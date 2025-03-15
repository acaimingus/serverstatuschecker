# serverstatuschecker

A little Spring Boot web application I wrote to easily check if my screens are running without having to TTY into my server.

## Usage

### Requirements

- Java 23
- A file specifying the PIDs and names you wish to be monitored
    - The file is evaluated in line pairs
    - Uneven lines are names you want to give to the process on the webpage
    - Even lines are the process PIDs
    - The start script will ask for the path to this file on start-up

### Running in HTTPS mode

To run in HTTPS mode, do the following:

- Create a SSL certificate for your domain (not covered here)
- You should now have a .key and a .crt file, maybe another .crt file from your certificate authority too
- Convert your certificate to a P12 keystore with the command:
    ```
    openssl pkcs12 -export -inkey selfsigned.key -in selfsigned.crt -certfile ca_bundle.crt -out keystore.p12 -name mycert
    ```
    - If your certificate is not self-signed, but has a CA certificate with it, use:
    ```
    openssl pkcs12 -export -inkey certificate.key -in certificate.crt -certfile ca_bundle.crt -out keystore.p12 -name mycert
    ```
    - You will be asked for a password to the keystore
- Setup the app by specifying the path, password and key name (leave the type as it is):
```
server.ssl.key-store=path/to/keystore/file.p12
server.ssl.key-store-password=KEYSTORE-PASSWORD
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=KEY-NAME
```

- To start the status checker, open the bin directory and run `start.sh` as sudo (HTTPS binding needs it). The script will ask you for the path to the PID file and then launch the site.

### Running in HTTP mode

To run in HTTP mode, do the following:
- Remove the following lines from `application.properties`:
```
server.ssl.key-store=path/to/keystore/file.p12
server.ssl.key-store-password=KEYSTORE-PASSWORD
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=KEY-NAME
```
- Change the port from `443` in `application.properties` to another one (for example `8080`)
- Start the script without sudo (not needed here). The script will ask you for the path to the PID file and then launch the site.

## Generating the PID list

My use case for this program was to monitor my servers running through the UNIX screen window manager. For that use case there's a script `generatepidlist.sh`, which takes all detached screens and puts them into a file called `pidlist.txt`.

## Example

![Website screenshot](example.png)