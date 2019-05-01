package com.forasoft.taskforforasoft;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private Album album;
    private RequestToItunesAPI requestToItunesAPI;
    public TextView name_album, name_artist, copyright, primary_genre_name, release_date, track_сount;


    public class CallBackForUpdateDataAlbum implements com.forasoft.taskforforasoft.Callback{
        // в методе call осуществляется инициализация интерфейса прокручивающегося листа с треками
        @Override
        public void call(List<Parcelable> result_list, final boolean type) {
            AlbumActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(type){
//                        name_artist.setText(result[0]);
//                        copyright.setText(result[1]);
//                        primary_genre_name.setText(result[2]);
//                        release_date.setText(result[3]);
//                        track_сount.setText(result[4]);
                    }else{
                        Log.d("debug", "call");
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        name_album = (TextView)findViewById(R.id.name_album);
        name_artist = (TextView)findViewById(R.id.name_artist);
        copyright = (TextView)findViewById(R.id.copyright);
        primary_genre_name = (TextView)findViewById(R.id.primary_genre_name);
        release_date = (TextView)findViewById(R.id.release_date);
        track_сount = (TextView)findViewById(R.id.track_сount);

        try {
            // получаем объект Album, он реализует интерфейс Parcelable, чтобы можно было передать его через Intent.
            // Можно использовать Serializable, но Parcelable работает быстрее с простыми типами данных, такими как int, double, String(объект).
            album = getIntent().getExtras().getParcelable("album");

            // заполняем поля Activity
            name_album.setText(album.getAlbumCensoredName());
            name_artist.setText(album.getArtistName());
            copyright.setText(album.getCopyright());
            primary_genre_name.setText(album.getPrimaryGenreName());
            release_date.setText(album.getReleaseDate());
            track_сount.setText(album.getTrackCount());

            requestToItunesAPI = new RequestToItunesAPI();
//            requestToItunesAPI.universalRequest("album", album_name, new CallBackForUpdateDataAlbum(), album);
        }catch(NullPointerException ex){
            Log.e("null point ex", "intent empty", ex);
        }
    }

}
















