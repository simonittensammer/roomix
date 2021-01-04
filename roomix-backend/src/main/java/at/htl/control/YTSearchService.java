package at.htl.control;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class YTSearchService {

    private HttpClient client = HttpClient.newHttpClient();

    private String  API_URL = "https://www.googleapis.com/youtube/v3/";
    private String API_TOKEN = "AIzaSyBQ4OBlQ9v34aLeJxrimhMt7PKrO4uxaDw";


    public String getVideos(String query) {
        String requestURL = API_URL + "search?q=" + query + "&key=" + API_TOKEN + "&part=snippet&type=video&maxResults=1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getVideoDuration(String videoId) {
        String requestURL = API_URL + "videos?id=" + videoId + "&key=" + API_TOKEN + "&part=snippet,contentDetails";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "";
    }
}
