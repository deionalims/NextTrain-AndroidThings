package com.nalims.things;

import android.app.Application;
import android.content.Context;
import com.nalims.things.api.ApiComponent;
import com.nalims.things.api.ApiModule;
import com.nalims.things.api.DaggerApiComponent;

public class ThingsApplication extends Application {

    private ApiComponent apiComponent;


    public ApiComponent getApiComponent(){
        if (apiComponent == null){
            apiComponent = DaggerApiComponent.builder().apiModule(new ApiModule(this)).build();
        }

        return apiComponent;
    }

    public static ThingsApplication getThingsApplication(Context context){
        return (ThingsApplication) context.getApplicationContext();
    }
}
