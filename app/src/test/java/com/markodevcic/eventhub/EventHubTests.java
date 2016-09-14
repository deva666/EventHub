/*
Copyright 2016, Marko Devcic, madevcic@gmail.com, http://www.markodevcic.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.markodevcic.eventhub;

import org.junit.Assert;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventHubTests {

    @Test
    public void testEventPublish() {
        EventHub eventHub = new EventHub();
        final boolean[] onEventCalled = {false};
        eventHub.subscribe(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                onEventCalled[0] = true;
            }
        });
        eventHub.publish(new SomeEvent());
        Assert.assertTrue(onEventCalled[0]);
    }

    @Test
    public void testWeakReference() {
        EventHub eventHub = new EventHub();
        ShouldNotBeCalledHandler eventHandler = new ShouldNotBeCalledHandler();
        eventHub.subscribe(SomeEvent.class, eventHandler);
        eventHandler = null;
        System.gc();
        eventHub.publish(new SomeEvent());
    }

    @Test
    public void testUnSubscribe() {
        EventHub eventHub = new EventHub();
        ShouldNotBeCalledHandler eventHandler = new ShouldNotBeCalledHandler();
        SubscriptionToken token = eventHub.subscribeForToken(SomeEvent.class, eventHandler);
        token.unSubscribe();
        eventHub.publish(new SomeEvent());
        Assert.assertFalse(token.isSubscribed());
    }

    @Test
    public void testMultipleEventsSubscribed(){
        EventHub eventHub = new EventHub();
        final boolean[] firstOnEventCalled = {false};
        eventHub.subscribe(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                firstOnEventCalled[0] = true;
            }
        });
        final boolean[] secondOnEventCalled = {false};
        eventHub.subscribe(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                secondOnEventCalled[0] = true;
            }
        });
        eventHub.publish(new SomeEvent());
        Assert.assertTrue(firstOnEventCalled[0]);
        Assert.assertTrue(secondOnEventCalled[0]);
    }

    @Test
    public void testMultipleEventsUnSubscribed(){
        EventHub eventHub = new EventHub();
        final boolean[] firstOnEventCalled = {false};
        eventHub.subscribe(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                firstOnEventCalled[0] = true;
            }
        });
        final boolean[] secondOnEventCalled = {false};
        SubscriptionToken token = eventHub.subscribeForToken(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                secondOnEventCalled[0] = true;
            }
        });
        token.unSubscribe();
        eventHub.publish(new SomeEvent());
        Assert.assertTrue(firstOnEventCalled[0]);
        Assert.assertFalse(secondOnEventCalled[0]);
    }

    @Test
    public void testMultipleTypesSubscribed(){
        EventHub eventHub = new EventHub();
        final boolean[] firstOnEventCalled = {false};
        eventHub.subscribe(SomeEvent.class, new OnEvent<SomeEvent>() {
            @Override
            public void invoke(SomeEvent event) {
                firstOnEventCalled[0] = true;
            }
        });
        final boolean[] secondOnEventCalled = {false};
        eventHub.subscribe(AnotherEvent.class, new OnEvent<AnotherEvent>() {
            @Override
            public void invoke(AnotherEvent event) {
                secondOnEventCalled[0] = true;
            }
        });
        eventHub.publish(new SomeEvent());
        Assert.assertTrue(firstOnEventCalled[0]);
        Assert.assertFalse(secondOnEventCalled[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPassingNullClass(){
        EventHub eventHub = new EventHub();
        eventHub.subscribe(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPassingNullClassForToken(){
        EventHub eventHub = new EventHub();
        eventHub.subscribeForToken(null, null);
    }

//    @Test
//    public void testHolderCanBeGCedWithToken(){
//        EventHub eventHub = new EventHub();
//        ShouldNotBeCalledHandler handler = new ShouldNotBeCalledHandler();
//        WeakReference<ShouldNotBeCalledHandler> reference = new WeakReference<>(handler);
//        SubscriptionToken token = eventHub.subscribeForToken(SomeEvent.class, handler);
//        handler = null;
//        System.gc();
//        eventHub.publish(new SomeEvent());
//        Assert.assertNull("When event hub returns a subscription token, token shouldn't prevent the handler from being GC'ed",
//                reference.get());
//    }

//    @Test
//    public void testTokenUnSubscribeWhenHolderGCed(){
//        EventHub eventHub = new EventHub();
//        ShouldNotBeCalledHandler handler = new ShouldNotBeCalledHandler();
//        WeakReference<ShouldNotBeCalledHandler> reference = new WeakReference<>(handler);
//        SubscriptionToken token = eventHub.subscribeForToken(SomeEvent.class, handler);
//        handler = null;
//        System.gc();
//        token.unSubscribe();
//        eventHub.publish(new SomeEvent());
//        token.unSubscribe();
//        Assert.assertNull("When event hub returns a subscription token, token shouldn't prevent the handler from being GC'ed",
//                reference.get());
//        Assert.assertFalse(token.isSubscribed());
//    }

    private static class ShouldNotBeCalledHandler implements OnEvent<SomeEvent> {
        @Override
        public void invoke(SomeEvent event) {
            throw new IllegalStateException("should not be called");
        }
    }


    private static class SomeEvent extends BaseEvent {

    }

    private static class AnotherEvent extends BaseEvent {

    }
}
