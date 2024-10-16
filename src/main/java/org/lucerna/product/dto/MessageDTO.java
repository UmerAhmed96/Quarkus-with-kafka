package org.lucerna.product.dto;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class MessageDTO implements Serializable {
    private String correlationId;
}
