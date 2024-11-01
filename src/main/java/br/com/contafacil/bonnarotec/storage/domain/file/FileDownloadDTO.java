package br.com.contafacil.bonnarotec.storage.domain.file;

public record FileDownloadDTO(byte[] data, String contentType, String filename) {
}
