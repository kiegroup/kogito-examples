# Process Springboot performance

## Description

A simple process service used to run throughput tests on Springboot. This test relies on Kafka. So to be able to run this you need to have
Kafka cluster installed and available over the network. Refer to [Kafka Apache site](https://kafka.apache.org/quickstart) to more information about how to install. 

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run in Local Dev Mode

```sh
mvn clean compile spring-boot:run
```


### Package and Run using uberjar

```sh
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```sh
java -jar target/process-performance-springboot.jar
```


## Example Usage

Once the service is up and running, to collect throughput numbers you need to manually change parameters in code of, compile and  run class ```org.kie.kogito.performance.client.MainRunner``` in process-performace-client project

