package com.github.xy02.util;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Rx {
    public static <K, V> Function<K, Observable<V>> getSubValues(Observable<V> values, Function<V, K> keySelector) {
        ConcurrentHashMap<K, Set<ObservableEmitter<V>>> m = new ConcurrentHashMap<>();
        Completable theEnd = values
                .doOnNext(v -> {
                    K key = keySelector.apply(v);
                    Set<ObservableEmitter<V>> emitterSet = m.get(key);
                    if (emitterSet != null) {
                        for (ObservableEmitter<V> emitter :emitterSet) {
                            emitter.onNext(v);
                        }
                    }
                })
                .ignoreElements()
                .cache();
        return key -> Observable.create(emitter -> {
            Set<ObservableEmitter<V>> emitterSet = m.get(key);
            if (emitterSet == null) {
                emitterSet = Collections.newSetFromMap(new ConcurrentHashMap<>());;
                m.put(key, emitterSet);
            }
            emitterSet.add(emitter);
            Disposable d = theEnd.subscribe(emitter::onComplete, emitter::onError);
            Set<ObservableEmitter<V>> finalEmitterSet = emitterSet;
            emitter.setDisposable(Disposable.fromAction(() -> {
                finalEmitterSet.remove(emitter);
                if (finalEmitterSet.size() == 0){
                    m.remove(key);
                }
                d.dispose();
            }));
        });
    }
}

