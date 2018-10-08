package com.nalims.things.api;

import android.content.Context;
import com.nalims.things.R;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;

@Module public class ApiModule {

    private static final String BASE_URL = "https://api.transilien.com/";

    private final Context context;

    public ApiModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(ApiHeadersInterceptor apiHeadersInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Level.BODY);

        return new OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(apiHeadersInterceptor)
            .build();
    }

    @Singleton
    @Provides
    SncfApi provideSncfApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(SncfApi.class);
    }

    @Singleton
    @Provides
    SncfRepository provideSncfRepository(SncfApi sncfApi) {
        return new SncfRepository(sncfApi);
    }

    @Singleton
    @Provides
    ApiHeadersInterceptor provideApiHeadersInterceptor(@ApiCredentials String credentials) {
        return new ApiHeadersInterceptor(credentials);
    }

    @Singleton
    @ApiCredentials
    @Provides
    String provideBase64Credentials() {
        String log = context.getResources().getString(R.string.api_login);
        String pass = context.getResources().getString(R.string.api_password);

        String input = log + ":" + pass;

        String base64String = null;
        try {
            byte[] data = input.getBytes("UTF-8");
            base64String = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return base64String;
    }
}
