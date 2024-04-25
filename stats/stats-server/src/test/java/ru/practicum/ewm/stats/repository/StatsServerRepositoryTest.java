package ru.practicum.ewm.stats.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class StatsServerRepositoryTest {

    @Autowired
    StatsServerRepository repository;

    @Test
    @DirtiesContext
    public void testFindStatsByDates() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 23, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 23, 23, 59);
        EndpointHit hit = EndpointHit.builder()
                .id(1L)
                .app("app")
                .ip("1.1.2.2")
                .date(start)
                .uri("/hit")
                .build();
        repository.save(hit);

        List<String> uris = Arrays.asList("/hit", "/get");
        ViewDto expect = ViewDto.builder()
                .app("app")
                .hits(1L)
                .uri("/hit")
                .build();

        List<ViewDto> stats = repository.findStatsByDates(start, end, uris);

        assertEquals(stats.get(0).getApp(), expect.getApp());
        assertEquals(stats.get(0).getUri(), expect.getUri());
        assertEquals(stats.get(0).getHits(), expect.getHits());
    }

    @Test
    @DirtiesContext
    public void testFindStatsByDatesUniqueIp() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 23, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 23, 23, 59);

        EndpointHit hit1 = EndpointHit.builder()
                .id(1L)
                .app("app")
                .ip("1.1.2.2")
                .date(start)
                .uri("/hit")
                .build();
        EndpointHit hit2 = EndpointHit.builder()
                .id(2L)
                .app("app")
                .ip("1.1.2.3")
                .date(start)
                .uri("/hit")
                .build();
        EndpointHit hit3 = EndpointHit.builder()
                .id(3L)
                .app("app")
                .ip("1.1.2.2")
                .date(start)
                .uri("/get")
                .build();
        repository.saveAll(Arrays.asList(hit1, hit2, hit3));

        List<String> uris = Arrays.asList("/hit", "/get");
        ViewDto expect = ViewDto.builder()
                .app("app")
                .hits(2L)
                .uri("/hit")
                .build();

        List<ViewDto> stats = repository.findStatsByDatesUniqueIp(start, end, uris);

        assertEquals(2, stats.size());
        assertEquals(expect.getApp(), stats.get(0).getApp());
        assertEquals(expect.getUri(), stats.get(0).getUri());
        assertEquals(expect.getHits(), stats.get(0).getHits());

        assertEquals("app", stats.get(1).getApp());
        assertEquals("/get", stats.get(1).getUri());
        assertEquals(1L, stats.get(1).getHits());
    }

}