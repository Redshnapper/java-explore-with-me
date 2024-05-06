package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.practicum.ewm.client.stats.StatsClient;

@Configuration
public class StatsClientConfig {

    private final RestTemplateBuilder builder;
    private final String statServerUrl;

    public StatsClientConfig(RestTemplateBuilder builder, @Value("${STATS_SERVER_URL:http://localhost:9090}") String statServerUrl) {
        this.builder = builder;
        this.statServerUrl = statServerUrl;
    }

    @Bean
    @Primary
    public StatsClient statClient() {
        return statClient(statServerUrl);
    }

    private StatsClient statClient(String statServerUrl) {
        return new StatsClient(statServerUrl, builder);
    }
}

