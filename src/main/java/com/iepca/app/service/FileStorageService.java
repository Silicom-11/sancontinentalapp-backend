package com.iepca.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "pdf", "doc", "docx"
    );

    @Value("${r2.account-id:}")
    private String accountId;

    @Value("${r2.access-key:}")
    private String accessKey;

    @Value("${r2.secret-key:}")
    private String secretKey;

    @Value("${r2.bucket:}")
    private String bucket;

    private S3Client s3Client;
    private boolean configured = false;

    @PostConstruct
    public void init() {
        if (accountId != null && !accountId.isBlank()
                && accessKey != null && !accessKey.isBlank()
                && secretKey != null && !secretKey.isBlank()) {
            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .region(Region.of("auto"))
                    .build();
            this.configured = true;
            logger.info("Cloudflare R2 configurado exitosamente");
        } else {
            logger.warn("Cloudflare R2 no configurado - almacenamiento de archivos deshabilitado");
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    public Map<String, String> uploadFile(byte[] content, String folder, String originalFilename, String contentType) {
        if (!configured) {
            throw new IllegalStateException("Almacenamiento de archivos no configurado");
        }
        validateFile(content, originalFilename);

        String extension = getExtension(originalFilename);
        String safeFolder = sanitizeFolder(folder);
        String safeFilename = FilenameUtils.getName(StringUtils.defaultIfBlank(originalFilename, "archivo"));
        String key = safeFolder + "/" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 8) + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(content));
        logger.info("Archivo subido a R2: {}", key);

        return Map.of(
                "key", key,
                "filename", safeFilename,
                "url", "/api/uploads/r2/" + key,
                "storage", "r2"
        );
    }

    public InputStream getFileStream(String key) {
        if (!configured) {
            throw new IllegalStateException("Almacenamiento de archivos no configurado");
        }

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObject(getRequest);
    }

    public void deleteFile(String key) {
        if (!configured) {
            return;
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        logger.info("Archivo eliminado de R2: {}", key);
    }

    private String getExtension(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension == null || extension.isBlank()) {
            return "";
        }
        return "." + extension.toLowerCase();
    }

    private void validateFile(byte[] content, String originalFilename) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("El archivo esta vacio");
        }
        if (content.length > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("El archivo supera el limite de 10 MB");
        }
        String extension = FilenameUtils.getExtension(StringUtils.defaultString(originalFilename)).toLowerCase();
        if (StringUtils.isBlank(extension) || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + extension);
        }
    }

    private String sanitizeFolder(String folder) {
        String normalizedFolder = StringUtils.defaultIfBlank(folder, "uploads")
                .replace("\\", "/")
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("/{2,}", "/");
        normalizedFolder = StringUtils.stripStart(normalizedFolder, "/");
        normalizedFolder = StringUtils.stripEnd(normalizedFolder, "/");
        return StringUtils.defaultIfBlank(normalizedFolder, "uploads");
    }
}

