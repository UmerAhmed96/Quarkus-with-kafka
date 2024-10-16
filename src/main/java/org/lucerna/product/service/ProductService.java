package org.lucerna.product.service;

import org.lucerna.product.dto.ProductDTO;

public interface ProductService {
     ProductDTO saveMessage(ProductDTO productDTO);
     ProductDTO getSavedProductResponse(String correlationId);

}
