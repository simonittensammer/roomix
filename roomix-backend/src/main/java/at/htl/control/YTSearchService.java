package at.htl.control;

import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class YTSearchService {

    private HttpClient client = HttpClient.newHttpClient();

    private String  API_URL = "https://www.googleapis.com/youtube/v3/";
    private String API_TOKEN = "AIzaSyBQ4OBlQ9v34aLeJxrimhMt7PKrO4uxaDw";


    public JsonObject getVideos(String query) {
        String requestURL = API_URL + "search?q=" + query + "&key=" + API_TOKEN + "&part=snippet&type=video&maxResults=3";
        return executeRequest(requestURL);
    }

    public JsonObject getVideoDuration(String videoId) {
        String requestURL = API_URL + "videos?id=" + videoId + "&key=" + API_TOKEN + "&part=snippet,contentDetails";
        return executeRequest(requestURL);
    }

    private JsonObject executeRequest(String requestURL) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .build();

        String res = "{}";
        try {
            res = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JsonReader jsonReader = Json.createReader(new StringReader(res));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        return object;
    }
}
