package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.dto.UpdateCommentRequest;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Valid @RequestBody NewCommentDto commentDto,
                             @PathVariable Long userId,
                             @PathVariable Long eventId) {
        log.info("Создание комментария= {}", commentDto);
        return commentService.createByAuthor(commentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@Valid @RequestBody UpdateCommentRequest commentRequest,
                             @PathVariable Long userId,
                             @PathVariable Long commentId) {
        log.info("Обновление комментария автором= {}", commentRequest);
        return commentService.updateByAuthor(commentRequest, userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос комментариев автором");
        return commentService.getAllCommentsByAuthor(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long userId,
                           @PathVariable Long commentId) {
        log.info("Удаление комментария автором по id= {}", commentId);
        commentService.deleteByCommentIdByAuthor(userId, commentId);
    }

}