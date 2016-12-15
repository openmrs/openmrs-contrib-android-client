package org.openmrs.mobile.test;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class MockResponseBody extends ResponseBody {
    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public BufferedSource source() {
        return null;
    }
}
