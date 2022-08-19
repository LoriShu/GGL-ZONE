package com.ggl.cloud.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ggl.cloud.security.TokenLogoutHandler;
import com.ggl.cloud.security.UnauthorizedHandler;
import com.ggl.cloud.security.filter.TokenAuthFilter;
import com.ggl.cloud.security.filter.TokenLoginFilter;
/**
 * 
 * description
 *
 * @author Lori
 * createTime 2022年8月19日-下午2:26:10
 *
 */
@Configuration
@EnableWebSecurity
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {
     
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserDetailsService userDetailsService;
    private PasswordEncoder defaultPasswordEncoder=new BCryptPasswordEncoder();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedHandler())
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/**/registry").permitAll()
                .antMatchers(HttpMethod.POST, "/**/getStatistics").permitAll()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/server/logout")
                .addLogoutHandler(new TokenLogoutHandler( redisTemplate)).and()
                .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate))
                .addFilter(new TokenAuthFilter(authenticationManager(), redisTemplate)).httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/music/**");
        web.ignoring().antMatchers("/video/**");
        web.ignoring().antMatchers("/avatar/**");
    }

    @Bean
    public PasswordEncoder defaultPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
