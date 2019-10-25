package fs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fm.FileManager;
import fm.FileManagerImpl;
import http.HttpClientRequest;
import http.HttpRequestHandler;
import http.HttpResponse;
import http.HttpServer;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
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
            } else {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(requestPath.substring(1)));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    response = new HttpResponse(200, "OK", builder.toString());
                } catch (FileNotFoundException e) {
                    response = new HttpResponse(404, "File Not Found", e.getMessage());
                } catch (IOException e) {
                    response = new HttpResponse(400, "IOException Reading File", e.getMessage());
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
            String fileName = getFileNameFromRequestPath(clientRequest.getPath());
            if (fileName != null) {
                try {
                    writeBodyToFile(clientRequest.getBody(), fileName);
                    response = new HttpResponse(200, "OK", "Successfully Saved File");
                }
                catch (Exception e) {
                    response = new HttpResponse(400, "Failed File Creation", "Could not create file");
                }
            }
            return response;
        }

        private String getFileNameFromRequestPath(String requestPath) {
            String[] requestParts = requestPath.split("/");
            if (requestParts.length == 2) {
                return requestParts[1];
            }
            return null;
        }

        private void writeBodyToFile(String body, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(body);
            writer.close();
        }
    }
}
