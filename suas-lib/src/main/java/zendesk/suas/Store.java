package zendesk.suas;


import android.support.annotation.NonNull;

/**
 * The {@code Store} contains the application's state, reducer logic, middleware and listeners.
 *
 * <ul>
 *     <li>{@link State} represents the application state. The state is partition into state keys.</li>
 *     <li>{@link Reducer} represents the logic to update the {@link State}. A reducer provides the functionality to update the state for a particular {@link Action}.</li>
 *     <li>{@link Middleware} is a function that intercepts an {@link Action} and can enrich or alter it before reaching the {@link Reducer}.</li>
 *     <li>{@link Listener} a function that gets called on state changes</li>
 *     <li>{@link Component} similar to a {@link Listener} with an additional function called {@link Selector}.</li>
 * </ul>
 */
public interface Store extends StoreApi {

    /**
     * Resets the full internal state with a new state and notifies all registered {@link Listener}
     * and {@link Component}.
     *
     * @param state the new state
     */
    void reset(@NonNull State state);


    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any state changes.
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param listener callback to be notified on state changes
     */
    void addListener(@NonNull Listener<State> listener);

    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any state changes.
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param filter function used to decide whether to notify or not
     * @param listener callback to be notified on state changes
     */
    void addListener(@NonNull Filter<State> filter, @NonNull Listener<State> listener);


    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in key.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param key the state key to listen for changes
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code key}
     */
    <E> void addListener(@NonNull String key, @NonNull Listener<E> listener);

    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in key.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param key the state key to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code key}
     */
    <E> void addListener(@NonNull String key, @NonNull Filter<E> filter, @NonNull Listener<E> listener);


    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in {@link Class}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code class}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param clazz the state key and type listen for changes
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code key}
     */
    <E> void addListener(@NonNull Class<E> clazz, @NonNull Listener<E> listener);

    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in {@link Class}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code class}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param clazz the state key and type to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code clazz}
     */
    <E> void addListener(@NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener);


    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in {@code key} and of type {@code clazz}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param key the state key to listen for changes
     * @param clazz the state type to listen for changes
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code key}
     */
    <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Listener<E> listener);

    /**
     * Adds a new {@link Listener} to the store.
     *
     * <p>
     *     The provided {@link Listener} will be notified on any changes to the part state that's tied
     *     to the passed in {@code key} and of type {@code clazz}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the listener will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param key the state key to listen for changes
     * @param clazz the state type to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param listener callback to be notified on state changes
     * @param <E> type of the state that's registered on the provided {@code key}
     */
    <E> void addListener(@NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter, @NonNull Listener<E> listener);

    /**
     * Remove a listener from the store.
     *
     * @param listener the listener to remove
     */
    void removeListener(@NonNull Listener<?> listener);


    /**
     * Connects a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any state changes.
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param <E> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E> void connect(@NonNull Component<State, E> component);

    /**
     * Connects a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any state changes.
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param filter function used to decide whether to notify or not
     * @param <E> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E> void connect(@NonNull Component<State, E> component, @NonNull Filter<State> filter);


    /**
     * Connects a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in key.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param key the state key to listen for changes
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key);

    /**
     * Connects a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in key.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param key the state key to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Filter<E> filter);

    /**
     * Adds a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in {@link Class}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code class}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param clazz the state type to listen for changes
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull Class<E> clazz);

    /**
     * Adds a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in {@link Class}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code class}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param clazz the state type to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull Class<E> clazz, @NonNull Filter<E> filter);


    /**
     * Adds a new {@link Component} to the store.
     *
     * <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in {@code key} and of type {@code clazz}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     If nothing else provided {@link Filters#DEFAULT} will be used to decide whether to
     *     notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param key the state key to listen for changes
     * @param clazz the state type to listen for changes
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz);

    /**
     * Adds a new {@link Component} to the store.
     *
     *  <p>
     *     The provided {@link Component} will be notified on any changes to the part state that's tied
     *     to the passed in {@code key} and of type {@code clazz}.
     *     If the provided type {@code E} doesn't match the object behind the passed in {@code key}
     *     the component will never be notified and a warning will be logged.
     *     <br>
     *     The provided {@link Filter} is used to decide whether to notify or not.
     * </p>
     *
     * @param component the component to connect
     * @param key the state key to listen for changes
     * @param clazz the state type to listen for changes
     * @param filter function used to decide whether to notify or not
     * @param <E> type of the state that's registered on the provided {@code key}
     * @param <F> type of the object that the component will receive in {@link Component#update(Object)}
     */
    <E, F> void connect(@NonNull Component<E, F> component, @NonNull String key, @NonNull Class<E> clazz, @NonNull Filter<E> filter);


    /**
     * Disconnect an action listener that was added for component
     *
     * @param component the component to disconnect from the store
     */
    void disconnect(@NonNull Component component);

}
