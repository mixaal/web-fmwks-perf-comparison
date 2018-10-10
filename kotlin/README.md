# Using Koltin Restful Service on GraalVM 

## Download GraalVM

Here's the github repository for GraalVM: https://github.com/oracle/graal/releases

I used the following version:

```
wget https://github.com/oracle/graal/releases/download/vm-1.0.0-rc7/graalvm-ce-1.0.0-rc7-linux-amd64.tar.gz

```

## Setup GraalVM as your JVM

```
cd Downloads
tar xzvf graalvm-ce*tar.gz
export JAVA_HOME=/home/mikc/Downloads/graalvm-ce-1.0.0-rc7/
export PATH="$JAVA_HOME/bin:$PATH"
```

Make sure that `java -vesion` reads:

```
openjdk version "1.8.0_172"
OpenJDK Runtime Environment (build 1.8.0_172-20180625212755.graaluser.jdk8u-src-tar-g-b11)
GraalVM 1.0.0-rc7 (build 25.71-b01-internal-jvmci-0.48, mixed mode)
```

## Compile project

```
mvn clean package
```

## Create native image

```
native-image -cp ./target/kotlin-rest-service-1.0-SNAPSHOT-jar-with-dependencies.jar -H:Name=kotlin-rest -H:Class=net.mixaal.poc.kotlin.HelloKt  -H:+JNI  -H:ReflectionConfigurationFiles=$(pwd)/*json
```

## Run and setup the service

Run: `./kotlin-rest` and make sure it reads something like:

```
mikc@mikc-ws:~/git/web-fmwks-perf-comparison/kotlin$ ./kotlin-rest 
[Thread-2] WARN org.eclipse.jetty.server.AbstractConnector - Ignoring deprecated socket close linger time
[Thread-2] INFO spark.embeddedserver.jetty.EmbeddedJettyServer - == Spark has ignited ...
[Thread-2] INFO spark.embeddedserver.jetty.EmbeddedJettyServer - >> Listening on 0.0.0.0:4567
[Thread-2] INFO org.eclipse.jetty.server.Server - jetty-9.4.z-SNAPSHOT; built: 2018-08-30T13:59:14.071Z; git: 27208684755d94a92186989f695db2d7b21ebc51; jvm 1.8.0_172
[Thread-2] INFO org.eclipse.jetty.server.session - DefaultSessionIdManager workerName=node0
[Thread-2] INFO org.eclipse.jetty.server.session - No SessionScavenger set, using defaults
[Thread-2] INFO org.eclipse.jetty.server.session - node0 Scavenging every 600000ms
[Thread-2] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@392e64a9{HTTP/1.1,[http/1.1]}{0.0.0.0:4567}
[Thread-2] INFO org.eclipse.jetty.server.Server - Started @2ms
```

Create customer:

```
curl -kv -X POST 'http://localhost:4567/customers/create?name=mixaal&email=mixaal@pixaal'
curl -kv -X GET  'http://localhost:4567/customers/'
```

## Performance test

Run apache benchmark to verify the speed:

```
ab -n 1000 -c 4 http://localhost:4567/customers/
```
