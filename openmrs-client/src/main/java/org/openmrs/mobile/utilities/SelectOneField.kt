package org.openmrs.mobile.utilities

import android.os.Parcel
import android.os.Parcelable
import org.openmrs.mobile.models.Answer
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class SelectOneField(private var answerList: List<Answer>, concept: String?) : Serializable, Parcelable {
    var concept: String? = null
    var chosenAnswer: Answer? = null

    fun setAnswer(answerPosition: Int) {
        if (answerPosition < answerList.size) {
            chosenAnswer = answerList[answerPosition]
        }
        if (answerPosition == -1) {
            chosenAnswer = null
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(concept)
        dest.writeSerializable(chosenAnswer)
        dest.writeList(answerList)
    }

    val chosenAnswerPosition: Int
        get() = answerList.indexOf(chosenAnswer)

    init {
        this.concept = concept
    }


    constructor(parcel: Parcel) : this(ArrayList(),parcel.readString()){
        chosenAnswer = parcel.readParcelable(Answer::class.java.classLoader)
        parcel.readList(answerList,Answer::class.java.classLoader)
    }


    companion object CREATOR : Parcelable.Creator<SelectOneField> {
        override fun createFromParcel(parcel: Parcel): SelectOneField {
            return SelectOneField(parcel)
        }

        override fun newArray(size: Int): Array<SelectOneField?> {
            return arrayOfNulls(size)
        }
    }
}