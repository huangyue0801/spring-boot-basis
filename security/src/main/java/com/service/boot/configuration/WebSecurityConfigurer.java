package com.service.boot.configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.service.boot.json.JSON;
import com.service.boot.security.AuthUserDetailsProvider;
import com.service.boot.security.UserLoginHandler;
import com.service.boot.security.UserLogoutHandler;
import com.service.boot.security.UsernamePasswordVerificationAuthenticationFilter;
import com.service.boot.utils.io.IOClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfigurer.class);

    @Resource
    private AuthUserDetailsProvider provider;

    @Resource
    private UserLoginHandler userLoginHandler;

    @Resource
    private UserLogoutHandler logoutSuccessHandler;

    @Resource
    private AuthenticationManager authenticationManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //不需要登录验证的
//                .antMatchers(getAntMatchers())
//                .permitAll()
                //需要登陆验证
                .mvcMatchers(getMvcMatchers())
//                .anyRequest()//所有的请求都需要登录
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/loginPage")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .logoutSuccessHandler(logoutSuccessHandler).deleteCookies("JSESSIONID").clearAuthentication(true)
                .permitAll()
                .invalidateHttpSession(true)
                .and();

        UsernamePasswordVerificationAuthenticationFilter filter = new UsernamePasswordVerificationAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(userLoginHandler);
        filter.setAuthenticationFailureHandler(userLoginHandler);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true).expiredUrl("/loginPage");
        http.headers().frameOptions().sameOrigin().disable();//支持iframe
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha dk = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "80");
        properties.setProperty("kaptcha.image.height", "30");
        properties.setProperty("kaptcha.textproducer.font.size", "26");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        properties.setProperty("kaptcha.producer.impl", "com.google.code.kaptcha.impl.NoNoise");
        dk.setConfig(new com.google.code.kaptcha.util.Config(properties));
        return dk;
    }

    private String[] getMvcMatchers() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(WebSecurityConfigurer.class.getResourceAsStream("/security.json")));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim().replace(" ", ""));
            }
            Security security = JSON.parseObject(sb.toString(), Security.class);
            if (security != null && security.mvc_matcher != null) {
                return security.mvc_matcher;
            }
        } catch (Exception e) {
            LOGGER.error("读取security.json配置文件失败", e);
        } finally {
            IOClose.close(reader);
        }
        return new String[]{};
    }

    private static final class Security {

        public String[] mvc_matcher;
    }
}
