package com.zendesk.suas;


public interface Store extends GetState, Dispatcher {

    void resetFullState(State state);

    void reset(String key, Object state);

    void reset(Object state);


    void addListener(Listener<State> listener);

    void addListener(Notifier<State> notifier, Listener<State> listener);


    <E> void addListener(String key, Listener<E> listener);

    <E> void addListener(String key, Notifier<E> notifier, Listener<E> listener);


    <E> void addListener(Class<E> clazz, Listener<E> listener);

    <E> void addListener(Class<E> clazz, Notifier<E> notifier, Listener<E> listener);


    <E> void addListener(String key, Class<E> clazz, Listener<E> listener);

    <E> void addListener(String key, Class<E> clazz, Notifier<E> notifier, Listener<E> listener);


    void removeListener(Listener<?> listener);


    <E> void connect(Component<State, E> component);

    <E> void connect(Component<State, E> component, Notifier<E> notifier);


    <E, F> void connect(Component<E, F> component, String key);

    <E, F> void connect(Component<E, F> component, String key, Notifier<E> notifier);


    <E, F> void connect(Component<E, F> component, Class<E> clazz);

    <E, F> void connect(Component<E, F> component, Class<E> clazz, Notifier<E> notifier);


    <E, F> void connect(Component<E, F> component, String key, Class<E> clazz);

    <E, F> void connect(Component<E, F> component, String key, Class<E> clazz, Notifier<E> notifier);


    void disconnect(Component component);


    void updateAll();

}
