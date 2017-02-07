package org.openmrs.mobile.test;

import org.junit.After;
import org.junit.Before;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Extend from this class to test presenters that use RxAndroid
 */
public abstract class ACUnitTestBaseRx extends ACUnitTestBase {

    @Before
    public void setUp(){
        super.initMocks();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }
}
