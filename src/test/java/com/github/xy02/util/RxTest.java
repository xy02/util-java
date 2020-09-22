package com.github.xy02.util;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RxTest {
    @Test
    public void testGetSubValues() throws InterruptedException {
        var f = Rx.getSubValues(Observable.interval(1, TimeUnit.SECONDS), v -> v % 2 == 0);
        f.apply(true).subscribe(x->System.out.println(x+":x:"+Thread.currentThread().getId()));
        f.apply(false).subscribe(x->System.out.println(x+":y:"+Thread.currentThread().getId()));
        Thread.sleep(5000);
    }
}
