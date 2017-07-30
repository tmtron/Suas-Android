/**
 * Middleware for logging {@link zendesk.suas.State} changes.
 * <br>
 * <p>
 * Create an instance using the default constructor:
 * <br>
 * <pre>
 * Middleware logger new LoggerMiddleware();
 * </pre>
 *
 * or using the builder for more configuration options:
 * <br>
 * <pre>
 * Middleware logger = new LoggerMiddleware.Builder()
 *      .withTitleFormatter(...)
 *      .withSerialization(...)
 *      ...
 *      .build()
 * </pre>
 *
 * Make sure the the logger is the last middleware in the list:
 * <br>
 * <pre>
 * Middleware logger = new LoggerMiddleware()
 *
 * Store store = Suas.createStore(...)
 *       .widthMiddleware(middleware1, middleware2, ... middlewareN, logger)
 *       .builder().
 * </pre>
 *
 */
package zendesk.suas;