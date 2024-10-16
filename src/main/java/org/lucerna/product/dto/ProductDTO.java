package org.lucerna.product.dto;

import lombok.*;

import java.io.Serializable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@ToString
@NoArgsConstructor
public class ProductDTO extends MessageDTO implements Serializable {
    @NotNull(message = "Product name cannot be null")
    @NotEmpty(message = "Product name cannot be empty")
    private String name;
}
