package ru.practicum.ewm.dto.stats;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewDto {
    private String app;
    private String uri;
    private Long hits;
}



