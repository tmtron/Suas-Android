package zendesk.suas;

import java.util.Arrays;
import java.util.Collection;

/**
 * Helper class for joining multiple {@link Subscription}s.
 */
public class CombinedSubscription implements Subscription {

    /**
     * Combine multiple {@link Subscription}s into one.
     *
     * @param subscriptions a list of subscriptions
     * @return a new subscription
     */
    public static Subscription from(Subscription... subscriptions) {
        return new CombinedSubscription(Arrays.asList(subscriptions));
    }

    /**
     * Combine multiple {@link Subscription}s into one.
     *
     * @param subscriptions a collection of subscriptions
     * @return a new subscription
     */
    public static Subscription from(Collection<Subscription> subscriptions) {
        return new CombinedSubscription(subscriptions);
    }

    private final Collection<Subscription> subscriptions;

    private CombinedSubscription(Collection<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void unsubscribe() {
        for(Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void subscribe() {
        for(Subscription subscription : subscriptions) {
            subscription.subscribe();
        }
    }

    @Override
    public void update() {
        for(Subscription subscription : subscriptions) {
            subscription.update();
        }
    }
}
