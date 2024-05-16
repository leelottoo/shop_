package com.keduit.shop.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.nio.file.Path;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        System.out.println("--------------------시큐리티필터체인--------------");

        http.formLogin()
                .loginPage("/members/login")  //로그인 처리 화면
                .defaultSuccessUrl("/") //성공 했을 때(루트)
                .usernameParameter("email")  //중요 loadUserByUsername(String email)로 실행
                .failureUrl("/members/login/error") // 실패 했을 때
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))  //로그아웃 처리 url
                .logoutSuccessUrl("/");

//      permitAll(): 모든 사용자가 인증 없이(로그인 없이) 해당 경로의 접근가능
//      anyRequest().authenticated() : 위의 경우 이외의 페이지는 인증 절차 필요
//      hasRole("ADMIN") : 관리자인 경우 /admin/(하위)의 모든 페이지 접근가능
        http.authorizeRequests()
                .mvcMatchers("/","/members/**",
                                "/item/**","/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated(); //위에 설정한 페이지에 인증되지 않은 사용자 접근 금지

//      인증되지 않은 사용자가 리소스 접근하여 실패했을 때 처리하는 핸들러 등록
        http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }


    @Bean  //  resources/setatic의 부분을 쓸 수 있게 해줌 (css 나 js 같은 부분) 권한에서 제외
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
