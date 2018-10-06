# Rest service in plain C

## Get ulfius

```
sudo apt install libulfius-dev  libmicrohttpd-dev
```

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
