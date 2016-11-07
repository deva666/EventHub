package com.markodevcic.eventhub

inline fun <reified T : BaseEvent> EventHub.subscribe(crossinline onEvent: (T) -> Unit) {
    this.subscribe(T::class.java, { t -> onEvent.invoke(t) })
}

inline fun <reified T : BaseEvent> EventHub.subscribe(crossinline onEvent: (T) -> Unit, publicationMode: PublicationMode) {
    this.subscribe(T::class.java, { t -> onEvent.invoke(t) }, publicationMode)
}

inline fun <reified T : BaseEvent> EventHub.subscribe(crossinline onEvent: (T) -> Unit, publicationMode: PublicationMode, crossinline predicate: () -> Boolean) {
    this.subscribe(T::class.java, { t -> onEvent.invoke(t) }, publicationMode, { predicate.invoke() })
}

inline fun <reified T : BaseEvent> EventHub.subscribe(crossinline onEvent: (T) -> Unit, crossinline predicate: () -> Boolean) {
    this.subscribe(T::class.java, { t -> onEvent.invoke(t) }, { predicate.invoke() })
}


inline fun <reified T : BaseEvent> EventHub.subscribeForToken(crossinline onEvent: (T) -> Unit): Token {
    return this.subscribeForToken(T::class.java, { t -> onEvent.invoke(t) })
}

inline fun <reified T : BaseEvent> EventHub.subscribeForToken(crossinline onEvent: (T) -> Unit, publicationMode: PublicationMode): Token {
    return this.subscribeForToken(T::class.java, { t -> onEvent.invoke(t) }, publicationMode)
}

inline fun <reified T : BaseEvent> EventHub.subscribeForToken(crossinline onEvent: (T) -> Unit, publicationMode: PublicationMode, crossinline predicate: () -> Boolean): Token {
    return this.subscribeForToken(T::class.java, { t -> onEvent.invoke(t) }, publicationMode, { predicate.invoke() })
}

inline fun <reified T : BaseEvent> EventHub.subscribeForToken(crossinline onEvent: (T) -> Unit, crossinline predicate: () -> Boolean): Token {
    return this.subscribeForToken(T::class.java, { t -> onEvent.invoke(t) }, { predicate.invoke() })
}

