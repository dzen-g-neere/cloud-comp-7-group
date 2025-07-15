package com.motivation.ietec_cdc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
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
                .title("Security service")
                .description("<b>This is the API documentation for the Security service.</b>" +
                        "\n\nEvery request must be done via port 8081!" +
                        "\n\n<b>If you have questions, please write to me on Slack/WhatsApp - Egor</b>")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Egor Busuioc")
                );
    }
}
