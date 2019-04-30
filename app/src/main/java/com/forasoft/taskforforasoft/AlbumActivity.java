package com.forasoft.taskforforasoft;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AlbumActivity extends AppCompatActivity {

    private String album_name;
    private RequestToItunesAPI requestToItunesAPI;
    public static TextView name_artist, copyright, primary_genre_name, release_date, track_сount;


    interface Callback {
        // подготовим интерфейс по которому нам будут возвращать данные из запроса
        // type задается для идентификации запроса true - результат по альбомам, false - результат по трекам
        void call(String result, boolean type);
    }


    public class CallBackForUpdateDataAlbum implements Callback{
        // в методе call осуществляется инициализация(возможное обновление, хотя я его так не использую, как следствие расширяемость) интерфейса
        @Override
        public void call(String result, boolean type) {
            if(type){
                Log.d("res", result);
                name_artist.setText(result);

//                name_artist.setText(result[0]);
//                copyright.setText(result[1]);
//                primary_genre_name.setText(result[2]);
//                release_date.setText(result[3]);
//                track_сount.setText(result[4]);
            }else{
                Log.d("debug", "call");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ((TextView)findViewById(R.id.name_album)).setText(album_name);
        name_artist = (TextView)findViewById(R.id.name_artist);
        copyright = (TextView)findViewById(R.id.copyright);
        primary_genre_name = (TextView)findViewById(R.id.primary_genre_name);
        release_date = (TextView)findViewById(R.id.release_date);
        track_сount = (TextView)findViewById(R.id.track_сount);

        try {
            album_name = getIntent().getExtras().getString("album_name");
            requestToItunesAPI = new RequestToItunesAPI();
//            requestToItunesAPI.universalRequest("album", album_name);
//            AsyncTaskForCallToItunesAPI asyncTaskForCallToItunesAPI = new AsyncTaskForCallToItunesAPI(album_name, this);


//            Log.d("position", "0");
//            asyncTaskForCallToItunesAPI.execute();
//            Log.d("position", "1");



            requestToItunesAPI.universalRequest("album", album_name, new CallBackForUpdateDataAlbum());

        }catch(NullPointerException ex){
            Log.e("null point ex", "intent empty", ex);
        }
    }

}





class AsyncTaskForCallToItunesAPI extends AsyncTask<Void, Integer, Void> {

    Album album;
    Context context;
    RequestToItunesAPI requestToItunesAPI = new RequestToItunesAPI();
String name;

    public AsyncTaskForCallToItunesAPI(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.d("position", "2");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("position", "3");

        Log.d("position", "4");
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        Log.d("position", "5");

    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
        Log.d("position", "6");
        // можно что-то сделать при выходе, например вызвать метод добавления сообщения в комментарии "Всем пока"
    }
}
















