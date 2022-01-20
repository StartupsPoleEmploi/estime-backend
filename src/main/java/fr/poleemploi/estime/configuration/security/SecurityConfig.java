package fr.poleemploi.estime.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@Profile({ "localhost", "recette", "production" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerLocation;

    @Value("${spring.security.oauth2.client.registration.estime.client-id}")
    private String clientId;

    @Override
    public void configure(WebSecurity web) throws Exception {
	web.ignoring().antMatchers("/individus/authentifier");
	web.ignoring().antMatchers("/actuator/info");
	web.ignoring().antMatchers("/actuator/health");
	web.ignoring().antMatchers("/aides/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	// method will add the Spring-provided CorsFilter to the application context which in turn bypasses the authorization checks for OPTIONS requests.
	http.cors();

	http.authorizeRequests().anyRequest().authenticated().and().oauth2ResourceServer().jwt().decoder(jwtTokenDecoder());
    }

    private JwtDecoder jwtTokenDecoder() {
	NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuerLocation);
	OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(issuerLocation);
	OAuth2TokenValidator<Jwt> delegatingValidator = new DelegatingOAuth2TokenValidator<>(defaultValidators, new JwtTokenValidator(clientId));
	decoder.setJwtValidator(delegatingValidator);
	return decoder;
    }
}
