package zendesk.suas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface ConnectionHandler {
    void handle(InputStream inputStream, OutputStream outputStream) throws IOException;
}
