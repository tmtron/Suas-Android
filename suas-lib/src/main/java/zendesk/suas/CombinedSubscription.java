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
    public void removeListener() {
        for(Subscription subscription : subscriptions) {
            subscription.removeListener();
        }
    }

    @Override
    public void addListener() {
        for(Subscription subscription : subscriptions) {
            subscription.addListener();
        }
    }

    @Override
    public void informWithCurrentState() {
        for(Subscription subscription : subscriptions) {
            subscription.informWithCurrentState();
        }
    }
}
