package com.example.timertracker.Model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NestedArrayListConverter {

    @TypeConverter
    public static ArrayList<ArrayList<Long>> fromString(String value) {
        Type listType = new TypeToken<ArrayList<ArrayList<Long>>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromNestedArrayList(ArrayList<ArrayList<Long>> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
