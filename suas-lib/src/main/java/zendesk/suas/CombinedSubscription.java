package zendesk.suas;

import java.util.Arrays;
import java.util.Collection;

public class CombinedSubscription implements Subscription {

    public static Subscription from(Subscription... subscriptions) {
        return new CombinedSubscription(Arrays.asList(subscriptions));
    }

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
