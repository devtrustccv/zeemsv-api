package cv.zeemsv.api.infrastructure.minio;

public class DocumentMinioDTO {
    private final String name;
    private final byte[] byteContent;
    private final long fileSize;
    private final String contentType;

    public DocumentMinioDTO(String name, byte[] byteContent, long fileSize, String contentType) {
        this.name = name;
        this.byteContent = byteContent;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }
}
