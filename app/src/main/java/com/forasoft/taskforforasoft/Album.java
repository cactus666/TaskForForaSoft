package com.forasoft.taskforforasoft;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Album implements Parcelable {

    private String idAlbum;
    private String artistName;
    private String albumCensoredName;
    private String urlImage;
    private int trackCount;
    private String copyright;
    private String primaryGenreName;
    private String releaseDate; //2014-03-29T07:00:00Z
    private List trackList;

    // конструктор для создания объекта для листа из альбомов. Этот констр сильно бы уменьшил время ожидания результата,
    // если бы можно было указать, что мы хотим получить в конце
    // (например в API YouTube есть параметр part для ограничения объема результатов),
    // так и здесь при первом просмотре альбомов в листе нам не нужна вся информация о нем.
    // оставлю этот конструктор в надежде, что если API сделают лучше, то будет легко расширить функционал приложения...
    public Album(String idAlbum, String artistName, String albumCensoredName, String urlImage){
        this.idAlbum = idAlbum;
        this.artistName = artistName;
        this.albumCensoredName = albumCensoredName;
        this.urlImage = urlImage;
    }

    // конструктор для создания объекта для страницы альбома
    public Album(String idAlbum, String artistName, String albumCensoredName, String urlImage, int trackCount, String copyright, String primaryGenreName, String releaseDate){
        this.idAlbum = idAlbum;
        this.artistName = artistName;
        this.albumCensoredName = albumCensoredName;
        this.urlImage = urlImage;
        this.trackCount = trackCount;
        this.copyright = copyright;
        this.primaryGenreName = primaryGenreName;
        this.releaseDate = releaseDate;
    }


    private Album(Parcel in) {
        idAlbum = in.readString();
        artistName = in.readString();
        albumCensoredName = in.readString();
        urlImage = in.readString();
        trackCount = in.readInt();
        copyright = in.readString();
        primaryGenreName = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getIdAlbum() {
        return idAlbum;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumCensoredName() {
        return albumCensoredName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List getTrackList() {
        return trackList;
    }

    public void setTrackList(List trackList) {
        this.trackList = trackList;
    }

    @Override
    public String toString(){
        return new StringBuilder("albumObject: ")
                .append(" id = ").append(idAlbum)
                .append(" artistName = ").append(artistName)
                .append(" albumCensoredName = ").append(albumCensoredName)
                .append(" urlImage = ").append(urlImage)
                .append(" trackCount = ").append(trackCount)
                .append(" copyright = ").append(copyright)
                .append(" primaryGenreName = ").append(primaryGenreName)
                .append(" releaseDate = ").append(releaseDate).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idAlbum);
        dest.writeString(artistName);
        dest.writeString(albumCensoredName);
        dest.writeString(urlImage);
        dest.writeInt(trackCount);
        dest.writeString(copyright);
        dest.writeString(primaryGenreName);
        dest.writeString(releaseDate);
    }
}
