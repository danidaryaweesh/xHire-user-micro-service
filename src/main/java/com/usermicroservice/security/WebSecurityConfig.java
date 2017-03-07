package com.usermicroservice.security;

import com.usermicroservice.domain.User;
import com.usermicroservice.security.jwt.JWTAuthenticationFilter;
import com.usermicroservice.security.jwt.JWTLoginFilter;
import com.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import java.util.List;

/**
 * Created by Teddy on 2017-03-06.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable caching
        http.headers().cacheControl();

        http.csrf().disable() // disable csrf for our requests.
                .authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // We filter the api/login requests
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // And filter other requests to check the presence of JWT in header
                .addFilterBefore((Filter) new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    //for testing,
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Create a default account, load all the admins
        List<User> users = userService.findUsersByRoleId(1);
        System.out.println("user = " + users.get(0).getUserRole().toString());

        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("password")
                .roles("ADMIN");
    }
}
