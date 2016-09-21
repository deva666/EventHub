package com.markodevcic.eventhub;

/***
 * Specifies which thread to use when invoking {@link OnEvent} action.
 */
public enum PublicationMode {
	MAIN_THREAD,
	BACKGROUND_THREAD,
	CALLING_THREAD
}
