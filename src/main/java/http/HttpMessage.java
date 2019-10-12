package http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpMessage {
    public String HTTP_VERSION = "HTTP/1.0";
    protected Map<String, String> headers;

    public HttpMessage() {
        this.headers = new HashMap<>();
    }

    protected abstract String getStartLine();

    public List<String> getHeaders() {
        List<String> formattedHeaders = new ArrayList<String>();
        this.headers.forEach((name, value) -> formattedHeaders.add(String.format("%s: %s", name, value)));
        return formattedHeaders;
    }

    public Map<String, String> getHeadersMap() {
        return this.headers;
    }
}
