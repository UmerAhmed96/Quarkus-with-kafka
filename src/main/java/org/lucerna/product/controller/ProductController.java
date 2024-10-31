package org.lucerna.product.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.lucerna.product.dto.ProductDTO;
import org.lucerna.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for handling Product-related API requests.
 * This controller provides an endpoint to save a product and handle
 * the associated messaging through Kafka.
 */
@Path("/api/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Inject
    ProductService productService;

    /**
     * POST endpoint to save a product.
     * This method accepts a ProductDTO object, saves it by sending a message through Kafka,
     * and waits to retrieve a response. If the operation is long-running, it should ideally be
     * handled asynchronously, with responses provided via webhooks.
     *
     * @param request ProductDTO containing product information to be saved
     * @return Response containing the saved ProductDTO
     */
    @POST
    public Response saveProduct( @Valid ProductDTO request) {
        LOGGER.debug("Saving Product {} ", request);

        ProductDTO sentMessage = productService.saveMessage(request);

        // Note: This is a blocking call to retrieve the response for the saved product.
        // For long-running processes, consider making this method asynchronous and using webhooks for the final response.
        ProductDTO responseMessage = productService.getSavedProductResponse(sentMessage.getCorrelationId());

        LOGGER.debug("Product Saved {}", request);

        return Response.ok(responseMessage).build();
    }
}
