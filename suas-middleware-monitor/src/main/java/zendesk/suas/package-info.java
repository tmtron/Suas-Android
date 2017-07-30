/**
 * Middleware for logging {@link zendesk.suas.State} changes to <a href="">Suas Monitor</a>
 * <br>
 * <p>
 * Create an instance using the default constructor:
 * <br>
 * <pre>
 * Middleware monitor = new MonitorMiddleware(context);
 * </pre>
 *
 * or using the builder for more configuration options:
 * <br>
 * <pre>
 * Middleware logger = new MonitorMiddleware.Builder(context)
 *      .withEnableBonjour(true)
 *      .withEnableAdb(false)
 *      .build()
 * </pre>
 *
 * Make sure the the monitor is the last middleware in the list:
 * <br>
 * <pre>
 * Middleware monitor = new MonitorMiddleware(context)
 *
 * Store store = Suas.createStore(...)
 *       .widthMiddleware(middleware1, middleware2, ... middlewareN, monitor)
 *       .builder().
 * </pre>
 */
package zendesk.suas;