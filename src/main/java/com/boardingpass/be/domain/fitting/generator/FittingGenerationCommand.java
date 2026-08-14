package com.boardingpass.be.domain.fitting.generator;

import com.boardingpass.be.domain.product.ProductColor;

public record FittingGenerationCommand(
    String sourceImageUrl,
    ProductColor productColor
) {
}
