# Rest service in plain C

## Get ulfius

On Ubuntu >= 17.10:

```
sudo apt install libulfius-dev  libmicrohttpd-dev
```

Or you can do the manual installation of ulfius:

https://github.com/babelouest/ulfius/blob/master/INSTALL.md#manual-install

## Compile

```
gcc -Wall -O2 rest-service.c -o rest-service -lulfius
```

## Run

```
./rest-service
```

## Test performance

```
ab -n 1000 -c 4 http://localhost:8080/helloworld

```
