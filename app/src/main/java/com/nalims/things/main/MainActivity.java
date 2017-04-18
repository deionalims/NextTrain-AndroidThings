package com.nalims.things.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.nalims.things.ThingsApplication;
import java.io.IOException;
import javax.inject.Inject;

public class MainActivity extends Activity implements MainScreen {

    @Inject MainPresenter mainPresenter;

    private AlphanumericDisplay display;

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

            // Light up the rainbow
            Apa102 ledstrip = RainbowHat.openLedStrip();
            ledstrip.setBrightness(0);
            int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
            for (int i = 0; i < rainbow.length; i++) {
                rainbow[i] = Color.HSVToColor(255, new float[]{i * 360.f / rainbow.length, 1.0f, 1.0f});
            }
            ledstrip.write(rainbow);
            // Close the device when done.
            ledstrip.close();

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
        mainPresenter.unbind();
        try {
            display.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
