package com.forasoft.taskforforasoft;

import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TimeUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        String url = createURL(entity, idAlbum, term);
        if(url == null){ return; }

        // отправляем запрос
        final com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("fail_request", e.toString());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    Log.i("url", url_for_request.toString());
                    // получаем результат и конвертируем полученный результат в json представление
                    rootJsonObject = new JSONObject(response.body().string());
                    if(entity.intern() == "album"){
                        // получпем количество найденных альбомов
                        int countAlbums = rootJsonObject.getInt("resultCount");
                        // проверяются совпадения, если есть хотябы 1 альбом, то все хорошо, иначе возвращаем callback'у null
                        if(countAlbums == 0){
                            callbackForResult.callForAlbum(null);
                            return;
                        }
                        // создаем лист объектов
                         List result_list = createResultListForAlbum(countAlbums, rootJsonObject);
                         if(result_list == null) { return; }

                         // сортирую в алфавитном порядке
                        result_list.sort(new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                              return ((Album)o1).getAlbumCensoredName().compareToIgnoreCase(((Album)o2).getAlbumCensoredName());
                            }
                        });
                        callbackForResult.callForAlbum(result_list);
                    }else if(entity.intern() == "song"){
                        // создаем лист объектов
                        List<Map<String, Object>> result_list = createResultListForTrack(rootJsonObject);
                        if(result_list == null){ return; }
                        callbackForResult.callForTrack(result_list);
                    }else{
                        Log.e("err", "check request parameter(entity)");
                    }
                } catch (JSONException ex) {
                    Log.e("err", "error with parse json result", ex);
                }
            }
        });
    }


    String createURL(String entity, Integer idAlbum, String term){
        // используем этот метод(чистим url_for_request), по 2 причинам:
        // 1. этот метод не пересоздает массив, как метод delete, а просто заполняет 0
        // 2. если использовать метод delete, то теряется расширяемось, а так первую часть адреса можно вынести в агументы к запросу.
        url_for_request.setLength(0);

        // генерируется url по агрументам
        if(entity.intern() == "song"){
            if(idAlbum == null)
                return null;
            url_for_request
                    .append("https://itunes.apple.com/lookup?id=")
                    .append(idAlbum)
                    .append("&entity=")
                    .append(entity);
        }else{
            if(term == null)
                return null;
            url_for_request
                    .append("https://itunes.apple.com/search?media=music&entity=")
                    .append(entity)
                    .append("&term=")
                    .append(term);
        }
        return url_for_request.toString();
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

    // метод заполнения map для трека
    Map<String, Object>  fillMusicTrackMap(JSONObject albumJsonObject, int position){
        Map<String, Object> track_map = new HashMap<>();

        try {
            track_map.put("track_name", albumJsonObject.getString("trackCensoredName"));
        } catch (JSONException e) {
            // безсмысленно выводить трек, если нет названия
            return null;
        }

        try {
            track_map.put("track_time", formationTimePlay(albumJsonObject.getInt("trackTimeMillis")));
        } catch (JSONException e) {
            track_map.put("track_time", "0");
        }

        track_map.put("track_number", position);

        return track_map;
    }

    // метод формирующий время проишрывания трека
    String formationTimePlay(int millis){
        // пока никто не видет можно делать канкатенацию строк чере "+"
        return TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    // метод создания result_list из rootJsonObject для листа из альбомов
    List createResultListForAlbum(int countAlbums, JSONObject rootJsonObject){
        // создаем лист объектов, используем конкретную реализацию - ArrayList, т.к. подкопотом там массив, сразу задаем капасити, чтобы не расширять массив в двое
        List result_list = new ArrayList<>(countAlbums);
        Album new_album;
        JSONObject albumJsonObject;
        try {
            // вытягиваем из rootJsonObject json массив, в котором лежат все json объекты (альбомы)
            resJsonArray = rootJsonObject.getJSONArray("results");
            // проходимся по каждому альбому и вызываем метод обработки альбома
            for (int i = 0; i < countAlbums; i++) {
                albumJsonObject = resJsonArray.getJSONObject(i);
                new_album = fillAlbumObject(albumJsonObject);
                if (new_album != null) {
                    // вызываем функцию заполнения объекта Album
                    result_list.add(new_album);
                }
            }
        }catch (JSONException ex) {
            Log.e("err", "error with parse json result for album", ex);
        }
        return result_list;
    }

    // метод создания result_list из rootJsonObject для листа из треков
    List<Map<String, Object>> createResultListForTrack(JSONObject rootJsonObject){
        Map<String, Object> new_track;
        JSONObject trackJsonObject;
        List<Map<String, Object>> result_list = null;

        try {
            // получпем количество найденных треков
            int countTrack = rootJsonObject.getInt("resultCount");
            // вытягиваем из rootJsonObject json массив, в котором лежат все json объекты (треки)
            resJsonArray = rootJsonObject.getJSONArray("results");

            // создаем лист объектов, используем конкретную реализацию - ArrayList, т.к. подкопотом там массив, сразу задаем капасити, чтобы не расширять массив в двое
            result_list = new ArrayList<>(countTrack);
            // проходимся по каждому треку и вызываем метод обработки трека, пропускае первый, т.к. это альбом
            for (int i = 1; i < countTrack; i++) {
                trackJsonObject = resJsonArray.getJSONObject(i);
                new_track = fillMusicTrackMap(trackJsonObject, i);
                if (new_track != null) {
                    // вызываем функцию заполнения объекта MusicTrack
                    // i задает номер трека, т.к. то что получаю в реквесте бывает имеет одинаковые значения
                    result_list.add(new_track);
                }
            }
        }catch(JSONException ex){
            Log.e("err", "error with parse json result for track", ex);
        }
        return result_list;
    }
}


