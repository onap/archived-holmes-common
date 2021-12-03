/**
 * Copyright 2018-2020 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Integer.class, (JsonDeserializer<Integer>) (json, typeOfT, context) -> {
                        try {
                            return json.getAsInt();
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
                        try {
                            return jsonElement == null ? null : new Date(jsonElement.getAsLong());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .create();
        }
    }

    private GsonUtil() {
    }


    public static String beanToJson(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    public static <T> T jsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    public static <T> List<T> jsonToList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, TypeToken.getParameterized(List.class, cls).getType());
        }
        return list;
    }

    public static <T> List<Map<String, T>> jsonToListMaps(String gsonString, Class<T> cls) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    TypeToken.getParameterized(List.class,
                            TypeToken.getParameterized(Map.class, String.class, cls).getType()
                    ).getType()
            );
        }
        return list;
    }

    public static <T> Map<String, T> jsonToMap(String gsonString, Class<T> cls) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, TypeToken.getParameterized(Map.class, String.class, cls).getType());
        }
        return map;
    }

    public static String getAsString(JsonObject o, String field) {
        String ret = null;

        if (field == null) {
            field = StringUtils.EMPTY;
        }

        if (o.has(field) && !o.get(field).isJsonNull()) {
            ret = o.get(field).getAsString();
        }
        return ret;
    }

    public static long getAsLong(JsonObject o, String field) {
        long ret = 0;

        if (field == null) {
            field = StringUtils.EMPTY;
        }

        if (o.has(field) && !o.get(field).isJsonNull()) {
            ret = o.get(field).getAsLong();
        }
        return ret;
    }

    public static int getAsInt(JsonObject o, String field) {
        int ret = 0;

        if (field == null) {
            field = StringUtils.EMPTY;
        }

        if (o.has(field) && !o.get(field).isJsonNull()) {
            ret = o.get(field).getAsInt();
        }
        return ret;
    }
}
