package br.com.contafacil.bonnarotec.storage.controller;

import br.com.contafacil.bonnarotec.storage.domain.file.FileDownloadDTO;
import br.com.contafacil.bonnarotec.storage.domain.file.FileEntity;
import br.com.contafacil.bonnarotec.storage.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            @RequestParam("file")MultipartFile file
            ) throws Exception {
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
            @PathVariable UUID id
    ) throws Exception {
        FileDownloadDTO download = storageService.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.filename() + "\"")
                .body(new ByteArrayResource(download.data()));
    }
}
