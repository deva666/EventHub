package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

 /*package*/ class TokenSubscription extends Subscription {

	private final OnEvent<? extends BaseEvent> onEvent;

	TokenSubscription(OnEvent<? extends BaseEvent> onEvent, PublicationMode publicationMode, @Nullable Predicate predicate) {
		super(publicationMode, predicate);
		this.onEvent = onEvent;
	}

	@Nullable
	@Override
	OnEvent<? extends BaseEvent> getNotifyAction() {
		return onEvent;
	}
}
