package com.forasoft.taskforforasoft;

import android.os.Parcelable;

import java.util.List;
import java.util.Map;

public interface Callback {
    // подготовим интерфейс по которому нам будут возвращать данные из запроса
    // result_list - это лист из map
    void callForTrack(List<Map<String, Object>> result_list);

    // подготовим интерфейс по которому нам будут возвращать данные из запроса
    // result_list - это лист из объектов(альбомов)
    void callForAlbum(List<Parcelable> result_list);
}
