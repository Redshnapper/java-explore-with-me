package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUserById(Long userId);
}
