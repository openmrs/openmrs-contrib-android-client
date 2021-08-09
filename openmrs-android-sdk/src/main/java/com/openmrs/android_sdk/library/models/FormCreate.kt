package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Form create
 *
 * <p> More about Forms https://rest.openmrs.org/#forms </p>
 * @constructor Create empty Form create
 */
class FormCreate {

    @SerializedName("uuid")
    @Expose
    var uuid: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("valueReference")
    @Expose
    var valueReference: String? = null

    @SerializedName("display")
    @Expose
    var display: String? = null

    @SerializedName("links")
    @Expose
    var links: List<Link>? = null

    @SerializedName("resourceVersion")
    @Expose
    var resourceVersion: String? = null

}
