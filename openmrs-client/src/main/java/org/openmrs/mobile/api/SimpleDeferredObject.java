package org.openmrs.mobile.api;

import org.jdeferred.impl.DeferredObject;

public class SimpleDeferredObject<T> extends DeferredObject<T, Throwable, Void> implements SimplePromise<T> {

    public SimplePromise<T> promise() {
        return (SimplePromise<T>) super.promise();
    }
}
