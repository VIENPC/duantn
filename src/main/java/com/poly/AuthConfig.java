package com.poly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.poly.service.CustomerService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	CustomerService customerServive;

	/*--Quản lý người dữ liệu người sử dụng--*/

	/*--Phân quyền sử dụng và hình thức đăng nhập--*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().disable();
		// Phân quyền sử dụng
		http.authorizeRequests()
				.antMatchers("/auth/login").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")

				.antMatchers("/cart", "/checkout", "/account").authenticated()
				.anyRequest().permitAll();

		http.exceptionHandling().accessDeniedPage("/auth/access/denied");
		// Giao diện đăng nhập
		http.formLogin()
				.loginPage("/auth/login/form")
				.loginProcessingUrl("/auth/login")
				.successForwardUrl("/auth/login/success")
				.failureUrl("/auth/login/error?fail=login")
				.usernameParameter("username")
				.passwordParameter("password");

		// http.rememberMe().key("aB3cD7eFgH2iJ9kL1mN4oP8qR5tU0vX6wZ").tokenValiditySeconds(1
		// * 24 * 60 * 60);
		// Cung cấp UserDetailsService của bạn

		http.logout().logoutUrl("/auth/logoff") // [/logout]
				.logoutSuccessUrl("/auth/logoff/success");
		http.oauth2Login()
				.loginPage("/auth/login/form")
				.defaultSuccessUrl("/oauth2/login/success", true)
				.failureUrl("/auth/login/error")
				.authorizationEndpoint()
				.baseUri("/oauth2/authorization");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customerServive).passwordEncoder(getPasswordEncoder());
	}

	/*--Mã hóa mật khẩu--*/
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	// @Autowired
	// BCryptPasswordEncoder pe;

	/*--Cho phép request đến REST API từ browser--*/
	@Override
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

}
