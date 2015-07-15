package com.uiSearch.data.serializer;

import android.util.Log;

import com.google.gson.Gson;
import com.uiSearch.data.entity.BaseEntity;
import com.uiSearch.data.entity.SearchSuggestionsEntity;

import javax.inject.Singleton;

@Singleton
public class JsonDataManager {
    private final Gson gson = new Gson();

    public JsonDataManager() {}

    public String serialize(BaseEntity baseEntity) {
        String jsonString = gson.toJson(baseEntity,BaseEntity.class);
        return jsonString;
    }

    public BaseEntity[] deserialize(String jsonString) {
        BaseEntity[] baseEntity = gson.fromJson(jsonString, SearchSuggestionsEntity[].class);
        return baseEntity;
    }
}