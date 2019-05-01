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

    // Есть 2 типа запросов "Search" и "Lookup":
    // - Lookup работает быстрее и в нем можно искать по Id, из чего следует, что поиск выведет кореектно и выведет только 1 результат.
    // Буду использовать этот тип запросов для поиска песен, id - id альбома, в результате получаю песни только этого альбама и не возникает коллизи
    // - Search используется для поиска подходящих значений, которые задаются в параметре term
    // Буду использовать этот подход для поиска альбомов, т.к. нужно паказть пользователю варианты, к тому же пользователь может ошибиться и ввести имя не альбома, а группы(например)
    // В результате поиска по альбому могут возникнуть коллизии, т.к. альбомы могут иметь одинаковое название.

    // Для Lookup должно entity быть song

    // API Itunes довольно просто и понадобится только один универсальный запрос, в аргументы которого будут подаваться нужные значения.
    // term - используется как ключевое слово(словосочетание) поиска
    // media - задает общий тип, получаемого результата. По условию задания - music. (по этому можно сразу определить его)
    // entity - задает конкретный тип того чего ведется поиск, зависит от media. По условию задания - album и musicTrack.
    // attribute - указывает к какому конкретному типу относится term. Использовать я его могбы(но не буду, т.к. для поисов треков есть Lookup), хотя если бы не было коллизий по назварниям и не было Lookup, то можно использовать этот подход.
    //             нужно использовать entity = musicTrack, и если не указать этот атрибут,
    //             то могут попасти в результат песни названия которых будут совпадать с названием альбома(заданном в поле term).
    //             attribute должен быть равен albumTerm.
    //      НО ЕСТЬ Lookup, поэтому attribute не используется.
    // limit - ограничитель количества результатов.

    // варианты использования метода:
    // 1) entity = album; term = String (все, что угодно: название альма, исполнителя, песни), из-за избыточной инф. в каждом объекте получаем данных о альбоме (даже если указан трек)
    // 2) entity = musicTrack; id = idAlbum (поиск по id, полученном ранее)

    public void universalRequest(final String entity, String term, final com.forasoft.taskforforasoft.Callback callbackForResult, Integer idAlbum){
        // используем этот метод(чистим url_for_request), по 2 причинам:
        // 1. этот метод не пересоздает массив, как метод delete, а просто заполняет 0
        // 2. если использовать метод delete, то теряется расширяемось, а так первую часть адреса можно вынести в агументы к запросу.
        url_for_request.setLength(0);

        // генерируется url по агрументам
        if(entity.intern() == "song"){
            if(idAlbum == null)
                return;
            url_for_request
                    .append("https://itunes.apple.com/lookup?id=")
                    .append(idAlbum)
                    .append("&entity=")
                    .append(entity);
        }else{
            if(term == null)
                return;
            url_for_request
                    .append("https://itunes.apple.com/search?media=music&entity=")
                    .append(entity)
                    .append("&term=")
                    .append(term);
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
                    Log.i("url", url_for_request.toString());
                    // получаем результат
                    String result = response.body().string();
//                    Log.d("result", result);
                    // конвертируем полученный результат в json представление
                    rootJsonObject = new JSONObject(result);
//                    Log.d("result", rootJsonObject.toString());
                    if(entity.intern() == "album"){
                        Album new_album;
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
                            new_album = fillAlbumObject(albumJsonObject);
                            if(new_album != null) {
                                // вызываем функцию заполнения объекта Album
                                result_list.add(new_album);
                            }
                        }
                        callbackForResult.call(result_list);
                    }else if(entity.intern() == "song"){
                        MusicTrack new_track;
                        // получпем количество найденных треков
                        int countTrack = rootJsonObject.getInt("resultCount");
                        // вытягиваем из rootJsonObject json массив, в котором лежат все json объекты (треки)
                        resJsonArray = rootJsonObject.getJSONArray("results");
                        JSONObject trackJsonObject;
                        // создаем лист объектов, используем конкретную реализацию - ArrayList, т.к. подкопотом там массив, сразу задаем капасити, чтобы не расширять массив в двое
                        List result_list = new ArrayList<>(countTrack);
                        // проходимся по каждому треку и вызываем метод обработки трека, пропускае первый, т.к. это альбом
                        for(int i = 1; i < countTrack; i++){
                            trackJsonObject = resJsonArray.getJSONObject(i);
                            new_track = fillMusicTrackObject(trackJsonObject);
                            if(new_track != null) {
                                // вызываем функцию заполнения объекта Album
                                result_list.add(new_track);
                            }
                        }
                        callbackForResult.call(result_list);
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
        String idAlbum, artistName, collectionCensoredName, artworkUrl60, copyright, primaryGenreName, releaseDate;
        int trackCount;

        try {
            idAlbum = albumJsonObject.getString("collectionId");
        } catch (JSONException e) {
            // безсмысленно выводить альбом, если нельзя будет получить песни
            return null;
        }

        try {
            artistName = albumJsonObject.getString("artistName");
        } catch (JSONException e) {
            // безсмысленно выводить альбом без названия
            return null;
        }

        try {
            collectionCensoredName = albumJsonObject.getString("collectionCensoredName");
        } catch (JSONException e) {
            collectionCensoredName = "missing";
        }

        try {
            artworkUrl60= albumJsonObject.getString("artworkUrl60");
        } catch (JSONException e) {
            artworkUrl60 = null;
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

        Album album_object = new Album(idAlbum, artistName, collectionCensoredName, artworkUrl60, trackCount, copyright, primaryGenreName, releaseDate);

        return album_object;
    }

    // метод заполнения объекта трек
    MusicTrack fillMusicTrackObject(JSONObject albumJsonObject){
        String trackCensoredName;
        int trackTimeMillis, trackNumber;

        try {
            trackCensoredName = albumJsonObject.getString("trackCensoredName");
        } catch (JSONException e) {
            // безсмысленно выводить трек, если нет названия
            return null;
        }

        try {
            trackTimeMillis = albumJsonObject.getInt("trackTimeMillis");
        } catch (JSONException e) {
            trackTimeMillis = 0;
        }

        try {
            trackNumber = albumJsonObject.getInt("trackNumber");
        } catch (JSONException e) {
            trackNumber = 0;
        }

        MusicTrack track_object = new MusicTrack(trackCensoredName, trackTimeMillis, trackNumber);

        return track_object;
    }
}


