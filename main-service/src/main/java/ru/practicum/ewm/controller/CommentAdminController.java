package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.UpdateCommentRequest;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@Valid @RequestBody UpdateCommentRequest commentRequest,
                             @PathVariable Long commentId) {
        log.info("Обновление комментария администратором= {}", commentRequest);
        return commentService.updateByAdmin(commentRequest, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long commentId) {
        log.info("Удаление комментария администратором по id= {}", commentId);
        commentService.deleteByCommentIdByAdmin(commentId);
    }
}
