# Using java sparkjava web framework for performance test

## Compile

```
mvn clean package
```

## Build native image

```
native-image -cp target/java-rest-service-1.0-SNAPSHOT-jar-with-dependencies.jar  -H:Name=helloworld -H:Class=net.mixaal.poc.java.Hello -H:+JNI
./helloworld
```

## Test performance

```
ab -n 1000 -c 4 http://localhost:4567/hello
```
