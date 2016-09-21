# EventHub #

## Publisher/Subscriber library for Android ##

* simple - not bloated with features
* fast - no reflection
* no need to unsubscribe (no memory leaks)

---------------
### Examples: ###
&nbsp;

*Subscribe with weak subscription, internally EventHub stores a weak reference to the subscriber.*
&nbsp;

```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "some event published"));
eventHub.publish(new SomeEvent());
```
&nbsp;

*You can also subscribe for token, then you control the lifetime of the subscription.*
&nbsp;

```java
EventHub eventHub = new EventHub();
Token token = eventHub.subscribeForToken(SomeEvent.class, event -> Log.d("event", "this won't be called"));
token.unSubscribe();
eventHub.publish(new SomeEvent());
```
&nbsp;

*Want to call the subscription on the main thread, background thread or calling thread? Pass the PublicationMode when subscribing.*
&nbsp;

```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "some event published"), PublicationMode.MAIN_THREAD);
eventHub.publish(new SomeEvent());
```
&nbsp;



------------

Written by [Marko Devcic](http://www.markodevcic.com)

License [APL 2.0 ](http://www.apache.org/licenses/LICENSE-2.0)