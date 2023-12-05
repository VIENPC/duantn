package com.poly.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.poly.entities.Authority;
import com.poly.entities.GioHang;
import com.poly.dao.AuthorityDAO;
import com.poly.dao.GioHangDAO;
import com.poly.dao.KhachHangDAO;
import com.poly.entities.KhachHang;

import net.bytebuddy.utility.RandomString;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomerService implements UserDetailsService {
	@Autowired
	BCryptPasswordEncoder pe;
	@Autowired
	CustomerService customerServive;
	@Autowired
	KhachHangDAO khdao;

	@Autowired
	GioHangDAO ghdao;

	@Autowired
	AuthorityDAO authorityDAO;

	public Optional<KhachHang> getAccount(String username) {
		return khdao.findById(username);
	}

	public List<String> getRolesByUsername(String username) {

		List<String> roleNames = new ArrayList<>();

		List<Authority> authorities = authorityDAO.findAll();

		for (Authority authority : authorities) {
			if (authority.getKhachhang().getUsernamekh().equals(username)) {
				roleNames.add(authority.getRole().getId());
			}
		}
		return roleNames;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			KhachHang account = khdao.findById(username)
					.orElseThrow(() -> new UsernameNotFoundException(username + " not found!"));

			// Tạo UserDetails từ Account
			String password = account.getPasswordkh();
			String[] roles = account.getAuthorities().stream()
					.map(au -> au.getRole().getId())
					.collect(Collectors.toList()).toArray(new String[0]);

			return User.withUsername(username)
					.password(password) // No need to encode the password again
					.roles(roles)
					.build();
		} catch (Exception e) {
			throw new UsernameNotFoundException(username + " not found!", e);
		}
	}

	public void loginFromOAuth2(OAuth2AuthenticationToken oauth2) {
		// String fullname = oauth2.getPrincipal().getAttribute("name");
		String email = oauth2.getPrincipal().getAttribute("email");
		String password = Long.toHexString(System.currentTimeMillis());
		String firstName = oauth2.getPrincipal().getAttribute("given_name");
		String lastName = oauth2.getPrincipal().getAttribute("family_name");
		String fullname = firstName + " " + lastName;
		String phoneNumber = oauth2.getPrincipal().getAttribute("phone_number");
		String gender = oauth2.getPrincipal().getAttribute("gender");
		Boolean genderValue = Boolean.parseBoolean(gender);
		Date now = new Date();
		// Lấy ảnh đại diện
		String imageUrl = oauth2.getPrincipal().getAttribute("picture");
		System.out.println("Ảnh đại diện: " + imageUrl);

		UserDetails user = User.withUsername(email).password(pe.encode(password)).roles("USER").build();
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		this.setToken(email, password);
		session.setAttribute("username", email);

		System.out.println("Email khách hàng" + email);

		KhachHang acc1 = new KhachHang(email, fullname, null, null, email, phoneNumber, now, genderValue, "Token", 1,
				null, null, null, null, null, null);
		khdao.save(acc1);
		GioHang gh = null;

		KhachHang kh = khdao.findById(oauth2.getPrincipal().getAttribute("email")).get();
		gh = ghdao.findByMaKH(kh.getUsernamekh());
		if (gh == null) {
			// Giỏ hàng không tồn tại, tạo giỏ hàng mới
			gh = new GioHang();
			gh.setKhachhang(kh);
			ghdao.save(gh);
		}

	}

	@Autowired
	HttpSession session;

	public void setToken(String username, String password) {
		byte[] auth = (username + ":" + password).getBytes();
		String token = "Basic " + Base64.getEncoder().encodeToString(auth);
		session.setAttribute("token", token);
	}

	public String getToken() {
		return (String) session.getAttribute("token");
	}

	public void updateToken(String token, String email) throws Exception {
		KhachHang entity = khdao.findByEmail(email);
		if (entity != null) {
			entity.setToken(token);
			khdao.save(entity);
		} else {
			throw new Exception("Không tìm thấy bất kì tài khoản nào với Email này: " + email);

		}

	}

	public KhachHang findById(String username) {
		return khdao.findById(username).get();
	}

	public KhachHang getByToken(String token) {
		return khdao.findByToken(token);
	}

	public void updatePassword(KhachHang entity, String newPassword) {
		entity.setPasswordkh(pe.encode(newPassword));
		entity.setToken("token");
		khdao.save(entity);
	}

	public void changePassword(KhachHang entity, String newPassword) {
		entity.setPasswordkh(pe.encode(newPassword));
		khdao.save(entity);
	}

	public List<KhachHang> findAll() {
		return khdao.findAll();
	}

	public KhachHang findByUsername(String username) {

		return khdao.findById(username).get();
	}

	public KhachHang update(KhachHang account) {
		return khdao.save(account);
	}

}
