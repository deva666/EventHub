package com.markodevcic.eventhub;

/**
 * Token returns from {@link EventHub#subscribeForToken(Class, OnEvent)} for releasing references and unsubscribing from events
 */
public abstract class Token {

	/**
	 * Release the reference and stop receiving event notifications
	 */
    abstract void unSubscribe();

	/**
	 * Is this token still subscribed
	 */
    abstract boolean isSubscribed();
}
