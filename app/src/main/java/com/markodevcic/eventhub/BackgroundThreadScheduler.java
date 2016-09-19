package com.markodevcic.eventhub;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*package*/ final class BackgroundThreadScheduler {

	private static final Executor executor = Executors.newSingleThreadExecutor();

	private BackgroundThreadScheduler() {}

	static void schedule(Runnable runnable) {
		executor.execute(runnable);

	}
}
