package es.vargontoc.notifications.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import es.vargontoc.notifications.obs.CorrelationIdFilter;
import es.vargontoc.notifications.security.ApiKeyAuthFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${security.api.key}") String apiKey)
                        throws Exception {
                http
                                .csrf(c -> c.disable())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(eh -> eh.authenticationEntryPoint(
                                                (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                                                "Unauthorized")))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/health", "/v3/api-docs/**",
                                                                "/swagger-ui.html", "/swagger-ui/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic(hb -> hb.disable())
                                .formLogin(fl -> fl.disable())
                                .logout(l -> l.disable());

                http.addFilterAfter(new ApiKeyAuthFilter(apiKey), BasicAuthenticationFilter.class);
                http.addFilterBefore(new CorrelationIdFilter(), CorrelationIdFilter.class);
                return http.build();
        }
}
