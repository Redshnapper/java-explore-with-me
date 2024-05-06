package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.toUser(userDto);
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users = ids != null && !ids.isEmpty() ? repository.findAllByIdIn(ids, pageable) :
                repository.findAll(pageable).getContent();
        return mapper.toListDto(users);
    }

    @Override
    public void deleteUserById(Long userId) {
        getUser(userId);
        repository.deleteById(userId);
    }

    private void getUser(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
