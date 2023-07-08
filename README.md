![Unit Tests](https://github.com/dendriel/rozsa-events-collector/actions/workflows/gradle.yml/badge.svg)
# Rozsa Events Collector

The aim of this project is to provide a **library** to be used in collecting data from the program execution flow. The
captured data is then published as an event to a remote server.

To avoid cluttering the business code with observability related functionalities, the library provides an annotation
based approach for marking methods, parameters and fields for collection without any further changes in the main
algorithm (although if desired is possible to collect data by explicitly calling the collector).

**This doc is under construction. You may refer to the [project draft](doc/DRAFT.md) for more information.**

## Terms

- **Data** - a single element that means something for the specific flow in observation
- **Event** - a composition of many pieces of data that have a deeper meaning when grouped together.
- **Collect** - the action of storing data from an execution flow
- **Submit** - the action of generating and submitting an event to a remote server


## TODO

- Start documentation
- Add reflection caching
- Allow to capture the same field in multiple flows
- Create final documentation

## NTH
- Allow to define reference values for the event (static key-value pairs in the BeginCollecting Annotation)
- Allow simple operations over sets (like counting elements)
- Allow to declare default flow name at class level