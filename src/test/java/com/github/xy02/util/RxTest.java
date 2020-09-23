package com.github.xy02.util;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RxTest {
    @Test
    public void testGetSubValues() throws Throwable {
        var f = Rx.getSubValues(Observable.interval(1, TimeUnit.SECONDS), v -> v % 2 == 0);
        var d =f.apply(true).subscribe(x -> System.out.println(x + ":x:" + Thread.currentThread().getId()));
        Observable.timer(2, TimeUnit.SECONDS).subscribe(x->d.dispose());
        f.apply(false).subscribe(x -> System.out.println(x + ":y:" + Thread.currentThread().getId()));
        Thread.sleep(5000);
    }
}
