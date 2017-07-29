package zendesk.suas;


import android.support.annotation.NonNull;

public interface Store extends GetState, Dispatcher {

    void resetFullState(@NonNull State state);


    void addListener(@NonNull Listener<State>  listener);

    void addListener(@NonNull Filter<State> filter, @NonNull Listener<State> listener);


    <E> void addListener(@NonNull String key, @NonNull Listener<E> listener);

    <E> void addListener(@NonNull String key, @NonNull Filter<E> filter, @NonNull Listener<E> listener);


    <E> void addListener(@NonNull Class<E> clazz, @NonNull Listener<E> listener);

    <E> void addListener(@NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener);


    <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Listener<E> listener);

    <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener);


    void removeListener(@NonNull Listener<?> listener);


    <E> void connect(@NonNull Component<State, E> component);

    <E> void connect(@NonNull Component<State, E> component, @NonNull Filter<State> filter);


    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key);

    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Filter<E> filter);


    <E, F> void connect(@NonNull Component<E, F> component, @NonNull Class<E> clazz);

    <E, F> void connect(@NonNull Component<E, F> component, @NonNull Class<E> clazz, @NonNull Filter<E> filter);


    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz);

    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter);


    void disconnect(@NonNull Component component);

}
