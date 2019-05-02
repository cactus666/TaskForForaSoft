package com.forasoft.taskforforasoft;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class AlbumActivity extends AppCompatActivity {

    private Album album;
    private RequestToItunesAPI requestToItunesAPI;
    private TextView name_album, name_artist, copyright, primary_genre_name, release_date, track_сount;
    private ImageView label_album;
    private ListView list_with_tracks;

    public class CallBackForCreateTrackList implements com.forasoft.taskforforasoft.Callback{
        // в методе callForTrack осуществляется инициализация интерфейса прокручивающегося листа с треками
        @Override
        public void callForTrack(final List<Map<String, Object>> result_list) {
            AlbumActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] from = {"track_number", "track_name", "track_time"};
                    int[] to = {R.id.track_number, R.id.track_name, R.id.track_time};
                    // создаем адаптер
                    SimpleAdapter simpleAdapter = new SimpleAdapter(AlbumActivity.this, result_list, R.layout.item_for_list_with_tracks, from, to);
                    // присваиваем адаптер
                    list_with_tracks.setAdapter(simpleAdapter);
                }
            });
        }

        @Override
        public void callForAlbum(List<Parcelable> result_list) {
            // ничего здесь писать не нужно
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        label_album = (ImageView)findViewById(R.id.label_for_album);
        name_album = (TextView)findViewById(R.id.name_album);
        name_artist = (TextView)findViewById(R.id.name_artist);
//        copyright = (TextView)findViewById(R.id.copyright);
        primary_genre_name = (TextView)findViewById(R.id.primary_genre_name);
        release_date = (TextView)findViewById(R.id.release_date);
//        track_сount = (TextView)findViewById(R.id.track_сount);
        list_with_tracks = (ListView)findViewById(R.id.list_with_tracks);

        // меняю титульник на "Album"
        getSupportActionBar().setTitle("Album");

        try {
            // получаем объект Album, он реализует интерфейс Parcelable, чтобы можно было передать его через Intent.
            // Можно использовать Serializable, но Parcelable работает быстрее с простыми типами данных, такими как int, double, String(объект).
            album = getIntent().getExtras().getParcelable("album");

            // заполняем поля Activity
            name_album.setText(album.getAlbumCensoredName());
            name_artist.setText(album.getArtistName());
            // Copyright не успользую, потому что для накоторых альбомов он слишком большой и мешает простмотру страницы
//            copyright.setText(album.getCopyright());
            primary_genre_name.setText(album.getPrimaryGenreName());
            release_date.setText(album.getReleaseDate());
            // т.к в результате получаем кол. треков в альбоме + сам альбом, то нужно вычесть 1, и получится кол. песен
            // track_count не успользую, потому что для накоторых альбомов он не считает кол повторных треков(перезаливок), но выводит их в лист, что очень странно видеть
//            track_сount.setText((album.getTrackCount()-1)+"");
            // асинхронно загружаем картинку для альбома
            Picasso.with(this)
                    .load(album.getUrlImage())
                    .into(label_album);


            requestToItunesAPI = new RequestToItunesAPI();
            requestToItunesAPI.universalRequest("song", null, new CallBackForCreateTrackList(), new Integer(album.getIdAlbum()));
        }catch(NullPointerException ex){
            Log.e("null point ex", "intent empty", ex);
        }
    }

}
















