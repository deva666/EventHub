# EventHub #

## Publisher/Subscriber library for Android ##

* simple - not bloated with features
* fast - no reflection
* no need to unsubscribe (no memory leaks)

---------------
### Example: ###

```java
EventHub eventHub = new EventHub();
eventHub.subscribe(SomeEvent.class, event -> Log.d("event", "some event published");
eventHub.publish(new SomeEvent());
```
------------

Written by [Marko Devcic](http://www.markodevcic.com)

License [APL 2.0 ](http://www.apache.org/licenses/LICENSE-2.0)