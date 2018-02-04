package com.nalims.things.main;

import com.nalims.things.api.SncfRepository;
import com.nalims.things.model.Train;
import com.nalims.things.model.TrainResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public MainPresenter(SncfRepository sncfRepository) {
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
        if (trainResponse.getTrainList() == null || trainResponse.getTrainList().size() == 0){
            screen.display("NoTr");
            return;
        }
        Train nextTrain = trainResponse.getTrainList().get(0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        Calendar nextTrainCalendar = Calendar.getInstance();
        try {
            nextTrainCalendar.setTime(simpleDateFormat.parse(nextTrain.getDate()));
        } catch (ParseException e) {
            onError(e);
        }

        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, 1);
        Calendar diffCalendar = Calendar.getInstance();
        diffCalendar.setTimeInMillis(0);
        long diff = nextTrainCalendar.getTimeInMillis() - now.getTime().getTime();
        if (diff < 0) {
            screen.display("LATE");
            return;
        }
        diffCalendar.setTimeInMillis(diff);

        SimpleDateFormat diffDateFormat =
            diffCalendar.get(Calendar.HOUR) > 0 ? new SimpleDateFormat(HOURS_MINUTES_PATTENR)
                : new SimpleDateFormat(MINUTES_PATTERN);
        screen.display(diffDateFormat.format(diffCalendar.getTime()));
    }

    private void onError(Throwable t) {
        screen.display("ERR");
    }

    void unbind() {
        screen = null;
    }
}
