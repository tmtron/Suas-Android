package zendesk.suas;

import android.support.annotation.NonNull;

public interface Continuation {
    void next(@NonNull Action<?> action);
}
