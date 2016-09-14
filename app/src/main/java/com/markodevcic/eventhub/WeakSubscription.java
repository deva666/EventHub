package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/*package*/  class WeakSubscription extends Subscription {

	private final WeakReference<OnEvent<? extends BaseEvent>> eventWeakReference;
	private final WeakReference<Predicate> predicateReference;

	/*package*/ WeakSubscription(OnEvent<? extends BaseEvent> onEvent, PublicationMode publicationMode, Predicate predicate) {
		super(publicationMode);
		eventWeakReference = new WeakReference<OnEvent<? extends BaseEvent>>(onEvent);
		this.predicateReference = new WeakReference<>(predicate);
	}

	@Override
	/*package*/ boolean canNotify() {
		Predicate predicate = predicateReference.get();
		if (predicate == null) {
			return true;
		} else {
			return predicate.invoke();
		}
	}

	@Nullable
	@Override
	/*package*/ OnEvent<? extends BaseEvent> getNotifyAction() {
		return eventWeakReference.get();
	}
}
