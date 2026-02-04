package thuchanh.ngohuuduc.utils;

import thuchanh.ngohuuduc.services.OauthService;
import thuchanh.ngohuuduc.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
        private final OauthService oauthService;
        private final UserService userService;
        private final JwtAuthenticationFilter jwtAuthFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                // Using the constructor matching the user's environment (likely a custom or
                // specific version)
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**")) // Disable CSRF for API endpoints
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/",
                                                                "/oauth/**", "/register", "/error", "/api/auth/**")
                                                .permitAll()
                                                .requestMatchers("/books/edit/**",
                                                                "/books/add", "/books/delete", "/admin/**")
                                                .hasAnyAuthority("ADMIN")
                                                .requestMatchers("/books", "/cart", "/cart/**")
                                                .authenticated()
                                                .requestMatchers("/api/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Use
                                                                                                           // IF_REQUIRED
                                                                                                           // (default)
                                                                                                           // for Web UI
                                                                                                           // support
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT
                                                                                                            // filter
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .permitAll())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/")
                                                .successHandler((request, response, authentication) -> {
                                                        var authorities = authentication.getAuthorities();
                                                        if (authorities.stream().anyMatch(
                                                                        a -> a.getAuthority().equals("ADMIN"))) {
                                                                response.sendRedirect("/admin");
                                                        } else {
                                                                response.sendRedirect("/");
                                                        }
                                                })
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .oauth2Login(
                                                oauth2Login -> oauth2Login
                                                                .loginPage("/login")
                                                                .failureUrl("/login?error")
                                                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                                                .userService(oauthService))
                                                                .successHandler(
                                                                                (request, response,
                                                                                                authentication) -> {
                                                                                        var oidcUser = (DefaultOidcUser) authentication
                                                                                                        .getPrincipal();
                                                                                        userService.saveOauthUser(
                                                                                                        oidcUser.getEmail(),
                                                                                                        oidcUser.getName());
                                                                                        response.sendRedirect("/");
                                                                                })
                                                                .permitAll())
                                .rememberMe(rememberMe -> rememberMe
                                                .key("hutech")
                                                .rememberMeCookieName("hutech")
                                                .tokenValiditySeconds(24 * 60 * 60)
                                                .userDetailsService(userService))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedPage("/403"))
                                .httpBasic(httpBasic -> httpBasic
                                                .realmName("hutech"))
                                .build();
        }
}