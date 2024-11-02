package br.com.contafacil.bonnarotec.storage.exception;

import java.util.UUID;

public class FileNotFoundException extends StorageException {
    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(UUID fileId) {
        super("File not found with ID: " + fileId);
    }
}
