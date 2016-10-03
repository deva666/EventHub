package com.markodevcic.eventhub;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a group of {@link Token} which can be unsubscribed together
 */
public final class CompositeToken
		implements Token {

	private List<Token> tokens = new ArrayList<>();

	/**
	 * Adds a new {@link Token} to this {@code CompositeToken}
	 * @param token
	 */
	public void add(Token token) {
		Ensure.condition(token != this, "can't add self to token list");
		if (!token.isSubscribed()) {
			return;
		}
		if (!tokens.contains(token)) {
			tokens.add(token);
		}
	}

	/**
	 * Removes a {@link Token} from this {@code CompositeToken} and unsubscribes it.
	 * @param token
	 */
	public void remove(Token token) {
		int position = -1;
		int size = tokens.size();
		for (int i = 0; i < size; i++) {
			if (tokens.get(i).equals(token)) {
				position = i;
				break;
			}
		}
		if (position >= 0) {
			token.unSubscribe();
			tokens.remove(position);
		}
	}

	/**
	 * Unsubscribes all tokens
	 */
	@Override
	public void unSubscribe() {
		for (Token token : tokens) {
			token.unSubscribe();
		}
	}

	/**
	 * Returns true if has subscriptions
	 * @return
	 */
	@Override
	public boolean isSubscribed() {
		for (Token token : tokens) {
			if (token.isSubscribed()) {
				return true;
			}
		}
		return false;
	}
}
