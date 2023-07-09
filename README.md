![Unit Tests](https://github.com/dendriel/rozsa-events-collector/actions/workflows/gradle.yml/badge.svg)
# Rozsa Events Collector

Rozsa Events Collector is a **spring-based library** that easies the **collection** and **transmission** of events in an execution flow.

The basic usage of this library is just to mark target elements from an execution flow. When this flow is called then the
expected data will be collected and submitted for a remove server of choice as a map o objects. The submission behavior
may be replaced by your custom logic if necessary.

To avoid cluttering the business code with observability related functionalities, the library provides an annotation
based approach for marking methods, parameters and fields for collection without any further changes in the main
algorithm. Although if desired, it is possible to collect data by explicitly calling the collector.

**This doc is under construction. You may refer to the [project draft](doc/DRAFT.md) for more information.**

## Terms

- **Data** - a single element that means something for the specific flow in observation;
- **Event** - a composition of many pieces of data that have a deeper meaning when grouped together;
- **Collect** - the action of storing data from an execution flow;
- **Submit** - the action of generating an event from collected data and submitting it to a remote server.

## Features

- Easily **start**, **collect** and **finish** the collection of data by using the `EventsCollectorManager`
- Keep observability operations out of business code by using **Annotations**
- Control collection behavior via **configuration** or by bean **overriding**
- Start using the library by just adding it to the project dua to its **auto-configuration**
- Allows to define and collect from **multiple flows**

## How To

Its is possible to collect data by accessing the `EventsCollectorManager` directly in your code or via collection `Annotations`.

### Collection Annotations

The following annotations are available:

- `@BeginCollecting` - Must be used to mark the start of a collection flow. The collection flow will automatically finish
- when the starting method returns
- `@Collect` - Mark a method for collection of its parameters
- `@CollectParameter` - Collect data from a parameter (only parameters marked with this annotation will be collected)
- `@CollectField` - Collect data from a field from an object
- `@CollectReturn` - Collect data from the return value from a method
- `@FinishCollecting` - May be used to finish a collection flow earlier (thus, this is optional)


### @BeginCollecting

`scope: method`

This annotation is mandatory to start a collection flow. If the flow isn't started, no data will be collected even if
other annotations were used.

```Java
    @BeginCollecting
    public ResponseEntity<PetResponse> getPetByName(String name) {
        ...
    }
```

#### Options

- `flow` -  it is possible to name the flow, so it won't collide with any other collection flows. It also allows to define custom flow
configuration.
- `submitOnError` - allows to submit event data even if the method returns with an exception (default: true)
```Java
    @BeginCollecting(flow = "xpto-flow-name", submitOnError = false)
```

### @Collect

`scope: method`

Use to inform that a method has parameters to be collected. Only methods marked with `@Collect` will be scanned for collection.

```java
    @Collect
    ResponseEntity<OwnerResponse> getOwner(Long id) {
        ...
    }
```

#### Options

- `flow` -  if defined, it will be used as the collection flow when collecting parameters (unless the CollectParameter itself defines its flow).
```java
    @Collect(flow = "foo-bar")
```

### @CollectParameter

`scope: parameter`

Defines that a parameter has to be collected. It will be read only if the target method is annotated with `@Collect`. The
collection will automatically finish when returning from the method marked with this annotation. It's possible to finish
earlier by using the [@FinishCollecting](#finishcollecting) annotation.
```java
    @Collect
    public Optional<Pet> getByName(
            @CollectParameter("petName") final String name
        ) {
        ...
    }
```

The `@CollectParameter` may receive an optional string that will be used as the key of this parameter. If no key is specified,
the parameter variable name will be used as key.

*Specifying a key is _optional_ but **recommended** as good practice (anyone may change the variable name for any reason,
but changing the key name is harder to pass by a core review).

#### Options

- `flow` - if defined, it will be used as the collection flow. If not defined, will use the flow defined by `@Collect` or
`default`
- `key` - string to be used as the collection key
- `collector` - if this is a complex object, it's possible to define the name of custom bean to do the collection logic. Check
the [Custom Collector](#custom-collector) section for how to define a custom collector
- `scanFields` - Instead of using the own parameter as the collect value, look for a field from this parameter marked with `@CollectField`.
Won't take effect if defined within a custom collector.

Configure to `scanFields` from target parameter.
```java
    @Collect
    public Optional<Pet> findPetByFilters(
            @CollectParameter(scanFields = true) final PetFilter filter
        ) {
        ...
    }
```

Overrides `@Collect` defined flow.
```java
    @Collect(flow = "xpto")
    public Optional<Pet> getByName(
        @CollectParameter(flow = "pet_flow", key = "petName") final String name
    ) {
    ...
    }
```

Defines a custom `collector`.
```java
    @Collect
    public Optional<Pet> findPetByFilters(
            @CollectParameter(collector = 'pet_filter_collector') final PetFilter filter
        ) {
        ...
    }
```

### @CollectField

`scope: field`

When using `scanFields` in a `@CollectParameter`, use `@CollectField` to mark fields from an object to be collected. Only
fields marked with this annotation will be collected.

```java
public class Owner {
    private Long id;

    @CollectField("owner_name")
    private String name;

    @CollectField("owner_age")
    private Integer age;

    private Gender gender;
}
```

#### Options

- `flow` - if defined, it will be used as the collection flow. If not defined, will use the flow defined by `@CollectParameter` or
`@Collect` annotations
- `key` - string to be used as the collection key
- `scanFields` - Instead of using the own parameter as the collect value, look for a field from this parameter marked with `@CollectField`.
Won't take effect if defined within a custom collector.

```java
public class Owner {
    @CollectField(flow = "xpto-flow", scanFields = true)
    private Pet favouritePet;
}
```

### @CollectReturn

`scope: method`

Use this annotation to collect data from the returning object from a method.
```java
    @CollectReturn("pet_id")
    public Long createPet(PetRequest petRequest) {
        ...
        }
```

It automatically unwraps the value if using `Optional<?>` before collecting. So, this is also valid.
```java
    @CollectReturn("pet_id")
    public Optional<Long> createPet(PetRequest petRequest) {
        ...
        }
```

#### Options

- `flow` - collection flow.
- `key` - string to be used as the collection key
- `collector` - if this is a complex object, it's possible to define the name of custom bean to do the collection logic. Check
  the [Custom Collector](#custom-collector) section for how to define a custom collector
- `scanFields` - Instead of using the own parameter as the collect value, look for a field from this parameter marked with `@CollectField`.
  Won't take effect if defined within a custom collector.

```java
    @CollectReturn(flow = PET_FLOW, collector = "pet_response_entity_collector")
    public ResponseEntity<PetResponse> findPetByFilter(String name, String color, Integer age, PetType type) {
        ...
        }
```

*`@CollectReturn` only auto-unwrapp from `Optional<?>` type. No other wrapper types are auto-handled right now. In this cases it is
recommended to use a [Custom Collector](#custom-collector).

### @FinishCollecting

`scope: method`

Use this annotation to finish a collection flow earlier. It will end the flow and submit event data after returning from
a method marked by this annotation.

```java
    @FinishCollecting(flow = "xpto")
    public Long create(final Pet pet) {
        ...
    }
```
#### Options

- `flow` - collection flow to be finished earlier.

### Collect via EventsCollectorManager

The `EventsCollectorManager` is the brain of the collection process and the annotations are a AOP-based layer that abstracts the usage
and inclusion of this manager directly on your code.

If you need to collect event data 'by hand' you can use inject the `EventsCollectorManager` in your code and use the following:

- `begin(String flow)` - initialize a collection flow
- `clear(String flow)` - clear all data from a collection flow (also ends the flow)
- `collect(String flow, String key, Object value)` - collect data
- `submit(String flow)` - submit data from a flow (also ends the flow)
- `getCollection(String flow)` - get data collected from a flow


## Custom Collector

Custom collectors are a way to define a customized logic to collect data. It can be used to handle complex objects or
transformations that can't be collected by simply using the `scanFields` and `@CollectiField` features.

To implement a custom collector, your have to provide a bean that implements a [ObjectCollector](/lib/src/main/java/com/rozsa/events/collector/api/ObjectCollector.java).
The bean receives the collection `flow`, `source` object for collection and the `EventsCollectorManager` so the collection
takes place. See the [Collect via EventsCollectorManager](#collect-via-eventscollectormanager) section for information about
using the manager for data collection.

The example code bellow defines a custom collector that is able to handle a `ResponseEntity<PetResponse>` and collect data from it.
```java
    @Bean("pet_response_entity_collector")
    public ObjectCollector petResponseCollector() {
        return (String flow, Object source, EventsCollectorManager eventsCollectorManager) -> {
            if (source instanceof ResponseEntity<?> target) {
                if (target.getStatusCode() != HttpStatus.OK) {
                    return;
                }

                if (target.getBody() instanceof PetResponse petResponse) {
                    eventsCollectorManager.collect(flow, PetFilterFlowKeys.RESPONSE_NAME, petResponse.getName());
                }
            }
        };
    }
```

*You may include and use any other beans to help in the collection and collection as many data from the source object as needed.

To use the custom collector, just set the collector bean name in the annotations that allows the `collector`. For instance,
```java
    @CollectReturn(flow = "pet_response_flow", collector = "pet_response_entity_collector")
    public ResponseEntity<PetResponse> findPetByFilter(String name, String color, Integer age, PetType type) {
        ...
        }
```

## Events ID Generator

When using `@BeginCollecting` to start a flow, an ID is auto-generated and added to the event data. The Events ID Generator
is responsible for doing that.

The default implementation of this component provides a `UUID` by using the java package `java.util.UUID.randomUUID`. This way,
the default auto-generated key implementation will provide an ID entry in the following format:

`{ "id": "e72f0041-ffb7-4f9d-9357-455249615c08" }`

The Events ID Generator may be overridden by providing a bean of type [EventsIdGenerator](/lib/src/main/java/com/rozsa/events/collector/api/EventsIdGenerator.java).


## Events Submitter

The events submission is the final part of the collection process. The Events Submitter is the component in charge of packing and
submitting the collected event data to the remote server.

The default implementation of the Events Submitter is an HTTP submitter that packs the event data and `POST` it as a JSON
body to the remoter server in the following format:

````http request
POST /collect HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: */*
x-flow: pet_flow
Content-Length: 99

{
	"id": "e72f0041-ffb7-4f9d-9357-455249615c08",
	"field-a": "DOG",
	"age": 6,
	"color": "Yellow"
}
````


## Configurations

Configurations allows to customize the collection behavior for each flow/type of event you want to observe. The following
may be configured via `application.yml` or `application.properties`.

- `submit-endpoint` - the endpoint of the remote serve in which events will be posted (default: `http://localhost:8080/collect`)
- `event-id-key` - the key name for the auto-generated id in the events (default: `id`)
- `event-header` - the name of the auto-inserted header with the event flow name  (default: `x-flow`)

The library `general` configuration is defined inside the `rozsa.events-collector` hierarchy:

```yml
rozsa:
  events-collector:
    submit-endpoint: 'http://localhost:8080/collect'
    event-id-key: 'custom-id'
    event-header: 'x-flow-custom'
```

You may also define the same configuration above for each one of your custom flows. To do that, define a field named `flows`,
the custom flow name and its properties, as follows:

```yml
rozsa:
  events-collector:
    submit-endpoint: 'http://localhost:${wiremock.server.port}/collect'
    event-id-key: 'custom-id'
    event-header: 'x-flow-custom'
    flows:
      flowAStar:
        submit-endpoint: 'http://localhost:${wiremock.server.port}/collect/flow-a'
        event-id-key: 'flow_a_key'
        event-header: 'x-events-collection'
      flowB:
        submit-endpoint: 'http://localhost:${wiremock.server.port}/collect/flow-b'
```

When defining custom flows configuration, the logic to follow is that you are overriding the general configuration. Thus,
if a field is not defined in the custom flow configuration, the general configuration will be used as fallback. For example:

```yml
rozsa:
  events-collector:
    submit-endpoint: 'http://localhost:${wiremock.server.port}/collect'
    event-id-key: 'custom-id'
    event-header: 'x-flow-custom'
    flows:
      flowB:
        submit-endpoint: 'http://localhost:${wiremock.server.port}/collect/flow-b'
```

We have defined a custom flow named `flowB` that defines only the `submit-endpoint`. This way, the `event-id-key` and `event-header`
values will fall back to the general configuration values `custom-id` and `x-flow-custom` respectively. If no general configuration is
defined, the default configuration will be used (`id` and `x-flow`).


## TODO

- Add tests for flow name overriding
- Add reflection caching
- Allow to capture the same field in multiple flows
- Create final documentation

## NTH
- Allow to define reference values for the event (static key-value pairs in the BeginCollecting Annotation)
- Allow simple operations over sets (like counting elements)
- Allow to declare default flow name at class level
- Define a custom collector for CollectField