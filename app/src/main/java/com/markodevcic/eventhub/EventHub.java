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

import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class EventHub {

	private final Map<Class<? extends BaseEvent>, Map<String, Subscription>> classToSubsMap = new HashMap<>();

	private PublicationMode defaultPublicationMode = PublicationMode.MAIN_THREAD;

	public EventHub() {}

	public EventHub(PublicationMode publicationMode) {
		defaultPublicationMode = publicationMode;
	}


	public <T extends BaseEvent> void subscribe(Class<T> eventClass, OnEvent<T> onEvent) {

		subscribe(eventClass, onEvent, defaultPublicationMode);
	}

	public <T extends BaseEvent> void subscribe(Class<T> eventClass,
												OnEvent<T> onEvent,
												PublicationMode publicationMode) {

		subscribe(eventClass, onEvent, publicationMode, null);
	}

	public <T extends BaseEvent> void subscribe(Class<T> eventClass,
												OnEvent<T> onEvent,
												Predicate predicate) {

		subscribe(eventClass, onEvent, defaultPublicationMode, predicate);
	}

	public <T extends BaseEvent> void subscribe(Class<T> eventClass,
												OnEvent<T> onEvent,
												PublicationMode publicationMode,
												@Nullable Predicate predicate) {

		Ensure.notNull(eventClass, "eventClass");
		Ensure.notNull(onEvent, "onEvent");
		Ensure.notNull(publicationMode, "publicationMode");
		Subscription subscription = new WeakSubscription(onEvent, publicationMode, predicate);
		subscribeInternal(eventClass, subscription);
	}

	private <T extends BaseEvent> void subscribeInternal(Class<T> eventClass, Subscription subscription) {
		synchronized (classToSubsMap) {
			if (classToSubsMap.containsKey(eventClass)) {
				Map<String, Subscription> subscriptionMap = classToSubsMap.get(eventClass);
				subscriptionMap.put(subscription.id, subscription);
			} else {
				Map<String, Subscription> subscriptionMap = new HashMap<>();
				subscriptionMap.put(subscription.id, subscription);
				classToSubsMap.put(eventClass, subscriptionMap);
			}
		}
	}

	public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass, OnEvent<T> onEvent) {
		return subscribeForToken(eventClass, onEvent, defaultPublicationMode, null);
	}

	public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass,
																	 OnEvent<T> onEvent,
																	 PublicationMode publicationMode) {

		return subscribeForToken(eventClass, onEvent, publicationMode, null);
	}

	public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass,
																	 OnEvent<T> onEvent,
																	 Predicate predicate) {

		return subscribeForToken(eventClass, onEvent, defaultPublicationMode, predicate);
	}

	public <T extends BaseEvent> SubscriptionToken subscribeForToken(Class<T> eventClass,
																	 OnEvent<T> onEvent,
																	 PublicationMode publicationMode,
																	 Predicate predicate) {
		Ensure.notNull(eventClass, "eventClass");
		Ensure.notNull(onEvent, "onEvent");
		Ensure.notNull(publicationMode, "publicationMode");
		Subscription subscription = new TokenSubscription(onEvent, publicationMode, predicate);
		subscribeInternal(eventClass, subscription);
		Action1<SubscriptionToken> onUnSubscribe = getTokenUnSubscribeAction();
		return new SubscriptionToken(eventClass, subscription.id, onUnSubscribe);
	}

	private Action1<SubscriptionToken> getTokenUnSubscribeAction() {
		return new Action1<SubscriptionToken>() {
			@Override
			public void invoke(SubscriptionToken subscriptionToken) {
				synchronized (classToSubsMap) {
					Map<String, Subscription> subscriptionMap = classToSubsMap.get(subscriptionToken.getEventClass());
					if (subscriptionMap != null && subscriptionMap.containsKey(subscriptionToken.getHolderId())) {
						subscriptionMap.remove(subscriptionToken.getHolderId());
					}
				}
			}
		};
	}

	public <T extends BaseEvent> void publish(final T event) {
		Ensure.notNull(event, "event");
		synchronized (classToSubsMap) {
			Map<String, Subscription> subscriptionMap = classToSubsMap.get(event.getClass());
			if (subscriptionMap != null) {
				for (Iterator<Map.Entry<String, Subscription>> it = subscriptionMap.entrySet().iterator(); it.hasNext(); ) {
					Subscription subscription = it.next().getValue();
					final OnEvent<T> onEvent = (OnEvent<T>) subscription.getNotifyAction();
					if (onEvent != null) {
						if (subscription.canNotify()) {
							executeOnEvent(onEvent, event, subscription.publicationMode);
						}
					} else {
						it.remove();
					}
				}
			}
		}
	}

	private <T extends BaseEvent> void executeOnEvent(final OnEvent<T> onEvent, final T event, PublicationMode publicationMode) {
		switch (publicationMode) {
			case MAIN_THREAD:
				if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
					onEvent.invoke(event);
				} else {
					MainThreadScheduler.schedule(new Runnable() {
						@Override
						public void run() {
							onEvent.invoke(event);
						}
					});
				}
				break;
			case BACKGROUND_THREAD:
				BackgroundThreadScheduler.schedule(new Runnable() {
					@Override
					public void run() {
						onEvent.invoke(event);
					}
				});
				break;
			case CALLING_THREAD:
				onEvent.invoke(event);
				break;
		}
	}
}
