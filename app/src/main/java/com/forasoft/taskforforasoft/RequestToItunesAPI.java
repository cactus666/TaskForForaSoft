package com.forasoft.taskforforasoft;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestToItunesAPI {

    private OkHttpClient okHttpClient;
    private StringBuilder url_for_request;
    private JSONObject rootJsonObject;
    private JSONArray resJsonArray;


    public RequestToItunesAPI(){
        okHttpClient = new OkHttpClient();
        url_for_request = new StringBuilder();
    }

    // API Itunes довольно просто и понадобится только один универсальный запрос, в аргументы которого будут подаваться нужные значения.
    // term - используется как ключевое слово(словосочетание) поиска
    // media - задает общий тип, получаемого результата. По условию задания - music. (по этому можно сразу определить его)
    // entity - задает конкретный тип того чего ведется поиск, зависит от media. По условию задания - album и musicTrack.
    // attribute - указывает к какому конкретному типу относится term. Использовать я его буду, т. к. для треков
    //             нужно использовать entity = musicTrack, и если не указать этот атрибут,
    //             то могут попасти в результат песни названия которых будут совпадать с названием альбома(заданном в жтом поле term).
    // limit - ограничитель количества результатов.

    // варианты использования метода:
    // 1) entity = album; term = String (все, что угодно: название альма, исполнителя, песни), из-за избыточной инф. в каждом объекте получаем данных о альбоме (даже если указан трек)
    // 2) entity = musicTrack; term = albumName (задает программа, на основании вабранного альбома)

//    example use method  - new RequestToItunesAPI().universalRequest("album", "Нищая страна");
//                        - new RequestToItunesAPI().universalRequest("musicTrack", "белые хлопья");
    public void universalRequest(final String entity, String term, final com.forasoft.taskforforasoft.Callback callbackForResult){
        // используем этот метод(чистим url_for_request), по 2 причинам:
        // 1. этот метод не пересоздает массив, как метод delete, а просто заполняет 0
        // 2. если использовать метод delete, то теряется расширяемось, а так первую часть адреса можно вынести в агументы к запросу.
        url_for_request.setLength(0);
        // генерируется url по агрументам
        url_for_request
                .append("https://itunes.apple.com/search?media=music&entity=")
                .append(entity)
                .append("&term=")
                .append(term);
        // зачем делать эту проверку я описал выше в пункте о attribute
        if(entity.intern() == "musicTrack"){
            url_for_request.append("&attribute=albumTerm");
        }
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url_for_request.toString())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("fail_request", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    Log.d("url", url_for_request.toString());
                    // получаем результат
                    String result = response.body().string();
//                    Log.d("result", result);
                    // конвертируем полученный результат в json представление
                    rootJsonObject = new JSONObject(result);
//                    Log.d("result", rootJsonObject.toString());
                    if(entity.intern() == "album"){
                        // получпем количество найденных альбомов
                        int countAlbums = rootJsonObject.getInt("resultCount");
                        // вытягиваем из rootJsonObject json массив, в котором лежат все json объекты (альбомы)
                        resJsonArray = rootJsonObject.getJSONArray("results");
                        JSONObject albumJsonObject;
                        // создаем лист объектов, используем конкретную реализацию - ArrayList, т.к. подкопотом там массив, сразу задаем капасити, чтобы не расширять массив в двое
                        List result_list = new ArrayList<>(countAlbums);
                        // проходимся по каждому альбому и вызываем метод обработки альбома
                        for(int i = 0; i < countAlbums; i++){
                            albumJsonObject = resJsonArray.getJSONObject(i);
                            // вызываем функцию заполнения объекта Album
                            result_list.add(fillAlbumObject(albumJsonObject));
                        }
                        callbackForResult.call(result_list, true);
                    }else if(entity.intern() == "musicTrack"){
                        // получпем количество найденных треков
                        int countTrack = rootJsonObject.getInt("resultCount");
                        // вытягиваем из rootJsonObject json массив, в котором лежат все json объекты (треки)
                        resJsonArray = rootJsonObject.getJSONArray("results");
                        JSONObject trackJsonObject;
                        // проходимся по каждому треку и вызываем метод обработки трека
                        for(int i = 0; i < countTrack; i++){
                            trackJsonObject = resJsonArray.getJSONObject(i);
                            System.out.println(new MusicTrack(
                                    trackJsonObject.getString("trackCensoredName"),
                                    trackJsonObject.getInt("trackTimeMillis"),
                                    trackJsonObject.getInt("trackNumber")
                            ).toString());
                        }
                    }else{
                        Log.e("err", "check request parameter(entity)");
                    }
                } catch (JSONException ex) {
                    Log.e("err", "error with parse json result", ex);
                }
            }
        });
    }


// метод заполнения объекта альбом
    Album fillAlbumObject(JSONObject albumJsonObject){
        String artistName, collectionCensoredName, artworkUrl60, copyright, primaryGenreName, releaseDate;
        int trackCount;

        try {
            artistName = albumJsonObject.getString("artistName");
        } catch (JSONException e) {
            artistName = "missing";
        }

        try {
            collectionCensoredName = albumJsonObject.getString("collectionCensoredName");
        } catch (JSONException e) {
            collectionCensoredName = "missing";
        }

        try {
            artworkUrl60= albumJsonObject.getString("artworkUrl60");
        } catch (JSONException e) {
            artworkUrl60 = "missing";
        }

        try {
            trackCount = albumJsonObject.getInt("trackCount");
        } catch (JSONException e) {
            trackCount = 0;
        }

        try {
            copyright = albumJsonObject.getString("copyright");
        } catch (JSONException e) {
            copyright = "missing";;
        }

        try {
            primaryGenreName = albumJsonObject.getString("primaryGenreName");
        } catch (JSONException e) {
            primaryGenreName = "missing";;
        }

        try {
            releaseDate = albumJsonObject.getString("releaseDate");
        } catch (JSONException e) {
            releaseDate = "missing";;
        }

        Album album_object = new Album(artistName, collectionCensoredName, artworkUrl60, trackCount, copyright, primaryGenreName, releaseDate);

        return album_object;
    }
}


