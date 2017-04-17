package com.nalims.things.model;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class TrainResponse {

    @Attribute(name = "gare")
    private int gare;
    @ElementList(inline = true) List<Train> trainList;

    public List<Train> getTrainList() {
        return trainList;
    }
}
