package zendesk.suas;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Set of {@link Executor}.
 */
class Executors {

    static Executor getDefaultExecutor() {
        return new DefaultCurrentThreadExecutor();
    }

    static Executor getAndroidExecutor() {
        return new AndroidExecutor();
    }

    public static class DefaultCurrentThreadExecutor implements Executor {

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }

    }

    public static class AndroidExecutor implements Executor {

        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run();
            } else {
                handler.post(runnable);
            }
        }

    }
}
