package com.lovedj.studyproject.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventBus {
    // subscriptionsByEventType 这个集合存放的是？
    // key 是 Event 参数的类
    // value 存放的是 Subscription 的集合列表
    // Subscription 包含两个属性，一个是 subscriber 订阅者（反射执行对象），一个是 SubscriberMethod 注解方法的所有属性参数值
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    // typesBySubscriber 这个集合存放的是？
    // key 是所有的订阅者
    // value 是所有订阅者里面方法的参数的class
    private final Map<Object, List<Class<?>>> typesBySubscriber;
    private EventBus(){
        typesBySubscriber = new HashMap<Object, List<Class<?>>>();
        subscriptionsByEventType = new HashMap<>();
    }

    static volatile EventBus defaultInstance;

    /** Convenience singleton for apps using a process-wide EventBus instance. */
    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }

    public void register(Object object) {
        // 1. 解析所有方法封装成 SubscriberMethod 的集合
        List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        Class<?> objClass = object.getClass();
        Method[] methods = objClass.getDeclaredMethods();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if(subscribe != null){
                // 所有的Subscribe属性 解析出来
                Class<?>[] parameterTypes = method.getParameterTypes();
                SubscriberMethod subscriberMethod = new SubscriberMethod(
                        method,parameterTypes[0],subscribe.threadMode(),subscribe.priority(),subscribe.sticky());
                subscriberMethods.add(subscriberMethod);
            }
        }
        // 2. 按照规则存放到 subscriptionsByEventType 里面去
        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            subscriber(object,subscriberMethod);
        }
    }

    // 2. 按照规则存放到 subscriptionsByEventType 里面去
    private void subscriber(Object object, SubscriberMethod subscriberMethod) {
        Class<?> eventType = subscriberMethod.eventType;
        // 随处能找到，我这个代码
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if(subscriptions == null){
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(eventType,subscriptions);
        }

        // 判断优先级 （不写）
        Subscription subscription = new Subscription(object,subscriberMethod);
        subscriptions.add(subscription);

        // typesBySubscriber 要弄好是为了方便移除
        List<Class<?>> eventTypes = typesBySubscriber.get(object);
        if(eventTypes == null){
            eventTypes = new ArrayList<>();
            typesBySubscriber.put(object,eventTypes);
        }
        if(!eventTypes.contains(eventType)){
            eventTypes.add(eventType);
        }
    }

    public void unregister(Object object) {
        List<Class<?>> eventTypes = typesBySubscriber.get(object);
        if(eventTypes != null){
            for (Class<?> eventType : eventTypes) {
                removeObject(eventType,object);
            }
        }
    }

    private void removeObject(Class<?> eventType, Object object) {
        // 获取事件类的所有订阅信息列表，将订阅信息从订阅信息集合中移除，同时将订阅信息中的active属性置为FALSE
        List<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == object) {
                    // 将订阅信息从集合中移除
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }

    public void post(Object event) {
        // 遍历 subscriptionsByEventType，找到符合的方法调用方法的 method.invoke() 执行。要注意线程切换
        Class<?> eventType = event.getClass();
        // 找到符合的方法调用方法的 method.invoke() 执行
        CopyOnWriteArrayList<Subscription> subscriptions =  subscriptionsByEventType.get(eventType);
        if(subscriptions != null){
            for (Subscription subscription : subscriptions) {
                executeMethod(subscription,event);
            }
        }
    }

    private void executeMethod(final Subscription subscription, final Object event) {
        ThreadMode threadMode =  subscription.subscriberMethod.threadMode;
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        switch (threadMode){
            case POSTING:
                invokeMethod(subscription,event);
                break;
            case MAIN:
                if(isMainThread){
                    invokeMethod(subscription,event);
                }else {
                    // 行不行，不行？行？
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeMethod(subscription,event);
                        }
                    });
                }
                break;
            case ASYNC:
                AsyncPoster.enqueue(subscription,event);
                break;
            case BACKGROUND:
                if(!isMainThread){
                    invokeMethod(subscription,event);
                }else {
                    AsyncPoster.enqueue(subscription,event);
                }
                break;
        }
    }

    private void invokeMethod(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber,event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
