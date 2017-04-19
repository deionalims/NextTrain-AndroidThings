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
        Train nextTrain = null;

        for (Train tr : trainResponse.getTrainList()) {
            if (tr.getTerm() == SAINT_LAZARE_ID) {
                nextTrain = tr;
                break;
            }
        }

        if (nextTrain == null){
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        Calendar nextTrainCalendar = Calendar.getInstance();
        try {
            nextTrainCalendar.setTime(simpleDateFormat.parse(nextTrain.getDate()));
        } catch (ParseException e) {
            onError(e);
        }

        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, 2); // <-- My device is not set on my Time Zone. Hence the +2 hours.

        long diffInMill = nextTrainCalendar.getTimeInMillis() - now.getTimeInMillis();
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
