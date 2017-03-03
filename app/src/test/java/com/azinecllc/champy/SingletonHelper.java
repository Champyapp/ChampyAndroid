package com.azinecllc.champy;

import java.lang.reflect.Field;

/**
 * Created by SashaKhyzhun on 3/3/17.
 */

public class SingletonHelper {

    public static void resetSingleton(Class clazz, String mInstance) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(mInstance);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
