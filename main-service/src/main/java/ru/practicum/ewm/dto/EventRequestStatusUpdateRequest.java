package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.enums.StateAction;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @NotNull
    List<Long> requestIds;
    @NotNull
    StateAction status;
}
