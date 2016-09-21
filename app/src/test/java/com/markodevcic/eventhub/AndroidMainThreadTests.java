package com.markodevcic.eventhub;

import android.os.Looper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AndroidMainThreadTests {

	@Test
	public void testMainThreadPublication() throws InterruptedException {
		EventHub eventHub = new EventHub(PublicationMode.MAIN_THREAD);

		final AtomicBoolean subscriptionCalled = new AtomicBoolean(false);
		eventHub.subscribe(SomeEvent.class, event -> {
			if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
				throw new IllegalStateException("should be called on main thread");
			}
			subscriptionCalled.set(true);
		});

		final AtomicBoolean eventPublished = new AtomicBoolean(false);
		new Thread(() -> {
			eventHub.publish(new SomeEvent());
			eventPublished.set(true);
		}).start();

		while (!eventPublished.get()) {
			Thread.sleep(1);
		}

		while (!subscriptionCalled.get()) {
			Robolectric.flushForegroundThreadScheduler();
		}
		Assert.assertTrue(subscriptionCalled.get());
	}
}
