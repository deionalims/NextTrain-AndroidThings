package com.nalims.things.main;

import dagger.Subcomponent;

@Subcomponent(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
