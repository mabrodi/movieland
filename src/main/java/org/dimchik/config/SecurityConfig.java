package org.dimchik.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.security.AuthFilter;
import org.dimchik.security.PublicEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Slf4j
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthFilter authFilter;
    private final RequestMappingHandlerMapping handlerMapping;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<RequestMatcher> publicMatchers = collectPublicMatchers();

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> {
                    if (!publicMatchers.isEmpty()) {
                        for (RequestMatcher requestMatcher : publicMatchers) {
                            auth.requestMatchers(requestMatcher).permitAll();
                        }
                    }

                    auth.requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html"
                            ).permitAll()
                            .anyRequest().authenticated();
                })

                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private List<RequestMatcher> collectPublicMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();

        handlerMapping.getHandlerMethods().forEach((info, handlerMethod) -> {
            if (!handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
                return;
            }
            Set<String> patterns = info.getPatternValues();
            Set<RequestMethod> httpMethods = info.getMethodsCondition().getMethods();

            for (String pattern : patterns) {
                if (httpMethods.isEmpty()) {
                    matchers.add(PathPatternRequestMatcher.withDefaults().matcher(pattern));

                } else {
                    for (RequestMethod method : httpMethods) {
                        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(method.asHttpMethod(), pattern));
                    }
                }
            }
        });

        return matchers;
    }

}
