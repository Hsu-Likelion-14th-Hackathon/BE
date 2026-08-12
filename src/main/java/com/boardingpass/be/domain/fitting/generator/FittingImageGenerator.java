package com.boardingpass.be.domain.fitting.generator;

public interface FittingImageGenerator {

  FittingGenerationResult generate(FittingGenerationCommand command);
}
