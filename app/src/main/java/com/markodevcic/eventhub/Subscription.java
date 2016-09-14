package com.markodevcic.eventhub;

import android.support.annotation.Nullable;

import java.util.UUID;

/*package*/ abstract class Subscription {

	/*package*/ final String id;
	/*package*/ final PublicationMode publicationMode;

	/*package*/ Subscription(PublicationMode publicationMode) {
		this.id = UUID.randomUUID().toString();
		this.publicationMode = publicationMode;
	}

	/*package*/
	abstract boolean canNotify();

	@Nullable
	/*package*/ abstract OnEvent<? extends BaseEvent> getNotifyAction();
}
