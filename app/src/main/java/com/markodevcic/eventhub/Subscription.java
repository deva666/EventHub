package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.UUID;

/*package*/ abstract class Subscription {

	/*package*/ final String id;
	/*package*/ final PublicationMode publicationMode;
	private final WeakReference<Predicate> predicateReference;

	/*package*/ Subscription(PublicationMode publicationMode, Predicate predicate) {
		this.id = UUID.randomUUID().toString();
		this.publicationMode = publicationMode;
		predicateReference = new WeakReference<>(predicate);
	}

	/*package*/ boolean canNotify() {
		Predicate predicate = predicateReference.get();
		if (predicate == null) {
			return true;
		} else {
			return predicate.invoke();
		}
	}

	@Nullable
	/*package*/ abstract OnEvent<? extends BaseEvent> getNotifyAction();
}
