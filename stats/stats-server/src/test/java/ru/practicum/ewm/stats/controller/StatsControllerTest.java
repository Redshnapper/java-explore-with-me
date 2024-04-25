package ru.practicum.ewm.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

    @MockBean
    StatsService statService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createHit() throws Exception {
        CreateStatsDto hitDto = CreateStatsDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .date(LocalDateTime.now().withNano(0))
                .build();

        when(statService.create(any(CreateStatsDto.class)))
                .thenReturn(hitDto);

        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    public void testGet() throws Exception {
        List<ViewDto> expectedResponse = Arrays.asList(
                ViewDto.builder().app("app1").uri("uri1").hits(10L).build(),
                ViewDto.builder().app("app2").uri("uri2").hits(20L).build()
        );

        when(statService.get(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean())).thenReturn(expectedResponse);

        mockMvc.perform(get("/stats")
                        .param("start", "2024-04-23 00:00:00")
                        .param("end", "2024-04-23 23:59:59")
                        .param("uris", "uri1", "uri2")
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].app").value("app1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uri").value("uri1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].hits").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].app").value("app2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].uri").value("uri2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].hits").value(20));
    }
}