package com.markodevcic.eventhub;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*package*/ final class BackgroundThreadScheduler {

	private static final Executor executor = Executors.newSingleThreadExecutor();

	private BackgroundThreadScheduler() {
		throw new IllegalStateException("no instances");
	}

	/*package*/
	static void schedule(Runnable runnable) {
		executor.execute(runnable);

	}
}
