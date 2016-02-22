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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;

import java.util.ArrayList;
import java.util.List;

public final class ToastUtil {

    private static OpenMRSLogger logger = OpenMRS.getInstance().getOpenMRSLogger();
    private static List<ToastThread> toastQueue = new ArrayList<ToastThread>();

    private ToastUtil() {
    }

    public enum ToastType {
        ERROR, NOTICE, SUCCESS, WARNING
    }

    public static void showShortToast(Context context, ToastType type, int textId) {
        showToast(context, type, context.getResources().getString(textId), Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, ToastType type, int textId) {
        showToast(context, type, context.getResources().getString(textId), Toast.LENGTH_LONG);
    }

    public static void showShortToast(Context context, ToastType type, String text) {
        showToast(context, type, text, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, ToastType type, String text) {
        showToast(context, type, text, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, ToastType type,
                                 String text, final int duration) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastRoot = inflater.inflate(R.layout.toast, null);

        Bitmap bitmap;
        ImageView toastImage = (ImageView) toastRoot.findViewById(R.id.toastImage);
        TextView toastText = (TextView) toastRoot.findViewById(R.id.toastText);
        toastText.setText(text);

        bitmap = ImageUtils.decodeBitmapFromResource(
                context.getResources(),
                getImageResId(type),
                toastImage.getLayoutParams().width,
                toastImage.getLayoutParams().height);
        toastImage.setImageBitmap(bitmap);

        logger.d("Decode bitmap: " + bitmap.toString());
        Toast toast = new Toast(context);

        toast.setView(toastRoot);
        toast.setDuration(duration);
        toast.show();

        ToastThread thread = new ToastThread(bitmap, duration);
        if (toastQueue.size() == 0) {
            thread.start();
        }
        toastQueue.add(thread);
    }

    private static int getImageResId(ToastType type) {
        int toastTypeImageId = 0;

        switch (type) {
            case ERROR:
                toastTypeImageId = R.drawable.toast_error;
                break;
            case NOTICE:
                toastTypeImageId = R.drawable.toast_notice;
                break;
            case SUCCESS:
                toastTypeImageId = R.drawable.toast_success;
                break;
            case WARNING:
                toastTypeImageId = R.drawable.toast_warning;
                break;
            default:
                break;
        }

        return toastTypeImageId;
    }

    private static class ToastThread extends Thread {
        private Bitmap mBitmap;
        private int mDuration;

        public ToastThread(Bitmap bitmap, int duration) {
            mBitmap = bitmap;
            mDuration = duration;
        }

        @Override
        public void run() {
            try {
                if (mDuration == Toast.LENGTH_SHORT) {
                    Thread.sleep(2000);
                } else {
                    Thread.sleep(3500);
                }
                logger.d("Recycle bitmap: " + mBitmap.toString());
                mBitmap.recycle();
                toastQueue.remove(0);
                if (toastQueue.size() > 0) {
                    toastQueue.get(0).run();
                }
            } catch (Exception e) {
                logger.e(e.toString());
            }
        }
    }
}
