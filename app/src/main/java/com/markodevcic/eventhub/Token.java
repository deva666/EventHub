package com.markodevcic.eventhub;

/**
 * Token returns from {@link EventHub#subscribeForToken(Class, OnEvent)} for releasing references and unsubscribing from events
 */
public interface Token {

	/**
	 * Release the reference and stop receiving event notifications
	 */
    void unSubscribe();

	/**
	 * Is this token still subscribed
	 */
    boolean isSubscribed();
}
