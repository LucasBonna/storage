package br.com.contafacil.bonnarotec.storage.controller;

import br.com.contafacil.bonnarotec.storage.domain.file.BatchDownloadSchema;
import br.com.contafacil.bonnarotec.storage.domain.file.FileDownloadDTO;
import br.com.contafacil.bonnarotec.storage.service.StorageService;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.file.FileEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/file")
@Tag(name = "Files", description = "Rotas de gerenciamento de arquivos")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @Operation(
            summary = "Upload de arquivo",
            description = "Realiza o upload de um arquivo e retorna seu identificador"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileEntity> upload(
            @Parameter(description = "Arquivo a ser enviado")
            @RequestParam("file") @NotNull MultipartFile file
            ) {
        FileEntity fileEntity = storageService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileEntity);
    }

    @Operation(
            summary = "Download de arquivos",
            description = "Baixa um arquivos atraves de seu identificador"
    )
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @Parameter(description = "ID do arquivo")
            @PathVariable @NotNull UUID id
    ) {
        FileDownloadDTO download = storageService.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.filename() + "\"")
                .body(new ByteArrayResource(download.data()));
    }

    @Operation(
            summary = "Download de arquivos em lote",
            description = "Realiza o download de arquivos em lote e retorna um zip através de um identificador"
    )
    @PostMapping("/download/batch")
    public ResponseEntity<Resource> batchDownload(
            @Parameter(description = "Lista de IDs para download")
            @Valid @RequestBody BatchDownloadSchema downloadDTO
    ) {
       ByteArrayResource zipResource = storageService.downloadBatch(downloadDTO.fileIds());

       return ResponseEntity.ok()
               .contentType(MediaType.APPLICATION_OCTET_STREAM)
               .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
               .body(zipResource);
    }
}
