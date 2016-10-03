package com.markodevcic.eventhub;

import android.os.Handler;
import android.os.Looper;

/*package*/ final class MainThreadScheduler {

	private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

	private MainThreadScheduler() {
		throw new IllegalStateException("no instances");
	}

	/*package*/ static void schedule(Runnable runnable) {
		MAIN_HANDLER.post(runnable);
	}
}
