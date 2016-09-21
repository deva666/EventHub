package com.markodevcic.eventhub;

import org.junit.Assert;
import org.junit.Test;

public class CompositeTokenTests {

	@Test
	public void testUnsubscribe() {
		EventHub eventHub = new EventHub(PublicationMode.CALLING_THREAD);
		CompositeToken compositeToken = new CompositeToken();
		compositeToken.add(eventHub.subscribeForToken(SomeEvent.class, event -> {
			throw new IllegalStateException("should not be called if unsubscibed");
		}));
		compositeToken.add(eventHub.subscribeForToken(AnotherEvent.class, event -> {
			throw new IllegalStateException("should not be called if unsubscibed");
		}));
		Assert.assertTrue(compositeToken.isSubscribed());
		compositeToken.unSubscribe();
		Assert.assertFalse(compositeToken.isSubscribed());
		eventHub.publish(new SomeEvent());
		eventHub.publish(new AnotherEvent());
	}
}
