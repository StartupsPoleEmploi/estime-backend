package fr.poleemploi.estime.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

@Configuration
public class MetricsConfig {


    @Value("${spring.application.name}")
    private String nameApplication;

    @Value("${spring.profiles.active}")
    private String environment;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
	return registry -> registry.config()
		.commonTags("application", nameApplication,
			"environment", environment)
		.meterFilter(MeterFilter.deny(id -> {
		    String uri = id.getTag("uri");
		    return uri != null && uri.startsWith("/actuator");
		}))
		.meterFilter(MeterFilter.deny(id -> {
		    String uri = id.getTag("uri");
		    return uri != null && uri.contains("favicon");
		}));
    }
}