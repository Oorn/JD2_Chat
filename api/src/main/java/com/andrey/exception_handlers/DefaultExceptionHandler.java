package com.andrey.exception_handlers;

import com.andrey.exceptions.BadRequestException;
import com.andrey.exceptions.BadTargetException;
import com.andrey.exceptions.InteractionWithSelfException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.exceptions.TooManyEntitiesException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {

        return new ResponseEntity<>(Collections.singletonMap("invalid argument: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Object> handleMailException(MailSendException e) {

        return new ResponseEntity<>(Collections.singletonMap("could not send email: ", e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationViolation(MethodArgumentNotValidException e) {

        ObjectError error = e.getBindingResult().getAllErrors().get(0);

        return new ResponseEntity<>(Collections.singletonMap("invalid argument: "
                , ((DefaultMessageSourceResolvable) error.getArguments()[0]).getDefaultMessage()
                        + " " + error.getDefaultMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException e) {

        return new ResponseEntity<>(Collections.singletonMap("bad request: ", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadTargetException.class)
    public ResponseEntity<Object> handleBadTarget(BadTargetException e) {

        return new ResponseEntity<>(Collections.singletonMap("bad target: ", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException e) {

        return new ResponseEntity<>(Collections.singletonMap("general error: ", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InteractionWithSelfException.class)
    public ResponseEntity<Object> handleSelfInteraction(InteractionWithSelfException e) {

        return new ResponseEntity<>(Collections.singletonMap("same user action: ", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<Object> handleNoPermission(NoPermissionException e) {

        return new ResponseEntity<>(Collections.singletonMap("not permitted: ", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<Object> handleNoSuchEntity(NoSuchEntityException e) {

        return new ResponseEntity<>(Collections.singletonMap("not found: ", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RemovedEntityException.class)
    public ResponseEntity<Object> handleRemovedEntity(RemovedEntityException e) {

        return new ResponseEntity<>(Collections.singletonMap("removed: ", e.getMessage()), HttpStatus.GONE);
    }

    @ExceptionHandler(TooManyEntitiesException.class)
    public ResponseEntity<Object> handleTooManyEntities(TooManyEntitiesException e) {

        return new ResponseEntity<>(Collections.singletonMap("limit reached: ", e.getMessage()), HttpStatus.FORBIDDEN);
    }


}
