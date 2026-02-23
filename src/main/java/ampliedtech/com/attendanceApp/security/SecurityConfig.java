package ampliedtech.com.attendanceApp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ampliedtech.com.attendanceApp.config.FaviconFilter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, FaviconFilter faviconFilter) throws Exception {
        http
            .addFilterBefore(faviconFilter, SecurityContextHolderFilter.class)
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/health", "/actuator/health", "/actuator/info").permitAll()

                // secured endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/**").hasRole("USER")
                .requestMatchers("/api/**").authenticated()

                .anyRequest().permitAll()
            )

            // 🔑 Keycloak / JWT with custom role mapping
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // scopes → SCOPE_profile
            String scope = jwt.getClaimAsString("scope");
            if (scope != null) {
                for (String s : scope.split(" ")) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
                }
            }

            // realm roles → ROLE_USER, ROLE_ADMIN (Keycloak often uses "user"/"admin" lowercase)
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                Object roles = realmAccess.get("roles");
                if (roles instanceof Collection<?> roleList) {
                    for (Object role : roleList) {
                        String r = role == null ? "" : String.valueOf(role).toUpperCase();
                        if (!r.isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                        }
                    }
                }
            }

            return authorities;
        });

        return converter;
    }
}
