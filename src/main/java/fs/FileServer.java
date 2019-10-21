package fs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fm.FileManager;
import fm.FileManagerImpl;
import http.HttpClientRequest;
import http.HttpRequestHandler;
import http.HttpResponse;
import http.HttpServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileServer {
    private HttpServer httpServer;
    private FileManager fileManager;
    private String dataDirectory;

    public FileServer(int port, String dataDirectory, boolean debug) throws IOException {
        this.httpServer = new HttpServer(port);
        this.httpServer.setDebug(debug);
        this.httpServer.setGetRequestHandler(new GetRequestHandler());
        this.httpServer.setPostRequestHandler(new PostRequestHandler());
        this.fileManager = new FileManagerImpl();
        this.dataDirectory = dataDirectory;
    }

    public void run() throws IOException {
        this.httpServer.run();
    }

    private class GetRequestHandler implements  HttpRequestHandler {
        @Override
        public HttpResponse handleRequest(HttpClientRequest clientRequest) {
            HttpResponse response = null;
            String requestPath = clientRequest.getPath();
            if (requestPath.equals("/")) {
                try {
                    Gson gson = new Gson();
                    String filesJson = gson.toJson(listFilesInDataDirectory());
                    JsonObject responseData = new JsonObject();
                    responseData.addProperty("files", filesJson);
                    response = new HttpResponse(200, "OK", responseData.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        private List<String> listFilesInDataDirectory() throws IOException {
            return FileServer.this.fileManager
                    .listDir(Paths.get(dataDirectory).toFile().getAbsolutePath())
                    .stream()
                    .filter(f -> !f.isHidden())
                    .map(f -> f.getName())
                    .collect(Collectors.toList());
        }
    }

    private class PostRequestHandler implements  HttpRequestHandler {
        @Override
        public HttpResponse handleRequest(HttpClientRequest clientRequest) {
            HttpResponse response = null;
            String requestPath = clientRequest.getPath();
            String body = clientRequest.getBody();

            try {
                String[] requestParts = requestPath.split("/");
                if (requestParts.length == 1) {
                    String fileName = requestParts[0];
                    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                    writer.println(body);
                }

                response = new HttpResponse(200, "OK", "Working?");
            }
            catch (Exception e) {

            }
            return response;
        }
    }
}
