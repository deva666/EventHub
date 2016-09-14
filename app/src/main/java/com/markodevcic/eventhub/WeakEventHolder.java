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

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.Executor;

/*package*/ final class WeakEventHolder {
    /*package*/ final String id;
    /*package*/ final WeakReference<? extends OnEvent<? extends BaseEvent>> reference;
    /*package*/ final Executor executor;

    /*package*/ WeakEventHolder(OnEvent<? extends BaseEvent> onEvent) {
        this(onEvent, null);
    }

    /*package*/ WeakEventHolder(OnEvent<? extends BaseEvent> onEvent, Executor executor) {
        this.id = UUID.randomUUID().toString();
        this.reference = new WeakReference<OnEvent<? extends BaseEvent>>(onEvent);
        this.executor = executor;
    }
}
