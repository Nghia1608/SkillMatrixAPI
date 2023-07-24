package com.phuquy.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Intern NC.Nghia",
                        email = "ncnghia-batch40@sdc.edu.vn"
                ),
                description = "OpenAPI documentation for Skill Matrix",
                title = "Open API Skill Matrix"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "http://localhost:8080"
                )
        }
)
@Configuration

public class OpenApiConfig {

}
