package rd.zhang.aio.kotlin.core.wried;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 2017/9/17.
 */

public class SingletonProvider {

    private Object provider;

    public SingletonProvider(Object provider) {
        this.provider = provider;
    }

    private static SingletonProvider singletonProvider = null;

    public static SingletonProvider instance(Object provider) {
        if (provider == null) {
            throw new NullPointerException();
        }
        if (singletonProvider == null) {
            synchronized (SingletonProvider.class) {
                if (singletonProvider == null) {
                    singletonProvider = new SingletonProvider(provider);
                }
            }
        }
        return singletonProvider;
    }

    private static Map<Class, SingletonProvider> data = new HashMap<>();

    public static <T> T get(Class<T> clazz, Object value) {
        SingletonProvider instance = data.get(clazz);
        if (instance == null) {
            instance = instance(value);
            data.put(clazz, instance);
        }
        return (T) instance.provider;
    }

}
