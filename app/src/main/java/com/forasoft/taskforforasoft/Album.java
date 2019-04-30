package com.forasoft.taskforforasoft;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Album implements Serializable {

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
    public Album(String artistName, String albumCensoredName, String urlImage){
        this.artistName = artistName;
        this.albumCensoredName = albumCensoredName;
        this.urlImage = urlImage;
    }

    // конструктор для создания объекта для страницы альбома
    public Album(String artistName, String albumCensoredName, String urlImage, int trackCount, String copyright, String primaryGenreName, String releaseDate){
        this.artistName = artistName;
        this.albumCensoredName = albumCensoredName;
        this.urlImage = urlImage;
        this.trackCount = trackCount;
        this.copyright = copyright;
        this.primaryGenreName = primaryGenreName;
        this.releaseDate = releaseDate;
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
                .append(artistName)
                .append(albumCensoredName)
                .append(urlImage)
                .append(trackCount)
                .append(copyright)
                .append(primaryGenreName)
                .append(releaseDate).toString();
    }
}
