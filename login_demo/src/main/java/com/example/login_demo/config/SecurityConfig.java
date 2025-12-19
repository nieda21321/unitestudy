package com.example.login_demo.config;

import com.example.login_demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 *  @Configuration
 *
 * 이 클래스가 "설정 정보(환경 설정)"를 담고 있는 설정용 클래스임을 스프링에 알리는 표시이다.
 *
 * @EnableWebSecurity
 *
 * 스프링 시큐리티의 웹 보안 기능을 활성화하는 어노테이션으로, 스프링 시큐리티가 웹 요청에 대해 보안을 적용할 수 있게 만든다.
 *
 * @RequiredArgsConstructor (롬복 기능)
 *
 * final이나 @NonNull이 붙은 멤버변수에 대해 생성자를 자동으로 만들어준다.
 * 여기서는 CustomUserDetailsService와 PasswordEncoder의 생성자를 대신 만들어주는 기능이다.
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    //  filterChain == 스프링 시큐리티의 핵심 설정 메서드로, HTTP 요청에 대한 보안 정책을 정한다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 공격 방어 기능을 비활성화한다. 학습용이나 API 용도일 때 자주 끈다.
                .csrf(csrf -> csrf.disable())

                // 사용자 인증 설정
                .userDetailsService(customUserDetailsService)

                // URL별 접근 권한 규칙을 정의
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/signupProc", "/css/**", "/js/**", "/error").permitAll()
                        .requestMatchers("/WEB-INF/views/**").permitAll()  // forward 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                /*
                    폼 로그인 설정
                    로그인 화면은 /login으로 지정
                    로그인 처리 URL은 /loginProc
                    로그인 성공 후 기본 이동 페이지는 /home
                    로그인 페이지 및 관련 URL은 누구나 접근 가능
                 */
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .usernameParameter("userId")
                        .passwordParameter("userPw")
                        .defaultSuccessUrl("/home", false)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                /*
                    로그아웃 설정
                    로그아웃 URL은 /logout
                    로그아웃 성공 후 이동할 페이지는 /login
                    세션을 무효화해서 로그아웃 처리
                 */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        // 위 설정을 적용한 보안 필터 체인을 스프링에 등록하기 위한 반환
        return http.build();
    }


    // AuthenticationManager 등록 ( 비밀번호 비교에 필수 )
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /* 이건 커스텀할때 사용함
    @Bean
    public AuthenticationProvider authenticationProvider() {

        //  DB 기반 사용자 인증을 처리하는 구현체
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // CustomUserDetailsService를 인증 서비스로 설정
        provider.setUserDetailsService(userDetailsService);
        // 비밀번호 암호화 및 비교할 때 사용할 인코더를 설정
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    */
}
