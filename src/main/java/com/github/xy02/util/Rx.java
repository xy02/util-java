package com.github.xy02.util;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Rx {
    public static <K, V> Function<K, Observable<V>> getSubValues(Observable<V> values, Function<V, K> keySelector) {
        var m = new ConcurrentHashMap<K, Set<ObservableEmitter<V>>>();
        var theEnd = values
                .doOnNext(v -> {
                    var key = keySelector.apply(v);
                    var emitterSet = m.get(key);
                    if (emitterSet != null) {
                        emitterSet.forEach(emitter -> emitter.onNext(v));
                    }
                })
                .ignoreElements()
                .cache();
        return key -> Observable.create(emitter -> {
            var emitterSet = m.computeIfAbsent(key, k -> new HashSet<>());
            emitterSet.add(emitter);
            var d = theEnd.subscribe(emitter::onComplete, emitter::onError);
            emitter.setDisposable(Disposable.fromAction(() -> {
                emitterSet.remove(emitter);
                d.dispose();
            }));
        });
    }
}

