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
