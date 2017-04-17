package com.nalims.things.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "train")
public class Train {
    @Element(name = "date")
    private String date;

    @Element(name = "num")
    private int num;

    @Element(name = "miss")
    private String miss;

    @Element(name = "term", required = false)
    private int term;

    @Element(name = "etat", required = false)
    private String etat;

    public String getDate() {
        return date;
    }

    public int getNum() {
        return num;
    }

    public String getMiss() {
        return miss;
    }

    public int getTerm() {
        return term;
    }

    public String getEtat() {
        return etat;
    }
}
