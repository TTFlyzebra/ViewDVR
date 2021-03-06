package com.longhorn.viewdvr.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: FlyZebra
 * Created by FlyZebra on 2018/3/29-下午1:25.
 */
public class GsonUtils {
    private static Gson gson = null;
    static {
        if(gson == null){
            gson = new Gson();
        }
    }

    public static <T> T json2Object(String jsonStr, Class<T> cls){
        try {
            return gson.fromJson(jsonStr,cls);
        } catch (JsonSyntaxException e) {
            FlyLog.e(e.toString());
            return null;
        }
    }

    public static <T> List<T> json2ListObj(String jsonStr,Class<T> cls){
        try {
            return gson.fromJson(jsonStr, new ListOfJson(cls));
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static class ListOfJson<T> implements ParameterizedType {
        private Class<?> wrapped;
        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }
        @Override
        public Type getRawType() {
            return List.class;
        }
        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static Map<String,String> json2Map(String json){
        return json2Object(json,Map.class);
    }

    public static HashMap<String,String> json2HashMap(String json){
        return json2Object(json,HashMap.class);
    }

    public static <T> String mapToJson(Map<String, T> map) {
        try{
            return gson.toJson(map);
        }catch (JsonSyntaxException e){
            FlyLog.d(e.toString());
            return null;
        }
    }

    public static String objectToJson(Object object){
        try {
            return gson.toJson(object);
        }catch (Exception e){
            FlyLog.i(e.toString());
            return null;
        }
    }

}
