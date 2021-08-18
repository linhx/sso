package com.linhx.sso.configs.security;

import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.services.token.TokenService;
import com.linhx.sso.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final UserService userService;
    private final EnvironmentVariable env;

    public WebSecurityConfig(UserDetailsService userDetailsService, TokenService tokenService,
                             UserService userService, EnvironmentVariable env) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.userService = userService;
        this.env = env;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .addFilter(new AuthenticationFilter(authenticationManager(), this.tokenService, this.userService, this.env))
                .addFilter(new AuthorizationFilter(authenticationManager(), this.tokenService))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder());
    }
}
