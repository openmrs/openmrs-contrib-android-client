package org.openmrs.mobile.utilities;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

public final class AnimationUtils {

    private Integer lastPosition = -1;

    public void setAnimation(View viewToAnimate, Context context , int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setDuration(700);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
