package com.example.taskmanager.user.infrastructure.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@AllArgsConstructor
class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final RestAuthenticationSuccessHandler authenticationSuccessHandler;
  private final RestAuthenticationFailureHandler authenticationFailureHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .mvcMatchers("/registration").permitAll()
            .mvcMatchers("/task-manager-openapi/**").permitAll()
            .mvcMatchers("/swagger-ui/**").permitAll()
            .mvcMatchers(HttpMethod.GET, "/users").hasAnyAuthority("ADMIN")
            .mvcMatchers(HttpMethod.PATCH, "/users/{username}/status").hasAnyAuthority("ADMIN")
            .antMatchers("/h2/console/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(jsonObjectAuthenticationFilter())
            .exceptionHandling()
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .logout().permitAll()
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            .and()
            .csrf().disable()
            .headers().frameOptions().disable();
  }

  private JsonObjectAuthenticationFilter jsonObjectAuthenticationFilter() throws Exception {
    var filter = new JsonObjectAuthenticationFilter(new ObjectMapper());
    filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
    filter.setAuthenticationFailureHandler(authenticationFailureHandler);
    filter.setAuthenticationManager(super.authenticationManager());
    return filter;
  }
}
