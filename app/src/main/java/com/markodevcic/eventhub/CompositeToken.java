package com.markodevcic.eventhub;

import java.util.ArrayList;
import java.util.List;

public final class CompositeToken
		implements Token {

	private final List<Token> tokens = new ArrayList<>();

	public void add(Token token) {
		if (!tokens.contains(token)) {
			tokens.add(token);
		}
	}

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
			tokens.remove(position);
		}
	}

	@Override
	public void unSubscribe() {
		for (Token token : tokens) {
			token.unSubscribe();
		}
	}

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
