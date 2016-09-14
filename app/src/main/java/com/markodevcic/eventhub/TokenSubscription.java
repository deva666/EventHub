package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

 class TokenSubscription extends Subscription {

	private final OnEvent<? extends BaseEvent> onEvent;
	private final WeakReference<Predicate> predicateReference;

	TokenSubscription(OnEvent<? extends BaseEvent> onEvent, PublicationMode publicationMode, @Nullable Predicate predicate) {
		super(publicationMode);
		this.onEvent = onEvent;
		this.predicateReference = new WeakReference<>(predicate);
	}

	@Override
	boolean canNotify() {
		Predicate predicate = predicateReference.get();
		if (predicate == null) {
			return true;
		} else {
			return predicate.invoke();
		}
	}

	@Nullable
	@Override
	OnEvent<? extends BaseEvent> getNotifyAction() {
		return onEvent;
	}
}
