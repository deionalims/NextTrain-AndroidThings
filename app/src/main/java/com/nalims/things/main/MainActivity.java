package com.nalims.things.main;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.pio.Gpio;
import com.nalims.things.ThingsApplication;
import java.io.IOException;
import javax.inject.Inject;

public class MainActivity extends Activity implements MainScreen {

    AlphanumericDisplay display;
    @Inject MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThingsApplication.getThingsApplication(this)
            .getApiComponent()
            .plus(new MainModule())
            .inject(this);

        mainPresenter.bind(this);

        try {
            display = RainbowHat.openDisplay();
            display.setEnabled(true);
            display.display("LOAD");

            // Light up the Red LED.
            Gpio led = RainbowHat.openLed(RainbowHat.LED_RED);
            led.setValue(true);
            // Close the device when done.
            led.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        try {
            display.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPresenter.unbind();
        super.onDestroy();
    }
}
