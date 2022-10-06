package com.shopServices.product;

import com.shopServices.product.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductServiceAppTests {

    @Container /* It starts a docker container to host a replicate of the mysql database! */
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8");


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    /* creates connection with the docker container using the db-url from application properties*/
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl );
    }

    @Test
    void createProductTest() throws Exception {

        /* Create a product Request */
        ProductRequest generatedProductRequest = generateProductRequest();

        /* Map the above request to Json request using ObjectMapper */
        String productRequestAsJsonString = objectMapper.writeValueAsString(generatedProductRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestAsJsonString))
                .andExpect(status().isCreated());
    }

    private ProductRequest generateProductRequest() {
        return ProductRequest.builder()
                .name("Iphone 14")
                .description("Iphone 14, just released")
                .price(1500.00)
                .build();
    }
}
