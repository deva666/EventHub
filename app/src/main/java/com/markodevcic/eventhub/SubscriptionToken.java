/*
Copyright 2016, Marko Devcic, madevcic@gmail.com, http://www.markodevcic.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.markodevcic.eventhub;

import java.util.concurrent.atomic.AtomicBoolean;

/*package*/ final class SubscriptionToken
        implements Token {

    private final Class<? extends BaseEvent> eventClass;
    private final String holderId;
    private Action1<SubscriptionToken> onUnSubscribe;
    private final AtomicBoolean isSubscribed = new AtomicBoolean(true);

    /*package*/ SubscriptionToken(Class<? extends BaseEvent> eventClass,
                                  String holderId,
                                  Action1<SubscriptionToken> onUnSubscribe) {
        this.eventClass = eventClass;
        this.holderId = holderId;
        this.onUnSubscribe = onUnSubscribe;
    }

    @Override
    public void unSubscribe() {
        if (isSubscribed.compareAndSet(true, false)) {
            onUnSubscribe.invoke(this);
            onUnSubscribe = null;
        }
    }

    @Override
    public boolean isSubscribed() {
        return isSubscribed.get();
    }

    /*package*/ Class<? extends BaseEvent> getEventClass() {
        return eventClass;
    }

    /*package*/ String getHolderId() {
        return holderId;
    }
}
