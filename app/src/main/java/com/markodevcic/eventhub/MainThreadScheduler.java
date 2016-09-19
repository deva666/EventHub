package com.markodevcic.eventhub;

import android.os.Handler;
import android.os.Looper;

/*package*/ final class MainThreadScheduler {

	private static final Handler mainHandler = new Handler(Looper.getMainLooper());

	private MainThreadScheduler() {}

	static void schedule(Runnable runnable) {
		mainHandler.post(runnable);
	}
}
