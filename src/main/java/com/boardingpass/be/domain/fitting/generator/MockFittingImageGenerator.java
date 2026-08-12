package com.boardingpass.be.domain.fitting.generator;

import org.springframework.stereotype.Service;

@Service
public class MockFittingImageGenerator implements FittingImageGenerator {

  @Override
  public FittingGenerationResult generate(FittingGenerationCommand command) {
    return FittingGenerationResult.success(command.sourceImageUrl());
  }
}
