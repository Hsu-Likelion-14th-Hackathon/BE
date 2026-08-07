package com.boardingpass.be.domain.user;

import com.boardingpass.be.domain.storage.AzureBlobStorageService;
import com.boardingpass.be.domain.user.dto.BodyImageDeleteResponse;
import com.boardingpass.be.domain.user.dto.BodyImageResponse;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBodyImageService {

  private final UserRepository userRepository;
  private final AzureBlobStorageService azureBlobStorageService;

  @Transactional
  public BodyImageResponse updateBodyImage(String fileKey) {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED));

    azureBlobStorageService.validateFileKey(fileKey);
    String imageUrl = azureBlobStorageService.createReadUrl(fileKey);
    user.updateDefaultBodyImage(imageUrl);

    return new BodyImageResponse(imageUrl);
  }

  @Transactional
  public BodyImageDeleteResponse deleteBodyImage() {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED));

    if (!user.hasDefaultBodyImage()) {
      throw new GeneralException(ErrorStatus.BODY_IMAGE_NOT_FOUND);
    }

    user.clearDefaultBodyImage();
    return new BodyImageDeleteResponse(true);
  }
}