package org.openmrs.client.utilities;


import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TabUtil {
    public static final int MIN_SCREEN_WIDTH_FOR_FINDPATIENTSACTIVITY = 480;
    public static final int MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY = 960;

    private TabUtil() {
    }

    public static void setHasEmbeddedTabs(Object inActionBar, WindowManager windowManager, int minScreenWidth) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int scaledScreenWidth = (int) (displaymetrics.widthPixels / displaymetrics.density);
        boolean inHasEmbeddedTabs = scaledScreenWidth >= minScreenWidth;

        // get the ActionBar class
        Class<?> actionBarClass = inActionBar.getClass();

        // if it is a Jelly Bean implementation (ActionBarImplJB), get the super class (ActionBarImplICS)
        if ("android.support.v7.app.ActionBarImplJB".equals(actionBarClass.getName())) {
            actionBarClass = actionBarClass.getSuperclass();
        }

        // if Android 4.3 >
        if ("android.support.v7.app.ActionBarImplJBMR2".equals(actionBarClass.getName())) {
            actionBarClass = actionBarClass.getSuperclass().getSuperclass();
        }

        try {
            final Field actionBarField = actionBarClass.getDeclaredField("mActionBar");
            actionBarField.setAccessible(true);
            inActionBar = actionBarField.get(inActionBar);
            actionBarClass = inActionBar.getClass();
        }
        catch (IllegalAccessException e) {}
        catch (IllegalArgumentException e) {}
        catch (NoSuchFieldException e) {}

        try {
            final Method method = actionBarClass.getDeclaredMethod("setHasEmbeddedTabs", new Class[]{Boolean.TYPE});
            method.setAccessible(true);
            method.invoke(inActionBar, new Object[]{ inHasEmbeddedTabs });
        }
        catch (NoSuchMethodException e) {}
        catch (InvocationTargetException e) {}
        catch (IllegalAccessException e) {}
        catch (IllegalArgumentException e) {}
    }
}
