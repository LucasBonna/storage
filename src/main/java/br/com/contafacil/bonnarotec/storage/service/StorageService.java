package br.com.contafacil.bonnarotec.storage.service;

import br.com.contafacil.bonnarotec.storage.domain.file.FileDownloadDTO;
import br.com.contafacil.bonnarotec.storage.domain.file.FileRepository;
import br.com.contafacil.bonnarotec.storage.exception.FileDownloadException;
import br.com.contafacil.bonnarotec.storage.exception.FileNotFoundException;
import br.com.contafacil.bonnarotec.storage.exception.FileUploadException;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.file.FileEntity;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;

    @Value("${storage.bucket-name}")
    private String bucketName;

    public FileEntity upload(MultipartFile file) throws FileUploadException {
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
            fileEntity.setExtension(extractExtension(file.getOriginalFilename()));
            fileEntity.setFilePath(path);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setCreatedAt(LocalDateTime.now());
            fileEntity.setUpdatedAt(LocalDateTime.now());
            return fileRepository.save(fileEntity);

        } catch (AmazonServiceException e) {
            throw new FileUploadException("Erro ao processar upload.");
        } catch (IOException e) {
            throw new FileUploadException("Erro ao ler arquivo.");
        }
    }

    public FileDownloadDTO download(UUID id) throws FileNotFoundException, FileDownloadException {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Arquivo nao encontrado"));

        if (fileEntity.getDeletedAt() != null) {
            throw new FileNotFoundException("Arquivo nao encontrado ou excluido");
        }

        try {
            S3Object object = s3Client.getObject(bucketName, fileEntity.getFilePath());
            byte[] content = object.getObjectContent().readAllBytes();

            return new FileDownloadDTO(
                    content,
                    fileEntity.getContentType(),
                    fileEntity.getFileName()
            );
        } catch (AmazonServiceException e) {
            System.out.println("Erro ao fazer download: " + e.getMessage());

            throw new FileDownloadException("Erro ao fazer download");
        } catch (IOException e) {
            System.out.println("Erro ao fazer download: " + e.getMessage());

            throw new FileDownloadException("Erro ao fazer download");
        }
    }

    public ByteArrayResource downloadBatch(List<UUID> ids) throws FileDownloadException, FileNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            for (UUID id : ids) {
                FileDownloadDTO download = this.download(id);

                if (download.data() != null) {
                    ZipEntry zipEntry = new ZipEntry(download.filename());
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(download.data());
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();
        } catch (IOException e) {
            System.out.println("Erro ao fazer download: " + e.getMessage());
            throw new FileDownloadException("Erro ao fazer download");
        }

        return new ByteArrayResource(baos.toByteArray());
    }

    private String generatePath(UUID id, String filename) {
        return String.format(
                "%s/%s/%s",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                id,
                filename
        );
    }

    private String extractExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        return (lastIndexOf == -1) ? "" : filename.substring(lastIndexOf);
    }
}
