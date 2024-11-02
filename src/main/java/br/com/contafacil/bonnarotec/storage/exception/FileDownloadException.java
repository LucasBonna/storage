package br.com.contafacil.bonnarotec.storage.exception;

public class FileDownloadException extends StorageException {
    public FileDownloadException(String message) {
        super(message);
    }

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
