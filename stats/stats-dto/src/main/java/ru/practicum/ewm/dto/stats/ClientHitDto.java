package ru.practicum.ewm.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientHitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
