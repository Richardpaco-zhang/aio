package rd.zhang.aio.kotlin.core.wried;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 2017/9/14.
 */
public class HttpProvider<T> implements Factory<T> {

    private static Map<Class, HttpProvider> https = new HashMap<>();

    private NewValue<T> getValue;

    @Override
    public T get() {
        if (getValue == null) {
            throw new NullPointerException();
        }
        return getValue.get();
    }

    public HttpProvider(NewValue value) {
        this.getValue = value;
    }

    public static <T> HttpProvider<T> check(Class<T> type, NewValue value) {
        HttpProvider provider = https.get(type);
        if (provider == null) {
            provider = new HttpProvider(value);
            https.put(type, provider);
        }
        return provider;
    }
}
