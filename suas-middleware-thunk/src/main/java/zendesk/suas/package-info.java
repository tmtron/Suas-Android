/**
 * Async Middleware - {@link zendesk.suas.Action}s carrying an {@link zendesk.suas.AsyncAction} get intercepted
 * by this {@link zendesk.suas.Middleware} and not dispatched to the reducer.
 * Instead the {@link zendesk.suas.AsyncMiddleware} executes {@link zendesk.suas.AsyncAction#execute(Dispatcher, GetState)}
 * <br>
 * <p>
 * How to use this middleware:
 * <ol>
 *     <li>Create an {@link zendesk.suas.AsyncAction}</li>
 *     <li>In {@link zendesk.suas.AsyncAction#execute(Dispatcher, GetState)} perform any operation. {@link zendesk.suas.AsyncAction#execute(Dispatcher, GetState)} <i>isn't</i> executed on a background thread.</li>
 *     <li>When the result is ready use the {@link zendesk.suas.Dispatcher} to dispatch a new {@link zendesk.suas.Action}</li>
 *     <li>Create {@link zendesk.suas.Action} by calling {@link zendesk.suas.AsyncMiddleware#create(AsyncAction)}</li>
 * </ol>
 * <br>
 * <pre>
 * AsyncAction asyncAction = new AsyncAction(){
 *   public void execute(final Dispatcher dispatcher, GetState getState) {
 *     Network network = new Network();
 *     network.load(new Callback() {
 *       public void onResult(Object data) {
 *         dispatcher.dispatchAction(new Action<>("network_success", data));
 *       }
 *     });
 *   }
 * };
 *
 * Action action = AsyncMiddleware.create(asyncAction);
 * store.dispatchAction(action);
 * </pre>
 * For convenience there's {@link zendesk.suas.AsyncMiddleware#forBlockingAction(AsyncAction)}. An {@link zendesk.suas.Action}
 * created with that function will be be executed on a background thread. For doing so it uses
 * Android's {@link android.os.AsyncTask}.
 */
package zendesk.suas;