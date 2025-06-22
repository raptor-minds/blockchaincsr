package com.blockchain.utility.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blockchain Utility API")
                        .version("1.0")
                        .description("Blockchain Utility Service API Documentation")
                        .contact(new Contact()
                                .name("Blockchain Team")
                                .email("contact@blockchain.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .tags(List.of(
                    new Tag().name("Transaction").description("Transaction management APIs")
                ));
    }
} 