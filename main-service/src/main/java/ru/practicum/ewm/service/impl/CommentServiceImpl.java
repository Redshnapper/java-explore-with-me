package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.dto.UpdateCommentRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;


    @Override
    public CommentDto updateByAuthor(UpdateCommentRequest commentRequest, Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Комментарий для данного пользователя не найден");
        }
        comment.setText(commentRequest.getText());
        return commentMapper.fromCommentToDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateByAdmin(UpdateCommentRequest commentRequest, Long commentId) {
        Comment comment = getComment(commentId);
        comment.setText(commentRequest.getText());
        return commentMapper.fromCommentToDto(commentRepository.save(comment));
    }


    @Override
    public void deleteByCommentIdByAdmin(Long commentId) {
        getComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllCommentsByAuthor(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable);
        return commentMapper.fromCommentListToDto(comments);
    }

    @Override
    public CommentDto createByAuthor(NewCommentDto commentDto, Long userId, Long eventId) {
        User author = getUser(userId);
        Event event = getEvent(eventId);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(commentDto.getText());
        comment.setCreatedDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.fromCommentToDto(savedComment);
    }

    @Override
    public void deleteByCommentIdByAuthor(Long userId, Long commentId) {
        getUser(userId);
        Comment comment = getComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + ", не оставлял комментарий с id=" + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        return commentMapper.fromCommentListToDto(comments);
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Собитие с id=" + eventId + " не найдено"));
    }


}
