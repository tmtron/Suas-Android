package com.zendesk.suas;


public interface Store extends GetState, Dispatcher {

    void resetFullState(State state);


    void addListener(Listener<State> listener);

    void addListener(Filter<State> filter, Listener<State> listener);


    <E> void addListener(String key, Listener<E> listener);

    <E> void addListener(String key, Filter<E> filter, Listener<E> listener);


    <E> void addListener(Class<E> clazz, Listener<E> listener);

    <E> void addListener(Class<E> clazz, Filter<E> filter, Listener<E> listener);


    <E> void addListener(String key, Class<E> clazz, Listener<E> listener);

    <E> void addListener(String key, Class<E> clazz, Filter<E> filter, Listener<E> listener);


    void removeListener(Listener<?> listener);


    <E> void connect(Component<State, E> component);

    <E> void connect(Component<State, E> component, Filter<State> filter);


    <E, F> void connect(Component<E, F> component, String key);

    <E, F> void connect(Component<E, F> component, String key, Filter<E> filter);


    <E, F> void connect(Component<E, F> component, Class<E> clazz);

    <E, F> void connect(Component<E, F> component, Class<E> clazz, Filter<E> filter);


    <E, F> void connect(Component<E, F> component, String key, Class<E> clazz);

    <E, F> void connect(Component<E, F> component, String key, Class<E> clazz, Filter<E> filter);


    void disconnect(Component component);

}
