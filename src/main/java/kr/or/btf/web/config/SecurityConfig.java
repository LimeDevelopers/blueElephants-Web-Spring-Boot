package kr.or.btf.web.config;

import kr.or.btf.web.common.handlers.CustomLoginFailureHandler;
import kr.or.btf.web.common.handlers.CustomLoginSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ApplicationContext context;

    @Autowired
    UserDetailsService userDetailsService;

    /**
     * 권한 계층
     *
     * @return
     */
    /*public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_PM > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }*/

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomLoginSuccessHandler("/loginSuccess");
    }

    @Bean
    public AuthenticationFailureHandler failureHandler(String userId, String userPw) {
        return new CustomLoginFailureHandler("/loginFailure", userId, userPw);
    }

    /**
     * resource/static 제외
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.ignoring().mvcMatchers("/favicon.ico");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //web.ignoring().antMatchers( "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CharacterEncodingFilter filter = new CharacterEncodingFilter();

        http.headers().frameOptions().disable();
        http.headers().xssProtection().block(true);
        http.headers().defaultsDisabled().contentTypeOptions();

        http.authorizeRequests()
                .mvcMatchers("/pages/myPage/**").hasAnyRole("NORMAL,STUDENT,PARENT,TEACHER")
                .mvcMatchers("/naver1c813a906449890c14557110ef4af25e.html", "/ClientUI/**","/node_modules/**","/loginFailure","/message","/error"
                        ,"/fragments/**","/popup/**","/","/index","/login","/api/common/download").permitAll()
                .mvcMatchers("/pages/**","/api/member/**","/api/nice/**","/api/openData/**","/upload/**","/api/commonCode/listForUppCdPid").permitAll()
                .mvcMatchers("/soulGod/**").hasAnyRole("MASTER,ADMIN,LECTURER,COUNSELOR")
                //.mvcMatchers("/**").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login").permitAll()
                //.successHandler(successHandler())courseMasterSeq
                .successForwardUrl("/loginSuccess")
                //.failureForwardUrl("/loginFailure")
                .usernameParameter("userId")                  // 폼 데이터중 로그인 아이디로 사용할 이름
                .passwordParameter("userPw")               // 폼 데이터중 로그인 패스워드로 사용할 이름
                .failureHandler(failureHandler("userId", "userPw"))
        ;
        http.rememberMe()
                .userDetailsService(userDetailsService)
                .key("remember-me-key");

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
                /*.and()
                    .apply(new SpringSocialConfigurer())*/
        http.addFilterBefore(filter, CsrfFilter.class)
                .csrf().disable();

        http.sessionManagement()
                .sessionFixation()
                .changeSessionId()
                .invalidSessionUrl("/")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false) //false : 이전세션아웃, true : 이전세션점유
                .expiredUrl("/pages/session/duplication ")
        ;


        //<input type="checkbo" name="remember-me" />Remember Me
        /*http.rememberMe()
                .userDetailsService(chargerService)
                .key("remember-me-sample");*/
        //Async 로 생성된 하위 쓰레드에도 principal 정보가 공유하도록 정의
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

/*

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
*/

}
