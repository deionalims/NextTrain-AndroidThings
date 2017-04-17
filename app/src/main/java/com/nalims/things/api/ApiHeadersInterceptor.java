package com.nalims.things.api;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHeadersInterceptor implements Interceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = " Basic ";

    private final String credentials;

    public ApiHeadersInterceptor(String credentials) {
        this.credentials = credentials;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header(AUTHORIZATION, BASIC + credentials);

        return chain.proceed(builder.build());
    }
}
