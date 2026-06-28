package cv.zeemsv.api.domain.documento.dto;

import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import org.springframework.web.multipart.MultipartFile;

public class UploadDTO {
    private final MultipartFile uploadedFile;
    private final String filename;
    private final String basePathToSave;
    private final String fullPath;
    private final ZeeTDocRelacaoEntity zeeTDocRelacao;

    public UploadDTO(MultipartFile uploadedFile, String filename, String basePathToSave, ZeeTDocRelacaoEntity zeeTDocRelacao) {
        this.uploadedFile = uploadedFile;
        this.filename = filename + DocumentoBus.getFileExtension(uploadedFile.getOriginalFilename());
        this.basePathToSave = basePathToSave;
        this.fullPath = DocumentoBus.addSlashToBasePath(basePathToSave) + this.filename;
        this.zeeTDocRelacao = zeeTDocRelacao;
    }

    public MultipartFile getUploadedFile() {
        return uploadedFile;
    }

    public String getFilename() {
        return filename;
    }

    public String getBasePathToSave() {
        return basePathToSave;
    }

    public String getFullPath() {
        return fullPath;
    }

    public ZeeTDocRelacaoEntity getZeeTDocRelacao() {
        return zeeTDocRelacao;
    }
}
