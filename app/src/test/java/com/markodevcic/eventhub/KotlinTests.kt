package com.markodevcic.eventhub

import org.junit.Assert
import org.junit.Test

class KotlinTests {

    @Test
    fun testSubscribe() {
        val eventHub = EventHub(PublicationMode.CALLING_THREAD)
        var eventCalled = false
        eventHub.subscribe<SomeEvent> { t -> eventCalled = true }
        eventHub.publish(SomeEvent())
        Assert.assertTrue(eventCalled)
    }
}