package com.nalims.things.main;

import android.util.Log;
import com.nalims.things.api.SncfRepository;
import com.nalims.things.model.Train;
import com.nalims.things.model.TrainResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainPresenter {

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";
    private static final String MINUTES_PATTERN = "mm'mn'";
    private static final String HOURS_MINUTES_PATTENR = "H'h'mm'mn'";
    private static final int COLOMBES_ID = 87381087;
    private static final int SAINT_LAZARE_ID = 87384008;

    private final SncfRepository sncfRepository;
    private MainScreen screen;
    private final ScheduledExecutorService scheduledExecutorService;

    MainPresenter(SncfRepository sncfRepository) {
        this.sncfRepository = sncfRepository;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    void bind(MainScreen mainScreen) {
        this.screen = mainScreen;
    }

    void getNextTrains() {
        int INTERVAL = 10;
        scheduledExecutorService.scheduleAtFixedRate(
            () -> sncfRepository.getNextTrainsWithArrival(COLOMBES_ID, SAINT_LAZARE_ID)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onTrainResponseReceived, this::onError), 0, INTERVAL, TimeUnit.SECONDS);
    }

    private void onTrainResponseReceived(TrainResponse trainResponse) {
        if (trainResponse.getTrainList() == null || trainResponse.getTrainList().size() == 0) {
            screen.display("NoTr");
            return;
        }

        long diff = trainResponse.getTrainList()
                                 .stream()
                                 .map(this::getDiff)
                                 .filter(dif -> dif >= 0)
                                 .findFirst()
                                 .orElse(-1L);

        if (diff < 0){
            onError(null);
        }

        Calendar diffCalendar = Calendar.getInstance();
        diffCalendar.setTimeInMillis(0);
        diffCalendar.setTimeInMillis(diff);

        SimpleDateFormat diffDateFormat =
            diffCalendar.get(Calendar.HOUR) > 0 ? new SimpleDateFormat(HOURS_MINUTES_PATTENR, Locale.FRANCE)
                                                : new SimpleDateFormat(MINUTES_PATTERN, Locale.FRANCE);
        screen.display(diffDateFormat.format(diffCalendar.getTime()));
    }

    private long getDiff(Train train) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.FRANCE);
        Calendar nextTrainCalendar = Calendar.getInstance();
        try {
            nextTrainCalendar.setTime(simpleDateFormat.parse(train.getDate()));
        } catch (ParseException e) {
            onError(e);
        }

        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, 2);
        Calendar diffCalendar = Calendar.getInstance();
        diffCalendar.setTimeInMillis(0);
        return nextTrainCalendar.getTimeInMillis() - now.getTime().getTime();
    }

    private void onError(Throwable t) {
        Log.e("ERROR", Arrays.toString(t.getStackTrace()));
        screen.display("ERR");
    }

    void unbind() {
        screen = null;
    }
}
