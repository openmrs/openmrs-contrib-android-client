package org.openmrs.mobile.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class ACUnitTestBase {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

}
