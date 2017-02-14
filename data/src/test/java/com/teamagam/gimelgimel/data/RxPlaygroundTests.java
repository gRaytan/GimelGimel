package com.teamagam.gimelgimel.data;

import org.junit.Test;

import java.util.Date;

import rx.Observable;
import rx.plugins.RxJavaSchedulersHook;
import rx.subjects.PublishSubject;

public class RxPlaygroundTests {


    @Test
    public void test() throws Exception {

        Date startingDate = new Date();

        Observable<Date> map = Observable.just(new Date());
//                .map(x -> new Date());

        Thread.sleep(1000);

        map.subscribe(date -> {
            long timeDif = date.getTime() - startingDate.getTime();
            if (timeDif < 1000) {
                //map is run before subscribe
                System.out.print("before");
            } else {
                //map is run on subscription
                System.out.print("on");
            }
        });
    }

    @Test
    public void name() throws Exception {

        Observable<Date> observable = Observable.just(createNull())
                .map(x -> {
                    System.out.println("map: " + Thread.currentThread());
                    return new Date();
                })
                .doOnNext(x ->
                        System.out.println("doOnNext: " + Thread.currentThread()));

        observable
                .subscribeOn(RxJavaSchedulersHook.createComputationScheduler())
//                .observeOn(RxJavaSchedulersHook.createNewThreadScheduler())
//                .map(x -> {
//                    System.out.println("observerOn: " + Thread.currentThread());
//                    return x;
//                })
//                .observeOn(RxJavaSchedulersHook.createIoScheduler())
                .subscribe(res -> {
                    System.out.println("subscriber: " + Thread.currentThread());
                });
        Thread.sleep(1000);
    }


    @Test
    public void publishSubject() throws Exception {
        PublishSubject<Date> s = PublishSubject.create();

        Observable<Date> observable = s.replay().autoConnect();
//                .observeOn(RxJavaSchedulersHook.createIoScheduler());
        observable.subscribe();
        Observable<Object> my = Observable.empty().mergeWith(observable);

        my

                .map(x -> {
                    System.out.println("map: " + Thread.currentThread());
                    return x;
                })
                .subscribeOn(RxJavaSchedulersHook.createIoScheduler())
                .observeOn(RxJavaSchedulersHook.createComputationScheduler())
                .subscribe(x -> {
                    System.out.println("subscriber: " + Thread.currentThread());
                });

        Thread.sleep(200);
        s.onNext(null);
        Thread.sleep(200);
    }

    private Object createNull() {
        System.out.println("null creation: " + Thread.currentThread());
        return null;
    }
}
