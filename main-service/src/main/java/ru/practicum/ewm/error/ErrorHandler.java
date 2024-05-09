package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.InvalidTimeException;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Ошибка валидации")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный запрос")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleInvalidTimeException(final InvalidTimeException e) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .reason("Дата и время указаны неверно")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("Объект не найден")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Ошибка сервера")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Нарушено ограничение целостности")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleRequestParameterException(final MissingServletRequestParameterException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Параметры запроса указаны неправильно")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Ошибка базы данных")
                .message(e.getMessage())
                .errorTime(LocalDateTime.now())
                .build();
    }

}