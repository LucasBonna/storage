package br.com.contafacil.bonnarotec.storage.domain.file;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BatchDownloadSchema(
        @NotNull
        @Size(min = 2, max = 100, message = "Minimo de arquivos deve ser 2 e o maximo 100")
        List<UUID> fileIds
        ) {
}
