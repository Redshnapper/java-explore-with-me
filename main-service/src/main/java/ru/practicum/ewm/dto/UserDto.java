package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @NotBlank(message = "Email не указан")
    @Email(message = "Email указан неправильно")
    @Size(min = 6, max = 254)
    String email;
    Long id;
    @NotBlank(message = "Имя не указано")
    @Size(min = 2, max = 250)
    String name;
}
