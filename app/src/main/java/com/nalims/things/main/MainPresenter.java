package com.nalims.things.main;

import com.nalims.things.api.SncfRepository;
import com.nalims.things.model.Train;
import com.nalims.things.model.TrainResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainPresenter {

    private static int INTERVAL = 10;
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";
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
        scheduledExecutorService.scheduleAtFixedRate(() -> sncfRepository.getNextTrains(COLOMBES_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainResponseReceived, this::onError), 0, INTERVAL,
            TimeUnit.SECONDS);
    }

    private void onTrainResponseReceived(TrainResponse trainResponse) {
        Train train = null;

        for (Train tr : trainResponse.getTrainList()) {
            if (tr.getTerm() == SAINT_LAZARE_ID) {
                train = tr;
                break;
            }
        }

        if (train == null){
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(train.getDate()));
        } catch (ParseException e) {
            onError(e);
        }

        Date now = new Date();
        long diffInMill = calendar.getTimeInMillis() - now.getTime();
        int diffInMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(diffInMill);
        diffInMinutes = diffInMinutes < 0 ? 0 : diffInMinutes;
        if (diffInMinutes > 60) {
            int hours = (int) TimeUnit.MILLISECONDS.toHours(diffInMill);
            int minutes = diffInMinutes % 60;
            screen.display(hours + "h" + minutes);
        } else {
            screen.display(diffInMinutes + "mn");
        }
    }

    private void onError(Throwable t) {
        screen.display("ERR");
    }

    void unbind() {
        screen = null;
    }
}
