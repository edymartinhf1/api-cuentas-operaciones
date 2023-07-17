package com.bootcamp.bank.operaciones.exception;

/**
 * Clase Exception de Negocio
 */
public class BusinessException extends RuntimeException{
    private String messageError;

    public BusinessException(String messageError) {
        super(messageError);
        this.messageError = messageError;
    }


}