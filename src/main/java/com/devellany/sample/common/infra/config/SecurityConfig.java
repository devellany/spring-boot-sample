package com.devellany.sample.common.infra.config;

import com.devellany.sample.common.domain.security.AnonymousAccount;
import com.devellany.sample.common.infra.handler.SignInSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final SignInSuccessHandler signInSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous().principal(AnonymousAccount.of());

        http.authorizeRequests()
                .mvcMatchers(
                        "/",
                        "/account/sign-in",
                        "/account/sign-up",
                        "/account/email/confirm",
                        "/account/email/resend",
                        "/account/email/change"
                ).permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/account/sign-in")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/account/sign-in")
                .defaultSuccessUrl("/", true)
                .successHandler(signInSuccessHandler)
                .permitAll();

        http.logout()
                .logoutUrl("/account/sign-out")
                .logoutSuccessUrl("/");

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
