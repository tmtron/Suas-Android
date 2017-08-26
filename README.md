<p align="center">
<a href="http://imgur.com/a0IkBEX"><img src="http://i.imgur.com/a0IkBEX.png" title="source: imgur.com" /></a>
</p>

<p align="center">
<a href="https://travis-ci.org/zendesk/Suas-Android"><img src="https://travis-ci.org/zendesk/Suas-Android.svg?token=iTfSE3QQamPUFfPk3VRD&branch=master" alt="Build Status" /></a>
<a href="https://raw.githubusercontent.com/zendesk/Suas-Android/master/LICENSE?token=AIff-tRF1JNYHzdVtmezFOz6ujRZMWozks5ZlpRrwA%3D%3D"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" /></a>
<a href="https://gitter.im/SuasArch/Lobby?utm_source=badge&amp;utm_medium=badge&amp;utm_campaign=pr-badge&amp;utm_content=badge"><img src="https://badges.gitter.im/Join%20Chat.svg" alt="Join the chat at https://gitter.im/SuasArch/Lobby" /></a>
</p>

<br />
<br />

Suas is a [unidirectional data flow architecture](https://suas.readme.io/docs/why-unidirectional-architectures) implementation for iOS/macOS/tvOS/watchOS and Android heavily inspired by [Redux](http://redux.js.org). It provides an easy-to-use library that helps to create applications that are consistent, deterministic, and scalable.

Suas focuses on providing [good developer experience](#developer-experience-and-tooling) and tooling such as [customizable logging](#customizable-logging) and [state changes monitoring](#state-changes-monitoring).

Join our [gitter chat channel](https://gitter.im/SuasArch/Lobby) for any questions. Or check [Suas documentation website](https://suas.readme.io).

# What's on this page

- [Suas application flow and components](#suas-application-flow-and-components)
- [Why use Suas](#why-use-suas)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Developer experience and tooling](#developer-experience-and-tooling)
- [Example applications built with Suas](#example-applications-built-with-suas)
- [Where to go next](#where-to-go-next)
- [Contributing](#contributing)
- [Contact us](#contact-us)
- [Suas future](#suas-future)

For more in depth documentation on how to use Suas [check Suas website](https://suas.readme.io), [Suas API Interface](https://zendesk.github.io/Suas-Android/) or go straight to a [list of applications built with Suas](https://suas.readme.io/docs/list-of-examples).

# Suas application flow and components

Suas architecture is composed of five core elements:

  * [Store](https://suas.readme.io/docs/store): main component that contains a [Reducer](https://suas.readme.io/docs/reducer) (or [set of reducers](https://suas.readme.io/docs/applications-with-multiple-decoupled-states)), and the main application [State](https://suas.readme.io/docs/state). [Listeners](https://suas.readme.io/docs/listener) subscribe to it for state changes. [Actions](https://suas.readme.io/docs/action) that cause state changes are dispatched to it.
  * [State](https://suas.readme.io/docs/state): defines the state of a component/screen or group of components/screens.
  * [Action](https://suas.readme.io/docs/action): each action specifies a change we want to effect on the state.
  * [Reducer](https://suas.readme.io/docs/reducer): contains the logic to alter the state based on a specific action received.
  * [Listener](https://suas.readme.io/docs/listener): callback that gets notified when the state changes.

The following animation describes the Suas runtime flow.

<p align="center">
<img src="http://i.imgur.com/E7Cx2tf.gif" title="source: imgur.com" />
</p>

# Why use Suas
Suas helps you to build highly-dynamic, consistent mobile applications:

- Cross platform; [Suas-iOS](https://github.com/zendesk/Suas-iOS) works on iOS, macOS, tvOS and watchOS. Suas-Android works on all API levels and provides a Kotlin-friendly interface.
- Focuses on [developer experience](#developer-experience-and-tooling) with plugins/tools like [LoggerMiddleware](https://suas.readme.io/docs/logging-in-suas) and [Suas Monitor](https://suas.readme.io/docs/monitor-middleware-monitor-js).
- Small code base with low operational footprint. Check the source code 🙂.
- Static typing and type information are conserved in the Store, Reducers, and Listeners.
- Fast out of the box, and can be customized by developers to be even faster [with filtering listeners](https://suas.readme.io/docs/filtering-listeners).

# Installation
Add Suas as a dependency to your build file:

Gradle:
```
# For Android Plugin 3.x and up
implementation 'com.zendesk.suas:suas:1.0.0'

# For Android Plugin 2.x and below
compile 'com.zendesk.suas:suas:1.0.0'
```

Maven:
```
<dependency>
  <groupId>com.zendesk.suas</groupId>
  <artifactId>suas</artifactId>
  <version>1.0.0</version>
</dependency>
```

# Getting started

Let's get started by building a counter application.

When building applications in Suas, we start by defining the state for our counter. In this example, the counter state is an object that contains the counter value.

```java
class Counter {

    private final int value;

    Counter(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
```

We then define the actions that affect the state. For the counter, we need increment and decrement actions.

```java
private static final String INCREMENT_ACTION = "increment";
private static final String DECREMENT_ACTION = "decrement";

// ...

private static Action getDecrementAction(int value) {
    return new Action<>(DECREMENT_ACTION, value);
}

private static Action getIncrementAction(int value) {
    return new Action<>(INCREMENT_ACTION, value);
}
```

Now that we have both the `State` and the `Actions`, we need to specify how actions are going to affect the state. This logic is implemented in the `Reducer`. The counter state reducer looks like the following:

```java
class CounterReducer extends Reducer<Counter> {

    @Nullable
    @Override
    public Counter reduce(@NonNull Counter oldState, @NonNull Action<?> action) {
        switch (action.getActionType()) {
            case INCREMENT_ACTION: {
                int incrementValue = action.getData();
                return new Counter(oldState.getValue() + incrementValue);
            }
            case DECREMENT_ACTION: {
                int decrementValue = action.getData();
                return new Counter(oldState.getValue() - decrementValue);
            }
            default: {
                return null;
            }
        }
    }

    @NonNull
    @Override
    public Counter getInitialState() {
        return new Counter(0);
    }
}
```

The reducer defines two things:

1. The initial state for the store. i.e. the initial `Counter` value.
2. The reduce function, which receives both the dispatched `Action` and the current `State`. This function decides what `State` to return based on the `Action`. If the reducer did not change the state, it should return `null`

The `Store` is the main component we interact with in the application. The store contains:

1. The application's state.
2. The reducer, or reducers.
3. (Advanced) The [middlewares](https://suas.readme.io/docs/middleware)

We create a store with the following snippet:

```java
Store store = Suas.createStore(new CounterReducer()).build();
```

Now we can dispatch actions and add listeners to it. Let's see how it works:

```java
public class SuasCounter {

    // ...

    public static void main(String [] args) {
        Store store = createStore();

        Listener<Counter> listener = (state) -> System.out.println("State changed to " + state.getValue());
        store.addListener(Counter.class, listener);

        store.dispatch(getIncrementAction(10));
        store.dispatch(getIncrementAction(1));
        store.dispatch(getDecrementAction(5));
    }
}
```

Let's break down the code above:
1. We add a listener to the store by calling `store.addListener(Counter.class, listener)` specifying the state type.
2. Then we dispatch different actions to the store calling `store.dispatch(getIncrementAction(10))` and `store.dispatch(getDecrementAction(5))`.

That's it, check our [documentation website](https://suas.readme.io/docs) for a full reference on Suas components and check the [list of example built using Suas](https://suas.readme.io/docs/list-of-examples).

# Developer experience and tooling

Suas focuses on developer experience and tooling. It provides two plugins in the form of [Middlewares](https://suas.readme.io/docs/middleware) out of the box.

## Customizable logging
While the `LoggerMiddleware` logs all the action received with the state changes.

<p align="center">
<img src="http://i.imgur.com/IPNGJic.gif" title="source: imgur.com" />
</p>

Read more about [how to use the LoggerMiddleware](https://suas.readme.io/docs/logging-in-suas).

## State transition monitoring

The `MonitorMiddleware` helps to track state transition and action dispatch history.
When using `MonitorMiddleware` the `Action` dispatched and `State` changes are sent to our [Suas Monitor desktop application](https://github.com/zendesk/Suas-Monitor).

<p align="center">
<img src="http://i.imgur.com/Gu1kYwb.gif" title="source: imgur.com" />
</p>

Read how to install and start using the `MonitorMiddleware` by heading to [getting started with monitor middleware article](https://suas.readme.io/docs/monitor-middleware-monitor-js).
Under the hood `Suas Monitor` uses the fantastic [Redux DevTools](https://github.com/gaearon/redux-devtools) to provide state and action information.

# Example applications built with Suas

Check Suas website for an updated [list of examples built with Suas](https://suas.readme.io/docs/list-of-examples).

# Where to go next

To get more information about Suas:
- Head to [Suas website](https://suas.readme.io/docs) for more in-depth knowledge about how to use Suas.
- Check the [Suas API refrerence](https://zendesk.github.io/Suas-iOS/).
- Read through how to use Suas by checking [some examples built with Suas](https://suas.readme.io/docs/list-of-examples).
- Join the conversation on [Suas gitter channel](https://gitter.im/SuasArch/Lobby) or get in touch with the [people behind Suas](#contact-us).

# Contributing

We love any sort of contribution. From changing the internals of how Suas works, changing Suas methods and public API, changing readmes and [documentation topics](https://suas.readme.io).

Feel free to suggest changes on the GitHub repos or directly [in Saus gitter channel](https://gitter.im/SuasArch/Lobby).

For reference check our [contributing](https://suas.readme.io/docs/contributing) guidelines.

# Contact us

Join our [gitter channel](https://gitter.im/SuasArch/Lobby) to talk to other Suas developers.

For any question, suggestion, or just to say hi, you can find the core team on twitter:

- [Omar Abdelhafith](https://twitter.com/ifnottrue)
- [Sebastian Chlan](https://twitter.com/sebchlan)
- [Steven Diviney](https://twitter.com/DivoDivenson)
- [Giacomo Rebonato](https://twitter.com/GiacomoRebonato)
- [Elvis Porebski](https://twitter.com/@eepDev)
- [Vitor Nunes](https://twitter.com/@vitornovictor)

# Suas future

To help craft Suas future releases, join us on [gitter channel](https://gitter.im/SuasArch/Lobby).

# Copyright and license

```
Copyright 2017 Zendesk, Inc.
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```
