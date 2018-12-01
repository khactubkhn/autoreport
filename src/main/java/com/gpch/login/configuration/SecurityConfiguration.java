package com.gpch.login.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.gpch.login.filtes.JwtAuthenticationTokenFilter;
import com.gpch.login.rest.CustomAccessDeniedHandler;
import com.gpch.login.rest.RestAuthenticationEntryPoint;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.
                jdbcAuthentication()
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
      JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
      jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager());
      return jwtAuthenticationTokenFilter;
    }
    @Bean
    public RestAuthenticationEntryPoint restServicesEntryPoint() {
      return new RestAuthenticationEntryPoint();
    }
    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
      return new CustomAccessDeniedHandler();
    }
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
      return super.authenticationManager();
    }
    protected void configure(HttpSecurity http) throws Exception {

      // Disable crsf cho đường dẫn /rest/**
      http.csrf().ignoringAntMatchers("/api/room/**");
      http.authorizeRequests().antMatchers("/api/login").permitAll();
      http.authorizeRequests().antMatchers("/report/**").permitAll();
      
      http.authorizeRequests().antMatchers("/login").permitAll();
      http.antMatcher("/room/**").httpBasic().authenticationEntryPoint(restServicesEntryPoint()).and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
          .antMatchers(HttpMethod.POST, "/api/room/**").access("hasRole('CUSTOMER')")
          .antMatchers(HttpMethod.GET, "/api/room/**").access("hasRole('CUSTOMER')")
          .antMatchers(HttpMethod.GET, "/default").access("hasRole('CUSTOMER')")
          .antMatchers(HttpMethod.GET, "/meeting").access("hasRole('CUSTOMER')")
          .antMatchers(HttpMethod.GET, "/userinformation").access("hasRole('CUSTOMER')")
          .antMatchers(HttpMethod.GET, "/meetingdetail").access("hasRole('CUSTOMER')")
        .antMatchers(HttpMethod.POST, "/rest/**").access("hasRole('ROLE_ADMIN')")
        .antMatchers(HttpMethod.DELETE, "/rest/**").access("hasRole('ROLE_ADMIN')").and()
        .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

}
