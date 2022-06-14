package org.openmrs.mobile.utilities

import android.view.View

fun View.makeVisible(){
    this.visibility = View.VISIBLE
}

fun View.makeInvisible(){
    this.visibility = View.INVISIBLE
}

fun View.makeGone(){
    this.visibility = View.GONE
}
