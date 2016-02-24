/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.views;

import org.odk.collect.android.logic.HierarchyElement;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HierarchyElementView extends RelativeLayout {

    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;
    private ImageView mIcon;


    public HierarchyElementView(Context context, HierarchyElement it) {
        super(context);

        setColor(it.getColor());

        mIcon = new ImageView(context);
        mIcon.setImageDrawable(it.getIcon());
        mIcon.setId(generateViewId());
        mIcon.setPadding(0, 0, dipToPx(4), 0);

        addView(mIcon, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mPrimaryTextView = new TextView(context);
        mPrimaryTextView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        mPrimaryTextView.setText(it.getPrimaryText());
        mPrimaryTextView.setId(generateViewId());
        mPrimaryTextView.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams l =
            new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        l.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
        addView(mPrimaryTextView, l);

        mSecondaryTextView = new TextView(context);
        mSecondaryTextView.setText(it.getSecondaryText());
        mSecondaryTextView.setTextAppearance(context, android.R.style.TextAppearance_Small);
        mSecondaryTextView.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams lp =
            new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, mPrimaryTextView.getId());
        lp.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
        addView(mSecondaryTextView, lp);

        setPadding(dipToPx(8), dipToPx(4), dipToPx(8), dipToPx(8));

    }

    	public final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	public int generateViewId() {
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}


    public void setPrimaryText(String text) {
        mPrimaryTextView.setText(text);
    }


    public void setSecondaryText(String text) {
        mSecondaryTextView.setText(text);
    }


    public void setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
    }


    public void setColor(int color) {
        setBackgroundColor(color);
    }


    public void showSecondary(boolean bool) {
        if (bool) {
            mSecondaryTextView.setVisibility(VISIBLE);
            setMinimumHeight(dipToPx(64));

        } else {
            mSecondaryTextView.setVisibility(GONE);
            setMinimumHeight(dipToPx(32));

        }
    }
    
    public int dipToPx(int dip) {
        return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
    }

}
