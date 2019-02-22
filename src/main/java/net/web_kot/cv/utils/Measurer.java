package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.function.Supplier;

@UtilityClass
public class Measurer {

    static {
        Locale.setDefault(Locale.US);
    }

    public void invoke(Runnable runnable) {
        long time = System.currentTimeMillis();
        runnable.run();
        calcAndPrint(time);
    }

    public <T> T produce(Supplier<T> runnable) {
        long time = System.currentTimeMillis();
        T result = runnable.get();
        calcAndPrint(time);
        return result;
    }

    private void calcAndPrint(long start) {
        double result = (System.currentTimeMillis() - start) / 1000D;
        System.out.println(String.format("Done in %.3f s", result));
    }

}
