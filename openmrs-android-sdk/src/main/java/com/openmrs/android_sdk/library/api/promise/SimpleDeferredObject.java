/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.openmrs.android_sdk.library.api.promise;

import org.jdeferred.impl.DeferredObject;

/**
 * The type Simple deferred object.
 *
 * @param <T> the type parameter
 */
public class SimpleDeferredObject<T> extends DeferredObject<T, Throwable, Void> implements SimplePromise<T> {
    public SimplePromise<T> promise() {
        return (SimplePromise<T>) super.promise();
    }
}
