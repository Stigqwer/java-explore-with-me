package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.EndpointHit;

import java.util.Map;

@Service
public class EventClient extends BaseClient {
    private static final String PATH = "/stats?start={start}&end={end}&uris={uris}";

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void createHit(EndpointHit endpointHit) {
        post("/hit", endpointHit);
    }

    public ResponseEntity<Object> getStats(String start, String end, String[] uris) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris
        );
        return get(PATH, null, parameters);
    }
}
