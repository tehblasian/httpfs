package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private ServerSocket server;
    private boolean running;
    private boolean debug;

    private HttpRequestHandler getRequestHandler;
    private HttpRequestHandler postRequestHandler;

    public HttpServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        System.out.println("Server listening on port " + this.server.getLocalPort());
    }

    public void run() throws IOException {
        this.running = true;
        while (running) {
            Socket client = server.accept();
            HttpClientRequest clientRequest = readRequestFromClient(client);
            if (this.debug) {
                System.out.println("Received request:\n");
                System.out.println(clientRequest);
            }

            HttpResponse serverResponse = handleHttpClientRequest(clientRequest);
        }
    }

    public void stop() {
        this.running = false;
    }

    public void setGetRequestHandler(HttpRequestHandler requestHandler) {
        this.getRequestHandler = requestHandler;
    }

    public void setPostRequestHandler(HttpRequestHandler requestHandler) {
        this.postRequestHandler = requestHandler;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private HttpClientRequest readRequestFromClient(Socket client) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        while (!line.isEmpty()) {
            stringBuilder.append(line + "\n");
            line = reader.readLine();
        }
        return HttpClientRequest.fromRaw(stringBuilder.toString());
    }

    private HttpResponse handleHttpClientRequest(HttpClientRequest clientRequest) {
        HttpResponse serverResponse = null;
        switch (clientRequest.getMethod()) {
            case "GET": {
                serverResponse = getRequestHandler.handleRequest(clientRequest);
                break;
            }
            case "POST": {
                serverResponse = postRequestHandler.handleRequest(clientRequest);
                break;
            }
        }
        return serverResponse;
    }

}
