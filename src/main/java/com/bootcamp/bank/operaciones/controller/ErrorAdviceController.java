package com.bootcamp.bank.operaciones.controller;

import com.bootcamp.bank.operaciones.model.ErrorBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorAdviceController {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorBean> runtimeExceptionHandler(RuntimeException ex){
        ErrorBean error = ErrorBean
                .builder()
                .codigoEstadoHttp(HttpStatus.NOT_FOUND.toString())
                .mensaje(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

