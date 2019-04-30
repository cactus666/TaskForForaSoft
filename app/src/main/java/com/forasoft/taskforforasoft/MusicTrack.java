package com.forasoft.taskforforasoft;

import java.io.Serializable;

public class MusicTrack implements Serializable {
    private String trackCensoredName;
    private int trackTimeMillis;
    private int trackNumber;

    public MusicTrack(String trackCensoredName, int trackTimeMillis, int trackNumber){
        this.trackCensoredName = trackCensoredName;
        this.trackTimeMillis = trackTimeMillis;
        this.trackNumber = trackNumber;
    }

    public String getTrackCensoredName() {
        return trackCensoredName;
    }

    public int getTrackTimeMillis() {
        return trackTimeMillis;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    @Override
    public String toString(){
        return new StringBuilder("trackObject: ")
                .append(trackCensoredName)
                .append(trackTimeMillis)
                .append(trackNumber).toString();
    }
}
