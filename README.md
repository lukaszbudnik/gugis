Gugis [![Build Status](https://travis-ci.org/lukaszbudnik/gugis.svg?branch=master)](https://travis-ci.org/lukaszbudnik/gugis) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lukaszbudnik.gugis/gugis/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.lukaszbudnik.gugis/gugis)
==============================

Lightweight and robust framework for creating composite components using Guice and Java 8 parallel streams!

Gugis automatically binds multiple implementations of the same interface to a composite component. When a call to composite component is being made Gugis automatically calls all found implementations. Gugis defines two types of implementations: primary and secondary. A typical use case would be disaster recovery where write operations (create, update, delete) would be routed to all implementations, other operations would be routed to only primaries, some to secondaries, and some operations (like read operations) may be routed to any implementation. See getting started for more information.

Gugis uses Java 8 parallel streams and... requires JVM 8 to run. If you are stuck with older versions of Java (6 and 7) there is a [Gugis-Rx](https://github.com/lukaszbudnik/gugis-rx) fork which instead of using Java 8 parallel streams uses Guava and JavaRx. However, it must be noted that Gugis-Rx is (very) much slower and does not offer all the features compared to the original Gugis project.

# Getting started

Let's assume we have a `StorageService` interface. It has two implementations `StorageService1Impl` and `StorageService2Impl`. Let's pretend that the first implementation uses Amazon S3 and the other one uses Rackspace Cloud Files.

## Primary and secondary

Gugis comes with `@Primary` and `@Secondary` annotations so that we can differentiate our implementations and theirs roles. `@Primary` can be added to `StorageService2Impl` and `@Secondary` can be added to `StorageService1Impl`. Please also note that there can be multiple implementations marked with either `@Primary` or `@Secondary`.

## Composite component

The last step is to create a composite component. For this we need to create another implementation of `StorageService` interface called `CompositeStorageService` and mark it with `@Composite` annotation. This implementation can have all methods' bodies empty - those methods will never be called. Instead Gugis will intercept all calls and route them to proper implementation(s).

How Gugis knows what method calls should be routed to what component(s)? Each method we want to be intercepted and routed needs to be marked with `@Propagate` annotation. This annotation has one parameter which can control propagation behaviour. Currently the following propagation bahaviour is supported:

* `@Propagate(propagation = Propagation.ALL)` - method call will be routed to all primaries and all secondaries
* `@Propagate(propagation = Propagation.PRIMARY)` - method call will be routed to all primaries
* `@Propagate(propagation = Propagation.SECONDARY)` - method call will be routed to all secondaries
* `@Propagate(propagation = Propagation.FASTEST)` - method call will be routed to all implementations (just like in `ALL`), but the processing will finish as soon as first implementation finishes
* `@Propagate(propagation = Propagation.RANDOM)` - method call will be routed to random implementation, either primary or secondary

## Error handling

Gugis is using Guice interceptor and all caught exceptions are wrapped with `GugisException` (runtime exception) which in turn wraps `InvocationTargetException` (thrown by Java reflection API). As `GugisException` is a runtime exception it needs to be explicitly added to `try/catch` block.

By default Gugis fails when any matching implementation throws an exception. This default behaviour can be changed. Gugis can be configured to complete a composite call even when some of matching implementations throw exceptions. However there must be at least one call that succeeds. This can be done by setting `@Propagate`'s parameter `allowFailure` to `true`. See `src/test/java` for more details.

## Setup

When creating an injector using `Guice.createInjector()` we need to tell Guice to use `GugisModule.`

Gugis automatically scans classpath for classes marked with `@Composite` annotation. If you need more complex setup you can disable autodiscovery by setting `autodiscovery` parameter to `false`. See unit tests for how to disable autodiscovery and use manual discovery instead.

After that all you have to do is to inject `CompositeStorageService` class using for example `@Inject` annotation or programmatically using `injector.getInstance()`.

# Examples

See `src/test/java` for lots of unit tests and examples.

# Download

Use the following Maven dependency:

```xml
<dependency>
  <groupId>com.github.lukaszbudnik.gugis</groupId>
  <artifactId>gugis</artifactId>
  <version>{version}</version>
</dependency>
```

or open [search.maven.org](http://search.maven.org/#search|ga|1|com.github.lukaszbudnik.gugis) and copy and paste dependency id for your favourite dependency management tool (Gradle (gugis uses Gradle), Buildr, Ivy, sbt, Leiningen, etc).

# Road map

Road map can be viewed on [milestones](https://github.com/lukaszbudnik/gugis/milestones) page.

# License

Copyright 2015 Łukasz Budnik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
