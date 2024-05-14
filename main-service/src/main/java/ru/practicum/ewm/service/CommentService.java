package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto createByAuthor(NewCommentDto commentDto, Long userId, Long eventId);

    CommentDto updateByAuthor(UpdateCommentRequest commentRequest, Long userId, Long commentId);

    List<CommentDto> getAllCommentsByAuthor(Long userId, int from, int size);

    void deleteByCommentIdByAuthor(Long userId, Long commentId);

    List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size);

    CommentDto updateByAdmin(UpdateCommentRequest commentRequest, Long commentId);

    void deleteByCommentIdByAdmin(Long commentId);
}