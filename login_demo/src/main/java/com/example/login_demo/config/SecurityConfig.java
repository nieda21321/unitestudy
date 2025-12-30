package com.example.login_demo.config;

import com.example.login_demo.jwt.JwtAuthenticationFilter;
import com.example.login_demo.jwt.JwtTokenProvider;
import com.example.login_demo.security.CustomUserDetailsService;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


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

    // 성공핸들러
    // private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    // 실패핸들러
    // private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final JwtTokenProvider jwtTokenProvider; // 주입 추가
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 주입 추가

    //  filterChain == 스프링 시큐리티의 핵심 설정 메서드로, HTTP 요청에 대한 보안 정책을 정한다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 공격 방어 기능을 비활성화한다. 학습용이나 API 용도일 때 자주 끈다.
                // 1. CSRF 비활성화 (JWT는 세션을 사용하지 않으므로 CSRF 공격에 비교적 안전함)
                .csrf(csrf -> csrf.disable())

                // 2. [중요] 세션 관리 정책을 무상태(Stateless)로 설정
                // 서버에서 세션을 생성하거나 유지하지 않도록 합니다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 폼 로그인 및 HTTP Basic 인증 비활성화
                // 우리는 이제 JWT 토큰을 직접 주고받을 것이므로 기본 UI 로그인은 끕니다.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                /*
                    폼 로그인 설정
                    로그인 화면은 /login으로 지정
                    로그인 처리 URL은 /loginProc
                    로그인 성공 후 successHandler
                    로그인 실패 시에는 failureHandler
                    로그인 페이지 및 관련 URL은 누구나 접근 가능
                 */
                /*
                JWT 토큰화 사용시에는 비활성화 처리함
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .usernameParameter("userId")
                        .passwordParameter("userPw")
                        //.defaultSuccessUrl("/home", false)
                        .successHandler(customAuthenticationSuccessHandler)
                        //.failureUrl("/login?error")
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                */

                // URL별 접근 권한 규칙을 정의
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/signupProc", "/css/**", "/js/**", "/error").permitAll()
                        .requestMatchers("/WEB-INF/views/**").permitAll()  // forward 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                // 사용자 인증 설정
                // .userDetailsService(customUserDetailsService)

                // 5. [중요] JWT 인증 필터 배치
                // UsernamePasswordAuthenticationFilter(기존 폼 로그인 필터)가 작동하기 전에
                // 우리 가 만든 JWT 필터가 먼저 토큰을 검사하도록 설정합니다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                /*
                    로그아웃 설정
                    로그아웃 URL은 /logout
                    로그아웃 성공 후 이동할 페이지는 /login
                    세션을 무효화해서 로그아웃 처리
                 */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        // JWT로 더이상 세션 미사용으로 주석처리
                        //.invalidateHttpSession(true)
                        //.deleteCookies("JSESSIONID")
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

    // [보안 강화] 비밀키(SecretKey)를 빈으로 등록하여 Provider에서 주입받아 사용
    @Bean
    public SecretKey secretKey(JwtProperties jwtProperties) {
        
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
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
