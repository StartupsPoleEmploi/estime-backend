package fr.poleemploi.estime.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
	return GroupedOpenApi.builder().group("springshop-public").pathsToMatch("*").build();
    }
}