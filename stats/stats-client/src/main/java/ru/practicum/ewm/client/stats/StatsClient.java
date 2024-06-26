package ru.practicum.ewm.client.stats;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    private static final String HIT_API_PREFIX = "/hit";
    private static final String STATS_API_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${STATS_SERVER_URL}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(CreateStatsDto endpointHitDto) {
        return post(HIT_API_PREFIX, endpointHitDto);
    }

    public ResponseEntity<Object> getViewStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);

        if (parameters.containsKey("uris")) {
            return get(STATS_API_PREFIX + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            return get(STATS_API_PREFIX + "?start={start}&end={end}&unique={unique}", parameters);
        }
    }

    public List<ViewDto> getViews(LocalDateTime start, LocalDateTime end, List<String> uriList) {
        Gson gson = new Gson();
        ResponseEntity<Object> viewsObject = getViewStats(start, end, uriList, true);
        String json = gson.toJson(viewsObject.getBody());
        ViewDto[] viewsArray = gson.fromJson(json, ViewDto[].class);
        return Arrays.asList(viewsArray);
    }

}

