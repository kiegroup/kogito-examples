# Event-Driven PMML Spring Boot Example

## Description

This example demonstrates the capability of the _Kogito Event-Driven Predictions AddOn_: to enable, when included as dependency of a simple service containing
PMML files, to trigger evaluations of its queries and receive the corresponding results via specific CloudEvents.

The source and destination of these events are two configured Kafka topics.

The main goal behind the addon is to allow Kogito PMML services to be used as part of an event processing pipeline.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.1+ installed
  - [Docker Engine](https://docs.docker.com/engine/) and [Docker Compose](https://docs.docker.com/compose/) installed

### Enable The AddOn

Like the other Kogito AddOns, the only required step to enable it is to include it as dependency in the [POM file](pom.xml):

```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>kogito-addons-springboot-event-predictions</artifactId>
</dependency>
```

The version is implicitly derived from the `kogito-bom` included in the `dependencyManagement` section.

### Configuration

The only configuration required is for the input and output topics.

Here is the important section of [application.properties](src/main/resources/application.properties):

```properties
kogito.addon.cloudevents.kafka.kogito_incoming_stream=<input_topic_name>
kogito.addon.cloudevents.kafka.kogito_outgoing_stream=<output_topic_name>
kogito.addon.tracing.decision.kafka.bootstrapAddress=<kafka_bootstrap_address>
spring.kafka.bootstrap-servers=<kafka_bootstrap_address>
spring.kafka.consumer.group-id=<group_id>
```

Insert the value you need in `<kafka_bootstrap_address>`, `<group_id>`, `<input_topic_name>` and `<output_topic_name>`. Pre-configured values already works if you follow this guide without changes.

### Start test Kafka instance via Docker Compose

There's a useful [docker-compose.yml](docker-compose.yml) in the root that starts a dedicated Kafka instance for quick tests.

Simply start it with this command from the root of the repo:

```
docker-compose up -d
```

Once everything is started you can check the data contained in your small Kafka instance via [Kafdrop](https://github.com/obsidiandynamics/kafdrop) at `http://localhost:9000/`.

### Compile and Run in Local Dev Mode

```
mvn clean compile spring-boot:run
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/dmn-event-driven-springboot.jar
```

or on Windows

```
mvn clean package
java -jar target\dmn-event-driven-springboot.jar
```

## Example Usage

Here is an example of a input event that triggers the evaluation of the [LinReg regression model](src/main/resources/test_regression.pmml)
included in this example. The `data` field contains the query input.

Just send this payload to the configured input topic:

```json
{
  "specversion": "1.0",
  "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
  "source": "SomeEventSource",
  "type": "PredictionRequest",
  "subject": "TheSubject",
  "kogitopmmlmodelname": "LinReg",
  "data": {
    "fld1":3.0,
    "fld2":2.0,
    "fld3":"y"
  }
}

```

And you should receive something similar to this in the output topic:

```json
{
  "specversion": "1.0",
  "id": "d54ace84-6788-46b6-a359-b308f8b21778",
  "source": "LinReg",
  "type": "PredictionResponse",
  "subject": "TheSubject",
  "kogitopmmlmodelname": "LinReg",
  "data": {
    "fld4": 52.5
  }
}
```

The `data` field contains the query output. Values of `id` fields will change, but the rest will be the same.

### Other examples

All the leaf subfolders of [the test events resource folder](src/test/resources/events) contain a pair of `input.json` and `output.json` files.

There's one for every possible variation in the structure of the input/output events supported by the addon. Feel free to try them all.