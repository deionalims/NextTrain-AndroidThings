package com.nalims.things.main;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.nalims.things.ThingsApplication;

import javax.inject.Inject;
import java.io.IOException;

public class MainActivity extends Activity implements MainScreen {

    @Inject
    MainPresenter mainPresenter;

    private AlphanumericDisplay display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThingsApplication.getThingsApplication(this)
                         .getApiComponent()
                         .plus(new MainModule())
                         .inject(this);

        try {
            display = RainbowHat.openDisplay();
            display.setEnabled(true);
            display.display("LOAD");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPresenter.bind(this);
        mainPresenter.getNextTrains();
    }

    @Override
    public void display(String toDisplay) {
        try {
            display.display(toDisplay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mainPresenter.unbind();
        try {
            display.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
