// SimpleWebService.java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleWebService {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/greet", new GreetHandler());
        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:8000/greet");
        server.start();
    }

    static class GreetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String name = "Guest";
            
            if (query != null && query.contains("name=")) {
                name = query.split("name=")[1];
            }

            String response = "{\"message\": \"Hello, " + name + "!\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}


// javac SimpleWebService.java


// WebServiceClient.java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceClient {

    public static void main(String[] args) {
        try {
            String name = "User";
            URL url = new URL("http://localhost:8000/greet?name=" + name);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + conn.getResponseCode());
            }

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String output;
            StringBuilder responseBuilder = new StringBuilder();

            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            conn.disconnect();

            String jsonResponse = responseBuilder.toString();
            // Simple manual JSON parsing (avoid dependency)
            String message = jsonResponse.split(":")[1].replace("}", "").replace("\"", "").trim();
            System.out.println("Server Response: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


// javac WebServiceClient.java
// java WebServiceClient
