package cv.zeemsv.api.domain.documento.business;

import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpDocEntity;
import cv.zeemsv.api.infrastructure.minio.DocumentMinioDTO;
import cv.zeemsv.api.infrastructure.minio.MinioStoreEngine;
import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpDocRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentoBus {
    public static final String ESTADO_ATIVO = "ATIVO";
    public static final String ESTADO_ELIMINADO = "ELIMINADO";

    private static final String DEFAULT_USER = "system";

    private final ZeeTDocRelacaoRepository docRelacaoRepository;
    private final ZeeTTpDocRepository tpDocRepository;
    private final MinioStoreEngine minioStoreEngine;

    @Transactional
    public ZeeTDocRelacaoEntity saveOrUpdate(ZeeTDocRelacaoEntity docRelacao) {
        return saveOrUpdate(docRelacao, null);
    }

    @Transactional
    public ZeeTDocRelacaoEntity saveOrUpdate(ZeeTDocRelacaoEntity docRelacao, String userCreate) {
        docRelacao.setPath(normalizeText(docRelacao.getPath()));

        if (docRelacao.getId() != null) {
            ZeeTDocRelacaoEntity current = docRelacaoRepository.findById(docRelacao.getId())
                .orElseThrow(() -> new EntityNotFoundException("Documento nao encontrado: " + docRelacao.getId()));

            current.setPath(docRelacao.getPath());
            current.setIdRelacao(docRelacao.getIdRelacao());
            current.setIdTpDoc(docRelacao.getIdTpDoc());
            current.setTipoRelacao(docRelacao.getTipoRelacao());
            current.setMimetype(docRelacao.getMimetype());
            current.setDocSize(docRelacao.getDocSize());
            current.setIdDoc(docRelacao.getIdDoc());
            current.setDescricao(docRelacao.getDescricao());
            return docRelacaoRepository.save(current);
        }

        docRelacao.setEstado(ESTADO_ATIVO);
        docRelacao.setDateCreate(LocalDateTime.now());
        docRelacao.setUserCreate(hasText(userCreate) ? userCreate.trim() : DEFAULT_USER);
        return docRelacaoRepository.save(docRelacao);
    }

    @Transactional
    public List<ZeeTDocRelacaoEntity> saveOrUpdateDocs(List<ZeeTDocRelacaoEntity> docsRelacao, String userCreate) {
        return docsRelacao.stream()
            .map(doc -> saveOrUpdate(doc, userCreate))
            .toList();
    }

    @Transactional
    public void saveOrUpdate(UploadDTO uploadedFile) {
        saveOrUpdate(uploadedFile, null);
    }

    @Transactional
    public void saveOrUpdate(UploadDTO uploadedFile, String userCreate) {
        saveOrUpdate(List.of(uploadedFile), userCreate);
    }

    @Transactional
    public void saveOrUpdate(List<UploadDTO> uploadedFiles, String userCreate) {
        List<DocumentMinioDTO> files = uploadedFiles.stream()
            .map(this::toMinioDocument)
            .toList();

        minioStoreEngine.saveFiles(files);

        for (UploadDTO item : uploadedFiles) {
            ZeeTDocRelacaoEntity docRelacao = item.getZeeTDocRelacao();
            reuseExistingUploadDocument(docRelacao);
            docRelacao.setPath(item.getFullPath());
            docRelacao.setMimetype(item.getUploadedFile().getContentType());
            docRelacao.setDocSize(BigDecimal.valueOf(item.getUploadedFile().getSize()));
            saveOrUpdate(docRelacao, userCreate);
        }
    }

    @Transactional
    public ZeeTDocRelacaoEntity saveGeneratedDocument(
        byte[] content,
        String filename,
        String basePathToSave,
        String contentType,
        ZeeTDocRelacaoEntity docRelacao,
        String userCreate
    ) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Conteudo do documento e obrigatorio.");
        }
        String fullPath = DocumentoBus.addSlashToBasePath(basePathToSave) + filename;
        minioStoreEngine.saveFiles(List.of(new DocumentMinioDTO(
            normalizeText(fullPath),
            content,
            content.length,
            contentType
        )));

        reuseExistingUploadDocument(docRelacao);
        docRelacao.setPath(fullPath);
        docRelacao.setMimetype(contentType);
        docRelacao.setDocSize(BigDecimal.valueOf(content.length));
        return saveOrUpdate(docRelacao, userCreate);
    }

    @Transactional(readOnly = true)
    public List<ZeeTTpDocEntity> loadTpDocByTipoObject(String tpObject) {
        return tpDocRepository.findByTipoRelacaoAndEstado(tpObject, ESTADO_ATIVO);
    }

    @Transactional(readOnly = true)
    public List<ZeeTDocRelacaoEntity> loadTpDocByTipoObjectDetalhes(String tpObject, BigDecimal idObjecto) {
        return docRelacaoRepository.findByTipoRelacaoAndIdRelacaoAndEstado(tpObject, idObjecto, ESTADO_ATIVO);
    }

    @Transactional(readOnly = true)
    public ZeeTDocRelacaoEntity loadDocRelacaosByObject(Integer idTpDoc, BigDecimal idObject, String tpObject) {
        return docRelacaoRepository.findByIdTpDocAndIdRelacaoAndTipoRelacao(idTpDoc, idObject, tpObject)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public ZeeTTpDocEntity getTpDocByID(Integer id) {
        return tpDocRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de documento nao encontrado: " + id));
    }

    @Transactional
    public void delete(Integer id) {
        ZeeTDocRelacaoEntity entity = docRelacaoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Documento nao encontrado: " + id));
        entity.setEstado(ESTADO_ELIMINADO);
        docRelacaoRepository.save(entity);
    }

    @Transactional
    public void deleteDocByObjectID(String objectType, BigDecimal idObject) {
        loadTpDocByTipoObjectDetalhes(objectType, idObject)
            .forEach(entity -> {
                entity.setEstado(ESTADO_ELIMINADO);
                docRelacaoRepository.save(entity);
            });
    }

    public static String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    public static String getFileNameWithExtensionByPath(String filePath) {
        if (filePath == null) {
            return null;
        }
        int slashIndex = filePath.lastIndexOf("/");
        return slashIndex >= 0 ? filePath.substring(slashIndex + 1) : filePath;
    }

    public static String addSlashToBasePath(String path) {
        if (path == null) {
            return "";
        }
        return path.endsWith("/") ? path : path + "/";
    }

    public static String getBasePathForModuloOrObject(String tipoObjeto, String objectRefOrId) {
        String ref = hasText(objectRefOrId) ? objectRefOrId.trim() + "/" : "";
        return Year.now() + "/modulos/" + tipoObjeto + "/" + ref;
    }

    public static String getBasePathForProcess(String processTypeKey, String processInstanceID, String taskKey) {
        String processId = hasText(processInstanceID) ? processInstanceID.trim() + "/" : "";
        String task = hasText(taskKey) ? taskKey.trim() + "/" : "";
        return Year.now() + "/processos/" + processTypeKey + "/" + processId + task;
    }

    public byte[] getDocContentByPath(String path) {
        return minioStoreEngine.getFileByPath(path);
    }

    private DocumentMinioDTO toMinioDocument(UploadDTO item) {
        try {
            return new DocumentMinioDTO(
                normalizeText(item.getFullPath()),
                item.getUploadedFile().getBytes(),
                item.getUploadedFile().getSize(),
                item.getUploadedFile().getContentType()
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao tentar ler ficheiro.", ex);
        }
    }

    private void reuseExistingUploadDocument(ZeeTDocRelacaoEntity docRelacao) {
        if (docRelacao == null
            || docRelacao.getId() != null
            || !hasText(docRelacao.getTipoRelacao())
            || docRelacao.getIdRelacao() == null) {
            return;
        }

        List<ZeeTDocRelacaoEntity> activeDocs = docRelacaoRepository.findByTipoRelacaoAndIdRelacaoAndEstadoOrderByDateCreateDescIdDesc(
            docRelacao.getTipoRelacao(),
            docRelacao.getIdRelacao(),
            ESTADO_ATIVO
        );

        if (docRelacao.getIdTpDoc() != null) {
            activeDocs.stream()
                .filter(item -> docRelacao.getIdTpDoc().equals(item.getIdTpDoc()))
                .findFirst()
                .ifPresent(item -> docRelacao.setId(item.getId()));
            return;
        }

        if (hasText(docRelacao.getDescricao())) {
            activeDocs.stream()
                .filter(item -> docRelacao.getDescricao().equals(item.getDescricao()))
                .findFirst()
                .ifPresent(item -> docRelacao.setId(item.getId()));
            return;
        }

        if (activeDocs.size() == 1) {
            docRelacao.setId(activeDocs.get(0).getId());
        }
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
