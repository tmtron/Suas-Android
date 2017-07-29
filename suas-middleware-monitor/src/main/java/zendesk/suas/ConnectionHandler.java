package zendesk.suas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectionHandler {
    void handle(InputStream inputStream, OutputStream outputStream) throws IOException;
}
