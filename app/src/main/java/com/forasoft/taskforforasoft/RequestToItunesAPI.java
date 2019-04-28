package com.forasoft.taskforforasoft;

import android.util.Log;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class RequestToItunesAPI {

    private OkHttpClient okHttpClient;
    private StringBuilder url_for_request;

    public RequestToItunesAPI(){
        okHttpClient = new OkHttpClient();
        url_for_request = new StringBuilder();
    }

    // API Itunes довольно просто и понадобится только один универсальный запрос, в аргументы которого будут подаваться нужные значения.
    // term - используется как ключевое слово(словосочетание) поиска
    // media - задает общий тип, получаемого результата. По условию задания - music. (по этому можно сразу определить его)
    // entity - задает конкретный тип того чего ведется поиск, зависит от media. По условию задания - album и musicTrack.
    // attribute - указывает к какому конкретному типу относится term. Использовать я его не буду, т. к. допускаю, что пользователь может спутать название группы с названием альбома.
    // limit - ограничитель количества результатов.

    public void universalRequest(final String entity, String term){
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
//                try {
                    String result = response.body().string();
                    if(entity.intern() == "album"){

                    }else if(entity.intern() == "musicTrack"){

                    }else{
                        Log.e("err", "check request parameter(entity)");
                    }
//                    JSONObject rootJsonObject = new JSONObject(str);
//                    JSONArray itemsJsonArray = rootJsonObject.getJSONArray("items");
//                    JSONObject itemsJsonObject = itemsJsonArray.getJSONObject(0);
//                    JSONObject statusJsonObject = itemsJsonObject.getJSONObject("status");

//                } catch (JSONException ex) {
//                    Log.e("err", "error with parse json result", ex);
//                }
            }
        });
    }
}
