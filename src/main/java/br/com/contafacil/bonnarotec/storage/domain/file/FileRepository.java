package br.com.contafacil.bonnarotec.storage.domain.file;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {

}
