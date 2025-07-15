package com.motivation.ietec_cdc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for configuring the Swagger documentation for the API.
 * @author EgorBusuioc
 * 21.05.2025
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("API-Gateway service") // Название API
                .description("<b>This is the API documentation for the API-Gateway service.</b>" +
                        "\n\nIn top right corner you can find navigation between services." +
                        "\n\nEvery request must be done via port 8081!" +
                        "\n\n\n<b>IMPORTANT NOTE:</b> To connect and test <b>Security Service</b> " +
                        "on your computer must be installed MySql with port: 3306, schema name: ietecusers and login, password: root." +
                        "\n\nAll tables will be created automatically, and after creation you can start test your authentication process" +
                        "\n\n<b>If you have questions, please write to me on Slack - Egor</b>")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Egor Busuioc")
                );
    }
}
