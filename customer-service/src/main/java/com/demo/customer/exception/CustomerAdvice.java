package com.demo.customer.exception;

import com.demo.customer.model.exception.ErrorResponse;
import com.demo.customer.model.exception.ValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.demo.customer.constant.ErrorCodes.VALIDATION;

@ControllerAdvice
@Slf4j
public class CustomerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException exception){

        commonLoggingError(exception);
        List<ValidationInfo> validations = exception.getBindingResult().getAllErrors().stream()
                .map(objectError -> ValidationInfo.builder()
                        .type(objectError.getCode())
                        .message(objectError.getDefaultMessage())
                        .build()).toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .code(VALIDATION)
                .validations(validations)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        commonLoggingError(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }

    private void commonLoggingError(Exception exception){
        log.error("customer error " , exception);
    }
}