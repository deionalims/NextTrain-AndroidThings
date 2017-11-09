package com.nalims.things.api;

import com.nalims.things.model.TrainResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SncfApi {

    String GARE = "/gare";
    String ARRIVEE = "/arrivee";
    String DEPART = "/depart";

    @GET(GARE + "/{fromStationId}" + ARRIVEE + "/{toStationId}")
    Observable<TrainResponse> getNextTrainsWithArrival(@Path("fromStationId") int from, @Path("toStationId") int to);

    @GET(GARE + "/{fromStationId}" + DEPART)
    Observable<TrainResponse> getNextTrains(@Path("fromStationId") int from);
}
