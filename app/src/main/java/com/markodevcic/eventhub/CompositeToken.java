package com.markodevcic.eventhub;

import java.util.ArrayList;
import java.util.List;

public final class CompositeToken
		implements Token {

	private final List<Token> tokens = new ArrayList<>();

	public synchronized void add(Token token) {
		if (!tokens.contains(token)) {
			tokens.add(token);
		}
	}

	public synchronized void remove(Token token) {
		int position = -1;
		int size = tokens.size();
		for (int i = 0; i < size; i++) {
			if (tokens.get(i).equals(token)) {
				position = i;
				break;
			}
		}
		if (position >= 0) {
			tokens.remove(position);
		}
	}

	@Override
	public synchronized void unSubscribe() {
		for (Token token : tokens) {
			token.unSubscribe();
		}
	}

	@Override
	public synchronized boolean isSubscribed() {
		for (Token token : tokens) {
			if (token.isSubscribed()) {
				return true;
			}
		}
		return false;
	}
}
