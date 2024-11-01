package br.com.contafacil.bonnarotec.storage.service;

import br.com.contafacil.bonnarotec.storage.domain.file.FileDownloadDTO;
import br.com.contafacil.bonnarotec.storage.domain.file.FileEntity;
import br.com.contafacil.bonnarotec.storage.domain.file.FileRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;

    @Value("${storage.bucket-name}")
    private String bucketName;

    public FileEntity upload(MultipartFile file) throws Exception {
        try {
            UUID id = UUID.randomUUID();
            String path = generatePath(id, file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(new PutObjectRequest(
                    bucketName,
                    path,
                    file.getInputStream(),
                    metadata
            ));

            FileEntity fileEntity = new FileEntity();
            fileEntity.setId(id);
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            fileEntity.setFilePath(path);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setCreatedAt(LocalDateTime.now());
            fileEntity.setUpdatedAt(LocalDateTime.now());
            return fileRepository.save(fileEntity);

        } catch (Exception e) {
            throw new Exception("Erro ao processar upload", e);
        }
    }

    public FileDownloadDTO download(UUID id) throws Exception {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new Exception("Arquivo nao encontrado"));

        if (fileEntity.getDeletedAt() != null) {
            throw new Exception("Arquivo nao encontrado ou excluido");
        }

        try {
            S3Object object = s3Client.getObject(bucketName, fileEntity.getFilePath());
            byte[] content = object.getObjectContent().readAllBytes();

            return new FileDownloadDTO(
                    content,
                    fileEntity.getContentType(),
                    fileEntity.getFileName()
            );
        } catch (Exception e) {
            throw new Exception("Erro ao gerar download", e);
        }
    }

    private String generatePath(UUID id, String filename) {
        return String.format(
                "%s/%s/%s",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                id,
                filename
        );
    }

}
