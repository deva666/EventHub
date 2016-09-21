package com.markodevcic.eventhub;

public interface Token {
    void unSubscribe();

    boolean isSubscribed();
}
