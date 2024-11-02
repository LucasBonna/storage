package br.com.contafacil.bonnarotec.storage.exception;

import br.com.contafacil.bonnarotec.storage.service.StorageService;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
