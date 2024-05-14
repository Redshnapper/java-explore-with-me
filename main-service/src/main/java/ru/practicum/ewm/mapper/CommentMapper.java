package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.model.Comment;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommentMapper {
    CommentDto fromCommentToDto(Comment comment);

    List<CommentDto> fromCommentListToDto(List<Comment> comments);
}