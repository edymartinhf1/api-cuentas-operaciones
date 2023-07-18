package com.bootcamp.bank.operaciones.exception;

/**
 * Clase Exception de Negocio
 */
public class BusinessException extends RuntimeException{

    public BusinessException(String messageError) {
        super(messageError);
    }


}