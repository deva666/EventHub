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

import java.util.*;
import java.util.concurrent.Executor;

public final class EventHub {

    private final Map<Class<? extends BaseEvent>, Map<String, WeakEventHolder>> classToHoldersMap = new HashMap<>();

    private EventHub() {
    }

    public <T extends BaseEvent> void subscribe(Class<T> eventClass, OnEvent<T> onEvent) {
        subscribe(eventClass, onEvent, null);
    }

    public <T extends BaseEvent> void subscribe(Class<T> eventClass, OnEvent<T> onEvent, Executor executor) {
        Ensure.notNull(eventClass, "eventClass");
        synchronized (classToHoldersMap) {
            WeakEventHolder eventHolder = new WeakEventHolder(onEvent, executor);
            subscribeInternal(eventClass, eventHolder);
        }
    }

    private <T extends BaseEvent> void subscribeInternal(Class<T> eventClass, WeakEventHolder eventHolder) {
        if (classToHoldersMap.containsKey(eventClass)) {
            Map<String, WeakEventHolder> handlers = classToHoldersMap.get(eventClass);
            handlers.put(eventHolder.id, eventHolder);
        } else {
            Map<String, WeakEventHolder> holderMap = new HashMap<>();
            holderMap.put(eventHolder.id, eventHolder);
            classToHoldersMap.put(eventClass, holderMap);
        }
    }

    public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass, OnEvent<T> onEvent) {
        return subscribeForToken(eventClass, onEvent, null);
    }

    public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass, OnEvent<T> onEvent, Executor executor) {
        Ensure.notNull(eventClass, "eventClass");
        synchronized (classToHoldersMap) {
            WeakEventHolder eventHolder = new WeakEventHolder(onEvent, executor);
            subscribeInternal(eventClass, eventHolder);
            Action1<SubscriptionToken> onUnSubscribe = getTokenUnSubscribeAction();
            return new SubscriptionToken(eventClass, eventHolder.id, onUnSubscribe);
        }
    }

    private Action1<SubscriptionToken> getTokenUnSubscribeAction() {
        return new Action1<SubscriptionToken>() {
            @Override
            public void invoke(SubscriptionToken subscriptionToken) {
                synchronized (classToHoldersMap) {
                    Map<String, WeakEventHolder> eventHolderMap = classToHoldersMap.get(subscriptionToken.getEventClass());
                    if (eventHolderMap != null && eventHolderMap.containsKey(subscriptionToken.getHolderId())) {
                        eventHolderMap.remove(subscriptionToken.getHolderId());
                    }
                }
            }
        };
    }

    public <T extends BaseEvent> void publish(final T event) {
        Ensure.notNull(event, "event");
        synchronized (classToHoldersMap) {
            Map<String, WeakEventHolder> eventHolders = classToHoldersMap.get(event.getClass());
            if (eventHolders != null) {
                for (Iterator<Map.Entry<String, WeakEventHolder>> it = eventHolders.entrySet().iterator(); it.hasNext(); ) {
                    WeakEventHolder eventHolder = it.next().getValue();
                    final OnEvent<T> onEvent = (OnEvent<T>) eventHolder.reference.get();
                    if (onEvent != null) {
                        invokeOnEvent(event, eventHolder, onEvent);
                    } else {
                        it.remove();
                    }
                }
            }
        }
    }

    private static <T extends BaseEvent> void invokeOnEvent(final T event, WeakEventHolder eventHolder, final OnEvent<T> onEvent) {
        if (eventHolder.executor != null) {
            eventHolder.executor.execute(new Runnable() {
                @Override
                public void run() {
                    onEvent.invoke(event);
                }
            });
        } else {
            onEvent.invoke(event);
        }
    }

    private static class InstanceHolder {
        private static final EventHub INSTANCE = new EventHub();
    }

    public static EventHub getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
