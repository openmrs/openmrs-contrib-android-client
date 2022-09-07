package com.openmrs.android_sdk.utilities

import com.openmrs.android_sdk.library.models.Resource
import rx.Observable

fun <T> Observable<T>.execute(): T = this.single().toBlocking().first()

fun <T: Resource> List<T>.mapAllergies(selectHeader: String) = LinkedHashMap<String, Resource>().apply {
    put(selectHeader, Resource())
    this@mapAllergies.forEach { put(it.display!!, it) }
}
