package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/*package*/ class WeakSubscription extends Subscription {

	private final WeakReference<OnEvent<? extends BaseEvent>> eventWeakReference;

	/*package*/ WeakSubscription(OnEvent<? extends BaseEvent> onEvent,
								 PublicationMode publicationMode,
								 Predicate predicate) {
		super(publicationMode, predicate);
		eventWeakReference = new WeakReference<>(onEvent);
	}

	@Nullable
	@Override
	OnEvent<? extends BaseEvent> getNotifyAction() {
		return eventWeakReference.get();
	}
}
