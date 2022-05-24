package com.linhx.sso.configs.security;

import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.services.CaptchaService;
import com.linhx.sso.services.UserService;
import com.linhx.sso.services.loginattempt.LoginAttemptService;
import com.linhx.sso.services.token.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final CaptchaService captchaService;
    private final EnvironmentVariable env;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserDetailsService userDetailsService, TokenService tokenService,
                             UserService userService,
                             LoginAttemptService loginAttemptService,
                             CaptchaService captchaService,
                             EnvironmentVariable env,
                             PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
        this.captchaService = captchaService;
        this.env = env;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new Http401UnauthorizedEntryPoint());
        // filter
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(Paths.PUBLIC_PATHS).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager(),
                        this.tokenService,
                        this.userService,
                        this.loginAttemptService,
                        this.captchaService,
                        this.env))
                .addFilter(new AuthorizationFilter(authenticationManager(),
                        this.tokenService
                ))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout(logout -> logout.logoutUrl(Paths.LOGOUT)
                        .logoutSuccessUrl(Paths.LOGIN)
                        .addLogoutHandler(new LogoutHandler(this.tokenService, this.env)));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(this.env.getCorsOrigins()));
        configuration.setAllowedMethods(Arrays.asList(this.env.getCorsMethods()));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
