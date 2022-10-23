package com.andrey.exception_handlers;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Object> handleValidationViolation(MailSendException e) {

        return new ResponseEntity<>(Collections.singletonMap("could not send email: ", e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationViolation(MethodArgumentNotValidException e) {

        return new ResponseEntity<>(Collections.singletonMap("invalid argument: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }


}
