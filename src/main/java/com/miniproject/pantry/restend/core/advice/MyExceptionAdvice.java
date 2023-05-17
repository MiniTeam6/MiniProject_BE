package com.miniproject.pantry.restend.core.advice;

import com.miniproject.pantry.restend.core.exception.Exception400;
import com.miniproject.pantry.restend.core.exception.Exception401;
import com.miniproject.pantry.restend.core.exception.Exception403;
import com.miniproject.pantry.restend.core.exception.Exception404;
import com.miniproject.pantry.restend.core.exception.Exception500;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.miniproject.pantry.restend.dto.ResponseDTO;
import io.sentry.Sentry;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class MyExceptionAdvice {


    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> badRequest(Exception400 e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.body(), e.status());
    }


    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> unAuthorized(Exception401 e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.body(), e.status());
    }


    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> forbidden(Exception403 e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.body(), e.status());
    }


    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> notFound(Exception404 e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.body(), e.status());
    }


    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> serverError(Exception500 e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.body(), e.status());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e){
        Sentry.captureException(e);


        ResponseDTO<String> responseDTO = new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, "unknownServerError", e.getMessage(),100);

        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
