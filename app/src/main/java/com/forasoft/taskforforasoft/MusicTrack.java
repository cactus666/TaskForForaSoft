package com.forasoft.taskforforasoft;

import android.os.Parcel;
import android.os.Parcelable;


// раньше использовался для созданее листа, тепери не нужен:(
public class MusicTrack implements Parcelable {
    private String trackCensoredName;
    private int trackTimeMillis;
    private int trackNumber;

    public MusicTrack(String trackCensoredName, int trackTimeMillis, int trackNumber){
        this.trackCensoredName = trackCensoredName;
        this.trackTimeMillis = trackTimeMillis;
        this.trackNumber = trackNumber;
    }

    protected MusicTrack(Parcel in) {
        trackCensoredName = in.readString();
        trackTimeMillis = in.readInt();
        trackNumber = in.readInt();
    }

    public static final Creator<MusicTrack> CREATOR = new Creator<MusicTrack>() {
        @Override
        public MusicTrack createFromParcel(Parcel in) {
            return new MusicTrack(in);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackCensoredName);
        dest.writeInt(trackTimeMillis);
        dest.writeInt(trackNumber);
    }
}
