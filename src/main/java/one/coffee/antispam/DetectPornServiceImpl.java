package one.coffee.antispam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class DetectPornServiceImpl implements DetectPornService {
    public static final String PORNO_TASK = "porno";
    public static final Set<String> TASKS = Set.of(PORNO_TASK);
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    @Value("${robbi.url}")
    String robbiUrl;
    @Value("${robbi.login}")
    String login;
    @Value("${robbi.password}")
    String password;
    @Value("${robbi.detectUrl}")
    String detectUrl;
    private AuthData authData;

    @Override
    public boolean hasPornOnImage(String url) throws IOException, InterruptedException {
        List<String> tags = makeDetectRequest(url);
        if (tags != null) {
            return tags.contains("porno") || tags.contains("ero");
        }
        return false;
    }

    private List<String> makeDetectRequest(String url) throws IOException, InterruptedException {

        DetectionRequest detectionRequest = new DetectionRequest(Instant.now().toEpochMilli(),
                TASKS, url, null, null, RequestContentType.IMAGE_URL.name());
        String json = mapper.writeValueAsString(detectionRequest);

        HttpRequest request = HttpRequest.newBuilder(URI.create(detectUrl))
                .header("Authorization", "Bearer " + getToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.debug(response.body());
        DetectionResponse detectionResponse = mapper.readValue(response.body(), DetectionResponse.class);

        return detectionResponse.getResult().get(PORNO_TASK).getTags();
    }

    private String getToken() throws IOException, InterruptedException {
        if (authData == null || Instant.now().isAfter(authData.getExpiresIn())) {
            makeAuthRequest();
        }
        return authData.getAccessToken();
    }

    private void makeAuthRequest() throws IOException, InterruptedException {
        String encoding = Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
        HttpRequest request = HttpRequest.newBuilder(URI.create(robbiUrl))
                .header("Authorization", "Basic " + encoding)
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        authData = mapper.readValue(response.body(), AuthData.class);
    }
}
