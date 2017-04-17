package com.nalims.things.main;

import com.nalims.things.api.SncfRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {
    @Provides MainPresenter provideMainPresenter(SncfRepository sncfRepository){
        return new MainPresenter(sncfRepository);
    }
}
