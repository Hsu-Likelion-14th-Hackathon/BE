package com.boardingpass.be.domain.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AzureBlobStorageService {

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
  private static final int UPLOAD_URL_EXPIRES_IN = 300;

  private final BlobContainerClient blobContainerClient;

  public UploadUrlResult createUploadUrl(String fileName, String contentType) {
    if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new GeneralException(ErrorStatus.UNSUPPORTED_FILE_TYPE);
    }

    String safeName = StringUtils.cleanPath(fileName == null ? "image.jpg" : fileName);
    String fileKey = "fitting/tmp/" + UUID.randomUUID() + "_" + safeName;

    try {
      BlobClient blobClient = container().getBlobClient(fileKey);

      BlobSasPermission permission = new BlobSasPermission()
          .setCreatePermission(true)
          .setWritePermission(true);

      OffsetDateTime expiry = OffsetDateTime.now().plusSeconds(UPLOAD_URL_EXPIRES_IN);
      BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiry, permission);

      String sas = blobClient.generateSas(values);
      String uploadUrl = blobClient.getBlobUrl() + "?" + sas;

      return new UploadUrlResult(uploadUrl, fileKey, UPLOAD_URL_EXPIRES_IN);
    } catch (Exception e) {
      throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
    }
  }

  public String createReadUrl(String fileKey) {
    validateFileKey(fileKey);
    BlobClient blobClient = container().getBlobClient(fileKey);

    BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
    OffsetDateTime expiry = OffsetDateTime.now().plusDays(30);
    String sas = blobClient.generateSas(new BlobServiceSasSignatureValues(expiry, permission));
    return blobClient.getBlobUrl() + "?" + sas;
  }

  private BlobContainerClient container() {
    blobContainerClient.createIfNotExists();
    return blobContainerClient;
  }

  public void validateFileKey(String fileKey) {
    if (fileKey == null || fileKey.isBlank() || fileKey.contains("..")
        || !fileKey.startsWith("fitting/")) {
      throw new GeneralException(ErrorStatus.INVALID_FILE_KEY);
    }
  }

  public record UploadUrlResult(String uploadUrl, String fileKey, int expiresIn) {}
}