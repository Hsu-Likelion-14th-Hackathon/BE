package com.boardingpass.be.domain.fitting.generator;

public record FittingGenerationResult(
    boolean success,
    String resultImageUrl
) {
  public static FittingGenerationResult success(String resultImageUrl) {
    return new FittingGenerationResult(true, resultImageUrl);
  }

  public static FittingGenerationResult failure() {
    return new FittingGenerationResult(false, null);
  }
}
