package com.nalims.things.api;

import com.nalims.things.model.TrainResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class SncfRepository {

    private final SncfApi sncfApi;

    public SncfRepository(SncfApi sncfApi) {
        this.sncfApi = sncfApi;
    }

    public Observable<ResponseBody> getNextTrainsWithArrival(int from, int to){
        return sncfApi.getNextTrainsWithArrival(from, to);
    }

    public Observable<TrainResponse> getNextTrains(int from){
        return sncfApi.getNextTrains(from);
    }
}
