package org.openmrs.mobile.utilities

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


class InputField(var concept: String) : Serializable, Parcelable {
    var id: Int
    var value : Double = -1.0
    var isRed = false

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(concept)
        dest.writeDouble(value)
        dest.writeInt(if (isRed) 1 else 0)
    }
    init {
        id = Math.abs(concept.hashCode())
    }


    constructor(parcel: Parcel) : this(parcel.readString()) {
        id = parcel.readInt()
        value = parcel.readDouble()
        isRed = parcel.readInt()==1
    }

    companion object CREATOR : Parcelable.Creator<InputField> {
        override fun createFromParcel(parcel: Parcel): InputField {
            return InputField(parcel)
        }

        override fun newArray(size: Int): Array<InputField?> {
            return arrayOfNulls(size)
        }
    }
}
