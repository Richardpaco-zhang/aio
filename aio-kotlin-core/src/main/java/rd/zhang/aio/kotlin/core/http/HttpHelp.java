package rd.zhang.aio.kotlin.core.http;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rd.zhang.aio.kotlin.core.function.FormatKt;

/**
 * Created by Richard on 2017/10/25.
 */
public class HttpHelp {

    public static <T> List<T> toArray(Reader reader, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
        ArrayList<JsonObject> jsonObjects = FormatKt.fromJson(reader, type);
        List<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(FormatKt.fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

}
