package com.markodevcic.eventhub;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a group of {@link Token} which can be unsubscribed together
 * Class is NOT thread safe
 */
public final class CompositeToken
		extends Token {

	private List<Token> tokens = new ArrayList<>();
	private boolean isSubscribed = true;

	/**
	 * Adds a new {@link Token} to this {@code CompositeToken} if the {@link Token} is subscribed
	 *
	 * @param token
	 */
	public void add(Token token) {
		Ensure.condition(token != this, "can't add self to token list");
		if (!isSubscribed || !token.isSubscribed()) {
			return;
		}
		if (!tokens.contains(token)) {
			tokens.add(token);
		}
	}

	/**
	 * Removes a {@link Token} from this {@code CompositeToken} and unsubscribes it.
	 *
	 * @param token
	 */
	public void remove(Token token) {
		boolean removed;
		removed = tokens.remove(token);
		if (removed) {
			token.unSubscribe();
		}
	}

	/**
	 * Unsubscribes all tokens
	 */
	@Override
	public void unSubscribe() {
		isSubscribed = false;
		Iterable<Token> tokensCopy;
		tokensCopy = tokens;
		tokens = null;
		for (Token token : tokensCopy) {
			token.unSubscribe();
		}
	}

	/**
	 * Returns true if has subscriptions
	 *
	 * @return
	 */
	@Override
	public boolean isSubscribed() {
		return isSubscribed;
	}

	public boolean hasSubscriptions() {
		return isSubscribed && tokens != null && !tokens.isEmpty();
	}
}
