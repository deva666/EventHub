[![Build Status](https://travis-ci.org/deva666/EventHub.svg?branch=master)](https://travis-ci.org/deva666/EventHub) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# EventHub #

## Android Publisher/Subscriber library for lazy people who don't want to worry about manual unsubscription and memory leaks ##

* no reflection
* no need to unsubscribe manually (forget about memory leaks)

---------------
### Examples: ###


*Subscribe with weak subscription, internally EventHub stores a weak reference to the subscriber.*


```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "some event published"));
eventHub.publish(new SomeEvent());
```
---------------

*You can also subscribe for token, then you control the lifetime of the subscription.*


```java
EventHub eventHub = new EventHub();
Token token = eventHub.subscribeForToken(SomeEvent.class, event -> Log.d("event", "this won't be called"));
token.unSubscribe();
eventHub.publish(new SomeEvent());
```

---------------
*Want to call the subscription on the main thread, background thread or calling thread? Pass the PublicationMode when subscribing.*


```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "some event published on main thread"), PublicationMode.MAIN_THREAD);
eventHub.publish(new SomeEvent());
```
---------------
*Have some custom rule whether the subscription can be invoked? Pass it to subscribe method.*


```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "this won't be called"), () -> false);
eventHub.publish(new SomeEvent());
```


------------
*In Kotlin you can specify the event type as generic parameter of subscribe method.*
```
val eventHub = EventHub()
eventHub.subscribe<SomeEvent> { e -> Log.d("event", "some event called") }
eventHub.publish(SomeEvent())
```

--------------

Written by [Marko Devcic](http://www.markodevcic.com)

License [APL 2.0 ](http://www.apache.org/licenses/LICENSE-2.0)
