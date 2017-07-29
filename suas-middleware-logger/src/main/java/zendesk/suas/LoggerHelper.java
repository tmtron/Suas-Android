package zendesk.suas;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class LoggerHelper {

    private static final String DEFAULT_LOG_TAG = "Suas Logger";
    private static final int MAXIMUM_ANDROID_LOG_TAG_LENGTH = 23;

    private LoggerHelper(){
        // Intentionally empty
    }

    static List<String> splitLogMessage(String message, int maxLength){
        final List<String> messages = new ArrayList<>();

        if(maxLength < 1){

            if(!(message != null && message.length() > 0)){
                messages.add("");
                return messages;
            }

            messages.add(message);
            return messages;
        }

        if(message == null || message.trim().length() == 0){
            messages.add("");
            return messages;
        }

        if(message.length() < maxLength){
            messages.add(message);
            return messages;
        }

        for(int i = 0, len = message.length(); i < len; i++){

            final int indexOfSeparator = message.indexOf("\n", i);
            final int newLine = (indexOfSeparator != -1) ? indexOfSeparator : len;

            do {
                final int end = Math.min(newLine, i + maxLength);
                messages.add(message.substring(i, end));
                i = end;
            } while(i < newLine);
        }

        return messages;
    }

    static char getLevelFromPriority (int priority) {
        switch (priority) {
            case Log.ASSERT:
                return 'A';
            case Log.DEBUG:
                return 'D';
            case Log.ERROR:
                return 'E';
            case Log.INFO:
                return 'I';
            case Log.VERBOSE:
                return 'V';
            case Log.WARN:
                return 'W';
            default:
                return 'I';
        }
    }

    /**
     * Log tags in android can be 23 characters maximum. Recent tooling updates is going to start outputting
     * warnings if > 23.
     *
     * @param tag The tag to use in the logging statement
     * @return The tag, subject to any restrictions enforced by Android. If the tag was missing, "Zendesk" will be
     *         used.
     */
    static String getAndroidTag(String tag) {
        if (tag == null || tag.trim().length() == 0) {
            return DEFAULT_LOG_TAG;
        }

        return tag.length() > MAXIMUM_ANDROID_LOG_TAG_LENGTH
                ? tag.substring(0, MAXIMUM_ANDROID_LOG_TAG_LENGTH)
                : tag;
    }
}