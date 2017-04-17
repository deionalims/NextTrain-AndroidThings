package com.nalims.things.api;

import com.nalims.things.ThingsApplication;
import com.nalims.things.main.MainComponent;
import com.nalims.things.main.MainModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {
    MainComponent plus(MainModule mainModule);
    void inject(ThingsApplication thingsApplication);
}
