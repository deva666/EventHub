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

	public EventHub() {
	}

	public EventHub(PublicationMode publicationMode) {
		defaultPublicationMode = publicationMode;
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription holds a weak reference to onEvent action, allowing it to be garbage collected.
	 *
	 * @param eventClass type of event to listen for publications
	 * @param onEvent    action to be invoked on event publish
	 */
	public <T extends BaseEvent> void subscribe(Class<T> eventClass, OnEvent<T> onEvent) {

		subscribe(eventClass, onEvent, defaultPublicationMode);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription holds a weak reference to onEvent action, allowing it to be garbage collected.
	 *
	 * @param eventClass      type of event to listen for publications
	 * @param onEvent         action to be invoked on event publish
	 * @param publicationMode where to schedule the event publish
	 */
	public <T extends BaseEvent> void subscribe(Class<T> eventClass,
												OnEvent<T> onEvent,
												PublicationMode publicationMode) {

		subscribe(eventClass, onEvent, publicationMode, null);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription holds a weak reference to onEvent action, allowing it to be garbage collected.
	 *
	 * @param eventClass type of event to listen for publications
	 * @param onEvent    action to be invoked on event publish
	 * @param predicate  predicate that will be invoked to check if the event can be published for this subscription
	 */
	public <T extends BaseEvent> void subscribe(Class<T> eventClass,
												OnEvent<T> onEvent,
												Predicate predicate) {

		subscribe(eventClass, onEvent, defaultPublicationMode, predicate);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription holds a weak reference to onEvent action, allowing it to be garbage collected.
	 *
	 * @param eventClass      type of event to listen for publications
	 * @param onEvent         action to be invoked on event publish
	 * @param publicationMode where to schedule the event publish
	 * @param predicate       predicate that will be invoked to check if the event can be published for this subscription
	 */
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


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Uses {@link PublicationMode} passed to EventHub constructor.
	 * When default constructor is used then uses {@code PublicationMode.MAIN_THREAD}.
	 * Subscription will hold a strong reference to onEvent action.
	 * To avoid memory leaks, unSubscribe method must be called on the {@link Token} when subscriber is done.
	 *
	 * @param eventClass type of event to listen for publications
	 * @param onEvent    action to be invoked on event publish
	 * @return {@link Token} which can be used to un-subscribe from notifications
	 */
	public <T extends BaseEvent> Token subscribeForToken(Class<T> eventClass, OnEvent<T> onEvent) {
		return subscribeForToken(eventClass, onEvent, defaultPublicationMode, null);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription will hold a strong reference to onEvent action.
	 * To avoid memory leaks, unSubscribe method must be called on the {@link Token} when subscriber is done.
	 *
	 * @param eventClass      type of event to listen for publications
	 * @param onEvent         action to be invoked on event publish
	 * @param publicationMode where to schedule the event publish
	 * @return {@link Token} which can be used to un-subscribe from notifications
	 */
	public <T extends BaseEvent> Token subscribeForToken(Class<T> eventClass,
														 OnEvent<T> onEvent,
														 PublicationMode publicationMode) {

		return subscribeForToken(eventClass, onEvent, publicationMode, null);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Subscription will hold a strong reference to onEvent action.
	 * To avoid memory leaks, unSubscribe method must be called on the {@link Token} when subscriber is done.
	 *
	 * @param eventClass type of event to listen for publications
	 * @param onEvent    action to be invoked on event publish
	 * @param predicate  predicate that will be invoked to check if the event can be published for this subscription
	 * @return {@link Token} which can be used to un-subscribe from notifications
	 */
	public <T extends BaseEvent> Token subscribeForToken(Class<T> eventClass,
														 OnEvent<T> onEvent,
														 Predicate predicate) {

		return subscribeForToken(eventClass, onEvent, defaultPublicationMode, predicate);
	}


	/***
	 * Subscribes the onEven action to an event that will be published.
	 * Uses {@link PublicationMode} passed to EventHub constructor.
	 * When default constructor is used then uses {@code PublicationMode.MAIN_THREAD}.
	 * Subscription will hold a strong reference to onEvent action.
	 * To avoid memory leaks, unSubscribe method must be called on the {@link Token} when subscriber is done.
	 *
	 * @param eventClass      type of event to listen for publications
	 * @param onEvent         action to be invoked on event publish
	 * @param publicationMode where to schedule the event publish
	 * @param predicate       predicate that will be invoked to check if the event can be published for this subscription
	 * @return {@link Token} which can be used to un-subscribe from notifications
	 */
	public <T extends BaseEvent> Token subscribeForToken(Class<T> eventClass,
														 OnEvent<T> onEvent,
														 PublicationMode publicationMode,
														 @Nullable Predicate predicate) {
		Ensure.notNull(eventClass, "eventClass");
		Ensure.notNull(onEvent, "onEvent");
		Ensure.notNull(publicationMode, "publicationMode");
		Subscription subscription = new TokenSubscription(onEvent, publicationMode, predicate);
		subscribeInternal(eventClass, subscription);
		Action1<SubscriptionToken> onUnSubscribe = getTokenUnSubscribeAction();
		return new SubscriptionToken(eventClass, subscription.id, onUnSubscribe);
	}

	private Action1<SubscriptionToken> getTokenUnSubscribeAction() {
		return subscriptionToken -> {
			synchronized (classToSubsMap) {
				Map<String, Subscription> subscriptionMap = classToSubsMap.get(subscriptionToken.getEventClass());
				if (subscriptionMap != null && subscriptionMap.containsKey(subscriptionToken.getHolderId())) {
					subscriptionMap.remove(subscriptionToken.getHolderId());
				}
			}
		};
	}

	/***
	 * Publishes the event to all subscribers
	 *
	 * @param event payload to be published
	 * @return value indicating if any subscribers got notified
	 */
	public <T extends BaseEvent> boolean publish(final T event) {
		Ensure.notNull(event, "event");
		synchronized (classToSubsMap) {
			boolean hasSubscribers = false;
			Map<String, Subscription> subscriptionMap = classToSubsMap.get(event.getClass());
			if (subscriptionMap != null) {
				for (Iterator<Map.Entry<String, Subscription>> it = subscriptionMap.entrySet().iterator(); it.hasNext(); ) {
					Subscription subscription = it.next().getValue();
					final OnEvent<T> onEvent = (OnEvent<T>) subscription.getNotifyAction();
					if (onEvent != null) {
						if (subscription.canNotify()) {
							executeOnEvent(onEvent, event, subscription.publicationMode);
							hasSubscribers = true;
						}
					} else {
						it.remove();
					}
				}
			}
			return hasSubscribers;
		}
	}


	private <T extends BaseEvent> void executeOnEvent(final OnEvent<T> onEvent, final T event, PublicationMode publicationMode) {
		switch (publicationMode) {
			case MAIN_THREAD:
				if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
					onEvent.invoke(event);
				} else {
					MainThreadScheduler.schedule(() -> onEvent.invoke(event));
				}
				break;
			case BACKGROUND_THREAD:
				BackgroundThreadScheduler.schedule(() -> onEvent.invoke(event));
				break;
			case CALLING_THREAD:
				onEvent.invoke(event);
				break;
		}
	}
}
